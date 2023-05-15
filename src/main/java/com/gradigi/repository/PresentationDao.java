package com.gradigi.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gradigi.model.Presentation;
import com.gradigi.model.User;

public interface PresentationDao extends JpaRepository<Presentation, Integer> {

	public List<Presentation> findByType(String type);

	public Page<Presentation> findByUser(User user, Pageable pageable);

}
