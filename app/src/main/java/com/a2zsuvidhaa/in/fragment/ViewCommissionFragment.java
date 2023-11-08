package com.a2zsuvidhaa.in.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a2zsuvidhaa.in.util.AppUitls;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.adapter.CommissionAdapter;
import com.a2zsuvidhaa.in.model.GridModel;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.AppDialogs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class ViewCommissionFragment extends Fragment {


    public ViewCommissionFragment() {
        // Required empty public constructor
    }

    private TextView tv_no_data;
    private ProgressBar progressBar;
    String start_date = "";
    String end_date = "";

    private String page = "";
    private String search_url;
    private int year, month, day;
    private int hour, minute;
    Calendar myCalendar;

    LinearLayout linearLayout;

    private FloatingActionButton btn_search;

    public static ViewCommissionFragment newInstance() {
        return new ViewCommissionFragment();
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
        return inflater.inflate(R.layout.fragment_view_commissioin, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        progressBar = view.findViewById(R.id.progressBar);
        btn_search = view.findViewById(R.id.btn_search);

        myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        getCommissions(view);

        btn_search.setOnClickListener(view1 -> {


            start_date = "";
            end_date = "";

            Dialog dialog = AppDialogs.searchReport(getActivity());
            TextView tv_start_date = dialog.findViewById(R.id.tv_start_date);
            TextView tv_end_date = dialog.findViewById(R.id.tv_end_date);
            Button btn_search = dialog.findViewById(R.id.btn_search);
            ImageButton btn_cancel = dialog.findViewById(R.id.btn_cancel);


            tv_start_date.setOnClickListener(view2 -> {
                datePicker(tv_start_date, 1);
            });

            tv_end_date.setOnClickListener(view2 -> {
                datePicker(tv_end_date, 2);
            });

            btn_cancel.setOnClickListener(view3 -> {
                dialog.dismiss();
            });
            btn_search.setOnClickListener(view4 -> {

                if (!start_date.isEmpty() && !end_date.isEmpty()) {
                    getCommissions(view);
                    dialog.dismiss();
                }
            });

            dialog.show();

        });

    }

    void getCommissions(View view1) {




        progressBar.setVisibility(View.VISIBLE);
        tv_no_data.setVisibility(View.GONE);
        if (linearLayout != null)
            linearLayout.removeAllViews();

        String url = APIs.GET_RETAILER_COMMISSION;
        if (AppPreference.getInstance(getActivity()).getRollId() == 1)
            url = APIs.GET_ADMIN_COMMISSION;
        else if (AppPreference.getInstance(getActivity()).getRollId() == 4)
            url = APIs.GET_DISTRIBUTOR_COMMISSION;

        url = url + "?"+APIs.USER_TAG+"=" + AppPreference.getInstance(getActivity()).getId()
                + "&"+APIs.TOKEN_TAG+"=" +  AppPreference.getInstance(getActivity()).getToken()
                +"&fromdate="+ AppUitls.currentDate()+"&todate="+AppUitls.currentDate();

        if (!start_date.equalsIgnoreCase("") || !end_date.equalsIgnoreCase(""))
            url = url + "&fromdate=" + start_date + "&todate=" + end_date;
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    if (response.equalsIgnoreCase("[]")) {
                        MakeToast.show(getActivity(), "No Data Found");
                        tv_no_data.setVisibility(View.VISIBLE);
                    } else {
                        try {

                            LayoutInflater layoutInfralte = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            linearLayout = view1.findViewById(R.id.ll_main_layout);
                            linearLayout.setPadding(0, 0, 0, 150);
                            List<View> viewList = new ArrayList<>();

                            JSONObject mainObject = new JSONObject(response);
                            Iterator iterator = mainObject.keys();
                            while (iterator.hasNext()) {

                                ArrayList<GridModel> list = new ArrayList<>();

                                String key = (String) iterator.next();
                                JSONObject headingObject = mainObject.getJSONObject(key);
                                View view = layoutInfralte.inflate(R.layout.list_business_view, null);

                                TextView tv_heading = view.findViewById(R.id.tv_heading);
                                tv_heading.setText(key);

                                GridView gridView = view.findViewById(R.id.gridView);


                                Iterator headingIterator = headingObject.keys();
                                while (headingIterator.hasNext()) {
                                    String key1 = (String) headingIterator.next();
                                    GridModel model = new GridModel();
                                    JSONObject jsonObject = headingObject.getJSONObject(key1);
                                    model.setHeading(key1);
                                    model.setTotalSale(jsonObject.getString("Total Sale"));
                                    model.setCommission(jsonObject.getString("Commission"));
                                    if (AppPreference.getInstance(getActivity()).getRollId() != 4)
                                        model.setCharge(jsonObject.getString("Charge"));
                                    model.setTxnCount(jsonObject.getString("Txn Count"));

                                    list.add(model);

                                }


                                CommissionAdapter adapter = new CommissionAdapter(getActivity(), list);
                                gridView.setAdapter(adapter);
                                setDynamicHeight(gridView);


                                view.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                viewList.add(view);

                            }

                            for (int i = 0; i < viewList.size(); i++)
                                linearLayout.addView(viewList.get(i));


                            //addView(jsonObject);

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();

                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
                ,
                error -> {
                    progressBar.setVisibility(View.GONE);
                }) {

        };
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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





       // View listItem = gridViewAdapter.getView(0, null, gridView);
       // listItem.measure(0, 0);
      //  totalHeight = listItem.getMeasuredHeight();



        for(int i = 0;i<items;i++){
            View listItem = gridViewAdapter.getView(0, null, gridView);
            listItem.measure(0, 0);
           totalHeight=totalHeight+ listItem.getMeasuredHeight();
        }

        if(items==1){

        }
       else totalHeight = totalHeight -(totalHeight/2);






    /*    if (items % 2 == 0)
            totalHeight = (totalHeight * (items / 2)) -(items*15) ;
        else if (items == 1) totalHeight = (totalHeight * items) - 45;
        else {
            int items2 = items - 1;
            totalHeight = (totalHeight * items2 / 2) - (45 * items2);
            totalHeight = totalHeight + 380;
        }*/
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void datePicker(TextView tv_date, int type) {

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                (view, year, monthOfYear, dayOfMonth) -> {

                    monthOfYear = monthOfYear + 1;

                    String m = String.valueOf(monthOfYear);
                    String d = String.valueOf(dayOfMonth);

                    if (monthOfYear < 10) {

                        m = ("0" + (monthOfYear));
                    }
                    if (dayOfMonth < 10) {

                        d = ("0" + (dayOfMonth));
                    }

                    tv_date.setText(d + "-" + (m) + "-" + year);
                    if (type == 1) {
                        start_date = year + "-" + (m) + "-" + d;
                    } else {
                        end_date = year + "-" + (m) + "-" + d;
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

}
