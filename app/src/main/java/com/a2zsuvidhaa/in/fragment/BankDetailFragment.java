package com.a2zsuvidhaa.in.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.activity.AdhaarPay.AdhaarActivity;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.activity.MainActivity;
import com.a2zsuvidhaa.in.activity.PaymentGatewayActivity;
import com.a2zsuvidhaa.in.adapter.BankDetailAdapter;
import com.a2zsuvidhaa.in.adapter.TCAdapter;
import com.a2zsuvidhaa.in.adapter.TagsAdapter;
import com.a2zsuvidhaa.in.fragment.home.HomeFragment;
import com.a2zsuvidhaa.in.listener.WebApiCallListener;
import com.a2zsuvidhaa.in.model.BankDetail;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;
import com.a2zsuvidhaa.in.util.Security;
import com.a2zsuvidhaa.in.util.WebApiCall;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BankDetailFragment extends Fragment {


    public BankDetailFragment() {
    }

    private TextView tv_error_hint;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private BankDetailAdapter adapter;
    private ArrayList<BankDetail> bankList;

    ArrayList<String> tags;
    TagsAdapter tagsAdapter;
    GridView tags_gv;

    ArrayList<String> text;
    TCAdapter textAdapter;
    ListView text_lv;
    Button submit;

    //UPI
    LinearLayout lin_upi;
    TextInputLayout tl_amount;
    EditText et_amount;

    public static String type="BT";
    public static BankDetailFragment newInstance() {
        return new BankDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_service_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lin_upi = view.findViewById(R.id.lin_upi);
        tl_amount = view.findViewById(R.id.tl_amount);
        et_amount = view.findViewById(R.id.et_amount);
        tv_error_hint = view.findViewById(R.id.tv_error_hint);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        submit=view.findViewById(R.id.btn_start);
        recyclerView.setHasFixedSize(false);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        et_amount.requestFocus();
        lin_upi.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(view12 -> {
            if(MainActivity.spos==0)
            {
                generateQRCode();
            }
            else if(MainActivity.spos==3)
            {
                Intent intent = new Intent(getActivity(), AdhaarActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getActivity(), PaymentGatewayActivity.class);
                startActivity(intent);
            }
        });

        tags_gv=view.findViewById(R.id.grid);
        text_lv=view.findViewById(R.id.list);

        tags=new ArrayList<>();
        text=new ArrayList<>();

        tags.add("UPI");
        tags.add("Bank Transfer");
        tags.add("Cash(CDM)");
        tags.add("Cash Deposit");
      //  tags.add("Adhaar Pay");
        tags.add("Payment Gateway");
        tags.add("Cash Collect");

        tagsAdapter=new TagsAdapter(getActivity(),tags);

        tags_gv.setAdapter(tagsAdapter);
        setDynamicHeight(tags_gv);

        text=new ArrayList<>();
        text.add("* No Charges to load money using UPI.");
        text.add("* Topup between ₹100 to ₹2,00,000 in a single transaction.");
        text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account.");

        textAdapter=new TCAdapter(getActivity(),text);
        text_lv.setAdapter(textAdapter);
        setDynamicHeight(text_lv);


        tags_gv.setOnItemClickListener((adapterView, view1, i, l) -> {

            MainActivity.mode_pos=MainActivity.spos=i;

            tagsAdapter.notifyDataSetChanged();
           // adapter.notifyDataSetChanged();
            text=new ArrayList<>();

            if(i==0)
            {
                recyclerView.setVisibility(View.GONE);
                lin_upi.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                text.add("* No Charges to load money using UPI.");
                text.add("* Topup between ₹100 to ₹2,00,000 in a single transaction.");
                text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account.");
            }
            else if(i==1)
            {
                type = "BT";
                getServiceList();
                lin_upi.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);
                text.add("* No Charges to load money using this mode.");
                text.add("* Payee Name: Excel One Stop Solution Pvt ltd");
                text.add("* If you are transferring funds from ICICI Bank to our ICICI Bank account then go to Transfers > Pay to Virtual Account option.");
                text.add("* Balance is updated daily within 30 min upon realisation of funds in our bank account.");
                text.add("* Note: Changing your registered mobile number with Excel One Stop Solution Pvt ltd will change your deposit bank account number.");
            }
            else if(i==2)
            {
                type = "CASH_CDM";
                getServiceList();
                lin_upi.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);
                text.add("* 100% penalty would be debited for the value of soiled or fake currency deposited to our account.");
                text.add("* Please Note: Excel One Stop Solution Pvt ltd will not be liable if payment is made with wrong details or to the wrong account, please double check all the details before making a cash deposit.");
            }
            else if(i==3){

                type = "CASH_DEPOSIT";
                getServiceList();
                lin_upi.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);

                text.add("* Balance is updated daily (10AM - 9PM) within 1 hour upon realisation of funds in our bank account.");
                text.add("* 100% penalty would be debited for the value of soiled or fake currency deposited to our account.");
                text.add("* Please Note: Excel One Stop Solution Pvt ltd will not be liable if payment is made with wrong details or to the wrong account, please double check all the details before making a cash deposit.");


            }
           /* else if(i==4)
            {
                lin_upi.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                text.add("* Maximum topup of ₹10,000 is allowed in a single transaction.");
                text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account. ");
            }*/
            else if (i==4){


                lin_upi.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                text.add("* Topup start Minimum ₹100.");
                text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account.");

            }
            else {
                type = "CASH_COLLECT";
                getServiceList();
                lin_upi.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);

                text.add("* Balance is updated daily (10AM - 9PM) within 1 hour upon realisation of funds in our bank account.");
                text.add("* 100% penalty would be debited for the value of soiled or fake currency deposited to our account.");
                text.add("* Please Note: Excel One Stop Solution Pvt ltd will not be liable if payment is made with wrong details or to the wrong account, please double check all the details before making a cash deposit.");

            }
            textAdapter=new TCAdapter(getActivity(),text);
            text_lv.setAdapter(textAdapter);
            setDynamicHeight(text_lv);
        });

        bankList = new ArrayList<>();


        //getServiceList();

    }
    private void setDynamicHeight(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > 2 ){
            x = items/2;
            rows = (int) (x + 1);
            totalHeight *= 3;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }
    private void setDynamicHeight(ListView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > 1 ){
            x = items/1;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight+20;
        text_lv.setLayoutParams(params);
    }
    private void generateQRCode() {



        submit.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final StringRequest request = new StringRequest(Request.Method.POST, APIs.GENERATE_QRCODE_URL,
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        JSONObject object = new JSONObject(response);
                        Log.e("GENERATEQRCODE resp","="+object.toString());

                        int status = object.getInt("status");
                        if (status == 1) {
                            Security security = new Security(APIs.ENCRYPTED_KEY);
                            String refId = object.getString("refId");
                            String upi_str = "upi://pay?pa=excelone@icici&pn=Excel Stop&tr="+refId+"&am="+et_amount.getText().toString()+"&cu=INR&mc=5411&tn="+ security.decrypt(AppPreference.getInstance(getActivity()).getMobile())+""+ AppPreference.getInstance(getActivity()).getShopName();
                            Log.e("upi_str",""+upi_str);
                            Intent intent = new Intent();
                          //  intent.addCategory(Intent.CATEGORY_HOME);
                            //intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse(upi_str));
                            Intent chooser = Intent.createChooser(intent, "Pay with...");
                            startActivityForResult(chooser, 1, null);
                           // et_amount.setText("");
                        } else if (status == 0) {

                            Toast.makeText(getActivity(), ""+object.getString("message"), Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        MakeToast.show(getActivity(), e.getMessage());

                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                    MakeToast.show(getActivity(), "on error");
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        MakeToast.show(getActivity(),"no internet connection");
                    } else if (error instanceof AuthFailureError) {
                        MakeToast.show(getActivity(),"AuthFailureError");
                    } else if (error instanceof ServerError) {
                        MakeToast.show(getActivity(),"ServerError");
                    } else if (error instanceof NetworkError) {
                        MakeToast.show(getActivity(),"NetworkError");
                    } else if (error instanceof ParseError) {
                        MakeToast.show(getActivity(),"ParseError");
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();
                param.put("amount", et_amount.getText().toString());

                param.put("token", AppPreference.getInstance(getActivity()).getToken());
                param.put("userId", String.valueOf(AppPreference.getInstance(getActivity()).getId()));
                Log.d("UPI",param.toString());
                return param;
            }
        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void getServiceList() {


        progressBar.setVisibility(View.VISIBLE);

       String url =  APIs.GET_BANK_DETAILS+ "?&type="+type;


        WebApiCall.getRequest(requireContext(),url);
        WebApiCall.webApiCallback(new WebApiCallListener() {
            @Override
            public void onSuccessResponse(JSONObject jsonObject) {
                try {

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equalsIgnoreCase("1")) {

                        bankList=new ArrayList<>();
                        JSONArray listArray = jsonObject.getJSONArray("banks");

                        for(int i = 0;i<listArray.length();i++){
                            JSONObject finalObject = listArray.getJSONObject(i);

                            BankDetail bankDetail = new BankDetail();
                            bankDetail.setId(finalObject.getString("id"));
                            bankDetail.setCharges(finalObject.getString("charge"));
                            bankDetail.setAccount_number(finalObject.getString("account_number"));
                            bankDetail.setBranchName(finalObject.getString("branch_name"));
                            bankDetail.setBankName(finalObject.getString("bank_name"));
                            bankDetail.setMessageOne(finalObject.getString("message_one"));
                            bankDetail.setMessageTwo(finalObject.getString("message_two"));
                            bankDetail.setIfsc(finalObject.getString("ifsc"));
                            bankDetail.setBank_transfer_place_holder(finalObject.getString("bank_transfer_place_holder"));
                            bankDetail.setCash_deposit_place_holder(finalObject.getString("cash_deposit_place_holder"));
                            bankDetail.setCash_cdm_place_holder(finalObject.getString("cash_cdm_place_holder"));

                            bankList.add(bankDetail);
                        }

                        adapter = new BankDetailAdapter(bankList,getActivity());
                        recyclerView.setAdapter(adapter);




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
                    }
                    else{
                        tv_error_hint.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {

                    tv_error_hint.setVisibility(View.VISIBLE);
                    MakeToast.show(getActivity(), e.getMessage());

                }


                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String message) {
                tv_error_hint.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                MakeToast.show(getActivity(), "onError");
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("fragment response pg", "=" + requestCode);
        if (requestCode == 1) {
            if(AppPreference.getInstance(getActivity()).getRollId()==5)
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, HomeFragment.Companion.newInstance()).commit();
            }
        else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, BankDetailFragment.newInstance()).commit();
        }
    }
}
