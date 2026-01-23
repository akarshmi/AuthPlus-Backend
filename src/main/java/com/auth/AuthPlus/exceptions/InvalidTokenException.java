package com.auth.AuthPlus.exceptions;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message){
        super(message);
    }

    public InvalidTokenException(){
        super("Invalid Token !!");
    }
}
