package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrazionePaziente extends AppCompatActivity{

    private static final String TAG = "RegistrazionePaziente";
    private static EditText text_nome, text_cognome, text_cf, text_cellulare, text_dettagliClinici, text_residenza;
    private static Spinner spinner_sesso, text_luogoNascita, text_citta_residenza;
    private static CheckBox check_legal;
    private static TextView textView_error_message, text_dataNascita;

    private static String mail, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione_paziente);

        mail = getIntent().getStringExtra("Mail");
        password = getIntent().getStringExtra("Password");

        text_nome = (EditText) findViewById(R.id.text_nome);
        text_cognome = (EditText) findViewById(R.id.text_cognome);
        text_cf = (EditText) findViewById(R.id.text_cf);
        text_dataNascita = (TextView) findViewById(R.id.text_dataNascita);
        text_cellulare = (EditText) findViewById(R.id.text_cellulare);
        text_dettagliClinici = (EditText) findViewById(R.id.text_dettagliClinici);

        text_residenza = (EditText) findViewById(R.id.text_residenza);

        text_luogoNascita = (Spinner) findViewById(R.id.text_luogoNascita);
        text_citta_residenza = (Spinner) findViewById(R.id.text_citta_residenza);

        spinner_sesso = (Spinner) findViewById(R.id.spinner_sesso);
        check_legal = (CheckBox) findViewById(R.id.check_legal);

        textView_error_message = (TextView) findViewById(R.id.error_message);

        List<String> spinner_data = new ArrayList<>();
        spinner_data.add("Sesso");
        spinner_data.add("Donna");
        spinner_data.add("Uomo");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinner_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sesso.setAdapter(adapter);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void conferma(View view){

        disableFields();

        try
        {
            String sesso = null;
            if(spinner_sesso.getSelectedItemPosition() == 0) throw new ErrorValuesException("Seleziona il sesso!");
            else if(spinner_sesso.getSelectedItemPosition() == 2) sesso = "M";
            else if(spinner_sesso.getSelectedItemPosition() == 1) sesso = "F";

            new RegistrazionePazienteAsync().execute(
                    GestioneAccount.registraAccountPaziente(check_legal.isChecked(), mail, text_nome.getText().toString(), text_cognome.getText().toString(), password, sesso, text_cf.getText().toString(), text_cellulare.getText().toString(), text_dettagliClinici.getText().toString(), text_dataNascita.getText().toString(), text_luogoNascita.getSelectedItem().toString(), text_residenza.getText().toString(), text_citta_residenza.getSelectedItem().toString()));

        }catch (ErrorValuesException e){
            showErrorTextView(e.getMessage());
        }catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
        }
        catch(Exception e) {
            showAlertDialog("Errore interno!");
        }finally{
            enableFields();
        }
    }

    //Mostro un alert
    private void showAlertDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    //Abilito le editext della activity
    private void enableFields(){

        text_nome.setBackgroundResource(R.drawable.field_editable);
        text_cognome.setBackgroundResource(R.drawable.field_editable);
        text_cf.setBackgroundResource(R.drawable.field_editable);
        text_luogoNascita.setBackgroundResource(R.drawable.field_editable);
        text_dataNascita.setBackgroundResource(R.drawable.field_editable);
        text_cellulare.setBackgroundResource(R.drawable.field_editable);
        text_dettagliClinici.setBackgroundResource(R.drawable.field_editable);
        text_residenza.setBackgroundResource(R.drawable.field_editable);
        text_citta_residenza.setBackgroundResource(R.drawable.field_editable);

        text_nome.setEnabled(true);
        text_cognome.setEnabled(true);
        text_cf.setEnabled(true);
        text_luogoNascita.setEnabled(true);
        text_dataNascita.setEnabled(true);
        text_cellulare.setEnabled(true);
        text_dettagliClinici.setEnabled(true);
        text_residenza.setEnabled(true);
        text_citta_residenza.setEnabled(true);
    }

    //Disabilito le editext della activity
    private void disableFields(){

        text_nome.setBackgroundResource(R.drawable.field_blocked);
        text_cognome.setBackgroundResource(R.drawable.field_blocked);
        text_cf.setBackgroundResource(R.drawable.field_blocked);
        text_luogoNascita.setBackgroundResource(R.drawable.field_blocked);
        text_dataNascita.setBackgroundResource(R.drawable.field_blocked);
        text_cellulare.setBackgroundResource(R.drawable.field_blocked);
        text_dettagliClinici.setBackgroundResource(R.drawable.field_blocked);
        text_residenza.setBackgroundResource(R.drawable.field_blocked);
        text_citta_residenza.setBackgroundResource(R.drawable.field_blocked);

        text_nome.setEnabled(false);
        text_cognome.setEnabled(false);
        text_cf.setEnabled(false);
        text_luogoNascita.setEnabled(false);
        text_dataNascita.setEnabled(false);
        text_cellulare.setEnabled(false);
        text_dettagliClinici.setEnabled(false);
        text_residenza.setEnabled(false);
        text_citta_residenza.setEnabled(false);
    }

    //Mostro un messaggio di errore
    private void showErrorTextView(String message){

        textView_error_message.setText(message);
    }


    public void backToCredenziali(View view){
        super.onBackPressed();
    }

    //Apro la home di login dopo la registrazione
    public void openLoginHome(){

        Intent i = new Intent(getApplicationContext(), LoginHome.class);
        i.putExtra("Mail", mail);
        i.putExtra("Password", password);

        startActivity(i);
        finish();
    }

    private class RegistrazionePazienteAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "RegistrazionePazienteAsync";
        private static final String SECTION = "GestoreAccountServer/AccountManager";

        private String action;

        @SuppressLint("LongLogTag")
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

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(JSONObject result) {

            try
            {
                if(result == null) throw new ResponseErrorException("Errore nel Server!");

                if(result.getString("status").equals("error")){

                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if(result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                openLoginHome();

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

            text_dataNascita.setText(day+"/"+(month+1)+"/"+year);
        }
    }
}
