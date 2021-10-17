package com.shokaku.hayate.fakemcddaily.Instance;

import android.os.CountDownTimer;

import com.shokaku.hayate.fakemcddaily.Activity.MainActivity;

/**
 * Created by Shokaku on 2018/1/23/023.
 */

public class Coupon {
  
  public int couponId;
  public boolean isExpired, isCounting = false, hadRedeemed = false;
  public int secCountDown;
  private String formatedTime;
  
  
  public Coupon(int couponId, boolean isExpired) {
    
    this.couponId = couponId;
    this.isExpired = isExpired;
    secCountDown = (isExpired) ? 0 : 120;
  }
  
  public void setSecCountDown(int secCountDown) {
    
    if (secCountDown == 0) {
      hadRedeemed = true;
      isCounting = false;
    }
    this.formatedTime = secToMin(secCountDown);
    this.secCountDown = secCountDown;
  }
  
  public String getFormatedTime() {
    
    return this.formatedTime;
  }
  
  public void startCountDown() {
    
    isCounting = true;
  
    new CountDownTimer(secCountDown * 1000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        
        MainActivity.mAdapter.notifyDataSetChanged();
        int sec = (int) millisUntilFinished/1000;
        setSecCountDown(sec);
      }
      
      @Override
      public void onFinish() {
        
        isCounting = false;
        hadRedeemed = true;
        MainActivity.mAdapter.notifyDataSetChanged();
      }
    }.start();
  }
  
  private String secToMin(int sec) {
    
    return String.format("%02d : %02d", sec/60, sec%60);
  }
}
