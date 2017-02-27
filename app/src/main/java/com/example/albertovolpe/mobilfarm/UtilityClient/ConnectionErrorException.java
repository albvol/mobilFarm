package com.example.albertovolpe.mobilfarm.UtilityClient;


import java.io.IOException;

/**
 * Created by albertovolpe on 13/01/17.
 */

public class ConnectionErrorException extends Exception {

    public String getMessage(){ return "Connessione assente!";}
}
