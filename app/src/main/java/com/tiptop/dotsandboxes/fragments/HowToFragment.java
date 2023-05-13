package com.tiptop.dotsandboxes.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.activities.MainActivity;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class HowToFragment extends Fragment {


    @BindView(R.id.wv_how_to)
    WebView wvHowTo;

    public HowToFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.activity_how_to, container, false);
        ButterKnife.bind(this, root);
        initToolbar();
        wvHowTo.loadUrl("file:///android_asset/html/index.html");
        return root;
    }

    /**
     * this method initialize toolbar
     */
    private void initToolbar() {
        ((MainActivity) getActivity()).commonToolbar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarBack.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarFacebook.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarMusic.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarSound.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarVibrate.setVisibility(View.GONE);
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).tvToolbarTitle.setText(getString(R.string.how_to_screen));
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.VISIBLE);
    }
}