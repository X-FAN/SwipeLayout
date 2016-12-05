package com.xf.swipelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xf.swipelayout.Adapter.SwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.test_show)
    RecyclerView mTestShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTestShow.setLayoutManager(new LinearLayoutManager(this));
        List<String> src = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            src.add("TEST**" + i);
        }
        mTestShow.setAdapter(new SwipeAdapter(src));

    }
}
