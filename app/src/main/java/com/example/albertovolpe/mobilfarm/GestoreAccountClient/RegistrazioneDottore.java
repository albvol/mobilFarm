package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import java.util.List;

public class RegistrazioneDottore extends AppCompatActivity {

    private static final String TAG = "RegistrazioneDottore";
    private static EditText text_nome, text_cognome, text_indirizzoStudio, text_prov, text_cellulare, text_numAlbo;
    private static Spinner spinner_sesso, spinner_citta, spinner_specializzazione;
    private static CheckBox check_legal;
    private static TextView textView_error_message;

    private static String mail, password;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione_dottore);

        mail = getIntent().getStringExtra("Mail");
        password = getIntent().getStringExtra("Password");

        text_nome = (EditText) findViewById(R.id.text_nome);
        text_cognome = (EditText) findViewById(R.id.text_cognome);
        text_indirizzoStudio = (EditText) findViewById(R.id.text_indirizzoStudio);
        text_prov = (EditText) findViewById(R.id.text_prov);
        text_cellulare = (EditText) findViewById(R.id.text_cellulare);
        text_numAlbo = (EditText) findViewById(R.id.text_numAlbo);

        spinner_citta = (Spinner) findViewById(R.id.spinner_citta);
        spinner_specializzazione = (Spinner) findViewById(R.id.spinner_specializzazione);
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

        spinner_citta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String comune = spinner_citta.getSelectedItem().toString();
                text_prov.setText(comune.substring(comune.indexOf("(")+1, comune.indexOf(")")));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void conferma(View view){

        disableFields();

        try
        {
            String sesso = null;
            if(spinner_sesso.getSelectedItemPosition() == 2) sesso = "M";
            else if(spinner_sesso.getSelectedItemPosition() == 1) sesso = "F";
            else sesso = "0";

            new RegistrazioneDottoreAsync().execute(
                    GestioneAccount.registraAccountDottore(check_legal.isChecked(), mail, text_nome.getText().toString(), text_cognome.getText().toString(), password, sesso, text_indirizzoStudio.getText().toString(), text_cellulare.getText().toString(), text_numAlbo.getText().toString(), spinner_citta.getSelectedItem().toString(), text_prov.getText().toString(), spinner_specializzazione.getSelectedItem().toString()));

        }catch (ErrorValuesException e){
            showErrorTextView(e.getMessage());
            enableFields();
        }catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
            enableFields();
        }
        catch(Exception e) {
            showAlertDialog("Errore interno!");
            enableFields();
        }finally {
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
        text_indirizzoStudio.setBackgroundResource(R.drawable.field_editable);
        text_prov.setBackgroundResource(R.drawable.field_editable);
        spinner_citta.setBackgroundResource(R.drawable.field_editable);
        text_cellulare.setBackgroundResource(R.drawable.field_editable);
        text_numAlbo.setBackgroundResource(R.drawable.field_editable);
        spinner_specializzazione.setBackgroundResource(R.drawable.field_editable);

        text_nome.setEnabled(true);
        text_cognome.setEnabled(true);
        text_indirizzoStudio.setEnabled(true);
        text_prov.setEnabled(true);
        spinner_citta.setEnabled(true);
        text_cellulare.setEnabled(true);
        text_numAlbo.setEnabled(true);
        spinner_specializzazione.setEnabled(true);
    }

    //Disabilito le editext della activity
    private void disableFields(){

        text_nome.setBackgroundResource(R.drawable.field_blocked);
        text_cognome.setBackgroundResource(R.drawable.field_blocked);
        text_indirizzoStudio.setBackgroundResource(R.drawable.field_blocked);
        text_prov.setBackgroundResource(R.drawable.field_blocked);
        spinner_citta.setBackgroundResource(R.drawable.field_blocked);
        text_cellulare.setBackgroundResource(R.drawable.field_blocked);
        text_numAlbo.setBackgroundResource(R.drawable.field_blocked);
        spinner_specializzazione.setBackgroundResource(R.drawable.field_blocked);

        text_nome.setEnabled(false);
        text_cognome.setEnabled(false);
        text_indirizzoStudio.setEnabled(false);
        text_prov.setEnabled(false);
        spinner_citta.setEnabled(false);
        text_cellulare.setEnabled(false);
        text_numAlbo.setEnabled(false);
        spinner_specializzazione.setEnabled(false);
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

    private class RegistrazioneDottoreAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "RegistrazioneDottoreAsync";
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
}
