package com.oneconnect.demoapp.OneConnectActivities;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.content.Intent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.oneconnect.demoapp.R;
import com.oneconnect.demoapp.Utils.Constants;
import java.io.IOException;
import top.oneconnectapi.app.api.OneConnect;

public class SplashScreen extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    private AppUpdateManager mAppUpdateManager;
    private final int RC_APP_UPDATE = 999;
    private int inAppUpdateType;
    private com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    OneConnect oneConnect = new OneConnect();
                    oneConnect.initialize(SplashScreen.this, "oTUDEq.susHlRkDDSSmGd1ODH0QihV0ZWnq96t1cTkYTSYcsG."); // Put Your OneConnect API Key for Work Server
                    try {
                        Constants.FREE_SERVERS = oneConnect.fetch(true);
                        Constants.PREMIUM_SERVERS = oneConnect.fetch(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();




        String TAG = "Firebase";



        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        coordinatorLayout = findViewById(R.id.cordi);

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        installStateUpdatedListener = installState -> {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        };
        mAppUpdateManager.registerListener(installStateUpdatedListener);

        inAppUpdateType = AppUpdateType.IMMEDIATE; //1
        inAppUpdate();


    }

    private void inAppUpdate() {

        try {
            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)) {

                        try {
                            mAppUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    inAppUpdateType,
                                    SplashScreen.this,
                                    RC_APP_UPDATE);
                        } catch (IntentSender.SendIntentException ignored) {

                        }
                    } else {
                        proceed();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                proceed();
            }
        });
    }

    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        coordinatorLayout,
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.gnt_ad_green));
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {
            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                inAppUpdateType,
                                this,
                                RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });


            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(SplashScreen.this, "Downloading...", Toast.LENGTH_LONG).show();
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(SplashScreen.this, "Download Canceled.", Toast.LENGTH_LONG).show();
            } else {
                proceed();
            }
        }
    }

    private void proceed() {
        if (!Utility.isOnline(getApplicationContext())) {

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Check internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);

                    if (prefs.getBoolean("firstTime", true)) {
                        startActivity(new Intent(SplashScreen.this, OneConnectPolicy.class));
                    } else {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }

                    finish();
                }
            }, 2000);
        }
    }
}

