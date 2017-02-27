package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestorePianoClient.PianiTerapeutici;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class SchedaPaziente extends Fragment {

    private static final String TAG = "SchedaPaziente";
    private OnFragmentInteractionListener mListener;

    private static View layout;
    private TextView feedback_message, feedback_message_2, text_nome_cognome, sesso, data_nascita_value, luogo_nascita_value, residenza_value, citta_residenza_value, cellulare_value, email_value, cd_value, dettagli_clinici;
    private static String pazienteID;
    private static RelativeLayout content;
    private Button rimuovi_button;
    private RelativeLayout section_piani;

    public SchedaPaziente() {
        // Required empty public constructor
    }

    public static void setInfo(String userID)
    {
        pazienteID = userID;
    }

    public static SchedaPaziente newInstance() {
        SchedaPaziente fragment = new SchedaPaziente();

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
        layout = inflater.inflate(R.layout.fragment_scheda_paziente, container, false);

        //Salvo i reference delle view
        text_nome_cognome = (TextView) layout.findViewById(R.id.text_nome_cognome);
        sesso = (TextView) layout.findViewById(R.id.sesso);
        data_nascita_value = (TextView) layout.findViewById(R.id.data_nascita_value);
        luogo_nascita_value = (TextView) layout.findViewById(R.id.luogo_nascita_value);
        residenza_value = (TextView) layout.findViewById(R.id.text_residenza);
        citta_residenza_value = (TextView) layout.findViewById(R.id.text_citta_residenza);
        cellulare_value = (TextView) layout.findViewById(R.id.cellulare_value);
        email_value = (TextView) layout.findViewById(R.id.email_value);
        cd_value = (TextView) layout.findViewById(R.id.cd_value);
        content = (RelativeLayout) layout.findViewById(R.id.content);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        feedback_message_2 = (TextView) layout.findViewById(R.id.feedback_message_2);
        cd_value = (TextView) layout.findViewById(R.id.cd_value);
        dettagli_clinici = (TextView) layout.findViewById(R.id.dettagli_clinici);
        rimuovi_button = (Button) layout.findViewById(R.id.rimuovi_button);

        rimuovi_button.setOnClickListener(rimuovi_paziente);

        section_piani = (RelativeLayout) layout.findViewById(R.id.section_piani);

        DottoreHome.cambiaTitoloSezione("Scheda Paziente");

        try {
            new SchedaPazienteAsync().execute(GestioneRubrica.visualizzaProfiloPazienteSelezionato(pazienteID));

            PianiTerapeutici pianiTerapeutici = new PianiTerapeutici();
            pianiTerapeutici.setInfo(pazienteID);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.replace(R.id.section_piani,pianiTerapeutici).commit();
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

    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){
        feedback_message.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        feedback_message.setText(message);
    }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /* Mostro le informazioni del paziente */
    private void showPazienteData(String nome_cognome, String sesso, String data_nascita, String luogo_nascita, String residenza, String cittaResidenza, String cellulare, String email, String cd, String dettagli_clinici)
    {
        this.text_nome_cognome.setText(nome_cognome);
        this.data_nascita_value.setText(data_nascita);
        this.luogo_nascita_value.setText(luogo_nascita);
        this.residenza_value.setText(residenza);
        this.citta_residenza_value.setText(cittaResidenza);
        this.cellulare_value.setText(cellulare);
        this.email_value.setText(email);
        this.cd_value.setText(cd);
        this.dettagli_clinici.setText(dettagli_clinici);

        String s;
        if(sesso.compareToIgnoreCase("M")==0)
            s="Uomo";
        else
            s="Donna";
        this.sesso.setText(s);

        hideFeedbackTextView();
        content.setVisibility(View.VISIBLE);
    }

    View.OnClickListener rimuovi_paziente = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                new SchedaPazienteAsync().execute(GestioneRubrica.rimuoviPaziente(pazienteID));
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

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }
    
    private class SchedaPazienteAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "SchedaPazienteAsync";
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

                }else if(action.equals("rimuoviPaziente"))
                {
                    feedback_message_2.setText("Paziente rimosso!");
                    rimuovi_button.setVisibility(View.GONE);
                }else if (action.equals("visualizzaProfiloPazienteSelezionato")){

                        JSONObject data = result.getJSONObject("data");
                        showPazienteData(data.getString("Nome") + " " + data.getString("Cognome"), data.getString("Sesso"), data.getString("DataNascita"), data.getString("LuogoNascita"), data.getString("Residenza"), data.getString("CittaResidenza"), data.getString("Cellulare"), data.getString("Mail"), data.getString("CodiceFiscale"), data.getString("DettagliClinici"));
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
