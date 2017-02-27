package com.example.albertovolpe.mobilfarm.GestorePianoClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestoreAccountClient.GestioneRubrica;
import com.example.albertovolpe.mobilfarm.GestoreAccountClient.PazienteHome;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome.cambiaTitoloSezione;

public class PianoTerapeutico extends Fragment {

    private static final String TAG = "PianoTerapeutico";
    private static SharedPreferences sharedPreferences;
    private OnFragmentInteractionListener mListener;

    private View layout;
    private TextView feedback_message;
    private EditText text_nome_piano;
    private Spinner spinner_pazienti;
    private Button button_load_second_section, button_add;
    private LinearLayout second_section, first_section;
    private String IDTerapia, nomePiano, userType, pazienteID, dottoreID, userID;
    private ListView farmaci_piano;
    private Boolean isNew;

    public PianoTerapeutico() {
        // Required empty public constructor
    }

    public static PianoTerapeutico newInstance() {
        PianoTerapeutico fragment = new PianoTerapeutico();
        return fragment;
    }

    public void setInfo(String IDTerapia, String dottoreID, String nomePiano, Boolean isNew){
        Log.i(TAG, IDTerapia+" "+dottoreID+" "+nomePiano+" "+isNew);
        this.IDTerapia = IDTerapia;
        this.nomePiano = nomePiano;
        this.isNew = isNew;
        this.dottoreID = dottoreID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_piano_terapeutico, container, false);

        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        text_nome_piano = (EditText) layout.findViewById(R.id.text_nome_piano);
        spinner_pazienti = (Spinner) layout.findViewById(R.id.spinner_pazienti);
        button_load_second_section = (Button) layout.findViewById(R.id.button_load_second_section);
        button_add = (Button) layout.findViewById(R.id.button_add);
        second_section = (LinearLayout) layout.findViewById(R.id.second_section);
        first_section = (LinearLayout) layout.findViewById(R.id.first_section);
        button_add.setOnClickListener(addFarmaco);
        farmaci_piano = (ListView) layout.findViewById(R.id.farmaci_piano);

        //Leggo il tipo di utente loggato
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userType = sharedPreferences.getString("Type", "Utente non identificato");
        userID = sharedPreferences.getString("UserID", "Utente non identificato");

        if(userType.compareTo("Dottore") == 0) DottoreHome.cambiaTitoloSezione("Piano Terapeutico");
        else if(userType.compareTo("Paziente") == 0)PazienteHome.cambiaTitoloSezione("Piano Terapeutico");

