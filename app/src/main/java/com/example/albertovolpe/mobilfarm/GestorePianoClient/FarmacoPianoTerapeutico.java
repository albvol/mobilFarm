package com.example.albertovolpe.mobilfarm.GestorePianoClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestoreFarmacoClient.GestioneFarmaco;
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

public class FarmacoPianoTerapeutico extends Fragment {

    private static final String TAG = "FarmacoPianoTerapeutico";
    private static SharedPreferences sharedPreferences;
    private OnFragmentInteractionListener mListener;

    private EditText text_nome_farmaco;
    private CheckBox lunedi, martedi, mercoledi, giovedi, venerdi, sabato, domenica;
    private TextView feedback_message, seleziona_farmaco_textvivew, text_data_termine, text_data_inizio, orario_inizio_farmaco;
    private View layout;
    private ListView lista_farmaci_trovati;
    private LinearLayout second_section;
    private Button button_save;
    private Spinner dosaggio, intervallo_farmaco;

    private String IDTerapia, IDFarmaco, NomeFarmaco, Composizione, userType, dottoreID, nomePiano;

    public FarmacoPianoTerapeutico() {
        // Required empty public constructor
    }

    public static FarmacoPianoTerapeutico newInstance() {
        FarmacoPianoTerapeutico fragment = new FarmacoPianoTerapeutico();
        return fragment;
    }

    public void setInfo(String IDTerapia, String dottoreID, String nomePiano){
        Log.i(TAG, IDTerapia +" "+dottoreID+" "+nomePiano);
        this.IDTerapia = IDTerapia;
        this.dottoreID = dottoreID;
        this.nomePiano = nomePiano;
    }

    public void setIDFarmaco(String IDFarmaco, String nomeFarmaco, String composizione){
        this.IDFarmaco = IDFarmaco;
        this.NomeFarmaco = nomeFarmaco;
        this.Composizione = composizione;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_farmaco_piano_terapeutico, container, false);

        text_nome_farmaco = (EditText) layout.findViewById(R.id.text_nome_farmaco);
        text_nome_farmaco.addTextChangedListener(search);
        lista_farmaci_trovati = (ListView) layout.findViewById(R.id.lista_farmaci_trovati);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        seleziona_farmaco_textvivew = (TextView) layout.findViewById(R.id.seleziona_farmaco_textvivew);
        second_section = (LinearLayout) layout.findViewById(R.id.second_section);
        button_save = (Button) layout.findViewById(R.id.button_save);
        button_save.setOnClickListener(action_save);
        lunedi = (CheckBox) layout.findViewById(R.id.lunedi);
        martedi = (CheckBox) layout.findViewById(R.id.martedi);
        mercoledi = (CheckBox) layout.findViewById(R.id.mercoledi);
        giovedi = (CheckBox) layout.findViewById(R.id.giovedi);
        venerdi = (CheckBox) layout.findViewById(R.id.venerdi);
        sabato = (CheckBox) layout.findViewById(R.id.sabato);
        domenica = (CheckBox) layout.findViewById(R.id.domenica);

        text_data_inizio = (TextView) layout.findViewById(R.id.text_data_inizio);
        text_data_termine = (TextView) layout.findViewById(R.id.text_data_termine);
        orario_inizio_farmaco = (TextView) layout.findViewById(R.id.orario_inizio_farmaco);

        dosaggio = (Spinner) layout.findViewById(R.id.dosaggio_farmaco);
        intervallo_farmaco = (Spinner) layout.findViewById(R.id.intervallo_farmaco);

        //Leggo il tipo di utente loggato
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userType = sharedPreferences.getString("Type", "Utente non identificato");

        if(userType.compareTo("Dottore") == 0){
            orario_inizio_farmaco.setOnClickListener(open_time_picker);
            text_data_inizio.setOnClickListener(open_data_picker);
            text_data_termine.setOnClickListener(open_data_picker);
        }

        if(IDFarmaco != null){

            try {
                showFeedbackTextView("Carico...");
                new FarmacoPianoTerapeuticoAsync().execute(
                        GestionePianoTerapeutico.visualizzaDettagliAssunzioneFarmaco(IDTerapia, IDFarmaco));
            } catch (JSONException e) {
                Log.i("TAG", e.getMessage());
                showAlertDialog("Errore Interno!");
            } catch (ActionNotAuthorizedException e) {
                Log.i("TAG", e.getMessage());
                showAlertDialog(e.getMessage());
            } catch (ErrorValuesException e) {
                Log.i("TAG", e.getMessage());
                showAlertDialog(e.getMessage());
            }
        }

