package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.ResultException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.model.Result;
import com.gradigi.response.ResultList;
import com.gradigi.response.ResultResponse;
import com.gradigi.result.PresentationInfo;

public interface ResultService {

	public ResultResponse saveResult(Result result, Integer sectionAttributeid, Integer teamId)
			throws SectionException, TeamException, AccessDeniedException;

	public List<ResultResponse> findResultByTeamId(Integer teamId) throws TeamException, ResultException;

	public List<ResultList> winner(Integer presentationId) throws PresentationException;

	public List<ResultList> winnerByAtrribute(Integer sectionId) throws PresentationException;

	public PresentationInfo results(Integer presentationId) throws PresentationException, ResultException;
	
}
