package com.gradigi.services;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.gradigi.response.EmailBox;

@Service
public class EmailService {

	@Autowired 
	private JavaMailSender javaMailSender;
	 
    
	
	public Boolean sendEmail(EmailBox em) throws AddressException, MessagingException {
		// TODO Auto-generated method stub
		try {
			SimpleMailMessage smm=new SimpleMailMessage();
			smm.setFrom("masaigradigi@gmail.com");
			smm.setTo(em.getRecipient());
			smm.setSubject(em.getSubject());
			smm.setText(em.getMsgBody());
			
			javaMailSender.send(smm);
		}catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	
	public Boolean sendEmail(String messege, String sub, String to) throws AddressException, MessagingException {

		String from = "royanimesh04@gmail.com";
		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// step 1: to get session object...

		Session session = Session.getInstance(properties, new Authenticator() {
		
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("royanimesh04@gmail.com", "rtfbndfjgqznjbfi");
			}
		});

		session.setDebug(true);
		MimeMessage mimeMessage = new MimeMessage(session);

		mimeMessage.setFrom(from);
		mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		mimeMessage.setSubject(sub);
		mimeMessage.setText(messege);
		Transport.send(mimeMessage);

		return true;
	}
}
