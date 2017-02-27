package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.GestioneVMF;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.RicercaFarmaco;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.VMF;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.FarmacoDaAssumere;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.FarmacoPianoTerapeutico;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.GestionePianoTerapeutico;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.PianiTerapeutici;
import com.example.albertovolpe.mobilfarm.GestorePianoClient.PianoTerapeutico;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.NotificationManager;
import com.example.albertovolpe.mobilfarm.UtilityClient.NotificationMessage;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.UIManager;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PazienteHome extends AppCompatActivity
        implements FarmacoDaAssumere.OnFragmentInteractionListener,
        VMF.OnFragmentInteractionListener,
        Rubrica.OnFragmentInteractionListener ,
        PianiTerapeutici.OnFragmentInteractionListener,
        RicercaDottore.OnFragmentInteractionListener,
        RicercaFarmaco.OnFragmentInteractionListener,
        SchedaDottore.OnFragmentInteractionListener,
        FarmacoPianoTerapeutico.OnFragmentInteractionListener,
        PianoTerapeutico.OnFragmentInteractionListener,
        ModalitaSOS.OnFragmentInteractionListener,
        ProfiloPaziente.OnFragmentInteractionListener{

    private static final String TAG = "PazienteHome";

    private static TextView title_bar;
    private static FragmentManager fragmentManager;

    private static LinearLayout section_sos;
    private ImageView button_logout, button_back, button_home, button_profile;
    public static Boolean isSOSOpened;

    public void loadData(){

        try {
            new ListaFarmaciInScadenzaAsync()
                    .execute(GestioneVMF.elencoFarmaciInScadenza());
            new ListaAssunzioniPianiAsync()
                    .execute(GestionePianoTerapeutico.elencoAssunzioni());

        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog("Errore Interno!");
        } catch (ActionNotAuthorizedException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog(e.getMessage());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paziente_home);

        title_bar = (TextView) findViewById(R.id.title_bar);
        fragmentManager = getSupportFragmentManager();

        button_logout = (ImageView) findViewById(R.id.button_logout);
        button_back = (ImageView) findViewById(R.id.button_back);
        button_home = (ImageView) findViewById(R.id.button_home);
        button_profile = (ImageView) findViewById(R.id.button_profile);

        section_sos = (LinearLayout) findViewById(R.id.section_sos);

        isSOSOpened = false;
        ((LinearLayout) findViewById(R.id.button_element_sos)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction()==MotionEvent.ACTION_DOWN)
                    apriModalitaSOS();
                else if(arg1.getAction()==MotionEvent.ACTION_UP)
                    chiudiModalitaSOS();
                return true;
                }
        });

        loadData();
        apriFarmaciDaAssumere();
    }

    public void apriFarmaciDaAssumere(){

        FarmacoDaAssumere fragment = new FarmacoDaAssumere();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).commit();
    }

    //Cambio il titolo presente nella barra di navigazione
    public static void cambiaTitoloSezione(String title){
        title_bar.setText(title);
    }

    public void apriProfiloPaziente(View view)
    {
        Fragment fragment = new ProfiloPaziente();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened,fragment).addToBackStack("Profilo").commit();

        showBackToHomeAndBackButtons();
    }

    public void apriCercaDottore(View view)
    {
        Fragment fragment = new RicercaDottore();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened,fragment).addToBackStack("Cerca Dottore").commit();

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

    public void apriCercaFarmaco(View view)
    {
        Fragment fragment = new RicercaFarmaco();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Cerca Farmaco").commit();

        showBackToHomeAndBackButtons();
    }

    public void apriRubrica(View view)
    {
        Fragment fragment = new Rubrica();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened,fragment).addToBackStack("Rubrica").commit();

        showBackToHomeAndBackButtons();
    }

    public void apriVMF(View view)
    {
        apriVMF();
        showBackToHomeAndBackButtons();
    }

    public static void apriVMF() {
        Fragment fragment = new VMF();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("virtual mobilFarm").commit();
    }

    public void apriPianiTerapeutici(View view)
    {
        Fragment fragment = new PianiTerapeutici();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Piani Terapeutici").commit();

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


    public void apriModalitaSOS()
    {
        section_sos.setVisibility(View.VISIBLE);

        findViewById(R.id.button_element_rubrica).setVisibility(View.GONE);
        findViewById(R.id.button_element_piani).setVisibility(View.GONE);
        findViewById(R.id.button_element_vmf).setVisibility(View.GONE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.button_element_sos);
        layout.setLayoutParams(new LinearLayout.LayoutParams(UIManager.dpToPixel(136), UIManager.dpToPixel(136)));

        ImageView sos= (ImageView)findViewById(R.id.sos);
        sos.setScaleX(2);
        sos.setScaleY(2);

        Fragment fragment = new ModalitaSOS();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        isSOSOpened = true;
        fragmentTransaction.replace(R.id.section_sos,fragment).commit();
    }

    public void chiudiModalitaSOS()
    {
        isSOSOpened = false;
        LinearLayout layout = (LinearLayout) findViewById(R.id.button_element_sos);
        layout.setLayoutParams(new LinearLayout.LayoutParams(UIManager.dpToPixel(68), UIManager.dpToPixel(68)));

        section_sos.removeAllViews();
        section_sos.setVisibility(View.GONE);

        ImageView sos= (ImageView)findViewById(R.id.sos);
        sos.setScaleX(1);
        sos.setScaleY(1);

        findViewById(R.id.button_element_rubrica).setVisibility(View.VISIBLE);
        findViewById(R.id.button_element_piani).setVisibility(View.VISIBLE);
        findViewById(R.id.button_element_vmf).setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() == 1) hideBackToHomeAndBackButtons();
        super.onBackPressed();
    }

    public void logout(View view)
    {
        try {
            new PazienteHomeAsync().execute(GestioneAccount.logout());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ActionNotAuthorizedException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    private void openLoginHome(){

        Intent i = new Intent(this, LoginHome.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy(){
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

    private class PazienteHomeAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "PazienteHomeAsync";
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
                if(result.getString("status").equals("error"))
                {
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
                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if(result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if(action.equals("elencoFarmaciInScadenza")){

                    JSONObject data = result.getJSONObject("data");

                    if(data.getInt("count")!=0) {
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

    private class ListaAssunzioniPianiAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "ListaAssunzioniPianiAsync";
        private static final String SECTION = "GestorePianoServer/PianoTerapeuticoManager";

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

                if(action.equals("elencoAssunzioni")){

                    JSONObject data = result.getJSONObject("data");

                    if(data.getInt("count")!=0) {

                        JSONArray lista_assunzioni = data.getJSONArray("lista_assunzioni");

                        ArrayList<JSONObject> list = new ArrayList<>();
                        for (int index = 0; index < data.getInt("count"); index++) {
                            list.add(lista_assunzioni.getJSONObject(index));
                        }

                        FarmacoDaAssumere.mostraAssunzioniDellaGiornata(list);
                        NotificationManager.setFarmaciDaAssumere(list, getApplicationContext());
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
