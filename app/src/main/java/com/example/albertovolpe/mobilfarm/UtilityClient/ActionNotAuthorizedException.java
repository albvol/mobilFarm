package com.example.albertovolpe.mobilfarm.UtilityClient;

/**
 * Created by albertovolpe on 13/01/17.
 */

public class ActionNotAuthorizedException extends Exception {

    @Override
    public String getMessage(){
        return "Azione non autorizzata!";
    }
}
