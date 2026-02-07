package com.library.manG.System.com.library.system.Exceptions;

import com.library.manG.System.com.library.system.DTO.RESPONSE;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(BookPriceNegativeException.class)
    public ResponseEntity<RESPONSE> BookPriceException(){
        RESPONSE res = new RESPONSE("EXCEPTION OCCURED","");
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<RESPONSE> notFoundException(){
        RESPONSE res = new RESPONSE("EXCEPTION not found has come","ERROR");
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }
}
