package com.example.albertovolpe.mobilfarm.GestorePianoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.PazienteHome;
import com.example.albertovolpe.mobilfarm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FarmacoDaAssumere extends Fragment {

    private static final String TAG = "FarmacoDaAssumere";

    private View layout;
    private TextView text_hello, text_ora_assunzione ,text_nome_farmaco, text_nessuna_farmaco_da_assumere;

    private OnFragmentInteractionListener mListener;

    public FarmacoDaAssumere() {
        // Required empty public constructor
    }

    public static FarmacoDaAssumere newInstance() {
        FarmacoDaAssumere fragment = new FarmacoDaAssumere();
        return fragment;
    }

    public static  void mostraAssunzioniDellaGiornata(ArrayList<JSONObject> lista_assunzioni)
    {
        JSONObject jsonObject;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date oggiInizio, oggiTermine, dataTermine, dataInizioOraInizio;
        Boolean ripeti;
        ArrayList<JSONObject> lista_assunzioni_odierne = new ArrayList<>();

        String nomePiano, nomeFarmacoComposizione, quantita, giornoCalcolato, giornoStringa;
        int intervallo;

        oggiInizio = new Date();
        oggiInizio.setHours(0);
        oggiInizio.setMinutes(0);
        oggiInizio.setSeconds(0);

        oggiTermine = new Date();
        oggiTermine.setHours(23);
        oggiTermine.setMinutes(59);
        oggiTermine.setSeconds(59);

        try
        {
            //Per ogni farmaco da assumere (Prescrizioni)
            for(JSONObject farmacoDaAssumere: lista_assunzioni)
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
                    if(dataTermine.after(oggiInizio))
                    {
                        Log.i(TAG, "DataTermine > oggi "+ dataTermine.toString() + ">" + oggiInizio);

                        nomePiano = farmacoDaAssumere.getString("NomePiano");
                        nomeFarmacoComposizione = farmacoDaAssumere.getString("Nome") + " (" + farmacoDaAssumere.getString("Composizione") + ")";
                        quantita = farmacoDaAssumere.getString("Dosaggio");
                        intervallo = farmacoDaAssumere.getInt("Intervallo");

                        //Data inizio e ora inizio
                        dataInizioOraInizio = format.parse(farmacoDaAssumere.getString("DataInizio") + " " + farmacoDaAssumere.getString("OraInizio"));
                        ripeti = true;

                        Log.i(TAG, "Data e ora inizio: "+dataInizioOraInizio.toString());

                        if (dataInizioOraInizio.after(oggiInizio) && dataInizioOraInizio.before(oggiTermine)) {

                            jsonObject = new JSONObject();
                            jsonObject.put("NomePiano", nomePiano);
                            jsonObject.put("Quantita", quantita);
                            jsonObject.put("NomeFarmacoComposizione", nomeFarmacoComposizione);
                            jsonObject.put("DataInizioOraInizio", dataInizioOraInizio);
                            lista_assunzioni_odierne.add(jsonObject);
                        }else Log.i(TAG, "NON INIZIA OGGI");

                        //Finche non termina la programmazione per questo farmaco
                        while(ripeti)
                        {
                            dataInizioOraInizio.setTime(dataInizioOraInizio.getTime() + (intervallo * 3600000));

                            Log.i(TAG, "Assunzione successiva tra "+intervallo+" ora");
                            Log.i(TAG, "Data assunzione successiva "+dataInizioOraInizio.toString());

                            if(dataInizioOraInizio.after(oggiInizio) && dataInizioOraInizio.before(oggiTermine)) {

                                giornoCalcolato = sdf.format(dataInizioOraInizio);

                                //Se nel determinato di giorno si deve assumere il farmaco aggiungo alla lista
                                giornoStringa = Character.toString(giornoCalcolato.charAt(0)).toUpperCase() + giornoCalcolato.substring(1).replace("ì", "i");

                                if ("1".equals(farmacoDaAssumere.getString(giornoStringa))) {

                                    jsonObject = new JSONObject();
                                    jsonObject.put("NomePiano", nomePiano);
                                    jsonObject.put("Quantita", quantita);
                                    jsonObject.put("NomeFarmacoComposizione", nomeFarmacoComposizione);
                                    jsonObject.put("DataInizioOraInizio", dataInizioOraInizio);
                                    lista_assunzioni_odierne.add(jsonObject);
                                }
                            }else if(dataInizioOraInizio.after(dataTermine)) ripeti = false;
                        }
                    }
                }
            }

            if(lista_assunzioni_odierne.size() == 0){
            }
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());

        } catch (ParseException e) {
            Log.i(TAG, e.getMessage());

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_farmaco_da_assumere, container, false);

        text_hello = (TextView) layout.findViewById(R.id.text_hello);
        text_ora_assunzione=(TextView) layout.findViewById(R.id.text_ora_assunzione);
        text_nome_farmaco=(TextView) layout.findViewById(R.id.text_nome_farmaco);
        text_nessuna_farmaco_da_assumere = (TextView) layout.findViewById(R.id.text_nessuna_scadenza);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        text_hello.setText("Ciao "+sharedPreferences.getString("Nome",""));
        PazienteHome.cambiaTitoloSezione("mobilFarm");

        return layout;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
