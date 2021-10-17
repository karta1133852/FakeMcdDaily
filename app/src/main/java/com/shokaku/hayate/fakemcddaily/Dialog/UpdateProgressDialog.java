package com.shokaku.hayate.fakemcddaily.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shokaku.hayate.fakemcddaily.R;


public class UpdateProgressDialog implements DialogInterface.OnCancelListener, View.OnClickListener{
  
  private Context context;
  private Dialog mDialog;
  private ProgressBar progressBar;
  private TextView txtNowUpdating, txtFraction;
  
  private long denominator, numerator;
  private int imageCount;
  private long[] currentByte;
  
  public UpdateProgressDialog(Context context){
    this.context = context;
  
    mDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog);
    mDialog.setContentView(R.layout.dialog_update_progressbar);
  
    if (mDialog.getWindow() != null) {
      /*int pxWidth = (int) (300 * getResources().getDisplayMetrics().density);
      int pxHeight = (int) (100 * getResources().getDisplayMetrics().density);
      mDialog.getWindow().setLayout(pxWidth, pxHeight);*/
      mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      mDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
              WindowManager.LayoutParams.WRAP_CONTENT);
    }
  
    mDialog.setCancelable(false);
    mDialog.setCanceledOnTouchOutside(false);
    mDialog.setOnCancelListener(this);
    
    progressBar = mDialog.findViewById(R.id.progressBarCount);
    txtNowUpdating = mDialog.findViewById(R.id.txtNowUpdating);
    txtFraction = mDialog.findViewById(R.id.txtFraction);
  
    progressBar.setProgress(0);
    numerator = denominator = 0;
    showFractionText();
  }
  
  public UpdateProgressDialog show() {
    mDialog.show();
    return this;
  }
  
  public void dismissDialog() {
    mDialog.dismiss();
  }
  
  public UpdateProgressDialog setDetail(String imgName) {
    txtNowUpdating.setText(imgName);
    return this;
  }
  
  public UpdateProgressDialog setDetail(int stringResId) {
    txtNowUpdating.setText(stringResId);
    return this;
  }
  
  public UpdateProgressDialog setProgress(double progress) {
    Double progressD = progress;
    progressBar.setProgress(progressD.intValue());
    return this;
  }
  
  public UpdateProgressDialog incrementProgressBy(double progress) {
    Double progressD = progress;
    progressBar.incrementProgressBy(progressD.intValue());
    return this;
  }
  
  public UpdateProgressDialog setProgressByFraction() {
    Double progressD = (double) numerator / (double) denominator * 100;
    //progressBar.setProgress(progressD.intValue());
    return this;
  }
  
  public UpdateProgressDialog setDenominator(int denominator) {
    this.denominator = denominator;
    numerator = 0;
    showFractionText();
    return this;
  }
  
  public UpdateProgressDialog increaseFrcation() {
    numerator++;
    showFractionText();
    setProgressByFraction();
    return this;
  }
  
  public UpdateProgressDialog setDownloadCount(int imageCount) {
    this.imageCount = imageCount;
    currentByte = new long[imageCount];
    for (int i = 0; i < currentByte.length; i++) {
      currentByte[i] = 0;
    }
    return this;
  }
  
  public UpdateProgressDialog addTotalBytes(long byteCount) {
    denominator += byteCount;
    showFractionText();
    setProgressByFraction();
    return this;
  }
  
  public UpdateProgressDialog setCurrentBytes(int index, long byteCount) {
  
    numerator = numerator - currentByte[index] + byteCount;
    currentByte[index] = byteCount;
    showFractionText();
    setProgressByFraction();
    return this;
  }
  
  private void showFractionText() {
    String str = "(" + numerator + "/" + denominator + ")";
    txtFraction.setText(str);
  }
  
  @Override
  public void onCancel(DialogInterface dialog) {
    mDialog.dismiss();
  }
  
  @Override
  public void onClick(View v) {
    mDialog.dismiss();
  }
}
