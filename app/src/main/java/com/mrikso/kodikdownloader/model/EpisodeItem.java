package com.mrikso.kodikdownloader.model;

import java.util.Objects;

public class EpisodeItem {
    private int episode;
    private String episodeUrl;
    private boolean isChecked = false;
    private final int season;
    private final String title;

    public EpisodeItem(int season, String title) {
        this.season = season;
        this.title = title;
    }

    public int getEpisode() {
        return this.episode;
    }

    public String getTitle() {
        return title;
    }

    public int getSeason() {
        return season;
    }
    public String getEpisodeUrl() {
        return this.episodeUrl;
    }
    
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
    }

    public String getEpisodeId(){
        return String.format("%d_%d", season, episode);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EpisodeItem that = (EpisodeItem) o;
        return episode == that.episode && isChecked == that.isChecked && season == that.season && title == that.title && Objects.equals(episodeUrl, that.episodeUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(episode, episodeUrl, isChecked, season, title);
    }
}
