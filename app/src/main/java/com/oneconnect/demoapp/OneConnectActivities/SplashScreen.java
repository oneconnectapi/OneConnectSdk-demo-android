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
                    oneConnect.initialize(SplashScreen.this, "VZmQWDUhF8A3uzhBY.2MUnG6rukFcv28ttnw6kk4.zJas.2QwU"); // Put Your OneConnect API Key for Work Server
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

/*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference typeRef = database.getReference("type");
        DatabaseReference admob_id = database.getReference("admob_id");
        DatabaseReference admob_banner = database.getReference("admob_banner");
        DatabaseReference admob_native = database.getReference("admob_native");
        DatabaseReference fb_banner = database.getReference("fb_banner");
        DatabaseReference fb_interstitial = database.getReference("fb_interstitial");
        DatabaseReference ad_interstitial = database.getReference("ad_interstitial");
        DatabaseReference admob_reward = database.getReference("admob_reward");*/


        String TAG = "Firebase";
/*
        typeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);

                MainActivity.type = "ad";
                Log.d(TAG, "Type" + value);
                Log.d(TAG, "Type" + MainActivity.type);

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

        /*admob_native.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_native_id = value;
                Log.d(TAG, "Native" + value);
                Log.d(TAG, "Native" + MainActivity.admob_native_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/


       /* admob_id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_id = value;
                Log.d(TAG, "Admob ID" + value);
                Log.d(TAG, "Admob ID" + MainActivity.admob_id);
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = applicationInfo.metaData;
                    applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", MainActivity.admob_id);
                    String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d(TAG, "The saved id is " + MainActivity.admob_id);
                    Log.d(TAG, "The saved id is " + apiKey);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/


       /* admob_banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_banner_id = value;
                Log.d(TAG, "Admob Banner" + value);
                Log.d(TAG, "Admob Banner" + MainActivity.admob_banner_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
*/
        /*ad_interstitial.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_interstitial_id = value;
                Log.d(TAG, "Admob interstitial" + value);
                Log.d(TAG, "Admob interstitial" + MainActivity.admob_interstitial_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

      /*  fb_banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.fb_banner_id = value;
                Log.d(TAG, "fb_banner" + value);
                Log.d(TAG, "fb_banner" + MainActivity.fb_banner_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

      /*  fb_interstitial.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.fb_interstitial_id = value;
                Log.d(TAG, "fb_interstitial" + value);
                Log.d(TAG, "fb_interstitial" + MainActivity.fb_interstitial_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

       /* facebook_reward.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.facebook_reward_id = value;
                Log.d(TAG, "facebook_reward" + value);
                Log.d(TAG, "facebook_reward" + MainActivity.facebook_reward_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
/*

        admob_reward.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_reward = value;
                Log.d(TAG, "admob_reward" + value);
                Log.d(TAG, "admob_reward" + MainActivity.admob_reward);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
*/

       /* official_dont_change_value.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.official_dont_change_value = value;
                Log.d(TAG, "official_dont_change_value" + value);
                Log.d(TAG, "official_dont_change_value" + MainActivity.official_dont_change_value);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

       /* all_ads_on_off.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                MainActivity.all_ads_on_off = false;

                assert value != null;
                if (value.equalsIgnoreCase("on")) {
                    MainActivity.all_ads_on_off = true;
                }

                Log.d(TAG, "all_ads_on_off " + value);
                Log.d(TAG, "all_ads_on_off " + MainActivity.all_ads_on_off);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                MainActivity.all_ads_on_off = false;
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
*/
       /* remove_premium.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                MainActivity.remove_premium = false;

                assert value != null;
                if (value.equalsIgnoreCase("on")) {
                    MainActivity.remove_premium = true;
                }

                Log.d(TAG, "remove_premium " + value);
                Log.d(TAG, "remove_premium " + MainActivity.remove_premium);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                MainActivity.remove_premium = false;
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

       /* remove_all_video_ads_button.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                MainActivity.remove_all_video_ads_button = false;

                assert value != null;
                if (value.equalsIgnoreCase("on")) {
                    MainActivity.remove_all_video_ads_button = true;
                }

                Log.d(TAG, "remove_all_video_ads_button " + value);
                Log.d(TAG, "remove_all_video_ads_button " + MainActivity.remove_all_video_ads_button);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                MainActivity.remove_all_video_ads_button = false;
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/


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

