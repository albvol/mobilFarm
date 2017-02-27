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
import android.widget.ListView;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestoreAccountClient.PazienteHome;
import com.example.albertovolpe.mobilfarm.R;
import com.example.albertovolpe.mobilfarm.UtilityClient.ActionNotAuthorizedException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ConnectionErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.ListaPianiTerapeuticiAdapter;
import com.example.albertovolpe.mobilfarm.UtilityClient.ResponseErrorException;
import com.example.albertovolpe.mobilfarm.UtilityClient.mobilFarmServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class PianiTerapeutici extends Fragment {

    private static final String TAG = "PianiTerapeutici";
    private static SharedPreferences sharedPreferences;
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private TextView feedback_message;
    private View layout;
    private String userType, pazienteID;

    public PianiTerapeutici() {
        // Required empty public constructor
    }

    public static PianiTerapeutici newInstance() {
        PianiTerapeutici fragment = new PianiTerapeutici();
        return fragment;
    }

    public void setInfo(String pazienteID){
        this.pazienteID = pazienteID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_piani_terapeutici, container, false);

        //Salvo i reference delle view
        listView = (ListView) layout.findViewById(R.id.lista_piani);
        feedback_message = (TextView) layout.findViewById(R.id.feedback_message);

        try {
                //Leggo il tipo di utente loggato
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                userType = sharedPreferences.getString("Type", "Utente non identificato");

                //Carico i piani terapeutici del paziente
                if(userType.compareTo("Paziente") == 0) {
                    PazienteHome.cambiaTitoloSezione("Piani Terapeutici");
                    new PianiTerapeuticiAsync().execute(GestionePianoTerapeutico.visualizzaPianiTerapeutici());
                }else if(userType.compareTo("Dottore") == 0) {
                    new PianiTerapeuticiAsync().execute(GestionePianoTerapeutico.visualizzaPianiTerapeuticiDiUnPaziente(pazienteID));
                }

        }catch (ActionNotAuthorizedException e) {
            Log.i(TAG, e.getMessage());
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

    private void showListaPiani(ArrayList lista){

        //Imposto l'adapter
        ListaPianiTerapeuticiAdapter adapter = new ListaPianiTerapeuticiAdapter(layout.getContext(), R.layout.piano_terapeutico, lista);
        listView.setAdapter(adapter);

        feedback_message.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    PianoTerapeutico fragment = new PianoTerapeutico();
                    fragment.setInfo(((JSONObject) listView.getItemAtPosition(position)).getString("IDTerapia"),
                            ((JSONObject) listView.getItemAtPosition(position)).getString("CompilatoDa"),
                            ((JSONObject) listView.getItemAtPosition(position)).getString("NomePiano"),
                            false);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                    fragmentTransaction.replace(R.id.section_opened, fragment).addToBackStack("Piano Terapeutico").commit();

                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                    showAlertDialog("Errore Interno!");
                }
            }
        });

        Log.i(TAG, listView.getLayoutParams().height+"");
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

    private class PianiTerapeuticiAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "PianiTerapeuticiAsync";
        private static final String SECTION = "GestorePianoServer/PianoTerapeuticoManager";

        private String action;

        @Override
        protected void onPreExecute() {
            showFeedbackTextView("Caricamento...");
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
        protected void onPostExecute(JSONObject result){

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

                    if(userType.compareTo("Dottore") == 0)
                        showFeedbackTextView("Nessun piano presente...");
                    else if(userType.compareTo("Paziente") == 0)
                        showFeedbackTextView("Nessun dottore ha realizzato\nun piano terapeutico per te...");
                }else
                {
                    JSONArray lista_piani = data.getJSONArray("lista_piani");

                    ArrayList<JSONObject> list = new ArrayList<>();
                    for (int index = 0; index < lista_piani.length(); index++)
                        list.add(lista_piani.getJSONObject(index));

                    showListaPiani(list);
                }
            }catch (JSONException e) {
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
