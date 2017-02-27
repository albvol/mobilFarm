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

public class ProfiloPaziente extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "ProfiloPaziente";

    private View layout;
    private ScrollView content;
    private TextView view_nome,view_cognome, view_sesso; //riepilogo in alto
    //Edit Text
    private EditText text_cf,text_dataNascita,text_luogoNascita,text_cellulare,text_dettagliClinici;
    private EditText text_cittaresidenza,text_residenza,text_SOS1,text_SOS2;
    private Button button_update;

    private String cf,dataNascita,luogoNascita,cellulare,dettagli,sos1,sos2,residenza,cittaResidenza;

    private TextView feedback_message;

    public ProfiloPaziente() {
        // Required empty public constructor
    }

    public static ProfiloPaziente newInstance(String param1, String param2) {
        ProfiloPaziente fragment = new ProfiloPaziente();
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
        layout = inflater.inflate(R.layout.fragment_profilo_paziente, container, false);


        view_nome = (TextView) layout.findViewById(R.id.view_nome);
        view_cognome = (TextView) layout.findViewById(R.id.view_cognome);
        view_sesso = (TextView) layout.findViewById(R.id.view_sesso);

        text_cf = (EditText) layout.findViewById(R.id.text_cf);
        text_dataNascita = (EditText) layout.findViewById(R.id.text_dataNascita);
        text_luogoNascita = (EditText) layout.findViewById(R.id.text_luogoNascita);
        text_cellulare = (EditText) layout.findViewById(R.id.text_cellulare);
        text_dettagliClinici = (EditText) layout.findViewById(R.id.text_dettagliClinici);
        text_cittaresidenza = (EditText) layout.findViewById(R.id.text_citta_residenza);
        text_residenza = (EditText) layout.findViewById(R.id.text_residenza);
        text_SOS1 = (EditText) layout.findViewById(R.id.text_SOS1);
        text_SOS2 = (EditText) layout.findViewById(R.id.text_SOS2);

        button_update = (Button) layout.findViewById(R.id.button_update);

        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        content = (ScrollView) layout.findViewById(R.id.content);

        text_dataNascita.setBackgroundResource(R.drawable.field_blocked);
        text_dataNascita.setEnabled(false);

        text_cf.setBackgroundResource(R.drawable.field_blocked);
        text_cf.setEnabled(false);

        text_luogoNascita.setBackgroundResource(R.drawable.field_blocked);
        text_luogoNascita.setEnabled(false);

        PazienteHome.cambiaTitoloSezione("Profilo");

        button_update.setOnClickListener(update);

        try {
            new ProfiloPazienteAsync().execute(
                    GestioneAccount.profiloPersonalePaziente());
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

    private View.OnClickListener update = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!isUpdate()){
                try {
                    disableFields();
                    String n1,n2;

                    if(text_SOS1.getText().toString().isEmpty())
                        n1="";
                    else
                        n1=text_SOS1.getText().toString();

                    if(text_SOS2.getText().toString().isEmpty())
                        n2="";
                    else
                        n2=text_SOS2.getText().toString();

                    if((n1.isEmpty()&& n2.isEmpty()) || !(n1.compareTo(n2)==0)){

                        new ProfiloPazienteAsync().execute(
                                GestioneAccount.aggiornaProfiloPaziente(text_cellulare.getText().toString(),text_dettagliClinici.getText().toString(),n1,n2,
                                        text_residenza.getText().toString(),text_cittaresidenza.getText().toString()) );
                    }else {
                        showAlertDialog("I due numeri di emergenza non possono essere uguali");
                        enableFields();
                    }

                } catch (JSONException e) {
                    showAlertDialog("Errore Interno!");
                    hideFeedbackTextView();
                }
                catch (ActionNotAuthorizedException e) {
                    showAlertDialog(e.getMessage());
                    hideFeedbackTextView();
                } catch (ErrorValuesException e) {
                    showAlertDialog(e.getMessage());
                    hideFeedbackTextView();
                }
                finally {
                    enableFields();
                }
            }else showFeedbackTextView("Nessun campo da aggiornare!");

        }
    };

    public void showPazienteData(String nome, String cognome, String sesso, String cf, String dataNascita,
                                 String luogoNascita, String cellulare,String dettagli, String sos1, String sos2,
                                 String residenza, String cittaResidenza){

        view_nome.setText(nome);
        view_cognome.setText(cognome);
        view_sesso.setText(sesso);

        text_cf.setText(cf);
        text_dataNascita.setText(dataNascita);
        text_luogoNascita.setText(luogoNascita);
        text_cellulare.setText(cellulare.replace("+39",""));
        text_dettagliClinici.setText(dettagli);
        text_residenza.setText(residenza);
        text_cittaresidenza.setText(cittaResidenza);

        this.cf=cf;
        this.dataNascita=dataNascita;
        this.luogoNascita=luogoNascita;
        this.cellulare=cellulare;
        this.dettagli=dettagli;
        this.residenza=residenza;
        this.cittaResidenza=cittaResidenza;

        if(sos1.equals("null")) {
            text_SOS1.setText("");
            text_SOS1.setHint("Numero Di Emergenza 1");
            this.sos1="null";
        }
        else{
            text_SOS1.setText(sos1.replace("+39",""));
            this.sos1 = sos1;
        }

        if(sos2.equals("null")) {
            text_SOS2.setText("");
            text_SOS2.setHint("Numero Di Emergenza 1");
            this.sos2="null";
        }
        else{
            text_SOS2.setText(sos2.replace("+39",""));
            this.sos2 = sos2;
        }

        hideFeedbackTextView();
        content.setVisibility(View.VISIBLE);

    }

    //Verifica se i campi contengono gli stessi dati di quando sono stati caricati
    public boolean isUpdate(){

        if(cf.compareToIgnoreCase(text_cf.getText().toString())==0 &&
                dataNascita.compareToIgnoreCase(text_dataNascita.getText().toString())==0 &&
                luogoNascita.compareToIgnoreCase(text_luogoNascita.getText().toString())==0 &&
                cellulare.compareToIgnoreCase(text_cellulare.getText().toString())==0 &&
                dettagli.compareToIgnoreCase(text_dettagliClinici.getText().toString())==0 &&
                sos1.compareToIgnoreCase(text_SOS1.getText().toString())==0 &&
                sos2.compareToIgnoreCase(text_SOS2.getText().toString())==0 &&
                cittaResidenza.compareToIgnoreCase(text_cittaresidenza.getText().toString())==0 &&
                residenza.compareToIgnoreCase(text_residenza.getText().toString())==0 )
            return true;
        else
            return false;
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

        text_cellulare.setBackgroundResource(R.drawable.field_editable);
        text_dettagliClinici.setBackgroundResource(R.drawable.field_editable);
        text_SOS1.setBackgroundResource(R.drawable.field_editable);
        text_SOS2.setBackgroundResource(R.drawable.field_editable);
        text_residenza.setBackgroundResource(R.drawable.field_editable);
        text_cittaresidenza.setBackgroundResource(R.drawable.field_editable);

        text_cellulare.setEnabled(true);
        text_dettagliClinici.setEnabled(true);
        text_SOS1.setEnabled(true);
        text_SOS2.setEnabled(true);
        text_residenza.setEnabled(true);
        text_cittaresidenza.setEnabled(true);
    }

    //Disabilito le editext della activity
    private void disableFields(){

        text_cellulare.setBackgroundResource(R.drawable.field_blocked);
        text_dettagliClinici.setBackgroundResource(R.drawable.field_blocked);
        text_SOS1.setBackgroundResource(R.drawable.field_blocked);
        text_SOS2.setBackgroundResource(R.drawable.field_blocked);
        text_residenza.setBackgroundResource(R.drawable.field_blocked);
        text_cittaresidenza.setBackgroundResource(R.drawable.field_blocked);


        text_cellulare.setEnabled(false);
        text_dettagliClinici.setEnabled(false);
        text_SOS1.setEnabled(false);
        text_SOS2.setEnabled(false);
        text_residenza.setEnabled(false);
        text_cittaresidenza.setEnabled(false);

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


    private class ProfiloPazienteAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "ProfiloPazienteAsync";
        private static final String SECTION = "GestoreAccountServer/AccountManager";

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
                if(result == null) throw new ResponseErrorException("Errore nel Server!");

                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }



                if(action.equals("aggiornaProfiloPaziente")){
                    showFeedbackTextView("Aggiornamento dei dati completato");
                    enableFields();
                }
                else if (action.equals("profiloPersonalePaziente"))
                {

                    JSONObject data = result.getJSONObject("data");
                    String sesso;
                    if (data.getString("Sesso").compareToIgnoreCase("M")==0)
                        sesso="Uomo";
                    else
                        sesso="Donna";
                    showPazienteData(data.getString("Nome"), data.getString("Cognome"), sesso,data.getString("CodiceFiscale"),
                            data.getString("DataNascita"),data.getString("LuogoNascita"),data.getString("Cellulare"),
                            data.getString("DettagliClinici"),data.getString("NumeroSOS1"),data.getString("NumeroSOS2"),
                            data.getString("Residenza"),data.getString("CittaResidenza"));
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
