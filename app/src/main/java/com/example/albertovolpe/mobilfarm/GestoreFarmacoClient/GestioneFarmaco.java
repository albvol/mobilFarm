package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by albertovolpe on 07/01/17.
 */

public final class GestioneFarmaco{

    private static SharedPreferences sharedPreferences;
    private static Pattern letters_numbers, letters, numbers;

    public GestioneFarmaco(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);


        letters_numbers = Pattern.compile("^[a-z0-9]+$", Pattern.CASE_INSENSITIVE);
        letters = Pattern.compile("^[a-z]+$", Pattern.CASE_INSENSITIVE);
        numbers = Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Preparo la richiesta relativa alla creazione di un nuovo farmaco
     * da parte del Dottore
     * @param nome il nome del Farmaco
     * @param composizione la relativa composizione
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject creaFarmaco(String nome, String composizione)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(nome.isEmpty()) throw new ErrorValuesException("Inserire il nome del Farmaco!");
        if(!(letters.matcher(nome)).find()) throw new ErrorValuesException("Il nome non può contenere valori numerici!");

        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Nome", nome);
        data.put("Composizione", composizione);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "creaFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }


    /**
     * Preparo la richiesta relativa alla ricerca di un farmaco
     * nel database pubblico
     * @param keyword la chiave di ricerca
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    public static JSONObject ricercaFarmaco(String keyword)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(keyword.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore") && !userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Keyword", keyword);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "ricercaFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }

    public static JSONObject esistenzaFarmaco(String nome, String composizione)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(nome.isEmpty() || composizione.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Nome", nome);
        data.put("Composizione", composizione);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "esistenzaFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }
}
