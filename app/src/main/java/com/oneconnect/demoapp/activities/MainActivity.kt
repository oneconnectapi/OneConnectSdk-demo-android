package com.oneconnect.demoapp.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oneconnect.demoapp.R
import com.oneconnect.demoapp.databinding.ActivityMainBinding
import top.oneconnectapi.app.OneConnectServer
import top.oneconnectapi.app.OpenVpnApi
import top.oneconnectapi.app.activities.DisconnectVPN
import top.oneconnectapi.app.api.OneConnect
import top.oneconnectapi.app.api.ServerResponseListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedServer: OneConnectServer? = null
    private var vpnStatus: String? = "DISCONNECTED"

    private val serverListLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedServer: OneConnectServer? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        result.data?.getParcelableExtra("selectedServer", OneConnectServer::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        result.data?.getParcelableExtra("selectedServer")
                    }

                this.selectedServer = selectedServer
                binding.tvCountryName.text = selectedServer?.country
                Glide.with(binding.ivFlag.context)
                    .load(selectedServer?.flagUrl)
                    .placeholder(R.drawable.ic_baseline_location_on_24)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivFlag)

                prepareVpn()
            }
        }

    private val vpnPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // User granted VPN permission
                startVpnConnection()
            } else {
                // User denied VPN permission
                Toast.makeText(this, "VPN permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Edge to edge workaround for xml layouts
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.layout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        //Intent filter to get real-time vpn stats from oneconnect library
        val filter = IntentFilter("top.oneconnectapi.app.VPN_STATS")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, filter)
        }

        //Get servers from oneconnect
        val oneConnect = OneConnect()
        oneConnect.initialize(
            this,
            "1i6oALWFpxdF46P1VAo9OB.cDE12kGDGyQQbhhEXdMs6fv0pZX"
        ) //Sample API key

        oneConnect.fetch(object : ServerResponseListener {
            override fun onSuccess(
                freeServersList: ArrayList<OneConnectServer>,
                premiumServersList: ArrayList<OneConnectServer>
            ) {

                if (freeServersList.isNotEmpty()) {
                    //Pick random server
                    selectedServer = freeServersList.random()
                    binding.tvCountryName.text = selectedServer?.country
                    Glide.with(binding.ivFlag.context)
                        .load(selectedServer?.flagUrl)
                        .placeholder(R.drawable.ic_baseline_location_on_24)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivFlag)
                }

                binding.cpIndicator.visibility = View.GONE
                binding.lytMain.visibility = View.VISIBLE

                //Pass the fetched list of servers to server list when clicked
                binding.btnServerList.setOnClickListener {
                    val intent = Intent(this@MainActivity, ServerListActivity::class.java)
                    intent.putParcelableArrayListExtra("freeServers", freeServersList)
                    intent.putParcelableArrayListExtra("proServers", premiumServersList)
                    serverListLauncher.launch(intent)
                }
            }

            override fun onFailure(e: Exception?) {
                binding.cpIndicator.visibility = View.GONE
                binding.lytMain.visibility = View.VISIBLE
                Log.e("OneConnectServer", "Failed to fetch servers", e)
            }
        })

        binding.btnConnect.setOnClickListener {
            if (vpnStatus == "DISCONNECTED")
                prepareVpn()
            else
                disconnectVPN()
        }

        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", "https://developer.oneconnect.top/privacy/")
            startActivity(intent)
        }

        binding.cpIndicator.isIndeterminate = true
    }

    private fun prepareVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            // Launch the system dialog for VPN permission
            vpnPermissionLauncher.launch(intent)
        } else {
            // Already has permission, start VPN directly
            startVpnConnection()
        }
    }

    private fun startVpnConnection() {
        Toast.makeText(this, "Starting VPN connection...", Toast.LENGTH_SHORT).show()

        if (selectedServer == null)
            return

        OpenVpnApi.startVpn(
            this@MainActivity,
            selectedServer!!.ovpnConfiguration,
            selectedServer!!.country,
            selectedServer!!.vpnUserName,
            selectedServer!!.vpnPassword
        )
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                updateUIState(intent?.getStringExtra("state"))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                var duration = intent?.getStringExtra("duration")
                var lastPacketReceive = intent?.getStringExtra("lastPacketReceive")
                var byteIn = intent?.getStringExtra("byteIn")
                var byteOut = intent?.getStringExtra("byteOut")

                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = "0.0 mB/s"
                if (byteOut == null) byteOut = "0.0 mB/s"

                binding.tvTimer.text = duration
                binding.tvDownload.text = getString(R.string.download_speed, byteIn)
                binding.tvUpload.text =  getString(R.string.upload_speed, byteOut)

                //updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUIState(vpnStatus: String?) {
        this.vpnStatus = vpnStatus

        when (vpnStatus) {
            "VPN_GENERATE_CONFIG" -> {
                binding.tvConnectionStatus.text = "Generating VPN configuration..."
            }
            "WAIT" -> {
                binding.tvConnectionStatus.text = "Waiting..."
            }
            "AUTH" -> {
                binding.tvConnectionStatus.text = "Authenticating..."
            }
            "GET_CONFIG" -> {
                binding.tvConnectionStatus.text = "Getting configuration..."
            }
            "ASSIGN_IP" -> {
                binding.tvConnectionStatus.text = "Assigning IP address..."
            }
            "RECONNECTING" -> {
                binding.tvConnectionStatus.text = "Reconnecting..."
            }
            "TCP_CONNECT" -> {
                binding.tvConnectionStatus.text = "Connecting (TCP)..."
            }
            "RESOLVE" -> {
                binding.tvConnectionStatus.text = "Resolving host..."
            }
            "NOPROCESS" -> {
                binding.tvConnectionStatus.text = "No process..."
            }
            "AUTH_FAILED" -> {
                binding.tvConnectionStatus.text = "Authentication failed"
            }
            "NONETWORK" -> {
                binding.tvConnectionStatus.text = "No network..."
            }
            "DISCONNECTED" -> {
                binding.tvTimer.visibility = View.GONE
                binding.ivPower.visibility = View.VISIBLE
                binding.tvConnectionStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_baseline_signal_cellular_alt_24_off,
                    0
                )

                binding.tvConnectionStatus.text = "Disconnected"
            }
            "EXITING" -> {
                binding.tvConnectionStatus.text = "Exiting..."
            }
            "CONNECTED" -> {
                binding.tvTimer.visibility = View.VISIBLE
                binding.ivPower.visibility = View.GONE
                binding.tvConnectionStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_baseline_signal_cellular_alt_24,
                    0
                )

                binding.tvConnectionStatus.text = "Connected"
            }
        }
    }

    private fun disconnectVPN() {
        val intent = Intent(this, DisconnectVPN::class.java)
        //Uncomment the line below to disconnect without alert dialog
        // intent.putExtra("instantStop", true)
        startActivity(intent)
    }

    public override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(broadcastReceiver)
            Log.d("ReceiverLifecycle", "broadcastReceiver unregistered")
        } catch (e: IllegalArgumentException) {
            Log.w("ReceiverLifecycle", "broadcastReceiver not registered: " + e.message)
        }
    }
}
