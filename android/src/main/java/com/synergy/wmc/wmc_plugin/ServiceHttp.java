package com.synergy.wmc.wmc_plugin;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.conn.params.ConnRoutePNames;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import okhttp3.Credentials;

public class ServiceHttp {
    private CloseableHttpClient httpclient;
    private RequestConfig config;
    private Session sesion;
    private RequestResponse requestResponse = null;

    private static final String E_TOKEN_ERROR = "E_TOKEN_ERROR";
    private static final String E_HTTP_ERROR = "HttpResponseException";
    private static final String E_HOST_ERROR = "HttpHostException";
    private static final String E_SESSION_ERROR = "E_TOKEN_ERROR";
    private static final String E_REQUEST_ERROR = "E_TOKEN_ERROR";

    public ServiceHttp(Session sesion) throws MalformedURLException, UnknownHostException {
        this.sesion = sesion;
        //configuracion de cliente con Proxy
        InetAddress address = InetAddress.getByName(new URL(sesion.getURL()).getHost());

        CredentialsProvider credentialsPovider = new BasicCredentialsProvider();

        credentialsPovider.setCredentials(new AuthScope(address.getHostAddress(), sesion.getPort()),
                new UsernamePasswordCredentials(sesion.getUsername(), sesion.getPassword()));

        HttpClientBuilder clientbuilder = HttpClients.custom();

        clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);

        httpclient = clientbuilder.build();

        HttpHost proxy = new HttpHost(address.getHostAddress(), sesion.getPort(), "http");

