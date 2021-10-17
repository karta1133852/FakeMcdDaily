package com.shokaku.hayate.fakemcddaily.Instance;

/**
 * Created by Shokaku on 2018/1/28/028.
 */

public class VersionInstance {
  
  public String versionName;
  public int versionCode;
  
  public VersionInstance() {}
  
  public VersionInstance(String versionName, int versionCode) {
    
    this.versionName = versionName;
    this.versionCode = versionCode;
  }
  
  /*public void setLocalCouponVersion(String localCouponsVersionName, int localCouponsVersionCode) {
  
    this.localCouponsVersionCode = localCouponsVersionCode;
    this.localCouponsVersionName = localCouponsVersionName;
  }
  
  public void setSyncCouponVersion(String syncCouponsVersionName, int syncCouponsVersionCode) {
    
    this.syncCouponsVersionCode = syncCouponsVersionCode;
    this.syncCouponsVersionName = syncCouponsVersionName;
  }*/
  
}
