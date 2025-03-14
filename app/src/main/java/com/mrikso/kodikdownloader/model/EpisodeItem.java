package com.mrikso.kodikdownloader.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class EpisodeItem implements Parcelable {
    private int episode;
    private String episodeUrl;
    private boolean isChecked = false;
    private final int season;
    private final String title;

    public EpisodeItem(int season, String title) {
        this.season = season;
        this.title = title;
    }

    protected EpisodeItem(Parcel in) {
        episode = in.readInt();
        episodeUrl = in.readString();
        isChecked = in.readByte() != 0; // isChecked == true if byte != 0
        season = in.readInt();
        title = in.readString();
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

    public String getEpisodeId() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(episode);
        dest.writeString(episodeUrl);
        dest.writeByte((byte) (isChecked ? 1 : 0)); // isChecked == true ? 1 : 0
        dest.writeInt(season);
        dest.writeString(title);
    }

    public static final Creator<EpisodeItem> CREATOR = new Creator<>() {
        @Override
        public EpisodeItem createFromParcel(Parcel in) {
            return new EpisodeItem(in);
        }

        @Override
        public EpisodeItem[] newArray(int size) {
            return new EpisodeItem[size];
        }
    };
}
