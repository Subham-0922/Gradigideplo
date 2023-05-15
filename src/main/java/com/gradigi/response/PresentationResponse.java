package com.gradigi.response;

import java.util.List;
import com.gradigi.model.Presentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PresentationResponse {

	private List<Presentation> content;
	private Integer pageNumber;
	private Integer pageSize;
	private Integer totalPages;
	private Boolean lastPage;
}
