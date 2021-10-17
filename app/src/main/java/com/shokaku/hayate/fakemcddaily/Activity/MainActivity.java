package com.shokaku.hayate.fakemcddaily.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.shokaku.hayate.fakemcddaily.Instance.Coupon;
import com.shokaku.hayate.fakemcddaily.Database.DatabaseHelper;
import com.shokaku.hayate.fakemcddaily.Database.FirebaseHandler;
import com.shokaku.hayate.fakemcddaily.Interface.OnCouponSyncListener;
import com.shokaku.hayate.fakemcddaily.CouponListViewAdapter;
import com.shokaku.hayate.fakemcddaily.R;
import com.shokaku.hayate.fakemcddaily.Database.SQLiteHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
  
  final static int assetsDBVersion = 1;
  
  public static Coupon[] coupons;
  
  public static List<String> syncImgNames = new ArrayList<>();
  public static List<String> syncCouponNames = new ArrayList<>();
  //test
  public static List<String> loaclCouponNames = new ArrayList<>();
  public static List<Bitmap> imgBitmaps = new ArrayList<>();
  
  
  RecyclerView recyclerlist;
  public static CouponListViewAdapter mAdapter;
  private NavigationView navigationView;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.getMenu().getItem(0).setChecked(true);
    
    
    checkSQLiteFileExists();
    DatabaseHandle();
    
    initCoupons();
    
    getView();
    setList();
  }
  
  private void getView() {
    
    recyclerlist = (RecyclerView) findViewById(R.id.cylist_show_data);
    navigationView = (NavigationView) findViewById(R.id.nav_view);
  }
  
  private void setList() {
    
    mAdapter = new CouponListViewAdapter(coupons, this);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerlist.setLayoutManager(layoutManager);
    mAdapter.notifyDataSetChanged();
    recyclerlist.setAdapter(mAdapter);
  }
  
  private void initCoupons() {
    
    int couponQty = 6;
    coupons = new Coupon[couponQty];
    for (int i = 0; i < couponQty; i++) {
      int couponId = (int) (Math.random() * MainActivity.loaclCouponNames.size());
      coupons[i] = new Coupon(couponId, (i < 3) ? false : true);
    }
  }
  
  public static void resetCoupons() {
  
    int couponQty = 6;
    coupons = new Coupon[couponQty];
    for (int i = 0; i < couponQty; i++) {
      coupons[i] = new Coupon(0, (i < 3) ? false : true);
    }
    
    mAdapter.notifyDataSetChanged();
  }
  
  private void checkSQLiteFileExists() {
    
    final String DB_NAME = "coupons.sqlite";
    final String DB_PATH = getFilesDir().getParent() + "/databases/";
    final String assets_PATH = "file:///android_asset/";
    String outFilePath = DB_PATH + DB_NAME;
    File dbFile = new File(outFilePath);
    
    if (!dbFile.exists()) {
      copyDatabase(DB_PATH, DB_NAME);
    } else {
      int localDBVersion = SQLiteDatabase
              .openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY).getVersion();
      if (assetsDBVersion > localDBVersion) {
        copyDatabase(DB_PATH, DB_NAME);
      }
    }
  }
  
  private void copyDatabase(String DB_PATH, String DB_NAME) {
    
    String outFilePath = DB_PATH + DB_NAME;
    File dbFile = new File(outFilePath);
    
    new File(DB_PATH).mkdirs();
    try {
      dbFile.createNewFile();
    } catch (IOException e) {
      throw new Error("檔案建立出現問題 !\n" + e);
    }
    //Toast.makeText(this, "資料庫不存在 ! 從Assets複製", Toast.LENGTH_SHORT).show();
    InputStream iStream = null;
    OutputStream oStream = null;
    try {
      iStream = getAssets().open(DB_NAME);
      oStream = new FileOutputStream(outFilePath);
      byte[] buffer = new byte[1024];
      int length;
      while ((length = iStream.read(buffer)) > 0) {
        oStream.write(buffer, 0, length);
      }
      oStream.flush();
      oStream.close();
      iStream.close();
    } catch (IOException ioe) {
      throw new Error("複製出現問題 !\n" + ioe);
    }
  }
  
  private void DatabaseHandle() {
    //Init Database instance
    DatabaseHelper.initInstance(this);
    
    SQLiteHandler.getDataFromSQLite();
    checkCouponUpdate(true);
  }
  
  private void checkCouponUpdate(final boolean isAutoCheck) {
    FirebaseHandler.checkCouponUpdate(new OnCouponSyncListener() {
      @Override
      public void onCouponUpdate(int versionCode) {
        Intent intent = new Intent(MainActivity.this, UpdateDetailsActivity.class);
        intent.putExtra("isLatest", false);
        intent.putExtra("latestVersion", versionCode);
        startActivity(intent);
      }
      
      @Override
      public void onLatest(int versionCode) {
        if (!isAutoCheck) {
          Intent intent = new Intent(MainActivity.this, UpdateDetailsActivity.class);
          intent.putExtra("isLatest", true);
          intent.putExtra("latestVersion", versionCode);
          startActivity(intent);
        }
      }
    });
  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    
    //noinspection SimplifiableIfStatement
    if (id == R.id.usage) {
      //fireDBHandler.syncCouponsDetails();
      return true;
      //mAdapter.notifyDataSetChanged();
    }
    
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
  
    navigationView.getMenu().getItem(0).setChecked(true);
    
    int id = item.getItemId();
    if (id == R.id.nav_setting) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav_update) {
      checkCouponUpdate(false);
    }
    
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}

