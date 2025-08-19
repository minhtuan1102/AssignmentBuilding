package com.javaweb.controllerAdvice;

import java.net.http.HttpHeaders;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.javaweb.model.ErrorRespondDTO;

import customexception.FieldRequiredException;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
	@ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            ArithmeticException ex, WebRequest request) {
		ErrorRespondDTO errorRespondDTO = new ErrorRespondDTO();
		errorRespondDTO.setError(ex.getMessage());
		List<String> details = new ArrayList<>();
		details.add("So nguyen lam sao chia duoc cho 0 the ?");
		errorRespondDTO.setDetail(details);
        return new ResponseEntity<>(errorRespondDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(FieldRequiredException.class)
    public ResponseEntity<Object> handleFieldRequiredException(
    		FieldRequiredException ex, WebRequest request) {
		ErrorRespondDTO errorRespondDTO = new ErrorRespondDTO();
		errorRespondDTO.setError(ex.getMessage());
		List<String> details = new ArrayList<>();
		details.add("Check lai name hoac numberofbasement di boi vi dang bi null do!");
		errorRespondDTO.setDetail(details);
        return new ResponseEntity<>(errorRespondDTO, HttpStatus.BAD_GATEWAY);
    }
}
