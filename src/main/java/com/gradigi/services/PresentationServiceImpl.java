package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.Section;
import com.gradigi.model.Team;
import com.gradigi.model.User;
import com.gradigi.repository.PresentationDao;
import com.gradigi.repository.SectionDao;
import com.gradigi.repository.TeamDao;
import com.gradigi.repository.UserRepository;
import com.gradigi.response.PresentationCreate;
import com.gradigi.response.PresentationResponse;

@Service
public class PresentationServiceImpl implements PresentationService {

	@Autowired
	private PresentationDao presentationDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomSecurity customSecurity;
	
	@Autowired
	private TeamDao teamDao;
	
	@Autowired
	private SectionDao sectionDao;
	
	@Autowired
	private SectionServiceImpl sectionServiceImpl;

	/**
	 * Creates a new presentation for a given user ID.
	 * 
	 * @param presentation The presentation to be created.
	 * @param userId       The ID of the user creating the presentation.
	 * @return A PresentationCreate object containing the saved presentation and a success status message.
	 * @throws UserException         If the provided user ID is invalid.
	 * @throws AccessDeniedException If the provided user ID is not authorized to create a new presentation.
	 */

	@Override
	public PresentationCreate createPresentation(Presentation presentation, Integer userId)
			throws UserException, AccessDeniedException {

		Optional<User> existedUser = userRepository.findById(userId);

		if (existedUser.isEmpty()) {
			throw new UserException("Invalid user id");
		}

		presentation.setUser(existedUser.get());

		PresentationCreate presentationCreate = new PresentationCreate();

		presentationCreate.setPresentation(presentationDao.save(presentation));
		presentationCreate.setStatus("Presentation created successfully");

		return presentationCreate;
	}

	/**
	 * Retrieves a list of presentations based on the provided page number and page
	 * size parameters. If either parameter is null, all presentations are returned.
	 * Otherwise, a paged result is returned using the provided page number and page
	 * size.
	 *
	 * @param pageNumber The number of the requested page. If null, all
	 *                   presentations are returned.
	 * @param pageSize   The size of the requested page. If null, all presentations
	 *                   are returned.
	 * @return A PresentationResponse object encapsulating the result set.
	 */

	@Override
	public PresentationResponse presentations(Integer pageNumber, Integer pageSize) {

		PresentationResponse presentationResponse = new PresentationResponse();

		if (pageNumber == null || pageSize == null) {

			List<Presentation> presentations = presentationDao.findAll();

			presentationResponse.setContent(presentations);
			return presentationResponse;
		}

		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

		Page<Presentation> page = presentationDao.findAll(pageable);

		List<Presentation> presentations = page.getContent();

		presentationResponse.setContent(presentations);
		presentationResponse.setPageNumber(pageNumber);
		presentationResponse.setPageSize(pageSize);
		presentationResponse.setTotalPages(page.getTotalPages());
		presentationResponse.setLastPage(page.isLast());

		return presentationResponse;
	}

	/**
	 * Retrieves a list of presentations with the given type.
	 * 
	 * @param type the type of presentation to retrieve
	 * @return a list of Presentation objects with the given type
	 * @throws UserException 
	 * @throws AccessDeniedException 
	 */

	@Override
	public List<Presentation> searchByType(String type, Integer userId,Integer pageNumber, Integer pageSize) throws AccessDeniedException, UserException {
		 
		 List<Presentation> presentations = searchByUser(userId, pageNumber, pageSize).getContent();
		 
		 List<Presentation> list = new ArrayList<>();
		 for(Presentation item : presentations) {
			 if(item.getType().toLowerCase().equals(type.toLowerCase())) {
				 list.add(item);
			 }
		 }
		 return list;
	}

	/**
	 * Searches for a list of presentations created by a user with the given userID.
	 * 
	 * @param userId The ID of the user whose presentations are being searched for.
	 * @return A List of Presentation objects created by the specified user.
	 * @throws UserException         If the provided user ID does not exist in the  UserRepository.
	 * @throws AccessDeniedException If the provided user ID is not authorized to search for presentations.
	 */

	@Override
	public PresentationResponse searchByUser(Integer userId, Integer pageNumber, Integer pageSize) throws UserException, AccessDeniedException {

		Optional<User> existedUser = userRepository.findById(userId);

		if (existedUser.isEmpty()) {
			throw new UserException("User doesn't exist having userId : " + userId);
		}
		
		 PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);

		 Page<Presentation> page = presentationDao.findByUser(existedUser.get(), pageRequest);
		
		 PresentationResponse presentationResponse = new PresentationResponse();
		

