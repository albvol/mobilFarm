package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.GestoreAccountClient.DottoreHome;
import com.example.albertovolpe.mobilfarm.GestoreAccountClient.PazienteHome;
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

/**
 * Created by albertovolpe on 01/06/16.
 */

public class ListaFarmaciVMFAdapter extends ArrayAdapter<JSONObject> implements Filterable {

    private static final String TAG = "ListaFarmaciVMFAdapter";
    private List<JSONObject> originalData, filteredData;
    private ItemFilter mFilter = new ItemFilter();


    public ListaFarmaciVMFAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);
        originalData = objects;
        filteredData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    public View getViewOptimize(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.farmaco_vmf, null);

            viewHolder = new ViewHolder();

            viewHolder.text_nome_farmaco = (TextView) convertView.findViewById(R.id.text_nome_farmaco);
            viewHolder.scadenza_value = (TextView) convertView.findViewById(R.id.scadenza_value);
            viewHolder.composizione_value = (TextView) convertView.findViewById(R.id.composizione_value);
            viewHolder.deleteButton = (ImageView) convertView.findViewById(R.id.delete_button);
            convertView.setTag(viewHolder);

        } else viewHolder = (ViewHolder) convertView.getTag();

        try
        {
            final JSONObject item = getItem(position);
            viewHolder.text_nome_farmaco.setText(item.getString("Nome"));
            viewHolder.scadenza_value.setText(item.getString("Scadenza"));
            viewHolder.composizione_value.setText(item.getString("Composizione")+ "mg");
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new ListaFarmaciVMFAdapterAsync().execute(
                                GestioneVMF.eliminaFarmacoVMF(item.getString("IDFarmaco")));
                    } catch (JSONException e) {
                        Log.i(TAG, e.getMessage());
                    } catch (ActionNotAuthorizedException e) {
                        Log.i(TAG, e.getMessage());
                    } catch (ErrorValuesException e) {
                        Log.i(TAG, e.getMessage());
                    }finally {

                    }
                }
            });

        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView text_nome_farmaco;
        public TextView scadenza_value;
        public TextView composizione_value;
        public ImageView deleteButton;
    }


    private class ListaFarmaciVMFAdapterAsync extends AsyncTask<JSONObject, Void, JSONObject>
    {
        private static final String TAG = "ListaFarmaciVMFAdapterAsync";
        private static final String SECTION = "GestoreFarmacoServer/VMFManager";
        private SharedPreferences sharedPreferences;

        private String userType, action;

        @Override
        protected void onPreExecute() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            userType = sharedPreferences.getString("Type", "Utente non identificato");
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
                if(result.getString("status").equals("error")){

                    if(result.getString("exception").equals("ActionNotAuthorizedException"))
                        throw new ActionNotAuthorizedException();
                    else
                        throw new ResponseErrorException(result.getString("message"));

                }else{
                    if(userType.compareTo("Dottore") == 0) DottoreHome.apriVMF();
                    else if(userType.compareTo("Paziente") == 0) PazienteHome.apriVMF();
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            catch (ResponseErrorException e) {
                Log.e(TAG, e.getMessage());
            }
            catch (ActionNotAuthorizedException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    public int getCount() {
        return filteredData.size();
    }

    public JSONObject getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<JSONObject> list = originalData;

            int count = list.size();
            final ArrayList<JSONObject> nlist = new ArrayList<JSONObject>(count);

            String filterableString;

            try {
                for (int i = 0; i < count; i++)
                {
                    filterableString = list.get(i).getString("Nome");
                    if (filterableString.toLowerCase().contains(filterString)) nlist.add(list.get(i));
                }
            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<JSONObject>) results.values;
            notifyDataSetChanged();
        }

    }
}
