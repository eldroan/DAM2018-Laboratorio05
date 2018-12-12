package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;


// AGREGAR en MapaFragment una interface MapaFragment.OnMapaListener con el método coordenadasSeleccionadas 
// IMPLEMENTAR dicho método en esta actividad.

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,
        NuevoReclamoFragment.OnNuevoLugarListener, MapaFragment.OnMapaListener {
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        BienvenidoFragment fragmentInicio = new BienvenidoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragmentInicio)
                .commit();

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;
                        String tag = "";
                        switch (menuItem.getItemId()) {
                            case R.id.optNuevoReclamo:
                                tag = "nuevoReclamoFragment";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment==null) {
                                    fragment = new NuevoReclamoFragment();
                                    ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
                                }

                                fragmentTransaction = true;
                                break;
                            case R.id.optListaReclamo:
                                tag="listaReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment==null) fragment = new ListaReclamosFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.optVerMapa:
                                //TODO HABILITAR
                                tag="mapaReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                //TODO si "fragment" es null entonces crear el fragmento mapa, agregar un bundel con el parametro tipo_mapa
                                if(fragment == null){
                                    fragment = new MapaFragment();
                                    ((MapaFragment) fragment).setListener(MainActivity.this);

                                }
                                Bundle b = new Bundle();
                                b.putInt("tipo_mapa",2);

                                fragment.setArguments(b);
                                //((MapaFragment) fragment).setListener(MainActivity.this);

                               fragmentTransaction = true;
                                break;
                            case R.id.optHeatMap:
                                tag="mapaReclamos";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment == null){
                                    fragment = new MapaFragment();
                                    ((MapaFragment) fragment).setListener(MainActivity.this);

                                }
                                Bundle bu = new Bundle();
                                bu.putInt("tipo_mapa",4);

                                fragment.setArguments(bu);
                               fragmentTransaction = true;
                                break;
                            case R.id.optFormularioDeBusqueda:
                                tag="formularioBusqueda";
                                fragment =  getSupportFragmentManager().findFragmentByTag(tag);
                                if(fragment == null){
                                    fragment = new FormularioBusquedaFragment();
                                    ((FormularioBusquedaFragment) fragment).setListener(MainActivity.this);

                                }
                                fragmentTransaction = true;
                                break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.contenido, fragment,tag)
                                    .addToBackStack(null)
                                    .commit();

                            menuItem.setChecked(true);

                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    // AGREGAR en MapaFragment una interface OnMapaListener con el método coordenadasSeleccionadas
    // IMPLEMENTAR dicho método en esta actividad.
    // el objetivo de este método, es simplmente invocar al fragmento "nuevoReclamoFragment"
    // pasando como argumento el objeto "LatLng" elegido por el usuario en el click largo
    // como ubicación del reclamo

/*        @Override
        public void coordenadasSeleccionadas(LatLng c) {
            String tag = "nuevoReclamoFragment";
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
            if(fragment==null) {
                fragment = new NuevoReclamoFragment();
                ((NuevoReclamoFragment) fragment).setListener(listenerReclamo);
            }
            Bundle bundle = new Bundle();
            bundle.putString("latLng",c.latitude+";"+c.longitude);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenido, fragment,tag)
                    .commit();

        }
    };
*/


        @Override
        public void obtenerCoordenadas() {
            // TODO: invocar el fragmento del mapa
            Fragment fragment = null;
            String tag="mapaReclamos";
            fragment =  getSupportFragmentManager().findFragmentByTag(tag);
            //TODO si "fragment" es null entonces crear el fragmento mapa, agregar un bundel con el parametro tipo_mapa
            if(fragment == null){
                fragment = new MapaFragment();
            }

            Bundle b = new Bundle();

            // pasando como parametro un bundle con "tipo_mapa"
            b.putInt("tipo_mapa",1);

            fragment.setArguments(b);
            ((MapaFragment) fragment).setListener(MainActivity.this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenido, fragment,tag)
                    .addToBackStack(null)
                    .commit();

                /*menuItem.setChecked(true);

                getSupportActionBar().setTitle(menuItem.getTitle());*/


            // para que el usuario vea el mapa y con el click largo pueda acceder
            // a seleccionar la coordenada donde se registra el reclamo
            // configurar a la actividad como listener de los eventos del mapa ((MapaFragment) fragment).setListener(this);
        }

    @Override
    public void coordenadasSeleccionadas(LatLng c) {
        String tag = "nuevoReclamoFragment";
        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
        Bundle b = null;
        b = fragment.getArguments();
        if(b == null){
            b = new Bundle();
        }
        b.putDouble("Lat",c.latitude);
        b.putDouble("Lng",c.longitude);
        fragment.setArguments(b);
        //Deberia setear el listener? Supongo que no porque este fragment ya viene de creado

        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment,tag)
                .addToBackStack(null)
                .commit();*/
        getSupportFragmentManager().popBackStack(); //Este es para volver a la anterior

    }
}
