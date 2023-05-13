package com.tiptop.dotsandboxes.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tiptop.dotsandboxes.DotsAndBoxesApplication;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.activities.MainActivity;
import com.tiptop.dotsandboxes.utils.Constants;
import com.tiptop.dotsandboxes.utils.PrefUtils;
//import com.google.android.gms.ads.AdView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
//implements ImagePickerCallback

    public PrefUtils prefUtils;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.et_set_your_name)
    EditText etSetYourName;
    @BindView(R.id.btn_save)
    AppCompatButton btnSave;
    @BindView(R.id.tv_game_played)
    TextView tvGamePlayed;
    @BindView(R.id.tv_game_win)
    TextView tvGameWin;
    @BindView(R.id.tv_game_lost)
    TextView tvGameLost;
    @BindView(R.id.tv_game_win_percentage)
    TextView tvGameWinPercentage;
//    @BindView(R.id.adView)
//    AdView adView;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.tv_play_games_name)
    TextView tvPlayGamesName;
    @BindView(R.id.btn_share)
    AppCompatButton btnShare;
    @BindView(R.id.tv_play_games_name_title)
    TextView tvPlayGamesNameTitle;

    long totalGamePlayed = 0L;
    long totalGameWin = 0L;
    long totalGameLost = 0L;
   // private ImagePickerFragment mImagePickerFragment;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.activity_profile, container, false);
        ButterKnife.bind(this, root);
        initToolbar();
        initialize();
        return root;
    }

    /**
     * this method initialize toolbar
     */
    private void initToolbar() {
        ((MainActivity) getActivity()).commonToolbar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).tvToolbarTitle.setText(getString(R.string.profile));
        ((MainActivity) getActivity()).ivToolbarMusic.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarSound.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarVibrate.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarFacebook.setVisibility(View.GONE);
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_save, R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                hideKeyboard(getActivity());
                if (checkName()) {
                    prefUtils.setString(Constants.prefrences.NAME, etSetYourName.getText().toString().trim());
                    btnSave.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_game_message) + " " + tvPlayGamesName.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mImagePickerFragment.processActivityResult(requestCode, resultCode, data, this);
    }*/

    /**
     * this method initialize variables and functions
     */
    private void initialize() {
      /*  mImagePickerFragment = ImagePickerFragment.newInstance(this, new MarshMallowHelper());
        mImagePickerFragment.setCropEnabled(false);*/
        // mImagePickerFragment.setCropType(Cropper.CROP_TYPE_OVAL);

        prefUtils = new PrefUtils(getContext());

        if (!(prefUtils.getString(Constants.prefrences.NAME) == null)) {
            etSetYourName.setText(prefUtils.getString(Constants.prefrences.NAME));
        }
        if (!(prefUtils.getString(Constants.prefrences.PROFILE_IMAGE) == null)) {
            if (!prefUtils.getString(Constants.prefrences.PROFILE_IMAGE).isEmpty())
                Glide.with(this).load(Uri.parse(prefUtils.getString(Constants.prefrences.PROFILE_IMAGE))).apply(RequestOptions.circleCropTransform()).into(ivProfile);
        }
        totalGamePlayed = ((DotsAndBoxesApplication) getActivity().getApplication()).getDb().gameScoreDao().getTotalGamesPlayed();
        tvGamePlayed.setText(String.valueOf(totalGamePlayed));
        totalGameWin = ((DotsAndBoxesApplication) getActivity().getApplication()).getDb().gameScoreDao().getWinMatches();
        tvGameWin.setText(String.valueOf(totalGameWin));
        totalGameLost = ((DotsAndBoxesApplication) getActivity().getApplication()).getDb().gameScoreDao().getLostMatches();
        tvGameLost.setText(String.valueOf(totalGameLost));
        if (prefUtils.getString(Constants.MY_ONLINE_NAME).isEmpty()) {
            tvPlayGamesName.setVisibility(View.GONE);
            tvPlayGamesNameTitle.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
        } else {
            tvPlayGamesName.setVisibility(View.VISIBLE);
            tvPlayGamesNameTitle.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            tvPlayGamesName.setText(prefUtils.getString(Constants.MY_ONLINE_NAME));
        }

        float winPercent = getPercentage();
        tvGameWinPercentage.setText(String.valueOf(winPercent));
        etSetYourName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
/*
    *//**
     * this function used to open dialog for selection option for profile image if user want to
     * add it.
     *//*
    private void selectProfileImage() {
        final CharSequence[] items;
        items = new CharSequence[]{getString(R.string.str_take_photo), getString(R.string.str_choose_from_library)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.str_choose_picture);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.str_take_photo))) {
                    mImagePickerFragment.captureImageFromCamera(ProfileFragment.this);

                } else if (items[item].equals(getString(R.string.str_choose_from_library))) {
                    mImagePickerFragment.selectImageFromGallery(ProfileFragment.this);
                }
            }
        });
        builder.show();
    }*/

    /**
     * This method calculate win percentage
     *
     * @return return float value of win percentage
     */
    private float getPercentage() {
        if (totalGamePlayed == 0) {
            return 0;
        } else {
            return (totalGameWin * 100) / totalGamePlayed;
        }
    }

    /**
     * this method checks Entered name is empty or not
     *
     * @return
     */
    private boolean checkName() {
        if (etSetYourName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.please_enter_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

/*    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mImagePickerFragment.processPermissionResults(requestCode, grantResults, this);
    }

    @Override
    public void onCompleteTakingImage(@Nullable Uri uri, boolean isCropped) {
        Glide.with(this).load(uri).apply(RequestOptions.circleCropTransform()).into(ivProfile);
        prefUtils.setString(Constants.prefrences.PROFILE_IMAGE, uri.toString());
    }

    @Override
    public void onCancelTakingImage() {

    }

    @Override
    public void onErrorTakingImage(@NonNull String messageToShow, @NonNull Throwable t) {

    }*/
}