package com.shokaku.hayate.fakemcddaily.Activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shokaku.hayate.fakemcddaily.Instance.Coupon;
import com.shokaku.hayate.fakemcddaily.R;


public class CouponDetailActivity extends AppCompatActivity {
  
  ImageView img_cover;
  Button btnRedeem;
  ImageButton btnClose;
  RelativeLayout layTime;
  TextView txtCountDown;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coupon_detail);
    
    //加入返回按鈕
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("優惠券");
    
    Bundle b = getIntent().getExtras();
    final int couponId, position;
    couponId = b.getInt("couponId");
    position = b.getInt("couponPosit");
    
    getView();
    
    Bitmap couponCover = MainActivity.imgBitmaps.get(couponId);
    img_cover.setImageBitmap(couponCover);
    btnClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    
    final Coupon coupon = MainActivity.coupons[position];
    if (coupon.isCounting) {
      
      btnRedeem.setVisibility(View.GONE);
      layTime.setVisibility(View.VISIBLE);
      new CountDownTimer(120 * 1000, 1000) {
        
        @Override
        public void onTick(long millisUntilFinished) {
          txtCountDown.setText("優惠倒數 " + coupon.getFormatedTime());
          if (coupon.secCountDown == 0) {
            onFinish();
          }
        }
        @Override
        public void onFinish() {}
      }.start();
    } else {
      
      btnRedeem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
          
          AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                  .setTitle(Html.fromHtml("<font color='#000000'>確認兌換優惠</font>"))//.setTitle("確認兌換優惠")
                  .setMessage("請確認您已在麥當勞櫃檯，點選「立即兌換」後，須於兩分鐘內出示給結帳人員")
                  .setNegativeButton("暫不兌換", null)
                  .setPositiveButton("立即兌換", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      btnRedeem.setVisibility(View.GONE);
                      layTime.setVisibility(View.VISIBLE);
                      coupon.startCountDown();
                      new CountDownTimer(120 * 1000, 1000) {
                        
                        @Override
                        public void onTick(long millisUntilFinished) {
                          txtCountDown.setText("優惠倒數 " + coupon.getFormatedTime());
                        }
                        @Override
                        public void onFinish() {}
                      }.start();
                    }
                  })
                  .show();
          dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                  .setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));
          dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                  .setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));
          TextView textView = dialog.findViewById(android.R.id.message);
          textView.setTextSize(16);
        }
      });
    }
    
    SystemBarTintManager tintManager=new SystemBarTintManager(this);
    tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
    tintManager.setStatusBarTintEnabled(true);
    
  }
  
  private void getView() {
    
    img_cover = findViewById(R.id.cover);
    txtCountDown = findViewById(R.id.countdown_timer);
    btnRedeem = findViewById(R.id.btnRedeem);
    layTime = findViewById(R.id.timer_layout);
    btnClose = findViewById(R.id.btnClose);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    
    getMenuInflater().inflate(R.menu.toolbar_button, menu);
    return true;
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
