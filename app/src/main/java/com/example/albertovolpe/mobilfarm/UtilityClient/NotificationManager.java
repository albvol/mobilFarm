package com.example.albertovolpe.mobilfarm.UtilityClient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by albertovolpe on 24/01/17.
 */

public final class NotificationManager {

    private final static String TAG = "NotificationManager";

    public static void setFarmaciInScadenza(ArrayList<JSONObject> list, Context context, String date) throws JSONException {

        Log.i(TAG, date);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationMessage.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date d;
        String text = "";
        int id;

        try {

            d = format.parse(date+" 09:00");
            for(int f=0;f<list.size();f++) text+= list.get(f).getString("Nome")+" ("+list.get(f).getString("Composizione")+"mg), \n";

            intent.putExtra("Title", "Scadenze odierne");
            intent.putExtra("Message", text.substring(0, text.length()-1));

            id = Integer.parseInt(date.replace("/", ""));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
            am.set(AlarmManager.RTC_WAKEUP, d.getTime(), pendingIntent);

        } catch (ParseException e) {
            Log.i(TAG,e.getMessage());
        }
    }

    public static void setFarmaciDaAssumere(ArrayList<JSONObject> list, Context context){

        //Oggi
        Calendar oggi = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date dataTermine, dataInizioOraInizio;
        Boolean ripeti;

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent;
        String nomePiano, nomeFarmacoComposizione, IDTerapia, IDFarmaco, quantita, giornoCalcolato, giornoStringa;
        int id, index = -1, intervallo;

        try
        {
            //Per ogni farmaco da assumere (Prescrizioni)
            for(JSONObject farmacoDaAssumere: list)
            {
                //Se è stato impostato
                if((farmacoDaAssumere.getString("DataTermine") != "null") &&
                        (farmacoDaAssumere.getString("DataInizio") != "null") &&
                        (farmacoDaAssumere.getString("OraInizio") != "null") &&
                        (farmacoDaAssumere.getString("DataTermine") != null) &&
                        (farmacoDaAssumere.getString("DataInizio") != null) &&
                        (farmacoDaAssumere.getString("OraInizio") != null) &&
                        (farmacoDaAssumere.getString("DataTermine") != "") &&
                        (farmacoDaAssumere.getString("DataInizio") != "") &&
                        (farmacoDaAssumere.getString("OraInizio") != "")&&
                        !(farmacoDaAssumere.getString("DataTermine").isEmpty()) &&
                        !(farmacoDaAssumere.getString("DataInizio").isEmpty()) &&
                        !(farmacoDaAssumere.getString("OraInizio").isEmpty()))
                {
                    Log.i(TAG, "LEGGO IL FARMACO DA ASSUMERE:");
                    Log.i(TAG, farmacoDaAssumere.toString());

                    //Salvo la data termine
                    dataTermine = format.parse(farmacoDaAssumere.getString("DataTermine")+" 23:59");

                    //Se la durata della prescrizione non è ancora terminata
                    if(dataTermine.after(oggi.getTime()))
                    {
                        Log.i(TAG, "DataTermine > oggi "+ dataTermine.toString() + ">" + oggi.getTime());

                        nomePiano = farmacoDaAssumere.getString("NomePiano");
                        nomeFarmacoComposizione = farmacoDaAssumere.getString("Nome") + " (" + farmacoDaAssumere.getString("Composizione") + ")";
                        IDTerapia = farmacoDaAssumere.getString("IDTerapia");
                        IDFarmaco = farmacoDaAssumere.getString("IDFarmaco");
                        quantita = farmacoDaAssumere.getString("Dosaggio");
                        intervallo = farmacoDaAssumere.getInt("Intervallo");

                        //Data inizio e ora inizio
                        Log.i(TAG, "ALTTTT"+farmacoDaAssumere.getString("DataInizio") + " " + farmacoDaAssumere.getString("OraInizio"));
                        dataInizioOraInizio = format.parse(farmacoDaAssumere.getString("DataInizio") + " " + farmacoDaAssumere.getString("OraInizio"));
                        ripeti = true;

                        Log.i(TAG, "Data e ora inizio: "+dataInizioOraInizio.toString());

                        //Verifico se devo mostrare la prima notifica
                        if (dataInizioOraInizio.before(oggi.getTime())) {
                            Log.i(TAG, "Mostro la prima notifica perchè "+ dataInizioOraInizio.toString() + " < " + oggi.getTime());

                            index++;
                            intent = new Intent(context, NotificationMessage.class);
                            intent.putExtra("Title", nomePiano);
                            intent.putExtra("Message", "E' il momento di assumere " + quantita + " dose di " + nomeFarmacoComposizione);

                            id = Integer.parseInt(IDTerapia + IDFarmaco + index);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
                            am.set(AlarmManager.RTC_WAKEUP, dataInizioOraInizio.getTime(), pendingIntent);
                        }else Log.i(TAG, "NON mostro la prima notifica perchè "+ dataInizioOraInizio.toString() + " > " + oggi.getTime());

                        //Finche non termina la programmazione delle notifiche per questo farmaco
                        while (ripeti)
                        {
                            dataInizioOraInizio.setTime(dataInizioOraInizio.getTime() + (intervallo * 3600000));

                            Log.i(TAG, "Assunzione successiva tra "+intervallo+" ora");
                            Log.i(TAG, "Data assunzione successiva "+dataInizioOraInizio.toString());

                            if (dataInizioOraInizio.before(dataTermine)) {

                                Log.i(TAG, "DataAssunzione calcolata > Data Termine "+dataInizioOraInizio.toString() + " < " + dataTermine);

                                giornoCalcolato = sdf.format(dataInizioOraInizio);

                                /*Se nel determinato di giorno si deve assumere il farmaco
                                  mostro la notifica */
                                giornoStringa = Character.toString(giornoCalcolato.charAt(0)).toUpperCase() + giornoCalcolato.substring(1).replace("ì", "i");


                                if ("1".equals(farmacoDaAssumere.getString(giornoStringa))) {

                                    Log.i(TAG, "Nel giorno"+ giornoCalcolato +" si può assumere questo medicinale! Imposto la notifica");

                                    index++;
                                    intent = new Intent(context, NotificationMessage.class);
                                    intent.putExtra("Title", nomePiano);
                                    intent.putExtra("Message", "E' il momento di assumere " + quantita + " dose di " + nomeFarmacoComposizione);

                                    id = Integer.parseInt(IDTerapia + IDFarmaco + index);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
                                    am.set(AlarmManager.RTC_WAKEUP, dataInizioOraInizio.getTime(), pendingIntent);
                                }else Log.i(TAG, "Nel giorno"+ giornoCalcolato +" NON si può assumere questo medicinale! NESSUNA NOTIFICA INSERITA");

                            } else{
                                Log.i(TAG, "DataAssunzione calcolata > Data Termine "+dataInizioOraInizio.toString() + " > " + dataTermine);
                                ripeti = false;
                            }
                        }
                    }else Log.i(TAG, "FINE: DataTermine < oggi "+ dataTermine.toString() + "<" + oggi.getTime());
                }else Log.i(TAG, "FINE: Farmaco ancora non presente");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
