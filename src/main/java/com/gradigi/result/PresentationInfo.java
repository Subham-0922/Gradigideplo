package com.gradigi.result;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentationInfo {

	private String title;
	private List<TeamInfo> teams;

	public PresentationInfo(String title) {
		this.title = title;
		this.teams = new ArrayList<>();
	}

}
