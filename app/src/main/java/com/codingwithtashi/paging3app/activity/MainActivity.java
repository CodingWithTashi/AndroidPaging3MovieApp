package com.codingwithtashi.paging3app.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.RequestManager;
import com.codingwithtashi.paging3app.adapter.MoviesAdapter;
import com.codingwithtashi.paging3app.adapter.MoviesLoadStateAdapter;
import com.codingwithtashi.paging3app.util.GridSpace;
import com.codingwithtashi.paging3app.util.MovieComparator;
import com.codingwithtashi.paging3app.util.Utils;
import com.codingwithtashi.paging3app.viewmodel.MovieViewModel;
import com.codingwithtashi.paging3app.databinding.ActivityMainBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    MovieViewModel mainActivityViewModel;
    ActivityMainBinding binding;
    MoviesAdapter moviesAdapter;
    @Inject
    RequestManager requestManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create View binding object
         binding = ActivityMainBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
         setSupportActionBar(binding.toolbar);

         if(Utils.API_KEY==null || Utils.API_KEY.isEmpty() || Utils.API_KEY.equals("YOUR_API_KEY"))
             Toast.makeText(this, "ADD YOUR  API KEY", Toast.LENGTH_SHORT).show();

        // Create new MoviesAdapter object and provide
         moviesAdapter = new MoviesAdapter(new MovieComparator(),requestManager);
        // Create ViewModel
         mainActivityViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        //set recyclerview and adapter
        initRecyclerviewAndAdapter();

        // Subscribe to to paging data
        mainActivityViewModel.moviePagingDataFlowable.subscribe(moviePagingData -> {
            // submit new data to recyclerview adapter
            moviesAdapter.submitData(getLifecycle(), moviePagingData);
        });
    }

    private void initRecyclerviewAndAdapter() {
        // Create GridlayoutManger with span of count of 2
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
       // Finally set LayoutManger to recyclerview
        binding.recyclerViewMovies.setLayoutManager(gridLayoutManager);

        // Add ItemDecoration to add space between recyclerview items
        binding.recyclerViewMovies.addItemDecoration(new GridSpace(2, 20, true));

        // set adapter
        binding.recyclerViewMovies.setAdapter(
                // This will show end user a progress bar while pages are being requested from server
                moviesAdapter.withLoadStateFooter(
                        // When we will scroll down and next page request will be sent
                        // while we get response form server Progress bar will show to end user
                        new MoviesLoadStateAdapter(v -> {
                            moviesAdapter.retry();
                        }))
        );
        //
        //moviesAdapter.addLoadStateListener();
        // set Grid span to set progress at center
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // If progress will be shown then span size will be 1 otherwise it will be 2
                return moviesAdapter.getItemViewType(position) == MoviesAdapter.LOADING_ITEM ? 1 : 2;
            }
        });

    }
}