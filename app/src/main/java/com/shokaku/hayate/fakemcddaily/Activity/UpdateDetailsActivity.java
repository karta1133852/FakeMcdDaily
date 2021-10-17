package com.shokaku.hayate.fakemcddaily.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shokaku.hayate.fakemcddaily.CouponUpdateListAdapter;
import com.shokaku.hayate.fakemcddaily.Database.FirebaseHandler;
import com.shokaku.hayate.fakemcddaily.Database.SQLiteHandler;
import com.shokaku.hayate.fakemcddaily.Dialog.UpdateProgressDialog;
import com.shokaku.hayate.fakemcddaily.Instance.CouponDetail;
import com.shokaku.hayate.fakemcddaily.Instance.CouponUpdateList;
import com.shokaku.hayate.fakemcddaily.Interface.UpdateListCallBack;
import com.shokaku.hayate.fakemcddaily.R;

import java.util.ArrayList;

public class UpdateDetailsActivity extends AppCompatActivity {
  
  private CouponUpdateList couponUpdateList;
  int localVersionCode, syncVersionCode;
  
  private Button btnUpdate;
  private RecyclerView recyclerlist;
  private ProgressBar progressBarLoading;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_details);
    
    //通知欄、導航欄設定
    SystemBarTintManager tintManager = new SystemBarTintManager(this);
    tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
    tintManager.setStatusBarTintEnabled(true);
    tintManager.setNavigationBarTintEnabled(true);
    
    //加入返回按鈕
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("更新");
    
    
    getView();
    setBtnListener();
    couponUpdate();
  }
  
  private void getView() {
    
    btnUpdate = findViewById(R.id.btn_update);
    recyclerlist = findViewById(R.id.list_updateDetailDisplay);
    progressBarLoading = findViewById(R.id.progressBarLoading);
  }
  
  private void setBtnListener() {
    
    btnUpdate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        update();
      }
    });
  }
  
  private void couponUpdate() {
    
    localVersionCode = SQLiteHandler.getCouponVersionFromSQLite();
    syncVersionCode = getIntent().getIntExtra("latestVersion", 1);
  
    FirebaseHandler.getUpdateList(localVersionCode, new UpdateListCallBack() {
      @Override
      public void onListSuccess(CouponUpdateList list) {
        couponUpdateList = list;
        progressBarLoading.setVisibility(View.GONE);
        setList();
      }
    });
  
    if (getIntent().getBooleanExtra("isLatest", true)) {
      syncVersionCode = localVersionCode;
      Snackbar.make(findViewById(android.R.id.content), getString(R.string.already_latest),
              Snackbar.LENGTH_LONG).show();
    } else {
      btnUpdate.setVisibility(View.VISIBLE);
    }
  }
  
  private void setList() {
    CouponUpdateListAdapter mAdapter = new CouponUpdateListAdapter(couponUpdateList, syncVersionCode);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerlist.setLayoutManager(layoutManager);
    mAdapter.notifyDataSetChanged();
    recyclerlist.setAdapter(mAdapter);
  }
  
  private void update() {
    
    final ArrayList<CouponDetail> downloadList = couponUpdateList.getDownloadList();
    final int downloadSize = downloadList.size();
    
    final UpdateProgressDialog dialog = new UpdateProgressDialog(this);
    dialog.show();
    dialog.setDetail(R.string.updating);
    dialog.setDownloadCount(downloadSize);
    
    for (int i = 0; i < downloadSize; i++) {
      final int finalI = i;
      FirebaseHandler.downloadCoupon(downloadList.get(i).imageName,
              new OnSuccessListener<Long>() {
                @Override
                public void onSuccess(Long aLong) {
                  dialog.addTotalBytes(aLong);
                }
              },
      new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                  if (bytes != null) {
                    SQLiteHandler.insertImg(finalI, downloadList.get(finalI).couponId,
                            downloadList.get(finalI).couponName, bytes);
                    if (finalI == downloadSize - 1) {
                      deleteCoupons(dialog);
                    }
                  }
                }
              },
              new OnProgressListener<Long>() {
                @Override
                public void onProgress(Long aLong) {
                  dialog.setCurrentBytes(finalI, aLong);
                }
              });
    }
  }
  
  private void deleteCoupons(UpdateProgressDialog dialog) {
  
    ArrayList<CouponDetail> deleteList = couponUpdateList.getDeleteList();
    int deleteSize = deleteList.size();
    
    for (int i = 0; i < deleteSize; i++) {
      dialog.setDetail(R.string.deleting);
      SQLiteHandler.deleteCoupon(deleteList.get(i).couponId);
      dialog.increaseFrcation();
    }
    dialog.dismissDialog();
    
    SQLiteHandler.updateCouponVersion(syncVersionCode);
    SQLiteHandler.getDataFromSQLite();
    MainActivity.resetCoupons();
  
    new AlertDialog.Builder(UpdateDetailsActivity.this)
            .setTitle(getString(R.string.tip))
            .setMessage(getString(R.string.update_complete))
            .setPositiveButton(R.string.close_history, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                closeActivity();
              }
            }).show();
  }
  
  private void closeActivity() {
    this.finish();
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    
    int id = item.getItemId();
    if (id == android.R.id.home)
      this.finish();
    
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
      this.finish();
    
    return super.onKeyDown(keyCode, event);
  }
}
