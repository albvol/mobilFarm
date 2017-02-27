package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class NuovoFarmaco extends Fragment {

    private static final String TAG = "NuovoFarmaco";
    private OnFragmentInteractionListener mListener;

    private EditText nome_farmaco;
    private static TextView text_scadenza_farmaco;
    private Spinner composizione_value;
    private View layout;
    private TextView feedback_message;
    private Button button_create;
    private static String previous_text_search;

    public NuovoFarmaco() {
        // Required empty public constructor
    }

    public static NuovoFarmaco newInstance() {
        NuovoFarmaco fragment = new NuovoFarmaco();
        return fragment;
    }

    public static void setInfo(String keyword){
        previous_text_search = keyword;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_nuovo_farmaco, container, false);
        nome_farmaco = (EditText) layout.findViewById(R.id.nome_farmaco);
        composizione_value = (Spinner) layout.findViewById(R.id.composizione_value);
        text_scadenza_farmaco = (TextView) layout.findViewById(R.id.text_scadenza_farmaco);
        text_scadenza_farmaco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        button_create = (Button) layout.findViewById(R.id.button_create);

        button_create.setOnClickListener(create_farmaco);

        DottoreHome.cambiaTitoloSezione("Nuovo Farmaco");

        if(!previous_text_search.isEmpty()) nome_farmaco.setText(previous_text_search);

        return layout;
    }

    private View.OnClickListener create_farmaco = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                if(composizione_value.getSelectedItemPosition() != 0) {
                    new NuovoFarmacoAsync().execute(
                            GestioneFarmaco.esistenzaFarmaco(nome_farmaco.getText().toString(), composizione_value.getSelectedItem().toString()));
                }else throw new ErrorValuesException("Selezionare la composizione!");
            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            } catch (ErrorValuesException e) {
                Log.i(TAG, e.getMessage());
                showFeedbackTextView(e.getMessage());
            }
        }
    };

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

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
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

    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
        feedback_message.setVisibility(View.VISIBLE);
    }

    //Nascondo il messaggio di feedback
    private void hideFeedbackTextView(){
        feedback_message.setVisibility(View.GONE);
    }


    //Abilito le editext della activity
    private void enableFields(){

        nome_farmaco.setBackgroundResource(R.drawable.field_editable);
        composizione_value.setBackgroundResource(R.drawable.field_editable);
        text_scadenza_farmaco.setBackgroundResource(R.drawable.field_editable);

        nome_farmaco.setEnabled(true);
        composizione_value.setEnabled(true);
        text_scadenza_farmaco.setEnabled(true);
    }

    //Disabilito le editext della activity
    private void disableFields(){

        nome_farmaco.setBackgroundResource(R.drawable.field_blocked);
        composizione_value.setBackgroundResource(R.drawable.field_blocked);
        text_scadenza_farmaco.setBackgroundResource(R.drawable.field_blocked);

        nome_farmaco.setEnabled(false);
        composizione_value.setEnabled(false);
        text_scadenza_farmaco.setEnabled(false);
    }

    private class NuovoFarmacoAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "NuovoFarmacoAsync";
        private static final String SECTION = "GestoreFarmacoServer/FarmacoManager";

        private String action;

        @Override
        protected void onPreExecute() {
            disableFields();
            showFeedbackTextView("Salvo le informazioni relative al farmaco...");
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
                if(result.getString("status").equals("error"))
                {
                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else
                        throw new ResponseErrorException(result.getString("message"));
                }

                if(action.equals("esistenzaFarmaco")){

                    JSONObject data = result.getJSONObject("data");

                    if(data.getString("count").equals("0")){

                        new NuovoFarmacoAsync().execute(
                                GestioneFarmaco.creaFarmaco(nome_farmaco.getText().toString(), composizione_value.getSelectedItem().toString()));

                        if(!text_scadenza_farmaco.getText().toString().isEmpty())
                        {
                            new NuovoFarmacoVMFAsync().execute(
                                    GestioneVMF.aggiungiFarmacoVMF(result.getString("data"), text_scadenza_farmaco.getText().toString()));
                        }else DottoreHome.apriVMF();

                    }else throw new ErrorValuesException("Il farmaco che stai cercando di inserire è già presente nel Database");
                }



            } catch (JSONException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            }
            catch (ResponseErrorException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            }
            catch (ActionNotAuthorizedException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog(e.getMessage());

            } catch (ErrorValuesException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }
    }


    private class NuovoFarmacoVMFAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "NuovoFarmacoAsync";
        private static final String SECTION = "GestoreFarmacoServer/VMFManager";

        private String action;

        @Override
        protected void onPreExecute() {
            showFeedbackTextView("Salvo il farmaco nel tuo VMF...");
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
                if(result.getString("status").equals("error"))
                {
                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else
                        throw new ResponseErrorException(result.getString("message"));
                }

                enableFields();
                DottoreHome.apriVMF();

            } catch (JSONException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            }
            catch (ResponseErrorException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            }
            catch (ActionNotAuthorizedException e){
                Log.e(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            text_scadenza_farmaco.setText(year+"/"+(month+1)+"/"+day);
        }
    }
}
