package abass.com.firebasepushnotifications.Request;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ahmed on 26-Mar-18.
 */

public class ApiConnector {

    public String addRequest(String Message){
        // URL for getting all customers

        String url = "http://refadatours.com/android/addRequest.php?message="+Message;
        // Get HttpResponse Object from url.
        // Get HttpEntity from Http Response Object

        HttpEntity httpEntity = null;

        try
        {

            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();



        } catch (ClientProtocolException e) {

            // Signals error in http protocol
            e.printStackTrace();

            //Log Errors Here



        } catch (IOException e) {
            e.printStackTrace();
        }
        String entityResponse = null;
        try {
            entityResponse = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entityResponse;
    }
}
