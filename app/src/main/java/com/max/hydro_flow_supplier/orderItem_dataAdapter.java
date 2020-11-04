package com.max.hydro_flow_supplier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class orderItem_dataAdapter extends RecyclerView.Adapter<orderItem_dataViewHolder> {
    private Context mctx;
    private int lastPosition = -1;
    private List<orderitem_data> orderItemList;

    public orderItem_dataAdapter(Context mctx, List<orderitem_data> orderItemList) {
        this.mctx = mctx;
        this.orderItemList = orderItemList;
    }


    @NonNull
    @Override
    public orderItem_dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.orderitem_item_layout, null);
        return new orderItem_dataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final orderItem_dataViewHolder holder, int position) {
        final orderitem_data cartdata = orderItemList.get(position);
        Glide.with(mctx).load(cartdata.getProductImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.productpic);
        holder.titletv.setText(cartdata.getProductName());
        holder.qty.setText(cartdata.getCartqty());
        holder.pricetv.setText(String.format("RS.%s", cartdata.getProductPrice()));
        holder.totalPrice.setText(String.format("GTotal:RS.%s/-", cartdata.getTotalPrice()));
        setAnimaton(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public void setAnimaton(View view, int position) {
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

        }
        lastPosition = position;
    }
}

class orderItem_dataViewHolder extends RecyclerView.ViewHolder {
    TextView titletv, pricetv, totalPrice,qty;
    ImageView productpic;


    orderItem_dataViewHolder(View itemView) {
        super(itemView);
        productpic = itemView.findViewById(R.id.cart_product_iv);
        titletv = itemView.findViewById(R.id.cart_product_name);
        pricetv = itemView.findViewById(R.id.cart_product_price);
        totalPrice = itemView.findViewById(R.id.cart_product_total);
        qty=itemView.findViewById(R.id.qty);
    }
}