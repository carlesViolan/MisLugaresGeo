package mislugares.example.carles.mislugares;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by carles on 19/07/2016.
 */

public class VistaLugar extends AppCompatActivity {

    int id;
    private ImageView imageView;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;


    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    private String KEY_foto= "foto";

    private Uri uriFoto;

    Lugar l = new Lugar();
    @Override

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vista_lugar);

        id = (int) getIntent().getLongExtra("id",0);
        l = Lugares.vectorLugares.get((int) id);
        KEY_foto +=id;
       // Log.i("KEY", KEY_foto);

        imageView = (ImageView) findViewById(R.id.foto);
        actualizarVistas();
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        editor = settings.edit();

        String foto= settings.getString(KEY_foto, null);
//     l.setFoto(foto);
//     Log.i("no entro ", "foto");

        if(foto!=null){
              // Log.i("entro ", foto);
//            ponerFoto(imageView, foto);

            imageView.setImageBitmap( StringToBitmap(foto));
        }
    }

    public Bitmap StringToBitmap(String s){

        try{

            byte[] encodeByte = Base64.decode(s, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(encodeByte , 0, encodeByte.length);


        }catch (NullPointerException e){

            return null;

        }catch (OutOfMemoryError e){

            return null;
        }


    }

    public void galeria(View view){

        Log.i("activity","gal");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,RESULTADO_GALERIA);

    }

    public void actualizarVistas() {

        TextView nombrelugar = (TextView) findViewById(R.id.nombrelugar);
        TextView Logo_tipo = (TextView) findViewById(R.id.Logo_tipo);
        TextView direccion = (TextView) findViewById(R.id.direccion);
        TextView telefono = (TextView) findViewById(R.id.telef);
        TextView url = (TextView) findViewById(R.id.url);
        TextView comentario = (TextView) findViewById(R.id.comentario);
        TextView fecha = (TextView) findViewById(R.id.f1);
        TextView hora = (TextView) findViewById(R.id.h1);

        nombrelugar.setText(l.getNombre());
        Logo_tipo.setText(l.getTipo().getTexto());
        //Log.e("JD",l.getDireccion());
        direccion.setText(l.getDireccion());
        telefono.setText(l.getTelefono() + "");
        url.setText(l.getUrl());
        comentario.setText(l.getComentario());
        fecha.setText(l.getFecha());
        hora.setText(l.getHour());

        ponerFoto(imageView, l.getFoto());
    }


    protected void onActivityResult(int requestCode, int ResultCode, Intent data){

        Log.i("activity","result");

        if (requestCode == RESULTADO_EDITAR){

            actualizarVistas();
            findViewById(R.id.scrollView).invalidate();

        }else if(requestCode == RESULTADO_GALERIA && ResultCode == Activity.RESULT_OK){

            try{

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap img = Bitmap.createScaledBitmap(selectedImage, 500,500,false);
                imageView.setImageBitmap(img);

//save bitmap to shared preferences
                String base64img = bitmaptoBase64(img);
                editor.putString(KEY_foto, base64img);
                editor.commit();
              //  Log.i("foto", " "+ settings.getString(KEY_foto,"DEFAULT"));


            }catch (FileNotFoundException e){}


//            l.setFoto(data.getDataString());
//            ponerFoto(imageView, l.getFoto());
//
//            editor.putString(KEY_foto, l.getFoto());
//            editor.commit();
//            Log.i("foto", " "+ settings.getString(KEY_foto,"DEFAULT"));


        }
        else if (requestCode == RESULTADO_FOTO && ResultCode == Activity.RESULT_OK
                && l !=null && uriFoto!=null){

            l.setFoto(uriFoto.toString());
            ponerFoto(imageView, l.getFoto());
        }

    }

    private String bitmaptoBase64(Bitmap img) {

        try{

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;

        }catch (NullPointerException e){

            return null;

        }catch (OutOfMemoryError e){}

        return null;
    }

 //metode per cridar fotos desde galeria
    protected void  ponerFoto (ImageView imageView, String uri){

        if(uri !=null){

        imageView.setImageURI(Uri.parse(uri));

        }else{

        imageView.setImageBitmap(null);

        }
    }

 //metode per cridar fotos desde camara

    public void tomarFoto(View view) {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "img_"
                + (System.currentTimeMillis() / 1000) + ".jpg"));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

 //metode per eliminar foto
    public void eliminarFoto(View view){

        l.setFoto(null);
        ponerFoto(imageView, null);

    }
 //funcio que refresca els canvis al tancar
 @Override
//    protected void onResume() { // o restart, la que va despres de l'onPause
//
//        super.onResume();
//
//        id = (int) getIntent().getLongExtra("id",0);
//        Lugar l = Lugares.vectorLugares.get((int) id);
//
//        TextView nombrelugar = (TextView)findViewById(R.id.nombrelugar);
//        TextView Logo_tipo = (TextView) findViewById(R.id.Logo_tipo);
//        TextView direccion = (TextView) findViewById(R.id.direccion);
//        TextView telefono = (TextView) findViewById(R.id.telef);
//        TextView url = (TextView) findViewById(R.id.url);
//        TextView comentario = (TextView) findViewById(R.id.comentario);
//        TextView fecha = (TextView) findViewById(R.id.f1);
//        TextView hora = (TextView) findViewById(R.id.h1);
//
//        nombrelugar.setText(l.getNombre());
//        //Log.e("JD",l.getDireccion());
//        direccion.setText(l.getDireccion());
//        telefono.setText(l.getTelefono()+"");
//        url.setText(l.getUrl());
//        comentario.setText(l.getComentario());
//        fecha.setText(l.getFecha());
//        hora.setText(l.getHour());
//        Logo_tipo.setText(l.getTipo().getTexto());
//    }

 //funcio que mostra el Menu
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.vista_lugar,menu);
        return true;
    }
    public void llamadatelefono(View view){

        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel: "+ l.getTelefono())));


    }
    public void pgWeb(View view){

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(l.getUrl())));
    }

    //metode que obre googleMaps
    public void verMapa(View view){

        Uri uri;
        double lat = l.getPosicion().getLatitud();
        double lon = l.getPosicion().getLongitud();

        if(lat !=0  || lon!=0){

            uri = Uri.parse("geo: " +lat + ", " + lon);
        }else{

            uri =Uri.parse("geo: 0,0?q= "+l.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

//metode que les opcions del Menu
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){

            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, l.getNombre() + " - " +l.getUrl());
                startActivityForResult(intent,RESULTADO_EDITAR);
                return true;

            case R.id.accion_llegar:
                verMapa(null);
                return  true;

            case R.id.accion_editar:

                Log.i("valor","de la "+id);
                //id = Long.parseLong(entrada.getText().toString());
                Intent i = new Intent (VistaLugar.this, EdicionLugar.class);
                i.putExtra("id", id);
                startActivityForResult(i,RESULTADO_EDITAR);
                return  true;

            case R.id.accion_borrar:
                new AlertDialog.Builder(this)
                        .setTitle("Está seguro que desea eliminar este lugar?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int whichButton){
                                Lugares.borrar((int) id);
                                finish();
                            }})
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

}
