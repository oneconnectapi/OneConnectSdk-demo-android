package com.oneconnect.demoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oneconnect.demoapp.R
import com.oneconnect.demoapp.databinding.ItemProServerBinding
import top.oneconnectapi.app.OneConnectServer

class ProServerAdapter(
    private val servers: List<OneConnectServer>,
    private val onItemClick: (OneConnectServer) -> Unit
) : RecyclerView.Adapter<ProServerAdapter.ServerViewHolder>() {

    inner class ServerViewHolder(val binding: ItemProServerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(server: OneConnectServer) {
            binding.tvServerName.text = server.serverName
            Glide.with(binding.ivFlag.context)
                .load(server.flagUrl)
                .placeholder(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivFlag)
            binding.root.setOnClickListener { onItemClick(server) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val binding = ItemProServerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        holder.bind(servers[position])
    }

    override fun getItemCount(): Int = servers.size
}
