package com.shokaku.hayate.fakemcddaily.Instance;

import java.util.ArrayList;

public class CouponUpdateList {
  
  private ArrayList<CouponDetail> couponDetails;
  // 1新增 -1移除 0沒改變
  private ArrayList<Integer> actions;
  
  //Update history list
  private ArrayList<Integer> versionCodes;
  private ArrayList<Integer> listActions;
  private ArrayList<String> couponNames;
  
  public CouponUpdateList() {
    
    couponDetails = new ArrayList<>();
    actions = new ArrayList<>();
    
    versionCodes = new ArrayList<>();
    listActions = new ArrayList<>();
    couponNames = new ArrayList<>();
  }
  
  public void addHistory(int version, int action, String name) {
    versionCodes.add(version);
    listActions.add(action);
    couponNames.add(name);
  }
  
  public ArrayList<String> getHistory(int version, int action) {
    
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < listActions.size(); i++) {
      if (listActions.get(i) == action && versionCodes.get(i) == version)
        list.add(couponNames.get(i));
    }
    return list;
  }
  
  public void addCouponDetail(String couponName, String imageName, int couponId) {
    couponDetails.add(new CouponDetail(couponName, imageName, couponId));
    actions.add(0);
  }
  
  public void addUpdate(int action, String couponName) {
    
    int index = 0;
    while (index < couponDetails.size()) {
      if (couponDetails.get(index).couponName.equals(couponName))
        break;
      index++;
    }
    actions.set(index, action);
  }
  
  public ArrayList<CouponDetail> getDownloadList() {
    
    ArrayList<CouponDetail> downloadList = new ArrayList<>();
    for (int i = 0; i < actions.size(); i++) {
      if (actions.get(i) == 1)
        downloadList.add(couponDetails.get(i));
    }
    return downloadList;
  }
  
  public ArrayList<CouponDetail> getDeleteList() {
    
    ArrayList<CouponDetail> deleteList = new ArrayList<>();
    for (int i = 0; i < actions.size(); i++) {
      if (actions.get(i) == -1)
        deleteList.add(couponDetails.get(i));
    }
    return deleteList;
  }
}
