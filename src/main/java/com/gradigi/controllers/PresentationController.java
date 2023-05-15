package com.gradigi.controllers;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.LoginAndSecurity.SecurityConfiguratons.TokenExtractor;
import com.gradigi.LoginAndSecurity.SecurityConfiguratons.UserDetailServiceImpl;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.SectionException;
import com.gradigi.exceptions.UserException;
import com.gradigi.model.Presentation;
import com.gradigi.model.PresentationStatus;
import com.gradigi.response.PresentationCreate;
import com.gradigi.response.PresentationResponse;
import com.gradigi.services.PresentationServiceImpl;
import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/presentations")
public class PresentationController {

	@Autowired
	private UserDetailServiceImpl userDetailsService;

	@Autowired
	private PresentationServiceImpl presentationServiceImpl;

	@Autowired
	private CustomSecurity customSecurity;

	@PostMapping("/save")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<PresentationCreate> createHandler(HttpServletRequest request,
			@RequestBody Presentation presentation) throws UserException, AccessDeniedException {

		int user_id = TokenExtractor.extractToken(request);

		customSecurity.validateUser(user_id);

		presentation.setPStatus(PresentationStatus.UPCOMING);
		PresentationCreate createPresentation = presentationServiceImpl.createPresentation(presentation, user_id);

		return new ResponseEntity<>(createPresentation, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<PresentationResponse> presentationsHandle(
			@RequestParam(value = "page", required = false) Integer pageNumber,
			@RequestParam(value = "size", required = false) Integer pageSize) {

		PresentationResponse presentations = presentationServiceImpl.presentations(pageNumber, pageSize);

		return new ResponseEntity<>(presentations, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/search")
	public ResponseEntity<List<Presentation>> searchByType(HttpServletRequest request,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer pageSize)
			throws AccessDeniedException, UserException {

		int user_id = TokenExtractor.extractToken(request);

		if (type != null) {
			System.out.println("type inside controller -- " + type);
			List<Presentation> presentations = presentationServiceImpl.searchByType(type.trim(),user_id,pageNumber, pageSize);
			this.sortPresentationByStartingDate(presentations);
			
			return new ResponseEntity<>(presentations, HttpStatus.OK);
		}

		
		List<Presentation> searchByName = presentationServiceImpl.searchByName(name, user_id, pageNumber, pageSize);
		
		//provide sorting based on the Starting Date
//		System.out.println("start the sorting");
		
		sortPresentationByStartingDate(searchByName);
		
//		System.out.println("end the sorting");
		
		return new ResponseEntity<>(searchByName, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/user")
	public ResponseEntity<PresentationResponse> searchByUser(HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNumber,
			@RequestParam(value = "size", defaultValue = "10", required = false) Integer pageSize)
			throws UserException, AccessDeniedException {

		int user_id = TokenExtractor.extractToken(request);

		customSecurity.validateUser(user_id);

		PresentationResponse searchByUser = presentationServiceImpl.searchByUser(user_id, pageNumber, pageSize);

		return new ResponseEntity<>(searchByUser, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{presentationId}")
	public ResponseEntity<String> deletePresentation(HttpServletRequest request,
			@PathVariable("presentationId") Integer presentationId)
			throws PresentationException, AccessDeniedException, UserException, SectionException {

		String deletePresentation = presentationServiceImpl.deletePresentation(presentationId);

		return new ResponseEntity<>(deletePresentation, HttpStatus.ACCEPTED);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{presentationId}")
	public ResponseEntity<PresentationCreate> updatePresentation(@RequestBody Presentation presentation,
			@PathVariable("presentationId") Integer presentationId, HttpServletRequest request)
			throws PresentationException, AccessDeniedException, UserException {

		PresentationCreate update = presentationServiceImpl.updatePresentation(presentation, presentationId);

		return new ResponseEntity<>(update, HttpStatus.ACCEPTED);
	}

	@GetMapping("/{presentationId}")
	public ResponseEntity<Presentation> searchPresentationById(@PathVariable("presentationId") Integer presentationId)
			throws PresentationException {

		Presentation presentation = presentationServiceImpl.searchPresentationBypresentationId(presentationId);

		return new ResponseEntity<>(presentation, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping
     public String updatePresentationByTime() throws PresentationException, AccessDeniedException {
		
		List<Presentation> presentationList=presentationServiceImpl.findAllPresentation();
		for(Presentation presentation:presentationList) {
			int id=presentation.getId();
			
			long mints1= ChronoUnit.MINUTES.between(presentation.getStart(), LocalDateTime.now());
			long mints2= ChronoUnit.MINUTES.between(presentation.getEnd(), LocalDateTime.now());
			
		    if(mints1>0 && mints2<0) {
				presentation.setPStatus(PresentationStatus.ONGOING);
			}
			else if(mints2>0) {
				presentation.setPStatus(PresentationStatus.DONE);
			}
			
			presentationServiceImpl.updatePresentation(presentation, id);
		}
		return "Presentations update Ontime";
	}
	
	
	
	public void sortPresentationByStartingDate(List<Presentation> presentations) {
		
		Collections.sort(presentations, new Comparator<Presentation>() {
			
			@Override
			public int compare(Presentation o1, Presentation o2) {
				// TODO Auto-generated method stub
				long days1=ChronoUnit.DAYS.between(o1.getStart(), LocalDateTime.now());
				
				long days2=ChronoUnit.DAYS.between(o2.getStart(), LocalDateTime.now());
				if(days1<days2) {
					return 1;
				}
				else {
					return -1;
				}
				
			}
		});
		
	}

}