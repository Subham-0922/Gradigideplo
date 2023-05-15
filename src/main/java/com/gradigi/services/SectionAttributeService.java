package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.gradigi.exceptions.SectionException;
import com.gradigi.model.SectionAttribute;

public interface SectionAttributeService {

	public String save(List<SectionAttribute> sectionAttribute, Integer sectionId)
			throws SectionException, AccessDeniedException;

	public List<SectionAttribute> findBySectionId(Integer sectionId) throws SectionException, AccessDeniedException;

	public String deleteAttributeById(Integer attributeId) throws SectionException, AccessDeniedException;
}
