package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ListaDottoriAdapter;
import com.example.albertovolpe.mobilfarm.UtilityClient.ListaPazientiAdapter;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Rubrica extends Fragment {

    private static final String TAG = "Rubrica";
    private static SharedPreferences sharedPreferences;

    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private TextView feedback_message;
    private View layout;
    private EditText text_filter;

    public Rubrica() {
        // Required empty public constructor
    }

    public static Rubrica newInstance() {
        Rubrica fragment = new Rubrica();

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
        layout = inflater.inflate(R.layout.fragment_rubrica, container, false);

        //Salvo i reference delle view
        listView = (ListView) layout.findViewById(R.id.lista_contatti);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        text_filter = (EditText) layout.findViewById(R.id.text_filter);

        new GestioneRubrica(getContext());

        //Leggo il tipo di utente loggato
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userType = sharedPreferences.getString("Type", "Utente non identificato");

        try {

            //Carico la rubrica
            if (userType.equals("Dottore")){

                DottoreHome.cambiaTitoloSezione("Rubrica");
                layout.findViewById(R.id.button_cerca_dottore).setVisibility(View.GONE);
                new RubricaAsync().execute(GestioneRubrica.visualizzaRubricaPazienti());

            }else if (userType.equals("Paziente")){

                PazienteHome.cambiaTitoloSezione("Rubrica");
                new RubricaAsync().execute(GestioneRubrica.visualizzaRubricaDottori());
            }

        }catch (ActionNotAuthorizedException e) {
            showAlertDialog(e.getMessage());
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
            showAlertDialog("Errore interno!");
        }

        return layout;
    }

    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
        feedback_message.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    //Nascondo il messaggio di feedback
    private void hideFeedbackTextView(){
        feedback_message.setVisibility(View.GONE);
    }

    private void showListaDottori(final ArrayList lista){

        //Imposto l'adapter
        final ListaDottoriAdapter adapter = new ListaDottoriAdapter(layout.getContext(), R.layout.contatto_dottore, lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    showDottore(false, ((JSONObject) lista.get(position)).getString("UserID"));
                } catch (JSONException e) {
                    e.getMessage();
                    showAlertDialog("Errore Interno!");
                }
            }
        });

        feedback_message.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        text_filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void showDottore(Boolean isNew, String userID)
    {
        SchedaDottore schedaDottore = new SchedaDottore();
        schedaDottore.setInfo(isNew, userID);

        FragmentTransaction fragmentTransaction = super.getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.section_opened, schedaDottore).addToBackStack("Scheda Dottore").commit();
    }

    public void showPaziente(String userID)
    {
        SchedaPaziente schedaPaziente = new SchedaPaziente();
        schedaPaziente.setInfo(userID);

        FragmentTransaction fragmentTransaction = super.getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.section_opened, schedaPaziente).addToBackStack("Scheda Paziente").commit();
    }

    private void showListaPazienti(final ArrayList lista){

        //Imposto l'adapter
        final ListaPazientiAdapter adapter = new ListaPazientiAdapter(layout.getContext(), R.layout.contatto_paziente, lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    showPaziente(((JSONObject) lista.get(position)).getString("UserID"));
                } catch (JSONException e) {
                    e.getMessage();
                    showAlertDialog("Errore Interno!");
                }
            }
        });

        feedback_message.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        text_filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    private class RubricaAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "RubricaAsync";
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
                    else
                        throw new ResponseErrorException(result.getString("message"));

                }
                
                JSONObject data = result.getJSONObject("data");
                if(data.getString("count").equals("0")) {

                    if(action.equals("visualizzaRubricaDottori"))
                        showFeedbackTextView("Nessun Dottore presente...\n\nPremi il tasto + per ricercare\ne aggiungere i tuoi dottori.");
                    else if(action.equals("visualizzaRubricaPazienti"))
                        showFeedbackTextView("Nessun contatto presente...\n\nInforma i tuoi pazienti di\naggiungerti su mobilFarm.");
                }else if (action.equals("visualizzaRubricaDottori"))
                {
                    JSONArray lista_dottori = data.getJSONArray("lista_dottori");

                    ArrayList<JSONObject> list = new ArrayList<>();
                    for (int index = 0; index < lista_dottori.length(); index++)
                        list.add(lista_dottori.getJSONObject(index));

                     showListaDottori(list);
                }else if (action.equals("visualizzaRubricaPazienti")){

                    JSONArray lista_pazienti = data.getJSONArray("lista_pazienti");

                    ArrayList<JSONObject> list = new ArrayList<>();
                    for (int index = 0; index < lista_pazienti.length(); index++)
                        list.add(lista_pazienti.getJSONObject(index));
                    showListaPazienti(list);
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
