package com.shokaku.hayate.fakemcddaily.Interface;

public interface OnCouponSyncListener {
  
  /**
   * Called when coupons need to update.
   *
   * @param versionCode latest coupon vesion.
   */
  void onCouponUpdate(int versionCode);
  
  /**
   * Called when coupons are already latest version.
   *
   * @param versionCode latest coupon vesion.
   */
  void onLatest(int versionCode);
}
