package com.mrikso.kodikdownloader.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrikso.kodikdownloader.R;
import com.mrikso.kodikdownloader.databinding.ListSearchItemBinding;
import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchItem;
import com.mrikso.kodikdownloader.model.SearchResultModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultAdapter
        extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
    private List<SearchResultModel.Results> results = new ArrayList<>();
    private SearchItemClickListener listener;

    public void setResults(List<SearchResultModel.Results> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListSearchItemBinding searchItemBinding =
                ListSearchItemBinding.inflate(inflater, parent, false);
        return new SearchResultViewHolder(searchItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchResultModel.Results postModel = results.get(position);
        holder.bind(postModel);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    protected class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private ListSearchItemBinding searchItemBinding;

        public SearchResultViewHolder(@NonNull ListSearchItemBinding searchItemBinding) {
            super(searchItemBinding.getRoot());
            this.searchItemBinding = searchItemBinding;
        }

        public void bind(SearchResultModel.Results result) {

            String status = result.getTranslation().getTitle();
            String title = result.getTitle();

            SearchItem searchItem = new SearchItem(title, result.getLink());
            String subtitleFormatted =
                    String.format("%d / %s", result.getYear(), result.getTitleOrig());
            if (result.getType().contains("serial") ) {
                String episode =
                        searchItemBinding
                                .getRoot()
                                .getContext()
                                .getResources()
                                .getQuantityString(
                                        R.plurals.episode_hint,
                                        result.getEpisodesCount(),
                                        result.getEpisodesCount());

                String season =
                        searchItemBinding
                                .getRoot()
                                .getContext()
                                .getString(R.string.season_hint, result.getLastSeason());
                String statusExt = String.format("%s (%s) – %s", episode, season, status);
                searchItem.setIsSerial(true);
				
				Map<Integer, SearchResultModel.Results.Season> seasonsMap = result.getSeasons();
				List<EpisodeItem> episodesMap = new ArrayList<>();
				
				for (Map.Entry<Integer, SearchResultModel.Results.Season> seasons : seasonsMap.entrySet()){
                    SearchResultModel.Results.Season seasonModel = seasons.getValue();
                    for (Map.Entry<Integer, String> episodeEntry : seasonModel.getEpisodes().entrySet()) {
                        EpisodeItem episodeItem = new EpisodeItem(seasons.getKey(), seasonModel.getTitle());
                        episodeItem.setEpisode(episodeEntry.getKey());
                        episodeItem.setEpisodeUrl(episodeEntry.getValue());
                        episodesMap.add(episodeItem);
                    }
				}
				
                searchItem.setEpisodes(episodesMap);
				searchItemBinding.download.setVisibility(View.VISIBLE);
                searchItemBinding.titleStatus.setText(statusExt);
            } else {
                searchItemBinding.titleStatus.setText(status);
				searchItemBinding.download.setVisibility(View.GONE);
            }

            searchItemBinding.titleName.setText(title);
            searchItemBinding.titleNameOrig.setText(subtitleFormatted);
            if (TextUtils.isEmpty(result.getOtherTitle())) {
                searchItemBinding.titleNameOther.setVisibility(View.GONE);
            } else {
                searchItemBinding.titleNameOther.setText(result.getOtherTitle());
            }
            if (listener != null) {
                searchItemBinding
                        .getRoot()
                        .setOnClickListener(v -> listener.onSearchItemClicked(searchItem));
				
				searchItemBinding
                        .download
                        .setOnClickListener(v -> listener.onBatchDownloadItemClicked(searchItem));
            }
        }
    }

    public void setSearchItemClickListener(SearchItemClickListener listener) {
        this.listener = listener;
    }
}
