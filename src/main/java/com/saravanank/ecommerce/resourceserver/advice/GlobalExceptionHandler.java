package com.saravanank.ecommerce.resourceserver.advice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<RestResponse> clientHttpErr(HttpClientErrorException clientExp) {
		RestResponse errResponse = new RestResponse();
		logger.error(clientExp.getMessage());
		errResponse.setError(clientExp.getMessage());
		errResponse.setMessage("Error occured while getting data from other clients");
		errResponse.setStatus(clientExp.getStatusCode().value());
		errResponse.setTimestamp(new Date().toString());
		return new ResponseEntity<RestResponse>(errResponse, clientExp.getStatusCode());
	}

	@ExceptionHandler({ JsonMappingException.class, JsonProcessingException.class })
	public ResponseEntity<RestResponse> jsonMappingException(JsonMappingException mapExp,
			JsonProcessingException proccessingExp) {
		RestResponse errResponse = new RestResponse();
		if (mapExp != null) {
			logger.error(mapExp.getMessage());
			errResponse.setError(mapExp.getMessage());
			errResponse.setMessage("Couldn't map JSON");
			errResponse.setStatus(500);
			errResponse.setTimestamp(new Date().toString());
		} else {
			logger.error(proccessingExp.getMessage());
			errResponse.setError(proccessingExp.getMessage());
			errResponse.setMessage("Couldn't process JSON");
			errResponse.setStatus(500);
			errResponse.setTimestamp(new Date().toString());
		}
		return new ResponseEntity<RestResponse>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		RestResponse errResponse = new RestResponse();
		Map<String, String> invalidFields = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(err -> {
			invalidFields.put(err.getField(), err.getDefaultMessage());
		});
		errResponse.setError("Invalid data");
		errResponse.setMessage(invalidFields);
		errResponse.setStatus(400);
		errResponse.setTimestamp(new Date().toString());
		return new ResponseEntity<Object>(errResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class) 
	public ResponseEntity<RestResponse> listOfConstraintViolation(ConstraintViolationException exp) {
		RestResponse errResponse = new RestResponse();
		Map<String, String> invalidFields = new HashMap<>();
		exp.getConstraintViolations().forEach(violation -> {
			invalidFields.put(violation.getPropertyPath().toString(), violation.getMessage());
		});
		errResponse.setError("One or more of data are invalid");
		errResponse.setMessage(invalidFields);
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
