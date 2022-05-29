package com.oneconnect.demoapp.OneConnectActivities;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.bumptech.glide.Glide;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.oneconnect.demoapp.SubscriptionId;
import com.oneconnect.demoapp.OneConnectFragments.OneConnectPro;
import com.oneconnect.demoapp.R;
import com.oneconnect.demoapp.OneConnectModel.Countries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import top.oneconnectapi.app.OpenVpnApi;
import top.oneconnectapi.app.core.OpenVPNThread;


public class MainActivity extends ActivityOneConnect implements OneConnectPro.RegionChooserInterface, PurchasesUpdatedListener, BillingClientStateListener {

    private static final String CHANNEL_ID = "vpn";
    public static String facebook_reward_id = "";
    public static String admob_reward="ca-app-pub-3940256099942544/5224354917";
    public static String official_dont_change_value;
    public Countries selectedCountry = null;

    public static String type = "fb";
    public static String admob_id = "ca-app-pub-3940256099942544~3347511713";
    public static String admob_banner_id = "ca-app-pub-3940256099942544/6300978111";
    public static String admob_interstitial_id = "ca-app-pub-3940256099942544/1033173712";
    public static String admob_native_id = "ca-app-pub-3940256099942544/2247696110";
    public static String fb_banner_id = "385157572510735_385163332510159";
    public static String fb_interstitial_id = "2840783936022266_2840931686007491";
    public static boolean all_ads_on_off = false;
    public static boolean remove_premium = false;
    public static boolean remove_all_video_ads_button = false;
    private InterstitialAd mInterstitialAdMob;

    private OpenVPNThread vpnThread = new OpenVPNThread();

    private BillingClient billingClient;
    private Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            SubscriptionId.ALL_MONTH,
            SubscriptionId.THREE_MONTH,
            SubscriptionId.SIX_MONTH,
            SubscriptionId.TWELVE_MONTH));

    private void connectToBillingService() {
        if (!billingClient.isReady()) {
            billingClient.startConnection(this);
        }
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            querySkuDetailsAsync(
                    BillingClient.SkuType.SUBS,
                    allSubs
            );
            queryPurchases();
        }
        updateSubscription();
    }

    @Override
    public void onBillingServiceDisconnected() {
        connectToBillingService();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

    }

    private void queryPurchases() {
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        List<Purchase> purchases = result.getPurchasesList();
        List<String> skus = new ArrayList<>();

        if (purchases != null) {
            for (Purchase purchase : purchases) {
                skus.add(purchase.getSku());
            }

            if (skus.contains(SubscriptionId.ALL_MONTH) ||
                    skus.contains(SubscriptionId.THREE_MONTH) ||
                    skus.contains(SubscriptionId.SIX_MONTH) ||
                    skus.contains(SubscriptionId.TWELVE_MONTH)
            ) {
                SubscriptionId.OneConnectSubThree = true;
            }
        }
    }

    private void querySkuDetailsAsync(@BillingClient.SkuType String skuType, List<String> skuList) {
        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build();

        billingClient.querySkuDetailsAsync(
                params, (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        for (SkuDetails details : skuDetailsList) {
                            skusWithSkuDetails.put(details.getSku(), details);
                        }
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
            }
        });

        if (TextUtils.isEmpty(type)) {
            type = "";
        }

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(7)
                .threshold(4)
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        showMessage("Thank you for your feedback!", "");
                        ratingDialog.dismiss();
                    }
                })
                .negativeButtonText("Never")
                .negativeButtonTextColor(R.color.grey_500)
                .playstoreUrl("https://play.google.com/store/apps/details?id=" + this.getPackageName())
                .onRatingBarFormSumbit(feedback -> {}).build();

        ratingDialog.show();

        billingClient = BillingClient
                .newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToBillingService();

        Intent intent = getIntent();

        if(getIntent().getExtras() != null) {
            selectedCountry = getIntent().getExtras().getParcelable("c");
            updateUI("LOAD");
        }

        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
            admob_banner_id = intent.getStringExtra("admob_banner");
            admob_interstitial_id = intent.getStringExtra("admob_interstitial");
            fb_banner_id = intent.getStringExtra("fb_banner");
            fb_interstitial_id = intent.getStringExtra("fb_interstitial");
        }

        if (!Utility.isOnline(getApplicationContext())) {
            showMessage("No Internet Connection", "error");
        } else {
            if (selectedCountry != null) {
                showInterstitialAndConnect();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        inAppUpdate();
    }

    public void prepareVpn() {

        updateCurrentVipServerIcon(selectedCountry.getFlagUrl());
        flagName.setText(selectedCountry.getCountry());

        if (Utility.isOnline(getApplicationContext())) {

            if(selectedCountry != null) {
                Intent intent = VpnService.prepare(this);
                Log.v("CHECKSTATE", "start");

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else
                    startVpn();
            } else {
                showMessage("Please select a server first", "");
            }

        } else {
                showMessage("No Internet Connection", "error");
        }
    }

    protected void startVpn() {
        try {
            OpenVpnApi.startVpn(this, selectedCountry.getOvpn(), selectedCountry.getCountry(), selectedCountry.getOvpnUserName(), selectedCountry.getOvpnUserPassword());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updateUI(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";

                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void disconnectFromVnp() {
        try {
            vpnThread.stop();
            updateUI("DISCONNECTED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegionSelected(Countries item) {

        selectedCountry = item;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.oneconnect_main;
    }

    private void inAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, 11);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            showMessage("Start Download", "");
            if (resultCode != RESULT_OK) {
                Log.d("Update", "Update failed" + resultCode);
            }
        }

        if (resultCode == RESULT_OK) {
            startVpn();
        } else {
            showMessage("Permission Denied", "error");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VPN";
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateCurrentVipServerIcon(String serverIcon) {
        Glide.with(this)
                .load(serverIcon)
                .into(imgFlag);
    }

    public void checkSelectedCountry() {
        if (selectedCountry == null) {
            updateUI("DISCONNECT");
            showMessage("Please select a server first", "");
        } else {
            showInterstitialAndConnect();
            updateUI("LOAD");
        }
    }
}
