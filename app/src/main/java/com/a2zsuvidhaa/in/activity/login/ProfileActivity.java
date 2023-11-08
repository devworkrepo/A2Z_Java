package com.a2zsuvidhaa.in.activity.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.PermissionHandler;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.activity.ForgetPasswordActivity;
import com.a2zsuvidhaa.in.adapter.DocumentsAdapter;
import com.a2zsuvidhaa.in.model.LoginData;
import com.a2zsuvidhaa.in.model.UserModel;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.AppUitls;
import com.a2zsuvidhaa.in.util.InternetConnection;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    CheckBox cb_tc;
    TextView tv_tc,tv_email_verify;

    RelativeLayout rr;
    LinearLayout layout_otp;
    private EditText ed_mobile,ed_name,ed_email,ed_address,ed_shopName,ed_shopAddress,ed_pin,ed_pancard,ed_adhaarcard;
    private TextView tv_incorrect;
    Button btn_signup;

    private RelativeLayout rl_progress;
    private ProgressBar progressBar;

    RecyclerView recyclerViewD;
    List<UserModel> memberListD = new ArrayList<>();
    DocumentsAdapter adapterD;

    ImageView cim;
    int pos_doc;

    String str=""; int strOldlen=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();

    }

    private void initView()
    {

        ed_mobile = findViewById(R.id.ed_mobile);
        ed_name = findViewById(R.id.ed_name);
        ed_email = findViewById(R.id.ed_email);
        ed_address = findViewById(R.id.ed_address);
        ed_shopName = findViewById(R.id.ed_shopName);
        ed_shopAddress = findViewById(R.id.ed_shopAddress);
        ed_pin = findViewById(R.id.ed_pin);
        ed_pancard = findViewById(R.id.ed_pancard);
        ed_adhaarcard = findViewById(R.id.ed_adhaarcard);
       // ed_name = findViewById(R.id.ed_name);
        recyclerViewD=findViewById(R.id.recycle);
        btn_signup = findViewById(R.id.btn_signup);
        tv_incorrect = findViewById(R.id.tv_incorrect);
        rl_progress = findViewById(R.id.rl_progress);
        progressBar = findViewById(R.id.progressBar);
        cb_tc=findViewById(R.id.cb_tc);
        tv_tc=findViewById(R.id.tv_tc);


        ed_adhaarcard.setText("");
        ed_adhaarcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                str = ed_adhaarcard.getText().toString();
                int strLen = str.length();


                if(strOldlen<strLen) {

                    if (strLen > 0) {
                        if (strLen == 4 || strLen == 9) {

                            str=str+"-";

                            ed_adhaarcard.setText(str);
                            ed_adhaarcard.setSelection(ed_adhaarcard.getText().length());

                        }else{

                            if(strLen==5){
                                if(!str.contains("-")){
                                    String tempStr=str.substring(0,strLen-1);
                                    tempStr +="-"+str.substring(strLen-1,strLen);
                                    ed_adhaarcard.setText(tempStr);
                                    ed_adhaarcard.setSelection(ed_adhaarcard.getText().length());
                                }
                            }
                            if(strLen==10){
                                if(str.lastIndexOf("-")!=9){
                                    String tempStr=str.substring(0,strLen-1);
                                    tempStr +="-"+str.substring(strLen-1,strLen);
                                    ed_adhaarcard.setText(tempStr);
                                    ed_adhaarcard.setSelection(ed_adhaarcard.getText().length());
                                }
                            }
                            strOldlen = strLen;
                        }
                    }else{
                        return;
                    }

                }else{
                    strOldlen = strLen;


                    Log.i("MainActivity ","keyDel is Pressed ::: strLen : "+strLen+"\n old Str Len : "+strOldlen);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tv_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDialogs.showMessageDialogtc(ProfileActivity.this);
            }
        });


        btn_signup.setOnClickListener(view -> {
            if (isValid()) {
                if (InternetConnection.isConnected(ProfileActivity.this)) {
                    signup();

                } else MakeToast.show(ProfileActivity.this, "No Internet Connection Available!");
            }
        });
        PermissionHandler.setPermissionGrantedListener(isGranted -> {
            if(isGranted){
                if (isValid()) {
                    if (InternetConnection.isConnected(ProfileActivity.this)) {
                        signup();

                    } else MakeToast.show(ProfileActivity.this, "No Internet Connection Available!");
                }
            }
        });

        UserModel userModel=new UserModel();
        userModel.setImg(false);
        userModel.setName("Photo");
        userModel.setImg("null");

        memberListD.add( userModel);

        UserModel userModel1=new UserModel();
        userModel1.setImg(false);
        userModel1.setName("PanCard");
        userModel1.setImg("null");

        memberListD.add( userModel1);

        UserModel userModel2=new UserModel();
        userModel2.setImg(false);
        userModel2.setName("AdhaarCard Front");
        userModel2.setImg("null");



        memberListD.add( userModel2);

        UserModel userModel3=new UserModel();
        userModel3.setImg(false);
        userModel3.setName("AdhaarCard Back");
        userModel3.setImg("null");

        memberListD.add( userModel3);




        adapterD = new DocumentsAdapter(ProfileActivity.this, memberListD, new DocumentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, CircleImageView cim, int position, UserModel userModel) {

                memberListD.get(position).setImg(false);
                memberListD.get(position).setImg_str("");
                cim.setImageResource(R.drawable.add_pic);
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAddItemClick(ImageView cimm, int poss, ImageView closee) {

                cim=cimm;
                pos_doc=poss;
                Log.e("pos doc",""+pos_doc);
                PermissionHandler.checkStorageAndCameraPermission(ProfileActivity.this);

            }
        });

        PermissionHandler.setPermissionGrantedListener(isGranted -> {
            if(isGranted){
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);

            }
        });

        recyclerViewD.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileActivity.this, RecyclerView.HORIZONTAL, false);

        recyclerViewD.setLayoutManager(linearLayoutManager);

        recyclerViewD.setAdapter(adapterD);

    }

    /////////////
    private Bitmap imageBitmap = null;
    private final static int IMAGE_RESULT = 200;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_RESULT) {

                // Log.e("filePath","="+filePath);
                Bitmap bitmap = getImageBitmap(data);
                if (bitmap != null) {

                    imageBitmap = AppUitls.getResizedBitmap(bitmap);
                    memberListD.get(pos_doc).setImg(true);
                    memberListD.get(pos_doc).setImg_str(AppUitls.getStringImage(imageBitmap));
                    cim.setImageBitmap(imageBitmap);

                }
            }

        }

    }
    private Bitmap getImageBitmap(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        Bitmap bitmap=null;
        String filePath="";
        Log.e("isCamera", "=" + isCamera);

        if (isCamera) {
            filePath = getCaptureImageOutputUri().getPath();

        }
        else {
            filePath =  getPath(data.getData());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        return bitmap;
    }
    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        Log.e("isCamera","="+isCamera);
        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return data.getData().toString();

    }
    public  String getPath(Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }
    //////////////
    private boolean isValid() {
        boolean isValid = false;

        if (!ed_name.getText().toString().isEmpty()) {
            if (!ed_mobile.getText().toString().isEmpty()) {
                if (ed_mobile.getText().toString().length() >= 10) {
                    if (!ed_email.getText().toString().isEmpty()) {
                        isValid = true;
                    } else MakeToast.show(this, "Email can't be empty");

                } else MakeToast.show(this, "Mobile Number can't be less than 10 digit");
            } else MakeToast.show(this, "Mobile Number can't be empty");

        } else MakeToast.show(this, "Name can't be empty");
            return isValid;
        }

    private void signup() {

        rl_progress.setVisibility(View.VISIBLE);
        btn_signup.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tv_incorrect.setVisibility(View.GONE);
     //   editTextUnFoucus();
        final StringRequest request = new StringRequest(Request.Method.POST, APIs.SIGNIN_URL,
                response -> {
                    try {


                        JSONObject object = new JSONObject(response);
                        Log.e("login resp","="+object.toString());
                        LoginData loginData;
                        int status = object.getInt("status");
                        if (status == 1) {


                        } else if (status == 2) {
                            rl_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_signup.setVisibility(View.VISIBLE);
                            tv_incorrect.setText(object.getString("message"));
                            tv_incorrect.setVisibility(View.VISIBLE);

                        } else if (status == 3) {
                            MakeToast.show(ProfileActivity.this, object.getString("message"));
                            Intent intent = new Intent(ProfileActivity.this, DeviceVerificationActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (status == 4) {
                            MakeToast.show(this, "4");
                        }
                        else if (status == 18) {
                            rl_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_signup.setVisibility(View.VISIBLE);
                            tv_incorrect.setText(object.getString("message"));
                            tv_incorrect.setVisibility(View.VISIBLE);

                        }
                        else if (status == 108) {
                            rl_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            btn_signup.setVisibility(View.VISIBLE);
                            String msg=object.getString("message");
                            String strMobile=object.getString("mobile");
                            Dialog dialog1 = AppDialogs.transactionStatus(ProfileActivity.this, msg, 1);
                            Button btn_ok = dialog1.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> {

                                dialog1.dismiss();
                                Intent intent = new Intent(this, ForgetPasswordActivity.class);
                                intent.putExtra("mobile",strMobile);
                                startActivity(intent);
                            });
                            dialog1.show();
                        }




                        rl_progress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        btn_signup.setVisibility(View.VISIBLE);
                        //editTextFoucus();

                    } catch (JSONException e) {
                        //editTextFoucus();
                        rl_progress.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        btn_signup.setVisibility(View.VISIBLE);
                        tv_incorrect.setVisibility(View.VISIBLE);
                        tv_incorrect.setText(e.getMessage());
                        MakeToast.show(ProfileActivity.this, e.getMessage());

                    }
                },
                error -> {
                    rl_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    btn_signup.setVisibility(View.VISIBLE);
                    tv_incorrect.setText("Login failed -> Something went wrong");
                    tv_incorrect.setVisibility(View.VISIBLE);
                    //editTextFoucus();
                    MakeToast.show(ProfileActivity.this, "on error");
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        MakeToast.show(this,"no internet connection");
                    } else if (error instanceof AuthFailureError) {
                        MakeToast.show(this,"AuthFailureError");
                    } else if (error instanceof ServerError) {
                        MakeToast.show(this,"ServerError");
                    } else if (error instanceof NetworkError) {
                        MakeToast.show(this,"NetworkError");
                    } else if (error instanceof ParseError) {
                        MakeToast.show(this,"ParseError");
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("mobile", ed_mobile.getText().toString());
                param.put("name", ed_name.getText().toString());
                param.put("email", ed_email.getText().toString());
                param.put("address", ed_address.getText().toString());
                param.put("company", ed_shopName.getText().toString());
                param.put("office_address", ed_shopAddress.getText().toString());
                param.put("pin_code", ed_pin.getText().toString());
                param.put("pan_number", ed_pancard.getText().toString());
                param.put("adhar_number", ed_adhaarcard.getText().toString());
                param.put("state_id", ed_shopAddress.getText().toString());
                param.put("role_id","5");

                param.put("profile_picture",memberListD.get(0).getImg_str() );
                param.put("pan_card_image",memberListD.get(1).getImg_str() );
                param.put("aadhaar_card_image", memberListD.get(2).getImg_str());
                param.put("aadhaar_card_image_back",memberListD.get(3).getImg_str() );

                Log.d("LoginTesting","=="+param.toString());
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
