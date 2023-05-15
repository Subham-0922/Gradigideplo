package com.gradigi.LoginAndSecurity.SecurityConfiguratons;

import javax.servlet.http.HttpServletRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gradigi.exceptions.UserException;
import com.gradigi.response.UserResponse;

public class TokenExtractor {

	public static int extractToken(HttpServletRequest request) throws UserException {

		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {

			UserResponse userResponse = new UserResponse();
			DecodedJWT jwt = JWT.decode(header.substring(7));

			return jwt.getClaim("userId").asInt();
		} else
			throw new UserException("Invalid token");
	}
}
