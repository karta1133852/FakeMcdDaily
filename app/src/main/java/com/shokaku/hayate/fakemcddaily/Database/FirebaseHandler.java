package com.shokaku.hayate.fakemcddaily.Database;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.shokaku.hayate.fakemcddaily.Instance.CouponUpdateList;
import com.shokaku.hayate.fakemcddaily.Interface.OnCouponSyncListener;
import com.shokaku.hayate.fakemcddaily.Interface.StringListCallBackInterface;
import com.shokaku.hayate.fakemcddaily.Interface.UpdateListCallBack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shokaku on 2018/1/25/025.
 */

public class FirebaseHandler {
  
  private static FirebaseDatabase getFireDB() {
    return FirebaseDatabase.getInstance();
  }
  
  public static void checkCouponUpdate(final OnCouponSyncListener listener) {
    
    DatabaseReference dbRef = getFireDB().getReference("coupon-info/coupon-version");
    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        
        int syncCouponVersionCode = Integer.parseInt(dataSnapshot.child("code").getValue().toString());
        int localCouponVersionCode = SQLiteHandler.getCouponVersionFromSQLite();
        if (syncCouponVersionCode > localCouponVersionCode) {
          listener.onCouponUpdate(syncCouponVersionCode);
        } else {
          listener.onLatest(syncCouponVersionCode);
        }
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
      
      }
    });
  }
  
  public static void downloadCoupon(String imageName, final OnSuccessListener<Long> getByteCountListener, final OnSuccessListener<byte[]> onSuccessListener, final OnProgressListener<Long> onProgressListener) {
    
    StorageReference storageRef = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://fakemcddaily.appspot.com/images").child(imageName + ".png");
    
    File tmpImagesFile = null;
    try {
      tmpImagesFile = File.createTempFile("coupon-", "-png");
    } catch (IOException e) {
      e.printStackTrace();
    }
    final File finalTmpFile = tmpImagesFile;
    
    storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
      @Override
      public void onSuccess(StorageMetadata storageMetadata) {
        getByteCountListener.onSuccess(storageMetadata.getSizeBytes());
      }
    });
    
    storageRef.getFile(tmpImagesFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
  
        int size = (int) finalTmpFile.length();
        byte[] bytes = new byte[size];
        try {
          BufferedInputStream buf = new BufferedInputStream(new FileInputStream(finalTmpFile));
          buf.read(bytes, 0, bytes.length);
          buf.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        onSuccessListener.onSuccess(bytes);
        
      }
    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
      @Override
      public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
        onProgressListener.onProgress(taskSnapshot.getTotalByteCount());
      }
    });
    
    /*
    final long ONE_MEGABYTE = 1024 * 1024;
    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
      
      @Override
      public void onSuccess(byte[] bytes) {
        if (bytes == null) {
          listener.onBytesSuccess(null);
        } else {
          listener.onBytesSuccess(bytes);
        }
      }
    });*/
  }
  
  public static void getUpdateList(final int localVersionCode, final UpdateListCallBack callBack) {
    
    DatabaseReference dbRef = getFireDB().getReference("coupon-info");
    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        
        CouponUpdateList list = new CouponUpdateList();
        for (DataSnapshot ds : dataSnapshot.child("details").getChildren()) {
          String couponName = ds.child("name").getValue().toString();
          String imageName = ds.child("img").getValue().toString();
          int couponId = Integer.parseInt(ds.child("coupon-id").getValue().toString());
          list.addCouponDetail(couponName, imageName, couponId);
        }
        for (DataSnapshot ds : dataSnapshot.child("update-info").getChildren()) {
          int version = Integer.parseInt(ds.child("code").getValue().toString());
          for (DataSnapshot ds2 : ds.child("info").getChildren()) {
            int action = (ds2.child("act").getValue().toString().equals("+")) ? 1 : -1;
            String couponName = ds2.child("str").getValue().toString();
            list.addHistory(version, action, couponName);
            if (version > localVersionCode)
              list.addUpdate(action, couponName);
          }
        }
        callBack.onListSuccess(list);
      }
      
      @Override
      public void onCancelled(DatabaseError databaseError) {
      
      }
    });
  }
  
  public static void getVersionList(int type, final StringListCallBackInterface callBackInterface) {
    
    DatabaseReference dbRefCouponsVersion = null;
    switch (type) {
      case 0: //應用程式版本清單
        dbRefCouponsVersion = getFireDB().getReference("app-version/list");
        break;
      case 1: //優惠券版本清單
        dbRefCouponsVersion = getFireDB().getReference("coupons-info/coupons-version/list");
        break;
    }
    
    dbRefCouponsVersion.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        
        List<String> versionNameList = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
          String couponVersionName = ds.child("name").getValue().toString();
          versionNameList.add(couponVersionName);
        }
        
        callBackInterface.onCallBack(versionNameList);
      }
      
      @Override
      public void onCancelled(DatabaseError databaseError) {
        //Toast.makeText(context, "版本清單檢查失敗 !", Toast.LENGTH_SHORT).show();
      }
    });
  }
}
