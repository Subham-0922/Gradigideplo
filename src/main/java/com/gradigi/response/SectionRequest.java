package com.gradigi.response;

import com.gradigi.model.Section;
import lombok.Data;

@Data
public class SectionRequest {

	private Section section;
	private Integer reveiwerId;
}
