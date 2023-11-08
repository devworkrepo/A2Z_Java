package com.a2zsuvidhaa.in.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.BuildConfig;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.activity.login.LoginActivity;
import com.a2zsuvidhaa.in.database.DBHelper;
import com.a2zsuvidhaa.in.firebase.FirebaseService;
import com.a2zsuvidhaa.in.util.AppLog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.installations.FirebaseInstallations;

public class SplashActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



      /*  TextView tv_slogan = findViewById(R.id.tv_slogan);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 1000.0f, 0.0f, 0.0f); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)

        animation.setDuration(7500); // animation duration, change accordingly
        animation.setRepeatCount(0); // animation repeat count
        animation.setFillAfter(false);
        tv_slogan .startAnimation(animation);//your_view for which you need animation*/
        Thread thread = new Thread(() -> {
            try {

                AppPreference.getInstance(SplashActivity.this).setToken("");
                AppPreference.getInstance(SplashActivity.this).setId(0);
                if (AppPreference.getInstance(SplashActivity.this).getAppStartCount() % 4 == 0) {
                    dbHelper = new DBHelper(this);
                    if (dbHelper.deleteData()) {} else {}
                    AppPreference.getInstance(SplashActivity.this).deleteAppStartCount();
                } else {
                    int appStartCount = AppPreference.getInstance(this).getAppStartCount() + 1;
                    AppPreference.getInstance(this).setAppStartCount(appStartCount);
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (dbHelper != null) {
                    dbHelper.close();
                    dbHelper = null;
                }
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("mobile_number", "");
                startActivity(intent);
                finish();
            }

        });
        thread.start();

    }
}
