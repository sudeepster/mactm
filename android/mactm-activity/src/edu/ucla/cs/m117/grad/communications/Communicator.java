package edu.ucla.cs.m117.grad.communications;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: spradhan
 * Date: 5/30/11
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Communicator {
    private static final String LOG_TAG = "Communicator";

    private Context contextApplication 	= null;
    private String strPhoneNumber		= null;
    private TelephonyManager telephonyManager 	= null;
    private	String 	queryURL = "http://mactm.web44.net/mactm/fetch.php?";
    private DefaultHttpClient httpClient = null;
    private static final int REGISTRATION_TIMEOUT = 30 * 1000;

    public Communicator(Context context) {
        contextApplication		= context.getApplicationContext();
        telephonyManager		= (TelephonyManager) contextApplication.getSystemService(Context.TELEPHONY_SERVICE);
	    strPhoneNumber			= telephonyManager.getLine1Number();
    }


    public String process(String view) {
        String errMsg = null;
        //JSONArray elements = new JSONArray();

        String request = queryURL;
        request += "mobile=" + strPhoneNumber.toLowerCase() + "&time=" + view.toLowerCase();

        Log.d( LOG_TAG, "request: "+request );
        String response = null;
        try {
            response = sendToServer( request );
            Log.d( LOG_TAG, "response: "+ response );

            JSONTokener tokener = new JSONTokener( response );
            Object o = tokener.nextValue();
            if( o instanceof JSONArray ) {
                JSONArray array = (JSONArray)o;
                for( int i = 0 ; i < array.length() ; ++i ) {
                    // TODO: process array here
                }
            } else
                throw new JSONException( "Top element is not a JSONArray" );

        } catch( IOException ex ) {
            errMsg = "Connection problem";
            Log.e( LOG_TAG, "IOException", ex );
        } catch( JSONException ex ) {
            errMsg = "Malformed response";
            Log.e( LOG_TAG, "Malformed JSON response: "+response, ex );
        }
        return errMsg;
    }

    private String sendToServer(String request) throws IOException {
        String result = null;
        maybeCreateHttpClient();
        HttpGet get = new HttpGet(request);
        HttpResponse resp = httpClient.execute( get );

        // Execute the GET transaction and read the results
        int status = resp.getStatusLine().getStatusCode();
        if( status != HttpStatus.SC_OK )
                throw new IOException( "HTTP status: "+Integer.toString( status ) );
        DataInputStream is = new DataInputStream( resp.getEntity().getContent() );
        result = is.readLine();
        return result;

    }

        private void maybeCreateHttpClient() {
            if ( httpClient == null) {
                httpClient = new DefaultHttpClient();
                HttpParams params = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
                ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
            }
    }
}
