package com.gradigi.controllers;

import java.nio.file.AccessDeniedException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gradigi.exceptions.SectionException;
import com.gradigi.model.SectionAttribute;
import com.gradigi.services.SectionAttributeImpl;

@CrossOrigin
@RestController
@RequestMapping("/attributes")
public class SectionAttributeController {

	@Autowired
	private SectionAttributeImpl sectionAttributeImpl;

	@PostMapping("/save")
	public ResponseEntity<String> saveSectionAttribute(@RequestBody List<SectionAttribute> sectionAttribute,
			@RequestParam(value = "section", required = true) Integer sectionId)
			throws SectionException, AccessDeniedException {

		String sectionAttribute2 = sectionAttributeImpl.save(sectionAttribute, sectionId);
        System.out.println("attribute save");
		return new ResponseEntity<>(sectionAttribute2, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<SectionAttribute>> findAllSectionAttribute(
			@RequestParam(value = "section", required = true) Integer sectionId)
			throws SectionException, AccessDeniedException {

		List<SectionAttribute> sectionAttributes = sectionAttributeImpl.findBySectionId(sectionId);

		return new ResponseEntity<>(sectionAttributes, HttpStatus.OK);
	}

	@DeleteMapping("/{attributeId}")

	@PreAuthorize("hasRole('ROLE_ADMIN')")

	public ResponseEntity<String> deleteSectionAttributeById(@PathVariable("attributeId") Integer attributeId)
			throws SectionException, AccessDeniedException {

		String sectionAttributes = sectionAttributeImpl.deleteAttributeById(attributeId);

		return new ResponseEntity<>(sectionAttributes, HttpStatus.OK);
	}
}
