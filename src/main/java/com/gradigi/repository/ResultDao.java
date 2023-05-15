package com.gradigi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gradigi.model.Presentation;
import com.gradigi.model.Result;
import com.gradigi.model.Section;
import com.gradigi.model.SectionAttribute;
import com.gradigi.model.Team;

public interface ResultDao extends JpaRepository<Result, Integer> {

	public List<Result> findByTeam(Team team);

	public List<Result> findByPresentation(Presentation presentaion);

	public List<Result> findBySection(Section section);

	public List<Result> findBySectionAttribute(SectionAttribute sectionAttribute);
}
