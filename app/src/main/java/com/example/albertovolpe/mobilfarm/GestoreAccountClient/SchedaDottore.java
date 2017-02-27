package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SchedaDottore extends Fragment {

    private static final String TAG = "SchedaDottore";
    private OnFragmentInteractionListener mListener;

    private static View layout;
    private static TextView feedback_message_2, text_nome_cognome, specializzazione, feedback_message, indirizzo_studio_value, città_studio_value, telefono_value, email_value;
    private static RelativeLayout content;
    private static Button aggiungi_button, rimuovi_button;

    private static Boolean isNewDoctor;
    private static String dottoreID;

    public SchedaDottore() {
        // Required empty public constructor
    }

    public static void setInfo(Boolean isNew, String userID)
    {
        dottoreID = userID;
        isNewDoctor = isNew;
    }

    public static SchedaDottore newInstance() {
        SchedaDottore fragment = new SchedaDottore();
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
        layout = inflater.inflate(R.layout.fragment_scheda_dottore, container, false);

        //Salvo i reference delle view
        text_nome_cognome = (TextView) layout.findViewById(R.id.text_nome_cognome);
        specializzazione = (TextView) layout.findViewById(R.id.specializzazione);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        feedback_message_2 = (TextView) layout.findViewById(R.id.feedback_message_2);
        indirizzo_studio_value = (TextView) layout.findViewById(R.id.indirizzo_studio_value);
        città_studio_value = (TextView) layout.findViewById(R.id.città_studio_value);
        telefono_value = (TextView) layout.findViewById(R.id.telefono_value);
        email_value = (TextView) layout.findViewById(R.id.email_value);
        content = (RelativeLayout) layout.findViewById(R.id.content);
        aggiungi_button = (Button) layout.findViewById(R.id.aggiungi_button);
        rimuovi_button = (Button) layout.findViewById(R.id.rimuovi_button);

        PazienteHome.cambiaTitoloSezione("Scheda Dottore");

        if(isNewDoctor)
        {
            aggiungi_button.setVisibility(View.VISIBLE);
            aggiungi_button.setOnClickListener(aggiungi_dottore);
        }else {
            rimuovi_button.setVisibility(View.VISIBLE);
            rimuovi_button.setOnClickListener(rimuovi_dottore);
        }

        try {
            new SchedaDottoreAsync().execute(
                    GestioneRubrica.visualizzaProfiloDottoreSelezionato(dottoreID));
        } catch (JSONException e) {
            showAlertDialog("Errore Interno!");
            hideFeedbackTextView();
        }
        catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
            showFeedbackTextView("Non sei autorizzato a visualizzare queste informazioni...");
        }
        catch (ErrorValuesException e) {
            showAlertDialog(e.getMessage());
            showFeedbackTextView("Ripeti l'azione...");
        }

        return layout;
    }

    View.OnClickListener aggiungi_dottore = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                new SchedaDottoreAsync().execute(GestioneRubrica.aggiungiDottore(dottoreID));
            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            } catch (ErrorValuesException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }
    };

    View.OnClickListener rimuovi_dottore = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                new SchedaDottoreAsync().execute(GestioneRubrica.rimuoviDottore(dottoreID));
            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            } catch (ErrorValuesException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }
    };

    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
        feedback_message.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
    }

    //Nascondo il messaggio di feedback
    private void hideFeedbackTextView(){
        feedback_message.setVisibility(View.GONE);
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

    /* Mostro le informazioni del dottore */
    private void showDottoreData(String nome_cognome, String specializzazione, String indirizzo_studio, String città_studio, String telefono_value, String email_value)
    {
        this.text_nome_cognome.setText(nome_cognome);
        this.specializzazione.setText(specializzazione);
        this.indirizzo_studio_value.setText(indirizzo_studio);
        this.città_studio_value.setText(città_studio);
        this.telefono_value.setText(telefono_value);
        this.email_value.setText(email_value);

        feedback_message.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    private class SchedaDottoreAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "SchedaDottoreAsync";
        private static final String SECTION = "GestoreAccountServer/RubricaManager";

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
                    else if(result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));

                }else {

                    if(action.equals("aggiungiDottore"))
                    {
                        feedback_message_2.setText("Dottore aggiunto!");
                        aggiungi_button.setVisibility(View.GONE);
                    }else if(action.equals("rimuoviDottore"))
                    {
                        feedback_message_2.setText("Dottore rimosso!");
                        rimuovi_button.setVisibility(View.GONE);
                    }else if(action.equals("visualizzaProfiloDottoreSelezionato")) {
                        JSONObject data = result.getJSONObject("data");
                        showDottoreData(data.getString("Nome") + " " + data.getString("Cognome"), data.getString("Specializzazione"), data.getString("IndirizzoStudio"), data.getString("Citta"), data.getString("TelefonoStudio"), data.getString("Mail"));
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
