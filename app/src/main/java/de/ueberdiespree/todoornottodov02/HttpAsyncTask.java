package de.ueberdiespree.todoornottodov02;

/**
 * Created by ulrike on 29.04.16.
 */

import java.io.InputStream;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ulrike on 04.04.16.
 */

public class HttpAsyncTask extends AsyncTask<String, Void, String> {

    private static final String LOGGER = "ULRIKE";
    Item serverItem;
    String url;
    private String identifier;
    private String returnStatement;

    public HttpAsyncTask(Item serverItem, String url, String identifier) {
        this.serverItem = serverItem;
        this.url = url;
        this.identifier = identifier;
    }

    public static String POST(String url, Item serverItem) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            //-----hier kann man noch auslagern: redundant!!!!--------------------------------------

            String json = "";

            // ** convert object to JSON string usin Jackson Lib
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(serverItem);

            Log.d(LOGGER, "Sende neues Item zum Server (POST):= " + json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
            //--------------------------------------------------------------------------------------

            // 6. set httpPost Entity
            httpPost.setEntity(se);


            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)

            {
                result = new InputStreamConverter().convertInputStreamToString(inputStream);
            }
            else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static String PUT(String url, Item serverItem) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make Put request to the given URL
            HttpPut httpPut = new HttpPut(url);

            String json = "";


            // ** convert object to JSON string usin Jackson Lib
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(serverItem);

            Log.d(LOGGER, "Update Item auf Server (PUT):= " + json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPut Entity
            httpPut.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");

            // 8. Execute Put request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = new InputStreamConverter().convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static String DELETE(String url, Item serverItem) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make Put request to the given URL
            HttpDelete httpDelete = new HttpDelete(url);

            String json = "";


            // ** convert object to JSON string usin Jackson Lib
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(serverItem);


            //Log.d(LOGGER, "Lösche Item vom Server (DELETE):= " + json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPut Entity
            //httpDelete.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpDelete.setHeader("Accept", "application/json");
            httpDelete.setHeader("Content-type", "application/json");

            // 8. Execute Put request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpDelete);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = new InputStreamConverter().convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            Log.d(LOGGER, "Führe GET aus.. Prüfe Item auf Server...");

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null) {
                result = new InputStreamConverter().convertInputStreamToString(inputStream);

                Log.d(LOGGER, "Hole Item(s) vom Server (GET):= " + result);


            }else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("GET error: ", e.getLocalizedMessage());
        }

        return result;
    }

    @Override
    protected String doInBackground(String... params) {

        //je nachdem welcher Identifier angegeben wird, wird ein anderer Http-Task ausgeführt

        switch(identifier){
            case "POST":
                return returnStatement = POST(url, serverItem);
            case "PUT":
                return returnStatement = PUT(url, serverItem);
            case "DELETE":
                return returnStatement = DELETE(url, serverItem);
            case "GET":
                return returnStatement = GET("http://10.0.3.2:8080/api/todos");


            default:
                //do Nothing
        }

        return returnStatement;
    }







}