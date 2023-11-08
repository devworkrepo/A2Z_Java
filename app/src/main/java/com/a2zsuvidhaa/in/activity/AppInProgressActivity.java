package com.a2zsuvidhaa.in.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.util.AutoLogoutManager;

public class AppInProgressActivity extends AppCompatActivity {
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_in_progress);


        String message = getIntent().getStringExtra("message");
        type = getIntent().getIntExtra("type", 0);

        TextView tv_app_in_progressTextView = findViewById(R.id.tv_app_in_progress);
        TextView tv_app_in_progressTextView2 = findViewById(R.id.tv_app_in_progress2);
        ImageView iv_type = findViewById(R.id.iv_type);



        if(type==1){
            iv_type.setImageDrawable(getResources().getDrawable(R.drawable.warning));
            if (!message.isEmpty())
            tv_app_in_progressTextView.setText("Your app token is missmatched!\nmay be you have logged in another device");
        }
        else if (type == 2) {
            tv_app_in_progressTextView2.setVisibility(View.VISIBLE);
            iv_type.setImageDrawable(getResources().getDrawable(R.drawable.danger_icon));
            tv_app_in_progressTextView.setText("A2Z Suvidhaa App can't be used on this device");
        }
        else {
            if (!message.isEmpty()) tv_app_in_progressTextView.setText(message);
        }


    }

    @Override
    public void onBackPressed() {
        if (type == 2) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else AutoLogoutManager.logout(this);

    }
}
