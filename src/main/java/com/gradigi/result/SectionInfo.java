package com.gradigi.result;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionInfo {
	private String sectionName;
	private List<AttributeInfo> attributes;

	public SectionInfo(String sectionName) {
		this.sectionName = sectionName;
		this.attributes = new ArrayList<>();
	}

}
