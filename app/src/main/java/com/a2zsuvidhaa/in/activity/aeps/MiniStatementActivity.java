/*
package com.a2zsuvidhaa.in.activity.aeps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.SharedPref;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.model.MiniStatement;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MiniStatementActivity extends AppCompatActivity {

    TextView tv_balance;
    ArrayList<MiniStatement> list = new ArrayList<>();
    ListView listView;
    Button print,btn_download;
    String data="",id="",file_name="";

    private ProgressBar progressBarAccountNo;

    File apkStorage = null;
    File outputFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_statement);
        progressBarAccountNo = findViewById(R.id.progress_accountNo);
        listView=findViewById(R.id.listview);
        tv_balance=findViewById(R.id.tv_balance);
        print=findViewById(R.id.btn_print);
        btn_download = findViewById(R.id.btn_download);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPdf();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
          try{
              String balance=getIntent().getExtras().getString("balance");
              data=getIntent().getExtras().getString("data");
              id=getIntent().getExtras().getString("id");
            JSONArray jsonArray=new JSONArray(data);
           */
/* JSONObject jsonObject = new JSONObject();
            jsonObject.put("date","05/06/2020");
            jsonObject.put("txnType","Dr");
            jsonObject.put("amount","17.70");
            jsonObject.put("narration","SMSChgsApr20-Ju");
            jsonArray.put(jsonObject);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("date","03/06/2020");
            jsonObject1.put("txnType","Cr");
            jsonObject1.put("amount","3300.00");
            jsonObject1.put("narration","MMT/IMPS/015512");
            jsonArray.put(jsonObject1);
*//*


            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject2= jsonArray.getJSONObject(i);
                MiniStatement model=new MiniStatement();
                model.setDate(jsonObject2.getString("date").trim());
                model.setDesc(jsonObject2.getString("narration").trim());
                model.setType(jsonObject2.getString("txnType").trim());
                model.setAmount(jsonObject2.getString("amount").trim());
                list.add(model);
                Log.e("json","="+jsonObject2.toString());
            }

            MiniStatementAdapter adapter = new MiniStatementAdapter(MiniStatementActivity.this,list);
            listView.setAdapter(adapter);

              tv_balance.setText("Closing Balance : "+balance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void download()
    {
        apkStorage = new File(
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/"
                        +"A2ZSUVIDHA");


        outputFile = new File(apkStorage, file_name+".pdf");

        if (ActivityCompat.checkSelfPermission(MiniStatementActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MiniStatementActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return ;
        }

        if (!outputFile.exists()) {
            Toast.makeText(this, "Downloading is in Progress.", Toast.LENGTH_SHORT).show();
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(APIs.PDF_BASE_URL+file_name);
            Log.e("url pdf","="+APIs.PDF_BASE_URL+file_name);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Log.e("path pdf",""+outputFile.getPath());


            request.setDestinationUri(Uri.fromFile(outputFile));
            downloadManager.enqueue(request);
            //new DownloadTask(VideosActivity.this,Server.Video_Url+path,name);
            Log.e("down", "Directory Created.");
            //new DownloadTask(PdfActivity.this,Server.Text_Url+path,name);
            Log.e("down", "Directory Created.");
        }
        else {

            Dialog dialog = AppDialogs.transactionStatus(this,"You have this Pdf in your offline library.",2);
            Button btnOK = dialog.findViewById(R.id.btn_ok);
            btnOK.setOnClickListener(view->dialog.dismiss());
            dialog.show();
        }

    }
    private void getPdf()
    {
        progressBarAccountNo.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.PDF_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            String name = jsonObject.getString("data");
                            file_name=name;
                            download();
                         //   Intent intent = new Intent(MiniStatementActivity.this,ViewPdfActivity.class);
                           // intent.putExtra("name",name);
                           // startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(MiniStatementActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            Intent intent = new Intent(MiniStatementActivity.this, AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }
                        else{
                            MakeToast.show(MiniStatementActivity.this,message);
                        }

                        progressBarAccountNo.setVisibility(View.GONE);


                    } catch (JSONException e) {
                        progressBarAccountNo.setVisibility(View.GONE);

                    }
                },
                error -> {
                    progressBarAccountNo.setVisibility(View.GONE);

                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", SharedPref.getInstance(MiniStatementActivity.this).getToken());
                params.put("userId", String.valueOf(SharedPref.getInstance(MiniStatementActivity.this).getId()));
                params.put("id",""+id);

                Log.e("pdf get",""+params.toString());
                return params;
            }
        };
        RequestHandler.getInstance(MiniStatementActivity.this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}
*/
