package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.Team;
import com.gradigi.model.User;
import com.gradigi.repository.PresentationDao;
import com.gradigi.repository.TeamDao;
import com.gradigi.repository.UserRepository;
import com.gradigi.response.TeamResponse;
import com.gradigi.response.UserResponse;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PresentationDao presentationDao;

	@Autowired
	private CustomSecurity customSecurity;

	/**
	 * Saves a team by creating a new Team object associated with a given
	 * presentation, and adding a list of users to that team.
	 *
	 * @param map            a Map where each key represents a team name and each
	 *                       value is a List of student user names belonging to that
	 *                       team
	 * @param presentationId the ID of the presentation to which the team is
	 *                       enrolling
	 * @return a String message indicating that the team has been successfully
	 *         enrolled in the presentation
	 * @throws UserException         if any of the specified user credentials are
	 *                               invalid or do not exist
	 * @throws PresentationException if the specified presentation ID does not exist
	 */

	@Override
	public String saveTeam(Map<String, List<String>> map, Integer presentationId)
			throws UserException, PresentationException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		for (Map.Entry<String, List<String>> element : map.entrySet()) {

			String teamKey = element.getKey();

			Team team = new Team();

			team.setPresentation(presentation.get());

			team.setName(teamKey);

			List<User> userList = team.getUsers();
			List<String> studentId = element.getValue();

			for (String item : studentId) {
				User existedUser = userRepository.findByUserName(item);

				if (existedUser != null) {
					userList.add(existedUser);
				}
			}

			teamDao.save(team);
		}

		return "Team has successfully enrolled to " + presentation.get().getTitle() + " presentation";
	}

	/**
	 * Retrieves a list of all teams along with the user information for each team,
	 * and returns it as a list of TeamResponse objects.
	 *
	 * @return a List of TeamResponse objects, each representing a team and its
	 *         associated users
	 */

	@Override
	public List<TeamResponse> teams() {

		List<Team> teams = teamDao.findAll();

		List<TeamResponse> result = new ArrayList<>();
		for (Team team : teams) {

			TeamResponse teamResponse = new TeamResponse();

			teamResponse.setId(team.getId());
			teamResponse.setName(team.getName());

			List<UserResponse> list = team.getUsers().stream()
					.map(user -> new UserResponse(user.getId(), user.getName(), user.getUserName())).toList();

			teamResponse.setUserResponse(list);
			result.add(teamResponse);
		}
		return result;
	}

	@Override
	public List<TeamResponse> teamsByPresentation(Integer presentationId) throws PresentationException {

		Optional<Presentation> existedPresentation = presentationDao.findById(presentationId);

		if (existedPresentation.isEmpty()) {
			throw new PresentationException("Presentation is not present to given presentation id");
		}

		ArrayList<TeamResponse> result = new ArrayList<>();

		List<Team> teams = teamDao.findByPresentation(existedPresentation.get());

		for (Team team : teams) {

			TeamResponse teamResponse = new TeamResponse();

			teamResponse.setId(team.getId());
			teamResponse.setName(team.getName());

			List<UserResponse> list = team.getUsers().stream()
					.map(user -> new UserResponse(user.getId(), user.getName(), user.getUserName())).toList();

			teamResponse.setUserResponse(list);
			result.add(teamResponse);
		}

		return result;
	}

	/**
	 * Resets the CSV file for a given presentation by deleting all teams associated
	 * with that presentation.
	 * 
	 * @param presentationId the ID of the presentation for which to reset the CSV
	 *                       file
	 * @return a String message indicating that the teams have been successfully
	 *         removed from the specified presentation
	 * @throws PresentationException if the specified presentation ID does not exist
	 * @throws TeamException         if there are no teams associated with the
	 *                               specified presentation
	 * @throws AccessDeniedException if the user attempting to reset the CSV file
	 *                               does not have permission to do so
	 */

	@Override
	public String resetCSVFile(Integer presentationId)
			throws PresentationException, TeamException, AccessDeniedException {

		Optional<Presentation> existedPresentation = presentationDao.findById(presentationId);

		if (existedPresentation.isEmpty()) {
			throw new PresentationException("Presentation is not present to given presentation id");
		}

		customSecurity.validateUser(existedPresentation.get().getUser().getId());

		List<Team> teams = teamDao.findByPresentation(existedPresentation.get());

		if (teams.isEmpty()) {
			throw new TeamException("There is no teams");
		}

		teamDao.deleteAll(teams);

		return "Teams has successfully removed to " + existedPresentation.get().getTitle() + " presentation";
	}

	/**
	 * Retrieves a list of all students in a specified team.
	 * 
	 * @param teamId the ID of the team to retrieve the students from
	 * @return a list of UserResponse objects representing the students in the team
	 * @throws TeamException if the specified team does not exist or has no students
	 */

	@Override
	public List<UserResponse> studentInTeam(Integer teamId) throws TeamException {

		Optional<Team> existedTeam = teamDao.findById(teamId);

		if (existedTeam.isEmpty()) {
			throw new TeamException("Invalid team id : " + teamId);
		}

		List<User> students = existedTeam.get().getUsers();

		if (students.isEmpty()) {
			throw new TeamException("There is no students in team - " + existedTeam.get().getName());
		}

		return students.stream().map((user) -> new UserResponse(user.getId(), user.getName(), user.getUserName()))
				.toList();
	}

}
