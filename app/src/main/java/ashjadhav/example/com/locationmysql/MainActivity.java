package ashjadhav.example.com.locationmysql;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,LocationListener{
    EditText loc_name;
    TextView lat_long;
    Button poi;
    Double latitude, longitude;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final int MY_REQUEST = 101;
    Boolean permission_granted = false;
    String ServerURL = "http://192.168.1.14/Location/save_poi.php" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize GoogleApiClientt/Initiate the Connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        loc_name = (EditText) findViewById(R.id.txt_area);
        lat_long = (TextView) findViewById(R.id.txt_latlong);
        poi = (Button) findViewById(R.id.btn_poi);
        //insert=new InsertData(this);


    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        if (permission_granted) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected void onResume() {
        super.onResume();
        if (permission_granted) {
            if (mGoogleApiClient.isConnected()) {
                requestLocationUpdates();
            }
        }
    }

    private void requestLocationUpdates() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, MY_REQUEST);
            } else {
                permission_granted = true;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_granted = true;
                } else {
                    permission_granted = false;
                    Toast.makeText(this, "This app requires Location Permission to be granted...", Toast.LENGTH_SHORT).show();
                }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude=location.getLatitude();
        longitude=location.getLongitude();

        lat_long.setText("(Latitude,Longitude): ("+latitude+","+longitude+")");

        poi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location_name=loc_name.getText().toString();
                String lat=latitude.toString();
                String lng=longitude.toString();
                insertData(location_name,lat,lng);
                }
        });

    }

    public void insertData(final String location_name,final String latitude,final String longitude){
        class SendPostReqAsyncTask extends AsyncTask<String,Void,String>{

            @Override
            protected String doInBackground(String... params) {
                String loc_nameHolder=location_name;
                String latitudeHolder=latitude;
                String longitudeHolder=longitude;

                List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("loc_name",loc_nameHolder));
                nameValuePairs.add(new BasicNameValuePair("latitude",latitudeHolder));
                nameValuePairs.add(new BasicNameValuePair("longitude",longitudeHolder));

                try{
                    HttpClient httpClient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost(ServerURL);
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse=httpClient.execute(httpPost);
                    HttpEntity httpEntity=httpResponse.getEntity();

                }
                catch(Exception e){
                    Toast.makeText(MainActivity.this, "Exception occured : "+e, Toast.LENGTH_SHORT).show();
                }

                return "Data inserted Successfully";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(MainActivity.this, "POI saved Successfully...", Toast.LENGTH_SHORT).show();
            }

        }

        SendPostReqAsyncTask send=new SendPostReqAsyncTask();
        send.execute(location_name,latitude,longitude);

    }
}
