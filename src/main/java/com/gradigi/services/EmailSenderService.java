package com.gradigi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

	@Autowired
	private JavaMailSender javaMailSender;

	public void sendMail(String toMail, String subject, String body) {

		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("murlikhaire28@gmail.com");
		message.setTo(toMail);
		message.setText(body);
		message.setSubject(subject);
		
		javaMailSender.send(message);
		
	}

}
