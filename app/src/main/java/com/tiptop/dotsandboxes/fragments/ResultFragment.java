package com.tiptop.dotsandboxes.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tiptop.dotsandboxes.DotsAndBoxesApplication;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.database.GameScore;
import com.tiptop.dotsandboxes.game.controllers.Game;
import com.tiptop.dotsandboxes.utils.Constants;

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
public class ResultFragment extends DialogFragment {
    public static final int FRAGMENT_ID = 31783;
    String winnerName;
    Game.Player winner;
    @BindView(R.id.tv_won_player_result)
    TextView tvWonPlayerResult;
    @BindView(R.id.img_won_player)
    ImageView imgWonPlayer;
    @BindView(R.id.tv_won_player_name)
    TextView tvWonPlayerName;
    @BindView(R.id.tv_won_player_score)
    TextView tvWonPlayerScore;
    @BindView(R.id.tv_lost_player_result)
    TextView tvLostPlayerResult;
    @BindView(R.id.img_lost_player)
    ImageView imgLostPlayer;
    @BindView(R.id.tv_lost_player_name)
    TextView tvLostPlayerName;
    @BindView(R.id.tv_lost_player_score)
    TextView tvLostPlayerScore;
    @BindView(R.id.ll_restart)
    LinearLayout llRestart;
    @BindView(R.id.ll_home)
    LinearLayout llHome;

    private OnFragmentInteractionListener mListener;
    private int player1Score;
    private int player2Score;
    private String player1Name = "";
    private String player2Name = "";
    private Game.Mode mode;
    private String gameMode;
    private boolean isTurnMe;
    private String playerMe = "";
    private String playerFriend = "";
    private String scoreMe = "";
    private String scoreFriend = "";
    private String result = "";
    private String playerNameMeFromPreference = "";
    private String playerMeImageFfromPreference = "";



