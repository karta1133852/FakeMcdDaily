package com.shokaku.hayate.fakemcddaily;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shokaku.hayate.fakemcddaily.Instance.CouponUpdateList;

import java.util.ArrayList;

public class CouponUpdateListAdapter extends RecyclerView.Adapter<CouponUpdateListAdapter.ViewHolder> {
  
  CouponUpdateList couponUpdateList;
  int latestVersion;
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    TextView txtCouponVersion, txtAddList, txtDeleteList;
    LinearLayout layAdd, layDel;
    
    public ViewHolder(View v) {
      super(v);
      txtCouponVersion = v.findViewById(R.id.txtCouponVersion);
      txtAddList = v.findViewById(R.id.txtAddList);
      txtDeleteList = v.findViewById(R.id.txtDeleteList);
      layAdd = v.findViewById(R.id.lay_add);
      layDel = v.findViewById(R.id.lay_del);
    }
  }
  
  public CouponUpdateListAdapter(CouponUpdateList couponUpdateList, int latestVersion) {
    this.couponUpdateList = couponUpdateList;
    this.latestVersion = latestVersion;
  }
  
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.view_item_coupon_update, viewGroup, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }
  
  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    
    int version = latestVersion - i;
    String versionStr = "v" + version;
    viewHolder.txtCouponVersion.setText(versionStr);
    
    ArrayList<String> historyAddList = couponUpdateList.getHistory(version, 1);
    if (historyAddList.size() != 0) {
      StringBuilder addListStr = new StringBuilder();
      for (String name : historyAddList) {
        addListStr.append(name + "\n");
      }
      viewHolder.txtAddList.setText(addListStr.toString().trim());
    } else {
      viewHolder.layAdd.setVisibility(View.GONE);
    }
    
    ArrayList<String> historyDelList = couponUpdateList.getHistory(version, -1);
    if (historyDelList.size() != 0) {
      StringBuilder delListStr = new StringBuilder();
      for (String name : historyDelList) {
        delListStr.append(name + "\n");
      }
      viewHolder.txtDeleteList.setText(delListStr.toString().trim());
    } else {
      viewHolder.layDel.setVisibility(View.GONE);
    }
  }
  
  @Override
  public int getItemCount() {
    return latestVersion;
  }
}
