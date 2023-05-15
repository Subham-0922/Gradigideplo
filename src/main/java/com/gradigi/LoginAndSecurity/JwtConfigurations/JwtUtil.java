package com.gradigi.LoginAndSecurity.JwtConfigurations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gradigi.repository.UserRepository;
import com.gradigi.response.AuthenticationResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
/* utility class for generating and validating the jwt token */

/* utility class for generating and validating the jwt token */

@Service
public class JwtUtil {
	@Autowired
	private UserRepository userRepository;

	private String secretKey;
	private int jwtExpirationInMs;

	@Value("${jwt.secret}")
	public void setSecret(String secret) {
		this.secretKey = secret;
	}

	@Value("${jwt.jwtExpirationInMs}")
	public void setJwtExpirationInMs(int jwtExpirationInMs) {
		
		this.jwtExpirationInMs = jwtExpirationInMs;
	}

	public AuthenticationResponse generateToken(UserDetails userdetail) {
		Map<String, Object> claims = new HashMap<>();

		boolean isAdmin = false;
		
		Collection<? extends GrantedAuthority> roles = userdetail.getAuthorities();

		if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			claims.put("isAdmin", true);
			isAdmin = true;
		}
		
		if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
			claims.put("isUser", true);
		}
		var userId = userRepository.findByEmail(userdetail.getUsername());

		claims.put("userId", userId.getId());
		claims.put("name", userId.getName());

		 String token = doGenerateToken(claims, userdetail.getUsername());
		 
		 AuthenticationResponse authenticationResponse = new AuthenticationResponse(token,isAdmin);
		 return authenticationResponse;
	}

	private String doGenerateToken(Map<String, Object> claims, String username) {
		return Jwts.builder().setClaims(claims).setSubject(username)

				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))

				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	public boolean validateToken(String authToken) {
		try {

			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {

			throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
		}
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public List<SimpleGrantedAuthority> getRolesFromToken(String authToken) {
		List<SimpleGrantedAuthority> roles = null;

		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken).getBody();

		Boolean isAdmin = claims.get("isAdmin", Boolean.class);

		Boolean isUser = claims.get("isUser", Boolean.class);
		if (isAdmin != null && isAdmin == true) {
			roles = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		if (isUser != null && isUser == true) {
			roles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
		}
		return roles;
	}
}
