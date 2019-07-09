package com.htetaunghlaing.mm.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.htetaunghlaing.mm.R;
import com.htetaunghlaing.mm.model.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {
    private Context context;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailersList) {
        this.context = context;
        this.trailerList = trailersList;

    }

    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.trailer_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.title.setText(trailerList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titletrailer);
            thumbnail = view.findViewById(R.id.imageview_youtube);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Trailer clickedDataItem = trailerList.get(pos);
                        String videoId = trailerList.get(pos).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                        intent.putExtra("VIDEO_ID", videoId);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }
}
