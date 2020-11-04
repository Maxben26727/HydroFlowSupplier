package com.max.hydro_flow_supplier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class orders extends AppCompatActivity {
    private RecyclerView order_recyclerView;
    private List<order_data> order_dataList;
    order_dataAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ImageButton back=findViewById(R.id.back_btn);
        order_dataList = new ArrayList<>();
        order_recyclerView = findViewById(R.id.order_recyclerView);
        order_recyclerView.setHasFixedSize(true);
        order_recyclerView.setLayoutManager(new LinearLayoutManager(orders.this));
        swipeRefreshLayout = findViewById(R.id.order_refresh);
        SharedPreferences preferences=getSharedPreferences("session", MODE_PRIVATE);
        String id=preferences.getString("delboyID",null);
        Query reference = FirebaseDatabase.getInstance().getReference("deliveryboys").orderByChild("delboyID").equalTo(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                String areaName = null;
                for (DataSnapshot itemSnapshot : childrens) {
                    user_data userData = itemSnapshot.getValue(user_data.class);
                    assert userData != null;
                    areaName = userData.getAreaName();
                }
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Query reference2 = FirebaseDatabase.getInstance().getReference("orders").orderByChild("assigned_area").equalTo(String.format("%s_%s",date,areaName));
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        order_dataList.clear();
                        Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                        for (DataSnapshot itemSnapshot : childrens) {
                            final order_data orderData = itemSnapshot.getValue(order_data.class);
                            assert orderData != null;
                            order_dataList.add(orderData);
                        }
                        Collections.reverse(order_dataList);
                        adapter = new order_dataAdapter(orders.this, order_dataList);
                        order_recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
