package com.gradigi.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.exceptions.FileReadingException;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.Team;
import com.gradigi.model.User;
import com.gradigi.randompassword.RandomPassword;
import com.gradigi.repository.PresentationDao;
import com.gradigi.repository.TeamDao;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

@Service
public class FileUserService {

	@Autowired
	private TeamServiceImpl teamServiceImpl;

	@Autowired
	private CustomSecurity customSecurity;

	@Autowired
	private PresentationDao presentationDao;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private EmailSenderService emailSenderService;

	@Autowired
	private TeamDao teamDao;

	/**
	 * Uploads a CSV file containing team and student data and associates it with
	 * the specified presentation.
	 *
	 * @param file           the CSV file to upload
	 * @param presentationId the ID of the presentation with which to associate the
	 *                       uploaded data
	 * @return a String message indicating that the teams have been successfully
	 *         saved to the specified presentation
	 * @throws FileReadingException  if there is an issue reading the uploaded CSV
	 *                               file
	 * @throws IOException           if there is an issue accessing the uploaded CSV
	 *                               file
	 * @throws UserException         if there are invalid user credentials in the
	 *                               uploaded CSV file
	 * @throws PresentationException if the specified presentation ID does not exist
	 * @throws AccessDeniedException if the user attempting to upload the CSV file
	 *                               does not have permission to do so
	 */

	public String uploadFile(MultipartFile file, Integer presentationId)
			throws FileReadingException, IOException, UserException, PresentationException {

		Optional<Presentation> existedPresentation = presentationDao.findById(presentationId);

		if (existedPresentation.isEmpty()) {
			throw new PresentationException("Presentation is not present to given presentation id");
		}

		customSecurity.validateUser(existedPresentation.get().getUser().getId());

		List<Team> teams = teamDao.findByPresentation(existedPresentation.get());

		if (!teams.isEmpty()) {
			throw new UserException("There is already teams exist");
		}

		InputStream inputStream = file.getInputStream();

		CsvParserSettings setting = new CsvParserSettings();

		setting.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(setting);
		List<Record> parseAllRecords = parser.parseAllRecords(inputStream);

		if (parseAllRecords.isEmpty())
			throw new FileReadingException("file is not readable");

		Map<String, List<String>> map = new HashMap<>();

		parseAllRecords.forEach(record -> {

			String studentName = record.getString("Student_name");
			String studentEmail = record.getString("Student_email");
			String studentId = record.getString("Student_id");
			String teamName = record.getString("Team_name");

			// for generating random 6 digit password
			RandomPassword rp = new RandomPassword();
			String password = rp.getRandomPassword();
			User obj = new User();
			obj.setName(studentName);
			obj.setEmail(studentEmail);
			obj.setUserName(studentId);
			obj.setPassword(password);

			boolean isRegisterdOrNot = false;
			try {
				isRegisterdOrNot = userService.registerUser(obj);
			} catch (UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (isRegisterdOrNot) {

				String emailSubject = "<Important> Gradigi Login Details <Masai>";
				String emailBody = "Dear " + studentName + ",\r\n" + "\r\n"
						+ "Please find your Login Credentials for Gradigi Platform.\r\n" + "\r\n"
						+ "Link : Plateform url\r\n" + "\r\n" + "Username : " + studentEmail + "\r\n" + "\r\n"
						+ "Password : " + password + "\r\n" + "\r\n"
						+ "Make sure you change your password by clicking on forgot password\r\n" + "\r\n"
						+ "Please find the video with step by step process for onboarding yourself to the Product.";

				emailSenderService.sendMail(studentEmail, emailSubject, emailBody);
			}

			if (map.containsKey(teamName)) {

				List<String> list = map.get(record.getString("Team_name"));
				list.add(studentId);
				map.put(teamName, list);

			} else {

				List<String> list = new ArrayList<>();
				list.add(studentId);
				map.put(teamName, list);
			}

		});

		return teamServiceImpl.saveTeam(map, presentationId);
	}
}