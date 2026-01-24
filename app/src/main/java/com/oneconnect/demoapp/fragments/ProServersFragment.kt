package com.oneconnect.demoapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oneconnect.demoapp.R
import com.oneconnect.demoapp.adapter.ProServerAdapter
import top.oneconnectapi.app.OneConnectServer

class ProServersFragment : Fragment() {

    private var servers: ArrayList<OneConnectServer>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        servers =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().intent.getParcelableArrayListExtra("proServers", OneConnectServer::class.java)
            } else {
                @Suppress("DEPRECATION")
                requireActivity().intent.getParcelableArrayListExtra("proServers")
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_free_servers, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerServers)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        servers?.let {
            val adapter = ProServerAdapter(servers!!) { selectedServer ->
                val resultIntent = Intent().apply {
                    putExtra("selectedServer", selectedServer) // pass full Parcelable object
                }
                requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                requireActivity().finish()
            }
            recyclerView.adapter = adapter
        }

        return view
    }
}