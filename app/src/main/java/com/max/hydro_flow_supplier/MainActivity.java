package com.max.hydro_flow_supplier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CardView delivery, hold, req_return;
    TextView del_count, pay_count, username,hold_count,return_count;
    ProgressBar del_boy_pbar, pay_pbar,hold_bar,return_bar;
    CircleImageView userpic;
    AlphaAnimation buttonClick;
    Button signout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main);
        del_count = findViewById(R.id.Delivery_count_txt);
        pay_count = findViewById(R.id.payments_count_txt);
        hold_count=findViewById(R.id.hold_count_txt);
        return_count=findViewById(R.id.return_count_txt);
        del_boy_pbar = findViewById(R.id.del_count_pbar);
        pay_pbar = findViewById(R.id.payment_count_pbar);
        hold_bar = findViewById(R.id.hold_count_pbar);
        return_bar = findViewById(R.id.return_count_pbar);
        signout=findViewById(R.id.signout);
        buttonClick = new AlphaAnimation(0.5F, 1F);
        buttonClick.setDuration(500);
        userpic = findViewById(R.id.userPic);
        username = findViewById(R.id.username);
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        String id = preferences.getString("delboyID", null);
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
                    Glide.with(MainActivity.this).load(userData.getPicurl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(userpic);
                    username.setText(userData.getName());
                }
                Query ref=FirebaseDatabase.getInstance().getReference("orders").orderByChild("status").equalTo(String.format("hold_%s",areaName));
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hold_count.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        hold_bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Query returned=FirebaseDatabase.getInstance().getReference("orders").orderByChild("assigned_area").equalTo(String.format("return_%s_%s",date,areaName));
                returned.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        return_count.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        return_bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Query reference2 = FirebaseDatabase.getInstance().getReference("orders").orderByChild("assigned_area").equalTo(String.format("%s_%s", date, areaName));
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final int[] paymenttot = {0};
                        final int[] recived = {0};
                        Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                        for (DataSnapshot itemSnapshot : childrens) {
                            final order_data orderData = itemSnapshot.getValue(order_data.class);
                            assert orderData != null;
                            paymenttot[0] += Integer.parseInt(orderData.getGrandTotal());
                            if (orderData.getStatus().equals("delivered")) {
                                recived[0] += Integer.parseInt(orderData.getGrandTotal());
                            }
                        }
                        pay_count.setText(String.format("%s/%s", recived[0], paymenttot[0]));
                        del_count.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        del_boy_pbar.setVisibility(View.GONE);
                        pay_pbar.setVisibility(View.GONE);
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

        delivery = findViewById(R.id.cv_delivery);
        hold = findViewById(R.id.cv_hold);
        req_return = findViewById(R.id.cv_return);
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delivery.startAnimation(buttonClick);
                startActivity(new Intent(getApplicationContext(), orders.class));

            }
        });
        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delivery.startAnimation(buttonClick);
                startActivity(new Intent(getApplicationContext(), orders.class));

            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "u clicked", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(),login.class));

            }
        });
    }
}
