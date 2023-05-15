package com.gradigi.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gradigi.LoginAndSecurity.SecurityConfiguratons.TokenExtractor;
import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.User;
import com.gradigi.response.ProfileResponse;
import com.gradigi.response.UserResponse;
import com.gradigi.services.UserServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserServiceImpl userServiceImpl;

//	@PostMapping("/save")
//	public ResponseEntity<String> userRegistration(@Valid @RequestBody User user) throws UserException {
//
//		String registeredUser = userServiceImpl.registerUser(user);
//
//		return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
//	}

	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<UserResponse>> adminHandler() {

		List<UserResponse> admins = userServiceImpl.admins();

		return new ResponseEntity<>(admins, HttpStatus.ACCEPTED);
	}


	@GetMapping("/presentations")

	public ResponseEntity<List<Presentation>> findPresentationsByUserName(HttpServletRequest request
		) throws TeamException, UserException {


		int userId = TokenExtractor.extractToken(request);

		List<Presentation> presentations = userServiceImpl.findPresentationsbyUserName(userId);

		return new ResponseEntity<>(presentations, HttpStatus.OK);
	}

	@GetMapping("/profile")
	public ResponseEntity<ProfileResponse> profileHandler(HttpServletRequest request)
			throws  UserException {

		int userId = TokenExtractor.extractToken(request);

		ProfileResponse profile = userServiceImpl.profile(userId);

		return new ResponseEntity<>(profile, HttpStatus.OK);
	}

}
