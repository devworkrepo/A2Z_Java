package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.PasswordValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ChangePasswordFragment extends Fragment {

    private static final String TAG = "ChangePassword";
    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        return fragment;
    }


    private EditText ed_currentPassword;
    private EditText ed_newPassword;
    private EditText ed_confirmPassword;
    private TextView tv_incorrect;
    private ProgressBar progressBar;
    private RelativeLayout rl_progress;

    private String strCurrentPasswrod;
    private String strNewPasword;
    private String strConfirmPassword;

    private Button btnChangePassword;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        btnChangePassword.setOnClickListener(view1->{
            if(validInput()){
                tv_incorrect.setVisibility(View.GONE);
                changePassword();
            }
        });
    }

    private void changePassword() {
        setProgressVisibility(true,1);
        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.CHANGE_PASSWROD,

                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if(status.equalsIgnoreCase("1")){
                            clearData();
                            Dialog dialog = AppDialogs.transactionStatus(getActivity(), message, 1);
                            dialog.show();
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view -> dialog.dismiss());
                        }
                        else if(status.equalsIgnoreCase("200")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",0);
                            startActivity(intent);
                        }
                        else if(status.equalsIgnoreCase("300")){
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message",message);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }
                        else {
                            showErrorMessage(message,true);
                        }


                    } catch (JSONException e) {
                        showErrorMessage("Something wend wrong! try again",true);

                    }
                    setProgressVisibility(false,1);

                },
                error -> {
                    setProgressVisibility(false,1);
                    showErrorMessage("Something wend wrong! try again",true);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                param.put("old_password", strCurrentPasswrod);
                param.put("password", strNewPasword);
                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void clearData() {
        ed_confirmPassword.setText("");
        ed_currentPassword.setText("");
        ed_newPassword.setText("");
    }

    private void setProgressVisibility(boolean b, int type) {

        if(b){
            progressBar.setVisibility(View.VISIBLE);
            rl_progress.setVisibility(View.VISIBLE);
            btnChangePassword.setVisibility(View.GONE);
            ed_newPassword.setEnabled(false);
            ed_confirmPassword.setEnabled(false);
            ed_currentPassword.setEnabled(false);
            ed_currentPassword.setAlpha(0.5f);
            ed_newPassword.setAlpha(0.5f);
            ed_confirmPassword.setAlpha(0.5f);
        }else{
            progressBar.setVisibility(View.GONE);
            rl_progress.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.VISIBLE);
            ed_newPassword.setEnabled(true);
            ed_confirmPassword.setEnabled(true);
            ed_currentPassword.setEnabled(true);
            ed_currentPassword.setAlpha(1f);
            ed_newPassword.setAlpha(1f);
            ed_confirmPassword.setAlpha(1f);
        }

    }

    private void showErrorMessage(String message,boolean shouldShow){
        if(shouldShow){
            tv_incorrect.setText(message);
            tv_incorrect.setVisibility(View.VISIBLE);
        }
        else {
            tv_incorrect.setVisibility(View.GONE);
        }
    }

    private void initView(View view) {
        ed_currentPassword= view.findViewById(R.id.ed_current_password);
        ed_newPassword= view.findViewById(R.id.ed_new_password);
        ed_confirmPassword= view.findViewById(R.id.ed_conform_password);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        progressBar = view.findViewById(R.id.progressBar);
        rl_progress = view.findViewById(R.id.rl_progress);
        tv_incorrect = view.findViewById(R.id.tv_incorrect);

    }

    private boolean validInput(){
        boolean isValid = false;
        strCurrentPasswrod = ed_currentPassword.getText().toString();
        strNewPasword = ed_newPassword.getText().toString();
        strConfirmPassword = ed_confirmPassword.getText().toString();

        if(!strCurrentPasswrod.isEmpty()){
            if(!strNewPasword.isEmpty()){
                if(PasswordValidator.validate(strNewPasword)){
                    if(!strConfirmPassword.isEmpty()){
                        isValid = true;
                    }else MakeToast.show(getActivity(),"Please confirm Passord! Doesn't match");
                }else MakeToast.show(getActivity(),"Password should have number, special character, Capital and Small alphabet and length greater than 8 digit!",2);
            }else MakeToast.show(getActivity(),"Enter new password");
        }else MakeToast.show(getActivity(),"Current password is empty!");
        return isValid;
    }

}
