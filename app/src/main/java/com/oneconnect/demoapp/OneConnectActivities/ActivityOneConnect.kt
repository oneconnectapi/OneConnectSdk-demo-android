package com.oneconnect.demoapp.OneConnectActivities


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.facebook.ads.AdView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.oneconnect.demoapp.SubscriptionId
import com.oneconnect.demoapp.R
import com.oneconnect.demoapp.Utils.Constants
import com.oneconnect.demoapp.ui.SideBarActivity
import es.dmoral.toasty.Toasty
import java.net.Inet4Address
import java.net.NetworkInterface


abstract class ActivityOneConnect : SideBarActivity() {

    var lottieAnimationView: LottieAnimationView? = null
    var vpnToastCheck = true
    private val adCount = 0

    private val customHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L

    var tvIpAddress: TextView? = null

    var tvUploadUnit: TextView? = null
    var tvDownloadUnit: TextView? = null
    var btnOpenServerList: ConstraintLayout? = null
    var timerTextView: TextView? = null
    var btnConnect: ConstraintLayout? = null
    var btnPower: ImageView? = null
    var btnPower2: ImageView? = null
    var connectionStateOff: TextView? = null
    var connectionStateOn: TextView? = null
    var connectionStatus: TextView? = null
    var bglayout: LinearLayout? = null
    var btnbg3: View? = null
    var btnbg2: View? = null

    var bg_changed: Boolean? = false

    var nativeAdLayout: NativeAdLayout? = null
    var frameLayout: RelativeLayout? = null
    private var mInterstitialAdMob: com.google.android.gms.ads.interstitial.InterstitialAd? = null
    private var loadingAd: Boolean? = false

    @JvmField
    var imgFlag: ImageView? = null

    @JvmField
    var flagName: TextView? = null

    var facebookAdView: AdView? = null
    private var nativeAd: NativeAd? = null

    @JvmField
    var mInterstitialAd: InterstitialAd? = null

    @JvmField
    var facebookInterstitialAd: InterstitialAd? = null

    private var STATUS: String? = "DISCONNECTED"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        btnOpenServerList = findViewById(R.id.btnOpenServerList)
        timerTextView = findViewById(R.id.tv_timer)
        btnConnect = findViewById(R.id.btnConnect)
        connectionStatus = findViewById(R.id.tvConnectionStatus)
        connectionStateOff = findViewById(R.id.connection_state_off)
        connectionStateOn = findViewById(R.id.connection_state_on)
        imgFlag = findViewById(R.id.flag_image)
        flagName = findViewById(R.id.flag_name)
        btnPower = findViewById(R.id.connect_btn)
        btnPower2 = findViewById(R.id.connect_btn2)
        bglayout = findViewById(R.id.bglayout)
        btnbg2 = findViewById(R.id.view2)
        btnbg3 = findViewById(R.id.view3)
        frameLayout = findViewById(R.id.fl_adplaceholder)
        nativeAdLayout = findViewById(R.id.native_ad_container)

        btnConnect?.setOnClickListener {
            btnConnectDisconnect()
        }





        lottieAnimationView = findViewById(R.id.animation_view)

