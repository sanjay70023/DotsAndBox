package com.tiptop.dotsandboxes.fragments;


import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.activities.MainActivity;
import com.tiptop.dotsandboxes.event_bus.RxBus;
import com.tiptop.dotsandboxes.event_bus.events.BotComputeEvent;
import com.tiptop.dotsandboxes.event_bus.events.EmitSoundEvent;
import com.tiptop.dotsandboxes.event_bus.events.GameEndEvent;
import com.tiptop.dotsandboxes.event_bus.events.OpponentMoveEvent;
import com.tiptop.dotsandboxes.event_bus.events.PlayerMoveEvent;
import com.tiptop.dotsandboxes.event_bus.events.ScoreMadeEvent;
import com.tiptop.dotsandboxes.event_bus.events.SquareCompletedEvent;
import com.tiptop.dotsandboxes.event_bus.events.TurnChangeEvent;
import com.tiptop.dotsandboxes.game.controllers.Game;
import com.tiptop.dotsandboxes.game.controllers.PlayerBot;
import com.tiptop.dotsandboxes.game.models.Edge;
import com.tiptop.dotsandboxes.utils.Constants;
import com.tiptop.dotsandboxes.utils.CountDownTimerWithPause;
import com.tiptop.dotsandboxes.utils.PrefUtils;
import com.tiptop.dotsandboxes.views.BoardView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class GameFragment extends Fragment {

    public static final int FRAGMENT_ID = 41783;
    public static final String ARG_PLAYER1_SCORE = "args.score.player1";
    public static final String ARG_PLAYER2_SCORE = "args.score.player2";
    public static final String ARG_GAME_MODE = "args.game.mode";
    public static final String ARG_PLAYER1_NAME = "args.name.payer1";
    public static final String ARG_PLAYER2_NAME = "args.name.payer2";
    private static final long BOT_DELAY_TIME = 1500;

    @BindView(R.id.tv_player_1_score)
    TextView tvPlayer1Score;
    @BindView(R.id.tv_player_2_score)
    TextView tvPlayer2Score;
    @BindView(R.id.tv_tuen_text)
    TextView tvTuenText;
    @BindView(R.id.boardView)
    BoardView boardView;
    @BindView(R.id.img_player1)
    ImageView imgPlayer1;
    @BindView(R.id.tv_player_1_name)
    TextView tvPlayer1Name;
    @BindView(R.id.ll_player1)
    LinearLayout llPlayer1;
    @BindView(R.id.tv_player_2_name)
    TextView tvPlayer2Name;
    @BindView(R.id.img_player2)
    ImageView imgPlayer2;
    @BindView(R.id.ll_player2)
    LinearLayout llPlayer2;
    private Subscription subscription;
    private Game game;
    private PlayerBot bot;
    private Game.Mode mode;
    private Vibrator vibrator;
    private boolean shouldVibrate;
    private OnFragmentInteractionListener mListener;
    private String difficulty = "";
    private String player1Name = "";
    private String player2Name = "";
    private boolean isTurnMe;
    private String playerYou;
    private String playerFriend;
    private String gameMode;
    private String playerNameMeFromPreference;
    private long botDelayTime = 0L;
    private PrefUtils prefUtils;
    private CountDownTimerWithPause progressTimer;
    private boolean shouldSound;
    private boolean isRequestSender;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(Bundle args) {
        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static long getBotDelayTime(int rows, int columns) {
        if (rows == 4 && columns == 4 || rows == 5 && columns == 5 || rows == 6 && columns == 6) {
            return BOT_DELAY_TIME;
        }
        return 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getWindow().setWindowAnimations(
                R.style.dialog_animation_fade);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressTimer != null && progressTimer.isPaused()) {
            progressTimer.resume();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefUtils = new PrefUtils(getActivity());

        playerNameMeFromPreference = prefUtils.getString(Constants.prefrences.NAME);


        Timber.e("\n \n Name = " + playerNameMeFromPreference);


        int rows = getResources().getInteger(R.integer.default_rows);
        int columns = getResources().getInteger(R.integer.default_columns);

        Bundle args = getArguments();

        if (args != null) {
            rows = args.getInt(Constants.SELECTED_ROW, rows);
            columns = args.getInt(Constants.SELECTED_COLUMN, columns);
            playerYou = args.getString(Constants.PLAYER1_NAME, Constants.PLAYER_YOU);
            playerFriend = args.getString(Constants.PLAYER2_NAME, Constants.PLAYER_ROBOT);
            gameMode = args.getString(Constants.GAME_MODE);
            isRequestSender = args.getBoolean(Constants.ISREQUESTSENDER, false);
           /* playerNameMeFromPreference = args.getString(Constants.prefrences.NAME);
            playerMeImageFfromPreference = args.getString(Constants.prefrences.PROFILE_IMAGE);*/
        }

        difficulty = rows + "_" + columns;
        game = new Game(rows, columns);
        bot = new PlayerBot(game);
        botDelayTime = getBotDelayTime(rows, columns);


        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        shouldVibrate = sharedPref.getBoolean(getContext().getString(R.string.pref_key_vibrate), true);
        SharedPreferences sharedPrefSoound = PreferenceManager.getDefaultSharedPreferences(getContext());
        shouldSound = sharedPrefSoound.getBoolean(getContext().getString(R.string.pref_key_sound), true);


        // RxBus
        initBusSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this, root);

        //it will set the game on the board
        boardView.setGame(game);
        ((MainActivity) getActivity()).commonToolbar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarBack.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarMusic.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarSound.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarVibrate.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarSetting.setVisibility(View.GONE);
        ((MainActivity) getActivity()).tvToolbarTitle.setText("");
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.INVISIBLE);

   /*     if (playerMeImageFfromPreference != null && !TextUtils.isEmpty(playerMeImageFfromPreference)) {
            ((MainActivity) getActivity()).setImageToImageView(playerMeImageFfromPreference, imgPlayer1);
        } else {
            ((MainActivity) getActivity()).setImageToImageViewFromResource(R.drawable.profile_image, imgPlayer1);
        }*/

        switch (gameMode) {
            case Constants.ROBOT:
                if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                    tvPlayer1Name.setText(playerNameMeFromPreference);
                } else {
                    tvPlayer1Name.setText(playerYou);
                }

                mode = Game.Mode.CPU;

                setImageToImageViewFromResource(R.drawable.robot1,imgPlayer2);
                tvPlayer2Name.setText(getString(R.string.robot));
                break;
            case Constants.FRIEND:
                tvPlayer1Name.setText(playerYou);
                mode = Game.Mode.PLAYER;

                setImageToImageViewFromResource(R.drawable.friend_icon,imgPlayer2);
                tvPlayer2Name.setText(playerFriend);
                break;
            case Constants.ONLINE:
                tvPlayer1Name.setText(playerYou);
                mode = Game.Mode.PLAYER;

                setImageToImageViewFromResource(R.drawable.friend_icon,imgPlayer2);
                tvPlayer2Name.setText(playerFriend);
                break;
        }

        if (!gameMode.equals(Constants.ONLINE)) {
            if (mListener != null) {
                mListener.onChooseTurnFragmentLoad();
            }
            boardView.enableInteraction();
        } else {
            if (isRequestSender) {
                onPlayer1Clicked();
                boardView.enableInteraction();
            } else {
                onPlayer2Clicked();
                boardView.disableInteraction();
            }

        }
        tvPlayer1Name.setSelected(true);
        tvPlayer2Name.setSelected(true);
        tvTuenText.setSelected(true);
        return root;
    }

    /**
     * This function used to click for the mService 2
     */
    public void onPlayer2Clicked() {

        if (mode == Game.Mode.CPU) {
            player2Name = getString(R.string.you);
            player1Name = getString(R.string.robot);

            if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                tvPlayer2Name.setText(playerNameMeFromPreference);
            } else {
                tvPlayer2Name.setText(player2Name);
            }
            setImageToImageViewFromResource(R.drawable.profile_image,imgPlayer2);
            setImageToImageViewFromResource(R.drawable.robot1,imgPlayer1);

            tvPlayer1Name.setText(player1Name);
            isTurnMe = false;
            boardView.setTurnMe(isTurnMe);
            setTurnText(Game.Player.PLAYER1);
            // setTurnText(Game.Player.PLAYER2);
            takeTurnFromBot();
        } else if (mode == Game.Mode.PLAYER) {
            player2Name = playerYou;
            player1Name = playerFriend;


            tvPlayer2Name.setText(playerYou);
            setImageToImageViewFromResource(R.drawable.profile_image,imgPlayer2);
            setImageToImageViewFromResource(R.drawable.friend_icon,imgPlayer1);

            tvPlayer1Name.setText(playerFriend);
            isTurnMe = false;
            boardView.setTurnMe(isTurnMe);
            setTurnText(Game.Player.PLAYER1);
        }
    }

    /**
     * This function is used to click for mService 1
     */
    public void onPlayer1Clicked() {
        if (mode == Game.Mode.CPU) {
            player1Name = getString(R.string.you);
            isTurnMe = true;
            player2Name = getString(R.string.robot);

            if (playerNameMeFromPreference != null && !TextUtils.isEmpty(playerNameMeFromPreference)) {
                tvPlayer1Name.setText(playerNameMeFromPreference);
            } else {
                tvPlayer1Name.setText(player1Name);
            }

                setImageToImageViewFromResource(R.drawable.profile_image, imgPlayer1);
            setImageToImageViewFromResource(R.drawable.robot1, imgPlayer2);

            boardView.setTurnMe(isTurnMe);
            setTurnText(Game.Player.PLAYER1);
        } else if (mode == Game.Mode.PLAYER) {
            player1Name = playerYou;
            isTurnMe = true;
            player2Name = playerFriend;


            tvPlayer1Name.setText(playerYou);

                setImageToImageViewFromResource(R.drawable.profile_image, imgPlayer1);

            boardView.setTurnMe(isTurnMe);
            setTurnText(Game.Player.PLAYER1);
            setImageToImageViewFromResource(R.drawable.friend_icon, imgPlayer2);
            tvPlayer2Name.setText(playerFriend);
        }
    }

    private void initBusSubscription() {
        subscription = RxBus.getInstance()
                .getBus()
                .doOnError(throwable -> Timber.e(throwable.getCause()))
                .subscribeOn(Schedulers.computation())
                .onBackpressureBuffer()
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    static final String TAG = "RxBusGameFragment";

                    @Override
                    public void call(Object event) {
                        Timber.e("call: " + event.getClass().toString());

                        /*
                         * The bot made a move
                         */
                        if (event instanceof BotComputeEvent) {
                            BotComputeEvent botMoveEvent = (BotComputeEvent) event;
                            int boxesCompleted = game.makeAMove(botMoveEvent.botMove.getDotStart(), botMoveEvent.botMove.getDotEnd());
                            boardView.invalidate();

                            if (boxesCompleted > 0 && game.getState() != Game.State.END) {

                                takeTurnFromBot();
                            } else {
                                boardView.invalidate();
                                boardView.enableInteraction();

                                setTurnText(Game.Player.PLAYER1);

                            }

                            if (shouldSound) {
                                RxBus.getInstance().send(new EmitSoundEvent());
                            }

                            if (boxesCompleted > 0) {
                                RxBus.getInstance().send(new SquareCompletedEvent());
                            }

                        }

                        /*
                         *  The mService made a move
                         */
                        else if (event instanceof PlayerMoveEvent) {
                            if (gameMode.equals(Constants.ONLINE)) {

                                PlayerMoveEvent playermoveEvent = (PlayerMoveEvent) event;
                                // this will send the message to connected player and make the move
//                                ((MainActivity) getActivity()).sendMessageOnline("start=" + playermoveEvent.playerMove.getDotStart() + ",end=" + playermoveEvent.playerMove.getDotEnd());
                                int boxesCompleted = game.makeAMove(playermoveEvent.playerMove.getDotStart(), playermoveEvent.playerMove.getDotEnd());
                                boardView.invalidate();
                                boardView.disableInteraction();

                                if (shouldSound) {
                                    RxBus.getInstance().send(new EmitSoundEvent());
                                }

                                if (boxesCompleted > 0) {
                                    RxBus.getInstance().send(new SquareCompletedEvent());
                                }
                            } else {
                                PlayerMoveEvent playermoveEvent = (PlayerMoveEvent) event;
                                int boxesCompleted = game.makeAMove(playermoveEvent.playerMove.getDotStart(), playermoveEvent.playerMove.getDotEnd());
                                boardView.invalidate();

                                if (boxesCompleted == 0) {
                                    setTurnText(Game.Player.PLAYER2);


                                    if (mode == Game.Mode.CPU) {

                                        takeTurnFromBot();

                                    } else {
                                        boardView.invalidate();
                                        boardView.enableInteraction();
                                    }
                                }

                                if (shouldSound) {
                                    RxBus.getInstance().send(new EmitSoundEvent());
                                }

                                if (boxesCompleted > 0) {
                                    RxBus.getInstance().send(new SquareCompletedEvent());
                                }
                            }
                        }
                        /*
                         *  The opponent made a move
                         */
                        else if (event instanceof OpponentMoveEvent) {
                            setTurnText(Game.Player.PLAYER1);

                            // change the avatar borders
                        } else if (event instanceof TurnChangeEvent) {
                            setTurnText(((TurnChangeEvent) event).nextPlayer);
                        }
                        /*
                         *  Register the event and reflect the score
                         */
                        else if (event instanceof ScoreMadeEvent) {
                            if (gameMode.equals(Constants.ONLINE)) {
                                ScoreMadeEvent scoreEvent = (ScoreMadeEvent) event;
                                if (scoreEvent.scoringPlayer == Game.Player.PLAYER1) {
                                    if (isRequestSender) {
                                        boardView.enableInteraction();
                                    } else {
                                        boardView.disableInteraction();
                                    }
                                    tvPlayer1Score.setText(String.format(Locale.getDefault(), "%d", scoreEvent.score));
                                } else {
                                    if (isRequestSender) {
                                        boardView.disableInteraction();
                                    } else {
                                        boardView.enableInteraction();
                                    }
                                    tvPlayer2Score.setText(String.format(Locale.getDefault(), "%d", scoreEvent.score));
                                }
                            } else {
                                ScoreMadeEvent scoreEvent = (ScoreMadeEvent) event;
                                if (scoreEvent.scoringPlayer == Game.Player.PLAYER1) {
                                    tvPlayer1Score.setText(String.format(Locale.getDefault(), "%d", scoreEvent.score));
                                } else {
                                    tvPlayer2Score.setText(String.format(Locale.getDefault(), "%d", scoreEvent.score));
                                }
                            }
                        }
                        /*
                         * Show final credits
                         */
                        else if (event instanceof GameEndEvent) {
                            Bundle args = loadGameResultFragment();

                            if (mListener != null) {
                                mListener.onWinFragmentLoad(ResultFragment.FRAGMENT_ID, args);
                            }

                        } else if (event instanceof EmitSoundEvent) {
                            if (shouldSound) {
                                if (mListener != null) {
                                    mListener.onSoundRequested();
                                }
                            }
                        } else if (event instanceof SquareCompletedEvent) {
                            if (shouldVibrate) {
                                vibrator.vibrate(getResources().getInteger(R.integer.vibrate_duration));
                            }
                        }
                    }
                });
    }

    public void makeMove(int startDot, int endDot) {
        int boxesCompleted = game.makeAMove(startDot, endDot);
        boardView.invalidate();

        if (boxesCompleted == 0) {
            setTurnText(Game.Player.PLAYER1);

            boardView.invalidate();
            boardView.enableInteraction();
        }

        if (shouldSound) {
            RxBus.getInstance().send(new EmitSoundEvent());
        }

        if (boxesCompleted > 0) {
            boardView.disableInteraction();
            RxBus.getInstance().send(new SquareCompletedEvent());
        }
    }

    public Bundle loadGameResultFragment() {
        Bundle args = getArguments();

        if (args == null)
            args = new Bundle();

        int player1Score = game.getBoard().getScore(Game.Player.PLAYER1);
        int player2Score = game.getBoard().getScore(Game.Player.PLAYER2);

        args.putInt(ARG_PLAYER1_SCORE, player1Score);
        args.putInt(ARG_PLAYER2_SCORE, player2Score);
        args.putSerializable(ARG_GAME_MODE, mode);
        args.putString(ARG_PLAYER1_NAME, player1Name);
        args.putString(ARG_PLAYER2_NAME, player2Name);
        args.putString(Constants.GAME_MODE, gameMode);
        args.putBoolean(Constants.TURN_ME, isTurnMe);
        args.putBoolean(Constants.ISREQUESTSENDER, isRequestSender);
        return args;
    }

    public void onSoundRequestClick() {

        SharedPreferences sharedPrefSoound = PreferenceManager.getDefaultSharedPreferences(getContext());
        shouldSound = sharedPrefSoound.getBoolean(getContext().getString(R.string.pref_key_sound), true);

        boardView.disableInteraction();
        if (mListener != null) {
            mListener.onSoundRequested();
        }
        boardView.enableInteraction();
        boardView.invalidate();
    }

    public void onVibrateRequested() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        shouldVibrate = sharedPref.getBoolean(getContext().getString(R.string.pref_key_vibrate), true);

        boardView.disableInteraction();
        if (shouldVibrate) {
            vibrator.vibrate(getResources().getInteger(R.integer.vibrate_duration));
        }
        boardView.enableInteraction();
        boardView.invalidate();
    }

    private void takeTurnFromBot() {
        boardView.disableInteraction();


        progressTimer = new CountDownTimerWithPause(botDelayTime, 4, true) {
            @Override
            public void onTick(long millisUntilFinished) {
                //progressBar.setProgress((int) ((float) millisUntilFinished / 4f));
            }

            @Override
            public void onFinish() {
                // progressBar.setProgress(0);
                // Offload to an async calculation on a separate Rx Thread
                Observable.fromCallable(new Callable<Edge>() {
                    @Override
                    public Edge call() throws Exception {

                        return bot.getNextMove();

                    }
                })
                        .doOnError(throwable -> Timber.e(throwable.getCause()))
                        .subscribeOn(Schedulers.computation())
                        .onBackpressureDrop()
                        .observeOn(AndroidSchedulers.mainThread())
                        /*.delay(botDelayTime, TimeUnit.MILLISECONDS)*/
                        .subscribe(new Action1<Edge>() {
                            @Override
                            public void call(Edge edge) {

                                Timber.e("onPostExecute: " + edge.getKey());
                                RxBus.getInstance().send(new BotComputeEvent(edge));

                            }
                        });
            }
        };

        progressTimer.create();

    }

    public void setTurnText(Game.Player player) {

        switch (gameMode) {
            case Constants.ROBOT:
                setTextWhenRobot(player);
                break;
            case Constants.FRIEND:
                setTextWhenFriend(player);
                break;
            case Constants.ONLINE:
                setTextWhenFriend(player);
                break;
        }


    }

    public void setTextWhenRobot(Game.Player player) {
        String playerName;
        Spannable turnString;
        if (isTurnMe) {
            if (player == Game.Player.PLAYER1) {
                playerName = getString(R.string.player1TurnName);
                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.turn_text), playerName));

            } else {
                playerName = getString(R.string.player2TurnName);

                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.turn_text), playerName));

            }
            tvTuenText.setText(turnString);
        } else {
            if (player == Game.Player.PLAYER1) {
                playerName = getString(R.string.player2TurnName);
                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.turn_text), playerName));

            } else {
                playerName = getString(R.string.player1TurnName);

                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.turn_text), playerName));

            }
            tvTuenText.setText(turnString);
        }
    }

    public void setTextWhenFriend(Game.Player player) {
        String playerName;
        Spannable turnString;
        if (isTurnMe) {
            if (player == Game.Player.PLAYER1) {
                playerName = playerYou;
                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.player_turn_text), playerName));

            } else {
                playerName = playerFriend;

                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.player_turn_text), playerName));

            }
            tvTuenText.setText(turnString);
        } else {
            if (player == Game.Player.PLAYER1) {
                playerName = playerFriend;
                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.player_turn_text), playerName));

            } else {
                playerName = playerYou;

                turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.player_turn_text), playerName));

            }
            tvTuenText.setText(turnString);
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
        subscription.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putString(ARG_BOARD, game.getBoard().toString());
        //outState.putBoolean(ARG_P1_NEXT, game.getState() == Game.State.PLAYER1_TURN);
        super.onSaveInstanceState(outState);
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

    public boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

    public interface OnFragmentInteractionListener {
        void onWinFragmentLoad(int fragmentId, Bundle args);

        void onSoundRequested();

        void onChooseTurnFragmentLoad();
    }
}
