package com.a2zsuvidhaa.in.navigation_drawer;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;


public class TitleViewHolder extends GroupViewHolder {
    private TextView titleView;
    private String titleString;
    private ImageView arrow;
    private ImageView icon;
    NavMenuAdapter adapter;

    public TitleViewHolder(View itemView, NavMenuAdapter adapter) {
        super(itemView);
        icon = itemView.findViewById(R.id.nav_menu_item_icon);
        titleView = itemView.findViewById(R.id.nav_menu_item_title);
        arrow = itemView.findViewById(R.id.nav_menu_item_arrow);
        this.adapter = adapter;
    }

    public void setGenreTitle(Context context, ExpandableGroup title) {
        if (title instanceof TitleMenu) {
            if (((TitleMenu) title).getImageResource() != 0){

                Picasso.get()
                        .load(((TitleMenu) title).getImageResource())
                        .resize(50, 50)
                        .centerCrop()
                        .placeholder(R.drawable.icon_no_image)
                        .error(R.drawable.icon_no_image)
                        .into(icon);

            }

            titleString = title.getTitle();
            titleView.setText(title.getTitle());

            if (title.getItems().size() > 0){
                arrow.setVisibility(View.VISIBLE);

                boolean isExpand = false;
                for (int i = 0; i < adapter.isExpandList.size(); i++) {
                    if (titleString.equals(adapter.isExpandList.get(i))){
                        isExpand = true;
                        break;
                    }
                }
                if (isExpand) {
                    arrow.setImageResource(R.drawable.v_arrow_up);
                } else {
                    arrow.setImageResource(R.drawable.v_arrow_down);
                }
            }else{
                arrow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void expand() {
        adapter.isExpandList.add(titleString);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void collapse() {
        adapter.isExpandList.remove(titleString);
        adapter.notifyDataSetChanged();
    }
}
