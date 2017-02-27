package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.GestioneFarmaco;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.GestioneVMF;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.GestionePianoTerapeutico;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.UIManager;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginHome extends AppCompatActivity {

    private static final String TAG = "LoginHome";

    private EditText editText_mail, editText_password;
    private TextView textView_error_message;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new UIManager(this);
        setContentView(R.layout.activity_login_home);

        editText_mail = (EditText) findViewById(R.id.login_mail);
        editText_password = (EditText) findViewById(R.id.login_password);
        textView_error_message = (TextView) findViewById(R.id.error_message);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(getIntent().getStringExtra("Mail") != null){

            showErrorTextView("Registrazione avvenuta con successo!\nProcedi con il login");
            editText_mail.setText(getIntent().getStringExtra("Mail"));
            editText_password.setText(getIntent().getStringExtra("Password"));
        }

        new mobilFarmServer(this);
        new GestioneAccount(this);
        new GestioneRubrica(this);
        new GestioneFarmaco(this);
        new GestioneVMF(this);
        new GestionePianoTerapeutico(this);

    }

    // Effettuo il login
    public void login(View v){

        resetErrorTextView();

        try
        {
            disableFields();
            new LoginHomeAsync()
                    .execute(GestioneAccount.login(editText_mail.getText().toString(), editText_password.getText().toString()));

        } catch (ErrorValuesException e) {
            showErrorTextView(e.getMessage());
            enableFields();
        } catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
            enableFields();
        }catch(Exception e) {
            showAlertDialog("Errore interno!");
            enableFields();
        }
    }

    //Apro l'activity credenziali
    public void registrati(View v){

        Intent i = new Intent(this, Credenziali.class);
        startActivity(i);
    }

    //Mostro un messaggio di errore
    private void showErrorTextView(String message){

        textView_error_message.setText(message);
    }

    //Resetto la textview del messaggio di errore
    private void resetErrorTextView(){

        textView_error_message.setText("");
    }

    //Resetto i campi edittext
    private void resetEditText(){

        editText_mail.setText("");
        editText_password.setText("");
    }

    private void showAlertDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    //Apro l'activity HOME dell'account
    public void openAccount(String userID, String type, String nome, String cognome){

        resetEditText();
        Intent i;

        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        sharedPreferences.putString("UserID", userID);
        sharedPreferences.putString("Type", type);
        sharedPreferences.putString("Nome", nome);
        sharedPreferences.putString("Cognome", cognome);
        sharedPreferences.commit();

        if(type.equals("Dottore"))
        {
            i = new Intent(this, DottoreHome.class);
            startActivity(i);
            finish();

        }else if(type.equals("Paziente")){

            try {
                new LoginHomeAsync()
                        .execute(GestioneAccount.modalitaSOS());

                i = new Intent(this, PazienteHome.class);
                startActivity(i);
            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }else showAlertDialog("Errore Interno!");

    }

    //Abilito le editext della activity
    private void enableFields(){

        editText_password.setBackgroundResource(R.drawable.field_editable);
        editText_mail.setBackgroundResource(R.drawable.field_editable);

        editText_password.setEnabled(true);
        editText_mail.setEnabled(true);
    }

    //Disabilito le editext della activity
    private void disableFields(){

        editText_password.setBackgroundResource(R.drawable.field_blocked);
        editText_mail.setBackgroundResource(R.drawable.field_blocked);

        editText_password.setEnabled(false);
        editText_mail.setEnabled(false);
    }

    private class LoginHomeAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "LoginHomeAsync";
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

                JSONObject data = result.getJSONObject("data");
                if (action.equals("login"))
                {
                    if (data.getBoolean("credenziali_corrette"))
                        openAccount(data.getString("UserID"), data.getString("Type"), data.getString("Nome"), data.getString("Cognome"));
                    else
                        showErrorTextView("Mail o password errate!");
                }else if(action.equals("modalitaSOS")){

                    sharedPreferences.edit().putString("NumeroSOS1", data.getString("NumeroSOS1")).commit();
                    sharedPreferences.edit().putString("NumeroSOS2", data.getString("NumeroSOS2")).commit();
                    finish();
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
                resetEditText();
                enableFields();
            }

        }
    }
}
