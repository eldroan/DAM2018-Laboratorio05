package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private int tipoMapa;
    private GoogleMap miMapa;
    private OnMapaListener listener;

    public interface OnMapaListener {
        public void coordenadasSeleccionadas(LatLng c);
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

        if(argumentos != null){
            tipoMapa = argumentos.getInt("tipo_mapa",0);
        }

        getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        miMapa = googleMap;
    }

    public void setListener(OnMapaListener listener) {
        this.listener = listener;
    }


}
