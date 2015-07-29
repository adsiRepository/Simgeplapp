package com.adsi38_sena.simgeplapp.Controlador.ComunicacionServidor;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.adsi38_sena.simgeplapp.Controlador.SalvaTareas;
import com.adsi38_sena.simgeplapp.Vistas.ActivityLogin;
import com.adsi38_sena.simgeplapp.Modelo.SIMGEPLAPP;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class AsyncLoggin extends AsyncTask<String, String, Boolean> {

    SIMGEPLAPP simgeplapp;

    private FragmentoCargaServidor dialogo_carga;
    private ActivityLogin myActy;

    ComunicadorServidor server;

    private String user_process, pass_onprocess;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        simgeplapp = (SIMGEPLAPP) myActy.getApplication();
        server = new ComunicadorServidor();
        dialogo_carga = new FragmentoCargaServidor();
        dialogo_carga.show(myActy.getFragmentManager(), SIMGEPLAPP.CargaSegura.TAG_PROGRESO_LOGGIN);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean pro;
        try {//obtenemos user_process y pass_onprocess
            user_process = params[0];
            pass_onprocess = params[1];
            //SystemClock.sleep(4000);
            //enviamos, recibimos y analizamos los datos en segundo plano.
            switch (server.intentoLoggeo(user_process, pass_onprocess)) {//ejecuto el metodo intentoLoggeo del objeto server este instanciado de la clase "ComunicadorServidor"
                case -1:
                    publishProgress(new String[]{"No se obtuvo respuesta del servidor"});
                    pro = false;
                    break;
                case 0:
                    publishProgress(new String[]{"El usuario no existe en la Base de Datos"});
                    pro = false;
                    break;
                case 1:
                    publishProgress(new String[]{"Autenticado"});
                    pro = true;
                    break;
                default:
                    publishProgress(new String[]{"Error desconocido"});
                    pro = false;
                    break;
            }
        } catch (ConnectTimeoutException e){
            publishProgress(new String[]{e.getMessage()+". Tiempo de espera terminado"});
            pro = false;
        } catch (ClientProtocolException e) {
            publishProgress(new String[]{e.getMessage()+". El Dominio no Existe o no esta disponible el Servidor"});
            pro = false;
        } catch (UnsupportedEncodingException e) {
            publishProgress(new String[]{e.toString()});
            pro = false;
        } catch (IOException e) {
            publishProgress(new String[]{e.toString()});
            pro = false;
        } catch (Exception e) {
            publishProgress(new String[]{e.toString()});
            pro = false;
        }
        return pro;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Toast.makeText(simgeplapp.getApplicationContext(), values[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(myActy != null) {
            if (result == true) {
                // if(simgeplapp.sessionAlive == false) {
                simgeplapp.session = new SIMGEPLAPP.Session();//Inicializo el objeto de session de la aplicacion
                simgeplapp.session.user = user_process;
                //configura los detalles de sesion que se conservaran durante reinicios y demas hasta que el usuario decida abandonar
                SharedPreferences confUser = myActy.getSharedPreferences("mi_usuario", myActy.MODE_PRIVATE);
                SharedPreferences.Editor editor = confUser.edit();
                editor.putString("usuario", user_process);
                editor.putString("onsesion", "ok");//guardo en las preferencias q la sesion esta iniciada para por si se apaga el cel por ejemplo, al volver no deba iniciar de nuevo
                editor.commit();
                simgeplapp.sessionAlive = true;
                myActy.onLogged();
            } else {
                //loggError();
                Toast.makeText(myActy.getApplicationContext(), "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
            dialogo_carga.cerrarCarga(myActy.getFragmentManager());
        }

            SalvaTareas.obtenerInstancia().removerProcesoLoggin(SIMGEPLAPP.CargaSegura.LLAVE_PROCESO_LOGIN);
    }

    @Override
    protected void onCancelled() {

    }

    public void setMyActy(Activity myActy) {
        this.myActy = (ActivityLogin) myActy;
    }

    public void desAdjuntar() {
        this.myActy = null;
    }

}
