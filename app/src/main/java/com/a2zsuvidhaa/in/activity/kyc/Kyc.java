package com.a2zsuvidhaa.in.activity.kyc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.loader.content.CursorLoader;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.obsez.android.lib.filechooser.ChooserDialog;


import net.lingala.zip4j.ZipFile;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;

public class Kyc extends AppCompatActivity {

    final static String link_UIDAI = "https://resident.uidai.gov.in/offline-kyc";
    final static int FILE_SELECT_CODE=1221;

    // all string from containing xml data are ended with x
    public static String xml_name, xml_birthDate, xml_emailSha,
            xml_gender, xml_mobileSha, xml_careOf, xml_country,
            xml_district, xml_house, xml_landmark, xml_location,
            xml_pinCode, xml_po, xml_state, xml_street, xml_subDistrict,
            xml_vtc, xml_photoByteCode, xml_referenceId, tempForXml;
    public static Context contextZ;
    Animation animAlpha;
    String pathh;
    AppPreference appPreference;

    // This is important!
    static {
        org.apache.xml.security.Init.init();
    }

    File fileObj_certificate, fileObj_zipFile;
    Button btn_verifyNow, btn_downloadAadhar;
    TextView tv_browseFile, tv_status;
    public EditText et_shareCode,et_aadharNumber;
    LinearLayout ll_afterBrowse;
    UserData userData;

    public static void onKycVerificationAgreed() {
        // saving data in sharedPreferences

        UserData userData = new UserData(contextZ);
        userData.setName(xml_name);
        userData.setBirthDate(xml_birthDate);
        userData.setEmailSha(xml_emailSha);
        userData.setGender(xml_gender);
        userData.setMobileSha(xml_mobileSha);
        userData.setFatherName(xml_careOf);
        userData.setCountry(xml_country);
        userData.setDistrict(xml_district);
        userData.setHouse(xml_house);
        userData.setLandmark(xml_landmark);
        userData.setLocation(xml_location);
        userData.setPinCode(xml_pinCode);
        userData.setState(xml_state);
        userData.setStreet(xml_street);
        userData.setSubDistrict(xml_subDistrict);
        userData.setVtc(xml_vtc);
        userData.setPhotoByteCode(xml_photoByteCode);
        userData.setAadharReferenceId(xml_referenceId);
        userData.setIsKycVerified(true);
        userData.applyUpdate();
    }

