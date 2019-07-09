package com.htetaunghlaing.mm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.htetaunghlaing.mm.adapter.TrailerAdapter;
import com.htetaunghlaing.mm.api.Client;
import com.htetaunghlaing.mm.api.Service;
import com.htetaunghlaing.mm.data.FavoriteDBHelper;
import com.htetaunghlaing.mm.model.Movie;
import com.htetaunghlaing.mm.model.Trailer;
import com.htetaunghlaing.mm.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;
    RecyclerView recyclerViewD;
    TrailerAdapter tAdapter;
    List<Trailer> trailerList;
    private FavoriteDBHelper favoriteDBHelper;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intentThatStartedThisActivity = getIntent();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle(getIntent().getExtras().getString("original_title"));

        initCollapsingToolbar();

        imageView = findViewById(R.id.thumbnail_image_header);
        nameOfMovie = findViewById(R.id.title);
        plotSynopsis = findViewById(R.id.plotsynopsis);
        userRating = findViewById(R.id.userrating);
        releaseDate = findViewById(R.id.releasedate);
        if (intentThatStartedThisActivity.hasExtra("original_title")) {

            String thumbnail = getIntent().getExtras().getString("poster_path");
            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String dateOfRelease = getIntent().getExtras().getString("release_date");

            //String poster="https://image.tmdb.org/t/p/w500"+ thumbnail;

            Glide.with(this)
                    .load(thumbnail)
                    .placeholder(R.drawable.load)
                    .into(imageView);
            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText("Rating: "+rating);
            releaseDate.setText(dateOfRelease);
        } else {
            Toast.makeText(getApplicationContext(), "No API Data!", Toast.LENGTH_LONG).show();
        }
        MaterialFavoriteButton materialFavoriteButton = findViewById(R.id.favourite_button);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.htetaunghlaing.mm.DetailActivity", MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite Added", true);
                    editor.commit();
                    saveFavorite();
                    Snackbar.make(buttonView, "Added to Favorite", Snackbar.LENGTH_SHORT).show();
                } else {
                    int movie_id=getIntent().getExtras().getInt("id");
                    favoriteDBHelper=new FavoriteDBHelper(DetailActivity.this);
                    favoriteDBHelper.deleteFavorite(movie_id);

                    SharedPreferences.Editor editor = getSharedPreferences("com.htetaunghlaing.mm.DetailActivity", MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite Removed", true);
                    editor.commit();
                    Snackbar.make(buttonView, "Removed from Favorite", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        initViews();
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("Movie Details");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0) {
                    collapsingToolbarLayout.setTitle(getIntent().getExtras().getString("original_title"));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("Movie Details");
                    isShow = false;
                }
            }
        });
    }

    private void initViews() {
        trailerList = new ArrayList<>();
        tAdapter = new TrailerAdapter(this, trailerList);

        recyclerViewD = findViewById(R.id.recycler_view_trailer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewD.setLayoutManager(mLayoutManager);
        recyclerViewD.setAdapter(tAdapter);
        tAdapter.notifyDataSetChanged();


        loadJSON();
    }

    private void loadJSON() {
        int movie_id = getIntent().getExtras().getInt("id");
        Log.d("msg", movie_id + "");
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key", Toast.LENGTH_SHORT).show();
                return;
            }
            Client client = new Client();
            Service apService = client.getClient().create(Service.class);
            Call<TrailerResponse> call = apService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    /*List<Trailer> tra = response.body().getResults();
                    Log.d("msg",response.body().getResults().toString());
                    recyclerViewD.setAdapter(new TrailerAdapter(getApplicationContext(), tra));
                    recyclerViewD.smoothScrollToPosition(0);*/

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            List<Trailer> trailer = response.body().getResults();
                            recyclerViewD.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                            recyclerViewD.smoothScrollToPosition(0);
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("msg", t.getMessage());

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveFavorite() {
        favoriteDBHelper = new FavoriteDBHelper(activity);
        favorite = new Movie();
        int movie_id = getIntent().getExtras().getInt("id");
        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");

        favorite.setId(movie_id);
        favorite.setOriginalTitle(nameOfMovie.getText().toString().trim());
        favorite.setPosterPath(poster);
        favorite.setVoteAverage(Double.parseDouble(rate));
        favorite.setOverview(plotSynopsis.getText().toString().trim());

        favoriteDBHelper.addFavorite(favorite);
    }

}