			presentationResponse.setContent( page.getContent());
			presentationResponse.setPageNumber(pageNumber);
			presentationResponse.setPageSize(pageSize);
			presentationResponse.setTotalPages(page.getTotalPages());
			presentationResponse.setLastPage(page.isLast());
		 
		 return presentationResponse;
	}

	/**
	 * Updates an existing presentation with the given presentationId using the information provided in the input Presentation object.
	 *
	 * @param presentation   an object representing the new information for the existing presentation.
	 * @param presentationId the ID of the presentation to be updated.
	 * @return a PresentationCreate object containing the updated presentation information and a success message.
	 * @throws PresentationException if the presentation with the given presentationId does not exist.
	 * @throws AccessDeniedException
	 */

	@Override
	public PresentationCreate updatePresentation(Presentation presentation, Integer presentationId)
			throws PresentationException, AccessDeniedException {

		PresentationCreate presentationUpdate = new PresentationCreate();

		Optional<Presentation> existedPresentation = presentationDao.findById(presentationId);

		if (existedPresentation.isEmpty()) {
			throw new PresentationException("Invalid presentationId : " + presentation.getId());
		}

		Integer userId = existedPresentation.get().getUser().getId();

		customSecurity.validateUser(userId);

		if (presentation.getStart() != null)
			existedPresentation.get().setStart(presentation.getStart());

		if (presentation.getEnd() != null)
			existedPresentation.get().setEnd(presentation.getEnd());

		if (presentation.getStart() != null)
			existedPresentation.get().setStart(presentation.getStart());

		if (presentation.getMeetingLink() != null)
			existedPresentation.get().setMeetingLink(presentation.getMeetingLink());

		if (presentation.getTitle() != null)
			existedPresentation.get().setTitle(presentation.getTitle());

		if (presentation.getType() != null)
			existedPresentation.get().setType(presentation.getType());

//		System.out.println("ok done");
		
		presentationUpdate.setStatus("Presentation updated successfully");
		presentationUpdate.setPresentation(presentationDao.save(existedPresentation.get()));

		return presentationUpdate;
	}

	/**
	 * Deletes the presentation with the given presentationId.
	 * 
	 * @param presentationId the id of the presentation to be deleted
	 * @return a string indicating that the presentation was deleted successfully
	 * @throws PresentationException if a presentation with the given presentationId does not exist in the system
	 * @throws AccessDeniedException
	 * @throws SectionException 
	 */

	@Override
	public String deletePresentation(Integer presentationId) throws PresentationException, AccessDeniedException, SectionException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("Invalid presentationId : " + presentationId);
		}
		
		customSecurity.validateUser(presentation.get().getUser().getId());
		
		
		List<Section> sections = sectionDao.findByPresentation(presentation.get());
		
		for(Section section : sections) {
			
			sectionServiceImpl.deleteSection(section.getId());
		}
		
		List<Team> teams = teamDao.findByPresentation(presentation.get());
		
		if(!teams.isEmpty()) {
			
		 teamDao.deleteAll(teams);
		}
	
		presentationDao.delete(presentation.get());

		return "Presentation delete successfully";
	}

	/**
	 * Retrieves the presentation with the given presentationId.
	 * 
	 * @param presentationId the id of the presentation to be retrieved
	 * @return the Presentation object with the given presentationId
	 * @throws PresentationException if a presentation with the given presentationId does not exist in the system
	 */

	@Override
	public Presentation searchPresentationBypresentationId(Integer presentationId) throws PresentationException {

		Optional<Presentation> presentation = presentationDao.findById(presentationId);

		if (presentation.isEmpty()) {
			throw new PresentationException("Presentation not found for given id");
		}
		return presentation.get();
	}

	@Override
	public List<Presentation> searchByName(String name, Integer userId, Integer pageNumber, Integer pageSize) throws AccessDeniedException, UserException {
		
		 List<Presentation> presentations = searchByUser(userId, pageNumber, pageSize).getContent();
		
		 List<Presentation> list = new ArrayList<>();
	     for(Presentation item :presentations) {
		   
		    if(item.getTitle().toLowerCase().contains(new StringBuffer(name.toLowerCase()))) {
			   list.add(item);
		    }
	    }
			
		return list;
		
	}

	@Override
	public List<Presentation> findAllPresentation() throws PresentationException {
		// TODO Auto-generated method stub
		
		List<Presentation> presentationList=presentationDao.findAll();
		if(presentationList.isEmpty()) {
			throw new PresentationException("Presentation not Present");
		}
		return presentationList;
	}
	
	
	

}
