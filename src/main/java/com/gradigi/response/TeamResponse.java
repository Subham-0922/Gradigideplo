package com.gradigi.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamResponse {

	private Integer id;
	private String name;
	private List<UserResponse> userResponse;
}
