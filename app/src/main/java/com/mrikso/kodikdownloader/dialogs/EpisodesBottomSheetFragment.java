package com.mrikso.kodikdownloader.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mrikso.kodikdownloader.adapter.EpisodesAdapter;
import com.mrikso.kodikdownloader.databinding.FragmentEpisodesBinding;
import com.mrikso.kodikdownloader.model.EpisodeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpisodesBottomSheetFragment extends BottomSheetDialogFragment
        implements EpisodesAdapter.OnItemClickListener {

    private static final String KEY_ITEMS = "KEY_ITEMS";
    private static final String KEY_REQUEST_CODE = "KEY_REQUEST_CODE";
    public static final String TAG = "EpisodesBottomSheetFragment";
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEpisodeItemSelected(EpisodeItem item);
        void onDownloadMultiSelected(List<EpisodeItem> items);
    }

    public static EpisodesBottomSheetFragment newInstance(List<EpisodeItem> items) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_ITEMS, new ArrayList<>(items));
        EpisodesBottomSheetFragment fragment = new EpisodesBottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private FragmentEpisodesBinding binding;
    private EpisodesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentEpisodesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new EpisodesAdapter();
        adapter.setOnItemClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            List<EpisodeItem> episodeItems = getArguments().getParcelableArrayList(KEY_ITEMS);
            adapter.submitList(episodeItems);
        }
        binding.downloadSelected.setOnClickListener(v-> listener.onDownloadMultiSelected(adapter.getSelected()));
    }

    @Override
    public void onEpisodeItemSelected(EpisodeItem item) {
        listener.onEpisodeItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
        listener = null;
    }

    @Override
    public void onMultiselectModeEnable(boolean enabled) {
        binding.downloadSelected.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
}