    public static void recursiveDelete(File file) {
        try {
            //to end the recursive loop
            if (!file.exists())
                return;

            //if directory, go inside and call recursively
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    //call recursively
                    recursiveDelete(f);
                }
            }
            //call delete to delete files and empty directory
            file.delete();
            System.out.println("Deleted file/folder: " + file.getAbsolutePath());
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc);
        et_aadharNumber = findViewById(R.id.et_aadharNumber);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Upgrade Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_cross_close2);
        contextZ = getApplicationContext();
        appPreference = new AppPreference(this);
        idAndListeners();
        ll_afterBrowse.setVisibility(View.GONE);
    }

    void idAndListeners() {

        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_aplha);

        btn_downloadAadhar = findViewById(R.id.btn_downloadAadhar);
        tv_browseFile = findViewById(R.id.tv_browseFile);
        tv_status = findViewById(R.id.tv_status);
        et_shareCode = findViewById(R.id.et_shareCode);
        btn_verifyNow = findViewById(R.id.btn_verifyNow);
        ll_afterBrowse = findViewById(R.id.ll_afterBrowse);



        btn_downloadAadhar.setOnClickListener(v -> {
            btn_downloadAadhar.setAlpha(0.5f);
            CustomTabsIntent anotherCustomTab = new CustomTabsIntent.Builder().build();
            anotherCustomTab.launchUrl(Kyc.this, Uri.parse(link_UIDAI));

        });

        tv_browseFile.setOnClickListener(v -> {

            btn_verifyNow.clearAnimation();
            tv_browseFile.startAnimation(animAlpha);
            new ChooserDialog(Kyc.this)
                    .withFilter(false, false)
                    // to handle the result(s)
                    .withChosenListener(new ChooserDialog.Result() {
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            pathh = path;
                            //Toast.makeText(Kyc.this, "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                            getZipFile();
                        }
                    })
                    .build()
                    .show();
            //showFileChooser();


        });


        btn_verifyNow.setOnClickListener(v -> {
            startFinalVerification();
            tv_browseFile.clearAnimation();
            btn_verifyNow.startAnimation(animAlpha);
        });

        // Initialize classes below
        userData = new UserData(this);

    }


    void getZipFile() {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {

            Toast.makeText(Kyc.this, "Please Allow to use storage"
                    , Toast.LENGTH_LONG).show();
            askStoragePermission();
            return;

        }
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + Environment.DIRECTORY_DOWNLOADS);

        /*File file = new File(Environment.getExternalStorageDirectory()
                + "/" + Environment.DIRECTORY_DOWNLOADS);*/
        /*Toast.makeText(contextZ, Environment.getExternalStorageDirectory()
                + "/" + Environment.DIRECTORY_DCIM, Toast.LENGTH_SHORT).show();*/

        File[] array = file.listFiles();
        String old = "";
        boolean found = false;

        Log.d("TAG", "getZipFile: " +pathh);
        if (pathh.contains("offlineaadhaar")
                && pathh.contains(".zip")) {
            found = true;
            file = new File(pathh);
        }

        if (found) {
            fileObj_zipFile = file;
            tv_status.setText("Document Found:\n" + file.getName());
            String message = "Enter Share Code and Click on Verify to Proceed";
            showCustomSnack(btn_downloadAadhar.getRootView(), message,
                    false, false);
            tv_status.setTextColor(Color.parseColor("#487E0A"));
            ll_afterBrowse.setVisibility(View.VISIBLE);
        } else {
            String message = "Document not found. Please download the Aadhaar " +
                    "Offline Verification Document by clicking above button.";
            showCustomSnack(btn_downloadAadhar.getRootView(), message, true, true);
        }


    }

    void startFinalVerification() {

        String shareCode = et_shareCode.getText().toString().trim();

        if (shareCode.length() != 4) {
            showCustomSnack(btn_downloadAadhar.getRootView(),
                    "Please enter 4 digit Share Code", false, true);
        } else {
            unzip(fileObj_zipFile, shareCode);
        }

    }

    /*unzip file using shareCode, then extract then send file address*/
    void unzip(File zipFilePath, String password) {

        String unzipErrorStatement = "Error: Unable to unzip file (wrong ShareCode)";
        String signatureFailStatement = "Signature Verification Failed\nPlease select valid document";

        /* firstly check if the file is extracted or not
         * if extracted then we are deleting the complete folder
         * and extract it again for verification, reason - there may be extra data there  */
        try {
            File folderUnzipped = new File(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
            if (folderUnzipped.exists()) recursiveDelete(folderUnzipped);
        } catch (Exception ignored) {

        }
        Toast.makeText(contextZ, zipFilePath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        // if not extracted then unzipping here
        ZipFile zipFile = new ZipFile(zipFilePath.getAbsolutePath(),
                password.toCharArray());
        try {
            zipFile.extractAll(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
            File unzipFolder = new File(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
            File[] filesInsideFolder = unzipFolder.listFiles();
            if (filesInsideFolder == null) {
                showCustomSnack(btn_downloadAadhar.getRootView(),
                        "Selected Document is Invalid, Please download the valid Document",
                        true, true);
                return;
            }
            if (filesInsideFolder.length != 0) fileObj_certificate = filesInsideFolder[0];
        } catch (Exception e) {
            showCustomSnack(btn_downloadAadhar.getRootView(),
                    unzipErrorStatement,
                    true, true);
            Toast.makeText(contextZ, "Take Screenshot: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (!verifySignature(fileObj_certificate)) {
            showCustomSnack(btn_downloadAadhar.getRootView(),
                    signatureFailStatement,
                    true, true);
            return;
        } else {
            tv_status.setText("Signature Verified");
        }

        try {
            getXmlData(zipFilePath.getParent() + "/" + zipFilePath.getName().split("\\.")[0]);
        } catch (Exception e) {
            showCustomSnack(btn_downloadAadhar.getRootView(),
                    "Error: " + e.getMessage(),
                    true, true);
        }

        afterVerificationSuccess();
    }

    void afterVerificationSuccess() {
        SheetClass sheetClass = new SheetClass();
        sheetClass.show(getSupportFragmentManager(), "exampleBottomSheet");
    }

    boolean checkMobileNumberMatched() {
        UserData userData = new UserData(this);
        String last4Digits = xml_referenceId.substring(0, 4);
        String mobile = userData.getMobile();

        return verifyMobileEmailSHA(mobile, Integer.parseInt(last4Digits), xml_mobileSha);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("TAG", "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        File file = new File(uri.getPath());
                        path = getPath(this, uri);
                        Log.d("TAG", "File Path: " + path+"/"+file.getAbsolutePath()+file.length());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    private String getRealPathFromUri(Uri uri){
        String[] projection = { "_data" };
        CursorLoader loader = new CursorLoader(Kyc.this,uri,projection,null,null,null);
        Cursor cursor = loader.loadInBackground();
        //int column_index = cursor.getColumnIndexOrThrow("_data");
        int column_idx= cursor.getColumnIndexOrThrow( "_data" );
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    /*Here we will verify the signature
     * by decrypting their code, and then getting hash
     * by calculating hash from xml then matching them*/
    boolean verifySignature(File xmlFile) {
        boolean valid = false;
        try {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            Document doc = f.newDocumentBuilder().parse(xmlFile);

            NodeList nodes = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature");
            if (nodes.getLength() == 0) {
                throw new Exception("Signature NOT found!");
            }

            Element sigElement = (Element) nodes.item(0);
            XMLSignature signature = new XMLSignature(sigElement, "");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager assetManager = getAssets();
            InputStream ims = assetManager.open("uidai_auth_sign_prod_2023.cer");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(ims);

            if (cert == null) {
                PublicKey pk = signature.getKeyInfo().getPublicKey();
                if (pk == null) {
                    throw new Exception("Did not find Certificate or Public Key");
                }
                valid = signature.checkSignatureValue(pk);
            } else {
                valid = signature.checkSignatureValue(cert);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return valid;
    }

    /*using file address open xml and then get data into strings (declared above)*/
    void getXmlData(String address) throws XmlPullParserException, IOException {

        File file = new File(address);
        // getting file from the extracted folder
        file = new File(file.listFiles()[0].getAbsolutePath());

        if (!file.getName().contains(".xml")) {
            Toast.makeText(this, "File is invalid, Please download again"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        // parsing the xml file
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser parser = xmlFactoryObject.newPullParser();
        Log.d("TAG", "getXmlData: "+parser.toString());
        parser.setInput(new FileInputStream(file.getAbsolutePath()), null); //setting input
        //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (event) {

                case XmlPullParser.START_TAG:

                    switch (tagName) {
                        case "OfflinePaperlessKyc":
                            xml_referenceId = parser.getAttributeValue(null, "referenceId");
                            break;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    switch (tagName) {
                        case "Poi":
                            xml_name = parser.getAttributeValue(null, "name");
                            xml_birthDate = parser.getAttributeValue(null, "dob");
                            xml_emailSha = parser.getAttributeValue(null, "e");
                            xml_gender = parser.getAttributeValue(null, "gender");
                            xml_mobileSha = parser.getAttributeValue(null, "m");
                            break;
                        case "Poa":
                            xml_careOf = parser.getAttributeValue(null, "careof");
                            xml_country = parser.getAttributeValue(null, "country");
                            xml_district = parser.getAttributeValue(null, "dist");
                            xml_house = parser.getAttributeValue(null, "house");
                            xml_landmark = parser.getAttributeValue(null, "landmark");
                            xml_location = parser.getAttributeValue(null, "loc");
                            xml_pinCode = parser.getAttributeValue(null, "pc");
                            xml_po = parser.getAttributeValue(null, "po");
                            xml_state = parser.getAttributeValue(null, "state");
                            xml_street = parser.getAttributeValue(null, "street");
                            xml_subDistrict = parser.getAttributeValue(null, "subdist");
                            xml_vtc = parser.getAttributeValue(null, "vtc");
                            break;
                        case "Pht":
                            xml_photoByteCode = tempForXml;
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                    tempForXml = parser.getText();
                    break;
            }
            event = parser.next();
        }
        //completeVerification();
    }

    /*this calculate hash of the string and match it with SHA256 given in xml*/
    public byte[] getSHA(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 12) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public boolean verifyMobileEmailSHA(String mobileEmail, int lastDigit, String SHA256) {
        if (lastDigit == 0) lastDigit++; // same case for 0 and 1

        String createdHashCode = toHexString(getSHA(mobileEmail));

        // now we are getting the hashcode for the string here
        for (int i = 1; i < lastDigit; i++) {
            createdHashCode = toHexString(getSHA(createdHashCode));
        }

        return createdHashCode.equals(SHA256);
    }

    /* complete verification of the file is here*/

    /*void completeVerification() {

        StringBuilder sb = new StringBuilder("");

        if (!name.equalsIgnoreCase(namex)) sb.append(name + ", " + namex + "\n");
        else sb.append("name verified\n");

        if (!verifySignature())
            sb.append("Signature Verification_activity Failed\n");
        else sb.append("signature passed\n");

        if (!validate(mobile + shareCode,
                4, mobilex))
            sb.append("Incorrect Mobile Number - " + mobile + shareCode + " = " + mobilex + "\n");
        else sb.append("mobile number verified\n");

        if (emailx.equals("")) sb.append("Email not registered\n");
        else if (!validate(email + shareCode,
                Integer.parseInt(String.valueOf(referenceIdx.charAt(3))), emailx))
            sb.append("Incorrect email\n");
        else sb.append("email verified\n");

        if (!birthDate.equalsIgnoreCase(birthDatex))
            sb.append(birthDate + ", " + birthDatex + "\n");
        else sb.append("birthdate verified\n");

        if (!gender.equalsIgnoreCase(genderx)) sb.append(gender + ", " + genderx + "\n");
        else sb.append("gender verified\n");

        if (!("s/o " + father).equalsIgnoreCase(careofx)) sb.append(father + ", " + careofx + "\n");
        else sb.append("father name verified\n");
        // setting image in image view from xml
        byte[] decodedString = Base64.decode(photoBytex, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        userPicIv.setImageBitmap(decodedByte);

        tv_status.setText(sb.toString());
    }*/

    public void askStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, 2);
        }
    }

    // for back option in toolbar (left side top)
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void showCustomSnack(View view, String message, boolean isIndefinite, boolean isError) {

        hideKeyboard(this);

        Snackbar snackbar = Snackbar.make(view.getRootView(), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (isIndefinite) snackbar.setDuration(1000);
        if (isError) snackBarView.setBackgroundColor(getResources().getColor(R.color.black));

        snackBarView.setOnClickListener(view1 -> snackbar.dismiss());
        snackbar.show();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_downloadAadhar.setAlpha(1f);
    }

    public class SheetClass extends BottomSheetDialogFragment {
        View view;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.layout_kyc_data_agree,
                    container, false);

            TextView tv_nameKyc, tv_mobileKyc, tv_eMailKyc,
                    tv_addressKyc, tv_guardianKyc, tv_genderKyc, tv_dobKyc;

            tv_nameKyc = view.findViewById(R.id.tv_nameKyc);
            tv_mobileKyc = view.findViewById(R.id.tvb_mobileKyc);
            tv_eMailKyc = view.findViewById(R.id.tvb_emailKyc);
            tv_addressKyc = view.findViewById(R.id.tvb_addressKyc);
            tv_guardianKyc = view.findViewById(R.id.tvb_guardianKyc);
            tv_genderKyc = view.findViewById(R.id.tv_genderKyc);
            tv_dobKyc = view.findViewById(R.id.tv_dobKyc);

            Button btn_agreeKyc = view.findViewById(R.id.btn_agreeKyc);

            // setting user image here
            ImageView iv_userImageKyc = view.findViewById(R.id.iv_userImageKyc);
            byte[] decodedString = Base64.decode(xml_photoByteCode, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.
                    decodeByteArray(decodedString, 0, decodedString.length);
            iv_userImageKyc.setImageBitmap(decodedByte);

            // For getting user Email and Mobile from Registration
            UserData userData = new UserData(getContext());
            String tempMail = userData.getEmail();
            String tempMobile = userData.getMobile();

            // setting other user data here

            String firstColor = "<font color=#4fa5d5>";
            String secondColor = "</font> <font color=#222020>";
            String lastCloseColor = "</font>";

            String colorFulText = firstColor + "Name: " + secondColor
                    + xml_name + lastCloseColor;
            tv_nameKyc.setText(Html.fromHtml(colorFulText));

            colorFulText = firstColor + "Mobile: " + secondColor
                    + tempMobile + lastCloseColor;
            tv_mobileKyc.setText(Html.fromHtml(colorFulText));

            colorFulText = firstColor + "Email: " + secondColor
                    + tempMail + lastCloseColor;
            tv_eMailKyc.setText(Html.fromHtml(colorFulText));
            //if(emailShax.equals("")) tv_eMailKyc.setVisibility(View.GONE);

            colorFulText = firstColor + "Guardian: " + secondColor
                    + xml_careOf + lastCloseColor;
            tv_guardianKyc.setText(Html.fromHtml(colorFulText));

            colorFulText = firstColor + "DOB: " + secondColor
                    + xml_birthDate + lastCloseColor;
            tv_dobKyc.setText(Html.fromHtml(colorFulText));

            String gender = xml_gender;
            if (xml_gender.equals("M")) gender = "Male";
            else if (xml_gender.equals("F")) gender = "Female";
            colorFulText = firstColor + "Gender: " + secondColor
                    + gender + lastCloseColor;
            tv_genderKyc.setText(Html.fromHtml(colorFulText));

            String address = xml_house + " " + xml_street + ", " + xml_landmark + " " +
                    xml_location + ", " + xml_pinCode + ", " + xml_district + ", " + xml_state + ", " + xml_country;
            colorFulText = firstColor + "Address: " + secondColor
                    + address + lastCloseColor;
            tv_addressKyc.setText(Html.fromHtml(colorFulText));

            // if user agree then they click this button
            btn_agreeKyc.setOnClickListener(view -> {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

                View view1 = inflater.inflate(R.layout.layout_kyc_success, container, false);
                ImageView iv = view1.findViewById(R.id.iv_kyc_success);
                TextView tv1 = view1.findViewById(R.id.tv_kyc_success1);
                TextView tv2 = view1.findViewById(R.id.tv_kyc_success2);
                Button button = view1.findViewById(R.id.btn_kyc_success);
                AppPreference appPreference = new AppPreference(contextZ);
                appPreference.setUserKYC(1);
                final StringRequest request = new StringRequest(Request.Method.POST, APIs.USER_KYC_COMPLETE,
                        response -> {
                            try {


                                JSONObject object = new JSONObject(response);
                                Log.e("user-kyc resp","="+object.toString());


                            } catch (JSONException e) {


                            }
                        },
                        error -> {


                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("user-id", appPreference.getId()+"");
                        param.put("token", appPreference.getToken());
                        return param;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("user_kyc", "1");
                        return param;
                    }
                };
                RequestHandler.getInstance(contextZ).addToRequestQueue(request);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(20),
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                final StringRequest request1 = new StringRequest(Request.Method.POST, APIs.USER_E_KYC,
                        response -> {
                            try {


                                JSONObject object = new JSONObject(response);
                                Log.e("user-kyc resp","="+object.toString());


                            } catch (JSONException e) {


                            }
                        },
                        error -> {


                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("user-id", appPreference.getId()+"");
                        param.put("token", appPreference.getToken());
                        return param;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        HashMap<String, String> param = new HashMap<>();
                        param.put("name", xml_name);
                        param.put("address", xml_house + " " + xml_street + ", " + xml_landmark + " " +
                                xml_location + ", " + xml_pinCode + ", " + xml_district + ", " + xml_state + ", " + xml_country);
                        param.put("father_name", xml_careOf);
                        param.put("pin_code", xml_pinCode);
                        param.put("dob", xml_birthDate);
                        param.put("gender", xml_gender);
                        param.put("adhar_number", et_aadharNumber.getText().toString());
                        param.put("city", xml_district);
                        return param;
                    }
                };
                RequestHandler.getInstance(contextZ).addToRequestQueue(request1);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(20),
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                final Animation animation = new AlphaAnimation(1, 0.8f); //to change visibility from visible to invisible
                animation.setDuration(1000); //1 second duration for each animation cycle
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.

                iv.startAnimation(animation);
                tv1.startAnimation(animation);
                tv2.startAnimation(animation);

                button.setOnClickListener(view2 -> {
                    //onKycVerificationAgreed();
                    getActivity().onBackPressed();
                });

                alertBuilder.setView(view1);
                alertBuilder.setCancelable(false);
                alertBuilder.show();
            });

            return view;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
