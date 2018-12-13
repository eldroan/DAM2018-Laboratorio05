package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;

public class NuevoReclamoFragment extends Fragment implements View.OnClickListener {


    private static final int REQUEST_MICROPHONE = 1234;

    public void setImagePath(String pathPhoto) {

        pathOfPhoto = pathPhoto;
        btnGuardar.setEnabled(comprobarSiPuedeGuardar());

        File file = new File(pathPhoto);

        Bitmap imageBitmap = null;

        try {
            imageBitmap = MediaStore.Images.Media
                    .getBitmap(getActivity().getApplicationContext().getContentResolver(),
                            Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageBitmap != null) {
            imageView.setImageBitmap(imageBitmap);
        }
    }



    public interface OnNuevoLugarListener {
        public void obtenerCoordenadas();
        public void sacarGuardarFoto();
        public String getPhotoPath();


    }

    public void setListener(OnNuevoLugarListener listener) {
        this.listener = listener;
    }

    private Reclamo reclamoActual;
    private ReclamoDao reclamoDao;

    private EditText reclamoDesc;
    private EditText mail;
    private Spinner tipoReclamo;
    private TextView tvCoord;
    private Button buscarCoord;
    private Button btnGuardar;
    private Button btnFoto;
    private ImageView imageView;
    private OnNuevoLugarListener listener;
    private static  String pathOfPhoto;
    private static String file_name;

    private Button btnGrabar;
    private Button btnReproducir;
    private Button btnParar;

    private Estado estadoBotones;

    private enum Estado { NADA,GRABANDO,GRABACIONFINALIZADA,REPRODUCIENDO}

    private MediaRecorder rec;
    private MediaPlayer mediaPlayer;


    private ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;
    public NuevoReclamoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reclamoDao = MyDatabase.getInstance(this.getActivity()).getReclamoDao();

        View v = inflater.inflate(R.layout.fragment_nuevo_reclamo, container, false);

        reclamoDesc = (EditText) v.findViewById(R.id.reclamo_desc);
        mail= (EditText) v.findViewById(R.id.reclamo_mail);
        tipoReclamo= (Spinner) v.findViewById(R.id.reclamo_tipo);
        tvCoord= (TextView) v.findViewById(R.id.reclamo_coord);
        buscarCoord= (Button) v.findViewById(R.id.btnBuscarCoordenadas);
        btnGuardar= (Button) v.findViewById(R.id.btnGuardar);
        btnFoto = (Button) v.findViewById(R.id.btnTomarFoto);
        imageView = (ImageView) v.findViewById(R.id.image_view);

        btnGrabar= (Button) v.findViewById(R.id.btnGrabar);
        btnReproducir= (Button) v.findViewById(R.id.btnReproducir);
        btnParar= (Button) v.findViewById(R.id.btnParar);

        btnGrabar.setOnClickListener(this);
        btnReproducir.setOnClickListener(this);
        btnParar.setOnClickListener(this);
        btnFoto.setOnClickListener(this);
        buscarCoord.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);



        actualizarBotonesDeReproducción(Estado.NADA);


        /*btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.sacarGuardarFoto();
            }
        });*/


        tipoReclamoAdapter = new ArrayAdapter<Reclamo.TipoReclamo>(getActivity(),android.R.layout.simple_spinner_item,Reclamo.TipoReclamo.values());
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoReclamo.setAdapter(tipoReclamoAdapter);

        int idReclamo =0;
        Bundle b = null;
        b = getArguments();
        double lat = 0;
        double lng = 0;
        if(b!=null)  {
            idReclamo = b.getInt("idReclamo",0);

            lat = b.getDouble("Lat",0);
            lng=  b.getDouble("Lng",0);

            if(b.getBoolean("trySettingPhoto",false) && pathOfPhoto != null && !pathOfPhoto.isEmpty()){
                setImagePath(pathOfPhoto);
            }else{
                pathOfPhoto = null;
            }
            if(b.getBoolean("resetRecordPath",true) ){
                file_name = null;
            }else{
                if(file_name != null){
                    //Hay un archivo grabado
                    actualizarBotonesDeReproducción(Estado.GRABACIONFINALIZADA);
                }
            }

        }

