package com.a2zsuvidhaa.in.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.model.UserModel;
import com.a2zsuvidhaa.in.util.SessionManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class DocumentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<UserModel> memberList;
    private ItemClickListener mClickListener;
    SessionManager sessionManager;
    public DocumentsAdapter(Context context, List<UserModel> memberList, ItemClickListener mClickListener) {
        this.context = context;
        this.memberList = memberList;
        this.mClickListener=mClickListener;
        sessionManager=new SessionManager(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);
      //  if (viewType == 0) {
            View memberView = inflater.inflate(R.layout.member_doc_view, viewGroup, false);
            viewHolder = new MemberView(memberView);
      ///  }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof MemberView){
            UserModel member = memberList.get(i);
            //Log.e("adapter",""+Server.IMG_URL+member.getImg());
           /* Picasso.get().load(Server.IMG_URL+member.getImg())
                    .placeholder(R.drawable.add_pic)
                    .into(((MemberView) viewHolder).ivProfile)
                    ;*/

            ((MemberView) viewHolder).tvName.setText(member.getName());


        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;//memberList.get(position)==null?VIEW_TYPE_ADD_MEMBER:VIEW_TYPE_MEMBER;
    }

    public class MemberView extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivCancel;
        ImageView ivProfile;
        TextView tvName,tvRelation,tvYou;
        public MemberView(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_pic);
            tvName = itemView.findViewById(R.id.tv_name);
            ivProfile.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            final int pos = getAdapterPosition();
            if(view.getId()==R.id.iv_pic){
            mClickListener.onAddItemClick(ivProfile,pos,ivProfile);
            }
        }
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public interface ItemClickListener {
        void onItemClick(View view, CircleImageView cim, int position, UserModel userModel);
        void onAddItemClick(ImageView cim, int pos, ImageView close);
    }



    public interface SetRelationListener {
        void onSet(String id, int rel);

    }
}
