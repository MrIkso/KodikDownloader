package com.mrikso.kodikdownloader.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mrikso.kodikdownloader.R;
import com.mrikso.kodikdownloader.adapter.SearchItemClickListener;
import com.mrikso.kodikdownloader.adapter.SearchResultAdapter;
import com.mrikso.kodikdownloader.databinding.FragmentMainBinding;
import com.mrikso.kodikdownloader.dialogs.EpisodesBottomSheetFragment;
import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchItem;
import com.mrikso.kodikdownloader.service.DownloadFile;
import com.mrikso.kodikdownloader.viewmodel.MainFragmentViewModel;

import java.util.Map;

public class MainFragment extends Fragment
        implements SearchItemClickListener, EpisodesBottomSheetFragment.OnItemClickListener {

    private FragmentMainBinding binding;
    private MainFragmentViewModel viewModel;
    private SearchResultAdapter resultAdapter;
    private SearchItem searchItem;
    private EpisodeItem episodeItem;
    public static final String TAG = "MainFragment";

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        initRecyclerView();
        initObserveValues();
        searchTypeSpinnerListener();

        final FloatingActionButton buttonSend = binding.buttonSend;
        buttonSend.setEnabled(false);
        binding.editTextUrl.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        buttonSend.setEnabled(!TextUtils.isEmpty(s));
                    }
                });

        buttonSend.setOnClickListener(
                v -> {
                    viewModel.startSearch(
                            binding.editTextUrl.getText().toString(),
                            binding.spinnerMethods.getSelectedItemPosition());
                });
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainFragmentViewModel.class);
        return binding.getRoot();
    }

    private void initRecyclerView() {
        resultAdapter = new SearchResultAdapter();
        resultAdapter.setSearchItemClickListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(resultAdapter);
    }

    private void searchTypeSpinnerListener() {
        binding.spinnerMethods.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            binding.editTextUrl.setHint(R.string.put_name_hint);
                        } else {
                            binding.editTextUrl.setHint(
                                    getString(
                                            R.string.put_id_hint,
                                            binding.spinnerMethods.getSelectedItem().toString()));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                });
    }

    private void initObserveValues() {
        viewModel
                .getSearchResults()
                .observe(
                        getViewLifecycleOwner(),
                        results -> {
                            if (results != null) {

                                if (results.getTotal() > 0) {
                                    Toast.makeText(
                                                    requireContext(),
                                                    getString(
                                                            R.string.found_hint,
                                                            results.getTotal()),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    resultAdapter.setResults(results.getResults());
                                } else {
                                    Toast.makeText(
                                                    requireContext(),
                                                    R.string.not_found_hint,
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });

        viewModel
                .getVideosMap()
                .observe(
                        getViewLifecycleOwner(),
                        result -> {
                            if (result != null && result.size() != 0) {
                                showQalityChoserDialog(result);
                            }
                        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
        resultAdapter = null;
    }

    @Override
    public void onSearchItemClicked(SearchItem searchItem) {
        this.searchItem = searchItem;
        Toast.makeText(requireContext(), searchItem.getTitle(), Toast.LENGTH_LONG).show();

        if (searchItem.getIsSerial()) {
            showEpidodeChoserDialog(searchItem.getEpisodes());
        } else {
            viewModel.loadVideos(searchItem.getUrl());
        }
    }

    private void showQalityChoserDialog(Map<String, String> videoMap) {
        String[] qualities = videoMap.keySet().toArray(new String[0]);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.qality_choser_title)
                .setNegativeButton(
                        android.R.string.cancel,
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .setSingleChoiceItems(
                        qualities,
                        0,
                        (dialog, i) -> {
                            downloadVideo(videoMap.get(qualities[i]));
                            dialog.dismiss();
                        })
                .create()
                .show();
    }

    private void showEpidodeChoserDialog(Map<Integer, String> episodes) {
        EpisodesBottomSheetFragment dialog = EpisodesBottomSheetFragment.newInstance(episodes);
        dialog.setOnItemClickListener(this);
        dialog.show(getParentFragmentManager(), EpisodesBottomSheetFragment.TAG);
    }

    private void downloadVideo(String url) {
        // Log.d(TAG, url);
        url = url.replace(":hls:manifest.m3u8", "");
        StringBuilder fileName = new StringBuilder(searchItem.getTitle());
        if (searchItem.getIsSerial()) {
            fileName.append(" — " + getString(R.string.episode_hint, episodeItem.getEpisode()));
        }
        fileName.append(".mp4");
        // Log.d(TAG, fileName.toString());
        DownloadFile.download(requireContext(), url, fileName.toString());
    }

    @Override
    public void onEpisodeItemSelected(EpisodeItem item) {
        episodeItem = item;
        viewModel.loadVideos(item.getEpisodeUrl());
    }
}
