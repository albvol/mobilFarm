package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.FarmacoInScadenza;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.GestioneVMF;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.NuovoFarmaco;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.RicercaFarmaco;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.VMF;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.FarmacoPianoTerapeutico;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.PianiTerapeutici;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.PianoTerapeutico;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.NotificationManager;
import com.example.albertovolpe.mobilfarm.UtilityClient.NotificationMessage;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DottoreHome extends AppCompatActivity
        implements FarmacoInScadenza.OnFragmentInteractionListener,
        VMF.OnFragmentInteractionListener,
        Rubrica.OnFragmentInteractionListener,
        SchedaPaziente.OnFragmentInteractionListener,
        RicercaFarmaco.OnFragmentInteractionListener,
        NuovoFarmaco.OnFragmentInteractionListener,
        PianoTerapeutico.OnFragmentInteractionListener,
        PianiTerapeutici.OnFragmentInteractionListener,
        FarmacoPianoTerapeutico.OnFragmentInteractionListener,
        ProfiloDottore.OnFragmentInteractionListener {

    private static final String TAG = "DottoreHome";

    private static TextView title_bar;
    public static FragmentManager fragmentManager;
    private ImageView button_logout, button_back, button_home, button_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dottore_home);


        title_bar = (TextView) findViewById(R.id.title_bar);
        button_logout = (ImageView) findViewById(R.id.button_logout);
        button_back = (ImageView) findViewById(R.id.button_back);
        button_home = (ImageView) findViewById(R.id.button_home);
        button_profile = (ImageView) findViewById(R.id.button_profile);

        fragmentManager = getSupportFragmentManager();

        loadData();
        apriFarmaciInScadenza();
    }

    public void loadData(){

        try {
            new ListaFarmaciInScadenzaAsync()
                    .execute(GestioneVMF.elencoFarmaciInScadenza());

        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog("Errore Interno!");
        } catch (ActionNotAuthorizedException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog(e.getMessage());
        }
    }

    //Cambio il titolo presente nella barra di navigazione
    public static void cambiaTitoloSezione(String title) {
        title_bar.setText(title);
    }

    public void apriProfiloDottore(View view) {
        Fragment fragment = new ProfiloDottore();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Profilo").commit();

        showBackToHomeAndBackButtons();
    }

    public void home(View view){

        if(fragmentManager.getBackStackEntryCount() > 0)
        {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        hideBackToHomeAndBackButtons();
    }

    public void back(View view){

        onBackPressed();
    }

    public void apriRubrica(View view) {

        Fragment fragment = new Rubrica();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Rubrica").commit();

        showBackToHomeAndBackButtons();
    }

    public void apriVMF(View view) {

        apriVMF();
        showBackToHomeAndBackButtons();
    }

    public static void apriVMF() {
        Fragment fragment = new VMF();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("virtual mobilFarm").commit();
    }

    public void apriCercaFarmaco(View view) {
        Fragment fragment = new RicercaFarmaco();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Cerca Farmaco").commit();

        showBackToHomeAndBackButtons();
    }

    public void apriNuovoPianoTerapeutico(View view){

        PianoTerapeutico fragment = new PianoTerapeutico();
        fragment.setInfo(null, null, null, true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Piano Terapeutico").commit();

        showBackToHomeAndBackButtons();
    }

    public void showBackToHomeAndBackButtons(){

        button_profile.setVisibility(View.GONE);
        button_logout.setVisibility(View.GONE);

        button_back.setVisibility(View.VISIBLE);
        button_home.setVisibility(View.VISIBLE);
    }

    public void hideBackToHomeAndBackButtons(){

        button_profile.setVisibility(View.VISIBLE);
        button_logout.setVisibility(View.VISIBLE);

        button_back.setVisibility(View.GONE);
        button_home.setVisibility(View.GONE);
    }

    public void apriFarmaciInScadenza(){

        FarmacoInScadenza fragment = new FarmacoInScadenza();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).commit();
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() == 1) hideBackToHomeAndBackButtons();

        super.onBackPressed();
    }

    public void logout(View view) {
        try {
            new DottoreHomeAsync().execute(GestioneAccount.logout());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ActionNotAuthorizedException e) {
            e.printStackTrace();
        }
    }

    private void openLoginHome() {

        Intent i = new Intent(this, LoginHome.class);
        startActivity(i);
        finish();
    }

    private void showAlertDialog(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    @Override
    protected void onDestroy() {
        /* Elimino i cookie */
        mobilFarmServer.deleteCookie();

        /* Elimino le shared Preferences */
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.remove("Type");
        editor.remove("UserID");
        editor.commit();

        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class DottoreHomeAsync extends AsyncTask<JSONObject, Void, JSONObject> {
        private static final String TAG = "DottoreHomeAsync";
        private static final String SECTION = "GestoreAccountServer/AccountManager";

        private String action;

        @Override
        protected JSONObject doInBackground(JSONObject... params) {

            try {

                action = params[0].getString("action");
                return mobilFarmServer.connect(SECTION, params[0]);

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            } catch (ConnectionErrorException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {
                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                //Apro la home
                openLoginHome();

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            } catch (ResponseErrorException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog("Errore Interno!");
            } catch (ActionNotAuthorizedException e) {
                Log.e(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private TextView t;

        public void setInfo(TextView t) {
            this.t = t;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            t.setText(hourOfDay + ":" + minute);
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView t;

        public void setInfo(TextView t) {
            this.t = t;
        }
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

            t.setText(year+"/"+(month+1)+"/"+day);
        }
    }

    private class ListaFarmaciInScadenzaAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "ListaFarmaciInScadenzaAsync";
        private static final String SECTION = "GestoreFarmacoServer/VMFManager";

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
                if(result.getString("status").equals("error"))
                {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if(action.equals("elencoFarmaciInScadenza")){

                    JSONObject data = result.getJSONObject("data");

                    if(!data.getString("count").equals("0")) {
                        JSONArray lista_scadenze = data.getJSONArray("lista_scadenze");

                        HashMap<String, ArrayList<JSONObject>> hashMap = new HashMap<String, ArrayList<JSONObject>>();
                        for (int index = 0; index < lista_scadenze.length(); index++) {

                            String x = ((JSONObject) lista_scadenze.get(index)).getString("Scadenza");
                            if (hashMap.containsKey(x)) {
                                hashMap.get(x).add(lista_scadenze.getJSONObject(index));
                            } else {
                                ArrayList<JSONObject> arrayList = new ArrayList<>();
                                arrayList.add(lista_scadenze.getJSONObject(index));
                                hashMap.put(x, arrayList);
                            }
                        }

                        String key;
                        for (int index = 0; index < hashMap.size(); index++) {
                            key = ((JSONObject) lista_scadenze.get(index)).getString("Scadenza");
                            NotificationManager.setFarmaciInScadenza(hashMap.get(key), getApplicationContext(), key);
                        }
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
