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
import android.widget.EditText;
import android.widget.ScrollView;
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

import static com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome.cambiaTitoloSezione;


public class ProfiloDottore extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "ProfiloDottore";

    private View layout;
    private ScrollView content;
    private TextView view_nome,view_cognome, view_sesso; //riepilogo in alto
    //Edit Text
    private EditText text_indirizzoStudio,text_prov,text_citta,text_cellulare,text_idAlbo,text_spinner;
    private Button button_update;

    private String indirizzoStudio,prov,citta,cellulare;

    private TextView feedback_message;


    public ProfiloDottore() {
        // Required empty public constructor
    }

    public static ProfiloDottore newInstance(String param1, String param2) {
        ProfiloDottore fragment = new ProfiloDottore();
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
        layout = inflater.inflate(R.layout.fragment_profilo_dottore, container, false);

        view_nome = (TextView) layout.findViewById(R.id.view_nome);
        view_cognome = (TextView) layout.findViewById(R.id.view_cognome);
        view_sesso = (TextView) layout.findViewById(R.id.view_sesso);

        text_indirizzoStudio = (EditText) layout.findViewById(R.id.text_indirizzoStudio);
        text_prov = (EditText) layout.findViewById(R.id.text_prov);
        text_citta = (EditText) layout.findViewById(R.id.text_citta);
        text_cellulare = (EditText) layout.findViewById(R.id.text_cellulare);
        text_idAlbo = (EditText) layout.findViewById(R.id.text_idAlbo);
        text_spinner =(EditText) layout.findViewById(R.id.text_spinner);

        button_update = (Button) layout.findViewById(R.id.button_update);

        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        content = (ScrollView) layout.findViewById(R.id.content);


        text_idAlbo.setBackgroundResource(R.drawable.field_blocked);
        text_idAlbo.setEnabled(false);
        text_spinner.setBackgroundResource(R.drawable.field_blocked);
        text_spinner.setEnabled(false);

        cambiaTitoloSezione("Profilo");

        text_idAlbo.setBackgroundResource(R.drawable.field_blocked);
        text_idAlbo.setEnabled(false);

        button_update.setOnClickListener(aggiorna);

        try {
            new ProfiloDottoreAsync().execute(
                    GestioneAccount.profiloPersonaleDottore());
        } catch (JSONException e) {
            showAlertDialog("Errore Interno!");
            hideFeedbackTextView();
        }
        catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
            showFeedbackTextView(e.getMessage());
        }


        return layout;

    }

    private View.OnClickListener aggiorna = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!isUpdate()){
                // aggiungere il prompt con la domanda per la conferma dell'aggiornamento
                //I dati sono stati modificati vuoi mantenere le modifiche?
                try {
                    disableFields();

                    new ProfiloDottore.ProfiloDottoreAsync().execute(
                            GestioneAccount.aggiornaProfiloDottore(text_indirizzoStudio.getText().toString(),text_prov.getText().toString(),text_citta.getText().toString(),
                                    text_cellulare.getText().toString(),text_spinner.getText().toString()));

                } catch (JSONException e) {
                    showAlertDialog("Errore Interno!");
                    hideFeedbackTextView();
                }
                catch (ActionNotAuthorizedException e) {
                    showAlertDialog(e.getMessage());
                    showFeedbackTextView(e.getMessage());
                } catch (ErrorValuesException e) {
                    showFeedbackTextView(e.getMessage());
                }
            }else{
                showFeedbackTextView("Nessun campo da aggiornare!");
            }

        }
    };

    public void showDottoreData(String nome, String cognome, String sesso,String indirizzoStudio,String prov,String citta,String cellulare,String idAlbo,String spec){

        view_nome.setText(nome);
        view_cognome.setText(cognome);
        view_sesso.setText(sesso);
        text_indirizzoStudio.setText(indirizzoStudio);
        text_prov.setText(prov);
        text_citta.setText(citta);
        text_cellulare.setText(cellulare);
        text_idAlbo.setText(idAlbo);
        text_spinner.setText(spec);

        this.indirizzoStudio=indirizzoStudio;
        this.prov=prov;
        this.citta=citta;
        this.cellulare=cellulare;

        hideFeedbackTextView();
        content.setVisibility(View.VISIBLE);


    }

    //Verifica se i campi contengono gli stessi dati di quando sono stati caricati
    public boolean isUpdate(){

        if(indirizzoStudio.compareToIgnoreCase(text_indirizzoStudio.getText().toString())==0 &&
                prov.compareToIgnoreCase(text_prov.getText().toString())==0 &&
                citta.compareToIgnoreCase(text_citta.getText().toString())==0 &&
                cellulare.compareToIgnoreCase(text_cellulare.getText().toString())==0)
            return true;
        else
            return false;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }


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

    //Abilito le editext della activity
    private void enableFields(){

        text_indirizzoStudio.setBackgroundResource(R.drawable.field_editable);
        text_prov.setBackgroundResource(R.drawable.field_editable);
        text_citta.setBackgroundResource(R.drawable.field_editable);
        text_cellulare.setBackgroundResource(R.drawable.field_editable);
        text_spinner.setBackgroundResource(R.drawable.field_editable);

        text_indirizzoStudio.setEnabled(true);
        text_prov.setEnabled(true);
        text_citta.setEnabled(true);
        text_cellulare.setEnabled(true);
        text_spinner.setEnabled(true);

    }

    //Disabilito le editext della activity
    private void disableFields(){

        text_indirizzoStudio.setBackgroundResource(R.drawable.field_blocked);
        text_prov.setBackgroundResource(R.drawable.field_blocked);
        text_citta.setBackgroundResource(R.drawable.field_blocked);
        text_cellulare.setBackgroundResource(R.drawable.field_blocked);
        text_spinner.setBackgroundResource(R.drawable.field_blocked);

        text_indirizzoStudio.setEnabled(false);
        text_prov.setEnabled(false);
        text_citta.setEnabled(false);
        text_cellulare.setEnabled(false);
        text_spinner.setEnabled(false);

    }

    private class ProfiloDottoreAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "ProfiloDottoreAsync";
        private static final String SECTION = "GestoreAccountServer/AccountManager";

        private String action;

        @Override
        protected void onPreExecute() {
            showFeedbackTextView("Aggiornamento in corso...");
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

                //roba da fare


                if (action.equals("profiloPersonaleDottore"))
                {
                    JSONObject data = result.getJSONObject("data");
                    String sesso;
                    if (data.getString("Sesso").compareToIgnoreCase("M")==0)
                        sesso="Uomo";
                    else
                        sesso="Donna";

                    showDottoreData(data.getString("Nome"), data.getString("Cognome"), sesso,data.getString("IndirizzoStudio")
                            , data.getString("Provincia") , data.getString("Citta"),data.getString("TelefonoStudio"),
                            data.getString("IDAlbo"),data.getString("Specializzazione"));
                }
                else if(action.equals("aggiornaProfiloDottore")){

                    showFeedbackTextView("Aggiornamento dei dati completato");
                    enableFields();
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
            } finally {
                enableFields();
            }

        }
    }
}
