package com.gradigi.LoginAndSecurity.JwtConfigurations;

import java.nio.file.AccessDeniedException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.gradigi.repository.UserRepository;

@Component("customSecurity")
public class CustomSecurity {

	@Autowired
	private UserRepository userRepository;

	public void validateUser(Integer id) throws AccessDeniedException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		Integer user_id = userRepository.findByEmail(userDetails.getUsername()).getId();

		if (!Objects.equals(id, user_id)) {
			
			throw new AccessDeniedException("Access denied");
		}
	}
}