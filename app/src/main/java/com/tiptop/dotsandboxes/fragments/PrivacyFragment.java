package com.tiptop.dotsandboxes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.activities.MainActivity;
import com.tiptop.dotsandboxes.utils.Constants;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyFragment extends Fragment {


    @BindView(R.id.wv_privacy_policy)
    WebView wvPrivacyPolicy;


    public PrivacyFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.activity_privacy, container, false);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        ((MainActivity) getActivity()).ivToolbarBack.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarFacebook.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarMusic.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarSound.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarVibrate.setVisibility(View.GONE);
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).tvToolbarTitle.setText(getActivity().getResources().getString(R.string.privacy_policy));
        wvPrivacyPolicy.loadUrl(Constants.URL_PRIVACY);
    }
}
