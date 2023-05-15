package com.gradigi.LoginAndSecurity.JwtConfigurations;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		HashMap<String, String> hashMap = new HashMap<>();

		String message;
		if (authException.getCause() != null) {
			message = authException.getCause().toString() + " " + authException.getMessage();
		} else {
			message = authException.getMessage();
			hashMap.put("status-code", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
			hashMap.put("status", HttpStatus.UNAUTHORIZED.name());
		}

		hashMap.put("message", message);

		byte[] body = new ObjectMapper().writeValueAsBytes(hashMap);
		response.getOutputStream().write(body);

		System.out.println("response - "+ response.getHeaderNames());

	}

}
