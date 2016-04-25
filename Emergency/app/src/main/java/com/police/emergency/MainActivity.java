package com.police.emergency;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mithun.hardware.GPSTracker;
import com.police.emergency.services.EmergencyHelpService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    GPSTracker gpsTracker = null;
    EditText etAddress;
    String severityLevel = "10";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etAddress = (EditText)findViewById(R.id.addressEditText);
        gpsTracker = new GPSTracker(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void getNumber(){
        String ussdCode  = "*" + "804" + Uri.encode("#");
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
        Log.d(mTtitle,smallTalkCode);
    }

    public String getPhoneNumber(){
        String phoneNumber = "";
        String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
        Object object = getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
                main_data, "mimetype=?",
                new String[]{"vnd.android.cursor.item/phone_v2"},
                "is_primary DESC");
        if (object != null) {
            do {
                if (!((Cursor) (object)).moveToNext())
                    break;
                phoneNumber = ((Cursor) (object)).getString(4);
                Log.e("yes", "yes1");
                Log.e("Phone Number", phoneNumber);
            } while (true);
            ((Cursor) (object)).close();
        }
        Log.e("yes", "yes2");
        return phoneNumber;
    }

    public void onSendButtonClick(View view) {


        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mDeviceId = tm.getDeviceId();
        //String mPhoneNumber = tm.getLine1Number();

        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getSimOperatorName();

       // getPhoneNumber();



        String lat = "";
        String lng = "";
        if (isNetworkConnected()) {
            if (gpsTracker.canGetLocation()) {
                Location location = gpsTracker.getLastKnownLocation();//getLocation();
                if(location != null){
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());

                }
                gpsTracker.stopUsingGPS();
                String addressLink = "http://" + etAddress.getText().toString();



                Log.e("Phone NO: ", mPhoneNumber);

                Toast.makeText(getApplicationContext(), "Phone Number: "+mPhoneNumber, Toast.LENGTH_SHORT).show();
               // insertToDatabase(mDeviceId, lat, lng, "1", addressLink, this);
            } else {
                gpsTracker.showSettingsAlert();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public void onServiceStartButtonClick(View view){
        Intent intent = new Intent(this, EmergencyHelpService.class);
        //intent.putExtra("address", getString(R.string.test_server_address_base)  + getString(R.string.server_address));
        startService(intent);
    }
    public void onServiceStopButtonClick(View view){
        stopService(new Intent(this, EmergencyHelpService.class));
    }

    private void insertToDatabase(final String device_id, final String latitude, final String longitude,
                                  final String level, final String address, final Context context){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramUsername = params[0];
                String paramAddress = params[1];


              //  String name = editTextName.getText().toString();
              //  String add = editTextAdd.getText().toString();

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("device_id", paramUsername));
                nameValuePairs.add(new BasicNameValuePair("latitude", paramAddress));
                nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                nameValuePairs.add(new BasicNameValuePair("level", level));
                String addressLink = address;
                addressLink += "/policeci/index.php/VictimController/insert";
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(addressLink);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();
                    Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
                    //String s = entity.toString();

                } catch (HttpResponseException e){
                    e.printStackTrace();
                    Toast.makeText(context, "Network Response Error", Toast.LENGTH_LONG).show();
                }catch (ClientProtocolException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Client Error", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
//                    Toast.makeText(context, "I/O Error", Toast.LENGTH_LONG).show();
                    Log.e("Error: ","I/O Error");
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "General Error", Toast.LENGTH_LONG).show();
                }

                return "success";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
              //  TextView textViewResult = (TextView) findViewById(R.id.textViewResult);
               // textViewResult.setText("Inserted");
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(device_id, latitude);
    }


    public  boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}