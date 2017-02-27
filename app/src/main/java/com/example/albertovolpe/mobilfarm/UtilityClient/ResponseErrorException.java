package com.example.albertovolpe.mobilfarm.UtilityClient;

/**
 * Created by albertovolpe on 13/01/17.
 */

public class ResponseErrorException extends Exception {

    private String message;

    public ResponseErrorException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
