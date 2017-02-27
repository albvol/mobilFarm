package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.albertovolpe.mobilfarm.UtilityClient.ErrorValuesException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ListaDottoriAdapter;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RicercaDottore extends Fragment {

    private static final String TAG = "RicercaDottore";
    private OnFragmentInteractionListener mListener;

    private EditText text_search;
    private View layout;
    private TextView feedback_message;
    private ListView content;

    public RicercaDottore() {
        // Required empty public constructor
    }

    public static RicercaDottore newInstance() {
        RicercaDottore fragment = new RicercaDottore();
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
        layout = inflater.inflate(R.layout.fragment_ricerca_dottore, container, false);

        text_search = (EditText) layout.findViewById(R.id.text_search);
        text_search.addTextChangedListener(search);

        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);
        content = (ListView) layout.findViewById(R.id.lista_dottori_trovati);

        PazienteHome.cambiaTitoloSezione("Cerca Dottore");
        return layout;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private TextWatcher search = new TextWatcher() {
        public void afterTextChanged(Editable s) {

            Pattern letters = Pattern.compile("^[a-z ]+$", Pattern.CASE_INSENSITIVE);

            try
            {
                if(s.length() != 0) {
                    if (!(letters.matcher(s.toString())).find())
                        throw new ErrorValuesException("Il nome non può contenere caratteri speciali o numeri!");
                    else new RicercaDottoreAsync().execute(
                                GestioneRubrica.ricercaDottore(text_search.getText().toString()));
                }
            } catch (ErrorValuesException e) {
                Log.i(TAG, e.getMessage());
                showFeedbackTextView(e.getMessage());
            } catch (ActionNotAuthorizedException e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog(e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
                showAlertDialog("Errore interno!");
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
        content.setVisibility(View.VISIBLE);
    }

    private void showAlertDialog(String message){

        new AlertDialog.Builder(getContext())
                .setTitle("Errore")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.logo)
                .show();
    }

    private void showListaDottori(final ArrayList<JSONObject> lista_dottori){

        //Imposto l'adapter
        ListaDottoriAdapter adapter = new ListaDottoriAdapter(layout.getContext(), R.layout.contatto_dottore, lista_dottori);
        content.setAdapter(adapter);
        content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    showDottore(true, ((JSONObject) lista_dottori.get(position)).getString("UserID"));
                } catch (JSONException e) {
                    e.getMessage();
                    showAlertDialog("Errore interno!");
                }
            }
        });
        hideFeedbackTextView();
    }

    public void showDottore(Boolean isNew, String userID)
    {
        SchedaDottore schedaDottore = new SchedaDottore();
        schedaDottore.setInfo(isNew, userID);

        FragmentTransaction fragmentTransaction = super.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.section_opened, schedaDottore);
        fragmentTransaction.commit();
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


    private class RicercaDottoreAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "RicercaDottoreAsync";
        private static final String SECTION = "GestoreAccountServer/RubricaManager";

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

                JSONObject data = result.getJSONObject("data");
                if (action.equals("ricercaDottore"))
                {
                    if(data.getInt("count") == 0)
                        showFeedbackTextView("Nessun dottore trovato...");
                    else{

                        JSONArray lista_dottori = data.getJSONArray("lista_dottori");

                        ArrayList<JSONObject> list = new ArrayList<>();
                        for (int index = 0; index < lista_dottori.length(); index++)
                            list.add(lista_dottori.getJSONObject(index));

                        showListaDottori(list);
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
