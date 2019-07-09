package com.htetaunghlaing.mm.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.htetaunghlaing.mm.DetailActivity;
import com.htetaunghlaing.mm.R;
import com.htetaunghlaing.mm.model.Movie;

import java.security.PublicKey;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.title.setText(movieList.get(i).getOriginalTitle());
        String vote = Double.toString((movieList.get(i).getVoteAverage()));
        viewHolder.userrating.setText(vote);

        //String poster="https://image.tmdb.org/t/p/w500"+ movieList.get(i).getPosterPath();
        Glide.with(context).load(movieList.get(i).getPosterPath())
                .placeholder(R.drawable.load)
                .into(viewHolder.loadimage);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, userrating;
        public ImageView loadimage;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            userrating = (TextView) view.findViewById(R.id.userrating);
            loadimage = (ImageView) view.findViewById(R.id.loadthumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("original_title", movieList.get(pos).getOriginalTitle());
                        intent.putExtra("id", movieList.get(pos).getId());
                        intent.putExtra("poster_path", movieList.get(pos).getPosterPath());
                        intent.putExtra("overview", movieList.get(pos).getOverview());
                        intent.putExtra("vote_average", Double.toString(Double.parseDouble(String.valueOf(movieList.get(pos).getVoteAverage()))));
                        intent.putExtra("release_date", movieList.get(pos).getReleaseDate());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }
                }
            });
        }
    }

}
