package com.gradigi.controllers;

import java.util.Random;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gradigi.services.EmailService;

@RestController
@RequestMapping("/otp")
@CrossOrigin
public class EmailOtpController {

	@Autowired
	EmailService emailService;

	@PostMapping("/send/{email}")
	public ResponseEntity<String> sendOtp(@Valid @PathVariable String email, HttpSession session)
			throws MessagingException {

		System.out.println("myAttribute - " + session.getAttribute("myAttribute"));
		Random rand = new Random();
		int minRange = 100000, maxRange = 999999;
		int otp = rand.nextInt(maxRange - minRange) + minRange;
		String subject = " OTP FROM TEAM-5 FOR signUp.";
		String messege = "OTP is " + otp + " to signup for Masai. DO NOT share with anyone";
		String to = email;
		String message;
		boolean getresult = emailService.sendEmail(messege, subject, to);
		if (getresult) {
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			message = "Verify_OTP";
		} else {
			session.setAttribute("messege", "Check your email id!!!");
			message = "Check your email id or OTP";
		}
		return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
	}

	@PostMapping("/verify/{otp}")
	public ResponseEntity<String> verifyOtp(@PathVariable("otp") int otp, HttpSession session) {

		int myOtp = (int) session.getAttribute("otp");

		String email = (String) session.getAttribute("email");

		String message;
		if (myOtp == otp) {
			session.removeAttribute("otp");
			message = "otp verified successfully";

		} else {
			message = "otp does not match";
		}

		return new ResponseEntity<String>(message, HttpStatus.ACCEPTED);
	}
}
