package com.codeground.wanderlustbulgaria.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.codeground.wanderlustbulgaria.R;

import dmax.dialog.SpotsDialog;

public class DialogWindowManager {
    private static AlertDialog mProgressDialog;

    public static void show(Context ctx){
        mProgressDialog = new SpotsDialog(ctx, R.style.DialogWindow);
        mProgressDialog.show();
    }

    public static void dismiss(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    public static boolean isShowing(){
        if(mProgressDialog != null) {
            return mProgressDialog.isShowing();
        }

        return false;
    }

    public static void hideKeyboard(Activity ctx){
        View view = ctx.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
