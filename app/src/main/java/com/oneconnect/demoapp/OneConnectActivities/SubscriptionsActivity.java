package com.oneconnect.demoapp.OneConnectActivities;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */


import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;
import com.oneconnect.demoapp.R;
import com.oneconnect.demoapp.SubscriptionId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionsActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingClientStateListener {

    private BillingClient billingClient;
    private final Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            SubscriptionId.ALL_MONTH,
            SubscriptionId.THREE_MONTH,
            SubscriptionId.SIX_MONTH,
            SubscriptionId.TWELVE_MONTH));

    private final int[] images = {
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo};

    private final int[] prices = {
            R.string.one_month,
            R.string.three_months,
            R.string.six_months,
            R.string.twelve_months};

    private final int[] month = {
            R.string.one_month_txt,
            R.string.three_months_txt,
            R.string.six_months_txt,
            R.string.twelve_months_txt};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription);

        CarouselView carouselView = findViewById(R.id.carouselView);

        carouselView.setSize(images.length);
        carouselView.setResource(R.layout.subscription_item);
        carouselView.setAutoPlay(false);
        carouselView.enableSnapping(true);
        carouselView.setIndicatorUnselectedColor(getResources().getColor(R.color.lighterprimary));
        carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
        carouselView.setCarouselOffset(OffsetType.CENTER);
        carouselView.setCarouselViewListener((view, position) -> {


            TextView tvPrice = view.findViewById(R.id.tvPrice);
            TextView tvMonth = view.findViewById(R.id.tvMonth);
            TextView btnBuyNow = view.findViewById(R.id.btnBuyNow);


            tvPrice.setText(getResources().getString(prices[position]));
            tvMonth.setText(getResources().getString(month[position]));

            btnBuyNow.setOnClickListener(view1 -> {

                SkuDetails skuDetails = null;

                switch (position) {
                    case 0:
                        skuDetails = skusWithSkuDetails.get(SubscriptionId.ALL_MONTH);
                        break;
                    case 1:
                        skuDetails = skusWithSkuDetails.get(SubscriptionId.THREE_MONTH);
                        break;
                    case 2:
                        skuDetails = skusWithSkuDetails.get(SubscriptionId.SIX_MONTH);
                        break;
                    case 3:
                        skuDetails = skusWithSkuDetails.get(SubscriptionId.TWELVE_MONTH);
                        break;
                }

                if (skuDetails != null) purchase(skuDetails);
            });
        });

        carouselView.show();

        billingClient = BillingClient
                .newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToBillingService();
    }

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
                    new ArrayList<>(allSubs)
            );
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        connectToBillingService();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {

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

    private void purchase(SkuDetails skuDetails) {
        BillingFlowParams params = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        billingClient.launchBillingFlow(this, params);
    }
}
