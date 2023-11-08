package com.a2zsuvidhaa.in.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.MainActivity;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.NumberToWordsConverter;
import com.a2zsuvidhaa.in.util.SoftKeyboard;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class R2RTransferFragment extends Fragment {


    private Spinner spn_idNumber;
    private EditText ed_input;
    private ImageButton btn_search;
    private ProgressBar progress_search;
    private TextView tv_inputTitle;
    private TextView tv_errorResponse;
    private Button btn_transfer;
    private ProgressBar progress_transfer;
    private EditText ed_amount;
    private EditText ed_remark;
    private LinearLayout ll_content;
    private RelativeLayout rl_progress;

    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_shopName;
    private TextView tv_mobile;
    private TextView tv_amountInWord;

    private String searchType = "ID";
    private String searchInput;
    private String memberId;
    private String remark;
    private String amount;

    private static final String TAG = "R2RTransferFragment";

    public R2RTransferFragment() {
    }

    public static R2RTransferFragment newInstance() {
        return new R2RTransferFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_r2r_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        setupSpnIdNumber();
        btn_search.setOnClickListener(view1 -> {
            searchInput = ed_input.getText().toString();
            if (!searchInput.isEmpty()) {
                getRetailer();
            } else MakeToast.show(getActivity(), "Enter Id or Mobile number!");
        });

        btn_transfer.setOnClickListener(view1 -> {
            remark = ed_remark.getText().toString();
            amount = ed_amount.getText().toString();
            if (!amount.isEmpty()) {
                if (!remark.isEmpty()) {
                    enterPin();
                } else MakeToast.show(getActivity(), "Enter remark");
            } else MakeToast.show(getActivity(), "Enter Amount!");

        });

        ed_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!ed_amount.getText().toString().isEmpty()){
                    String amountInWord = NumberToWordsConverter.convert(Integer.parseInt(ed_amount.getText().toString()));
                    tv_amountInWord.setText(amountInWord+" Rs only/-");
                }
                else {
                    tv_amountInWord.setText("Enter amount");
                }


            }
        });
    }

    private void enterPin() {
        Dialog dialog = AppDialogs.r2rTransferConfimationWithPin(getActivity());
        EditText ed_confirmAmount = dialog.findViewById(R.id.ed_confirmAmount);
        EditText ed_verificationPin = dialog.findViewById(R.id.ed_verificationPin);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_confirm = dialog.findViewById(R.id.btn_confirm);

        TextView tv_shopNameD = dialog.findViewById(R.id.tv_shopName);
        TextView tv_usernameD = dialog.findViewById(R.id.tv_username);
        TextView tv_mobileD = dialog.findViewById(R.id.tv_mobile);
        TextView tv_transferAmount = dialog.findViewById(R.id.tv_transferAmount);

        tv_shopNameD.setText(tv_shopName.getText().toString());
        tv_usernameD.setText(tv_name.getText().toString()+" - "+tv_id.getText().toString());
        tv_mobileD.setText(tv_mobile.getText().toString());
        tv_transferAmount.setText(ed_amount.getText().toString());

        btn_cancel.setOnClickListener(view1 -> dialog.dismiss());

        btn_confirm.setOnClickListener(view1 -> {
            String pin = ed_verificationPin.getText().toString();
            if(ed_confirmAmount.getText().toString().equalsIgnoreCase(amount)) {
                if (!pin.isEmpty()) {
                    verifyPin(pin);
                    dialog.dismiss();
                } else MakeToast.show(getActivity(), "Enter verification pin");
            }else MakeToast.show(getActivity(),"Amount does not matched!");
        });

        dialog.show();
    }

    private void setupSpnIdNumber() {

        String[] types = {"ID", "NUMBER"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.spinner_layout, types);
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spn_idNumber.setAdapter(dataAdapter);
        spn_idNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchType = spn_idNumber.getSelectedItem().toString();
                if (searchType.equalsIgnoreCase("ID")) {
                    tv_inputTitle.setText("Enter Retailer ID");
                    ed_input.setHint("Enter ID");
                } else {
                    tv_inputTitle.setText("Enter Retailer Number");
                    ed_input.setHint("Enter Number");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void init(View view) {
        spn_idNumber = view.findViewById(R.id.spn_idNumber);
        ed_input = view.findViewById(R.id.ed_input);
        btn_search = view.findViewById(R.id.btn_search);
        progress_search = view.findViewById(R.id.pg_search);
        tv_inputTitle = view.findViewById(R.id.tv_inputTitle);
        tv_errorResponse = view.findViewById(R.id.tv_errorResponse);
        ed_amount = view.findViewById(R.id.ed_amount);
        ed_remark = view.findViewById(R.id.ed_remark);
        tv_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        tv_mobile = view.findViewById(R.id.tv_mobile);
        tv_amountInWord = view.findViewById(R.id.tv_amountInWord);
        tv_shopName = view.findViewById(R.id.tv_shopName);
        progress_transfer = view.findViewById(R.id.pg_transfer);
        rl_progress = view.findViewById(R.id.rl_progress);
        btn_transfer = view.findViewById(R.id.btn_transfer);
        ll_content = view.findViewById(R.id.ll_content);


    }


    private void getRetailer() {

        setSearchProgress(true);
        String url =APIs.GET_RETAILER_DETAILS
                + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken()
                + "&SEARCH_TYPE=" + searchType
                + "&INPUT=" + searchInput;
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        boolean hasData = jsonObject.getBoolean("hasData");

                        if (status.equalsIgnoreCase("1")) {
                            if (hasData) {

                                ll_content.setVisibility(View.VISIBLE);
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    tv_id.setText(data.getString("id"));
                                    tv_name.setText(data.getString("name"));
                                    tv_mobile.setText(data.getString("mobile"));
                                    tv_shopName.setText(data.getString("shopName"));
                                    memberId = data.getString("memberId");

                                }
                                ll_content.setVisibility(View.VISIBLE);
                            } else {
                                tv_errorResponse.setText(message);
                                tv_errorResponse.setVisibility(View.VISIBLE);
                            }

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            tv_errorResponse.setText(message);
                            tv_errorResponse.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        tv_errorResponse.setText("Something went wrong!\ntry again");
                        tv_errorResponse.setVisibility(View.VISIBLE);
                    }
                    setSearchProgress(false);
                },
                error -> {

                    setSearchProgress(false);
                    tv_errorResponse.setText("Something went wrong!\ntry again");
                    tv_errorResponse.setVisibility(View.VISIBLE);

                }) {


        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void fundTransfer() {

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.FUND_TRANSFER_R2R,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            Dialog dialog = AppDialogs.transactionStatus(getActivity(),message,1);
                            Button btn_ok = dialog.findViewById(R.id.btn_ok);
                            btn_ok.setOnClickListener(view->{
                                dialog.dismiss();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                            dialog.setOnCancelListener(dialog1 -> {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                            dialog.show();

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {

                            tv_errorResponse.setText(message);
                            tv_errorResponse.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        tv_errorResponse.setText("Something went wrong!\ntry again");
                        tv_errorResponse.setVisibility(View.VISIBLE);
                    }

                    setTransferProgress(false);
                },
                error -> {
                    setTransferProgress(false);
                    tv_errorResponse.setText("Something went wrong!\ntry again");
                    tv_errorResponse.setVisibility(View.VISIBLE);
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(getActivity()).getToken()));
                param.put("amount",amount);
                param.put("dt_scheme","0.0");
                param.put("wallet","0");
                param.put("remark",remark);
                param.put("memberId",memberId);
                return param;
            }

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setSearchProgress(boolean progress) {
        if (progress) {
            btn_search.setVisibility(View.GONE);
            progress_search.setVisibility(View.VISIBLE);
            tv_errorResponse.setVisibility(View.GONE);
            ll_content.setVisibility(View.GONE);
        } else {
            btn_search.setVisibility(View.VISIBLE);
            progress_search.setVisibility(View.GONE);
        }
    }
    private void setTransferProgress(boolean progress) {
        if (progress) {
            btn_transfer.setVisibility(View.GONE);
            progress_transfer.setVisibility(View.VISIBLE);
            rl_progress.setVisibility(View.VISIBLE);
            tv_errorResponse.setVisibility(View.GONE);
        }
        else {


            btn_transfer.setVisibility(View.VISIBLE);
            progress_transfer.setVisibility(View.GONE);
            rl_progress.setVisibility(View.GONE);
        }
    }

    private void verifyPin(String pin) {

        setTransferProgress(true);
        final StringRequest request = new StringRequest(Request.Method.GET,
                APIs.VERIFY_PIN
                        + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                        + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken()
                        + "&pin=" + pin
                        + "&type=TXN",
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {
                            SoftKeyboard.hide(Objects.requireNonNull(getActivity()));
                            fundTransfer();

                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(getActivity(), AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            setTransferProgress(false);
                            tv_errorResponse.setText(message);
                            tv_errorResponse.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        setTransferProgress(false);
                        tv_errorResponse.setText("Something went wrong!\ntry again");
                        tv_errorResponse.setVisibility(View.VISIBLE);
                    }

                },
                error -> {

                    setTransferProgress(false);
                    tv_errorResponse.setText("Something went wrong!\ntry again");
                    tv_errorResponse.setVisibility(View.VISIBLE);

                }) {


        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
