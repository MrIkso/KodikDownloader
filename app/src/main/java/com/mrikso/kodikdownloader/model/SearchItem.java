package com.mrikso.kodikdownloader.model;

import java.util.List;
import java.util.Map;

public class SearchItem {
    private boolean isSerial;
    private String title;
    private int season;
    private String url;
    private List<EpisodeItem> episodes;

    public SearchItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public boolean getIsSerial() {
        return this.isSerial;
    }

    public void setIsSerial(boolean isSerial) {
        this.isSerial = isSerial;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<EpisodeItem> getEpisodes() {
        return this.episodes;
    }

    public void setEpisodes(List<EpisodeItem> episodes) {
        this.episodes = episodes;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSeason() {
        return this.season;
    }

    public void setSeason(int season) {
        this.season = season;
    }
}
