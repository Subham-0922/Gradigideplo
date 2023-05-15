package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.User;
import com.gradigi.response.TeamResponse;
import com.gradigi.response.UserResponse;

public interface TeamService {

	public String saveTeam(Map<String, List<String>> map, Integer presentationId)
			throws UserException, PresentationException;

	public List<TeamResponse> teams();

	public List<TeamResponse> teamsByPresentation(Integer presentationId) throws PresentationException;

	public String resetCSVFile(Integer presentationId)
			throws PresentationException, TeamException, AccessDeniedException;

	public List<UserResponse> studentInTeam(Integer teamId) throws TeamException;
}
