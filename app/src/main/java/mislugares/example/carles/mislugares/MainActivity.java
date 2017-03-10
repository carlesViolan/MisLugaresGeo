package mislugares.example.carles.mislugares;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener {

    EditText nombrelugar;
    Spinner Logo_tipo;
    EditText direccion;
    EditText telefono;
    EditText url;
    EditText comentario;

    private LocationManager manejador;
    private Location mejorLocaliz;
    private static final long DOS_MINUTOS = 2 * 60 * 100;

    public AdaptadorLugares adaptador;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.edicion_lugar);


        try {
            FileInputStream fis = getApplicationContext().openFileInput("datos.dat");
            ObjectInputStream is = new ObjectInputStream(fis);
            Lugares.vectorLugares = (List<Lugar>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {
            Log.e("HODFjsdfsDFSDF", 100000 + "");
            e.printStackTrace();
            Lugares.vectorLugares = Lugares.ejemploLugares();
        }

        setContentView(R.layout.activity_main);
        adaptador = new AdaptadorLugares(this);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adaptador);


        final Activity activity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(activity, VistaLugar.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        nombrelugar = (EditText) findViewById(R.id.nombre);
        Logo_tipo = (Spinner) findViewById(R.id.tipo);
        direccion = (EditText) findViewById(R.id.direccion);
        telefono = (EditText) findViewById(R.id.telefono);
        url = (EditText) findViewById(R.id.url);
        comentario = (EditText) findViewById(R.id.comentario);

        //Logo_tipo.setSelection(TipoLugar.getNombres());

//afegim un so quan es carregui aquesta activitat
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sonido);
        mp.start();
//funcions de localització
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.GPS_PROVIDER));
      }
       if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
       }

    }

    private void actualizaMejorLocaliz(Location localiz) {

        if (localiz != null && (mejorLocaliz == null || localiz.getAccuracy() < 2 * mejorLocaliz.getAccuracy()
                || localiz.getTime() - mejorLocaliz.getTime() > DOS_MINUTOS)) {

            Log.d(Lugares.TAG, "Nueva mejor localización");

            mejorLocaliz = localiz;

            Lugares.posicionActual.setLatitud(localiz.getLatitude());
            Lugares.posicionActual.setLongitud(localiz.getLongitude());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.activity_main);
        adaptador = new AdaptadorLugares(this);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adaptador);

 //variable afegida this
        final Activity activity = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(activity, VistaLugar.class);
                i.putExtra("id", id);
                startActivity(i);
            }
        });

        activarProveedores();
    }

    private void activarProveedores() {

        if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    20 * 1000, 5, this);
        }
        if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    10 * 1000, 10, this);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manejador.removeUpdates(this);
    }

    public void lanzarAcercaDe(View view) {

        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);
    }

    //    public void lanzarVistaLugar(View view){
//
//        Intent i = new Intent(this, VistaLugar.class);
//        i.putExtra("id",(long)0);
//        startActivity(i);
//    }
    public void lanzarVistaLugar(View view) {
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Seleccion de lugar")
                .setMessage("indica su id:")
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        long id = Long.parseLong(entrada.getText().toString());
                        Intent i = new Intent(MainActivity.this, VistaLugar.class);
                        i.putExtra("id", id);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();


    }


    public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
        Intent i = new Intent(this, VistaLugar.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;//el menu esta visible

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.action_settings) {

            return true;
        }

        if (id == R.id.acercaDe) {

            lanzarAcercaDe(null);
            return true;
        }

        if (id == R.id.menu_buscar) {

            lanzarVistaLugar(null);
            return true;
        }

        if (id == R.id.nuevo) {

            Intent i = new Intent(this, EdicionLugar.class);
            i.putExtra("id", -1);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        try {
//
//            FileOutputStream fos = getApplicationContext().openFileOutput("datos.dat", Context.MODE_PRIVATE);
//            ObjectOutputStream os = new ObjectOutputStream(fos);
//            os.writeObject(Lugares.vectorLugares);
//            os.close();
//            fos.close();
//
//        } catch (Exception e) {
//
//        }
    }

    //metodos de la interfaz LocationListener
    @Override
    public void onLocationChanged(Location location) {

        Log.d(Lugares.TAG, " Nueva localizacion: " + location);
        actualizaMejorLocaliz(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.d(Lugares.TAG, "Cambia estado: " + provider);
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Log.d(Lugares.TAG, "Se deshabilita: " + provider);
        activarProveedores();
    }

    @Override
    public void onProviderDisabled(String provider) {

        Log.d(Lugares.TAG, " Se habilita: " + provider);
        activarProveedores();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
//    }


//    public void modificarLugar(View view){
//        Lugar l = new Lugar();
//
//        l.setNombre(nombrelugar.getText().toString());
//        //Logo_tipo.(l.getTipo().getTexto());
//        l.setDireccion(direccion.getText().toString());
//        try {
//            l.setTelefono(Integer.parseInt(telefono.getText().toString()));
//        } catch (Exception e) {
//            l.setTelefono(0);
//        }
//
//        l.setUrl(url.getText().toString());
//        l.setComentario(comentario.getText().toString());
//
//        Lugares.vectorLugares.add(l);
//
//        finish();
//    }
}
