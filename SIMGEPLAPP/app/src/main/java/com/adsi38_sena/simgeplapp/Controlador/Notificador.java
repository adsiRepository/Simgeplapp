package com.adsi38_sena.simgeplapp.Controlador;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.adsi38_sena.simgeplapp.Modelo.SIMGEPLAPP;
import com.adsi38_sena.simgeplapp.R;
import com.adsi38_sena.simgeplapp.Vistas.ActivityMonitoreo;

public class Notificador {

    //http://donnierock.com/2012/07/25/programando-un-activity-de-android-para-que-se-abra-a-una-hora-definida/

    //private SIMGEPLAPP simgeplapp;

    public Notificador(/*SIMGEPLAPP app*/){
      //  this.simgeplapp = app;
    }

    public void notificarAlertaPlanta(Service service, double[] variables) {
        //fuentes => http://androcode.es/2012/09/notificaciones-metodo-tradicional-notification-builder-y-jelly-bean/
        try {
            //se supone que debe enviar notificacion al obtener lecturas extrañas:
            Bitmap icon = BitmapFactory.decodeResource(service.getResources(), R.drawable.img_notif);

            //FUENTE DE SONIDOS => http://soundbible.com/
            //Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);//sonido

            Intent to_Monitoreo = new Intent(service, ActivityMonitoreo.class);
            to_Monitoreo.putExtra("en_espera", true);
            to_Monitoreo.putExtra("temperatura", variables[0]);
            to_Monitoreo.putExtra("presion", variables[1]);
            to_Monitoreo.putExtra("nivel", variables[2]);
            to_Monitoreo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent acty_pendiente = PendingIntent.getActivity(service, /*codigoPeticion*/0,
                    /*intent*/to_Monitoreo, /*flags*/ PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager mngNotif = (NotificationManager) service.getSystemService(service.NOTIFICATION_SERVICE);

            NotificationCompat.Builder constructorNotificacion = new NotificationCompat.Builder(service)
                    .setContentIntent(acty_pendiente)
                    .setTicker("Alerta Simgeplap")
                    .setSmallIcon(R.drawable.icon_notif)
                    .setContentTitle("Notificacion Simgeplapp")
                    .setContentText("Ha habido un sobresalto en la planta")
                    .setWhen(System.currentTimeMillis())
                            //sonido personalizado; archivo ubicado en la carpeta raw
                    .setSound(Uri.parse("android.resource://" + service.getPackageName() + "/" + R.raw.sound_alarm_1))
                    .setVibrate(new long[]{800, 500, 900, 150, 1100})//tiempos entre vibracion y descanso
                    .setLargeIcon(icon)
                    .setLights(Color.RED, 1, 0);

            mngNotif.notify(SIMGEPLAPP.NOTIFICACIONES.ID_NOTIFICACION_ALERTA, constructorNotificacion.build());

        } catch (Exception eh) {
            Toast.makeText(service.getApplicationContext(), eh.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void notificarPerdida_deConexion(Service context){
        try {
            NotificationManager mngNotif = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            //RemoteViews vistaNotificacion = new RemoteViews(context.getPackageName(), R.layout.custom_view_notif);

            NotificationCompat.Builder constructorNotificacion = new NotificationCompat.Builder(context)
                    .setTicker("falla de conexion Simgeplapp")
                    .setSmallIcon(R.drawable.err_conn)
                    .setContentTitle("No hay Conexion a Internet")
                    .setContentText("Reinicia el Servicio al tener conexion nuevamente.")
                    .setSubText("No hay Conexion a Internet en estos momentos, por lo tanto el servicio se detendra." +
                            "Asegurate de reactivarlo al coseguir conexion nuevamente.")
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                            //sonido personalizado; archivo ubicado en la carpeta raw
                            //FUENTE DE SONIDOS => http://soundbible.com/
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sound_error_a))
                    .setVibrate(new long[]{600, 500,2000})//tiempos entre vibracion y descanso
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_fail_conex))
                    .setLights(Color.RED, 1, 0);


            mngNotif.notify(SIMGEPLAPP.NOTIFICACIONES.ID_NOTIFICACION_PERDIDA_CONEXION, constructorNotificacion.build());

        } catch (Exception eh) {
            Toast.makeText(context.getApplicationContext(), eh.toString(), Toast.LENGTH_LONG).show();
        }
    }


}


