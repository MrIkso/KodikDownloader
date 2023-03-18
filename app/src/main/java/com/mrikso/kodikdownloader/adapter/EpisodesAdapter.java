package com.mrikso.kodikdownloader.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrikso.kodikdownloader.databinding.ListEpisodeItemBinding;
import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.R;

import java.util.ArrayList;
import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {
    private List<EpisodeItem> results = new ArrayList<>();

    private OnItemClickListener listener;
    private ListEpisodeItemBinding binding;

    public void setResults(List<EpisodeItem> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ListEpisodeItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EpisodeItem episode = results.get(position);
        holder.bind(episode);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ListEpisodeItemBinding binding;

        public ViewHolder(@NonNull ListEpisodeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EpisodeItem episode) {

            String title =
                    binding.getRoot()
                            .getContext()
                            .getString(R.string.episode_hint, episode.getEpisode());

            binding.name.setText(title);

            if (listener != null) {
                binding.getRoot().setOnClickListener(v -> listener.onEpisodeItemSelected(episode));
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onEpisodeItemSelected(EpisodeItem item);
    }
}
