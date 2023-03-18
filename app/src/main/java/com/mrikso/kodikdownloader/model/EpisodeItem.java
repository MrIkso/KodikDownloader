package com.mrikso.kodikdownloader.model;

public class EpisodeItem {
    private final int episode;
    private final String episodeUrl;

    public EpisodeItem(int episode, String episodeUrl) {
        this.episode = episode;
        this.episodeUrl = episodeUrl;
    }

    public int getEpisode() {
        return this.episode;
    }


    public String getEpisodeUrl() {
        return this.episodeUrl;
    }

}
