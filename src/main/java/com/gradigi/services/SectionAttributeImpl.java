package com.gradigi.services;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gradigi.LoginAndSecurity.JwtConfigurations.CustomSecurity;
import com.gradigi.exceptions.SectionException;
import com.gradigi.model.Result;
import com.gradigi.model.Section;
import com.gradigi.model.SectionAttribute;
import com.gradigi.repository.ResultDao;
import com.gradigi.repository.SectionAttributeDao;
import com.gradigi.repository.SectionDao;

@Service
public class SectionAttributeImpl implements SectionAttributeService {

	@Autowired
	private SectionAttributeDao sectionAttributeDao;

	@Autowired
	private SectionDao sectionDao;

	@Autowired
	private CustomSecurity customSecurity;
	
	@Autowired
	private ResultDao resultDao;

	/**
	 * Saves a new section attribute for a given section.
	 * 
	 * @param sectionAttribute the SectionAttribute object to be saved
	 * @param sectionId        the ID of the section to which the attribute belongs
	 * @return a message indicating the successful addition of the attribute to the section
	 * @throws SectionException      if the specified section does not exist
	 * @throws AccessDeniedException if the current user does not have permission to add an attribute to the section
	 */

	@Override
	public String save(List<SectionAttribute> sectionAttribute, Integer sectionId)
			throws SectionException, AccessDeniedException {

		Optional<Section> section = sectionDao.findById(sectionId);

		if (section.isEmpty()) {
			throw new SectionException("create section before adding attribute");
		}

		customSecurity.validateUser(section.get().getAssignedUser().getId());

		
		for(SectionAttribute attribute : sectionAttribute) {
			
			if(isSectionAttribute(section.get(), attribute.getName())) {
				throw new SectionException(attribute.getName()+" attribute already exist");
			}
			
			attribute.setSection(section.get());
			sectionAttributeDao.save(attribute);
		}
		return "Section attributes added successfully";
	}

	/**
	 * Retrieves a list of attributes for a given section.
	 * 
	 * @param sectionId the ID of the section for which to retrieve the attributes
	 * @return a list of SectionAttribute objects belonging to the specified section
	 * @throws SectionException    if no attributes are found for the given section ID
	 * @throws AccessDeniedException if the current user does not have permission to access the requested attributes
	 */

	@Override
	public List<SectionAttribute> findBySectionId(Integer sectionId) throws SectionException, AccessDeniedException {

		Optional<Section> section = sectionDao.findById(sectionId);

		if (section.isEmpty()) {
			throw new SectionException("Section is not present to given section id");
		}

		customSecurity.validateUser(section.get().getAssignedUser().getId());

		List<SectionAttribute> sectionAttributes = sectionAttributeDao.findBySection(section.get());

		if (sectionAttributes.isEmpty()) {
			throw new SectionException("There is no attribute in " + section.get().getName() + " section");
		}
		return sectionAttributes;
	}

	/**
	 * Deletes a section attribute with the given ID.
	 *
	 * @param attributeId the ID of the section attribute to be deleted
	 * @return a message indicating the successful deletion of the attribute
	 * @throws SectionException    if the given attribute ID is invalid
	 * @throws AccessDeniedException if the current user does not have permission to delete the attribute
	 */

	@Override
	public String deleteAttributeById(Integer attributeId) throws SectionException, AccessDeniedException {

		Optional<SectionAttribute> attribute = sectionAttributeDao.findById(attributeId);

		if (attribute.isEmpty()) {
			throw new SectionException("Invalid section attribute id");
		}

		customSecurity.validateUser(attribute.get().getSection().getAssignedUser().getId());

		 List<Result> results = resultDao.findBySectionAttribute(attribute.get());
		
		if(!results.isEmpty()) {
			resultDao.deleteAll(results);
		}
		
		sectionAttributeDao.deleteById(attributeId);

		return attribute.get().getName() + " attribute deleted successfully";
	}

	
	public boolean isSectionAttribute(Section section, String attribute) {
		
		List<SectionAttribute> attributes = sectionAttributeDao.findBySection(section);
		
		for(SectionAttribute item : attributes){
			if(item.getName().equals(attribute)) return true;
		}
		return false;
	}
}
