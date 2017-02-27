package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.GestionePianoTerapeutico;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FarmacoInScadenza extends Fragment {

    private static final String TAG = "FarmacoInScadenza";

    private View layout;
    private TextView text_hello,text_scadenza,text_nomeFarmaco, text_nessuna_scadenza;

    private OnFragmentInteractionListener mListener;

    public FarmacoInScadenza() {
        // Required empty public constructor
    }


    public static FarmacoInScadenza newInstance() {
        FarmacoInScadenza fragment = new FarmacoInScadenza();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout=inflater.inflate(R.layout.fragment_farmaco_in_scadenza, container, false);

        DottoreHome.cambiaTitoloSezione("mobilFarm");
        text_hello = (TextView) layout.findViewById(R.id.text_hello);
        text_scadenza=(TextView) layout.findViewById(R.id.text_scadenza);
        text_nomeFarmaco=(TextView) layout.findViewById(R.id.text_nome_farmaco);
        text_nessuna_scadenza = (TextView) layout.findViewById(R.id.text_nessuna_scadenza);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        text_hello.setText("Salve, Dott."+sharedPreferences.getString("Cognome",""));

        try {
            new FarmacoInScadenzaAsync().execute(
                    GestioneVMF.elencoFarmaciScaduti());
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog("Errore Interno!");
        }
        catch (ActionNotAuthorizedException e) {

            Log.i(TAG, e.getMessage());
            showAlertDialog(e.getMessage());
        }


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

    public void showScadenze(ArrayList<JSONObject> farmaci){

        ListaFarmaciInScadenzaAdapter adapter = new ListaFarmaciInScadenzaAdapter(layout.getContext(), R.layout.farmaco_in_scadenza, farmaci);
        ((ListView) layout.findViewById(R.id.lista_farmaci)).setAdapter(adapter);
    }

    private class FarmacoInScadenzaAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "FarmacoInScadenzaAsync";
        private static final String SECTION = "GestoreFarmacoServer/VMFManager";

        private String action;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {

            try {

                action = params[0].getString("action");
                return mobilFarmServer.connect(SECTION, params[0]);

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            catch(ConnectionErrorException e) {
                Log.e(TAG, e.getMessage());
            }
            catch(IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try
            {
                if(result == null) throw new ResponseErrorException("Errore nel Server!");

                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if (action.equals("elencoFarmaciScaduti"))
                {
                    JSONObject data = result.getJSONObject("data");
                    if(data.getString("count").equals("0")) text_nessuna_scadenza.setVisibility(View.VISIBLE);
                    else
                    {
                        JSONArray lista_scadenze = data.getJSONArray("lista_scadenze");

                        ArrayList<JSONObject> farmaci = new ArrayList<>();
                        for (int index = 0; index < lista_scadenze.length(); index++)
                            farmaci.add(lista_scadenze.getJSONObject(index));

                        showScadenze(farmaci);
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            catch (ResponseErrorException e) {
                Log.e(TAG, e.getMessage());
            }
            catch (ActionNotAuthorizedException e) {
                Log.e(TAG, e.getMessage());
            }

        }
    }
    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }
}
