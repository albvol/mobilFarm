package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * Created by albertovolpe on 14/01/17.
 */

public final class GestioneVMF {

    private static SharedPreferences sharedPreferences;

    public GestioneVMF(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Preparo la richiesta relativa alla ricezione dell'elenco dei
     * farmaci presenti nel proprio virtualmobilFram
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject visualizzaVMF()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore") && !userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaVMF");

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa all'inserimento di un
     * farmaco nel proprio virtualmobilFram
     * @param IDFarmaco l'identificativo del farmaco da aggiungere al VMF
     * @param scadenza la data di scadenza del farmaco
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject aggiungiFarmacoVMF(String IDFarmaco, String scadenza)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(IDFarmaco.isEmpty() || scadenza == null) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore") && !userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDFarmaco", IDFarmaco);
        data.put("Scadenza", scadenza);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "aggiungiFarmacoVMF");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di ricezione delle scadenze prossime
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    public static JSONObject elencoFarmaciScaduti()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente") && !userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        JSONObject data = new JSONObject();
        data.put("Anno", year);
        data.put("Mese", (month+1));
        data.put("Giorno", day);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "elencoFarmaciScaduti");
        dataToSend.put("data", data);


        return dataToSend;
    }

    /**
     * Preparo la richiesta di ricezione delle scadenze prossime
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    public static JSONObject elencoFarmaciInScadenza()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente") && !userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        JSONObject data = new JSONObject();
        data.put("Anno", year);
        data.put("Mese", (month+1));
        data.put("Giorno", day);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "elencoFarmaciInScadenza");
        dataToSend.put("data", data);


        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa all'eliminazione di un
     * farmaco dal proprio virtualmobilFram
     * @param IDFarmaco l'identificativo del farmaco da eliminare
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject eliminaFarmacoVMF(String IDFarmaco)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(IDFarmaco.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore") && !userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDFarmaco", IDFarmaco);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "eliminaFarmacoVMF");
        dataToSend.put("data", data);

        return dataToSend;
    }
}
