package com.gradigi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gradigi.model.Presentation;
import com.gradigi.model.Section;

public interface SectionDao extends JpaRepository<Section, Integer> {

	public List<Section> findByPresentation(Presentation presentation);
	
}