    public static ResultFragment newInstance(Bundle args) {
        ResultFragment f = new ResultFragment();

        if (args != null)
            f.setArguments(args);

        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        // set the animations to use on showing and hiding the dialog
        getDialog().getWindow().setWindowAnimations(
                R.style.dialog_animation_fade);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @OnClick({R.id.ll_restart, R.id.ll_home})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_restart:
                mListener.onReplayRequested(getArguments());
                dismiss();
                break;
            case R.id.ll_home:
                mListener.onMenuRequested();
                dismiss();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        player1Score = getArguments().getInt(GameFragment.ARG_PLAYER1_SCORE);
        player2Score = getArguments().getInt(GameFragment.ARG_PLAYER2_SCORE);
        player1Name = getArguments().getString(GameFragment.ARG_PLAYER1_NAME);
        player2Name = getArguments().getString(GameFragment.ARG_PLAYER2_NAME);
        mode = (Game.Mode) getArguments().getSerializable(GameFragment.ARG_GAME_MODE);
        gameMode = getArguments().getString(Constants.GAME_MODE);
        isTurnMe = getArguments().getBoolean(Constants.TURN_ME);
        playerNameMeFromPreference = getArguments().getString(Constants.prefrences.NAME);
        playerMeImageFfromPreference = getArguments().getString(Constants.prefrences.PROFILE_IMAGE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, root);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (player1Score > player2Score) {
            winner = Game.Player.PLAYER1;


            if (isTurnMe) {
                result = getString(R.string.win);
                playerMe = getString(R.string.me);

                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                    if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                        player1Name = playerNameMeFromPreference;
                    }
                }
                scoreMe = String.valueOf(player1Score);
                scoreFriend = String.valueOf(player2Score);



            } else {
                result = getString(R.string.lost);
                playerMe = getString(R.string.me);

                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player1Name;
                } else {
                    playerFriend = getString(R.string.robot);
                    if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                        player2Name = playerNameMeFromPreference;
                    }
                }



                scoreMe = String.valueOf(player2Score);
                scoreFriend = String.valueOf(player1Score);
            }
            Glide.with(getActivity())
                    .load(R.drawable.winner_small_trofi_icon)
                    .into(imgWonPlayer);
            Glide.with(getActivity())
                    .load(R.drawable.lost_game_small_graphic)
                    .into(imgLostPlayer);

            tvWonPlayerResult.setText(getString(R.string.win));
            tvLostPlayerResult.setText(getString(R.string.lost));
            tvWonPlayerName.setText(player1Name);
            tvWonPlayerScore.setText(String.valueOf(player1Score));
            tvLostPlayerName.setText(player2Name);
            tvLostPlayerScore.setText(String.valueOf(player2Score));
        } else if (player1Score < player2Score) {
            winner = Game.Player.PLAYER2;

            if (isTurnMe) {
                result = getString(R.string.lost);
                playerMe = getString(R.string.me);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player2Name;
                } else {

                    if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                        player1Name = playerNameMeFromPreference;
                    }
                    playerFriend = getString(R.string.robot);
                }
                scoreMe = String.valueOf(player1Score);
                scoreFriend = String.valueOf(player2Score);



            } else {
                result = getString(R.string.win);
                playerMe = getString(R.string.me);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player1Name;
                } else {
                    playerFriend = getString(R.string.robot);
                    if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                        player2Name = playerNameMeFromPreference;
                    }
                }
                scoreMe = String.valueOf(player2Score);
                scoreFriend = String.valueOf(player1Score);


            }

            Glide.with(getActivity())
                    .load(R.drawable.winner_small_trofi_icon)
                    .into(imgWonPlayer);
            Glide.with(getActivity())
                    .load(R.drawable.lost_game_small_graphic)
                    .into(imgLostPlayer);

            tvWonPlayerResult.setText(getString(R.string.win));
            tvLostPlayerResult.setText(getString(R.string.lost));
            tvWonPlayerName.setText(player2Name);
            tvWonPlayerScore.setText(String.valueOf(player2Score));
            tvLostPlayerName.setText(player1Name);
            tvLostPlayerScore.setText(String.valueOf(player1Score));

        } else {
            winner = Game.Player.NONE;
            tvWonPlayerResult.setText(getString(R.string.tie));
            tvLostPlayerResult.setText(getString(R.string.tie));

            if (isTurnMe) {
                result = getString(R.string.tie);
                playerMe = getString(R.string.me);

                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                    if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                        player1Name = playerNameMeFromPreference;
                    }
                }



                scoreMe = String.valueOf(player1Score);
                scoreFriend = String.valueOf(player2Score);
            } else {
                result = getString(R.string.tie);
                playerMe = getString(R.string.me);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player1Name;
                } else {
                    playerFriend = getString(R.string.robot);
                    if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                        player2Name = playerNameMeFromPreference;
                    }
                }


                scoreMe = String.valueOf(player2Score);
                scoreFriend = String.valueOf(player1Score);
            }

            Glide.with(getActivity())
                    .load(R.drawable.draw_icon)
                    .into(imgWonPlayer);
            Glide.with(getActivity())
                    .load(R.drawable.draw_icon)
                    .into(imgLostPlayer);
            tvWonPlayerName.setText(player2Name);
            tvWonPlayerScore.setText(String.valueOf(player2Score));
            tvLostPlayerName.setText(player1Name);
            tvLostPlayerScore.setText(String.valueOf(player1Score));
        }


        GameScore gameScore = new GameScore();
        gameScore.setDate(System.currentTimeMillis());
        gameScore.setMode(mode.toString());


        String opponent = playerMe + " " + getString(R.string.versus) + " " + playerFriend;
        String playerScore = scoreMe + ":" + scoreFriend;

        gameScore.setOpponent(opponent);
        gameScore.setScore(playerScore);
        gameScore.setResult(result);

        try {
            ((DotsAndBoxesApplication) getActivity().getApplication()).getDb().gameScoreDao().insertGameScore(gameScore);
        } catch (Exception e) {
            Timber.e(e.getCause());
        }


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

    public interface OnFragmentInteractionListener {
        void onReplayRequested(Bundle arguments);

        void onMenuRequested();
    }

}
