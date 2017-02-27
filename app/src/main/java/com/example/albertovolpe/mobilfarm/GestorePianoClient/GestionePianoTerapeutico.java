package com.example.albertovolpe.mobilfarm.GestorePianoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by albertovolpe on 07/01/17.
 */

public final class GestionePianoTerapeutico {

    private static SharedPreferences sharedPreferences;
    private static Pattern letters_numbers, letters, numbers;

    public GestionePianoTerapeutico(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        letters_numbers = Pattern.compile("^[a-z 0-9]+$", Pattern.CASE_INSENSITIVE);
        letters = Pattern.compile("^[a-z ]+$", Pattern.CASE_INSENSITIVE);
        numbers = Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE);
    }


    /**
     * Preparo la richiesta relativa alla ricezione dell'elenco dei
     * piani terapeutici personali
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject visualizzaPianiTerapeutici()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaPianiTerapeutici");

        return dataToSend;
    }


    /**
     * Preparo la richiesta relativa alla ricezione dell'elenco delle assunzioni
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    public static JSONObject elencoAssunzioni()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "elencoAssunzioni");

        return dataToSend;
    }


    /**
     * Preparo la richiesta relativa alla ricezione dell'elenco dei
     * piani terapeutici del paziente
     * @param pazienteID l'identificativo del paziente
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    public static JSONObject visualizzaPianiTerapeuticiDiUnPaziente(String pazienteID)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(pazienteID.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("PazienteID", pazienteID);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaPianiTerapeuticiDiUnPaziente");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa alla ricezione dell'elenco
     * dei farmaci presenti in un piano terapeutico
     * @param IDTerapia l'identificativo del piano terapeutico
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject visualizzaPianoTerapeutico(String IDTerapia)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(IDTerapia.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente") && !userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDTerapia", IDTerapia);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaPianoTerapeutico");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa alla ricezione dell'elenco
     * dei farmaci presenti in un piano terapeutico
     * @param IDTerapia l'identificativo del piano terapeutico
     * @param IDFarmaco l'identificativo del farmaco
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject visualizzaDettagliAssunzioneFarmaco(String IDTerapia, String IDFarmaco)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if((IDTerapia.isEmpty()) || (IDFarmaco.isEmpty())) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente") && !userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDTerapia", IDTerapia);
        data.put("IDFarmaco", IDFarmaco);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "visualizzaDettagliAssunzioneFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa all'eliminazione di un
     * farmaco dal un determinato piano terapeutico
     * @param IDTerapia l'identificativo del piano terapeutico
     * @param IDFarmaco l'identificativo del farmaco da eliminare dalla terapia
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject rimuoviFarmaco(String IDTerapia, String IDFarmaco)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(IDTerapia.isEmpty() || IDFarmaco.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDTerapia", IDTerapia);
        data.put("IDFarmaco", IDFarmaco);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "rimuoviFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa all'inclusione di un
     * farmaco in un determinato piano terapeutico
     * @param IDTerapia l'identificativo del piano terapeutico
     * @param IDFarmaco l'identificativo del farmaco da aggiungere alla terapia
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject aggiungiFarmaco(String IDTerapia, String IDFarmaco)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(IDTerapia.isEmpty() || IDFarmaco.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDTerapia", IDTerapia);
        data.put("IDFarmaco", IDFarmaco);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "aggiungiFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa alla creazione di un
     * nuovo piano terapeutico
     * @param nomePiano il nome del piano terapeutico
     * @param inviatoA l'identificativo del paziente a cui è rivolto il piano
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject creaPianoTerapeutico(String nomePiano, String inviatoA)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(nomePiano.isEmpty()) throw new ErrorValuesException("Inserire il nome della terapia!");
        if(!(letters_numbers.matcher(nomePiano)).find()) throw new ErrorValuesException("Il nome della terapia non può contenere caratteri speciali!");

        if(inviatoA.isEmpty()) throw new ErrorValuesException("Selezionare un paziente!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("NomePiano", nomePiano);
        data.put("InviatoA", inviatoA);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "creaPianoTerapeutico");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta relativa all'aggiornamento dei dettagli di assunzione relativi ad un
     * farmaco in un determinato piano terapeutico
     * @param IDTerapia l'identificativo della terapia relativa all'assunzione del farmaco
     * @param IDFarmaco l'identificativo del farmaco relativo ai dettagli di assunzione
     * @param dosaggio il dosaggio da assumere
     * @param dataInizio la data di inizio di assunzione del farmaco
     * @param dataTermine la data di termine di assunzione del farmaco
     * @param oraInizio l'ora di inizio di assunzione del farmaco
     * @param intervallo l'intervallo di assunzione del farmaco
     * @param lunedi valore booleano che indica se il lunedì si deve assumere il farmaco
     * @param martedi valore booleano che indica se il martedì si deve assumere il farmaco
     * @param mercoleldi valore booleano che indica se il mercoleldì si deve assumere il farmaco
     * @param giovedi valore booleano che indica se il giovedì si deve assumere il farmaco
     * @param venerdi valore booleano che indica se il venerdì si deve assumere il farmaco
     * @param sabato valore booleano che indica se il sabato si deve assumere il farmaco
     * @param domenica valore booleano che indica se il domenica si deve assumere il farmaco
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject aggiornaDettagliAssunzioneFarmaco(String IDTerapia, String IDFarmaco, String dosaggio, String dataInizio, String dataTermine, String oraInizio, String intervallo, Boolean lunedi, Boolean martedi, Boolean mercoleldi, Boolean giovedi, Boolean venerdi, Boolean sabato, Boolean domenica)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(dataInizio.isEmpty()) throw new ErrorValuesException("Selezionare la data di inizio!");
        if(dataTermine.isEmpty()) throw new ErrorValuesException("Selezionare la data di termine!");
        if(oraInizio.isEmpty()) throw new ErrorValuesException("Selezionare l'ora di inizio!");

        if(intervallo.equals("Intervallo")) throw new ErrorValuesException("Selezionare l’intervallo di assunzione!");
        if(dosaggio.equals("Dosaggio")) throw new ErrorValuesException("Selezionare la dose da assumere!");

        if(IDTerapia.isEmpty() || IDFarmaco.isEmpty() || dosaggio.isEmpty()
                || dataInizio == null || dataTermine == null || oraInizio == null || intervallo == null
                || lunedi == null || martedi == null || mercoleldi == null || giovedi == null
                || venerdi == null || sabato == null || domenica == null ) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IDTerapia", IDTerapia);
        data.put("IDFarmaco", IDFarmaco);
        data.put("Dosaggio", dosaggio);
        data.put("DataInizio", dataInizio);
        data.put("DataTermine", dataTermine);
        data.put("OraInizio", oraInizio);
        data.put("Intervallo", intervallo);
        data.put("Lunedi", lunedi);
        data.put("Martedi", martedi);
        data.put("Mercoleldi", mercoleldi);
        data.put("Giovedi", giovedi);
        data.put("Venerdi", venerdi);
        data.put("Sabato", sabato);
        data.put("Domenica", domenica);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "aggiornaDettagliAssunzioneFarmaco");
        dataToSend.put("data", data);

        return dataToSend;
    }
}
