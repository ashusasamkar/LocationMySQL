package ashjadhav.example.com.locationmysql;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


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

public class InsertData {

    private Context context;
    String ServerURL = "http://192.168.1.14/Location/save_poi.php" ;

    public InsertData(Context context) {
        this.context = context;
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
                    Toast.makeText(context, "Exception occured : "+e, Toast.LENGTH_SHORT).show();
                }

                return "Data inserted Successfully";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context, "POI saved Successfully...", Toast.LENGTH_SHORT).show();
            }

        }

        SendPostReqAsyncTask send=new SendPostReqAsyncTask();
        send.execute(location_name,latitude,longitude);

        }
}
