package com.mrikso.kodikdownloader.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.mrikso.kodikdownloader.model.SearchResultModel;
import com.mrikso.kodikdownloader.repository.KodikRepository;
import com.mrikso.kodikdownloader.service.KodikVideosService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragmentViewModel extends ViewModel {
    private final String TAG = "MainFragmentViewModel";
    private final KodikRepository kodikRepository;
    private final KodikVideosService kodikVideosService;
    private LiveData<SearchResultModel> searchResults;
    private final MutableLiveData<Map<String, String>> mapOfVideoLinks = new MutableLiveData<>();

    public MainFragmentViewModel() {
        kodikRepository = new KodikRepository();
        kodikVideosService = new KodikVideosService();
        searchResults = kodikRepository.getResults();
    }

    public void startSearch(String name, int mode) {
        Log.i(TAG, "start search + mode " + String.valueOf(mode));
        switch (mode) {
            case 0: // title mode
                kodikRepository.getSearchByTitle(name);
                break;
            case 1: // shikimori mode
                kodikRepository.getSearchByShikimoriId(name);
                break;
            case 2: // kinopoisk mode
                kodikRepository.getSearchByKinopoiskId(name);
                break;
            case 3: // imdb mode
                kodikRepository.getSearchByIMDbId(name);
                break;
        }
    }

    public LiveData<SearchResultModel> getSearchResults() {
        return searchResults;
    }

    public void loadVideos(String url) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            mapOfVideoLinks.postValue(kodikVideosService.getVideosMap(url));
                        });
    }

    public LiveData<Map<String, String>> getVideosMap() {
        return mapOfVideoLinks;
    }
}
