package com.a2zsuvidhaa.in.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.a2zsuvidhaa.in.AppPreference;
import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.activity.AdhaarPay.AdhaarActivity;
import com.a2zsuvidhaa.in.adapter.TCAdapter;
import com.a2zsuvidhaa.in.adapter.TagsAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class AddMoneyActivity extends AppCompatActivity {

    ArrayList<String> tags;
    TagsAdapter tagsAdapter;
    GridView tags_gv;

    ArrayList<String> text;
    TCAdapter textAdapter;
    ListView text_lv;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Money");
        getSupportActionBar().setSubtitle("Balance - Rs. "+ AppPreference.getInstance(this).getUserBalance());

        tags_gv=findViewById(R.id.grid);
        text_lv=findViewById(R.id.list);
        start=findViewById(R.id.btn_start);

        start.setOnClickListener(view -> {


            Intent intent;



            if(MainActivity.spos==0)
            {
                intent = new Intent(AddMoneyActivity.this, AdhaarActivity.class);
            }
            else {
                intent = new Intent(AddMoneyActivity.this, PaymentGatewayActivity.class);
            }
            startActivity(intent);
        });

        tags=new ArrayList<>();
        text=new ArrayList<>();

        tags.add("Adhaar Pay");
        tags.add("Payment Gateway");

        tagsAdapter=new TagsAdapter(AddMoneyActivity.this,tags);
        tags_gv.setAdapter(tagsAdapter);

        text=new ArrayList<>();

        text.add("* Maximum topup of ₹10,000 is allowed in a single transaction.");
        text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account. ");

        textAdapter=new TCAdapter(AddMoneyActivity.this,text);
        text_lv.setAdapter(textAdapter);

        tags_gv.setSelection(0);
        tags_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.spos=i;
                tagsAdapter.notifyDataSetChanged();
                text=new ArrayList<>();
                if(i==0)
                {
                    text.add("* Maximum topup of ₹10,000 is allowed in a single transaction.");
                    text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account. ");
                }
                else {
                    text.add("* Topup start Minimum ₹100.");
                    text.add("* Pending/Timeout transactions may take upto 2 working days to reflect in your account.");
                }
                textAdapter=new TCAdapter(AddMoneyActivity.this,text);
                text_lv.setAdapter(textAdapter);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }
}
