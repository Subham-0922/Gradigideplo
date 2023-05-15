package com.gradigi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gradigi.model.Presentation;
import com.gradigi.model.Team;

public interface TeamDao extends JpaRepository<Team, Integer>{
	
  public List<Team> findByPresentation(Presentation presentaion);
	
}
