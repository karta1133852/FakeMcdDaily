package com.shokaku.hayate.fakemcddaily.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;*/

import com.shokaku.hayate.fakemcddaily.Activity.MainActivity;
import com.shokaku.hayate.fakemcddaily.Interface.OnBytesSuccessListener;

import java.io.ByteArrayOutputStream;

/**
 * Created by Shokaku on 2018/1/25/025.
 */

public class SQLiteHandler {
  
  public static DatabaseHelper getDB() {
    return DatabaseHelper.getInstance();
  }
  
  public static int getCouponVersionFromSQLite() {
  
    SQLiteDatabase readableDatabase = getDB().getReadableDatabase();
    Cursor cur = readableDatabase.rawQuery("SELECT * FROM coupon_version", null);
    
    cur.moveToFirst();
    int versionCode = cur.getInt(cur.getColumnIndex("version_code"));
    cur.close();
  
    readableDatabase.close();
    
    return versionCode;
  }
  
  public static void updateCouponVersion(int couponVersion) {
  
    SQLiteDatabase writableDatabase = getDB().getWritableDatabase();
    
    String sqlTruncate = "DELETE FROM coupon_version";
    writableDatabase.execSQL(sqlTruncate);
    
    String sqlInsert = "INSERT INTO coupon_version (version_code) VALUES (" + couponVersion + ")";
    writableDatabase.execSQL(sqlInsert);
    
    writableDatabase.close();
  }
  
  public static void getDataFromSQLite() {
    
    SQLiteDatabase readableDatabase = getDB().getReadableDatabase();
    Cursor cur = readableDatabase.rawQuery("SELECT * FROM coupons ORDER BY coupon_order", null);
    if (cur.moveToFirst()) {
      MainActivity.loaclCouponNames.clear();
      MainActivity.imgBitmaps.clear();
      do {
        String couponName = cur.getString(cur.getColumnIndex("coupon_name"));
        MainActivity.loaclCouponNames.add(couponName);
        MainActivity.imgBitmaps.add(getImage(couponName));
      } while (cur.moveToNext());
    }
    cur.close();
    readableDatabase.close();
  }
  
  public static void insertImg(int couponOrder, int couponId, String couponName, byte[] bytes) {
    
    SQLiteDatabase writableDatabase = getDB().getWritableDatabase();
  
    String sqlInsert = "INSERT OR IGNORE INTO coupons (coupon_order, coupon_id, coupon_name, img) " +
            "VALUES (" + couponOrder + ", " + couponId + ", ?, ?)";
    SQLiteStatement insertStmt = writableDatabase.compileStatement(sqlInsert);
    insertStmt.bindString(1, couponName);
    insertStmt.bindBlob(2, bytes);
    insertStmt.executeInsert();
    insertStmt.clearBindings();
    
    String sqlUpdate = "UPDATE coupons SET coupon_order = " + couponOrder +
            " WHERE coupon_id = " + couponId;
    writableDatabase.execSQL(sqlUpdate);
    
    writableDatabase.close();
  }
  
  public static void deleteCoupon(int couponId) {
  
    SQLiteDatabase writableDatabase = getDB().getWritableDatabase();
    String sqlDelete = "DELETE FROM coupons WHERE coupon_id = " + couponId;
    writableDatabase.execSQL(sqlDelete);
  
    writableDatabase.close();
  }
  
  public static Bitmap getImage(String couponName){
    
    SQLiteDatabase readableDatabase = getDB().getReadableDatabase();
    
    String sql = "SELECT img FROM coupons WHERE coupon_name = '" + couponName + "'";
    Cursor cur = readableDatabase.rawQuery(sql, null);
    
    if (cur.moveToFirst()) {
      byte[] imgByte = cur.getBlob(0);
      cur.close();
      return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }
    if (cur != null && !cur.isClosed()) {
      cur.close();
    }
    
    readableDatabase.close();
    return null;
  }
  
  /*
  public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
    
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
    return outputStream.toByteArray();
  }*/
}
