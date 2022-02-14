package com.example.uscfilms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import util.common;

public class ReviewActivity extends AppCompatActivity {

    private String rate;
    private String name_date;
    private String content;

    private TextView tv_rate;
    private TextView tv_name_date;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        new common().translucentStatusBar(ReviewActivity.this,true);

        Bundle bundle = getIntent().getExtras();

        rate = bundle.getString("Rate");
        name_date = bundle.getString("Name_date");
        content = bundle.getString("Content");

        tv_rate = findViewById(R.id.activity_review_rating);
        tv_name_date = findViewById(R.id.activity_review_name_date);
        tv_content = findViewById(R.id.activity_review_content);

        tv_rate.setText(rate);
        tv_name_date.setText(name_date);
        tv_content.setText(content);

    }
}