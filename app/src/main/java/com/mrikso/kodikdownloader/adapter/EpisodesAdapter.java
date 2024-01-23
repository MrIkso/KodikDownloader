package com.mrikso.kodikdownloader.adapter;

import android.view.LayoutInflater;
import android.view.View;
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

    private int checkedPosition = -1;

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
        holder.bind(episode, position);
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

        public void bind(EpisodeItem episode, int position) {

            String title =
                    binding.getRoot()
                            .getContext()
                            .getString(R.string.episode_hint, episode.getEpisode());

            binding.name.setText(title);

            binding.checkedView.setVisibility(episode.isChecked() ? View.VISIBLE : View.GONE);

            if (listener != null) {
                binding.getRoot()
                        .setOnClickListener(
                                v -> {
                                    if (getSelected().size() > 0) {
                                        onCheckItem(episode, position);
                                    } else {
                                        listener.onMultiselectModeEnable(false);
                                        listener.onEpisodeItemSelected(episode);
                                    }
                                });

                binding.getRoot()
                        .setOnLongClickListener(
                                v -> {
                                    onCheckItem(episode, position);
                                    return true;
                                });
            }
        }

        void onCheckItem(EpisodeItem episode, int position) {
            checkedPosition = position;
            episode.setChecked(!episode.isChecked());
            binding.checkedView.setVisibility(episode.isChecked() ? View.VISIBLE : View.GONE);
            if (checkedPosition != getAdapterPosition()) {
                notifyItemChanged(checkedPosition);
                checkedPosition = getAdapterPosition();
            }
            if (getSelected().isEmpty()) {
                listener.onMultiselectModeEnable(false);
            } else {
                listener.onMultiselectModeEnable(true);
            }
        }
    }

    public List<EpisodeItem> getSelected() {
        ArrayList<EpisodeItem> selected = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).isChecked()) {
                selected.add(results.get(i));
            }
        }
        return selected;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onEpisodeItemSelected(EpisodeItem item);

        void onMultiselectModeEnable(boolean enabled);
    }
}
