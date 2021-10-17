package com.shokaku.hayate.fakemcddaily;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shokaku.hayate.fakemcddaily.Activity.CouponDetailActivity;
import com.shokaku.hayate.fakemcddaily.Activity.MainActivity;
import com.shokaku.hayate.fakemcddaily.Instance.Coupon;

import java.util.Calendar;


public class CouponListViewAdapter extends RecyclerView.Adapter<CouponListViewAdapter.ViewHolder> {
  
  private Context context;
  
  Coupon[] coupons;
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    ImageView imgCover, imgMask;
    TextView txtStatus, txtCountDown;
    Button btnRedeem, btnHadRedeemed, btnExpired;
    LinearLayout layCountDown;
    
    public ViewHolder(View v) {
      super(v);
      imgCover =  v.findViewById(R.id.cover);
      imgMask =  v.findViewById(R.id.mask);
      txtStatus =  v.findViewById(R.id.status);
      btnRedeem =  v.findViewById(R.id.btnRedeem_list_item);
      txtCountDown =  v.findViewById(R.id.redeem_timer);
      btnHadRedeemed =  v.findViewById(R.id.had_redeemed);
      btnExpired =  v.findViewById(R.id.expired);
      layCountDown =  v.findViewById(R.id.layout_countDown);
    }
  }
  
  //建構函數
  public CouponListViewAdapter(Coupon[] coupons, Context context) {
    
    this.context = context;
    this.coupons = coupons;
  }
  
  @Override
  public CouponListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_item_coupon, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
  
    final Coupon coupon = MainActivity.coupons[position];
    final int couponId = coupon.couponId;

    String couponName = MainActivity.loaclCouponNames.get(couponId);
    Bitmap couponCover = MainActivity.imgBitmaps.get(couponId);//sqliteHandler.getImage(couponName);
    holder.imgCover.setImageBitmap(couponCover);
    
    View.OnClickListener onClickListener = new View.OnClickListener() {
    
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, CouponDetailActivity.class);
        Bundle b = new Bundle();
        b.putInt("couponId", couponId);
        b.putInt("couponPosit", position);
        intent.putExtras(b);
        context.startActivity(intent);
      }
    };
    
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH) + 1;
    int day = c.get(Calendar.DAY_OF_MONTH);
    int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
    int dayLeft = 2 - position, hourLeft = 24 - hourOfDay;
    
    String status = "期限 : %d 年 %d 月 %d 日 尚餘 : %d 天 %d 小時";
    
    if (coupon.isCounting) {
  
      setViewVisibility(1, holder);
      
      holder.txtCountDown.setText(coupon.getFormatedTime());
      holder.txtCountDown.setOnClickListener(onClickListener);
      holder.layCountDown.setOnClickListener(onClickListener);
      
    } else if (coupon.isExpired) {
  
      setViewVisibility(3, holder);
      
      c.add(Calendar.DAY_OF_MONTH, -(position - 2));
      status = String.format(status, year, month, day, 0, 0);
      holder.txtStatus.setText(status);
      holder.imgMask.setOnClickListener(null);
      
    } else if (coupon.hadRedeemed) {
  
      setViewVisibility(2, holder);
      
      status = String.format(status, year, month, day + 2 - position, 0, 0);
      holder.txtStatus.setText(status);
      holder.imgMask.setOnClickListener(null);
      
    } else {
  
      setViewVisibility(0, holder);
      
      status = String.format(status, year, month, day + 2 - position, dayLeft, hourLeft);
      holder.txtStatus.setText(status);
      holder.imgCover.setOnClickListener(onClickListener);
      holder.btnRedeem.setOnClickListener(onClickListener);
    }
  }
  
  private void setViewVisibility(int type, ViewHolder holder) {
    
    switch (type) {
  
      case 0: //優惠券
    
        holder.imgMask.setVisibility(View.GONE);
        holder.txtCountDown.setVisibility(View.GONE);
        holder.btnRedeem.setVisibility(View.VISIBLE);
        holder.btnExpired.setVisibility(View.GONE);
        holder.btnHadRedeemed.setVisibility(View.GONE);
        break;
      case 1: //倒數
        
        holder.imgMask.setVisibility(View.VISIBLE);
        holder.txtCountDown.setVisibility(View.VISIBLE);
        holder.btnRedeem.setVisibility(View.GONE);
        holder.btnExpired.setVisibility(View.GONE);
        holder.btnHadRedeemed.setVisibility(View.GONE);
        break;
      case 2: //已兌換
        
        holder.imgMask.setVisibility(View.VISIBLE);
        holder.txtCountDown.setVisibility(View.GONE);
        holder.btnRedeem.setVisibility(View.GONE);
        holder.btnExpired.setVisibility(View.GONE);
        holder.btnHadRedeemed.setVisibility(View.VISIBLE);
        break;
      case 3: //已截止
  
        holder.imgMask.setVisibility(View.VISIBLE);
        holder.txtCountDown.setVisibility(View.GONE);
        holder.btnRedeem.setVisibility(View.GONE);
        holder.btnExpired.setVisibility(View.VISIBLE);
        holder.btnHadRedeemed.setVisibility(View.GONE);
        break;
    }
  }
  
  @Override
  public int getItemCount() {
    return MainActivity.coupons.length;
  }
  
}