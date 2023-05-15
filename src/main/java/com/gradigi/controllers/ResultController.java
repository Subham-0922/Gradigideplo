package com.gradigi.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.ResultException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Result;
import com.gradigi.response.ResultList;
import com.gradigi.response.ResultResponse;
import com.gradigi.result.PresentationInfo;
import com.gradigi.services.ResultServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("/results")
public class ResultController {

	@Autowired
	private ResultServiceImpl resultServiceImpl;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/save")
	public ResponseEntity<ResultResponse> saveResult(HttpServletRequest request, @RequestBody Result result,
			@RequestParam("attribute") Integer attribute, @RequestParam("team") Integer teamId)
			throws SectionException, TeamException, AccessDeniedException, UserException {

		ResultResponse saveResult = resultServiceImpl.saveResult(result, attribute, teamId);

		return new ResponseEntity<>(saveResult, HttpStatus.CREATED);
	}


	@GetMapping
	public ResponseEntity<List<ResultResponse>> findResultByTeam(
			@RequestParam(value = "team", required = true) Integer teamId) throws TeamException, ResultException {


		List<ResultResponse> result = resultServiceImpl.findResultByTeamId(teamId);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/winner/{presentationId}")
	public ResponseEntity<List<ResultList>> winnerHandler(@PathVariable("presentationId") Integer presentationId)
			throws PresentationException {

		List<ResultList> winner = resultServiceImpl.winner(presentationId);
		

		return new ResponseEntity<>(winner, HttpStatus.ACCEPTED);
	}

	@GetMapping("/winner")
	public ResponseEntity<List<ResultList>> winnerAttributeHandler(
			@RequestParam(value = "section", required = true) Integer sectionId) throws PresentationException {

		List<ResultList> winnerByAtrribute = resultServiceImpl.winnerByAtrribute(sectionId);

		return new ResponseEntity<>(winnerByAtrribute, HttpStatus.ACCEPTED);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{presentationId}")
	public ResponseEntity<PresentationInfo> test(@PathVariable("presentationId") Integer presentationId)
			throws PresentationException, ResultException{
		
		PresentationInfo results = resultServiceImpl.results(presentationId);
	
		return new ResponseEntity<>(results, HttpStatus.ACCEPTED);
	}
	

}