        if(isNew || ((userType.compareTo("Dottore") == 0) && (userID.compareTo(dottoreID) == 0))) gestisciPianoDottore();
        else visualizzaPianoPaziente();
        return layout;
    }

    private View.OnClickListener addFarmaco = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        cambiaTitoloSezione("Farmaco del Piano");

        FarmacoPianoTerapeutico fragment = new FarmacoPianoTerapeutico();
        fragment.setInfo(IDTerapia, userID, nomePiano);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.section_opened, fragment).commit();
        }
    };

    private View.OnClickListener loadTherapy = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        try {
            disableFields();
            new PianoTerapeuticoAsync().execute(
                    GestionePianoTerapeutico.creaPianoTerapeutico(text_nome_piano.getText().toString(), pazienteID));
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            showFeedbackTextView("Errore Interno!");
            enableFields();
        } catch (ActionNotAuthorizedException e) {
            Log.i(TAG, e.getMessage());
            showFeedbackTextView(e.getMessage());
            enableFields();
        } catch (ErrorValuesException e) {
            Log.i(TAG, e.getMessage());
            showFeedbackTextView(e.getMessage());
            enableFields();
        }
        }
    };

    private void visualizzaPianoPaziente()
    {
        try
        {
            new PianoTerapeuticoAsync().execute(
                    GestionePianoTerapeutico.visualizzaPianoTerapeutico(IDTerapia));

            first_section.setVisibility(View.VISIBLE);
            text_nome_piano.setVisibility(View.VISIBLE);
            text_nome_piano.setEnabled(false);
            text_nome_piano.setBackgroundResource(R.drawable.field_blocked);
            text_nome_piano.setText(nomePiano);
            spinner_pazienti.setVisibility(View.GONE);
            second_section.setVisibility(View.VISIBLE);
            button_load_second_section.setVisibility(View.GONE);
            button_add.setVisibility(View.GONE);

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

    private void gestisciPianoDottore()
    {
        try {
            if(isNew)
            {
                first_section.setVisibility(View.VISIBLE);
                new ListaPazientiAsync().execute(
                        GestioneRubrica.visualizzaRubricaPazienti());

                button_load_second_section.setVisibility(View.VISIBLE);
                button_load_second_section.setOnClickListener(loadTherapy);

            }else{

                new PianoTerapeuticoAsync().execute(
                        GestionePianoTerapeutico.visualizzaPianoTerapeutico(IDTerapia));

                first_section.setVisibility(View.VISIBLE);
                text_nome_piano.setVisibility(View.VISIBLE);
                text_nome_piano.setEnabled(false);
                text_nome_piano.setBackgroundResource(R.drawable.field_blocked);
                text_nome_piano.setText(nomePiano);
                spinner_pazienti.setVisibility(View.GONE);
                second_section.setVisibility(View.VISIBLE);
                button_load_second_section.setVisibility(View.GONE);
                button_add.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog("Errore Interno!");
        } catch (ActionNotAuthorizedException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog("Errore Interno!");
        } catch (ErrorValuesException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog(e.getMessage());
        }
    }

    private void showPianoData(ArrayList<JSONObject> lista_farmaci)
    {
        ListaFarmaciPianoAdapter adapter = new ListaFarmaciPianoAdapter(layout.getContext(), R.layout.farmaco_piano, lista_farmaci);
        farmaci_piano.setAdapter(adapter);
        farmaci_piano.setOnItemClickListener(showDettagliAssunzione);
    }

    private void showSpinnerPazienti(final ArrayList<JSONObject> lista_pazienti){

        List<String> nomi = new ArrayList<>();

        try {
            for(int i=0; i<lista_pazienti.size(); i++)
            nomi.add(i, lista_pazienti.get(i).getString("Nome")+" "+lista_pazienti.get(i).getString("Cognome")+" ("+lista_pazienti.get(i).getString("CittaResidenza")+")");
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            showAlertDialog("Errore Interno!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, nomi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pazienti.setAdapter(adapter);
        spinner_pazienti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    pazienteID = lista_pazienti.get(position).getString("UserID");
                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                    showAlertDialog("Errore Interno!");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public AdapterView.OnItemClickListener showDettagliAssunzione = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            try {
                if(userType.compareTo("Dottore") == 0)
                    DottoreHome.cambiaTitoloSezione("Farmaco del Piano");
                else if(userType.compareTo("Paziente") == 0)
                    PazienteHome.cambiaTitoloSezione("Farmaco del Piano");

                FarmacoPianoTerapeutico fragment = new FarmacoPianoTerapeutico();
                fragment.setInfo(IDTerapia, dottoreID, nomePiano);
                fragment.setIDFarmaco(((JSONObject) farmaci_piano.getItemAtPosition(position)).getString("IDFarmaco"), ((JSONObject) farmaci_piano.getItemAtPosition(position)).getString("Nome"), ((JSONObject) farmaci_piano.getItemAtPosition(position)).getString("Composizione"));

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.section_opened, (Fragment)fragment);
                fragmentTransaction.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void enableFields()
    {
        text_nome_piano.setBackgroundResource(R.drawable.field_editable);
        spinner_pazienti.setBackgroundResource(R.drawable.field_editable);

        text_nome_piano.setEnabled(true);
        spinner_pazienti.setEnabled(true);
    }

    public void disableFields(){

        text_nome_piano.setBackgroundResource(R.drawable.field_blocked);
        spinner_pazienti.setBackgroundResource(R.drawable.field_blocked);

        text_nome_piano.setEnabled(false);
        spinner_pazienti.setEnabled(false);
    }

    //Mostro un messaggio
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
    }

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
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


    public class PianoTerapeuticoAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "PianoTerapeuticoAsync";
        private static final String SECTION = "GestorePianoServer/PianoTerapeuticoManager";

        private String action;

        @Override
        protected void onPreExecute() {

            showFeedbackTextView("Carico...");
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
                if(result.getString("status").equals("error")) {

                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else
                        throw new ResponseErrorException(result.getString("message"));

                }

                if(action.equals("creaPianoTerapeutico")){

                    IDTerapia = result.getString("data");
                    nomePiano = text_nome_piano.getText().toString();

                    second_section.setVisibility(View.VISIBLE);
                    button_load_second_section.setVisibility(View.GONE);
                    showFeedbackTextView("");

                }else if(action.equals("visualizzaPianoTerapeutico")){

                    JSONObject data = result.getJSONObject("data");

                    if(data.getInt("count") == 0) showFeedbackTextView("Farmaci non presenti!");
                    else{

                        showFeedbackTextView("");
                        JSONArray lista = data.getJSONArray("lista_farmaci");

                        ArrayList<JSONObject> list = new ArrayList<>();
                        for (int index = 0; index < lista.length(); index++)
                            list.add(lista.getJSONObject(index));

                        showPianoData(list);
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

    private class ListaPazientiAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "ListaPazientiAsync";
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
                if (action.equals("visualizzaRubricaPazienti")){

                    if(data.getString("count").equals("0"))
                        showFeedbackTextView("Nessun contatto presente...\n\nNon puoi cerare una nuova terapia.");
                    else{

                        JSONArray lista_pazienti = data.getJSONArray("lista_pazienti");

                        ArrayList<JSONObject> list = new ArrayList<>();

                        JSONObject firstElem = new JSONObject();
                        firstElem.put("Nome", "Seleziona");
                        firstElem.put("Cognome", "Paziente");
                        firstElem.put("CittaResidenza", "");
                        firstElem.put("UserID", "");
                        list.add(firstElem);
                        for (int index = 0; index < lista_pazienti.length(); index++)
                            list.add(lista_pazienti.getJSONObject(index));

                        showSpinnerPazienti(list);
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
