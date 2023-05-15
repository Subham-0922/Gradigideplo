package com.gradigi.response;

import com.gradigi.model.Presentation;
import lombok.Data;

@Data
public class PresentationCreate {

	private String status;
	private Presentation presentation;
}
