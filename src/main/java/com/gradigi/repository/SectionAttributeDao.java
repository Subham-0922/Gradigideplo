package com.gradigi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gradigi.model.Section;
import com.gradigi.model.SectionAttribute;

public interface SectionAttributeDao extends JpaRepository<SectionAttribute, Integer> {
	
	public List<SectionAttribute> findBySection(Section section);
	

}
