package com.gradigi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.OTP;
import com.gradigi.model.Presentation;
import com.gradigi.model.Team;
import com.gradigi.model.User;
import com.gradigi.repository.OTPRepo;
import com.gradigi.repository.TeamDao;
import com.gradigi.repository.UserRepository;
import com.gradigi.response.AuthenticationRequestPassChange;
import com.gradigi.response.AuthenticationRequestPassChangeByOTP;
import com.gradigi.response.EmailBox;
import com.gradigi.response.ProfileResponse;
import com.gradigi.response.UserResponse;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private OTPRepo orepo;
	

	@Autowired
	private TeamDao teamDao;
	
	@Autowired
	private EmailService em;

	@Override
	public String getOTP(OTP ot) throws UserException, AddressException, MessagingException{
		User u=userRepository.findByEmail(ot.getEmail());
		if(u==null) {
			throw new UserException("Email address is invalid");
		}
		Random rand = new Random();
		int minRange = 100000, maxRange = 999999;
		int otp = rand.nextInt(maxRange - minRange) + minRange;
		ot.setOtp(otp);
		
		EmailBox eb=new EmailBox(ot.getEmail(), "Hello "+u.getName()+"\n"
				+ "Here is your OTP to reset your password for your Gradigi/n"+"OTP:-"+otp+"/n", "OTP for Forget password from Gradigi");
		
		orepo.save(ot);
		em.sendEmail(eb);
		
		
		
		return "Email Sent";
	}
	@Override
	public String verifyOtpAndUpdatePassword(AuthenticationRequestPassChangeByOTP aotp) throws AddressException, UserException {
		
		OTP ot=orepo.findByEmailOrderByIdDesc(aotp.getEmail()).get(0);
		System.out.println(ot.getOtp());
		System.out.println(aotp.getOtp());
		System.out.println(ot.getOtp()==aotp.getOtp());
		if(ot==null) {
			throw new AddressException("Email not found");
		}
		if(aotp.getOtp()!=ot.getOtp()) {
			throw new UserException("OTP verification failed please try again later");
		}
		User isUserPresent = userRepository.findByEmail(aotp.getEmail());
		if(isUserPresent==null) {
			throw new UserException("No User Found for this Email Address");
		}
		isUserPresent.setPassword(passwordEncoder.encode(aotp.getNewPassword()));
		
		userRepository.save(isUserPresent);

		return "Password Change Successfully ";
	}
	@Override
	public boolean registerUser(User user) throws UserException {
		User isUserPresent = userRepository.findByEmail(user.getEmail());

		if (isUserPresent == null) {

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setRole("ROLE_USER");
			userRepository.save(user);

			return true;

		} 
		return false;
//			else
//			throw new UserException("user already exists please login");
	}
	@Override
	public String changeUserPassword(AuthenticationRequestPassChange auth) throws UserException {
		User isUserPresent = userRepository.findByEmail(auth.getUserName());

		isUserPresent.setPassword(passwordEncoder.encode(auth.getNewPassword()));
		
		userRepository.save(isUserPresent);

		return "Password Change Successfully ";

			

		
	}
	
	

	@Override
	public List<UserResponse> admins() {

		List<UserResponse> admins = new ArrayList<>();

		List<User> users = userRepository.findAll();

		for (User user : users) {
			if (user.getRole().equals("ROLE_ADMIN")) {

				UserResponse userResponse = new UserResponse();
				userResponse.setId(user.getId());
				userResponse.setName(user.getName());
				userResponse.setUsername(user.getName());
				
				admins.add(userResponse);
			}

		}
		return admins;
	}

	/**
	 * Finds all presentations associated with a given user by their user name.
	 * 
	 * @param userName the user name of the user whose presentations to retrieve
	 * @return a List of Presentation objects associated with the specified user
	 * @throws TeamException if the specified user is not involved in any team
	 * @throws UserException if the specified user name is invalid or does not exist, or if the user is not involved in any presentation
	 */

	@Override
	public List<Presentation> findPresentationsbyUserName(Integer userId) throws TeamException, UserException {

		Optional<User> exitedUser = userRepository.findById(userId);
		if (exitedUser.isEmpty()) {
			throw new UserException("Invalid user id " + exitedUser.get().getId() + " or doesn't exist");
		}
		
		List<Team> teams = teamDao.findAll();

		if (teams.isEmpty()) {
			throw new TeamException("User is not involve in any team");
		}

		List<Presentation> presentations = new ArrayList<>();

		for (Team team : teams) {

			for (User user : team.getUsers()) {

				if ( exitedUser.get().getId()==user.getId()) {
					presentations.add(team.getPresentation());
					break;
				}
			}

		}
		if (presentations.isEmpty()) {
			throw new UserException("User is not involve in any presentation");
		}

		return presentations;
	}

	@Override
	public ProfileResponse profile(Integer userId) throws UserException {
		
		Optional<User> existedUser = userRepository.findById(userId);
		
		if(existedUser.isEmpty()) {
			throw new UserException("Invalid token");
		}
		
		ProfileResponse profileResponse = new ProfileResponse();
		
		profileResponse.setName(existedUser.get().getName());
		profileResponse.setEmail(existedUser.get().getEmail());
		profileResponse.setUsername(existedUser.get().getUserName());
		
		return profileResponse;
	}
	
}






































































































































































































