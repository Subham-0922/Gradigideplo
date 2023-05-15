package com.gradigi.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponse {

	private Integer id;
	private String name;
	//change by Dhiraj
	
	private Integer present;
	private UserResponse assignedUser;

}
