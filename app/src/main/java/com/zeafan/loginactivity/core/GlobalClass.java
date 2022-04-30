package com.zeafan.loginactivity.core;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.data.User;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.core.app.ActivityCompat;

public class GlobalClass  extends Application {
    public static User currentUser;
    public static final String EmptyGuid = "00000000-0000-0000-0000-000000000000";
    public static byte[] convertBitmapToArrayByte(Bitmap bitmap,int dstWidth) {
        bitmap = resizeImage2(bitmap,dstWidth);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public static Bitmap resizeImage2(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        int newHeight = (int)(scaleWidth* height);
       return Bitmap.createScaledBitmap(bitmap,Math.min(width,newWidth), Math.min(height,newHeight), true);
    }

    public static Bitmap GenerateQRCode(String values,int dimension){
        Bitmap bitmap;
        if(!values.isEmpty()){
            QRGEncoder qrgEncoder = new QRGEncoder(values,null, QRGContents.Type.TEXT,dimension);
            qrgEncoder.setColorBlack(Color.BLACK);
            qrgEncoder.setColorWhite(Color.WHITE);
            try {
                bitmap = qrgEncoder.getBitmap();
            } catch (Exception e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
                return null;
            }
            return bitmap;
        }else {
            return null;
        }
    }

    public static void SendExceptionToFirebaseServer(Exception ex) {
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true);
        if(ex!=null) {
            firebaseCrashlytics.recordException(ex);
        }
    }

    public static String getSimpleUUID(String uid) {
        String firstItem = uid.substring(0,3);
        String secondItem = uid.substring(uid.length()-3);
        return firstItem+secondItem;
    }

    public static class StaticCore{
        public static boolean IsNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            return false;
        }
        public static boolean iSAvailabe_Wifi(Context context) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi!=null&&mWifi.isConnected();
        }
        public static boolean isAvailableAcessData(Context context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class cmClass = null;
            try {
                cmClass = Class.forName(cm.getClass().getName());
            } catch (ClassNotFoundException e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
                e.printStackTrace();
            }
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            return  (Boolean) method.invoke(cm);

        }

        public static void Click(View view,Context context) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
        }
        public static void takeImagefromCamera(Activity activityCompat, final int requsetCodeTakePhoto) {
            activityCompat.startActivityForResult(new Intent( MediaStore.ACTION_IMAGE_CAPTURE),requsetCodeTakePhoto);
        }

        public static   void checkPermissionGetImage(Activity activity,int requsetCodeGetImageFromGallery) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (!activity.shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requsetCodeGetImageFromGallery);
                        return;
                    }
                }

            }
            GetImageFromGallery(activity,requsetCodeGetImageFromGallery);
        }

        public static  void checkPermissionTakeImage(Activity activity,int requsetCodeTakePhoto) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (!activity.shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requsetCodeTakePhoto);
                        return;
                    }
                }

            }
            takeImagefromCamera(activity,requsetCodeTakePhoto);
        }

        public static void GetImageFromGallery(Activity activity,int requsetCodeGetImageFromGallery) {
            try {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                activity.startActivityForResult(photoPickerIntent, requsetCodeGetImageFromGallery);
            }catch (Exception ex){
                GlobalClass.SendExceptionToFirebaseServer(ex);
                Toast.makeText(activity, activity.getString(R.string.no_memory), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
