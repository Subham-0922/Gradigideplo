package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.ResultException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.model.Presentation;
import com.gradigi.model.Result;
import com.gradigi.model.Section;
import com.gradigi.model.SectionAttribute;
import com.gradigi.model.Team;
import com.gradigi.repository.PresentationDao;
import com.gradigi.repository.ResultDao;
import com.gradigi.repository.SectionAttributeDao;
import com.gradigi.repository.SectionDao;
import com.gradigi.repository.TeamDao;
import com.gradigi.response.ResultList;
import com.gradigi.response.ResultResponse;
import com.gradigi.result.AttributeInfo;
import com.gradigi.result.PresentationInfo;
import com.gradigi.result.SectionInfo;
import com.gradigi.result.TeamInfo;
import com.gradigi.sorting.TeamSorting;

@Service
public class ResultServiceImpl implements ResultService {

	@Autowired
	private ResultDao resultDao;

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private SectionAttributeDao sectionAttributeDao;

	@Autowired
	private PresentationDao presentationDao;

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private CustomSecurity customSecurity;

	@Override
	public ResultResponse saveResult(Result result, Integer sectionAttributeId, Integer teamId)
			throws SectionException, TeamException, AccessDeniedException {

		Optional<SectionAttribute> sectionAttribute = sectionAttributeDao.findById(sectionAttributeId);

		if (sectionAttribute.isEmpty()) {
			throw new SectionException("Invalid section attribute id");
		}

		Integer user_id = sectionAttribute.get().getSection().getAssignedUser().getId();
		customSecurity.validateUser(user_id);

		Optional<Team> team = teamDao.findById(teamId);

		if (team.isEmpty())
			throw new TeamException("team is not present to given teamId ");

		List<Result> teamsResult = resultDao.findByTeam(team.get());

		boolean existedResult = teamsResult.stream()
				.anyMatch(item -> item.getSectionAttribute().getId().equals(sectionAttributeId));

		if (existedResult) {
			throw new TeamException("Result has already added to " + sectionAttribute.get().getName());
		}

		Section section = sectionAttribute.get().getSection();

		Presentation presentation = section.getPresentation();

		result.setPresentation(presentation);

		result.setSection(section);

		result.setSectionAttribute(sectionAttribute.get());

		result.setTeam(team.get());

		result.setUser(section.getAssignedUser());

		resultDao.save(result);

		ResultResponse resultResponse = new ResultResponse();

		resultResponse.setId(result.getId());
		resultResponse.setSection(section.getName());
		resultResponse.setPresentation(presentation.getTitle());
		resultResponse.setTeam(team.get().getName());
		resultResponse.setValue(result.getValue());
		resultResponse.setComment(result.getComment());
		resultResponse.setSectionAttribute(sectionAttribute.get().getName());
		resultResponse.setJudge(section.getAssignedUser().getName());

		return resultResponse;
	}

	@Override
	public List<ResultResponse> findResultByTeamId(Integer teamId) throws TeamException, ResultException {

		Optional<Team> team = teamDao.findById(teamId);

		if (team.isEmpty()) {
			throw new TeamException("Team is not present to given teamId");
		}

		List<Result> results = resultDao.findByTeam(team.get());

		if (results.isEmpty()) {
			throw new ResultException("Result is not available for given team");
		}

		List<ResultResponse> response = new ArrayList<>();

		for (Result result : results) {

			ResultResponse resultResponse = new ResultResponse();

			resultResponse.setId(result.getId());
			resultResponse.setSection(result.getSection().getName());
			resultResponse.setPresentation(result.getPresentation().getTitle());
			resultResponse.setTeam(result.getTeam().getName());
			resultResponse.setValue(result.getValue());
			resultResponse.setComment(result.getComment());
			resultResponse.setSectionAttribute(result.getSectionAttribute().getName());
			resultResponse.setJudge(result.getUser().getName());

			response.add(resultResponse);
		}
		return response;
	}

