package com.tiptop.dotsandboxes.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import java.text.MessageFormat;

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
public class WonLostFragment extends DialogFragment {
    public static final int FRAGMENT_ID = 31783;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_won_player)
    ImageView imgWonPlayer;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.ll_restart)
    LinearLayout llRestart;
    @BindView(R.id.ll_home)
    LinearLayout llHome;

    String winnerName;
    Game.Player winner;

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
    private boolean isRequestSender;


    public static WonLostFragment newInstance(Bundle args) {
        WonLostFragment f = new WonLostFragment();

        if (args != null)
            f.setArguments(args);

        return f;
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
        isRequestSender = getArguments().getBoolean(Constants.ISREQUESTSENDER, false);
        if (!isRequestSender) {
            String tempName = player1Name;
            int tempScore = player1Score;
            player1Name = player2Name;
            player2Name = tempName;

           /* player1Score = player2Score;
            player2Score = tempScore;*/
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_won_lost, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setSelected(true);

        if (player1Score > player2Score) {
            winner = Game.Player.PLAYER1;


            if (isTurnMe) {
                tvTitle.setText(getContext().getString(R.string.you_won));
                Glide.with(getActivity()).load(getActivity().getDrawable(R.drawable.winner_trofi_big_icon)).into(imgWonPlayer);
                result = getString(R.string.win);
                playerMe = getString(R.string.me);

                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player2Name;
                } else if (gameMode.equals(Constants.ONLINE)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                }
                scoreMe = String.valueOf(player1Score);
                scoreFriend = String.valueOf(player2Score);
                tvScore.setText(MessageFormat.format("{0}:{1}", player1Score, player2Score));
            } else {
                result = getString(R.string.lost);
                playerMe = getString(R.string.me);
                tvTitle.setText(getContext().getString(R.string.better_luck_next_time));
                Glide.with(getActivity()).load(getActivity().getDrawable(R.drawable.lost_game_graphic)).into(imgWonPlayer);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player1Name;
                } else if (gameMode.equals(Constants.ONLINE)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                }

                scoreMe = String.valueOf(player2Score);
                scoreFriend = String.valueOf(player1Score);
                tvScore.setText(MessageFormat.format("{0}:{1}", player2Score, player1Score));
            }
        } else if (player1Score < player2Score) {
            winner = Game.Player.PLAYER2;

            if (isTurnMe) {
                result = getString(R.string.lost);
                playerMe = getString(R.string.me);
                tvTitle.setText(getContext().getString(R.string.better_luck_next_time));
                Glide.with(getActivity()).load(getActivity().getDrawable(R.drawable.lost_game_graphic)).into(imgWonPlayer);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player2Name;
                } else if (gameMode.equals(Constants.ONLINE)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                }
                scoreMe = String.valueOf(player1Score);
                scoreFriend = String.valueOf(player2Score);
                tvScore.setText(MessageFormat.format("{0}:{1}", player2Score, player1Score));
            } else {
                result = getString(R.string.win);
                playerMe = getString(R.string.me);
                tvTitle.setText(getContext().getString(R.string.you_won));
                Glide.with(getActivity()).load(getActivity().getDrawable(R.drawable.winner_trofi_big_icon)).into(imgWonPlayer);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player1Name;
                } else if (gameMode.equals(Constants.ONLINE)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                }
                scoreMe = String.valueOf(player2Score);
                scoreFriend = String.valueOf(player1Score);
                tvScore.setText(MessageFormat.format("{0}:{1}", player1Score, player2Score));
            }

        } else {
            winner = Game.Player.NONE;
            tvTitle.setText(getContext().getString(R.string.draw));
            if (isTurnMe) {
                result = getString(R.string.tie);
                playerMe = getString(R.string.me);

                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player2Name;
                } else if (gameMode.equals(Constants.ONLINE)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                }

                scoreMe = String.valueOf(player1Score);
                scoreFriend = String.valueOf(player2Score);
            } else {
                result = getString(R.string.tie);
                playerMe = getString(R.string.me);
                if (gameMode.equals(Constants.FRIEND)) {
                    playerFriend = player1Name;
                } else if (gameMode.equals(Constants.ONLINE)) {
                    playerFriend = player2Name;
                } else {
                    playerFriend = getString(R.string.robot);
                }
                scoreMe = String.valueOf(player2Score);
                scoreFriend = String.valueOf(player1Score);
            }
            tvScore.setText(MessageFormat.format("{0}:{1}", player1Score, player2Score));
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
