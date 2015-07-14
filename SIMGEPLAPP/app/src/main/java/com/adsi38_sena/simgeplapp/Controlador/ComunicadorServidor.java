package com.adsi38_sena.simgeplapp.Controlador;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ComunicadorServidor {

    //Atributos
    private String IPv4;//se consigue la direccion mediante el comando "ipconfig" en cmd, el IPv4 de la conexion LAN

    InputStream corriente_datos_entrantes = null;
    String result = "";

    //Metodos

    //Construtor
    public ComunicadorServidor(String dirIP){//constructor de la clase comunicacion al servidor
        this.IPv4 = dirIP;//supongo que es buen punto para definir la ip //esta se cambia cuando el servidor cambia de terminal
    }


    public int loggin(String user, String pass) throws JSONException, IOException {
        String url = "http://"+IPv4+"/Servidor_Simgeplapp/login.php";

        int isInDB;//se puede loggear?, existe en base de datos?

        ArrayList<NameValuePair> valoresEnviados= new ArrayList<NameValuePair>();
        valoresEnviados.add(new BasicNameValuePair("user", user));
        valoresEnviados.add(new BasicNameValuePair("pass", pass));

        JSONArray jarray = getDatosRespServidor(url, valoresEnviados);//metodo que obtiene la respuesta del servidor
        if(jarray != null && jarray.length() > 0){
            JSONObject jobj = jarray.getJSONObject(0);//viene con un array codificado en json que contiene en este caso un solo indice (un solo valor)
            isInDB = jobj.getInt("logged");//el json viene desde el php con un entero entre 0 y 1
            if(isInDB == 1){//por convencion 1 es verdadero, si existe en bd me envia 1
                return 1;
            } else {
                return 0;//0 es que no existe en la base de datos
            }
        }else {
            return -1;//-1 significa que no obtuvo respuesta del servidor
        }

    }

    //metodo que obtiene en un JSonArray el arreglo confeccionado y enviado por el fichero php en el servidor
    protected JSONArray getDatosRespServidor(String url_servidor, ArrayList<NameValuePair> parametros) throws IOException {
        getRespServidor(url_servidor, parametros);//definira la variable "corriente_datos_entrantes"
        if (corriente_datos_entrantes != null) {//s� obtuvo una respuesta
            desglosarRespuesta();//basicamente convierte la corriente de informacion entrante en una lista despuesta en un String donde cada reglon es una pareja nombre-valor
            return getJsonArray();//se decodifica esta lista en un objeto arreglo JSon
        } else {
            return null;
        }
    }

    //peticion HTTP
    protected void getRespServidor(String url_servidor, ArrayList<NameValuePair> parametros) throws IOException {
        //conecta via http y envia un post.
        HttpClient cliente_web = new DefaultHttpClient();
        HttpPost peticion_post = new HttpPost(url_servidor);
        peticion_post.setEntity(new UrlEncodedFormEntity(parametros));//se codifica en forma de form
        //ejecuto peticion enviando datos por POST
        HttpResponse respuesta_servidor = cliente_web.execute(peticion_post);
        HttpEntity entidad_respuesta = respuesta_servidor.getEntity();
        corriente_datos_entrantes = entidad_respuesta.getContent();
    }

    protected void desglosarRespuesta() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(corriente_datos_entrantes, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            corriente_datos_entrantes.close();
            result = sb.toString();
    }

    protected JSONArray getJsonArray() {
        //parse json data
        try {
            JSONArray jArray = new JSONArray(result);
            return jArray;
        } catch (JSONException e) {
            return null;
        }
    }

    public void setIPv4(String IPv4) {
        this.IPv4 = IPv4;
    }

    public String getIPv4() {
        return IPv4;
    }

}