package com.library.manG.System.com.library.system.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//Book not found exception
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends Exception{
    public BookNotFoundException(String message){
        super(message);
    }
}
