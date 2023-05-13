package com.tiptop.dotsandboxes.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.utils.Constants;
import com.tiptop.dotsandboxes.utils.PrefUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseTurnFragment extends DialogFragment {


    @BindView(R.id.img_player_1)
    ImageView imgPlayer1;
    @BindView(R.id.img_player_2)
    ImageView imgPlayer2;
    @BindView(R.id.tv_player_1_name)
    TextView tvPlayer1Name;
    @BindView(R.id.tv_player_2_name)
    TextView tvPlayer2Name;

    private OnFragmentInteractionListener mListener;
    private String gameMode;
    private String playerYou;
    private String playerFriend;
    private String playerNameMeFromPreference;
    private String playerMeImageFfromPreference;
    private PrefUtils prefUtils;

    public ChooseTurnFragment() {
        // Required empty public constructor
    }

    public static ChooseTurnFragment newInstance(Bundle args) {
        ChooseTurnFragment f = new ChooseTurnFragment();

        if (args != null)
            f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefUtils = new PrefUtils(getActivity());

        playerNameMeFromPreference = prefUtils.getString(Constants.prefrences.NAME);
        playerMeImageFfromPreference = prefUtils.getString(Constants.prefrences.PROFILE_IMAGE);

        Timber.e("\n \n Name = " + playerNameMeFromPreference + " and image url = " + playerMeImageFfromPreference);

        gameMode = getArguments().getString(Constants.GAME_MODE);
        playerYou = getArguments().getString(Constants.PLAYER1_NAME, Constants.PLAYER_YOU);
        playerFriend = getArguments().getString(Constants.PLAYER2_NAME, Constants.PLAYER_ROBOT);
       /* playerNameMeFromPreference = getArguments().getString(Constants.prefrences.NAME);
        playerMeImageFfromPreference = getArguments().getString(Constants.prefrences.PROFILE_IMAGE);*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_turn_selection, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (gameMode) {
            case Constants.ROBOT:

                if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                    tvPlayer1Name.setText(getString(R.string.you));
                } else {
                    tvPlayer1Name.setText(getString(R.string.you));
                }
                if (playerMeImageFfromPreference != null && !TextUtils.isEmpty(playerMeImageFfromPreference)) {
                    setImageToImageView(playerMeImageFfromPreference, imgPlayer1);
                } else {
                    setImageToImageViewFromResource(R.drawable.profile_image, imgPlayer1);
                }
                setImageToImageViewFromResource(R.drawable.robot1, imgPlayer2);

                tvPlayer2Name.setText(getString(R.string.robot));
                break;
            case Constants.FRIEND:

                tvPlayer1Name.setText(playerYou);

                if (playerMeImageFfromPreference != null && !TextUtils.isEmpty(playerMeImageFfromPreference)) {
                    setImageToImageView(playerMeImageFfromPreference, imgPlayer1);
                } else {
                    setImageToImageViewFromResource(R.drawable.profile_image, imgPlayer1);
                }

                setImageToImageViewFromResource(R.drawable.friend_icon, imgPlayer2);
                tvPlayer2Name.setText(playerFriend);
                break;
            case Constants.ONLINE:

                tvPlayer1Name.setText(playerYou);

                if (playerMeImageFfromPreference != null && !TextUtils.isEmpty(playerMeImageFfromPreference)) {
                    setImageToImageView(playerMeImageFfromPreference, imgPlayer1);
                } else {
                    setImageToImageViewFromResource(R.drawable.profile_image, imgPlayer1);
                }
                setImageToImageViewFromResource(R.drawable.friend_icon, imgPlayer2);
                tvPlayer2Name.setText(playerFriend);
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        // set the animations to use on showing and hiding the dialog
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.img_player_1, R.id.img_player_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_player_1:
                mListener.onPlayer1Selected();
                dismiss();
                break;
            case R.id.img_player_2:
                mListener.onPlayer2Selected();
                dismiss();
                break;
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        getActivity().onBackPressed();
    }

    /**
     * This method is used to load image to image view specified
     *
     * @param imageUrl  it is the url of the image
     * @param imageView it is the imageview, in which image should load
     */
    public void setImageToImageView(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }

    /**
     * This method is used to load image to image view specified
     *
     * @param imageUrl  it is the url of the image from resouce folder
     * @param imageView it is the imageview, in which image should load
     */
    public void setImageToImageViewFromResource(int imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }

    public interface OnFragmentInteractionListener {
        void onPlayer1Selected();

        void onPlayer2Selected();
    }
}
