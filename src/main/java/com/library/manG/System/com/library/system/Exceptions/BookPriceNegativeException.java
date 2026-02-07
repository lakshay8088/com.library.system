package com.library.manG.System.com.library.system.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class BookPriceNegativeException extends Exception{
    public BookPriceNegativeException(String message){
        super(message);
    }
}




//This is negative price exception class

