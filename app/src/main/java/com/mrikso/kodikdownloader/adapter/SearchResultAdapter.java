package com.mrikso.kodikdownloader.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrikso.kodikdownloader.R;
import com.mrikso.kodikdownloader.databinding.ListSearchItemBinding;
import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchItem;
import com.mrikso.kodikdownloader.model.SearchResultModel;

import java.util.ArrayList;
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
            if (result.getType().contains("serial")) {
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

                SearchResultModel.Results.Season seasonModel =
                        result.getSeasons().get(result.getSeasons().keySet().toArray()[0]);
                // Log.i("tag", seasonModel.getLink());
                searchItem.setEpisodes(seasonModel.getEpisodes());
                searchItemBinding.titleStatus.setText(statusExt);
            } else {
                searchItemBinding.titleStatus.setText(status);
            }

            searchItemBinding.titleName.setText(title);
            searchItemBinding.titleNameOrig.setText(subtitleFormatted);

            searchItemBinding.titleNameOther.setText(result.getOtherTitle());

            if (listener != null) {
                searchItemBinding
                        .getRoot()
                        .setOnClickListener(v -> listener.onSearchItemClicked(searchItem));
            }
        }
    }

    public void setSearchItemClickListener(SearchItemClickListener listener) {
        this.listener = listener;
    }
}