        RequestConfig.Builder reqconfigconbuilder= RequestConfig.custom();
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxy);
        config = reqconfigconbuilder.build();

        /*InetAddress address = InetAddress.getByName(new URL(sesion.getURL()).getHost());

        httpclient = new DefaultHttpClient();
        //settea usuario y contraseña
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(address.getHostAddress(), sesion.getPort()),
                new UsernamePasswordCredentials(sesion.getUsername(), sesion.getPassword()));

        HttpHost proxy = new HttpHost(address.getHostAddress(),  sesion.getPort());

        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);*/
    }


    public void request (String uri, String method, Map<String,String> options, Map<String,String> data,ResultRequestListener listener){
        MethodHttp methodHttp = MethodHttp.valueOf(method);

        Log.e("ApiWMC/get","session react "+sesion);
        Log.e("ApiWMC/get","Url react "+uri);
        Log.e("ApiWMC/get","client "+httpclient.toString());

        switch (methodHttp){
            case GET:
                get(uri,options,data,listener);
                break;
            case PUT:
                put(uri,options,data,listener);
                break;
            case POST:
                post(uri,options,data,listener);
                break;
            case DELETE:
                delete(uri,options,data,listener);
                break;
        }
    }


    private void post(String uri, Map<String,String> options, Map<String,String> data,ResultRequestListener listener){
        try {

            Gson gson=new GsonBuilder().create();
            String json = gson.toJson(data);


            HttpPost post = new HttpPost(uri);
            post.setConfig(config);

            //add headers
            post.setHeader("sessionid", sesion.getSessionid());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                post.setHeader(entry.getKey(),entry.getValue().toString());
            }

            //add data
            StringEntity params =new StringEntity(json);
            post.setEntity(params);

            HttpResponse response = httpclient.execute(post);

            if(response.getStatusLine().getStatusCode() != 409 //Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 412 // Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 503 // mantenimiento
                    && response.getStatusLine().getStatusCode() != 407){ //
                String result = EntityUtils.toString(response.getEntity(),
                        "UTF-8");

                System.out.println(result);


                Map<String, String> res = new HashMap<String, String>();
                res.put("status_code",String.valueOf(response.getStatusLine().getStatusCode()));
                res.put("data",result);
                res.put("headers",response.getAllHeaders().toString());

                requestResponse = new RequestResponse(res,null);
            }else{
                requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error request responseCode:"+response.getStatusLine().getStatusCode())));
            }
        } catch (HttpResponseException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HTTP_ERROR,String.valueOf(ht.getStatusCode()),ht));

        } catch (MalformedURLException mue){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,mue));
        } catch (UnknownHostException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,ht));
        } catch (Exception e) {
            requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, e));
        }

        if(requestResponse.getException() != null){
            ExceptionObject exceptionObject = requestResponse.getException();
            listener.onError(exceptionObject.getError(),exceptionObject.getMessage(),exceptionObject.getE());
        }else{
            listener.onSuccess(requestResponse.getResult());
        }
    }

    private void get(String uri, Map<String,String> options, Map<String,String> data,ResultRequestListener listener){

        try{
            HttpGet get = new HttpGet(uri);
            get.setConfig(config);

            String credential = Credentials.basic(sesion.getUsername(), sesion.getPassword());
            get.setHeader("sessionid",sesion.getSessionid());
            get.setHeader("Proxy-Authorization",credential);

            for (Map.Entry<String, String> entry : options.entrySet()) {
                get.setHeader(entry.getKey(),entry.getValue().toString());
            }

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : data.entrySet()) {

                urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }

            URI uriRequest = new URIBuilder(get.getURI()).addParameters(urlParameters).build();

            get.setURI(uriRequest);

            HttpResponse response = httpclient.execute(get);

            if(response.getStatusLine().getStatusCode() != 409 //Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 412 // Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 503 // mantenimiento
                    && response.getStatusLine().getStatusCode() != 407){ //
                String result = EntityUtils.toString(response.getEntity(),
                        "UTF-8");

                System.out.println(result);


                Map<String, String> res = new HashMap<String, String>();
                res.put("status_code",String.valueOf(response.getStatusLine().getStatusCode()));
                res.put("data",result);
                res.put("headers",response.getAllHeaders().toString());
                requestResponse = new RequestResponse(res,null);
            }else{
                requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error request responseCode:"+response.getStatusLine().getStatusCode())));
            }
        } catch (HttpResponseException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HTTP_ERROR,String.valueOf(ht.getStatusCode()),ht));

        } catch (MalformedURLException mue){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,mue));
        } catch (UnknownHostException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,ht));
        } catch (Exception e) {
            requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, e));
        }

        if(requestResponse.getException() != null){
            ExceptionObject exceptionObject = requestResponse.getException();
            listener.onError(exceptionObject.getError(),exceptionObject.getMessage(),exceptionObject.getE());
        }else{
            listener.onSuccess(requestResponse.getResult());
        }
    }

    private void put(String uri, Map<String,String> options, Map<String,String> data,ResultRequestListener listener){

        try {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(data);

            HttpPut put = new HttpPut(uri);
            put.setConfig(config);

            put.setHeader("sessionid", sesion.getSessionid());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                put.setHeader(entry.getKey(),entry.getValue().toString());
            }

            StringEntity params =new StringEntity(json);
            put.setEntity(params);

            HttpResponse response = httpclient.execute(put);

            if(response.getStatusLine().getStatusCode() != 409 //Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 412 // Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 503 // mantenimiento
                    && response.getStatusLine().getStatusCode() != 407){ //
                String result = EntityUtils.toString(response.getEntity(),
                        "UTF-8");

                System.out.println(result);


                Map<String, String> res = new HashMap<String, String>();
                res.put("status_code",String.valueOf(response.getStatusLine().getStatusCode()));
                res.put("data",result);
                res.put("headers",response.getAllHeaders().toString());
                requestResponse = new RequestResponse(res,null);
            }else{
                requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error request responseCode:"+response.getStatusLine().getStatusCode())));
            }
        } catch (HttpResponseException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HTTP_ERROR,String.valueOf(ht.getStatusCode()),ht));

        } catch (MalformedURLException mue){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,mue));
        } catch (UnknownHostException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,ht));
        } catch (Exception e) {
            requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, e));
        }

        if(requestResponse.getException() != null){
            ExceptionObject exceptionObject = requestResponse.getException();
            listener.onError(exceptionObject.getError(),exceptionObject.getMessage(),exceptionObject.getE());
        }else{
            listener.onSuccess(requestResponse.getResult());
        }
    }

    private void delete(String uri, Map<String,String> options, Map<String,String> data,ResultRequestListener listener){

        try {
            // conversion de map to object

            HttpDeleteWithBody delete = new HttpDeleteWithBody(uri);
            delete.setHeader("sessionid",sesion.getSessionid());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                delete.setHeader(entry.getKey(),entry.getValue().toString());
            }
            //post.setHeader("Proxy-Authorization",);

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }

            delete.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = httpclient.execute(delete);

            if(response.getStatusLine().getStatusCode() != 409 //Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 412 // Cuenta inactiva
                    && response.getStatusLine().getStatusCode() != 503 // mantenimiento
                    && response.getStatusLine().getStatusCode() != 407){ //
                String result = EntityUtils.toString(response.getEntity(),
                        "UTF-8");

                System.out.println(result);


                Map<String, String> res = new HashMap<String, String>();
                res.put("status_code",String.valueOf(response.getStatusLine().getStatusCode()));
                res.put("data",result);
                res.put("headers",response.getAllHeaders().toString());

                requestResponse = new RequestResponse(res,null);
            }else{
                requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error request responseCode:"+response.getStatusLine().getStatusCode())));
            }
        } catch (HttpResponseException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HTTP_ERROR,String.valueOf(ht.getStatusCode()),ht));

        } catch (MalformedURLException mue){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,mue));
        } catch (UnknownHostException ht){
            requestResponse = new RequestResponse(new ExceptionObject(E_HOST_ERROR,E_HTTP_ERROR,ht));
        } catch (Exception e) {
            requestResponse = new RequestResponse(new ExceptionObject(E_REQUEST_ERROR,E_REQUEST_ERROR, e));
        }
    }

    public void resource (String uri,ResultRequestDataListener listener){
        try {
            // conversion de map to object
            HttpGet get = new HttpGet(uri);
            get.setConfig(config);

            String credential = Credentials.basic(sesion.getUsername(), sesion.getPassword());
            get.setHeader("sessionid",sesion.getSessionid());
            get.setHeader("Proxy-Authorization",credential);

            URI uriRequest = new URIBuilder(get.getURI()).build();

            get.setURI(uriRequest);

            HttpResponse response = httpclient.execute(get);

            if (response.getStatusLine().getStatusCode() == 200){
                try{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    response.getEntity().writeTo(baos);
                    byte[] bytes = baos.toByteArray();

                    listener.onSuccess(bytes);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                listener.onError(E_REQUEST_ERROR,E_REQUEST_ERROR, new Exception("Error request responseCode:"+response.getStatusLine().getStatusCode()));
            }
        } catch (HttpResponseException ht){
            listener.onError(E_HTTP_ERROR,String.valueOf(ht.getStatusCode()),ht);
        } catch (MalformedURLException mue){
            listener.onError(E_HOST_ERROR,E_HTTP_ERROR,mue);
        } catch (UnknownHostException ht){
            listener.onError(E_HOST_ERROR,E_HTTP_ERROR,ht);
        } catch (Exception e) {
            listener.onError(E_REQUEST_ERROR,E_REQUEST_ERROR, e);
        }
    }
}
