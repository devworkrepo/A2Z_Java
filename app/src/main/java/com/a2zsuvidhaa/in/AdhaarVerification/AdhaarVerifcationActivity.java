package com.a2zsuvidhaa.in.AdhaarVerification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.R;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AdhaarVerifcationActivity extends AppCompatActivity {

    File directory,directory1;


    Button zipbtn;
    EditText et_code;
    TextView tv_data;
    private static final int PERMISSION_REQUEST_CODE = 100;
    String fileName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhaar_verifcation);
        zipbtn = findViewById(R.id.btn);
        et_code = findViewById(R.id.et_passcode);
        tv_data = findViewById(R.id.data);


        fileName="test.zip";//getIntent().getExtras().getString("fileName");
        directory1 = new File(Environment.getExternalStorageDirectory(), "Download");
//        Log.d("Files", "Path: " + directory.toString());
      /*  File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (File file : files) {
            if (file.getName().endsWith(".zip"))
                zipbtn.setText(file.getName());
            Log.d("Files", "FileName:" + file.getName());
        }*/
//                //if your file protected with password

        zipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



             //   Log.e("directory","="+directory1.getPath()+"/private.zip");



                try {
                    Log.e("file path private","="+directory1.getPath() + "/"+fileName);

                   /* ZipFile zipFile = new ZipFile(directory1.getPath() + "/"+fileName);
                    //if (zipFile.isEncrypted()) {
                        zipFile.setPassword(""+et_code.getText().toString());
                   // }
                    zipFile.extractAll(directory1.getPath() + "/" + "private/");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ParseXML();
                        }
                    },1000);
*/

                } catch (Exception e) {
                    Toast.makeText(AdhaarVerifcationActivity.this, "Something went wrong.Maybe password is incorrect.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //ZipArchive zipArchive = new ZipArchive();
                //zipArchive.unzip(directory.getPath() + "/private.zip", directory.getPath() + "/" + "private/", "3328");



            }
        });

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AdhaarVerifcationActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AdhaarVerifcationActivity
                .this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AdhaarVerifcationActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AdhaarVerifcationActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void ParseXML() {

        directory = new File(Environment.getExternalStorageDirectory(), "Download/private");
        Log.d("Files", "Path private: " + directory.toString());
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files[0].getName());


        File dir = Environment.getExternalStorageDirectory();
        File yourFile = new File(dir.getAbsolutePath() + "/Download/private/", ""+files[0].getName());
        Log.e("file:", "" + yourFile);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(yourFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Poi");


            Element element = (Element) nodeList.item(0);
            Log.e("node", "=" + element.getAttribute("name"));
            Toast.makeText(this, ""+element.getAttribute("name"), Toast.LENGTH_SHORT).show();

            tv_data.setText(""+element.getAttribute("name")+"\n"+element.getAttribute("dob"));




        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
    }

}
