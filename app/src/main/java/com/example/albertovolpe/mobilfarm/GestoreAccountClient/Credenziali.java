package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Credenziali extends AppCompatActivity {

    private static final String TAG = "Credenziali";
    private static EditText editText_password, editText_password_repeat, editText_mail;
    private static TextView textView_error_message;
    private static Spinner spinner_account_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credenziali);

        //Salvo i reference ai campi
        textView_error_message = (TextView) findViewById(R.id.error_message);
        editText_mail = (EditText) findViewById(R.id.credenziali_mail);
        editText_password = (EditText) findViewById(R.id.credenziali_password);
        editText_password_repeat = (EditText) findViewById(R.id.credenziali_password_repeat);
        spinner_account_type = (Spinner) findViewById(R.id.account_type);

        List<String> spinner_data = new ArrayList<>();
        spinner_data.add("Tipologia di Account");
        spinner_data.add("Paziente");
        spinner_data.add("Dottore");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinner_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_account_type.setAdapter(adapter);

        editText_password.setTransformationMethod(new PasswordTransformationMethod());
        editText_password_repeat.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void registratiStepTwo(View v){

        //Svuoto la textView per gli errori
        resetErrorTextView();

        try
        {
            //Disabilito la modifica dei campi
            disableFields();

            //Verifico se esiste già un account con questa mail
            new CredenzialiAsync()
                    .execute(GestioneAccount.controlloCredenziali(editText_mail.getText().toString()));

        }catch (ErrorValuesException e){
            showErrorTextView(e.getMessage());
            resetPasswordEditText();
            enableFields();
        }catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
            enableFields();
        }
         catch(Exception e) {
             Log.e(TAG, e.getMessage());
             showAlertDialog("Errore interno!");
             enableFields();
         }
    }

    private void openStepTwo(){

        Intent i;

        if(spinner_account_type.getSelectedItemPosition() == 2)
            i = new Intent(this, RegistrazioneDottore.class);
        else
            i = new Intent(this, RegistrazionePaziente.class);

        //Passo la mail e la password alla prossima activity
        i.putExtra("Mail", editText_mail.getText().toString());
        i.putExtra("Password", editText_password.getText().toString());

        enableFields();
        startActivity(i);
    }

    //Abilito le editext della activity
    private void enableFields(){

        editText_password_repeat.setBackgroundResource(R.drawable.field_editable);
        editText_password.setBackgroundResource(R.drawable.field_editable);
        editText_mail.setBackgroundResource(R.drawable.field_editable);

        editText_password_repeat.setEnabled(true);
        editText_password.setEnabled(true);
        editText_mail.setEnabled(true);
    }

    //Disabilito le editext della activity
    private void disableFields(){

        editText_password_repeat.setBackgroundResource(R.drawable.field_blocked);
        editText_password.setBackgroundResource(R.drawable.field_blocked);
        editText_mail.setBackgroundResource(R.drawable.field_blocked);

        editText_password_repeat.setEnabled(false);
        editText_password.setEnabled(false);
        editText_mail.setEnabled(false);
    }

    //Mostro un messaggio di errore
    private void showErrorTextView(String message){

        textView_error_message.setText(message);
    }

    //Resetto la textview del messaggio di errore
    private void resetErrorTextView(){

        textView_error_message.setText("");
    }

    //Resetto i campi edittext delle password
    private void resetPasswordEditText(){

        editText_password.setText("");
        editText_password_repeat.setText("");
    }

    //Resetto il campo edittext della mail
    private void resetEmailEditText(){

        editText_mail.setText("");
        editText_mail.setText("");
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

    public void openLoginHome(View view)
    {
        resetErrorTextView();
        resetPasswordEditText();
        resetEmailEditText();

        super.onBackPressed();
    }

    private class CredenzialiAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "CredenzialiAsync";
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

                if(result.getString("status").equals("error")){

                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if(result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                JSONObject data = result.getJSONObject("data");
                if (action.equals("controlloCredenziali")) {

                    if(!data.getBoolean("mail_presente")){

                        //Controllo se la password è stata inserita in maniera corretta
                        String password = editText_password.getText().toString();
                        if(password.length()< 6) throw new ErrorValuesException("La password deve essere lunga dai 6 ai 15 caratteri");

                        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(password);
                        if(m.find()) throw new ErrorValuesException("La password può contenere solo lettere maiuscole, minuscole e numeri!”");
                        if(!password.equals(editText_password_repeat.getText().toString())) throw new ErrorValuesException("Le password non corrispondono!");

                        if(spinner_account_type.getSelectedItemPosition() == 0) throw new ErrorValuesException("Seleziona la tipologia di account!");

                        openStepTwo();
                    }
                    else{
                        showErrorTextView("La mail appartiene ad un account già esistente!");
                        resetEmailEditText();
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
            } catch (ErrorValuesException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            } finally {
                enableFields();
            }

        }
    }
}
