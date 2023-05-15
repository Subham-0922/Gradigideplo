package com.gradigi.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultResponse {

	private Integer id;
	private Double value;
	private String comment;
	private String presentation;
	private String team;
	private String section;
	private String sectionAttribute;
	private String judge;
}
