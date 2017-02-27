package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestoreAccountClient.PazienteHome;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ListaFarmaciAdapter;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome.cambiaTitoloSezione;

public class RicercaFarmaco extends Fragment {

    private static final String TAG = "RicercaFarmaco";
    private static SharedPreferences sharedPreferences;
    private OnFragmentInteractionListener mListener;

    private EditText text_search;
    private ImageView button_new_farmaco;
    private View layout;
    private TextView feedback_message;
    private ListView content;
    private Button aggiungi_button;
    private String userType;

    public RicercaFarmaco() {
        // Required empty public constructor
    }

    public static RicercaFarmaco newInstance() {
        RicercaFarmaco fragment = new RicercaFarmaco();
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
        layout = inflater.inflate(R.layout.fragment_ricerca_farmaco, container, false);

        text_search = (EditText) layout.findViewById(R.id.text_search);
        text_search.addTextChangedListener(search);
        button_new_farmaco = (ImageView) layout.findViewById(R.id.button_new_farmaco);
        button_new_farmaco.setOnClickListener(showNewFarmaco);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        content = (ListView) layout.findViewById(R.id.lista_farmaci_trovati);
        aggiungi_button = (Button) layout.findViewById(R.id.aggiungi_button);

        aggiungi_button.setOnClickListener(showNewFarmaco);

        //Leggo il tipo di utente loggato
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userType = sharedPreferences.getString("Type", "Utente non identificato");

        if(userType.compareTo("Dottore") == 0)
        {
            button_new_farmaco.setVisibility(View.VISIBLE);
            DottoreHome.cambiaTitoloSezione("Cerca Farmaco");
        }else if(userType.compareTo("Paziente") == 0){
            button_new_farmaco.setVisibility(View.GONE);
            PazienteHome.cambiaTitoloSezione("Cerca Farmaco");
        }

        return layout;
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

    private TextWatcher search = new TextWatcher() {
        public void afterTextChanged(Editable s) {

            try
            {
                new RicercaFarmacoAsync().execute(
                        GestioneFarmaco.ricercaFarmaco(text_search.getText().toString()));
            } catch (ErrorValuesException e) {
                Log.i(TAG, e.getMessage());
                showFeedbackTextView(e.getMessage());
                //enableFields();
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
                //enableFields();
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog("Errore interno!");
                //enableFields();
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };



    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
        feedback_message.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
    }

    //Nascondo il messaggio di feedback
    private void hideFeedbackTextView(){
        feedback_message.setVisibility(View.GONE);
        aggiungi_button.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener showNewFarmaco = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideFeedbackTextView();
            apriNuovoFarmaco(v);
        }
    };

    public void apriNuovoFarmaco(View view){

        cambiaTitoloSezione("Nuovo Farmaco");

        NuovoFarmaco nuovoFarmaco = new NuovoFarmaco();
        nuovoFarmaco.setInfo(text_search.getText().toString());

        Fragment fragment = nuovoFarmaco;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.section_opened,fragment).addToBackStack("Ricerca Farmaco").commit();
    }

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    private void showListaFarmaci(final ArrayList<JSONObject> lista_farmaci){

        //Imposto l'adapter
        ListaFarmaciAdapter adapter = new ListaFarmaciAdapter(layout.getContext(), R.layout.farmaco, lista_farmaci);
        content.setAdapter(adapter);
        content.setOnItemClickListener(open_data_picker);

        hideFeedbackTextView();
    }

    private AdapterView.OnItemClickListener open_data_picker = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(((TextView) view.findViewById(R.id.scadenza_value)).getText().toString().isEmpty()){
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setInfo(view);
                DialogFragment newFragment = datePickerFragment;
                newFragment.show(getFragmentManager(), "dataPicker");
            }else{
                try {
                    new AggiungiFarmacoVMFAsync().execute(
                            GestioneVMF.aggiungiFarmacoVMF(((JSONObject) content.getItemAtPosition(position)).getString("IDFarmaco"), ((TextView) view.findViewById(R.id.scadenza_value)).getText().toString()));
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
        }
    };


    private class RicercaFarmacoAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "RicercaFarmacoAsync";
        private static final String SECTION = "GestoreFarmacoServer/FarmacoManager";

        private String action;

        @Override
        protected void onPreExecute() {
            showFeedbackTextView("Ricerca in corso...");
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
                if(result == null) throw new ResponseErrorException("Errore nel Server!");

                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if (action.equals("ricercaFarmaco"))
                {
                    JSONObject data = result.getJSONObject("data");
                    if(data.getInt("count") == 0) {
                        showFeedbackTextView("Nessun farmaco trovato...");

                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String userType = sharedPreferences.getString("Type", "Utente non identificato");
                        if(userType.equals("Dottore")) aggiungi_button.setVisibility(View.VISIBLE);
                    }else{

                        JSONArray lista_dottori = data.getJSONArray("lista_farmaci");

                        ArrayList<JSONObject> list = new ArrayList<>();
                        for (int index = 0; index < lista_dottori.length(); index++)
                            list.add(lista_dottori.getJSONObject(index));

                        showListaFarmaci(list);
                    }

                }else if(action.equals("aggiungiFarmacoVMF")) getActivity().onBackPressed();


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
    private class AggiungiFarmacoVMFAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "AggiungiFarmacoVMFAsync";
        private static final String SECTION = "GestoreFarmacoServer/VMFManager";

        private String action;

        @Override
        protected void onPreExecute() {
            showFeedbackTextView("Salvataggio in corso...");
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
                if(result == null) throw new ResponseErrorException("Errore nel Server!");

                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if(action.equals("aggiungiFarmacoVMF")){

                    if(userType.compareTo("Dottore") == 0) DottoreHome.apriVMF();
                    else if(userType.compareTo("Paziente") == 0) PazienteHome.apriVMF();
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private View v;
        public void setInfo(View v) {
            this.v = v;
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

            ((TextView) v.findViewById(R.id.scadenza_value)).setText(year+"/"+(month+1)+"/"+day);
            ((ImageView) v.findViewById(R.id.add)).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rounded_plus));
        }
    }

}
