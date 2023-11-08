package com.a2zsuvidhaa.in.AdhaarVerification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a2zsuvidhaa.in.R;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class AdhaarWebActivity extends AppCompatActivity {

    WebView webView;
    private long enqueue;
    DownloadManager dm;
    boolean flag = true;
    boolean downloading = true;

    EditText et_otp;
    Button btn_submit;
    String otp="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhaar_web);
        webView = findViewById(R.id.webview);

        et_otp=findViewById(R.id.et_otp);
        btn_submit=findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("url start","=");

                try {
                    //    task=valOtp&boxchecked=0&zipcode=1234&totp=423858&task=valOtp&boxchecked=0

                    otp=et_otp.getText().toString();
                  downloadFileAsync("https://resident.uidai.gov.in/offline-kyc","adhaar.zip");
                    Log.e("url finish","=");
                }

                        catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        });

        checkDownloadPermission();
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://resident.uidai.gov.in/offline-kyc");
       // webView.loadUrl("http://prod.excelonestopsolution.com/");
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                try {
                //    public void postData() {
                        // Create a new HttpClient and Post Header
                    Log.e("url start","="+url);

                          //    task=valOtp&boxchecked=0&zipcode=1234&totp=423858&task=valOtp&boxchecked=0
                            // Add your data

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
               /* File directory1 = new File(Environment.getExternalStorageDirectory(), "Download");
                File file = new File(directory1.getPath(), "private.zip");
                if(file.exists())
                    file.delete();
                try {
                    DeleteRecursive(new File(directory1 + "/private"));
                }
                catch (Exception ew)
                {
                    ew.printStackTrace();
                }

Log.e("dire path start","="+mimetype+"0-=0"+directory1.getAbsolutePath()+"/elevennn.zip");
*/
                 /* DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                String fileName = contentDisposition.replace("inline; filename=", "");
               try {
                   fileName = fileName.replaceAll(".+UTF-8''", "");
                   fileName = fileName.replaceAll("\"", "");
                   fileName = URLDecoder.decode(fileName, "UTF-8");
                   request.setTitle(fileName);
               }
               catch (Exception e)
               {
                   e.printStackTrace();
               }
                //file scanned by MediaScannar
                request.allowScanningByMediaScanner();

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                long idDownLoad = dm.enqueue(request);
              DownloadManager.Query query = null;
                query = new DownloadManager.Query();
                Cursor c = null;
                if(query!=null) {
                    query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
                }

                while (downloading) {
                    c = dm.query(query);
                    if (c.moveToFirst()) {
                        int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        Log.e("FLAG", "Downloading");
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {

                            downloading = false;
                            flag = true;
                            Log.e("FLAG", "done"+flag);
                            break;


                        }
                        if (status == DownloadManager.STATUS_FAILED) {
                            Log.e("FLAG", "Fail");
                            downloading = false;
                            flag = false;
                            break;
                        }

                        if(flag)
                        {
                            Intent intent=new Intent(AdhaarWebActivity.this,AdhaarVerifcationActivity.class);
                            intent.putExtra("fileName",fileName);
                            startActivity(intent);
                            finish();
                        }
                        c.moveToFirst();
                    }
                }
*/
            }

            // Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

        });
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    private void downloadFileAsync(String url, String filename){

        new AsyncTask<String, Void, String>() {
            String SDCard;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL("https://resident.uidai.gov.in/offline-kyc");
                    HttpURLConnection urlConnection = null;
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setChunkedStreamingMode(0);


                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("task", "valOtp");
                    jsonObject.addProperty("boxchecked", "0");
                    jsonObject.addProperty("zipcode", "1234");
                    jsonObject.addProperty("totp", ""+otp);
                    Log.e("json","="+jsonObject.toString());


                    OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonObject.toString());
                    writer.flush();


                    urlConnection.connect();
                    int lengthOfFile = urlConnection.getContentLength();
                    //SDCard = Environment.getExternalStorageDirectory() + File.separator + "downloads";
                    SDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"";
                    int k = 0;
                    boolean file_exists;
                    String finalValue = params[1];
                    do {
                        if (k > 0) {
                            if (params[1].length() > 0) {
                                String s = params[1].substring(0, params[1].lastIndexOf("."));
                                String extension = params[1].replace(s, "");

                                finalValue = s + "(" + k + ")" + extension;
                            } else {
                                String fileName = params[0].substring(params[0].lastIndexOf('/') + 1);
                                String s = fileName.substring(0, fileName.lastIndexOf("."));
                                String extension = fileName.replace(s, "");
                                finalValue = s + "(" + k + ")" + extension;
                            }
                        }
                        File fileIn = new File(SDCard, finalValue);
                        file_exists = fileIn.exists();
                        k++;
                    } while (file_exists);

                    File file = new File(SDCard, finalValue);
                    FileOutputStream fileOutput = null;
                    fileOutput = new FileOutputStream(file, true);
                    InputStream inputStream = null;
                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    long total = 0;
                    while ((count = inputStream.read(buffer)) != -1) {
                        total += count;
                        //publishProgress(""+(int)((total*100)/lengthOfFile));
                        fileOutput.write(buffer, 0, count);
                    }
                    fileOutput.flush();
                    fileOutput.close();
                    inputStream.close();
                } catch (MalformedURLException e){
                }  catch (FileNotFoundException e){
                } catch (IOException e){
                } catch (Exception e){
                }
                return params[1];
            }
            @Override
            protected void onPostExecute(final String result) {

                Log.e("result","="+result.toString());
            }

        }.execute(url, filename);
    }
    class WEBSERVICEREQUESTOR extends AsyncTask<String, Integer, String>
    {
        String URL;
        List<NameValuePair> parameters;

        private ProgressDialog pDialog;

        public WEBSERVICEREQUESTOR(String url, List<NameValuePair> params)
        {
            this.URL = url;
            this.parameters = params;
        }

        @Override
        protected void onPreExecute()
        {
            pDialog = new ProgressDialog(AdhaarWebActivity.this);
            pDialog.setMessage("Processing Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;

                HttpPost httpPost = new HttpPost(URL);

                if (parameters != null)
                {
                    httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                }
                httpResponse = httpClient.execute(httpPost);

                httpEntity = httpResponse.getEntity();
                return EntityUtils.toString(httpEntity);

            }  catch (Exception e)
            {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();

            try
            {
                Log.e("result","="+result.toString());
            }
            catch (Exception e)
            {

            }
            super.onPostExecute(result);
        }
    }

    private void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
            {
                child.delete();
                DeleteRecursive(child);
            }

        fileOrDirectory.delete();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AdhaarWebActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(AdhaarWebActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AdhaarWebActivity
                    .this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
