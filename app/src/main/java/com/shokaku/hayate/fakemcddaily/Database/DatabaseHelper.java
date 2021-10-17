package com.shokaku.hayate.fakemcddaily.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shokaku on 2018/1/25/025.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
  
  private static final String DB_NAME = "coupons.sqlite";
  private static int version = 2;
  private static DatabaseHelper instance = null;
  private Context context;
  
  private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    
    super(context, name, factory, version);
    this.version = version;
    this.context = context;
  }
  
  public int getVersion() { return version; }
  
  //避免dead lock
  public static void initInstance(Context context){
    
    if (instance == null) {
      instance = new DatabaseHelper(context, DB_NAME, null, version);
    }
  }
  
  public static DatabaseHelper getInstance(){
    
    if (instance == null) {
      instance = new DatabaseHelper(null, DB_NAME, null, version);
    }
    return instance;
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    
    //資料庫版本 : 2
    
    //優惠券資料
    String sqlCreateCoupons = "CREATE TABLE coupons ( " +
            "coupon_order INTEGER NOT NULL, " +
            "coupon_id INTEGER NOT NULL UNIQUE, " +
            "coupon_name TEXT NOT NULL UNIQUE, " +
            "img BLOB NOT NULL)";
    db.execSQL(sqlCreateCoupons);
  
    //優惠券版本號
    String sqlCreateVersion = "CREATE TABLE coupon_version (" +
            "version_code INTEGER NOT NULL UNIQUE)";
    db.execSQL(sqlCreateVersion);
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  
  }
}
