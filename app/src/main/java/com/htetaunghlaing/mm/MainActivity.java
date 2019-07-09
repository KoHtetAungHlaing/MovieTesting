package com.htetaunghlaing.mm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.htetaunghlaing.mm.adapter.MovieAdapter;
import com.htetaunghlaing.mm.api.Client;
import com.htetaunghlaing.mm.api.Service;
import com.htetaunghlaing.mm.data.FavoriteDBHelper;
import com.htetaunghlaing.mm.model.Movie;
import com.htetaunghlaing.mm.model.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    private AppCompatActivity activity = MainActivity.this;
    public static final String LOG_TAG = MovieAdapter.class.getName();
    private FavoriteDBHelper favoriteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //http://api.themoviedb.org/3/movie/top_rated?api_key=f6dd00ddac87d5ccf56b505157487947
        //http://api.themoviedb.org/3/movie/popular?api_key=f6dd00ddac87d5ccf56b505157487947
        //http://api.themoviedb.org/3/movie/100/videos?api_key=f6dd00ddac87d5ccf56b505157487947

        initViews();
    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initViews() {
        /*pd = new ProgressDialog(this);
        pd.setMessage("Fetching Movies......");
        pd.setCancelable(false);
        pd.show();*/

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        favoriteDBHelper = new FavoriteDBHelper(activity);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_LONG).show();

            }
        });

        checkSortOrder();
    }

    private void loadJSON() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }
            Client client = new Client();
            Service apiService =
                    client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        List<Movie> a = response.body().getResults();
                        recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), a));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        //pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Error Fetching Data!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadJSON1() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }
            Client client = new Client();
            Service apiService =
                    client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        List<Movie> a = response.body().getResults();
                        recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), a));
                        recyclerView.smoothScrollToPosition(0);
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                       // pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Error Fetching Data!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(LOG_TAG, "Preference updated!");
        checkSortOrder();
    }

    private void checkSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG, "Sorting by most popular!");
            loadJSON();
        } else if (sortOrder.equals(this.getString(R.string.favorite))) {
            Log.d(LOG_TAG, "Sorting by favorite!");
            initViews2();
        } else {
            Log.d(LOG_TAG, "Sorting by vote average");
            loadJSON1();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieList.isEmpty()) {
            checkSortOrder();
        } else {

        }
    }

    private void initViews2() {
        recyclerView = findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDBHelper = new FavoriteDBHelper(activity);
        getAllFavorite();
    }

    public void getAllFavorite() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                movieList.clear();
                movieList.addAll(favoriteDBHelper.getAllFavorite());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
