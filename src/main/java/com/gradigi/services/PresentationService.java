package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.List;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.response.PresentationCreate;
import com.gradigi.response.PresentationResponse;

public interface PresentationService {

	public PresentationCreate createPresentation(Presentation presentation, Integer userId)
			throws UserException, AccessDeniedException;

	public PresentationCreate updatePresentation(Presentation presentation, Integer presentationId)
			throws PresentationException, AccessDeniedException;

	public String deletePresentation(Integer presentationId)
			throws SectionException, PresentationException, AccessDeniedException;

	public PresentationResponse presentations(Integer pageNumber, Integer pageSize);

	public List<Presentation> searchByType(String type, Integer userId,Integer pageNumber, Integer pageSize) throws AccessDeniedException, UserException;
	
	public List<Presentation> searchByName(String name, Integer userId, Integer pageNumber, Integer pageSize) throws AccessDeniedException, UserException;

	public  PresentationResponse searchByUser(Integer userId,Integer pageNumber, Integer pageSize) throws UserException, AccessDeniedException;

	public Presentation searchPresentationBypresentationId(Integer presentationId) throws PresentationException;
	
	public List<Presentation> findAllPresentation() throws PresentationException;
}
