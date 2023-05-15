package com.gradigi.result;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.digester.ArrayStack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamInfo {
	private String teamName;
	private List<SectionInfo> sections;

	public TeamInfo(String teamName) {
		this.teamName = teamName;
		this.sections = new ArrayList<>();
	}

}
