package com.example.albertovolpe.mobilfarm.GestoreAccountClient;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albertovolpe.mobilfarm.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission_group.SMS;

public class ModalitaSOS extends Fragment implements LocationListener {

    private final static String TAG = "ModalitaSOS";
    private OnFragmentInteractionListener mListener;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private View layout;
    private TextView time, text;
    private static TextView editLocation;
    private LocationManager locationManager;
    private Geocoder gcd;
    private List<Address> addresses;
    private String position, number1, number2;
    private Boolean gps;

    public ModalitaSOS() {
        // Required empty public constructor
    }

    public static ModalitaSOS newInstance() {
        ModalitaSOS fragment = new ModalitaSOS();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gcd = new Geocoder(getContext(), Locale.getDefault());
        gps = true;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission()) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        else gps = false;

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_modalita_sos, container, false);

        time = (TextView) layout.findViewById(R.id.time);
        text = (TextView) layout.findViewById(R.id.text);
        editLocation = (TextView) layout.findViewById(R.id.editLocation);

        editLocation.setText("");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        number1 = sharedPreferences.getString("NumeroSOS1", "");
        number2 = sharedPreferences.getString("NumeroSOS2", "");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PazienteHome.isSOSOpened) {
                    Log.i(TAG, "3");
                    time.setText("3");
                }
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PazienteHome.isSOSOpened) {
                    Log.i(TAG, "2");
                    time.setText("2");
                }
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PazienteHome.isSOSOpened) {
                    Log.i(TAG, "1");
                    time.setText("1");
                }
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PazienteHome.isSOSOpened) {
                    time.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    editLocation.setVisibility(View.VISIBLE);
                    if(gps == false)
                    {
                        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), ModalitaSOS.class), 0);
                        SmsManager sms = SmsManager.getDefault();
                        String message = "";
                        if(!number1.isEmpty())
                        {
                            editLocation.append("\nInvio l'sms a:"+number1);
                            sms.sendTextMessage(number1, null, "Messaggio di S.O.S: \nHo bisogno di aiuto!", pi, null);
                        }
                        if(!number2.isEmpty())
                        {
                            editLocation.append("\nInvio l'sms a:"+number2);
                            sms.sendTextMessage(number2, null, "Messaggio di S.O.S: \nHo bisogno di aiuto!", pi, null);
                        }

                        editLocation.append("\nAvvio la chiamata al 118");

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+"+393476760352"));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }
                }
            }
        }, 4000);

        return layout;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
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

    @Override
    public void onLocationChanged(Location location) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else locationManager.removeUpdates(this);

        /*------- To get city name from coordinates -------- */
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0){

                editLocation.setText("Luogo trovato\n"+addresses.get(0).getLocality()+","+addresses.get(0).getAddressLine(0).toString());
                position = "Mi trovo a "+addresses.get(0).getLocality()+","+addresses.get(0).getAddressLine(0).toString()+"\n"+"https://www.google.it/maps/@"+location.getLatitude()+","+location.getLongitude();
            }

        } catch (IOException e) {
            editLocation.setText("Luogo non trovato!\nUtilizzo le coordinate!\n");
            position = "https://www.google.it/maps/@"+location.getLatitude()+","+location.getLongitude();
        }finally{

            if(PazienteHome.isSOSOpened){

                PendingIntent pi = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), ModalitaSOS.class), 0);
                SmsManager sms = SmsManager.getDefault();
                String message = "";
                Log.i(TAG, number1+" "+number2);
                if(!number1.isEmpty())
                {
                    editLocation.append("\nInvio l'sms a:"+number1);
                    sms.sendTextMessage(number1, null, "Messaggio di S.O.S: \nHo bisogno di aiuto!\n "+position, pi, null);
                }
                if(!number2.isEmpty())
                {
                    editLocation.append("\nInvio l'sms a:"+number2);
                    sms.sendTextMessage(number2, null, "Messaggio di S.O.S: \nHo bisogno di aiuto!\n "+position, pi, null);
                }

                editLocation.append("\nAvvio la chiamata al 118");

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+"+393476760352"));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
