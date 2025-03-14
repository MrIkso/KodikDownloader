package com.mrikso.kodikdownloader.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchResultModel;
import com.mrikso.kodikdownloader.repository.KodikRepository;
import com.mrikso.kodikdownloader.service.KodikVideosService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainFragmentViewModel extends ViewModel {
    private final String TAG = "MainFragmentViewModel";
    private final KodikRepository kodikRepository;
    private final KodikVideosService kodikVideosService;
    private final LiveData<SearchResultModel> searchResults;
    private final MutableLiveData<Map<String, String>> mapOfVideoLinks = new MutableLiveData<>();
    private final MutableLiveData<Map<EpisodeItem, Map<String, String>>> mapOfAllVideoLinks =
            new MutableLiveData<>();

    public MainFragmentViewModel() {
        kodikRepository = new KodikRepository();
        kodikVideosService = new KodikVideosService();
        searchResults = kodikRepository.getResults();
    }

    public void startSearch(String name, int mode) {
        // Log.i(TAG, "start search + mode " + String.valueOf(mode));
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
                .execute(() -> mapOfVideoLinks.postValue(kodikVideosService.getVideosMap(url)));
    }

    public void loadVideos(List<EpisodeItem> seasonEpisodes) {
        Executors.newSingleThreadExecutor()
                .execute(() -> {
                    try {
                        mapOfAllVideoLinks.postValue(batchDownload(seasonEpisodes));
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                });
    }

    public LiveData<Map<String, String>> getVideosMap() {
        return mapOfVideoLinks;
    }

    public LiveData<Map<EpisodeItem, Map<String, String>>> getAllVideosMap() {
        return mapOfAllVideoLinks;
    }

    private Map<EpisodeItem, Map<String, String>> batchDownload(List<EpisodeItem> seasonEpisodes)
            throws InterruptedException {
        Map<EpisodeItem, Map<String, String>> episodesVideos = new HashMap<>(seasonEpisodes.size());
        for (EpisodeItem ep : seasonEpisodes) {
            episodesVideos.put(ep, kodikVideosService.getVideosMap(ep.getEpisodeUrl()));
            Thread.sleep(100);
        }
        return episodesVideos;
    }
}
