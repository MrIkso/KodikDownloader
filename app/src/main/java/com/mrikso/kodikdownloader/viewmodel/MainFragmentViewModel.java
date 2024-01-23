package com.mrikso.kodikdownloader.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchResultModel;
import com.mrikso.kodikdownloader.repository.KodikRepository;
import com.mrikso.kodikdownloader.service.KodikVideosService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainFragmentViewModel extends ViewModel {
    private final String TAG = "MainFragmentViewModel";
    private final KodikRepository kodikRepository;
    private final KodikVideosService kodikVideosService;
    private LiveData<SearchResultModel> searchResults;
    private final MutableLiveData<Map<String, String>> mapOfVideoLinks = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, Map<String, String>>> mapOfAllVideoLinks =
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
                .execute(
                        () -> {
                            mapOfVideoLinks.postValue(kodikVideosService.getVideosMap(url));
                        });
    }

    public void loadVideos(Map<Integer, String> seasonEpisodes) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                mapOfAllVideoLinks.postValue(batchDownload(seasonEpisodes));
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                        });
    }

    public void loadVideos(List<EpisodeItem> seasonEpisodes) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                Map<Integer, String> itemMap =
                                        seasonEpisodes.stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                EpisodeItem::getEpisode,
                                                                item -> item.getEpisodeUrl()));

                                mapOfAllVideoLinks.postValue(batchDownload(itemMap));
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                        });
    }

    public LiveData<Map<String, String>> getVideosMap() {
        return mapOfVideoLinks;
    }

    public LiveData<Map<Integer, Map<String, String>>> getAllVideosMap() {
        return mapOfAllVideoLinks;
    }

    // возвращает мапу из эпизодами и ссылкали на видео в разных качествах
    private Map<Integer, Map<String, String>> batchDownload(Map<Integer, String> seasonEpisodes)
            throws InterruptedException {
        Map<Integer, Map<String, String>> episodesVideos = new HashMap<>(seasonEpisodes.size());

        for (Map.Entry<Integer, String> ep : seasonEpisodes.entrySet()) {
            Log.i(TAG, ep.getKey() + " " + ep.getValue());
            episodesVideos.put(ep.getKey(), kodikVideosService.getVideosMap(ep.getValue()));
            Thread.sleep(100);
        }
        return episodesVideos;
    }
}