        return layout;
    }

    private TextWatcher search = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            try
            {
                showFeedbackTextView("Cerco...");
                new RicercaFarmaciTerapiaAsync().execute(
                        GestioneFarmaco.ricercaFarmaco(text_nome_farmaco.getText().toString()));
            } catch (ErrorValuesException e) {
                Log.i(TAG, e.getMessage());
                showFeedbackTextView(e.getMessage());
                //enableFields();
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
                //enableFields();
            } catch (Exception e) {
                e.printStackTrace();
                showAlertDialog("Errore interno!");
                //enableFields();
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };


    private View.OnClickListener open_time_picker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DottoreHome.TimePickerFragment timePickerFragment = new DottoreHome.TimePickerFragment();
            timePickerFragment.setInfo((TextView) v);
            DialogFragment newFragment = timePickerFragment;
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    private View.OnClickListener open_data_picker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DottoreHome.DatePickerFragment datePickerFragment = new DottoreHome.DatePickerFragment();
            datePickerFragment.setInfo((TextView) v);
            DialogFragment newFragment = datePickerFragment;
            newFragment.show(getFragmentManager(), "dataPicker");
        }
    };

    private View.OnClickListener action_save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try{
                new FarmacoPianoTerapeuticoAsync().execute(
                        GestionePianoTerapeutico.aggiornaDettagliAssunzioneFarmaco(IDTerapia, IDFarmaco, dosaggio.getSelectedItem().toString().replace(" dose", "").replace(" dosi", ""), text_data_inizio.getText().toString(), text_data_termine.getText().toString(), orario_inizio_farmaco.getText().toString(), intervallo_farmaco.getSelectedItem().toString().replace(" ore", ""), lunedi.isChecked(), martedi.isChecked(), mercoledi.isChecked(), giovedi.isChecked(), venerdi.isChecked(), sabato.isChecked(), domenica.isChecked()));
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

    private void showListaFarmaciPerTerapie(final ArrayList<JSONObject> lista_risultati_farmaci){

        feedback_message.setText("");
        if(text_nome_farmaco.isEnabled()) {
            seleziona_farmaco_textvivew.setVisibility(View.VISIBLE);

            //Imposto l'adapter
            ScegliFarmacoPianoAdapter adapter = new ScegliFarmacoPianoAdapter(layout.getContext(), R.layout.scegli_farmaco_piano, lista_risultati_farmaci);
            lista_farmaci_trovati.setAdapter(adapter);
            lista_farmaci_trovati.setVisibility(View.VISIBLE);
            lista_farmaci_trovati.setOnItemClickListener(seleziona_farmaco);
        }
    }

    public void showDettagliAssunzione(String Dosaggio, String DataInizio, String DataTermine, String OraInizio, String Intervallo, Boolean Lunedi, Boolean Martedi, Boolean Mercoledi, Boolean Giovedi, Boolean Venerdi, Boolean Sabato, Boolean Domenica)
    {
        text_nome_farmaco.setText(NomeFarmaco +" ("+ Composizione +")");
        //////dosaggio.setIten(Dosaggio);
        text_data_termine.setText(DataTermine);
        text_data_inizio.setText(DataInizio);
        orario_inizio_farmaco.setText(OraInizio);
        /////intervallo_farmaco.setText(Intervallo);
        lunedi.setChecked(Lunedi);
        martedi.setChecked(Martedi);
        mercoledi.setChecked(Mercoledi);
        giovedi.setChecked(Giovedi);
        venerdi.setChecked(Venerdi);
        sabato.setChecked(Sabato);
        domenica.setChecked(Domenica);

        if(userType.compareTo("Paziente") == 0) setAbilityOfPaziente();
        else if(userType.compareTo("Dottore") == 0) setAbilityOfDottore();
        showFeedbackTextView("");
        second_section.setVisibility(View.VISIBLE);
    }

    public void disableFields(){
        text_nome_farmaco.setEnabled(false);
        lista_farmaci_trovati.setEnabled(false);
        feedback_message.setEnabled(false);
        seleziona_farmaco_textvivew .setEnabled(false);
        second_section.setEnabled(false);
        lunedi.setEnabled(false);
        martedi.setEnabled(false);
        mercoledi.setEnabled(false);
        giovedi.setEnabled(false);
        venerdi.setEnabled(false);
        sabato.setEnabled(false);
        domenica.setEnabled(false);
        dosaggio.setEnabled(false);
        intervallo_farmaco.setEnabled(false);
    }

    private void setAbilityOfPaziente()
    {
        text_nome_farmaco.setBackgroundResource(R.drawable.field_blocked);
        dosaggio.setBackgroundResource(R.drawable.field_blocked);
        text_data_termine.setBackgroundResource(R.drawable.field_blocked);
        text_data_inizio.setBackgroundResource(R.drawable.field_blocked);
        orario_inizio_farmaco.setBackgroundResource(R.drawable.field_blocked);
        intervallo_farmaco.setBackgroundResource(R.drawable.field_blocked);

        text_nome_farmaco.setEnabled(false);
        dosaggio.setEnabled(false);
        text_data_termine.setEnabled(false);
        text_data_inizio.setEnabled(false);
        orario_inizio_farmaco.setEnabled(false);
        intervallo_farmaco.setEnabled(false);
        lunedi.setEnabled(false);
        martedi.setEnabled(false);
        mercoledi.setEnabled(false);
        giovedi.setEnabled(false);
        venerdi.setEnabled(false);
        sabato.setEnabled(false);
        domenica.setEnabled(false);


        button_save.setVisibility(View.GONE);
    }

    private void setAbilityOfDottore()
    {
        text_nome_farmaco.setBackgroundResource(R.drawable.field_editable);
        dosaggio.setBackgroundResource(R.drawable.field_editable);
        text_data_termine.setBackgroundResource(R.drawable.field_editable);
        text_data_inizio.setBackgroundResource(R.drawable.field_editable);
        orario_inizio_farmaco.setBackgroundResource(R.drawable.field_editable);
        intervallo_farmaco.setBackgroundResource(R.drawable.field_editable);

        text_nome_farmaco.setEnabled(true);
        dosaggio.setEnabled(true);
        text_data_termine.setEnabled(true);
        text_data_inizio.setEnabled(true);
        orario_inizio_farmaco.setEnabled(true);
        intervallo_farmaco.setEnabled(true);
        lunedi.setEnabled(true);
        martedi.setEnabled(true);
        mercoledi.setEnabled(true);
        giovedi.setEnabled(true);
        venerdi.setEnabled(true);
        sabato.setEnabled(true);
        domenica.setEnabled(true);

        button_save.setVisibility(View.VISIBLE);
    }

    private AdapterView.OnItemClickListener seleziona_farmaco = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            try {
                JSONObject object = (JSONObject) lista_farmaci_trovati.getItemAtPosition(position);
                text_nome_farmaco.setText(object.getString("Nome"));
                text_nome_farmaco.setEnabled(false);
                text_nome_farmaco.setBackgroundResource(R.drawable.field_blocked);

                seleziona_farmaco_textvivew.setVisibility(View.GONE);
                feedback_message.setText("");
                lista_farmaci_trovati.setVisibility(View.GONE);
                second_section.setVisibility(View.VISIBLE);

                IDFarmaco = object.getString("IDFarmaco");

                new FarmacoPianoTerapeuticoAsync().execute(GestionePianoTerapeutico.aggiungiFarmaco(IDTerapia, IDFarmaco));
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
    };

    //Mostro un messaggio di feedback
    private void showFeedbackTextView(String message){

        feedback_message.setText(message);
        feedback_message.setVisibility(View.VISIBLE);
        lista_farmaci_trovati.setVisibility(View.GONE);
    }

    private class FarmacoPianoTerapeuticoAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "FarmacoPianoTerapeuticoAsync";
        private static final String SECTION = "GestorePianoServer/PianoTerapeuticoManager";

        private String action;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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

                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if(action.equals("visualizzaDettagliAssunzioneFarmaco"))
                {
                    JSONObject data = result.getJSONObject("data");
                    showDettagliAssunzione(data.getString("Dosaggio"), data.getString("DataInizio"), data.getString("DataTermine"), data.getString("OraInizio"), data.getString("Intervallo"), "1".equals(data.getString("Lunedi")), "1".equals(data.getString("Martedi")), "1".equals(data.getString("Mercoledi")), "1".equals(data.getString("Giovedi")), "1".equals(data.getString("Venerdi")), "1".equals(data.getString("Sabato")), "1".equals(data.getString("Domenica")));

                }else if(action.equals("aggiornaDettagliAssunzioneFarmaco")){

                    PianoTerapeutico fragment = new PianoTerapeutico();
                    fragment.setInfo(IDTerapia, dottoreID, nomePiano, false);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.section_opened, (Fragment)fragment);
                    fragmentTransaction.commit();
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

    private class RicercaFarmaciTerapiaAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "RicercaFarmaciTerapiaAsync";
        private static final String SECTION = "GestoreFarmacoServer/FarmacoManager";

        private String action;

        @Override
        protected void onPreExecute() {
            showFeedbackTextView("Ricerca in corso...");
            super.onPreExecute();
        }

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

                if (result.getString("status").equals("error")) {
                    if (result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else if (result.getString("exception").equals("ResponseErrorException"))
                        throw new ResponseErrorException(result.getString("message"));
                }

                if (action.equals("ricercaFarmaco"))
                {
                    JSONObject data = result.getJSONObject("data");
                    if(data.getInt("count") == 0)  showFeedbackTextView("Nessun farmaco trovato...");
                    else{

                        JSONArray lista_dottori = data.getJSONArray("lista_farmaci");

                        ArrayList<JSONObject> list = new ArrayList<>();
                        for (int index = 0; index < lista_dottori.length(); index++)
                            list.add(lista_dottori.getJSONObject(index));

                        showListaFarmaciPerTerapie(list);
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
