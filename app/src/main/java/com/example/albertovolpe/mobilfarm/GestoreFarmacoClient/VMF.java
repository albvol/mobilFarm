package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestoreAccountClient.PazienteHome;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class VMF extends Fragment {

    private static final String TAG = "VMF";
    private static SharedPreferences sharedPreferences;

    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private TextView feedback_message;
    private View layout;
    private EditText text_filter;

    public VMF() {
        // Required empty public constructor
    }


    public static VMF newInstance() {
        VMF fragment = new VMF();
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
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_vmf, container, false);

        //Salvo i reference delle view
        listView = (ListView) layout.findViewById(R.id.lista_farmaci);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        text_filter = (EditText) layout.findViewById(R.id.text_filter);

        //Leggo il tipo di utente loggato
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userType = sharedPreferences.getString("Type", "Utente non identificato");

        if (userType.equals("Dottore")) DottoreHome.cambiaTitoloSezione("virtual mobilFarm");
        else if (userType.equals("Paziente")) PazienteHome.cambiaTitoloSezione("virtual mobilFarm");


        try {
            //Carico il vmf
            new VMFAsync().execute(GestioneVMF.visualizzaVMF());

        }catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
            showAlertDialog("Errore interno!");
        }


        return layout;

    }

    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
        feedback_message.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    //Nascondo il messaggio di feedback
    private void hideFeedbackTextView(){
        feedback_message.setVisibility(View.GONE);
    }

    private void showListaFarmaci(ArrayList lista){

        //Imposto l'adapter
        final ListaFarmaciVMFAdapter adapter = new ListaFarmaciVMFAdapter(layout.getContext(), R.layout.farmaco_vmf, lista);
        listView.setAdapter(adapter);

        feedback_message.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        text_filter.addTextChangedListener(new TextWatcher() {

            Pattern letters_numbers = Pattern.compile("^[a-z 0-9]+$", Pattern.CASE_INSENSITIVE);

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    if(s.length() != 0) {
                        if (!(letters_numbers.matcher(s.toString())).find())
                            throw new ErrorValuesException("Il filtro non può contenere caratteri speciali!");
                        else adapter.getFilter().filter(s.toString());
                    }
                } catch (ErrorValuesException e) {
                    showAlertDialog(e.getMessage());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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


    public class VMFAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "VMFAsync";
        private static final String SECTION = "GestoreFarmacoServer/VMFManager";

        private String action;

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
                if(result.getString("status").equals("error")){

                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else
                        throw new ResponseErrorException(result.getString("message"));

                }

                if(action.equals("eliminaFarmacoVMF")){

                    new VMFAsync().execute(GestioneVMF.visualizzaVMF());

                }else if(action.equals("visualizzaVMF")){

                    JSONObject data = result.getJSONObject("data");
                    if(data.getString("count").equals("0"))
                        showFeedbackTextView("Nessun Farmaco presente...\n\nPremi il tasto + per ricercare\ne aggiungere i tuoi farmaci.");
                    else
                    {
                        JSONArray lista_dottori = data.getJSONArray("lista_farmaci");

                        ArrayList<JSONObject> list = new ArrayList<>();
                        for (int index = 0; index < lista_dottori.length(); index++)
                            list.add(lista_dottori.getJSONObject(index));

                        showListaFarmaci(list);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            }
            catch (ResponseErrorException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            }
            catch (ActionNotAuthorizedException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }
    }

}
