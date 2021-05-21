package com.synergy.wmc.wmc_plugin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class ApiWmcModule {

    private static final String E_TOKEN_ERROR = "E_TOKEN_ERROR";
    private static final String E_HTTP_ERROR = "HttpResponseException";
    private static final String E_HOST_ERROR = "HttpHostException";
    private static String mcc = "";
    private static String mnc = "";
    private static String unique = "";
    private static final String E_SESSION_ERROR = "E_TOKEN_ERROR";
    private static final String E_REQUEST_ERROR = "E_TOKEN_ERROR";
    private static final String UrlWmc = "https://api.worldmobileconnection.com/v2/";
    private static String device_name = "";
    private static String device_os = "";
    private static String appid = "";
    private static String app_Version = "";
    private static String language = "";
    private static String network_type= "";

    public static void initialize (Context context ,String apikey, ResultInitListener listener) {
        try {
            // TODO Logic to get token from proxy
            // Return a JSON with token: {token: '32978423984khsdfkhsakdf'}
            Log.e("ApiWMC/getToken","api key "+apikey);

            Token token =  getToken(apikey);

            Log.e("ApiWMC/getToken","context "+context);

            if(token != null){
                new SessionManager(context).setApiKey(apikey);
                listener.onSuccess("Inicializacion correcta");
            }

        }  catch (Exception e) {
            Log.e("ApiWMC/getToken","error",e);
            // Return real error
            listener.onError(E_TOKEN_ERROR,"E_TOKEN_ERROR", new Exception("Error get token"));
        }

    }

    public static Token getToken(String apikey){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(UrlWmc+"gettoken");
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("apikey", apikey));
            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = client.execute(post);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            Log.e("ApiWMC/getToken","token react "+result.toString());
            Log.e("ApiWMC/getTokenCODE", response.getStatusLine().getStatusCode() + "");
            if(response.getStatusLine().getStatusCode() != 409 //Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 412 // Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 503 // mantenimiento
                    && response.getStatusLine().getStatusCode() != 407){ //
                Gson gson = new Gson();
                Token token = gson.fromJson(result.toString(),Token.class);
                return token;
            }
        }catch (HttpResponseException ht){
            //promise.reject(E_HTTP_ERROR,String.valueOf(ht.getStatusCode()));
        } catch (Exception e) {
            Log.e("ApiWMC/getToken","error",e);
            // Return real error
        }
        return null;
    }

    /**
     * Obtiene una session valida otorgada por WMC
     * @param token token valido
     */
    public static Session  getSession (Context context ,String token) {
        try {
            // TODO Logic to get session from proxy
            HttpClient client = new DefaultHttpClient();
            //HttpPost post = new HttpPost(UrlWmc+"getuser");
            Log.e("ApiWMC/getSession","token "+token);
            //Inicializa valores de mcc, mnc y unique
            getInfoSim(context);
            setDetailDevice();
            //BEGIN Tests simulator
            if(mcc == null || mcc.isEmpty()){
                mcc = "214";
            }
            if(mnc == null || mnc.isEmpty()){
                mnc = "07";
            }
            if(unique == null || unique.isEmpty()){
                unique = "josem";
            }
/*
            if(latitude == null || latitude.isEmpty()){
                latitude = "41.3891";
            }
            if(longitude == null || longitude.isEmpty()){
                longitude = "2.1611";
            }
*/
            //END Tests simulator

            String connection = getTransport(context);

            Log.e("ApiWMC/getSession","mcc "+mcc);
            Log.e("ApiWMC/getSession","mnc "+mnc);
            Log.e("ApiWMC/getSession","uniqid "+unique);
            Log.e("ApiWMC/getSession","transport "+connection);
            Log.e("ApiWMC/getSession","device_name "+device_name);
            Log.e("ApiWMC/getSession","device_os "+device_os);
            Log.e("ApiWMC/getSession","appid "+appid);
            Log.e("ApiWMC/getSession","app_Version "+app_Version);
            Log.e("ApiWMC/getSession","app_Version "+language);
            Log.e("ApiWMC/getSession","network_type "+network_type);
            /*Log.e("ApiWMC/getSession","latitud "+latitud);
            Log.e("ApiWMC/getSession","longitud "+longitud);*/


            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("token", token));
            urlParameters.add(new BasicNameValuePair("mcc", mcc));
            urlParameters.add(new BasicNameValuePair("mnc", mnc));
            urlParameters.add(new BasicNameValuePair("uniqid", unique));
            urlParameters.add(new BasicNameValuePair("transport",connection));
            urlParameters.add(new BasicNameValuePair("device_name",device_name));
            urlParameters.add(new BasicNameValuePair("device_os", device_os));
            urlParameters.add(new BasicNameValuePair("appid", appid));
            urlParameters.add(new BasicNameValuePair("app_Version",app_Version));
            /*urlParameters.add(new BasicNameValuePair("latitude",latitud + ""));
            urlParameters.add(new BasicNameValuePair("longitude",longitud + ""));*/
            urlParameters.add(new BasicNameValuePair("language",language));
            urlParameters.add(new BasicNameValuePair("network_type",network_type));
            HttpPost post = new HttpPost(UrlWmc+"getuser");

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            Log.e("ApiWMC/getSession",post.getURI().toString());



            HttpResponse response = client.execute(post);


            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            Log.e("ApiWMC/getSession",result.toString());

            Gson gson = new Gson();

            Session obj = gson.fromJson(result.toString(), Session.class);
            obj.setTransport(Session.Transport.valueOf(connection));
            obj.setCreated(new Date().getTime());

            new SessionManager(context).setSession(obj);
            return  obj;

        } catch (HttpResponseException ht){
        } catch (Exception e) {
            // Return real error
            Log.e("ApiWMC/getSession","error",e);
        }
        return null;
    }


    public static void request(Context context,String uri,String method,Map<String,String> options, Map<String,String> data,ResultRequestListener listener){

        Session.Transport transport = Session.Transport.valueOf(getTransport(context));
        SessionManager sessionManager = new SessionManager(context);
        Session session = sessionManager.getSession(transport);

        if(session == null){
            String apikey = sessionManager.getApiKey();
            Token token = getToken(apikey);
            session = getSession(context,token.getToken());
            if(session == null){
                listener.onError(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error get Session"));
            }
        }

        try{
            ServiceHttp serviceHttp = new ServiceHttp(session);
            serviceHttp.request(uri,method,options,data,listener);
        }catch (Exception e){
            listener.onError(E_REQUEST_ERROR,E_REQUEST_ERROR, e);
        }

    }

    public static void getResource(Context context,String uri, ResultRequestDataListener listener){
        Session.Transport transport = Session.Transport.valueOf(getTransport(context));
        SessionManager sessionManager = new SessionManager(context);
        Session session = sessionManager.getSession(transport);

        if(session == null){
            String apikey = sessionManager.getApiKey();
            Token token = getToken(apikey);
            session = getSession(context,token.getToken());
            if(session == null){
                listener.onError(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error get Session"));
            }
        }
        try{
            ServiceHttp serviceHttp = new ServiceHttp(session);
            serviceHttp.resource(uri,listener);
        }catch (Exception e){
            listener.onError(E_REQUEST_ERROR,E_REQUEST_ERROR, e);
        }
    }

    /**
     * Método que proporciona la información de mcc, mnc y unique del la sim del dispositivo.
     * @param context
     */
    private static void getInfoSim(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // obtención de informacion de sim del dispositivo
        TelephonyManager tel = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String networkOperator = tel.getSimOperator();
        unique = tel.getDeviceId();
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = networkOperator.substring(0, 3);
            mnc = networkOperator.substring(3);
        }
        network_type = Utils.getNetwork(context);
    }

    /**
     *
     * Método que obtiene el medio por el cual esta conectado el dispositivo
     * @param context
     * @return Wifi o Mobile
     */
    private static String getTransport(Context context){
       final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {

                    if (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI)){
                        return "wifi";
                    }else{
                        return "mobile";
                    }
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        return "wifi";
                    }else{
                        return "mobile";
                    }
                }
            }
        }
        return "wifi";
    }

    private static void setDetailDevice(){ //detalle de dispositivo
        device_name = DeviceName.getDeviceName(); //nombre de dispositovo
        device_os = Utils.currentVersion(); // version del sistema operativo
        appid = 'com.smarttest.spidy_app_movil'; // appid de la aplicación
        app_Version = '1.0'; // versión de la aplicación
        language = Locale.getDefault().getLanguage();
    }
}
