package com.example.albertovolpe.mobilfarm.UtilityClient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.example.albertovolpe.mobilfarm.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertovolpe on 01/06/16.
 */

public class ListaDottoriAdapter extends ArrayAdapter<JSONObject> implements Filterable{

    private static final String TAG = "ListaDottoriAdapter";
    private List<JSONObject> originalData, filteredData;
    private ItemFilter mFilter = new ItemFilter();

    public ListaDottoriAdapter(Context context, int textViewResourceId, List objects) {
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

            convertView = inflater.inflate(R.layout.contatto_dottore, null);

            viewHolder = new ViewHolder();

            viewHolder.text_nome_cognome = (TextView) convertView.findViewById(R.id.text_nome_cognome);
            viewHolder.specializzazione = (TextView) convertView.findViewById(R.id.specializzazione);

            convertView.setTag(viewHolder);

        } else viewHolder = (ViewHolder) convertView.getTag();

        try
        {
            JSONObject item = getItem(position);
            viewHolder.text_nome_cognome.setText(item.getString("Nome")+" "+ item.getString("Cognome"));
            viewHolder.specializzazione.setText(item.getString("Specializzazione"));

        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView text_nome_cognome, specializzazione;
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
                    filterableString = list.get(i).getString("Cognome");
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