        btnOpenServerList?.setOnClickListener {
            if(Constants.FREE_SERVERS != "" && Constants.PREMIUM_SERVERS != "")
                showServerList()
            else
                showMessage("Loading servers. Please try again", "")
        }
        if (canShowAd) {

            if (MainActivity.type == "ad") {
                MobileAds.initialize(
                    this
                ) { initializationStatus ->
                    val statusMap = initializationStatus.adapterStatusMap
                    for (adapterClass in statusMap.keys) {
                        val status = statusMap[adapterClass]
                        Log.d(
                            "MyApp", String.format(
                                "Adapter name: %s, Description: %s, Latency: %d",
                                adapterClass, status!!.description, status!!.latency
                            )
                        )
                    }

                    val adLoader = AdLoader.Builder(this, MainActivity.admob_native_id)
                        .forNativeAd { nativeAd ->
                            frameLayout!!.visibility = View.VISIBLE
                            val adView = layoutInflater
                                .inflate(R.layout.native_layout, null) as NativeAdView
                            if (!SubscriptionId.OneConnectSubTwo && !SubscriptionId.OneConnectSubThree) {
                                populateUnifiedNativeAdView(nativeAd, adView)
                                frameLayout!!.removeAllViews()
                                frameLayout!!.addView(adView)
                            }
                        }
                        .withAdListener(object : AdListener() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                           }
                        })
                        .withNativeAdOptions(
                            NativeAdOptions.Builder()
                                    .build()
                        )
                        .build()

                    adLoader.loadAd(
                        AdRequest.Builder()
                            .build()
                    )


                }

            } else {
                val nativeAd: NativeAd = NativeAd(this, MainActivity.fb_banner_id)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {}
                    override fun onError(ad: Ad, adError: AdError) {
                        Log.w("AdLoader", MainActivity.fb_banner_id)
                        Log.w("AdLoader", "onAdFailedToLoad" + adError.errorMessage)
                    }

                    override fun onAdLoaded(ad: Ad) {
                        if (nativeAd == null || nativeAd !== ad) {
                            return
                        }
                        nativeAd.unregisterView()
                        if (!SubscriptionId.OneConnectSubTwo && !SubscriptionId.OneConnectSubThree) {
                            nativeAdLayout!!.setVisibility(View.VISIBLE)
                        }
                        val inflater = LayoutInflater.from(this@ActivityOneConnect)
                        val adView = inflater.inflate(
                            R.layout.native_banner_ad_layout,
                            nativeAdLayout,
                            false
                        ) as LinearLayout
                        nativeAdLayout!!.addView(adView)
                        val adChoicesContainer: LinearLayout =
                            nativeAdLayout!!.findViewById<LinearLayout>(R.id.ad_choices_container)
                        val adOptionsView = AdOptionsView(this@ActivityOneConnect, nativeAd, nativeAdLayout)
                        adChoicesContainer.removeAllViews()
                        adChoicesContainer.addView(adOptionsView, 0)
                        val nativeAdIcon: com.facebook.ads.MediaView =
                            adView.findViewById(R.id.native_ad_icon)
                        val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
                        val nativeAdMedia: com.facebook.ads.MediaView =
                            adView.findViewById(R.id.native_ad_media)
                        val nativeAdSocialContext =
                            adView.findViewById<TextView>(R.id.native_ad_social_context)
                        val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
                        val sponsoredLabel =
                            adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                        val nativeAdCallToAction =
                            adView.findViewById<Button>(R.id.native_ad_call_to_action)
                        nativeAdTitle.text = nativeAd.advertiserName
                        nativeAdBody.text = nativeAd.adBodyText
                        nativeAdSocialContext.text = nativeAd.adSocialContext
                        nativeAdCallToAction.visibility =
                            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                        nativeAdCallToAction.text = nativeAd.adCallToAction
                        sponsoredLabel.text = nativeAd.sponsoredTranslation
                        val clickableViews: MutableList<View> = ArrayList()
                        clickableViews.add(nativeAdTitle)
                        clickableViews.add(nativeAdCallToAction)
                        nativeAd.registerViewForInteraction(
                            adView, nativeAdMedia, nativeAdIcon, clickableViews
                        )
                    }

                    override fun onAdClicked(ad: Ad) {}
                    override fun onLoggingImpression(ad: Ad) {}
                }
                nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build()
                )
            }
        }
    }

    private fun showIP() {
        val queue = Volley.newRequestQueue(this)
        val urlip = "https://checkip.amazonaws.com/"

        val stringRequest =
            StringRequest(Request.Method.GET, urlip, { response ->

                val result = response.replace("\n","")
                tvIpAddress?.setText(result)
            })
            { e ->
                run {
                    Log.d("IP ERROR: ", e.message.toString())
                    tvIpAddress?.text = getIpv4HostAddress()
                }
            }
        queue.add(stringRequest)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAd!!.destroy()
        }
        super.onDestroy()
    }

    private fun btnConnectDisconnect() {
        if (STATUS != "DISCONNECTED") {
            disconnectAlert()
        } else {
            if (!Utility.isOnline(applicationContext)) {
                showMessage("No Internet Connection", "error")
            } else {
                checkSelectedCountry()
            }
        }
    }

    fun showInterstitialAndConnect() {

        if (MainActivity.all_ads_on_off && !SubscriptionId.OneConnectSubOne && !SubscriptionId.OneConnectSubThree && !SubscriptionId.OneConnectSubTwo) {
            if (MainActivity.type == "ad") {
                if(loadingAd == false) {

                    loadingAd = true
                    val adRequest = AdRequest.Builder().build()

                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ActivityOneConnect,
                        MainActivity.admob_interstitial_id,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                                mInterstitialAdMob = interstitialAd
                                Log.i("INTERSTITIAL", "onAdLoaded")
                                loadingAd = false

                                if (mInterstitialAdMob != null) {

                                    mInterstitialAdMob!!.show(this@ActivityOneConnect)

                                    mInterstitialAdMob!!.setFullScreenContentCallback(object :
                                        FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                              Log.d("TAG", "The ad was dismissed.")
                                            prepareVpn()
                                        }

                                        fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                             Log.d("TAG", "The ad failed to show.")
                                            prepareVpn()
                                        }

                                        override fun onAdShowedFullScreenContent() {
                                             mInterstitialAdMob = null
                                            Log.d("TAG", "The ad was shown.")
                                        }
                                    })
                                } else {
                                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                    prepareVpn()
                                }
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                  Log.i("INTERSTITIAL", loadAdError.message)
                                loadingAd = false
                                mInterstitialAdMob = null
                            }
                        })

                }

            } else if (MainActivity.type == "start") { } else {
                AdSettings.addTestDevice("fd37f950-1634-46f1-9691-3b8a4c96ab70")
                AudienceNetworkAds.initialize(this@ActivityOneConnect)

                val interstitialAdListener: InterstitialAdListener =
                    object : InterstitialAdListener {
                        override fun onInterstitialDisplayed(ad: Ad) {}
                        override fun onInterstitialDismissed(ad: Ad) {
                            prepareVpn()
                        }

                        override fun onError(
                            ad: Ad,
                            adError: com.facebook.ads.AdError
                        ) {
                        }

                        override fun onAdLoaded(ad: Ad) {
                            facebookInterstitialAd!!.show()
                        }

                        override fun onAdClicked(ad: Ad) {}
                        override fun onLoggingImpression(ad: Ad) {}
                    }
                if (MainActivity.all_ads_on_off && !SubscriptionId.OneConnectSubOne && !SubscriptionId.OneConnectSubThree && !SubscriptionId.OneConnectSubTwo) {
                    if (facebookInterstitialAd!!.isAdLoaded) {
                        facebookInterstitialAd!!.show()
                    } else {
                        facebookInterstitialAd = InterstitialAd(
                            this@ActivityOneConnect,
                            MainActivity.fb_interstitial_id
                        )
                        facebookInterstitialAd!!.loadAd(
                            facebookInterstitialAd!!.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener).build()
                        )
                        prepareVpn()
                    }
                } else {
                    prepareVpn()
                }
            }
        } else {
            prepareVpn()
        }
    }

    protected abstract fun checkSelectedCountry()
    protected abstract fun prepareVpn()
    protected abstract fun disconnectFromVnp()

    protected fun updateUI(status:String) {

        when (status) {
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                connectionOff()
                btnConnect!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
                btnPower!!.visibility = View.VISIBLE
                btnPower2!!.visibility = View.GONE

                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOn!!.visibility = View.INVISIBLE
                lottieAnimationView!!.visibility = View.INVISIBLE
                connectionStatus!!.visibility = View.INVISIBLE


                showIP()
            }
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                btnConnect!!.isEnabled = true
                connectionOn()
                timer()
                timerTextView!!.visibility = View.VISIBLE
                btnPower!!.visibility = View.GONE
                btnPower2!!.visibility = View.VISIBLE

                connectionStateOff!!.visibility = View.INVISIBLE
                connectionStateOn!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.INVISIBLE
                connectionStatus!!.visibility = View.INVISIBLE

                showIP()
            }
            "WAIT" -> {
                STATUS = "WAITING"
                connectionStateOff!!.visibility = View.INVISIBLE
                connectionStateOn!!.visibility = View.INVISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE
                connectionStatus!!.visibility = View.VISIBLE
                connectionStatus!!.text = "Please wait"
            }
            "AUTH" -> {
                STATUS = "AUTHENTICATION"
                connectionStateOff!!.visibility = View.INVISIBLE
                connectionStateOn!!.visibility = View.INVISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE
                connectionStatus!!.visibility = View.VISIBLE
                connectionStatus!!.text = "Authenticating"
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                connectionStateOff!!.visibility = View.INVISIBLE
                connectionStateOn!!.visibility = View.INVISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE
                connectionStatus!!.visibility = View.VISIBLE
                connectionStatus!!.text = "Reconnecting"
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                connectionOff()
                btnConnect!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
                btnPower!!.visibility = View.VISIBLE
                btnPower2!!.visibility = View.GONE

                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOn!!.visibility = View.INVISIBLE
                lottieAnimationView!!.visibility = View.INVISIBLE
                connectionStatus!!.visibility = View.INVISIBLE



            }
            "LOAD" -> {
                STATUS = "LOAD"
                connectionStateOff!!.visibility = View.INVISIBLE
                connectionStateOn!!.visibility = View.INVISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE
                connectionStatus!!.visibility = View.VISIBLE
                connectionStatus!!.text = "Connecting"
            }
        }
    }

    protected fun connectionOff() {
        connectionStateOff!!.visibility = View.VISIBLE
        connectionStateOn!!.visibility = View.GONE
        btnbg2?.setBackgroundResource(R.drawable.connect_btn2)
        btnbg3?.setBackgroundResource(R.drawable.connect_btn3)

        if(bg_changed == true) {
            bg_changed = false
            val backgrounds = arrayOfNulls<Drawable>(2)
            backgrounds[0] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg2, null)
            backgrounds[1] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)

            val crossfade = TransitionDrawable(backgrounds)

            bglayout?.background = crossfade
            crossfade.startTransition(1000)
        }
    }

    protected fun connectionOn() {
        connectionStateOff!!.visibility = View.GONE
        connectionStateOn!!.visibility = View.VISIBLE
        btnbg2?.setBackgroundResource(R.drawable.connect_btn2_on)
        btnbg3?.setBackgroundResource(R.drawable.connect_btn3_on)

        if(bg_changed == false) {
            bg_changed = true
            val backgrounds = arrayOfNulls<Drawable>(2)
            backgrounds[0] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)
            backgrounds[1] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg2, null)

            val crossfade = TransitionDrawable(backgrounds)

            bglayout?.background = crossfade
            crossfade.startTransition(1000)
        }
    }

    protected fun showMessage(msg: String?, type:String) {

        if(type == "success") {
            Toasty.success(
                this@ActivityOneConnect,
                msg + "",
                Toast.LENGTH_SHORT
            ).show()
        } else if (type == "error") {
            Toasty.error(
                this@ActivityOneConnect,
                msg + "",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toasty.normal(
                this@ActivityOneConnect,
                msg + "",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    protected fun timer() {
        if (adCount == 0) {
            startTime = SystemClock.uptimeMillis()
            customHandler.postDelayed(updateTimerThread, 0)
            timeSwapBuff += timeInMilliseconds
        }
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            val hrs = mins / 60
            secs = secs % 60
            val milliseconds = (updatedTime % 1000).toInt()
            timerTextView!!.text = (String.format("%02d", hrs) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs))
            customHandler.postDelayed(this, 0)

        }
    }

    protected fun disconnectAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Do you want to disconnect?")
        builder.setPositiveButton(
            "Disconnect"
        ) { _, _ ->
            disconnectFromVnp()
            STATUS = "DISCONNECTED"


            showMessage("Server Disconnected", "success")
        }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            showMessage("VPN Remains Connected", "success")
        }
        builder.show()
    }


    private fun populateUnifiedNativeAdView(
        nativeAd:com.google.android.gms.ads.nativead.NativeAd,
        adView:NativeAdView
    ) {
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView!!.bodyView.visibility = View.INVISIBLE
        } else {
            adView!!.bodyView.visibility = View.VISIBLE
            (adView!!.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView!!.callToActionView.visibility = View.INVISIBLE
        } else {
            adView!!.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView!!.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd!!.icon.drawable
            )
            adView!!.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView!!.priceView.visibility = View.INVISIBLE
        } else {
            adView!!.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView!!.storeView.visibility = View.INVISIBLE
        } else {
            adView!!.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView!!.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView!!.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView!!.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView!!.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }


    private fun refreshAd() {
        val adLoader = AdLoader.Builder(this, MainActivity.admob_native_id)
            .forNativeAd { nativeAd ->
                frameLayout!!.visibility = View.VISIBLE
                val adView = layoutInflater
                    .inflate(R.layout.native_layout, null) as NativeAdView
                if (!SubscriptionId.OneConnectSubTwo && !SubscriptionId.OneConnectSubThree) {
                    populateUnifiedNativeAdView(nativeAd, adView)
                    frameLayout!!.removeAllViews()
                    frameLayout!!.addView(adView)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()

        adLoader.loadAd(
            AdRequest.Builder()
                .build()
        )
    }

    private fun showServerList() {
        startActivity(Intent(this, Servers::class.java))
    }

    open fun updateConnectionStatus(
        duration: String?,
        lastPacketReceive: String?,
        byteIn: String,
        byteOut: String
    ) {
        val byteinKb = byteIn.split("-").toTypedArray()[1]
        val byteoutKb = byteOut.split("-").toTypedArray()[1]

        val uploadSpeed = byteoutKb.split(" ").toTypedArray()[1]
        val uploadUnit = byteoutKb.split(" ").toTypedArray()[2]

        val downloadSpeed = byteinKb.split(" ").toTypedArray()[1]
        val downloadUnit = byteinKb.split(" ").toTypedArray()[2]





    }


    fun updateSubscription() {
        if (MainActivity.all_ads_on_off && !SubscriptionId.OneConnectSubOne && !SubscriptionId.OneConnectSubThree && !SubscriptionId.OneConnectSubTwo) {
            Log.d("UPDATESUBS", "onStart----: ")


            when (MainActivity.type) {
                "ad" -> {
                    refreshAd()
                }
                "start" -> {

                    val mrecParameters = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT)
                    mrecParameters.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    mrecParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)


                }
                else -> {

                    val nativeAd: NativeAd = NativeAd(this, MainActivity.fb_banner_id)
                    val nativeAdListener: NativeAdListener = object : NativeAdListener {
                        override fun onMediaDownloaded(ad: Ad) {}
                        override fun onError(ad: Ad, adError: AdError) {
                            Log.w("AdLoader", MainActivity.fb_banner_id)
                            Log.w("AdLoader", "onAdFailedToLoad" + adError.errorMessage)
                        }

                        override fun onAdLoaded(ad: Ad) {
                            if (nativeAd == null || nativeAd !== ad) {
                                return
                            }
                            nativeAd.unregisterView()
                            if (!SubscriptionId.OneConnectSubTwo && !SubscriptionId.OneConnectSubThree) {
                                nativeAdLayout!!.visibility = View.VISIBLE
                            }
                            val inflater = LayoutInflater.from(this@ActivityOneConnect)
                            val adView = inflater.inflate(
                                R.layout.native_banner_ad_layout,
                                nativeAdLayout,
                                false
                            ) as LinearLayout
                            nativeAdLayout!!.addView(adView)
                            val adChoicesContainer: LinearLayout =
                                this@ActivityOneConnect.findViewById<LinearLayout>(R.id.ad_choices_container)
                            val adOptionsView =
                                AdOptionsView(this@ActivityOneConnect, nativeAd, nativeAdLayout)
                            adChoicesContainer.removeAllViews()
                            adChoicesContainer.addView(adOptionsView, 0)
                            val nativeAdIcon: com.facebook.ads.MediaView =
                                adView.findViewById(R.id.native_ad_icon)
                            val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
                            val nativeAdMedia: com.facebook.ads.MediaView =
                                adView.findViewById(R.id.native_ad_media)
                            val nativeAdSocialContext =
                                adView.findViewById<TextView>(R.id.native_ad_social_context)
                            val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
                            val sponsoredLabel =
                                adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                            val nativeAdCallToAction =
                                adView.findViewById<Button>(R.id.native_ad_call_to_action)
                            nativeAdTitle.text = nativeAd.advertiserName
                            nativeAdBody.text = nativeAd.adBodyText
                            nativeAdSocialContext.text = nativeAd.adSocialContext
                            nativeAdCallToAction.visibility =
                                if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                            nativeAdCallToAction.text = nativeAd.adCallToAction
                            sponsoredLabel.text = nativeAd.sponsoredTranslation
                            val clickableViews: MutableList<View> = ArrayList()
                            clickableViews.add(nativeAdTitle)
                            clickableViews.add(nativeAdCallToAction)
                            nativeAd.registerViewForInteraction(
                                adView, nativeAdMedia, nativeAdIcon, clickableViews
                            )
                        }

                        override fun onAdClicked(ad: Ad) {}
                        override fun onLoggingImpression(ad: Ad) {}
                    }
                    nativeAd!!.loadAd(
                        nativeAd!!.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build()
                    )

                }
            }
        } else {
            Log.e(TAG, "onStart: ")
        }
    }

    private val canShowAd: Boolean
        get() = MainActivity.all_ads_on_off &&
                !SubscriptionId.OneConnectSubOne &&
                !SubscriptionId.OneConnectSubThree &&
                !SubscriptionId.OneConnectSubTwo


    companion object {
        protected val TAG = MainActivity::class.java.simpleName
    }

    fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }
}