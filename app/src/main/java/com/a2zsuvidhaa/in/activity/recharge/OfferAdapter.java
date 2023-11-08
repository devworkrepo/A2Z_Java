package com.a2zsuvidhaa.in.activity.recharge;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.listener.OfferCallbackListener;
import com.a2zsuvidhaa.in.model.Offer;

import java.util.ArrayList;


public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Offer> list;

    public OfferAdapter(ArrayList<Offer> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_offer,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Offer offer = list.get(i);
        viewHolder.tv_price.setText("₹"+offer.getPrice());
        viewHolder.tv_offer.setText(offer.getOfferDesc());

        viewHolder.ll_main_layout.setOnClickListener(view -> {
            listener.onOfferCallback(offer);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_main_layout;
        private TextView tv_price;
        private TextView tv_offer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_main_layout = itemView.findViewById(R.id.ll_main_layout);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_offer = itemView.findViewById(R.id.tv_offer);
        }
    }
    private OfferCallbackListener listener;
    public  void setupOfferCallbackListener(OfferCallbackListener listener){
        this.listener=listener;
    }
}
