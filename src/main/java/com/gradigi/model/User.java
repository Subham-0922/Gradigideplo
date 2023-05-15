package com.gradigi.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String userName;
	
	@Column(nullable = false)
	private String name;


	@Column(nullable = false, unique = true)
	@Email(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
	private String email;

	@Column(nullable = false)
	private String password;
	
	@JsonIgnore
	@Column(nullable = false)
	private String role;

	@ManyToMany(mappedBy = "users")
	private List<Team> team = new ArrayList<>();
}
