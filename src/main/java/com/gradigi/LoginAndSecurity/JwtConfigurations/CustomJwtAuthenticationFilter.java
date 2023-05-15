package com.gradigi.LoginAndSecurity.JwtConfigurations;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
/*whenever any request hit the server first this class will executed
 * if request contains jwt token then  request handle according to configuration
 * otherwise it follow the basic spring security cofiguration */

@Component
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwtToken = extractJwtFromRequest(request);

		if (StringUtils.hasText(jwtToken) && jwtTokenUtil.validateToken(jwtToken)) {

			UserDetails userDetails = new User(jwtTokenUtil.getUsernameFromToken(jwtToken), "",
					jwtTokenUtil.getRolesFromToken(jwtToken));

			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			
			

			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		} else {
			
			System.out.println("Cannot set the Security Context");
		}
		filterChain.doFilter(request, response);
	}

	private String extractJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

}
