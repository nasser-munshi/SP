package com.mithun.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mithun.comm.http.HttpHelperClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krishna Ray on 11/23/2015.
 */
public class HttpPostReqAsyncTask extends AsyncTask<String, Void, String> {
    Context appContext;
    String httpAddress;
    HttpHelperClass httpHelper;
    public HttpPostReqAsyncTask(Context context, String address){
        appContext = context;
        httpAddress = address;
        httpHelper = new HttpHelperClass(appContext);
    }
    @Override
    protected String doInBackground(String... params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("device_id", params[0]));
        nameValuePairs.add(new BasicNameValuePair("latitude", params[1]));
        nameValuePairs.add(new BasicNameValuePair("longitude", params[2]));
        nameValuePairs.add(new BasicNameValuePair("level", params[3]));
        httpHelper.httpPostData(httpAddress, nameValuePairs);
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(appContext, result, Toast.LENGTH_LONG).show();
        Toast.makeText(appContext, "Data Send Successfully2", Toast.LENGTH_SHORT).show();

    }
}