	@Override
	public List<ResultList> winner(Integer presentationId) throws PresentationException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("Presentation doesn't exist ");
		}

		List<Result> results = resultDao.findByPresentation(presentation.get());

		Map<Team, Double> sortTeam = sortTeam(results);

		return resultResponse(sortTeam);
	}

	@Override
	public List<ResultList> winnerByAtrribute(Integer sectionId) throws PresentationException {

		Optional<Section> section = sectionDao.findById(sectionId);

		List<Result> results = resultDao.findBySection(section.get());

		Map<Team, Double> sortTeam = sortTeam(results);

		return resultResponse(sortTeam);
	}

	public List<ResultList> resultResponse(Map<Team, Double> teamValue) {
		List<ResultList> resultResponse = new ArrayList<>();

		Integer sequence = 1;
		for (Map.Entry<Team, Double> entry : teamValue.entrySet()) {

			ResultList response = new ResultList();

			response.setSequence(sequence++);
			response.setTeam(entry.getKey().getName());
			response.setValue(entry.getValue());
			response.setType(entry.getKey().getPresentation().getType());

			resultResponse.add(response);
		}

		return resultResponse;
	}

	public Map<Team, Double> sortTeam(List<Result> results) {

		Map<Team, Double> teamValue = new HashMap<>();

		int i = 0;
		for (Result result : results) {

			Double value = result.getValue();
			Team team = result.getTeam();

//			if(teamValue.containsKey(team)) {
//				teamValue.put(team, teamValue.get(team)+ value);
//			}else {
//				
//				PrensentationResult prensentationResult = new PrensentationResult();
//				prensentationResult.setAttribute(result.getSectionAttribute().getName());
//				prensentationResult.setSection(result.getSection().getName());
//				prensentationResult.setSequence(i++);
////				prensentationResult.set
//			}
			teamValue.put(team, teamValue.getOrDefault(team, 0.0 + value));
		}

		List<Map.Entry<Team, Double>> list = new LinkedList<>(teamValue.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new TeamSorting());

		// Creating a LinkedHashMap from the sorted list
		Map<Team, Double> sortedMap = new LinkedHashMap<>();

		for (Map.Entry<Team, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	@Override
	public PresentationInfo results(Integer presentationId) throws PresentationException, ResultException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("Presentation doesn't exist ");
		}

		List<Result> results = resultDao.findByPresentation(presentation.get());
		if (results.isEmpty()) {
			throw new ResultException("result not found");
		}

		PresentationInfo presentationInfo = new PresentationInfo(presentation.get().getTitle());

		Map<Integer, Map<Integer, List<Integer>>> uniqueResultIds = new HashMap<>();

		for (Result result : results) {

			Integer teamid = result.getTeam().getId();
			Integer sectionid = result.getSection().getId();

			if (uniqueResultIds.containsKey(teamid)) {///// if map contains teamid

				if (uniqueResultIds.get(teamid).containsKey(sectionid)) {// if team contains sectionid

					uniqueResultIds.get(teamid).get(sectionid).add(result.getSectionAttribute().getId());
				} else {
					uniqueResultIds.get(teamid).put(sectionid,
							new ArrayList<>(Arrays.asList(result.getSectionAttribute().getId())));

				}

			} else {
				Map<Integer, List<Integer>> map = new HashMap<>();

				map.put(sectionid, new ArrayList<>(Arrays.asList(result.getSectionAttribute().getId())));

				uniqueResultIds.put(teamid, map);

			}

		}

		return giveFinalResult(uniqueResultIds, presentationInfo);

	}

	private PresentationInfo giveFinalResult(Map<Integer, Map<Integer, List<Integer>>> uniqueResultIds,
			PresentationInfo presentationInfo) {

		for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry1 : uniqueResultIds.entrySet()) {

			int key1 = entry1.getKey();
			Team team = teamDao.findById(key1).get();
			String teamName = team.getName();
			TeamInfo teamInfo = new TeamInfo(teamName);

			Map<Integer, List<Integer>> innerMap = entry1.getValue();

			for (Map.Entry<Integer, List<Integer>> entry2 : innerMap.entrySet()) {

				int key2 = entry2.getKey();

				String sectionName = sectionDao.findById(key2).get().getName();
				SectionInfo sectionInfo = new SectionInfo(sectionName);

				List<Integer> innerList = entry2.getValue();

				List<Result> results = resultDao.findByTeam(team);
				for (Result result : results) {

					for (Integer value : innerList) {

						if (result.getSectionAttribute().getId() == value) {

							String attributeName = result.getSectionAttribute().getName();
							Double marks = result.getValue();

							AttributeInfo attributeInfo = new AttributeInfo(attributeName, marks);
							sectionInfo.getAttributes().add(attributeInfo);

						}

					}
				}

				teamInfo.getSections().add(sectionInfo);

			}
			presentationInfo.getTeams().add(teamInfo);
		}
		return presentationInfo;

	}
}
