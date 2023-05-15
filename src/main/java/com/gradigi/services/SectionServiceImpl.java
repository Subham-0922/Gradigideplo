package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.Result;
import com.gradigi.model.Section;
import com.gradigi.model.SectionAttribute;
import com.gradigi.model.User;
import com.gradigi.repository.PresentationDao;
import com.gradigi.repository.ResultDao;
import com.gradigi.repository.SectionAttributeDao;
import com.gradigi.repository.SectionDao;
import com.gradigi.repository.UserRepository;
import com.gradigi.response.SectionRequest;
import com.gradigi.response.SectionResponse;
import com.gradigi.response.UserResponse;

@Service
public class SectionServiceImpl implements SectionService {

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PresentationDao presentationDao;

	@Autowired
	private CustomSecurity customSecurity;
	
	@Autowired
	private ResultDao resultDao;
	
	@Autowired
	private SectionAttributeDao sectionAttributeDao;

	/**
	*
	*	Creates and saves a new section for the given presentation, assigned to the specified user.
	*
	*	@param section the Section object to be created and saved
	*	@param userId the ID of the user to whom the section is assigned
	*	@param presentationId the ID of the presentation to which the section belongs
	*	@return a SectionCreate object containing details of the newly created section
	*	@throws SectionException if the section already exists in the presentation
	*	@throws UserException if the specified user does not exist
	*	@throws PresentationException if the specified presentation does not exist
	*	@throws AccessDeniedException if the current user does not have access to the presentation
	*/
	
	@Override
	public String saveSection(List<SectionRequest> sections, Integer presentationId)
			throws SectionException, UserException, PresentationException, AccessDeniedException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("Presentation doesn't exist of given id");
		}

	    customSecurity.validateUser(presentation.get().getUser().getId());

		List<Section> existedSections = sectionDao.findByPresentation(presentation.get());

		
		for(SectionRequest request : sections) {
			//in this line i add one condition 
			boolean exist = existedSections.stream().anyMatch(item -> item.getName().equals(request.getSection().getName()) && item.getPresent()==1);
			
			if (exist) {
				throw new SectionException(request.getSection().getName() + " section already exist");
			}
			
			boolean reviewer = existedSections.stream().anyMatch(item -> item.getAssignedUser().getId().equals(request.getReveiwerId()) && item.getPresent()==1);
			
			Optional<User> user = userRepository.findById(request.getReveiwerId());
			
			if (user.isEmpty()) {
				throw new UserException("Invalid reveiwer id");
			}
			
			if (reviewer) {
				throw new SectionException(user.get().getName() + " has already assigned to section");
			}
			
			request.getSection().setAssignedUser(user.get());
			request.getSection().setPresentation(presentation.get());
			
			sectionDao.save(request.getSection());
		}
		
		return "Section added successfully";
	}
	
	/**
	*
	*	Returns a list of SectionResponse objects for all the sections in the given presentation.
	*
	*	@param presentationId the ID of the presentation for which to retrieve sections
	*	@return a List of SectionResponse objects containing details of each section in the presentation
	*	@throws PresentationException if the specified presentation does not exist
	*	@throws SectionException if no sections are available for the given presentation ID
	 * @throws AccessDeniedException 
	*/

	@Override
	public List<SectionResponse> findByPresentation(Integer presentationId)
			throws PresentationException, SectionException, AccessDeniedException {
		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("presentation not found to given id");
		}
		
		customSecurity.validateUser(presentation.get().getUser().getId());

		List<Section> sections = sectionDao.findByPresentation(presentation.get());

		if (sections.isEmpty()) {
			throw new SectionException("no sections are available for given presentation id");
		}

