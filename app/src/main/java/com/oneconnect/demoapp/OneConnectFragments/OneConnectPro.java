package com.oneconnect.demoapp.OneConnectFragments;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.oneconnect.demoapp.OneConnectActivities.MainActivity;
import com.oneconnect.demoapp.R;
import com.oneconnect.demoapp.OneConnectActivities.SubscriptionsActivity;
import com.oneconnect.demoapp.OneConnectAdapter.ServerProAdapter;
import com.oneconnect.demoapp.SubscriptionId;
import com.facebook.ads.*;
import com.oneconnect.demoapp.Utils.Constants;
import com.oneconnect.demoapp.OneConnectModel.Countries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class OneConnectPro extends Fragment implements ServerProAdapter.RegionListAdapterInterface {

    private RecyclerView recyclerView;
    private ServerProAdapter adapter;
    private RegionChooserInterface regionChooserInterface;
    private static RewardedAd rewardedAd;
    private RelativeLayout animationHolder;
    private static final String TAG = "Facebok Ads";
    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;
    private RewardedAd mRewardedAd;
    private RewardedVideoAd rewardedVideoAd;
    private static SharedPreferences sharedPreferences;
    static Countries countryy;
    public static Context context;
    public static boolean viewSet = false;
    static View view;
    public static boolean fbAdIsLoading = true;
    public static boolean googleAdIsLoading = true;
    public static boolean googleAdResune = false;
    public static boolean fbAdResume = false;
    public static ProgressDialog progressdialog;
    private static PopupWindow pw;
    private static View popupView;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdSettings.addTestDevice("4cbd7f01-b2fb-4d12-ac35-f399d9f30351");
        AdSettings.addTestDevice("ad883e4f-8d84-4631-afdb-12104e62f4b8");
        AdSettings.addTestDevice("bd62c248-68dd-486b-9f83-efbe4a5d5db3");
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.free_server, container, false);

        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("! Just a moment finding best video for you !");
        progressdialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        sharedPreferences = getContext().getSharedPreferences("userRewarded",Context.MODE_PRIVATE);

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);
        mPurchaseLayout.setVisibility(View.GONE);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
                initAdMob();
            }
        });

        LayoutInflater pInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = pInflater.inflate(R.layout.layout_bottom_sheet, (ViewGroup) view, false);
        pw = new PopupWindow(popupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, true);
        pw.setAnimationStyle(R.style.Animation);
        initOnClick();

        adapter = new ServerProAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadServers();
    }

    private void loadServers() {

        ArrayList<Countries> servers = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(Constants.PREMIUM_SERVERS);

            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Countries(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setData(servers);
        animationHolder.setVisibility(View.GONE);
    }

    @Override
    public void onCountrySelected(Countries item) {

             regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Countries item);
    }

    public static void unblockServer()
    {

        TextView title = popupView.findViewById(R.id.title);
        if (!MainActivity.remove_all_video_ads_button) {
            title.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

        FrameLayout mainLayout = popupView.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> {
            pw.dismiss();
        });

        popupView.findViewById(R.id.but_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SubscriptionsActivity.class));
                pw.dismiss();
            }
        });

        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public static void onItemClick(Countries country)
    {
        countryy = country;
        if (SubscriptionId.OneConnectSubTwo || SubscriptionId.OneConnectSubThree || !MainActivity.remove_premium) {
            Intent intent=new Intent(context, MainActivity.class);
            intent.putExtra("c", country);
            intent.putExtra("type",MainActivity.type);
            intent.putExtra("admob_banner",MainActivity.admob_banner_id);
            intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
            intent.putExtra("fb_banner",MainActivity.fb_banner_id);
            intent.putExtra("fb_interstitial",MainActivity.fb_interstitial_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
        else
        {
            unblockServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        googleAdIsLoading = true;
        fbAdIsLoading = true;
        googleAdResune = false;
        fbAdResume = false;
    }

    public void initAdMob() {

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getActivity(), MainActivity.admob_reward,
            adRequest, new RewardedAdLoadCallback(){
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                    mRewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {

                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {

                            Log.d(TAG, "Ad was dismissed.");
                            mRewardedAd = null;
                        }
                    });
                }
            });
    }

    private void initOnClick() {

    }
}