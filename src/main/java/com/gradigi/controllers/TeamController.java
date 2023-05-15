package com.gradigi.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.response.TeamResponse;
import com.gradigi.response.UserResponse;
import com.gradigi.services.TeamServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("/teams")
public class TeamController {

	@Autowired
	private TeamServiceImpl teamServiceImpl;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<TeamResponse>> teamByPresentation(
			@RequestParam(value = "presentation", required = false) Integer presentationId)
			throws PresentationException {

		List<TeamResponse> teams;

		if (presentationId == null) {
			teams = teamServiceImpl.teams();
		} else {
			teams = teamServiceImpl.teamsByPresentation(presentationId);
		}
		return new ResponseEntity<>(teams, HttpStatus.OK);
	}

	@GetMapping("/{teamId}")
	public ResponseEntity<List<UserResponse>> teamById(@PathVariable("teamId") Integer teamId) throws TeamException {

		List<UserResponse> studentInTeam = teamServiceImpl.studentInTeam(teamId);
		return new ResponseEntity<>(studentInTeam, HttpStatus.OK);
	}

}
