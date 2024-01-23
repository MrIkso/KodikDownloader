package com.mrikso.kodikdownloader.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mrikso.kodikdownloader.BuildConfig;
import com.mrikso.kodikdownloader.R;
import com.mrikso.kodikdownloader.adapter.SearchItemClickListener;
import com.mrikso.kodikdownloader.adapter.SearchResultAdapter;
import com.mrikso.kodikdownloader.databinding.FragmentMainBinding;
import com.mrikso.kodikdownloader.dialogs.DelayedProgressDialog;
import com.mrikso.kodikdownloader.dialogs.EpisodesBottomSheetFragment;
import com.mrikso.kodikdownloader.downloader.DownloadFile;
import com.mrikso.kodikdownloader.downloader.DownloaderMode;
import com.mrikso.kodikdownloader.model.EpisodeItem;
import com.mrikso.kodikdownloader.model.SearchItem;
import com.mrikso.kodikdownloader.utils.KeyboardUtils;
import com.mrikso.kodikdownloader.viewmodel.MainFragmentViewModel;

import java.util.List;
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
    private DelayedProgressDialog progressDialog;
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

        binding.editTextUrl.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            performSearch();

                            return true;
                        }
                        return false;
                    }
                });

        buttonSend.setOnClickListener(
                v -> {
                    performSearch();
                });

        progressDialog = new DelayedProgressDialog();
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

    private void performSearch() {
        if (!TextUtils.isEmpty(binding.editTextUrl.getText())) {
            KeyboardUtils.hideKeyboard(this);
            viewModel.startSearch(
                    binding.editTextUrl.getText().toString(),
                    binding.spinnerMethods.getSelectedItemPosition());
        }
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
                            hideProgressDialog();
                            if (result != null && !result.isEmpty()) {
                                // Log.i(TAG, "show normal dialog");
                                showQualityChoserDialog(result, null, false);
                            }
                        });

        viewModel
                .getAllVideosMap()
                .observe(
                        getViewLifecycleOwner(),
                        result -> {
                            hideProgressDialog();
                            if (result != null && !result.isEmpty()) {
                                 Log.i(TAG, "show bathDownload");
                                showQualityChoserDialog(result.values().stream().findFirst().get(), result, true);
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
        progressDialog = null;
    }

    @Override
    public void onSearchItemClicked(SearchItem searchItem) {
        this.searchItem = searchItem;
        // Toast.makeText(requireContext(), searchItem.getTitle(), Toast.LENGTH_LONG).show();

        if (searchItem.getIsSerial()) {
            showEpidodeChoserDialog(searchItem.getEpisodes());
        } else {
            showProgressDialog();
            viewModel.loadVideos(searchItem.getUrl());
        }
    }

    private void showQualityChoserDialog(
            Map<String, String> videoMap,
            Map<Integer, Map<String, String>> allVideos,
            boolean isBatchDowload) {
        // Print the resulting map
        videoMap.forEach((key, item) -> System.out.println("Key: " + key + ", Data: " + item));
        String[] qualities = videoMap.keySet().toArray(new String[0]);

        AlertDialog dialog =
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.quality_choser_title)
                        .setNegativeButton(
                                getString(android.R.string.cancel),
                                (d, which) -> {
                                    d.dismiss();
                                })
                        .setSingleChoiceItems(
                                qualities,
                                0,
                                (d, index) -> {
                                    String selectedQuality = qualities[index];
                                    // Log.i(TAG, "selection.q: " + selectedQuality);
                                    if (isBatchDowload) {

                                        downloadVideosBatch(allVideos, selectedQuality);

                                    } else {
                                        downloadVideo(videoMap.get(selectedQuality));
                                    }
                                    d.dismiss();
                                })
                        .create();

        dialog.show();
    }

    private void showEpidodeChoserDialog(Map<Integer, String> episodes) {
        EpisodesBottomSheetFragment dialog = EpisodesBottomSheetFragment.newInstance(episodes);
        dialog.setOnItemClickListener(this);
        dialog.show(getParentFragmentManager(), EpisodesBottomSheetFragment.TAG);
    }

    private void downloadVideo(String url) {
        Log.d(TAG, url);

        StringBuilder fileName = new StringBuilder(searchItem.getTitle());
        if (searchItem.getIsSerial()) {
            fileName.append(" — " + getString(R.string.episode_hint, episodeItem.getEpisode()));
        }
        fileName.append(".mp4");
        // Log.d(TAG, fileName.toString());
        DownloadFile.download(requireContext(), downloaderMode, url, fileName.toString(), false);
    }

    @Override
    public void onEpisodeItemSelected(EpisodeItem item) {
        episodeItem = item;
        showProgressDialog();
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
        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        String versionName = String.valueOf(BuildConfig.VERSION_NAME);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.about)
                .setMessage(getString(R.string.about_text, versionName, versionCode))
                .setNeutralButton(
                        android.R.string.ok,
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .setPositiveButton(
                        "GitHub",
                        (dialog, which) -> {
                            DownloadFile.openInBrowser(
                                    requireContext(), "https://github.com/MrIkso/KodikDownloader");
                            dialog.dismiss();
                        })
                .create()
                .show();
    }

    private void showProgressDialog() {
        progressDialog.show(getParentFragmentManager(), DelayedProgressDialog.tag);
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void onBatchDownloadItemClicked(SearchItem searchItem) {
        if (downloaderMode == DownloaderMode.ADM) {
            this.searchItem = searchItem;
            showProgressDialog();

            viewModel.loadVideos(searchItem.getEpisodes());
        } else {
            Toast.makeText(
                            requireContext(),
                            R.string.batch_download_avaliable_only_on_adm,
                            Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void downloadVideosBatch(
            Map<Integer, Map<String, String>> allVideos, String selectedQuality) {
        StringBuilder videos = new StringBuilder();
        for (Map.Entry<Integer, Map<String, String>> episodeVideos : allVideos.entrySet()) {
            int episodeNum = episodeVideos.getKey();
            Map<String, String> episodeVideoMap = episodeVideos.getValue();
            // Log.i(TAG, "GENERATING INFO..");
            String url = episodeVideoMap.get(selectedQuality);
            String fileName =
                    String.format(
                            "%s  —  %s.mp4",
                            searchItem.getTitle(), getString(R.string.episode_hint, episodeNum));
            videos.append(url);
            videos.append("<info>");
            videos.append(fileName);
            videos.append("<line>");
        }

        if (videos.toString().endsWith("<line>")) {
            videos.substring(0, videos.length() - 6);
        }
        DownloadFile.download(requireContext(), DownloaderMode.ADM, videos.toString(), null, true);
    }

    @Override
    public void onDownloadMultiSelected(List<EpisodeItem> items) {
        showProgressDialog();
        viewModel.loadVideos(items);
    }
}
