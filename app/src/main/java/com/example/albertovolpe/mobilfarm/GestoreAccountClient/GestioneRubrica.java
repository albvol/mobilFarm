package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by albertovolpe on 12/01/17.
 */

public final class GestioneRubrica {

    private static SharedPreferences sharedPreferences;

    GestioneRubrica(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Preparo la richiesta relativa alla visualizzazione della rubrica di un dottore
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    public static JSONObject visualizzaRubricaPazienti()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaRubricaPazienti");

        return dataToSend;
    }

    /**
     * Preparo la richiesta di informazioni relative a un paziente
     * @param pazienteID l'id del paziente
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject visualizzaProfiloPazienteSelezionato(String pazienteID)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(pazienteID.isEmpty()) throw new ErrorValuesException("PazienteID assente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("PazienteID", pazienteID);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaProfiloPazienteSelezionato");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di eliminazione di un paziente dalla rubrica personale
     * @param pazienteID l'id del paziente
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject rimuoviPaziente(String pazienteID)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(pazienteID.isEmpty()) throw new ErrorValuesException("PazienteID assente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("PazienteID", pazienteID);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "rimuoviPaziente");
        dataToSend.put("data", data);

        return dataToSend;
    }


    /**
     * Preparo la richiesta relativa alla visualizzazione della rubrica di un paziente
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject visualizzaRubricaDottori()
            throws JSONException, ActionNotAuthorizedException{

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaRubricaDottori");

        return dataToSend;
    }

    /**
     * Preparo la richiesta di informazioni relative a un dottore
     * @param dottoreID l'id del dottore
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject visualizzaProfiloDottoreSelezionato(String dottoreID)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(dottoreID.isEmpty()) throw new ErrorValuesException("DottoreID assente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("DottoreID", dottoreID);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaProfiloDottoreSelezionato");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di eliminazione di un dottore dalla rubrica personale
     * @param dottoreID l'id del dottore
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject rimuoviDottore(String dottoreID)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(dottoreID.isEmpty()) throw new ErrorValuesException("DottoreID assente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("DottoreID", dottoreID);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "rimuoviDottore");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di ricerca dei dottori nel database pubblico
     * @param keyword la chiave di ricerca
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject ricercaDottore(String keyword)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(keyword.isEmpty()) throw new ErrorValuesException("Chiave assente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Keyword", keyword);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "ricercaDottore");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di aggiunta di un dottore nella propria rubrica
     * @param dottoreID l'id del dottore
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject aggiungiDottore(String dottoreID)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(dottoreID.isEmpty()) throw new ErrorValuesException("DottoreID assente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("DottoreID", dottoreID);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "aggiungiDottore");
        dataToSend.put("data", data);

        return dataToSend;
    }
}
