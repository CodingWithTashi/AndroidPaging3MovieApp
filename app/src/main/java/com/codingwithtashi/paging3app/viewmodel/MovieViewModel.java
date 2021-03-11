package com.codingwithtashi.paging3app.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.codingwithtashi.paging3app.model.Movie;
import com.codingwithtashi.paging3app.paging.MoviePagingSource;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;
public class MovieViewModel extends ViewModel {
    // Define Flowable for movies
    public Flowable<PagingData<Movie>> moviePagingDataFlowable;
    public MovieViewModel() {
        init();
    }
    // Init ViewModel Data
    private void init() {
        // Define Paging Source
        MoviePagingSource moviePagingSource = new MoviePagingSource();

        // Create new Pager
        Pager<Integer, Movie> pager = new Pager(
                // Create new paging config
                new PagingConfig(20, //  Count of items in one page
                        20, //  Number of items to prefetch
                        false, // Enable placeholders for data which is not yet loaded
                        20, // initialLoadSize - Count of items to be loaded initially
                        20 * 499),// maxSize - Count of total items to be shown in recyclerview
                () -> moviePagingSource); // set paging source

        // inti Flowable
        moviePagingDataFlowable = PagingRx.getFlowable(pager);
        CoroutineScope coroutineScope = ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(moviePagingDataFlowable, coroutineScope);

    }
}