//		Section sec=new Section(presentationId, null, false, null, null);
		
		return sections.stream()
				//section present change by Dhiraj
				.map(section -> new SectionResponse(section.getId(), section.getName(),section.getPresent(),
						new UserResponse(section.getAssignedUser().getId(), section.getAssignedUser().getName(),
								section.getAssignedUser().getUserName())))
				.toList();
	}
	
	/**
	*
	*	Deletes the section with the specified section ID, if the authenticated user is authorized to do so.
	*
	*	@param sectionId the ID of the section to delete
	*	@return a String message indicating whether the section was deleted successfully
	*	@throws SectionException if the specified section does not exist
	*	@throws AccessDeniedException if the authenticated user is not authorized to delete the section
	*/

	@Override
	public String deleteSection(Integer sectionId) throws SectionException, AccessDeniedException {

		Optional<Section> section = sectionDao.findById(sectionId);

		if (section.isEmpty()) {
			throw new SectionException("Invalid section id");
		}

		Integer userId = section.get().getPresentation().getUser().getId();
		customSecurity.validateUser(userId);
		
		List<SectionAttribute> attributes = sectionAttributeDao.findBySection(section.get());
		
		if(!attributes.isEmpty()) {
			
			for(SectionAttribute item : attributes) {
				 List<Result> results = resultDao.findBySectionAttribute(item);
					
				if(!results.isEmpty()) {
					resultDao.deleteAll(results);
				}
			}
		}
		
		sectionAttributeDao.deleteAll(attributes);
		sectionDao.delete(section.get());

		return section.get().getName() + " section deleted successfully";
	}
	
	/**
	*
	*	Updates the assigned user of the section with the specified section ID to the user with the specified user ID.
	*
	*	@param section the Section object containing the section ID and the new assigned user details
	*	@param userId the ID of the user to whom the section is to be assigned
	*	@return the updated Section object
	*	@throws SectionException if the specified section does not exist
	*/

	@Override
	public Section updateSection(Section section, Integer userId) throws SectionException {

		Optional<Section> existedSection = sectionDao.findById(section.getId());

		if (existedSection.isEmpty()) {
			throw new SectionException("Invalid section id");
		}

		Optional<User> user = userRepository.findById(userId);

		existedSection.get().setAssignedUser(user.get());

		return existedSection.get();
	}
	
	
	
	@Override
	public Section updateSectionByPresent(Section section, Integer userId) throws SectionException {

		Optional<Section> existedSection1 = sectionDao.findById(section.getId());

		if (existedSection1.isEmpty()) {
			throw new SectionException("Invalid section id");
		}

//		Optional<User> user = userRepository.findById(userId);
//
//		existedSection.get().setAssignedUser(user.get());
		Section existedSection=existedSection1.get();
		existedSection.setName(section.getName());
		existedSection.setPresent(section.getPresent());
        
		return sectionDao.save(existedSection);
	}
	
	/**
	*
	*	Retrieves the details of the section with the specified section ID.
	*
	*	@param sectionId the ID of the section to retrieve
	*	@return the SectionResponse object containing the section details
	*	@throws SectionException if the specified section does not exist
	*/

	@Override
	public SectionResponse sectionById(Integer sectionId) throws SectionException {
		Optional<Section> section = sectionDao.findById(sectionId);

		if (section.isEmpty()) {
			throw new SectionException("Invalid section id");
		}
		SectionResponse sectionResponse = new SectionResponse();

		sectionResponse.setId(section.get().getId());
		sectionResponse.setName(section.get().getName());
		//change by Dhiraj
		sectionResponse.setPresent(section.get().getPresent());
		sectionResponse.setAssignedUser(new UserResponse(section.get().getAssignedUser().getId(),
				section.get().getAssignedUser().getName(), section.get().getAssignedUser().getUserName()));

		return sectionResponse;
	}
	
	/**
	*
	*	Retrieves the section assigned to a specific user for a given presentation.
	*	@param userId the ID of the user for whom to retrieve the section
	*	@param presentationId the ID of the presentation to which the section belongs
	*	@return a SectionResponse object containing the ID, name, and assigned user of the section
	*	@throws SectionException if no sections are found for the given presentation
	*	@throws PresentationException if the given presentation ID or user ID is invalid
	*	@throws AccessDeniedException if the user does not have permission to access the requested section
	*/

	@Override
	public SectionResponse sectionByUser(Integer userId, Integer presentationId)
			throws SectionException, PresentationException, AccessDeniedException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("Invalid presentation id : " + presentationId);
		}

		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			throw new PresentationException("Invalid user id");
		}

		customSecurity.validateUser(userId);

		List<Section> sections = sectionDao.findByPresentation(presentation.get());

		if (sections.isEmpty()) {
			throw new SectionException("");
		}

		SectionResponse sectionResponse = new SectionResponse();
		for (Section section : sections) {

			if (section.getAssignedUser().getId() == userId) {

				sectionResponse.setId(section.getId());
				sectionResponse.setName(section.getName());
				//change by Dhiraj
				sectionResponse.setPresent(section.getPresent());
				sectionResponse.setAssignedUser(new UserResponse(section.getAssignedUser().getId(),
						section.getAssignedUser().getName(), section.getAssignedUser().getUserName()));
				break;
			}
		}

		return sectionResponse;
	}
	
	/**
	*
	*	Retrieves a list of presentations assigned to a specific user for review.
	*	@param userId the ID of the user for whom to retrieve the presentations
	*	@return a list of Presentation objects assigned to the user for review
	*	@throws UserException if the given user ID is invalid or the user is not assigned to any presentations for review
	*	@throws AccessDeniedException if the user does not have permission to access the requested presentations
	*/

	@Override
	public List<Presentation> presentationsByAssignUser(Integer userId) throws UserException, AccessDeniedException {

		List<Presentation> presentations = new ArrayList<>();

		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			throw new UserException("Invalid user id");
		}
		
		customSecurity.validateUser(userId);

		List<Section> sections = sectionDao.findAll();

		Set<Integer> uniqueIds = new HashSet<>();

		for (Section section : sections) {

			if (section.getAssignedUser().getId() == userId && !uniqueIds.contains(section.getPresentation().getId())) {

				presentations.add(section.getPresentation());

				uniqueIds.add(section.getPresentation().getId());
			}
		}
		if (presentations.isEmpty()) {
			throw new UserException("User is not assigned to any presentation for review");
		}
		return presentations;
	} 

}