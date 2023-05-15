package com.gradigi.controllers;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gradigi.LoginAndSecurity.JwtConfigurations.JwtUtil;
import com.gradigi.LoginAndSecurity.SecurityConfiguratons.UserDetailServiceImpl;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.OTP;
import com.gradigi.response.AuthenticationRequest;
import com.gradigi.response.AuthenticationRequestPassChange;
import com.gradigi.response.AuthenticationRequestPassChangeByOTP;
import com.gradigi.response.AuthenticationResponse;
import com.gradigi.services.UserService;
import com.gradigi.services.UserServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class AuthenticationController {

	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;
	
	@Autowired
	private UserService userService;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> createAthenticationToken(
			@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(

					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
					

		} catch (DisabledException e) {

			throw new Exception("user_disabled", e);

		} catch (BadCredentialsException e) {

			throw new Exception("Invalid_crediantials", e);

		}

		final UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(authenticationRequest.getUsername());

		final AuthenticationResponse token = jwtUtil.generateToken(userDetails);

		return new ResponseEntity<>(token, HttpStatus.CREATED);
	}
	
	@PostMapping("/authenticate/changePassword")
	public ResponseEntity<String> changeAthenticationCredential(
			@RequestBody AuthenticationRequestPassChange authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(

					authenticationRequest.getUserName(), authenticationRequest.getOldPassword()));
			
			userServiceImpl.changeUserPassword(authenticationRequest);
			System.out.println("success");
					

		} catch (DisabledException e) {

			throw new Exception("user_disabled", e);

		} catch (BadCredentialsException e) {

			throw new Exception("Invalid_crediantials", e);

		}
		

		final UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(authenticationRequest.getUserName());

		final AuthenticationResponse token = jwtUtil.generateToken(userDetails);
		
		return new ResponseEntity<String>("help",HttpStatus.OK);
//		return new ResponseEntity<>(token, HttpStatus.CREATED);
	}
	
	@GetMapping("/authenticate/forgotpassword/{email}")
	public ResponseEntity<String> forgetPassword(@PathVariable String email) throws AddressException, UserException, MessagingException{
		email=email.toLowerCase();
		OTP ot=new OTP();
		ot.setEmail(email);
		userService.getOTP(ot);
		
		return new ResponseEntity<String>("OTP has been send to your email",HttpStatus.CREATED);
	}
	@PatchMapping("/authenticate/verify")
	public ResponseEntity<String> verifyOTP(@RequestBody AuthenticationRequestPassChangeByOTP aotp) throws AddressException, UserException{
		
		userService.verifyOtpAndUpdatePassword(aotp);
		return new ResponseEntity<String>("Password Updated SuccessFully", HttpStatus.ACCEPTED);
	}

	@GetMapping("/get")
	public String getAdmin(HttpServletResponse response) {
		return "Hello admin welcome to the......";

	}
}
