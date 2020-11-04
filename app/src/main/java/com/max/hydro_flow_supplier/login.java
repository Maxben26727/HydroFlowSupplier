package com.max.hydro_flow_supplier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ImageButton sign_in_btn=findViewById(R.id.sign_in_btn);
        final ProgressBar progressBar=findViewById(R.id.progressBar2);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final EditText ed_email = findViewById(R.id.ed_email);
        final EditText ed_password = findViewById(R.id.ed_pass);
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();




        progressBar.setVisibility(View.GONE);
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String[] str_email = {ed_email.getText().toString().trim()};
                final String[] str_password = {ed_password.getText().toString().trim()};
                if (str_email[0].isEmpty() || str_password[0].isEmpty()) {
                    Toast.makeText(login.this, "Input Your Auth info", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Query reference = FirebaseDatabase.getInstance().getReference("deliveryboys").orderByChild("email").equalTo(str_email[0]);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                            String  delboyID = null;
                            for (DataSnapshot itemSnapshot : childrens) {
                                user_data userData = itemSnapshot.getValue(user_data.class);
                                assert userData != null;
                                delboyID=userData.getDelboyID();
                            }
                            final String finalDelboyID = delboyID;
                            if(!dataSnapshot.exists())
                            {
                                Toast.makeText(login.this, "Unknown User!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }else{
                            mAuth.signInWithEmailAndPassword(str_email[0], str_password[0])
                                    .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                editor.putString("delboyID", finalDelboyID);
                                                editor.apply();
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();

                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(login.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(login.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        });
    }
}
