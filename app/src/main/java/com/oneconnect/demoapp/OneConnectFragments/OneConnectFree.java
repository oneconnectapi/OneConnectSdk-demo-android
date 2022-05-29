package com.oneconnect.demoapp.OneConnectFragments;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.oneconnect.demoapp.OneConnectActivities.MainActivity;
import com.oneconnect.demoapp.R;
import com.oneconnect.demoapp.OneConnectAdapter.ServerFreeAdapter;
import com.oneconnect.demoapp.SubscriptionId;
import com.oneconnect.demoapp.Utils.Constants;
import com.oneconnect.demoapp.OneConnectModel.Countries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class OneConnectFree extends Fragment implements ServerFreeAdapter.RegionListAdapterInterface {
    private RecyclerView recyclerView;
    private ServerFreeAdapter adapter;
    private ArrayList<Countries> countryArrayList;
    private OneConnectPro.RegionChooserInterface regionChooserInterface;
    int server;
    InterstitialAd mInterstitialAd;
    public com.facebook.ads.InterstitialAd facebookInterstitialAd;

    boolean isAds;
    private RelativeLayout animationHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pro_server, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countryArrayList = new ArrayList<>();
        animationHolder = view.findViewById(R.id.animation_layout);

        adapter = new ServerFreeAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        if (MainActivity.all_ads_on_off && getResources().getBoolean(R.bool.fb_list) && (!SubscriptionId.OneConnectSubOne && !SubscriptionId.OneConnectSubThree&& !SubscriptionId.OneConnectSubTwo)) {
            isAds = true;
        } else if (MainActivity.all_ads_on_off && getResources().getBoolean(R.bool.ad_list) && (!SubscriptionId.OneConnectSubOne && !SubscriptionId.OneConnectSubThree && !SubscriptionId.OneConnectSubTwo)) {

            isAds = true;
        } else {

            isAds = false;

        }
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
            JSONArray jsonArray = new JSONArray(Constants.FREE_SERVERS);
            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Countries(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));

                if((i % 2 == 0)&&(i > 0)){
                    if (!SubscriptionId.OneConnectSubTwo && !SubscriptionId.OneConnectSubThree) {
                        servers.add(null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        animationHolder.setVisibility(View.GONE);
        adapter.setData(servers);
    }

    @Override
    public void onCountrySelected(Countries item) {
        if(isAds) {
            if (MainActivity.type.equals("ad")) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                }
            } else if (MainActivity.type.equals("start")) {



            } else {
                if (facebookInterstitialAd != null) {
                    if (facebookInterstitialAd.isAdLoaded()) {
                        facebookInterstitialAd.show();

                    } else {
                        com.facebook.ads.InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                Log.d("ADerror", adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                facebookInterstitialAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(OneConnectFree.this.getContext(), MainActivity.fb_interstitial_id);
                        facebookInterstitialAd.loadAd(facebookInterstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
                    }
                }
            }
        }

        regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof OneConnectPro.RegionChooserInterface) {
            regionChooserInterface = (OneConnectPro.RegionChooserInterface) ctx;
        }
    }
}
