package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import javax.xml.datatype.Duration;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;

public class FormularioBusquedaFragment extends Fragment implements View.OnClickListener {
    private ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;
    private Spinner tipoReclamo;
    private Button bttnBuscar;
    private MapaFragment.OnMapaListener mapaListener;
    public void setListener(MapaFragment.OnMapaListener listener) {
        this.mapaListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_formulario_busqueda, container, false);
        tipoReclamo = (Spinner) v.findViewById(R.id.reclamo_tipo);
        bttnBuscar = (Button) v.findViewById(R.id.btnBuscar);
        bttnBuscar.setOnClickListener(this);
        tipoReclamoAdapter = new ArrayAdapter<Reclamo.TipoReclamo>(getActivity(),android.R.layout.simple_spinner_item,Reclamo.TipoReclamo.values());
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoReclamo.setAdapter(tipoReclamoAdapter);

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == bttnBuscar){
            Reclamo.TipoReclamo tr = tipoReclamoAdapter.getItem(tipoReclamo.getSelectedItemPosition());
            /*Toast.makeText(getContext(),tr.toString() + " APRETADO!",Toast.LENGTH_LONG).show();*/

            Fragment fragment = null;
            String tag="mapaReclamos";
            fragment =  getFragmentManager().findFragmentByTag(tag);
            //TODO si "fragment" es null entonces crear el fragmento mapa, agregar un bundel con el parametro tipo_mapa
            if(fragment == null){
                fragment = new MapaFragment();
                ((MapaFragment) fragment).setListener(mapaListener);
            }

            Bundle b = new Bundle();

            // pasando como parametro un bundle con "tipo_mapa"
            b.putInt("tipo_mapa",5);
            b.putString("tipo_reclamo", tr.toString());
            fragment.setArguments(b);


            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenido, fragment,tag)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