        cargarReclamo(idReclamo);

        tvCoord.setText(lat+";"+lng);
        boolean edicionActivada = !tvCoord.getText().toString().equals("0.0;0.0");
        reclamoDesc.setEnabled(edicionActivada );
        reclamoDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    btnGuardar.setEnabled(comprobarSiPuedeGuardar());
                }
            }
        });
        mail.setEnabled(edicionActivada );
        tipoReclamo.setEnabled(true);
        tipoReclamo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btnGuardar.setEnabled(comprobarSiPuedeGuardar());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnGuardar.setEnabled(comprobarSiPuedeGuardar());


        /*buscarCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.obtenerCoordenadas();

            }
        });*/

        /*btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrUpdateReclamo();
            }
        });*/
        return v;
    }

    private boolean comprobarSiPuedeGuardar() {
        Reclamo.TipoReclamo tipoSeleccionado = tipoReclamoAdapter.getItem(tipoReclamo.getSelectedItemPosition());

        if(tipoSeleccionado == Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO || tipoSeleccionado == Reclamo.TipoReclamo.VEREDAS){
            return pathOfPhoto != null;
        }else{
            return (file_name != null) || reclamoDesc.getText().length() >=8;
        }

    }

    private void actualizarBotonesDeReproducción(Estado est) {

        if(est == Estado.NADA){
            btnReproducir.setEnabled(false);
            btnGrabar.setEnabled(true);
            btnParar.setEnabled(false);
        }else if(est == Estado.GRABANDO){
            btnReproducir.setEnabled(false);
            btnGrabar.setEnabled(false);
            btnParar.setEnabled(true);

        }else if(est == Estado.REPRODUCIENDO){
            btnReproducir.setEnabled(false);
            btnGrabar.setEnabled(false);
            btnParar.setEnabled(true);
        }else if(est == Estado.GRABACIONFINALIZADA){
            btnReproducir.setEnabled(true);
            btnGrabar.setEnabled(true);
            btnParar.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGrabar:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    //No tengo permisos -> los pido

                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_MICROPHONE);

                }else{
                    //Tengo permisos
                    comenzarGrabacionDeAudio();

                }

                break;
            case R.id.btnReproducir:
                actualizarBotonesDeReproducción(Estado.REPRODUCIENDO);
                estadoBotones = Estado.REPRODUCIENDO;
                FileInputStream fileInputStream = null;
                mediaPlayer = new MediaPlayer();
                try {
                    fileInputStream = new FileInputStream(file_name);
                    mediaPlayer.setDataSource(fileInputStream.getFD());
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnParar:
                if(estadoBotones == Estado.GRABANDO){
                    // Esta grabando
                    finalizarGrabacionDeAudio();
                    btnGuardar.setEnabled(comprobarSiPuedeGuardar());
                }else{
                    // Esta reproduciendo
                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                }
                actualizarBotonesDeReproducción(Estado.GRABACIONFINALIZADA);
                break;
            case R.id.btnTomarFoto:
                listener.sacarGuardarFoto();
                break;
            case R.id.btnBuscarCoordenadas:
                listener.obtenerCoordenadas();
                break;
            case R.id.btnGuardar:
                saveOrUpdateReclamo();
                break;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MICROPHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //yay, me dieron permisos. Ahora puedo hacer cosas.
                    
                    comenzarGrabacionDeAudio();
                }else{
                    //No me los dieron >:( hay que pedir de nuevo >:)
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_MICROPHONE);

                }
                break;
        }
    }


    private void finalizarGrabacionDeAudio() {
        rec.stop();
        rec.release();
        rec = null;
    }
    private void comenzarGrabacionDeAudio() {
        actualizarBotonesDeReproducción(Estado.GRABANDO);
        estadoBotones = Estado.GRABANDO;

        String file_path=getActivity().getApplicationContext().getFilesDir().getPath();
        File file= new File(file_path);

        Long date=new Date().getTime();
        Date current_time = new Date(Long.valueOf(date));

        rec=new MediaRecorder();

        rec.setAudioSource(MediaRecorder.AudioSource.MIC);
        rec.setAudioChannels(1);
        rec.setAudioSamplingRate(8000);
        rec.setAudioEncodingBitRate(44100);
        rec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        if (!file.exists()){
            file.mkdirs();
        }

        String s =file+"/"+current_time+".3gp";
        rec.setOutputFile(s);

        try {
            rec.prepare();
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }
        rec.start();
        file_name = s;
    }



    private void cargarReclamo(final int id){
        if( id >0){
            Runnable hiloCargaDatos = new Runnable() {
                @Override
                public void run() {
                    reclamoActual = reclamoDao.getById(id);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mail.setText(reclamoActual.getEmail());
                            tvCoord.setText(reclamoActual.getLatitud()+";"+reclamoActual.getLongitud());
                            reclamoDesc.setText(reclamoActual.getReclamo());
                            setImagePath(reclamoActual.getImagePath());
                            file_name = reclamoActual.getVoicePath();
                            if(file_name != null){
                                estadoBotones = Estado.GRABACIONFINALIZADA;
                                actualizarBotonesDeReproducción(estadoBotones);
                            }
                            Reclamo.TipoReclamo[] tipos= Reclamo.TipoReclamo.values();
                            for(int i=0;i<tipos.length;i++) {
                                if(tipos[i].equals(reclamoActual.getTipo())) {
                                    tipoReclamo.setSelection(i);
                                    break;
                                }
                            }
                        }
                    });
                }
            };
            Thread t1 = new Thread(hiloCargaDatos);
            t1.start();
        }else{
            String coordenadas = "0;0";
            if(getArguments()!=null) coordenadas = getArguments().getString("latLng","0;0");
            tvCoord.setText(coordenadas);
            reclamoActual = new Reclamo();
        }

    }

    private void saveOrUpdateReclamo(){
        reclamoActual.setEmail(mail.getText().toString());
        reclamoActual.setReclamo(reclamoDesc.getText().toString());
        reclamoActual.setTipo(tipoReclamoAdapter.getItem(tipoReclamo.getSelectedItemPosition()));
        reclamoActual.setImagePath(pathOfPhoto);
        reclamoActual.setVoicePath(file_name);
        if(tvCoord.getText().toString().length()>0 && tvCoord.getText().toString().contains(";")) {
            String[] coordenadas = tvCoord.getText().toString().split(";");
            reclamoActual.setLatitud(Double.valueOf(coordenadas[0]));
            reclamoActual.setLongitud(Double.valueOf(coordenadas[1]));
        }
        Runnable hiloActualizacion = new Runnable() {
            @Override
            public void run() {

                if(reclamoActual.getId()>0)
                    reclamoDao.update(reclamoActual);
                else
                    reclamoDao.insert(reclamoActual);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // limpiar vista
                        mail.setText(R.string.texto_vacio);
                        tvCoord.setText("0.0;0.0");
                        reclamoDesc.setText(R.string.texto_vacio);
                        imageView.setImageBitmap(null);
                        pathOfPhoto = null;
                        file_name = null;
                        estadoBotones = Estado.NADA;
                        actualizarBotonesDeReproducción(estadoBotones);

                        boolean edicionActivada = !tvCoord.getText().toString().equals("0.0;0.0");
                        reclamoDesc.setEnabled(edicionActivada );
                        mail.setEnabled(edicionActivada );
                        tipoReclamo.setEnabled(edicionActivada);
                        btnGuardar.setEnabled(edicionActivada);

                        getActivity().getFragmentManager().popBackStack();
                    }
                });
            }
        };
        Thread t1 = new Thread(hiloActualizacion);
        t1.start();
    }



}
