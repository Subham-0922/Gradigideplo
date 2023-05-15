package com.gradigi.controllers;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gradigi.LoginAndSecurity.SecurityConfiguratons.TokenExtractor;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.PresentationStatus;
import com.gradigi.model.Section;
import com.gradigi.response.SectionRequest;
import com.gradigi.response.SectionResponse;
import com.gradigi.services.PresentationServiceImpl;
import com.gradigi.services.SectionServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/sections")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SectionController {

	@Autowired
	private SectionServiceImpl sectionServiceImpl;
	
	@Autowired
	private PresentationServiceImpl presentationServiceImpl;

	@PostMapping("/save")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> saveSection(@RequestBody List<SectionRequest> section,
			@RequestParam(value = "presentation", required = true) Integer presentationId)
			throws SectionException, UserException, PresentationException, AccessDeniedException {

		for(SectionRequest sectionresponce:section) {
			sectionresponce.getSection().setPresent(1);
		}
		String saveSection = sectionServiceImpl.saveSection(section, presentationId);

		return new ResponseEntity<>(saveSection, HttpStatus.CREATED);
	}

	@GetMapping("/presentation/{presentationId}")
	public ResponseEntity<List<SectionResponse>> findSectionsByPresentationId(
			@PathVariable("presentationId") Integer presentationId)
			throws AccessDeniedException, PresentationException, SectionException {

		List<SectionResponse> sections = sectionServiceImpl.findByPresentation(presentationId);

		return new ResponseEntity<>(sections, HttpStatus.OK);
	}

	@DeleteMapping("/{sectionId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteSection(@PathVariable("sectionId") Integer sectionId)
			throws SectionException, AccessDeniedException {

		String deleteSection = sectionServiceImpl.deleteSection(sectionId);

		return new ResponseEntity<>(deleteSection, HttpStatus.OK);
	}

	@GetMapping("/{sectionId}")
	public ResponseEntity<SectionResponse> sectionById(@PathVariable("sectionId") Integer sectionId)
			throws SectionException {

		SectionResponse section = sectionServiceImpl.sectionById(sectionId);

		return new ResponseEntity<>(section, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping
	public ResponseEntity<Section> updateSection(HttpServletRequest request, @RequestBody Section section)
			throws SectionException, UserException {

		int user_id = TokenExtractor.extractToken(request);

		Section updateSection = sectionServiceImpl.updateSection(section, user_id);

		return new ResponseEntity<>(updateSection, HttpStatus.OK);
	}
	
	//update the section
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("update")
	public ResponseEntity<Section> updateSectionByPresent(HttpServletRequest request, @RequestBody Section section)
			throws SectionException, UserException {

		int user_id = TokenExtractor.extractToken(request);

		Section updateSection = sectionServiceImpl.updateSectionByPresent(section, user_id);

		return new ResponseEntity<>(updateSection, HttpStatus.OK);
	}


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/user")
	public ResponseEntity<List<Presentation>> assignUser(HttpServletRequest request)
			throws UserException, AccessDeniedException, PresentationException {

		System.out.println("heelo");
		int userId = TokenExtractor.extractToken(request);		
//		updatePresentationByTime();

		List<Presentation> presentationsByAssignUser = sectionServiceImpl.presentationsByAssignUser(userId);

		return new ResponseEntity<>(presentationsByAssignUser, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/user/{presentationId}")
	public ResponseEntity<SectionResponse> sectionToAssign(HttpServletRequest request,
			@PathVariable("presentationId") Integer presentationId)
			throws SectionException, PresentationException, AccessDeniedException, UserException {

		int user_id = TokenExtractor.extractToken(request);

		SectionResponse section = sectionServiceImpl.sectionByUser(user_id, presentationId);

		return new ResponseEntity<>(section, HttpStatus.OK);
	}
	
//	public void updatePresentationByTime() throws PresentationException, AccessDeniedException {
//		
//		List<Presentation> presentationList=presentationServiceImpl.findAllPresentation();
//		for(Presentation presentation:presentationList) {
//			int id=presentation.getId();
//			
//			long mints1= ChronoUnit.MINUTES.between(presentation.getStart(), LocalDateTime.now());
//			long mints2= ChronoUnit.MINUTES.between(presentation.getEnd(), LocalDateTime.now());
//			
//		    if(mints1>0 && mints2<0) {
//				presentation.setPStatus(PresentationStatus.ONGOING);
//			}
//			else if(mints2>0) {
//				presentation.setPStatus(PresentationStatus.DONE);
//			}
//			
//			presentationServiceImpl.updatePresentation(presentation, id);
//		}
//		
//	}

}
