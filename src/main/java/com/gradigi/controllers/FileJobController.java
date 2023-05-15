package com.gradigi.controllers;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.gradigi.exceptions.FileReadingException;
import com.gradigi.exceptions.PresentationException;
import com.gradigi.exceptions.TeamException;
import com.gradigi.exceptions.UserException;
import com.gradigi.services.FileUserService;
import com.gradigi.services.TeamServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/csv")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class FileJobController {

	@Autowired
	private FileUserService fileUserService;

	@Autowired
	private TeamServiceImpl teamServiceImpl;

	@PostMapping
	public ResponseEntity<String> importCsvToDB(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "presentation", required = true) Integer presentationId)
			throws IOException, FileReadingException, UserException, PresentationException {

		String message = fileUserService.uploadFile(file, presentationId);

		return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
	}

	@DeleteMapping
	public ResponseEntity<String> resetCSV(
			@RequestParam(value = "presentation", required = true) Integer presentationId)
			throws PresentationException, TeamException, AccessDeniedException {

		String resetCSVFile = teamServiceImpl.resetCSVFile(presentationId);

		return new ResponseEntity<>(resetCSVFile, HttpStatus.OK);
	}

}