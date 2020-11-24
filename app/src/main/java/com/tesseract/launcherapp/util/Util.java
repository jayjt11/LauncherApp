package com.tesseract.launcherapp.util;

import android.app.ProgressDialog;
import android.content.Context;

public class Util {

    static ProgressDialog progressDialog;


    public static  void showLoader(Context mContext) {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
//        progressDialog.setTitle(Constants.TITLE);
        progressDialog.setMessage("loading apps...");
        progressDialog.show();
    }

    public static  void hideLoader() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
