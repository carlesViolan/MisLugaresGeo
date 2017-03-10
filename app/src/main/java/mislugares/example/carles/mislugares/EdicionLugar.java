package mislugares.example.carles.mislugares;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class EdicionLugar extends AppCompatActivity {

    long id;

    EditText nombrelugar;
    Spinner Logo_tipo;
    EditText direccion ;
    EditText telefono;
    EditText url;
    EditText comentario ;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);

        nombrelugar = (EditText) findViewById(R.id.nombre);

        Logo_tipo = (Spinner) findViewById(R.id.tipo);

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Logo_tipo.setAdapter(adaptador);

        direccion = (EditText) findViewById(R.id.direccion);
        telefono = (EditText) findViewById(R.id.telefono);
        url = (EditText) findViewById(R.id.url);
        comentario = (EditText) findViewById(R.id.comentario);

        id = getIntent().getIntExtra("id", 0);

        Log.i("valor","de la id fuera de if "+id);

        if (id != -1) {

            Lugar l = Lugares.vectorLugares.get((int) id);

            nombrelugar.setText(l.getNombre());
            // Logo_tipo.setSelection(1).getTexto();
            direccion.setText(l.getDireccion());
            telefono.setText(l.getTelefono() + "");
            url.setText(l.getUrl());
            comentario.setText(l.getComentario());

            String tipo = l.getTipo().getTexto();
            int i;
            for (i = 0; i < TipoLugar.getNombres().length && !tipo.equals(TipoLugar.getNombres()[i]); i++);

            Logo_tipo.setSelection(i);
        }
    }

//    public void lanzarAcercaDe(View view){
//
//        Intent i = new Intent(this, AcercaDe.class);
//        startActivity(i);
//    }

    public void modificarLugar(){

        Lugar l = Lugares.vectorLugares.get((int) id);

        l.setNombre(nombrelugar.getText().toString());
        l.setDireccion(direccion.getText().toString());
        try {
            l.setTelefono(Integer.parseInt(telefono.getText().toString()));
        } catch (Exception e) {
        }
        l.setUrl(url.getText().toString());
        l.setComentario(comentario.getText().toString());
        l.setTipo(TipoLugar.getTipo((String) Logo_tipo.getSelectedItem()));
    }

    private void crearLugar() {

        Lugar l = new Lugar();

        l.setNombre(nombrelugar.getText().toString());
        l.setDireccion(direccion.getText().toString());
        try {
            l.setTelefono(Integer.parseInt(telefono.getText().toString()));
        } catch (Exception e) {
        }
        l.setUrl(url.getText().toString());
        l.setComentario(comentario.getText().toString());
        l.setTipo(TipoLugar.getTipo((String) Logo_tipo.getSelectedItem()));

        Lugares.vectorLugares.add(l);
    }
//primer exercici x mostrar un item de la llista

    //    public void lanzarVistaLugar(View view){
//
//        Intent i = new Intent(this, VistaLugar.class);
//        i.putExtra("id",(long)0);
//        startActivity(i);
//    }
    public void lanzarVistaLugar(View view){
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Seleccion de lugar")
                .setMessage("indica su id:")
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int whichButton){

                          long id = Long.parseLong(entrada.getText().toString());
                          Intent i = new Intent (EdicionLugar.this, VistaLugar.class);
                          i.putExtra("id", id);
                          startActivity(i);
                }})
                .setNegativeButton("Cancelar", null)
                .show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.edicion_lugar, menu);
        return true;//el menu esta visible

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id== R.id.guardar){

            if (this.id == -1) crearLugar();

            else modificarLugar();

            try {

                FileOutputStream fos = getApplicationContext().openFileOutput("datos.dat", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(Lugares.vectorLugares);
                os.close();
                fos.close();

            } catch (Exception e) {

            }

            finish();
            return true;
        }

        if(id == R.id.cancelar){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
