package com.mrikso.kodikdownloader.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class SearchResultModel {

    @SerializedName("time")
    private String time;

    @SerializedName("total")
    private int total;

    @SerializedName("results")
    private List<Results> results;

    public String getTime() {
        return time;
    }

    public int getTotal() {
        return total;
    }

    public List<Results> getResults() {
        return results;
    }

    public static class Results {

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("link")
        private String link;

        @SerializedName("title")
        private String title;

        @SerializedName("title_orig")
        private String titleOrig;

        @SerializedName("other_title")
        private String otherTitle;

        @SerializedName("translation")
        private Translation translation;

        @SerializedName("year")
        private int year;

        @SerializedName("last_season")
        private int lastSeason;

        @SerializedName("last_episode")
        private int lastEpisode;

        @SerializedName("episodes_count")
        private int episodesCount;

        @SerializedName("kinopoisk_id")
        private String kinopoiskId;

        @SerializedName("imdb_id")
        private String imdbId;

        @SerializedName("worldart_link")
        private String worldartLink;

        @SerializedName("shikimori_id")
        private String shikimoriId;

        @SerializedName("quality")
        private String quality;

        @SerializedName("camrip")
        private boolean camrip;

        @SerializedName("lgbt")
        private boolean lgbt;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("screenshots")
        private List<String> screenshots;

        @SerializedName("seasons")
        private Map<Integer, Season> seasons;

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getLink() {
            return link;
        }

        public String getTitle() {
            return title;
        }

        public String getTitleOrig() {
            return titleOrig;
        }

        public String getOtherTitle() {
            return otherTitle;
        }

        public Translation getTranslation() {
            return translation;
        }

        public int getLastSeason() {
            return lastSeason;
        }

        public Map<Integer, Season> getSeasons() {
            return seasons;
        }

        public int getLastEpisode() {
            return lastEpisode;
        }

        public int getEpisodesCount() {
            return episodesCount;
        }

        public int getYear() {
            return year;
        }

        public String getKinopoiskId() {
            return kinopoiskId;
        }

        public String getImdbId() {
            return imdbId;
        }

        public String getWorldartLink() {
            return worldartLink;
        }

        public String getShikimoriId() {
            return shikimoriId;
        }

        public String getQuality() {
            return quality;
        }

        public boolean isCamrip() {
            return camrip;
        }

        public boolean isLgbt() {
            return lgbt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public List<String> getScreenshots() {
            return screenshots;
        }

        public static class Translation {

            @SerializedName("id")
            private int id;

            @SerializedName("title")
            private String title;

            @SerializedName("type")
            private String type;

            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getType() {
                return type;
            }
        }
        
        public static class Season {

            @SerializedName("link")
            private String link;

            @SerializedName("episodes")
            private Map<Integer, String> episodes;


            public String getLink() {
                return link;
            }

            public Map<Integer, String> getEpisodes() {
                return episodes;
            }
        }
    }
}
