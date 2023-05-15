package com.gradigi.services;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.OTP;
import com.gradigi.model.Presentation;
import com.gradigi.model.User;
import com.gradigi.response.AuthenticationRequestPassChange;
import com.gradigi.response.AuthenticationRequestPassChangeByOTP;
import com.gradigi.response.ProfileResponse;
import com.gradigi.response.UserResponse;

public interface UserService {

	public boolean registerUser(User user) throws UserException;

	public List<UserResponse> admins();

	public List<Presentation> findPresentationsbyUserName(Integer userId) throws TeamException, UserException;

	public ProfileResponse profile(Integer userId) throws UserException;
	
	public String changeUserPassword(AuthenticationRequestPassChange auth) throws UserException;
	public String getOTP(OTP ot) throws UserException, AddressException, MessagingException;
	public String verifyOtpAndUpdatePassword(AuthenticationRequestPassChangeByOTP aotp) throws AddressException, UserException;
	
}