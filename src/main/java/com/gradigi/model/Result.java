package com.gradigi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Result {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private Double value;

	private String comment;

	@ManyToOne
	private Presentation presentation;

	@ManyToOne
	private Team team;

	@ManyToOne
	private Section section;

	@ManyToOne
	private SectionAttribute sectionAttribute;

	@ManyToOne
	private User user;

	
	
}
