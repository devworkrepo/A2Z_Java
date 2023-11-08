package com.a2zsuvidhaa.in.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CreateRetailerActivity extends AppCompatActivity {

    private static final String TAG = "CreateRetailerActivity";

    //view references
    private EditText ed_fullName,ed_father,ed_dob;
    private EditText ed_email;
    private EditText ed_mobile;
    private EditText ed_shopName;
    private EditText ed_retailerAddress;
    private EditText ed_shopAddress;
    private EditText ed_pincode;
    private EditText ed_gender;
    private EditText ed_pancard;
    private EditText ed_aadharNumber;
    private EditText ed_amount;
    private TextView spn_state;
    private TextView spn_city;
    private Spinner spn_region;
    private CircleImageView iv_retailerImage;
    private ImageView iv_shopImage;
    private ImageView iv_pancardImage;
    private ImageView iv_aadharFrontImage;
    private ImageView iv_aadharBackImage;
    private ImageView iv_chequeImage;
    private ImageView iv_applicationForm;
    private ImageButton imgBtn_back;
    private Button btn_createRetailer;
    private ScrollView sv_layout;
    private ProgressBar progressState;
    private ProgressBar progressCreateRetailer;

    //Strings
    private String sFullName;
    private String sEmail;
    private String sMobile;
    private String sShopName;
    private String sRetailerAddress;
    private String sShopAddress;
    private String sPincode;
    private String sPancard;
    private String sAadharNo;
    private String sAmount;
    private String sState,city_id;
    private String sRegion;

    private HashMap<String, String> stateHashmap = new HashMap<>();
    private HashMap<String, String> cityHashmap = new HashMap<>();

    //variables
    private Bitmap bmRetailerImage = null;
    private Bitmap bmShopImage = null;
    private Bitmap bmAadharFrontImage = null;
    private Bitmap bmAadharBackImage = null;
    private Bitmap bmApplicationFormImage = null;
    private Bitmap bmPancardImage = null;
    private Bitmap bmChequeImage = null;

    private LinearLayout lin_pan,lin_img;

    private int PANCARD_IMAGE_RESULT_CODE = 100;
    private int AADHAR_FRONT_IMAGE_RESULT_CODE = 200;
    private int AADHAR_BACK_IAMGE_RESULT_CODE = 300;
    private int RETAILER_IMAGE_RESULT_CODE = 400;
    private int SHOP_IMAGE_RESULT_CODE = 500;
    private int CHEQUE_IMAGE_RESULT_CODE = 600;
    private int APPLICATION_FORM_IMAGE_RESULT_CODE = 700;

    AppPreference appPreference;
    int code=0;

    String str=""; int strOldlen=0;
    String kyc_status="0";
    private RelativeLayout rl_progress,rel_update;
    private ProgressBar progressBar;
    File file;
    Button btn_verify_adhaar,btn_pan_verify;
    TextView tv_upload_pan,tv_upload_adhaar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_retailer);

        initView();






        imgBtn_back.setOnClickListener(view -> onBackPressed());







    }

    // Here we will confirm the userData with the aadhar file;




    private void initView() {

        rl_progress = findViewById(R.id.rl_progress);
        rel_update = findViewById(R.id.rel_update);
        progressBar = findViewById(R.id.progress_createRetailer);

        ed_fullName = findViewById(R.id.ed_fullName);
        ed_father = findViewById(R.id.ed_father);
        ed_dob = findViewById(R.id.ed_dob);
        ed_email = findViewById(R.id.ed_email);
        ed_mobile = findViewById(R.id.ed_mobile);
        ed_shopName = findViewById(R.id.ed_shopName);
        ed_retailerAddress = findViewById(R.id.ed_retailerAddress);
        ed_shopAddress = findViewById(R.id.ed_shopAddress);
        ed_pincode = findViewById(R.id.ed_pincode);
        ed_gender = findViewById(R.id.ed_gender);
        ed_pancard = findViewById(R.id.ed_pancard);
        ed_aadharNumber = findViewById(R.id.ed_aadharNumber);
        ed_amount = findViewById(R.id.ed_amount);
        spn_state = findViewById(R.id.spn_state);
        spn_city = findViewById(R.id.spn_city);
        spn_region = findViewById(R.id.spn_region);
        iv_retailerImage = findViewById(R.id.iv_retailerImage);
        iv_shopImage = findViewById(R.id.iv_shopImage);
       // iv_pancardImage = findViewById(R.id.iv_pancardImage);

      //  iv_chequeImage = findViewById(R.id.iv_chequeImage);
        iv_applicationForm = findViewById(R.id.iv_applicationForm);
        imgBtn_back = findViewById(R.id.imgBtn_back);
        btn_createRetailer = findViewById(R.id.btn_createRetailer);
        sv_layout = findViewById(R.id.sv_layout);
        progressState = findViewById(R.id.progressState);
        progressCreateRetailer = findViewById(R.id.progress_createRetailer);
        lin_pan = findViewById(R.id.lin_pan);
        lin_img = findViewById(R.id.lin_img);
        tv_upload_adhaar=findViewById(R.id.tv_upload_adhaar);
        getData();


    }

    public void getData(){
        progressState.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.GET, APIs.USER_DETAIL_DATA,
                response -> {

                    try {
                        progressState.setVisibility(View.GONE);
                        Log.e("get data","="+response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            JSONObject object = new JSONObject(response);
                            JSONObject data = object.getJSONObject("details");

                            ed_fullName.setText(data.getString("name"));
                            if (data.getString("fater_name")!=null) ed_father.setText(data.getString("fater_name"));
                            if (data.getString("dob")!=null) ed_dob.setText(data.getString("dob"));
                            ed_email.setText(data.getString("email"));
                            ed_mobile.setText(data.getString("mobile"));
                            if (data.getString("shop_name")!=null) ed_shopName.setText(data.getString("shop_name"));
                            if (data.getString("parmanent_address")!=null) ed_retailerAddress.setText(data.getString("parmanent_address"));
                            if (data.getString("office_address")!=null) ed_shopAddress.setText(data.getString("office_address"));
                            if (data.getString("pin_code")!=null) ed_pincode.setText(data.getString("pin_code"));
                            if (data.getString("gender")!=null) ed_gender.setText(data.getString("gender"));
                            if (data.getString("pan_number")!=null) ed_pancard.setText(data.getString("pan_number"));
                            if (data.getString("aadhaar_number")!=null) ed_aadharNumber.setText(data.getString("aadhaar_number"));
                            if (data.getString("state_name")!=null) spn_state.setText(data.getString("state_name"));
                            if (data.getString("city_name")!=null) spn_city.setText(data.getString("city_name"));

                        } else if (status.equalsIgnoreCase("200")) {

                        } else if (status.equalsIgnoreCase("300")) {


                        } else {

                        }

                    } catch (Exception e) {
                        progressState.setVisibility(View.GONE);
                    }
                },
                error -> {

                    progressState.setVisibility(View.GONE);

                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();

                param.put("user-id", String.valueOf(AppPreference.getInstance(CreateRetailerActivity.this).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(CreateRetailerActivity.this).getToken()));



                return param;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean validateField() {

        boolean isValid = false;
        sFullName = ed_fullName.getText().toString();
        sEmail = ed_email.getText().toString();
        sMobile = ed_mobile.getText().toString();
        sShopName = ed_shopName.getText().toString();
        sRetailerAddress = ed_retailerAddress.getText().toString();
        sShopAddress = ed_shopAddress.getText().toString();
        sPincode = ed_pincode.getText().toString();
        sPancard = ed_pancard.getText().toString();
        sAadharNo = ed_aadharNumber.getText().toString().replace("-","");;
        sAmount = ed_amount.getText().toString();
return  true;
        /*if (!sFullName.isEmpty()) {
            if (!sEmail.isEmpty()) {
                if (!sMobile.isEmpty()) {
                    if (!sShopName.isEmpty()) {
                        if (!sRetailerAddress.isEmpty()) {
                            if (!sShopAddress.isEmpty()) {
                                if (!sPincode.isEmpty()) {
                                    if (!sPancard.isEmpty()) {
                                        if (!sAadharNo.isEmpty()) {
                                            if (!sAmount.isEmpty()) {
                                                if (bmPancardImage != null) {
                                                    if (bmAadharFrontImage != null) {
                                                        if (bmAadharBackImage != null) {
                                                            isValid = true;
                                                        } else
                                                            MakeToast.show(this, "Aadhar Back image required");
                                                    } else
                                                        MakeToast.show(this, "Aadhar Front image required");
                                                } else
                                                    MakeToast.show(this, "Pan Card image required");

                                            } else MakeToast.show(this, "Amount can't be empty");
                                        } else MakeToast.show(this, "Aadhar Number can't be empty");
                                    } else MakeToast.show(this, "Pan Card Number can't be empty");
                                } else MakeToast.show(this, "Pin Code can't be empty");
                            } else MakeToast.show(this, "Shop Address can't be empty");

                        } else MakeToast.show(this, "Retailer Address can't be empty");
                    } else MakeToast.show(this, "Shop Name can't be empty");
                } else MakeToast.show(this, "Mobile Number can't be empty");
            } else MakeToast.show(this, "Email ID can't be empty");
        } else MakeToast.show(this, "Full Name can't be empty");
        return isValid;*/
    }


    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PANCARD_IMAGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmPancardImage = AppUitls.getResizedBitmap(bitmap);
                    bmPancardImage = getRotateImageBitmap(filePath,bmPancardImage);
                    iv_pancardImage.setImageBitmap(bmPancardImage);

                }
            } else if (requestCode == AADHAR_FRONT_IMAGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmAadharFrontImage = AppUitls.getResizedBitmap(bitmap);
                    bmAadharFrontImage = getRotateImageBitmap(filePath,bmAadharFrontImage);
                    iv_aadharFrontImage.setImageBitmap(bmAadharFrontImage);
                    //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 64, 64);
                }
            } else if (requestCode == AADHAR_BACK_IAMGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmAadharBackImage = AppUitls.getResizedBitmap(bitmap);
                    bmAadharBackImage = getRotateImageBitmap(filePath,bmAadharBackImage);
                    iv_aadharBackImage.setImageBitmap(bmAadharBackImage);
                    //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 64, 64);
                }
            } else if (requestCode == APPLICATION_FORM_IMAGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmApplicationFormImage = AppUitls.getResizedBitmap(bitmap);
                    iv_applicationForm.setImageBitmap(bmApplicationFormImage);
                    //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 64, 64);
                }
            } else if (requestCode == SHOP_IMAGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmShopImage = AppUitls.getResizedBitmap(bitmap);
                    iv_shopImage.setImageBitmap(bmShopImage);
                    //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 64, 64);
                }
            } else if (requestCode == RETAILER_IMAGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmRetailerImage = AppUitls.getResizedBitmap(bitmap);

                    bmRetailerImage = getRotateImageBitmap(filePath,bmRetailerImage);

                    iv_retailerImage.setImageBitmap(bmRetailerImage);
                    //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 64, 64);
                }
            } else if (requestCode == CHEQUE_IMAGE_RESULT_CODE) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ALPHA_8;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                    bmChequeImage = AppUitls.getResizedBitmap(bitmap);
                    iv_chequeImage.setImageBitmap(bmChequeImage);
                    //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 64, 64);
                }
            }

        }

    }

    private Bitmap getRotateImageBitmap(String filepath,Bitmap bitmap)
    {
        ExifInterface exif = null;
        try {
            File pictureFile = new File(filepath);
            exif = new ExifInterface(pictureFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = ExifInterface.ORIENTATION_NORMAL;

        if (exif != null)
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateBitmap(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateBitmap(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateBitmap(bitmap, 270);
                break;
        }
        return bitmap;
    }
    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

    public boolean validate(String mobileEmail, int lastDigit, String SHA256) {
        if (lastDigit == 0) lastDigit++; // same case for 0 and 1

        String createdHashCode = toHexString(getSHA(mobileEmail));

        // now we are getting the hashcode for the string here
        for (int i = 1; i < lastDigit; i++) {
            createdHashCode = toHexString(getSHA(createdHashCode));
        }

        return createdHashCode.equals(SHA256);
    }

    private void panVerify()
    {
        progressState.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.PAN_VERIFY,
                response -> {
                    try {
                        progressState.setVisibility(View.GONE);
                           Log.e("pan verify","="+response);
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")) {

                            String panName = jsonObject.getString("name");
                            Log.e("name","="+ed_fullName.getText().toString());
                            if(!ed_fullName.getText().toString().toLowerCase().contains(panName.toLowerCase()))
                            {
                                Dialog dialog = AppDialogs.transactionStatus(
                                        this, "Your Adhaar Name and PanCard Name is mismatched", 2);
                                dialog.show();
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                btn_ok.setOnClickListener(view -> dialog.dismiss());
                            }
                            else {
                                kyc_status="1";
                                appPreference.setPan(""+sPancard);
                                lin_img.setVisibility(View.VISIBLE);
                                rel_update.setVisibility(View.VISIBLE);
                                ed_pancard.setEnabled(false);
                                btn_pan_verify.setVisibility(View.GONE);
                                Dialog dialog = AppDialogs.transactionStatus(
                                        this, "Verification Successfull.\n"+panName, 1);
                                dialog.show();
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                btn_ok.setOnClickListener(view -> dialog.dismiss());
                            }

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {

                            Intent intent = new Intent(this, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            Dialog dialog = AppDialogs.transactionStatus(
                                    this, message, 2);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                        }

                    } catch (Exception e) {
                        progressState.setVisibility(View.GONE);
                    }
                },
                error -> {

                    progressState.setVisibility(View.GONE);

                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(CreateRetailerActivity.this).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(CreateRetailerActivity.this).getToken()));

                param.put("pan_number", sPancard);


                return param;
            }

        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
