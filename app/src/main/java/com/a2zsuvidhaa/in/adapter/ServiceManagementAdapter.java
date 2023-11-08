package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.a2zsuvidhaa.in.activity.AppInProgressActivity;
import com.a2zsuvidhaa.in.model.ServiceManagement;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.RequestHandler;
import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.util.APIs;
import com.a2zsuvidhaa.in.util.MakeToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServiceManagementAdapter extends RecyclerView.Adapter<ServiceManagementAdapter.ViewHolder> {

    private ArrayList<ServiceManagement> list;
    private Context context;

    public ServiceManagementAdapter(ArrayList<ServiceManagement> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_services,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ServiceManagement serviceManagement = list.get(i);
        viewHolder.tv_id.setText(serviceManagement.getId());
        viewHolder.tv_name.setText(serviceManagement.getName());
        viewHolder.tv_category.setText(serviceManagement.getCategory());
        viewHolder.tv_message.setText(serviceManagement.getMessage());

        if(serviceManagement.getStatus().equalsIgnoreCase("1")) {
            viewHolder.check_status.setChecked(true);
            viewHolder.tv_status_message.setText("Service Up");
            viewHolder.tv_status_message.setTextColor(context.getResources().getColor(R.color.success));
        }
        else {
            viewHolder.check_status.setChecked(false);
            viewHolder.tv_status_message.setText("Service Down");
            viewHolder.tv_status_message.setTextColor(context.getResources().getColor(R.color.red));
        }

        viewHolder.check_status.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
               updateService(list.get(viewHolder.getAdapterPosition()).getId(),"1", viewHolder);
            }else{
                updateService(list.get(viewHolder.getAdapterPosition()).getId(),"0",viewHolder);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id;
        TextView tv_name;
        TextView tv_category;
        TextView tv_message;
        TextView tv_status_message;
        CheckBox check_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_id = itemView.findViewById(R.id.tv_id);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_status_message = itemView.findViewById(R.id.tv_status_message);
            check_status = itemView.findViewById(R.id.check_status);


        }
    }

    private void updateService(String id, String value, ViewHolder viewHolder) {

        final StringRequest request = new StringRequest(Request.Method.POST,
                APIs.UPDATE_SERVICE_MANAGEMENT,
                response -> {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("1")) {

                            if(value.equalsIgnoreCase("0")){
                                viewHolder.tv_status_message.setText("Service Down");
                                viewHolder.tv_status_message.setTextColor(context.getResources().getColor(R.color.red));


                            }else {


                                viewHolder.tv_status_message.setText("Service Up");
                                viewHolder.tv_status_message.setTextColor(context.getResources().getColor(R.color.success));

                            }



                            MakeToast.show(context,message);


                        } else if (status.equalsIgnoreCase("200")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);
                        } else if (status.equalsIgnoreCase("300")) {
                            Intent intent = new Intent(context, AppInProgressActivity.class);
                            intent.putExtra("message", message);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        }
                        else{
                            MakeToast.show(context,message);
                        }


                    } catch (JSONException e) {


                        MakeToast.show(context, e.getMessage());

                    }



                },
                error -> {

                    MakeToast.show(context, "onError");
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> param = new HashMap<>();

                param.put("userId", String.valueOf(AppPreference.getInstance(context).getId()));
                param.put("token", String.valueOf(AppPreference.getInstance(context).getToken()));
                param.put("id",id);
                param.put("status_id",value);
                return param;
            }

        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
