package com.max.hydro_flow_supplier;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class order_dataAdapter extends RecyclerView.Adapter<order_dataViewHolder> {
    private Context mctx;
    private List<order_data> order_dataList;
    private int lastPosition = -1;
    Dialog dialog;

    public order_dataAdapter(Context mctx, List<order_data> order_dataList) {
        this.mctx = mctx;
        this.order_dataList = new ArrayList<>();
        this.order_dataList = order_dataList;
        dialog = new Dialog(mctx);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    @NonNull
    @Override
    public order_dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.orderd_cardview_layout, null);
        return new order_dataViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final order_dataViewHolder holder,  int position) {
        final customer_data[] customerData = new customer_data[1];
        final order_data orderData = order_dataList.get(position);
        switch (orderData.getStatus()) {
            case "shipped":
                holder.check_btn.setVisibility(View.GONE);
                holder.view_invoice_btn.setVisibility(View.VISIBLE);
                holder.pay_btn.setVisibility(View.VISIBLE);
                holder.hold_btn.setVisibility(View.VISIBLE);
                holder.return_btn.setVisibility(View.VISIBLE);
                break;
            case "accepted":
                holder.check_btn.setVisibility(View.VISIBLE);
                holder.done.setVisibility(View.GONE);
                break;
            case "delivered":
                holder.done.setVisibility(View.VISIBLE);
                holder.view_invoice_btn.setVisibility(View.GONE);
                holder.pay_btn.setVisibility(View.GONE);
                holder.hold_btn.setVisibility(View.GONE);
                holder.return_btn.setVisibility(View.GONE);
                holder.check_btn.setVisibility(View.GONE);
                break;
        }
        holder.items.setHasFixedSize(true);
        holder.items.setLayoutManager(new LinearLayoutManager(mctx));
        final DatabaseReference reference_order = FirebaseDatabase.getInstance().getReference("orders").child(orderData.getOrderID());

        reference_order.child("orderItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<orderitem_data> cart_dataList=new ArrayList<>();
                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    orderitem_data cartData = itemSnapshot.getValue(orderitem_data.class);
                    cart_dataList.add(cartData);
                }
                orderItem_dataAdapter adapter = new orderItem_dataAdapter(mctx, cart_dataList);
                holder.items.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




        Query reference = FirebaseDatabase.getInstance().getReference("customers").orderByChild("customerID").equalTo(orderData.getCustomerID());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> childrens = dataSnapshot.getChildren();
                for (DataSnapshot itemSnapshot : childrens) {
                    customerData[0] = itemSnapshot.getValue(customer_data.class);
                }
                assert customerData[0] != null;
                holder.customerName.setText(customerData[0].getName());
                holder.address.setText(customerData[0].getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.orderID.setText(String.format("orderID:%s", orderData.getOrderID()));
        holder.check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mctx);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                dialog.setCanceledOnTouchOutside(false);
                TextView title, message;
                Button yes, no;
                title = dialog.findViewById(R.id.alert_title);
                message = dialog.findViewById(R.id.alert_message);
                yes = dialog.findViewById(R.id.yes_btn);
                no = dialog.findViewById(R.id.no_btn);
                dialog.setCancelable(true);
                title.setText("ARE U SURE?");
                message.setText("if ordered items is loaded to truck then press yes");
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> hashMap = new HashMap();
                        hashMap.put("status", "shipped");
                        reference_order.updateChildren(hashMap);
                        dialog.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        holder.pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mctx, payment_confirm.class);
                Bundle extra = new Bundle();
                extra.putString("orderID", orderData.getOrderID());
                extra.putString("customerID",customerData[0].getCustomerID());
                extra.putString("customerName", String.valueOf(holder.customerName.getText()));
                extra.putString("Gtotal", orderData.getGrandTotal());
                extra.putString("balance",customerData[0].getBalance());
                intent.putExtras(extra);
                mctx.startActivity(intent);
            }
        });
        holder.view_invoice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                String filename = String.format("%s.pdf", orderData.getOrderID());
                final File pdfFile = new File(String.format("%s/docs/%s", mctx.getExternalFilesDir(""), filename));
                if (!pdfFile.exists()) {
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference().child("invoices").child(String.format("%s.pdf", orderData.getOrderID()));

                    String extStorageDirectory = mctx.getExternalFilesDir("").getPath();
                    File rootPath = new File(extStorageDirectory, "docs");
                    if (!rootPath.exists()) {
                        rootPath.mkdirs();
                    }

                    final File localFile = new File(rootPath, filename);

                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Uri path = FileProvider.getUriForFile(mctx, mctx.getPackageName()+".provider", pdfFile);
                            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                            pdfIntent.setDataAndType(path, "application/pdf");
                            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            dialog.dismiss();
                            mctx.startActivity(pdfIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                            dialog.dismiss();
                        }
                    });
                } else {
                    Uri path = FileProvider.getUriForFile(mctx, mctx.getPackageName()+".provider", pdfFile);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    dialog.dismiss();
                    mctx.startActivity(pdfIntent);

                }
            }
        });
        holder.hold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(mctx, android.R.style.Theme_Material_Light_Dialog);
                datePickerDialog.setTitle("Select Next deliver Date");
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date;
                        if (month < 10)
                            date = dayOfMonth + "-0" + month + "-" + year;
                        else
                            date = dayOfMonth + "-" + month + "-" + year;
                        String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            Date date2 = format.parse(date);
                            Date date3 = format.parse(date1);
                            if (date2.compareTo(date3) < 0) {
                                Toast.makeText(mctx, "Select Future", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.show();
                                HashMap<String, Object> hashMap = new HashMap();
                                hashMap.put("assigned_area", String.format("%s_%s", date, customerData[0].getAreaName()));
                                hashMap.put("status", String.format("hold_%s", customerData[0].getAreaName()));
                                reference_order.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                datePickerDialog.show();


            }
        });
        holder.return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mctx);
                dialog.setContentView(R.layout.alert_dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                dialog.setCanceledOnTouchOutside(false);
                TextView title, message;
                Button yes, no;
                title = dialog.findViewById(R.id.alert_title);
                message = dialog.findViewById(R.id.alert_message);
                yes = dialog.findViewById(R.id.yes_btn);
                no = dialog.findViewById(R.id.no_btn);
                dialog.setCancelable(true);
                title.setText("ARE U SURE?");
                message.setText("The Order Will be Cancelled");
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> hashMap = new HashMap();
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        hashMap.put("status", "cancelled");
                        hashMap.put("assigned_area", String.format("return_%s_%s", date, customerData[0].getAreaName()));
                        reference_order.updateChildren(hashMap);
                        dialog.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        holder.show_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.items.getVisibility()==View.GONE)
                    holder.items.setVisibility(View.VISIBLE);
                else if(holder.items.getVisibility()==View.VISIBLE)
                    holder.items.setVisibility(View.GONE);
            }
        });
       // setAnimaton(holder.itemView, position);
    }

    private void setAnimaton(View view, int position) {
        if (position > lastPosition) {
            Animation fadout = new AlphaAnimation(1, 0);
            fadout.setStartOffset(1500);
            fadout.setDuration(1500);
            Animation fadein = new AlphaAnimation(0, 1);
            fadein.setDuration(1500);
            AnimationSet animation = new AnimationSet(true);
            animation.addAnimation(fadein);
            animation.addAnimation(fadout);
            view.setAnimation(fadein);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return order_dataList.size();
    }


}

class order_dataViewHolder extends RecyclerView.ViewHolder {

    TextView customerName, orderID, address;
    Button check_btn, view_invoice_btn, pay_btn, hold_btn, return_btn,show_items;
    ImageView done;
    RecyclerView items;

    order_dataViewHolder(View itemView) {
        super(itemView);
        customerName = itemView.findViewById(R.id.cust_name);
        orderID = itemView.findViewById(R.id.orderid);
        address = itemView.findViewById(R.id.address);
        check_btn = itemView.findViewById(R.id.check_btn);
        view_invoice_btn = itemView.findViewById(R.id.viewinvoice_btn);
        pay_btn = itemView.findViewById(R.id.payment_btn);
        hold_btn = itemView.findViewById(R.id.hold_btn);
        return_btn = itemView.findViewById(R.id.return_btn);
        done = itemView.findViewById(R.id.done);
        items=itemView.findViewById(R.id.item_list);
        show_items=itemView.findViewById(R.id.show_items);
    }
}


