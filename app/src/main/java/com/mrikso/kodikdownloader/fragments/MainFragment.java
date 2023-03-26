package com.mrikso.kodikdownloader.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mrikso.kodikdownloader.R;
import com.mrikso.kodikdownloader.adapter.SearchItemClickListener;
import com.mrikso.kodikdownloader.adapter.SearchResultAdapter;
import com.mrikso.kodikdownloader.databinding.FragmentMainBinding;
import com.mrikso.kodikdownloader.dialogs.EpisodesBottomSheetFragment;
import com.mrikso.kodikdownloader.downloader.DownloaderMode;
import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchItem;
import com.mrikso.kodikdownloader.downloader.DownloadFile;
import com.mrikso.kodikdownloader.viewmodel.MainFragmentViewModel;

import java.util.Map;

public class MainFragment extends Fragment
        implements SearchItemClickListener, EpisodesBottomSheetFragment.OnItemClickListener {

    private FragmentMainBinding binding;
    private MainFragmentViewModel viewModel;
    private SearchResultAdapter resultAdapter;
    private SearchItem searchItem;
    private EpisodeItem episodeItem;
    private DownloaderMode downloaderMode;
    private SharedPreferences sharedPrefs;

    public static final String TAG = "MainFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);

        intitPreferences();
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
                    if (!TextUtils.isEmpty(binding.editTextUrl.getText())) {
                        viewModel.startSearch(
                                binding.editTextUrl.getText().toString(),
                                binding.spinnerMethods.getSelectedItemPosition());
                    }
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
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);
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
                            if (results != null && results.getTotal() > 0) {

                                Toast.makeText(
                                                requireContext(),
                                                getString(R.string.found_hint, results.getTotal()),
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
        sharedPrefs = null;
        episodeItem = null;
        searchItem = null;
    }

    @Override
    public void onSearchItemClicked(SearchItem searchItem) {
        this.searchItem = searchItem;
        // Toast.makeText(requireContext(), searchItem.getTitle(), Toast.LENGTH_LONG).show();

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
        Log.d(TAG, url);
		if(url.startsWith("//")){
			url = url.replaceFirst("//", "https://");
		}
        url = url.replace(":hls:manifest.m3u8", "").replace(":hls:hls.m3u8", "");
		
		Log.d(TAG, url);
        StringBuilder fileName = new StringBuilder(searchItem.getTitle());
        if (searchItem.getIsSerial()) {
            fileName.append(" — " + getString(R.string.episode_hint, episodeItem.getEpisode()));
        }
        fileName.append(".mp4");
        // Log.d(TAG, fileName.toString());
        DownloadFile.download(requireContext(), downloaderMode, url, fileName.toString());
    }

    @Override
    public void onEpisodeItemSelected(EpisodeItem item) {
        episodeItem = item;
        viewModel.loadVideos(item.getEpisodeUrl());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_options, menu);
        switch (downloaderMode) {
            case ADM:
                menu.findItem(R.id.menu_downloder_adm).setChecked(true);
                break;
            case IDM:
                menu.findItem(R.id.menu_downloder_idm).setChecked(true);
                break;
            case BROWSER:
                menu.findItem(R.id.menu_downloder_browser).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_downloder_adm) {
            item.setChecked(!item.isChecked());
            downloaderMode = DownloaderMode.ADM;
            savePreferences();
            return true;
        }
        if (item.getItemId() == R.id.menu_downloder_idm) {
            item.setChecked(!item.isChecked());
            downloaderMode = DownloaderMode.IDM;
            savePreferences();
            return true;
        }
        if (item.getItemId() == R.id.menu_downloder_browser) {
            item.setChecked(!item.isChecked());
            downloaderMode = DownloaderMode.BROWSER;
            savePreferences();
            return true;
        }
        if (item.getItemId() == R.id.about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void intitPreferences() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int mode = sharedPrefs.getInt("downloader_mode", 2);
        downloaderMode = DownloaderMode.values()[mode];
    }

    private void savePreferences() {
        sharedPrefs.edit().putInt("downloader_mode", downloaderMode.getMode()).apply();
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.about)
                .setMessage(R.string.about_text)
                .setNeutralButton(
                        android.R.string.ok,
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .create()
                .show();
    }
}
