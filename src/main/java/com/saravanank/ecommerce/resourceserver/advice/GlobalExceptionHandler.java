package com.saravanank.ecommerce.resourceserver.advice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.saravanank.ecommerce.resourceserver.exceptions.BadRequestException;
import com.saravanank.ecommerce.resourceserver.exceptions.NotFoundException;
import com.saravanank.ecommerce.resourceserver.model.RestResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<RestResponse> dataNotFoundException(NotFoundException notFoundException) {
		RestResponse errResponse = new RestResponse();
		logger.error(notFoundException.getErrorMessage());
		errResponse.setError("Not found");
		errResponse.setMessage(notFoundException.getErrorMessage());
		errResponse.setStatus(notFoundException.getErrorCode().value());
		errResponse.setTimestamp(new Date().toString());
		return new ResponseEntity<RestResponse>(errResponse, notFoundException.getErrorCode());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<RestResponse> badRequestException(BadRequestException badRequesyExp) {
		RestResponse errResponse = new RestResponse();
		logger.error(badRequesyExp.getErrorMessage());
		errResponse.setError("Not found");
		errResponse.setMessage(badRequesyExp.getErrorMessage());
		errResponse.setStatus(badRequesyExp.getErrorCode().value());
		errResponse.setTimestamp(new Date().toString());
		return new ResponseEntity<RestResponse>(errResponse, badRequesyExp.getErrorCode());
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<RestResponse> constraintViolation(SQLIntegrityConstraintViolationException exp) {
		RestResponse errResponse = new RestResponse();
		logger.error(exp.getMessage());
		errResponse.setError(exp.getMessage());
		errResponse.setMessage("Cannot update or delete this data as it is used somewhere else");
		errResponse.setStatus(400);
		errResponse.setTimestamp(new Date().toString());
		return new ResponseEntity<RestResponse>(errResponse, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		RestResponse errResponse = new RestResponse();
		logger.error("Unsupported Http method to path " + request.getContextPath());
		errResponse.setError("Unsupported HTTP method");
		errResponse.setMessage("");
		errResponse.setStatus(status.value());
		errResponse.setTimestamp(new Date().toString());
		return new ResponseEntity<Object>(errResponse, status);
	}

}
