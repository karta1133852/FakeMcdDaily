package com.shokaku.hayate.fakemcddaily.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shokaku.hayate.fakemcddaily.R;


public class SettingsActivity extends AppCompatActivity {
  
  Spinner spinner_1, spinner_2, spinner_3;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    
    //通知欄、導航欄設定
    SystemBarTintManager tintManager=new SystemBarTintManager(this);
    tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
    tintManager.setStatusBarTintEnabled(true);
    tintManager.setNavigationBarTintEnabled(true);
    //加入返回按鈕
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("設定");
    
    getView();
    setSpinner();
  }
  
  private void getView() {
    
    spinner_1 = findViewById(R.id.spin_first);
    spinner_2 = findViewById(R.id.spin_second);
    spinner_3 = findViewById(R.id.spin_third);
  }
  
  private void setSpinner() {
    
    ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_dropdown_item, MainActivity.loaclCouponNames);
    
    spinner_1.setAdapter(spinAdapter);
    spinner_1.setSelection(MainActivity.coupons[0].couponId);
    spinner_2.setAdapter(spinAdapter);
    spinner_2.setSelection(MainActivity.coupons[1].couponId);
    spinner_3.setAdapter(spinAdapter);
    spinner_3.setSelection(MainActivity.coupons[2].couponId);
    
    AdapterView.OnItemSelectedListener onItemSelect = new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        int i = -1;
        switch (spinner.getId()) {
          case R.id.spin_first:
            i = 0;
            break;
          case R.id.spin_second:
            i = 1;
            break;
          case R.id.spin_third:
            i = 2;
            break;
        }
        MainActivity.coupons[i].couponId = position;
        MainActivity.mAdapter.notifyItemChanged(i);
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    };
    
    spinner_1.setOnItemSelectedListener(onItemSelect);
    spinner_2.setOnItemSelectedListener(onItemSelect);
    spinner_3.setOnItemSelectedListener(onItemSelect);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    
    int id = item.getItemId();
    if (id == android.R.id.home)
      this.finish();
    
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)  {
    
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
      this.finish();
    
    return super.onKeyDown(keyCode, event);
  }
}
