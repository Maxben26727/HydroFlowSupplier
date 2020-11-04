package com.max.hydro_flow_supplier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class payment_confirm extends AppCompatActivity {
        SignaturePad signaturePad;
        TextView orderID_tv,cname_tv,Gtotal_tv,bal_tv,pre_bal,total;
        Bundle extra;
        Button clear,confirm;
        ImageButton back;
        EditText rec_amount;
        int balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        signaturePad=findViewById(R.id.signature_pad);
        clear=findViewById(R.id.clear);
        orderID_tv=findViewById(R.id.order_id_value);
        cname_tv=findViewById(R.id.name_value);
        Gtotal_tv=findViewById(R.id.amount_value);
        bal_tv=findViewById(R.id.balance_value);
        rec_amount=findViewById(R.id.rec_amnt);
        confirm=findViewById(R.id.confirm_payment_id);
        pre_bal=findViewById(R.id.pre_value);
        total=findViewById(R.id.tot_value);
        back=findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       Intent i=getIntent();
       extra=i.getExtras();

        orderID_tv.setText(String.format("ORDERID:%s",extra.getString("orderID")));
       cname_tv.setText(extra.getString("customerName"));

           Gtotal_tv.setText(String.format("AMOUNT: Rs.%s",extra.getString("Gtotal")));
           pre_bal.setText(String.format("PENDING PAYMENT: Rs.%s",extra.getString("balance")));
           total.setText(String.format("TOTAL: Rs.%s", String.valueOf(Integer.parseInt(extra.getString("Gtotal")) + Integer.parseInt(extra.getString("balance")))));



        rec_amount.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
              if(s.length()>0) {
                  int a = Integer.parseInt(extra.getString("Gtotal"));
                  int b = Integer.parseInt(String.valueOf(s));
                  balance=Integer.parseInt(extra.getString("balance"));
                  balance = (balance+a) - b;
                  bal_tv.setText(String.format("BALANCE: Rs. %s", String.format(String.valueOf(balance))));
              }
            else
              {
                  bal_tv.setText("BALANCE: Rs.0");
              }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
clear.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        signaturePad.clear();
    }
});
confirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
Bitmap bitmap=signaturePad.getSignatureBitmap();
if(String.valueOf(rec_amount.getText()).matches(""))
{
    Toast.makeText(payment_confirm.this, "ENTER RECIVED AMOUNT", Toast.LENGTH_SHORT).show();
}
else if(signaturePad.isEmpty())
{
    Toast.makeText(payment_confirm.this, "Customer Sign Required!", Toast.LENGTH_SHORT).show();
}
else {
    final Dialog dialog = new Dialog(payment_confirm.this);
    dialog.setContentView(R.layout.loading_dialog);
    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(false);
    dialog.show();
    final DatabaseReference databaseReference_transaction= FirebaseDatabase.getInstance().getReference("transactions");
    final String id = databaseReference_transaction.push().getKey();
    final StorageReference storageReference = FirebaseStorage.getInstance()
            .getReference().child("pay_proof").child(id);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
    byte[] data = baos.toByteArray();
    storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
           storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {
                   String signurl=uri.toString();

                   String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                   transaction_data transactiondata=new transaction_data(id,extra.getString("customerName"),extra.getString("orderID"),extra.getString("customerID"),extra.getString("Gtotal"),String.valueOf(rec_amount.getText()).trim(),date,signurl);
                   databaseReference_transaction.child(id).setValue(transactiondata).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           dialog.dismiss();
                           DatabaseReference fr=FirebaseDatabase.getInstance().getReference("orders").child(Objects.requireNonNull(extra.getString("orderID")));
                           HashMap<String,Object> hashMap=new HashMap<>();
                           hashMap.put("status","delivered");
                           fr.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   DatabaseReference cref=FirebaseDatabase.getInstance().getReference("customers").child(extra.getString("customerID"));
                                   HashMap<String,Object> hashMap1=new HashMap<>();
                                   hashMap1.put("balance",String.valueOf(balance));
                                   cref.updateChildren(hashMap1);
                                   Toast.makeText(payment_confirm.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                               finish();
                               }
                           });
                       }
                   });

               }
           });
        }
    });

}
    }
});
    }
}
