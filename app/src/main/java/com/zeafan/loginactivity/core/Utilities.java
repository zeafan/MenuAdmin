package com.zeafan.loginactivity.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zeafan.loginactivity.R;

import java.util.HashMap;

import androidx.annotation.NonNull;

/**
 * Created by usamaa on 24-Aug-17.
 */

public class Utilities {
    public static MaterialDialog showWarningDialog(Context context, String title, int message) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .contentColorRes(android.R.color.black)
                .titleColorRes(android.R.color.black)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    public static void showWarningDialogIntoThreat(final Activity activity, final int title, final int message) {
        if(activity.isFinishing()||activity.isDestroyed()){
            return;
        }
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 new MaterialDialog.Builder(activity)
                        .title(title)
                        .content(message)
                        .contentColorRes(android.R.color.black)
                        .titleColorRes(android.R.color.black)
                        .negativeText(R.string.ok)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }});
    }
    public static void showWarningDialogIntoThreat(final Activity activity, final int title, final String message) {
        if(activity == null){
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
             MaterialDialog.Builder m =   new MaterialDialog.Builder(activity)
                        .title(title)
                        .content(message)
                        .contentColorRes(android.R.color.black)
                        .titleColorRes(android.R.color.black)
                        .negativeText(R.string.ok)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        });
                if (!activity.isFinishing() && activity.hasWindowFocus()) {
                    m.show();
                }
            }});
    }
    public static MaterialDialog showWarningDialog(Context context, int title, String message) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .contentColorRes(android.R.color.black)
                .titleColorRes(android.R.color.black)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    public static MaterialDialog showWarningDialog(Context context, int title, String message, final IResult iResult) {

        return new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .contentColorRes(android.R.color.black)
                .titleColorRes(android.R.color.black)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        iResult.notifySuccess(dialog);
                    }
                })
                .show();
    }
    public static MaterialDialog showWarningDialog(Context context, int title, int message, final IResult iResult) {

        return new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .contentColorRes(android.R.color.black)
                .titleColorRes(android.R.color.black)
                .negativeText(R.string.ok)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        iResult.notifySuccess(dialog);
                    }
                })
                .show();
    }

    public static void showWarningDialogIntoThreat(final Activity activity, final int title, final int message, final int negativeText, final int positiveText, final IResult iResult) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder(activity)
                        .title(title)
                        .content(message)
                        .contentColorRes(android.R.color.black)
                        .titleColorRes(android.R.color.black)
                        .cancelable(false)
                        .negativeText(negativeText)
                        .positiveText(positiveText)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                iResult.notifySuccess(null);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                iResult.notifyError(null);
                            }
                        })
                        .show();
            }});
    }
//    public static MaterialDialog showConfrimationDialog(Context context, int title, String message,final IResult iResult) {
//
//        return  new MaterialDialog.Builder(context)
//                .title(title)
//                .content(message)
//                .contentColorRes(android.R.color.black)
//                .titleColorRes(android.R.color.black)
//                .positiveText(R.string.ok)
//                .negativeText(R.string.cancel)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        iResult.notifySuccess(dialog);
//                    }
//                })
//                .showListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface dialog) {
//
//                    }
//                }).onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                        iResult.notifyError(dialog);
//                    }
//                })
//                .show();
//    }
    public static MaterialDialog showConfrimationDialog(Context context, int title, int message, final IResult iResult) {

        return  new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .contentColorRes(android.R.color.black)
                .titleColorRes(android.R.color.black)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        iResult.notifySuccess(dialog);
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        iResult.notifyError(dialog);
                    }
                })
                .show();
    }
    public static String KEY_DIALOG="Dialog";
    public static String KEY_PROGRESS_BAR="ProgressBar";


    public static boolean IsNetworkAvailable(Context context){
        boolean result;
        result = GlobalClass.StaticCore.IsNetworkAvailable(context);
        try {
            result &= (GlobalClass.StaticCore.iSAvailabe_Wifi(context) || GlobalClass.StaticCore.isAvailableAcessData(context));
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void showLongToast(Context context, String message, int duration) {
        for (int i=0; i < duration; i++) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
    public static void showLongToast(Context context, int message, int duration) {
        for (int i=0; i < duration; i++) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
    public static void showToastinThread(final Activity activity, final ProgressDialog progress, final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress!=null&&progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void showToast(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

            }
        });
    }

    public static Dialog showWaitDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
          View view = LayoutInflater.from(context).inflate(R.layout.progress_wait, null);
          builder.setView(view);
          builder.create();
         return builder.show();
    }
}
