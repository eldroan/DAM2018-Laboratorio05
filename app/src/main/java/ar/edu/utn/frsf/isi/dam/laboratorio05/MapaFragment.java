package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback, OnRequestPermissionsResultCallback {

    private int tipoMapa;
    private GoogleMap miMapa;
    private OnMapaListener listener;




    public interface OnMapaListener {
        public void coordenadasSeleccionadas(LatLng c);
    }
    public void setListener(OnMapaListener listener) {
        this.listener = listener;
    }
    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        tipoMapa = 0;

        Bundle argumentos = getArguments();

        if (argumentos != null) {
            tipoMapa = argumentos.getInt("tipo_mapa", 0);
        }

        /*if(tipoMapa == 2)
        {
            reclamoDao = MyDatabase.getInstance(this.getActivity()).getReclamoDao();
            *//*reclamos = reclamoDao.getAll();*//*
        }
        else
        {
            *//*reclamos = null;*//*
            reclamoDao = null;
            reclamos = null;
        }*/

        getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        miMapa = googleMap;
        miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(tipoMapa == 1){
                    Log.d("LONGPRESS","Se recibio un longpress en el mapa de tipo 1");
                    listener.coordenadasSeleccionadas(latLng);
                }
            }
        });

        if(tipoMapa == 2 ){

            configurarMapaConReclamos();

        }
        actualizarMapa();
    }

    private void configurarMapaConReclamos() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                final List <Reclamo> reclamos = MyDatabase.getInstance(MapaFragment.this.getActivity()).getReclamoDao().getAll();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(reclamos.isEmpty() == false){
                            LatLngBounds.Builder boundBuilder =  new LatLngBounds.Builder();
                            for(Reclamo r : reclamos){
                                //Creo el marker
                                MarkerOptions currentMarker = new MarkerOptions();
                                currentMarker.position(new LatLng(r.getLatitud(),r.getLongitud()));
                                currentMarker.title(r.getReclamo());

                                //Lo agrego al mapa
                                miMapa.addMarker(currentMarker);

                                //Lo incluyo en los bounds
                                boundBuilder.include(currentMarker.getPosition());
                            }
                            /*LatLngBounds b = boundBuilder.build();
                            b.*/
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 100);

                            miMapa.moveCamera(cu );
                            actualizarMapa();
                        }
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void actualizarMapa() {

        // Here, thisActivity is the current activity
        Activity thisActivity = getActivity();
        if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 9999);
            return;
        }else{
            miMapa.setMyLocationEnabled(true);

        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(getActivity(), "Permission graanted", Toast.LENGTH_SHORT).show();
                    actualizarMapa();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    actualizarMapa();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
