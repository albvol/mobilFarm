package com.example.albertovolpe.mobilfarm.GestoreFarmacoClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.albertovolpe.mobilfarm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by albertovolpe on 01/06/16.
 */

public class ListaFarmaciInScadenzaAdapter extends ArrayAdapter<JSONObject>{

    private static final String TAG = "ListaFarmaciInScadenzaAdapter";


    public ListaFarmaciInScadenzaAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    @SuppressLint("LongLogTag")
    public View getViewOptimize(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.farmaco_in_scadenza, null);

            viewHolder = new ViewHolder();

            viewHolder.text_nome_farmaco = (TextView) convertView.findViewById(R.id.text_nome_farmaco);
            viewHolder.scadenza_value = (TextView) convertView.findViewById(R.id.scadenza_value);
            convertView.setTag(viewHolder);

        } else viewHolder = (ViewHolder) convertView.getTag();

        try
        {
            final JSONObject item = getItem(position);
            viewHolder.text_nome_farmaco.setText(item.getString("Nome") +" "+item.getString("Composizione")+ "mg");
            viewHolder.scadenza_value.setText(item.getString("Scadenza"));

        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView text_nome_farmaco;
        public TextView scadenza_value;
    }
}
