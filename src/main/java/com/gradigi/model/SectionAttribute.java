package com.gradigi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class SectionAttribute {
 
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	private Integer id;

	private String name;
	private String description;
	
	@JsonIgnore
	@ManyToOne
	private Section section;
}
