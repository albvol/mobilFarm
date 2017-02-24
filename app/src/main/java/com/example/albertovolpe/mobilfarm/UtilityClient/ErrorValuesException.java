package com.example.albertovolpe.mobilfarm.UtilityClient;

/**
 * Created by albertovolpe on 13/01/17.
 */

public class ErrorValuesException extends Exception {

    private String message;

    public ErrorValuesException(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
