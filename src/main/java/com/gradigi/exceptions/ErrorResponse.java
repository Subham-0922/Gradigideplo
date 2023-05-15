package com.gradigi.exceptions;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

	private String message;
	private LocalDateTime timestamp;
	private String exception;
	private Integer status;
	private String error;
	private String path;
}
