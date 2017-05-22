package com.example.user.proba3.network;

import android.os.AsyncTask;

import com.example.user.proba3.network.RequestCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jaroslaw on 24.04.2017.
 */

public class UploadRequestTask extends AsyncTask<String, Void, String> {
    RequestCallback<String> mCallback;

    public UploadRequestTask(RequestCallback<String> callback) {
        mCallback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;

        if(!isCancelled() && params != null && params.length > 0) {
            String urlString = params[0];
            String method = params[1];
            String action = params[2];
            String data = params[3];

            String urlToSend = urlString + "?action=" + action;
            try {
                URL url = new URL(urlToSend);
                result = requestUrl(url, method, data);
                if(result == null) {
                    throw new IOException("No response received.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String requestUrl(URL url, String method, String data) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = "";
        int responseCode = 0;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setChunkedStreamingMode(0);
            // Send request

            OutputStreamWriter outputStream = new OutputStreamWriter(connection.getOutputStream());
            outputStream.write(data);
            outputStream.flush();

            // Receive response
            responseCode = connection.getResponseCode();
            if(responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP Error Code: "+responseCode);
            }
            stream = connection.getInputStream();
            if(stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String responseData = null;
                while((responseData = reader.readLine())!=null) {
                    result += responseData;
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            result = "REQUEST_ERROR";
        }
        finally {
            if(stream != null)
                stream.close();
            if(connection != null)
                connection.disconnect();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.updateFromResponse(s);
    }
}
