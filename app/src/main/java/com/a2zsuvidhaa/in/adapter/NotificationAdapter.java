package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.model.NotificationModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by server3 on 4/5/2017.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>  {

    Context context;
    ArrayList<NotificationModel> notificationModelArrayList=new ArrayList<>();
    TextView notificationMsg;
    ImageView play;







    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationModelArrayList)
    {
        this.context=context;
        this.notificationModelArrayList=notificationModelArrayList;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {


        public MyViewHolder(View view) {
            super(view);
            //view.setOnClickListener(this);

            notificationMsg=view.findViewById(R.id.text);



        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        notificationMsg.setText(notificationModelArrayList.get(position).getNotificationMessage());

        //holder.value.setText(dummy.get(position).toString());




    }


    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();//dummy.size();
    }




}
