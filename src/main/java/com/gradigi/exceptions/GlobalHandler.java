package com.gradigi.exceptions;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalHandler {

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> noHandlerException(NoHandlerFoundException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> argumentException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> messageException(DataIntegrityViolationException ex,
			HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> nullPointerException(NullPointerException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ErrorResponse> messageException(MessagingException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorResponse> userException(UserException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResultException.class)
	public ResponseEntity<ErrorResponse> resultException(ResultException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SectionException.class)
	public ResponseEntity<ErrorResponse> sectionException(SectionException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(TeamException.class)
	public ResponseEntity<ErrorResponse> teamException(TeamException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(PresentationException.class)
	public ResponseEntity<ErrorResponse> presentationException(PresentationException ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

		ErrorResponse errorResponse = provideErrorResponse(ex, request);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ErrorResponse provideErrorResponse(Exception ex, HttpServletRequest request) {

		String exception = ex.getClass().getSimpleName();

		String error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

		String path = request.getRequestURI();

		return new ErrorResponse(ex.getLocalizedMessage(), LocalDateTime.now(), exception, status, error, path);
	}
}
