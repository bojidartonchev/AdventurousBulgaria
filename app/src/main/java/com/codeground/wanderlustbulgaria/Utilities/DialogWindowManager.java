package com.codeground.wanderlustbulgaria.Utilities;

import android.app.AlertDialog;
import android.content.Context;

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

}
