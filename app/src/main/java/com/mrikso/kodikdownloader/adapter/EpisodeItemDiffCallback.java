package com.mrikso.kodikdownloader.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.mrikso.kodikdownloader.model.EpisodeItem;

public class EpisodeItemDiffCallback extends DiffUtil.ItemCallback<EpisodeItem> {
    @Override
    public boolean areItemsTheSame(@NonNull EpisodeItem oldItem, @NonNull EpisodeItem newItem) {
        return oldItem.getEpisodeId().equals(newItem.getEpisodeId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull EpisodeItem oldItem, @NonNull EpisodeItem newItem) {
        return oldItem.equals(newItem);
    }
}
