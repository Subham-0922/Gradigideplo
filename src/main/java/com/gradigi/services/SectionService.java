package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.Section;
import com.gradigi.response.SectionRequest;
import com.gradigi.response.SectionResponse;

public interface SectionService {

	public String saveSection(List<SectionRequest> section, Integer presentationId)
			throws SectionException, PresentationException, UserException, AccessDeniedException;

	public List<SectionResponse> findByPresentation(Integer presentationId)
			throws PresentationException, SectionException, AccessDeniedException;

	public String deleteSection(Integer sectionId) throws SectionException, AccessDeniedException;

	public SectionResponse sectionById(Integer sectionId) throws SectionException;

	public Section updateSection(Section section, Integer userId) throws SectionException;
	
	public Section updateSectionByPresent(Section section, Integer userId) throws SectionException;

	public SectionResponse sectionByUser(Integer userId, Integer presentationId)
			throws SectionException, AccessDeniedException, PresentationException;

	public List<Presentation> presentationsByAssignUser(Integer userId) throws UserException, AccessDeniedException;
}
