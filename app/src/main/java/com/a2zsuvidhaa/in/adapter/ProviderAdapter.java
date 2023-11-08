package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2zsuvidhaa.in.listener.ProviderCallbackListener;
import com.a2zsuvidhaa.in.model.Provider;
import com.a2zsuvidhaa.in.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Provider> providerList;

    public ProviderAdapter(Context context, ArrayList<Provider> providerList) {
        this.context = context;
        this.providerList = providerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_provider, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Provider provider = providerList.get(i);

        viewHolder.tv_providerName.setText(provider.getProviderName());

        try {
            Picasso.get()
                    .load(provider.getProviderImage())
                    .resize(50, 50)
                    .centerCrop()
                    .placeholder(provider.getDefaultIcon())
                    .error(provider.getDefaultIcon())
                    .into(viewHolder.cv_providerImage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        viewHolder.main_layout.setOnClickListener(view ->
                listener.onProviderCallback(providerList.get(viewHolder.getAdapterPosition())));

    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cv_providerImage;
        TextView tv_providerName;
        LinearLayout main_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_providerImage = itemView.findViewById(R.id.cv_providerImage);
            tv_providerName = itemView.findViewById(R.id.tv_providerName);
            main_layout = itemView.findViewById(R.id.ll_main_layout);

        }
    }

    private ProviderCallbackListener listener;

    public void setupProviderCallback(ProviderCallbackListener listener) {
        this.listener = listener;
    }

    public void filterList(ArrayList<Provider> filteredList) {
        providerList = filteredList;
        notifyDataSetChanged();
    }
}
