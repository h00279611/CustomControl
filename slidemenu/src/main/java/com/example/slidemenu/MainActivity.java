package com.example.slidemenu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    private List<MyBean> mMyBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.lv_main);
        mMyBeans = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mMyBeans.add(new MyBean("content" + i));
        }

        mListView.setAdapter(new MyAdapter(this, mMyBeans));


    }
}
