package com.mrikso.kodikdownloader.dialogs;

import android.os.Bundle;
import android.util.Log;
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
    }

    public static EpisodesBottomSheetFragment newInstance(Map<Integer, String> items) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_ITEMS, new HashMap<>(items));
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
            final Map<Integer, String> stringArrayList =
                    (Map<Integer, String>) getArguments().getSerializable(KEY_ITEMS);

            List<EpisodeItem> listEpisodes = new ArrayList<>();
            for (Map.Entry<Integer, String> ep : stringArrayList.entrySet()) {
                // Log.i(TAG, ep.getKey() + " " + ep.getValue());
                listEpisodes.add(new EpisodeItem(ep.getKey(), ep.getValue()));
            }

            adapter.setResults(listEpisodes);
        }
        binding.close.setOnClickListener(
                v -> {
                    dismiss();
                });
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
    }
}
