package com.smart.service;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	public boolean sendEmail(String subject, String message, String to) {
		boolean f = false; // that means email is not sent
		String from = "email_id";
//		email_id => Email Id which email id want to send mail
		// rest of code
		// variable for gmail
		String host = "smtp.gmail.com";
		// get system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);
		// setting important information to properites object
		// host set
		properties.put("mail.smtp.host", host);

		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
			
		// Step1 : to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("email_id","password");
//				email id => email_id
//				password => email password
			}
		});
		session.setDebug(true);

		// step 2 : compose the message {text, multi media}
		MimeMessage m = new MimeMessage(session);
		try {
			// from email
			m.setFrom(from);
			// adding recepient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// adding subject to message
			m.setSubject(subject);
			// adding text to message
		//	m.setText(message);
			
			m.setContent(message,"text/html");
// step 3 : Send message using transport class
			Transport.send(m);
			System.out.println("Send Success........");
			f = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return f; // if any pb coomes it will return false
	}
}
