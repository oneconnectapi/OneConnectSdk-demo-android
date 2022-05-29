package com.oneconnect.demoapp.OneConnectAdapter;


/**
 * https://developer.oneconnect.top/
 * @package Oneconnect SDK Project
 * @author oneconnect.top
 * @copyright May 2022
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.oneconnect.demoapp.OneConnectFragments.OneConnectPro;
import com.oneconnect.demoapp.R;
import com.oneconnect.demoapp.OneConnectModel.Countries;
import java.util.ArrayList;
import java.util.List;

public class ServerProAdapter extends RecyclerView.Adapter<ServerProAdapter.mViewhoder> {

    ArrayList<Countries> datalist = new ArrayList<>();

    private final Context context;
    private final int AD_TYPE = 0;
    private final int CONTENT_TYPE = 1;
    public ServerProAdapter(Context ctx) {
        this.context=ctx;
    }

    @NonNull
    @Override
    public ServerProAdapter.mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.pro_item, parent, false);
        return new ServerProAdapter.mViewhoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ServerProAdapter.mViewhoder holder, int position) {
        if(getItemViewType(position) == CONTENT_TYPE){
            Countries data = datalist.get(position);
            holder.app_name.setText(data.getCountry());

            Glide.with(context)
                    .load(data.getFlagUrl())
                    .into(holder.flag);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OneConnectPro.onItemClick(data);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    @Override
    public int getItemViewType(int position) {
        return CONTENT_TYPE;
    }

    public static class mViewhoder extends RecyclerView.ViewHolder
    {
        TextView app_name;
        ImageView flag;

        public mViewhoder(View itemView) {
            super(itemView);
            app_name = itemView.findViewById(R.id.region_title);
            flag = itemView.findViewById(R.id.country_flag);
        }
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Countries item);
    }
    public void setData(List<Countries> servers) {
        datalist.clear();
        datalist.addAll(servers);
        notifyDataSetChanged();
    }
}
