package com.trackerforce.splitmate.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.ui.SplitmateView;

public class WebViewFragment extends Fragment implements SplitmateView {

    private String url;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final WebView webView = getComponent(R.id.webView, WebView.class);
        webView.loadUrl(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}