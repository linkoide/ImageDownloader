package com.izv.android.imagedownloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class principal extends Activity {

    private EditText url , nombre;
    private RadioButton privada , publica;
    private Button boton;
    private ProgressDialog dialogo;
    private String rutaMovil;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        url = (EditText)findViewById(R.id.et_url);
        nombre = (EditText)findViewById(R.id.et_nombre);
        privada = (RadioButton)findViewById(R.id.rb_privada);
        publica = (RadioButton)findViewById(R.id.rb_publica);
        boton = (Button)findViewById(R.id.bt_descargar);
        iv = (ImageView)findViewById(R.id.iv_foto);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class HiloFacil extends AsyncTask<Object, Integer, String> {

        HiloFacil(String... p) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialogo = new ProgressDialog(principal.this);
            dialogo.setMessage("Descargando imagen de la URL");
            dialogo.show();
        }

        @Override
        protected String doInBackground(Object[] params) {
            try {
                String web = url.getText().toString();
                URL url = new URL(web);
                String nombreImg = comprobarNombre(web);
                String extension = obtenerExtension(nombreImg);
                URLConnection urlCon = url.openConnection();

                if(extension.equals("jpg")||extension.equals("png")||extension.equals("gif")){
                    InputStream is = urlCon.getInputStream();

                    if (privada.isChecked()) {
                        rutaMovil = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +"/"+nombreImg;

                    } else if(publica.isChecked()){
                   rutaMovil = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()+"/"+ nombreImg;

                    }
                    FileOutputStream fos = new FileOutputStream(rutaMovil);

                    byte[] array = new byte[1000];
                    int leido = is.read(array);
                    while (leido > 0) {
                        fos.write(array, 0, leido);
                        leido = is.read(array);
                    }
                    is.close();
                    fos.close();

                }else{
                    Log.v("Extension: ", "Error en el tipo de extensión");
                }

            } catch (Exception e) {
                Log.v("LOG: ",e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            File imgFile = new  File(rutaMovil);

            iv.setImageURI(Uri.fromFile(imgFile));


            // iv.setImageURI(Uri.fromFile(new File(rutaMovil)));
            //o también
            //iv.setImageURI(Uri.parse(rutaMovil));

            super.onPostExecute(s);
            dialogo.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }
    }

    public String comprobarNombre(String web){
        String nombreImg;
        String[] elementosWeb = web.split("/");

        if(nombre.getText().toString().isEmpty() || nombre.getText().toString()==null  ||  nombre.getText().toString().trim().length()==0){
            nombreImg = elementosWeb[elementosWeb.length-1];
        }else{
            String extension=obtenerExtension(elementosWeb[elementosWeb.length-1]);
            nombreImg = nombre.getText().toString()+"."+extension;
        }

        return nombreImg;


    }

    public String obtenerExtension(String nombreImg){

        String extension = nombreImg.substring(nombreImg.length()-3);
        return extension;
    }

    public void guardar(View v) {
        if(url.getText().toString().compareToIgnoreCase("")!=0){
            HiloFacil hf = new HiloFacil();
            hf.execute();
        }else{
            Toast.makeText(this, "Introduzca una URL correcta", Toast.LENGTH_SHORT).show();
        }
    }
}
