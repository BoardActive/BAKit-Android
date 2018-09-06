package com.boardactive.sdk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.boardactive.sdk.R;


/**
 * @company BoardActive Corporation
 * @author Thomas Powell
 */
public class AdDropVertAdapter extends RecyclerView.Adapter<AdDropVertAdapter.CustomViewHolder> {

    private List<AdDrop> dataList;
    private Context context;

    public AdDropVertAdapter(Context context, List<AdDrop> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewDescription;

        private ImageView imageView;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            textViewTitle = (TextView) mView.findViewById(R.id.textView3);
            textViewCategory = (TextView) mView.findViewById(R.id.textView4);
            textViewDescription = (TextView) mView.findViewById(R.id.textView5);
            imageView = (ImageView) mView.findViewById(R.id.imageView);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custom_vert_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.textViewTitle.setText(dataList.get(position).title);
        holder.textViewCategory.setText(dataList.get(position).category);
        holder.textViewDescription.setText(dataList.get(position).description);

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(dataList.get(position).image_url)
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
