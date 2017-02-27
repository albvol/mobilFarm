package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by albertovolpe on 07/01/17.
 */

public final class GestioneAccount {

    private static SharedPreferences sharedPreferences;
    private static Pattern letters_numbers, letters, numbers;

    GestioneAccount(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        letters_numbers = Pattern.compile("^[a-z 0-9]+$", Pattern.CASE_INSENSITIVE);
        letters = Pattern.compile("^[a-z ]+$", Pattern.CASE_INSENSITIVE);
        numbers = Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Preparo la richiesta di login
     * @param mail la mail relativa all'account
     * @param password la password relativa all'account
     * @return pacchetto di dati da inviare al server
     * @throws NoSuchAlgorithmException errore di criptazione della password
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ErrorValuesException dati assenti
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject login(String mail, String password)
            throws NoSuchAlgorithmException, JSONException, ErrorValuesException, ActionNotAuthorizedException {

        //Pre-condition
        if(mail.length() < 6 || password.length() < 6) throw new ErrorValuesException("Mail o Password non corrette!");
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) throw new ErrorValuesException("Formato della Mail errato!");
        if(!(letters_numbers.matcher(password)).find()) throw new ErrorValuesException("Formato della password errato!");

        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        //if(userType.equals("Dottore") || userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Mail", mail);
        data.put("Password", md5(password));

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "login");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di ricezione dei numeri SOS
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject modalitaSOS()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "modalitaSOS");

        return dataToSend;
    }

    /**
     * Preparo la richiesta di logout
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject logout()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore") && !userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "logout");

        return dataToSend;
    }

    /**
     * Preparo la richiesta di verifica della mail
     * @param mail la mail con cui l'utente vuole registrarsi
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     */
    protected static JSONObject controlloCredenziali(String mail)
            throws JSONException, ActionNotAuthorizedException, ErrorValuesException {

        //Pre-condition
        if(mail.length() < 6) throw new ErrorValuesException("Il campo Mail deve contenere almeno 6 caratteri!");
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) throw new ErrorValuesException("Formato della Mail errato!");

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Mail", mail);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "controlloCredenziali");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di registrazione di un account di tipo paziente
     * @param mail la mail dell'utente
     * @param nome il nome dell'utente
     * @param cognome il cognome dell'utente
     * @param password la password dell'utente
     * @param sesso il sesso dell'utente
     * @param codiceFiscale il codice fiscale dell'utente
     * @param cellulare il cellulare dell'utente
     * @param dettagliClinici i dettagli clinici (allergie, intolleranze) dell'utente
     * @param dataNascita la data di nascita dell'utente
     * @param luogoNascita il luogo di nascita dell'utente
     * @param residenza la via di residenza dell'utente
     * @param cittaResidenza la città di residenza dell'utente
     * @return il pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     * @throws ErrorValuesException dati assenti
     * @throws NoSuchAlgorithmException errore di criptazione della password
     */
    protected static JSONObject registraAccountPaziente(Boolean legal, String mail, String nome, String cognome, String password, String sesso, String codiceFiscale, String cellulare, String dettagliClinici, String dataNascita, String luogoNascita, String residenza, String cittaResidenza)
            throws ErrorValuesException, ActionNotAuthorizedException, JSONException, NoSuchAlgorithmException {


        //Pre-condition
        if(nome.length() < 1) throw new ErrorValuesException("Inserire il nome!");
        if(!((letters.matcher(nome)).find())) throw new ErrorValuesException("Il nome deve contenere solo lettere!");

        if(cognome.length() < 1) throw new ErrorValuesException("Inserire il cognome!");
        if(!(letters.matcher(cognome)).find()) throw new ErrorValuesException("Il cognome deve contenere solo lettere!");

        if(sesso.length() < 1) throw new ErrorValuesException("Seleziona il sesso!");
        if(sesso != "M" && sesso != "F") throw new ErrorValuesException("Seleziona il sesso!");

        if(codiceFiscale.length() < 1 && codiceFiscale.length() != 16) throw new ErrorValuesException("Inserire il codice fiscale!");
        if(!(letters_numbers.matcher(codiceFiscale)).find())  throw new ErrorValuesException("Codice fiscale non corretto!");

        if(dataNascita.length() < 1) throw new ErrorValuesException("Inserire la data di nascita!");

        if(luogoNascita.length() < 1) throw new ErrorValuesException("Inserisci il luogo di nascita!");
        if((numbers.matcher(luogoNascita)).find()) throw new ErrorValuesException("Il luogo di nascita di non può contenere dei numeri!");

        if(cittaResidenza.length() < 1) throw new ErrorValuesException("Inserisci il comune di residenza!");
        if((numbers.matcher(cittaResidenza)).find()) throw new ErrorValuesException("Il comune di residenza non può contenere dei numeri!");

        if(residenza.length() < 1) throw new ErrorValuesException("Inserisci la residenza!");

        if(cellulare.length() != 10) throw new ErrorValuesException("Inserire il cellulare!");
        if(!(numbers.matcher(cellulare)).find()) throw new ErrorValuesException("Il cellulare deve contenere solo dei numeri!");

        if(!legal) throw new ErrorValuesException("Per procedere devi dichiarare di aver preso visione della normativa sulla privacy!");

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Mail", mail);
        data.put("Password", md5(password));
        data.put("Tipologia", "Paziente");
        data.put("DettagliClinici", dettagliClinici);
        data.put("Cellulare", cellulare);
        data.put("Nome", nome);
        data.put("Cognome", cognome);
        data.put("CodiceFiscale", codiceFiscale);
        data.put("DataNascita", dataNascita);
        data.put("LuogoNascita", luogoNascita);
        data.put("Residenza", residenza);
        data.put("CittaResidenza", cittaResidenza);
        data.put("Sesso", sesso);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "registraAccountPaziente");
        dataToSend.put("data", data);

        return dataToSend;
    }

    ///AGGIUGNERE  I COMMENTI
    protected static JSONObject registraAccountDottore(Boolean legal, String mail, String nome, String cognome, String password, String sesso, String indirizzoStudio, String telefonoStudio, String IDAlbo, String citta, String provincia, String specializzazione)
            throws ErrorValuesException, ActionNotAuthorizedException, JSONException, NoSuchAlgorithmException {

        //Pre-condition
        if(nome.length() < 1) throw new ErrorValuesException("Inserire il nome!");
        if(!((letters.matcher(nome)).find())) throw new ErrorValuesException("Il nome deve contenere solo lettere!");

        if(cognome.length() < 1) throw new ErrorValuesException("Inserire il cognome!");
        if(!(letters.matcher(cognome)).find()) throw new ErrorValuesException("Il cognome deve contenere solo lettere!");

        if(sesso.length() < 1) throw new ErrorValuesException("Seleziona il sesso!");
        if(sesso != "M" && sesso != "F") throw new ErrorValuesException("Seleziona il sesso!");

        if(indirizzoStudio.length() < 1) throw new ErrorValuesException("Inserire l'indirizzo dello studio!");
        if(!(letters_numbers.matcher(indirizzoStudio)).find()) throw new ErrorValuesException("L'indirizzo non può contenere caratteri speciali!");

        if((provincia.length() != 2) || !(letters.matcher(provincia)).find()) throw new ErrorValuesException("Provincia assente, inserisci la città dello studio!");

        if(citta.length() < 1) throw new ErrorValuesException("Inserisci la citta dello studio!");
        if((numbers.matcher(citta)).find()) throw new ErrorValuesException("La città non può contenere dei numeri!");

        if(telefonoStudio.length() != 9 && telefonoStudio.length() != 10) throw new ErrorValuesException("Inserire il telefono dello studio!");
        if(!(numbers.matcher(telefonoStudio)).find()) throw new ErrorValuesException("Il telefono deve contenere solo dei numeri!");

        if(IDAlbo.length() != 8) throw new ErrorValuesException("Identificativo dell'Albo non corretto!");
        if(!(letters_numbers.matcher(IDAlbo)).find()) throw new ErrorValuesException("L'ID Albo non può contenere caratteri speciali!");

        if(specializzazione.length() < 1) throw new ErrorValuesException("Inserire la specializzazione!");
        if(!(letters.matcher(specializzazione)).find()) throw new ErrorValuesException("La specializzazione può contenere solo lettere!");

        if(!legal) throw new ErrorValuesException("Per procedere devi dichiarare di aver preso visione della normativa sulla privacy!");


        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Mail", mail);
        data.put("Password", md5(password));
        data.put("Nome", nome);
        data.put("Cognome", cognome);
        data.put("Tipologia", "Dottore");
        data.put("Sesso", sesso);

        data.put("IDAlbo", IDAlbo);
        data.put("Citta", citta);
        data.put("Provincia", provincia);
        data.put("Specializzazione", specializzazione);
        data.put("IndirizzoStudio", indirizzoStudio);
        data.put("TelefonoStudio", telefonoStudio);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "registraAccountDottore");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di ricezione dei dati relativi al profilo personale del dottore
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject profiloPersonaleDottore()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "profiloPersonaleDottore");

        return dataToSend;
    }

    /**
     * Preparo la richiesta di ricezione dei dati relativi al profilo personale del paziente
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject profiloPersonalePaziente()
            throws JSONException, ActionNotAuthorizedException {

        //Pre-condition
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "profiloPersonalePaziente");

        return dataToSend;
    }

    /**
     * Preparo la richiesta di aggiornamento dei dati relativi al profilo personale del paziente
     * @param cellulare il numero di cellulare dell'utente
     * @param dettagliClinici i dettagli clinici del paziente
     * @param numeroDiEmergenza1 il numero di emergenza
     * @param numeroDiEmergenza2 il secondo numero di emergenza
     * @param residenza la via di residenza del paziente
     * @param cittaResidenza la città di residenza del paziente
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ErrorValuesException dati assenti
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject aggiornaProfiloPaziente(String cellulare, String dettagliClinici, String numeroDiEmergenza1, String numeroDiEmergenza2, String residenza, String cittaResidenza)
            throws JSONException, ErrorValuesException, ActionNotAuthorizedException {

        //Pre-condition
        if((numeroDiEmergenza1.length() != 10) && (numeroDiEmergenza1 != "")) throw new ErrorValuesException("Numero di emergenza non corretto!");
        if((numeroDiEmergenza2.length() != 10) && (numeroDiEmergenza2 != "")) throw new ErrorValuesException("Numero di emergenza non corretto!");

        if(cittaResidenza.isEmpty()) throw new ErrorValuesException("Inserire la città di residenza!");
        if(numbers.matcher(cittaResidenza).find()) throw new ErrorValuesException("La città non può contenere caratteri speciali!");

        if(residenza.isEmpty()) throw new ErrorValuesException("Inserisci la residenza!");

        if(cellulare.length() != 10) throw new ErrorValuesException("Il cellulare inserito non è corretto!");

        if(cellulare.isEmpty() || dettagliClinici.isEmpty() || residenza.isEmpty() || cittaResidenza.isEmpty()) throw new ErrorValuesException("Informazioni incomplete");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Paziente")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("Cellulare", cellulare);
        data.put("DettagliClinici", dettagliClinici);
        data.put("NumeroSOS1", numeroDiEmergenza1);
        data.put("NumeroSOS2", numeroDiEmergenza2);
        data.put("Residenza", residenza);
        data.put("CittaResidenza", cittaResidenza);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "aggiornaProfiloPaziente");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Preparo la richiesta di aggiornamento dei dati relativi al profilo personale del dottore
     * @param indirizzoStudio l'indirizzo dello studio del dottore
     * @param provincia la provincia in cui si trova lo studio del dottore
     * @param citta la citta in cui si trova lo studio del dottore
     * @param telefonoStudio il numero di telefono dello studio del dottore
     * @param specializzazioneImpiego la specializzazione del dottore
     * @return pacchetto di dati da inviare al server
     * @throws JSONException errore durante la fase di composizione del pacchetto da inviare al server
     * @throws ErrorValuesException dati assenti
     * @throws ActionNotAuthorizedException l'utente non è autorizzato a compiere questa azione
     */
    protected static JSONObject aggiornaProfiloDottore(String indirizzoStudio, String provincia, String citta, String telefonoStudio, String specializzazioneImpiego)
            throws JSONException, ErrorValuesException, ActionNotAuthorizedException {

        //Pre-condition
        if(indirizzoStudio.isEmpty()) throw new ErrorValuesException("Inserire l'indirizzo!");
        if(!(letters_numbers.matcher(indirizzoStudio)).find()) throw new ErrorValuesException("L’indirizzo non può contenere caratteri speciali!");

        if(provincia.isEmpty()) throw new ErrorValuesException("Inserire le iniziali della provincia!");
        if(!(letters.matcher(provincia)).find()) throw new ErrorValuesException("La provincia può contenere solo lettere!");

        if(citta.isEmpty()) throw new ErrorValuesException("Inserire la città!");
        if(!(letters.matcher(citta)).find()) throw new ErrorValuesException("La città può contenere solo lettere!");

        if(telefonoStudio.isEmpty() || telefonoStudio.length() != 9) throw new ErrorValuesException("Numero non corretto!");

        if(specializzazioneImpiego.isEmpty()) throw new ErrorValuesException("Inserire la specializzazione!");
        if(!(letters.matcher(citta)).find()) throw new ErrorValuesException("La specializzazione non può contenere caratteri speciali o numeri!");

        if(indirizzoStudio.isEmpty() || provincia.isEmpty() || citta.isEmpty() || telefonoStudio.isEmpty() || specializzazioneImpiego.isEmpty()) throw new ErrorValuesException("Informazioni incomplete!");
        String userType = sharedPreferences.getString("Type", "Utente non identificato");
        if(!userType.equals("Dottore")) throw new ActionNotAuthorizedException();

        //Preparo la richiesta
        JSONObject data = new JSONObject();
        data.put("IndirizzoStudio", indirizzoStudio);
        data.put("Provincia", provincia);
        data.put("Citta", citta);
        data.put("TelefonoStudio", telefonoStudio);
        data.put("Specializzazione", specializzazioneImpiego);

        JSONObject dataToSend = new JSONObject();
        dataToSend.put("action", "aggiornaProfiloDottore");
        dataToSend.put("data", data);

        return dataToSend;
    }

    /**
     * Algoritmo md5
     * @param word il termine da criptare
     * @return stringa criptata
     * @throws NoSuchAlgorithmException
     */
    private static String md5(String word) throws NoSuchAlgorithmException {

        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(word.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

        return hexString.toString();
    }


}

