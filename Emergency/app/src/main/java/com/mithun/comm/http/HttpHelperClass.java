package com.mithun.comm.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by Krishna Ray on 11/23/2015.
 */
public class HttpHelperClass {
    Context appContext;
    public HttpHelperClass(Context context){
        appContext = context;
    }
    public boolean httpPostData(String addressLink, List<NameValuePair> nameValuePairs ){
        boolean success = false;
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(addressLink);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            success = true;
        }catch (HttpResponseException e){
            success = false;
            Log.e("SelfProtect", Log.getStackTraceString(e));
            Toast.makeText(appContext, "Network Response Error", Toast.LENGTH_LONG).show();
        }catch (ClientProtocolException e) {
            success = false;
            Log.e("SelfProtect", Log.getStackTraceString(e));
            Toast.makeText(appContext, "Client Error", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            success = false;
            Log.e("SelfProtect", Log.getStackTraceString(e));
            Toast.makeText(appContext, "I/O Error", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            success = false;
            Log.e("SelfProtect", Log.getStackTraceString(e));
            Toast.makeText(appContext, "General Error", Toast.LENGTH_LONG).show();
        }
        return success;
    }
}
