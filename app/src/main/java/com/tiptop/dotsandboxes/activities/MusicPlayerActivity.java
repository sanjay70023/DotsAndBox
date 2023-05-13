package com.tiptop.dotsandboxes.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.services.MusicPlayerService;
import com.tiptop.dotsandboxes.services.MusicService;
//import com.tiptop.dotsandboxes.utils.AdsWrapper;
import com.tiptop.dotsandboxes.utils.Constants;
import com.tiptop.dotsandboxes.utils.PrefUtils;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.games.Games;
//import com.google.android.gms.games.GamesActivityResultCodes;
//import com.google.android.gms.games.GamesCallbackStatusCodes;
//import com.google.android.gms.games.InvitationsClient;
//import com.google.android.gms.games.Player;
//import com.google.android.gms.games.PlayersClient;
//import com.google.android.gms.games.RealTimeMultiplayerClient;
//import com.google.android.gms.games.multiplayer.Invitation;
//import com.google.android.gms.games.multiplayer.InvitationCallback;
//import com.google.android.gms.games.multiplayer.Multiplayer;
//import com.google.android.gms.games.multiplayer.Participant;
//import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
//import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
//import com.google.android.gms.games.multiplayer.realtime.Room;
//import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
//import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
//import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.preference.PreferenceManager;

/**
 * This activity is act as base activity for the application. it will bind the @MusicPlayerService
 */
public abstract class MusicPlayerActivity extends AppCompatActivity {
    final static int RC_LOOK_AT_MATCHES = 10001;
    private static final String TAG = "MusicPlayerActivity";
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SELECT_PLAYERS = 9010;
    private static final int RC_WAITING_ROOM = 9007;
    private static final int RC_INVITATION_INBOX = 9008;
    public MusicPlayerService mService;
    public boolean doubleBackToExitPressedOnce;
//    public AdsWrapper adsWrapper;
    public PrefUtils prefUtils;
    boolean serviceBound = false;
    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount mSignedInAccount = null;
    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;
//    RoomConfig.Builder roomBuilder;
    HashSet<Integer> pendingMessageSet = new HashSet<>();
//    private GoogleSignInClient mGoogleSignInClient = null;
    // Client used to interact with the Invitation system.
//    private InvitationsClient mInvitationsClient = null;
    // Client used to interact with the TurnBasedMultiplayer system.
//    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;
    private String mPlayerId;
    private GoogleSignInAccount googleSignInAccount;
//    private Room mRoom;
//    private RoomConfig mJoinedRoomConfig;
    private String senderInvitationId;
    private boolean mBoundMusicService = false;
    private String row;
    private String column;
    private String nameFromPrefrence;
    private String mMyParticipantId;
    private String mPlayerName;
    private boolean isRequestSender;
    private String mParticipantName;
    private boolean mPlaying;
    private AlertDialog dialogAcceptReject;
    private String myCreatedRoomId;
    private boolean isGameStarted;
    private int inviteesSize;

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mService = binder.getService();
            serviceBound = true;

            Intent intent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);

            boolean playMusic = PreferenceManager.getDefaultSharedPreferences(MusicPlayerActivity.this).getBoolean(getString(R.string.pref_key_music), true);

            if (playMusic) {
                intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
            } else {
                intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
            }

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

            Toast.makeText(MusicPlayerActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            startService(new Intent(MusicPlayerService.ACTION_STOP_MUSIC));
            serviceBound = false;
        }
    };
    /**
     * this handleMessageSentCallback will get call back once message has send
     * and it will remove the token id from pending message list
     */
//    private RealTimeMultiplayerClient.ReliableMessageSentCallback handleMessageSentCallback =
//            new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
//                @Override
//                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientId) {
//                    // handle the message being sent.
//                    Log.e(TAG, "onRealTimeMessageSent: message send" + recipientId);
//                    synchronized (this) {
//                        pendingMessageSet.remove(tokenId);
//                    }
//                }
//            };
//
//    /**
//     * this mRoomStatusCallbackHandler will gives each and every call from room players creation to disconnected form room
//     */
//    private RoomStatusUpdateCallback mRoomStatusCallbackHandler = new RoomStatusUpdateCallback() {
//        @Override
//        public void onRoomConnecting(@Nullable Room room) {
//            Log.d(TAG, "onRoomConnecting: onRoomConnecting");
//            mRoom = room;
//            // Update the UI status since we are in the process of connecting to a specific room.
//        }
//
//        @Override
//        public void onRoomAutoMatching(@Nullable Room room) {
//            Log.d(TAG, "onRoomConnecting: onRoomAutoMatching ");
//            mRoom = room;
//            // Update the UI status since we are in the process of matching other players.
//        }
//
//        @Override
//        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
//            Log.d(TAG, "onRoomConnecting: onPeerInvitedToRoom");
//            mRoom = room;
//            // Update the UI status since we are in the process of matching other players.
//        }
//
//        @Override
//        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
//            Log.d(TAG, "onRoomConnecting: onPeerDeclined");
//            mRoom = room;
//            // Peer declined invitation, see if game should be canceled
//            if (!mPlaying) {
//                // player wants to leave the room.
//                mRealTimeMultiplayerClient
//                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            }
//        }
//
//        @Override
//        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
//            Log.d(TAG, "onRoomConnecting: onPeerJoined");
//            mRoom = room;
//            // Update UI status indicating new players have joined!
//        }
//
//        @Override
//        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
//            Log.d(TAG, "onRoomConnecting: onPeerLeft");
//            mRoom = room;
//            // Peer left, see if game should be canceled.
//            if (!mPlaying) {
//                // player wants to leave the room.
//                gameOver(getString(R.string.player_left_the_match));
//                if (mRoom != null && mRealTimeMultiplayerClient != null && mJoinedRoomConfig != null) {
//                    mRealTimeMultiplayerClient.leave(mJoinedRoomConfig, mRoom.getRoomId())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    // mRoomId = null;
//                                    mJoinedRoomConfig = null;
//                                }
//                            });
//                }
//                mJoinedRoomConfig = null;
//               /* mRealTimeMultiplayerClient
//                        .leave(mJoinedRoomConfig, mRoom.getRoomId());*/
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            }
//        }
//
//        @Override
//        public void onConnectedToRoom(@Nullable Room room) {
//            Log.d(TAG, "onRoomConnecting: onConnectedToRoom");
//            // Connected to room, record the room Id.
//            mRoom = room;
//
//            Games.getPlayersClient(MusicPlayerActivity.this, GoogleSignIn.getLastSignedInAccount(MusicPlayerActivity.this))
//                    .getCurrentPlayerId().addOnSuccessListener(new OnSuccessListener<String>() {
//                @Override
//                public void onSuccess(String playerId) {
//
//                    mMyParticipantId = mRoom.getParticipantId(playerId);
//                }
//            });
//        }
//
//        @Override
//        public void onDisconnectedFromRoom(@Nullable Room room) {
//            Log.d(TAG, "onRoomConnecting: onDisconnectedFromRoom");
//
//            // This usually happens due to a network error, leave the game.
//            // player wants to leave the room.
//            gameOver("");
//            mPlaying = false;
//            if (mRoom != null && mRealTimeMultiplayerClient != null && mJoinedRoomConfig != null) {
//                mRealTimeMultiplayerClient
//                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
//            }
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            // show error message and return to main screen
//            mRoom = null;
//            mJoinedRoomConfig = null;
//        }
//
//        @Override
//        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
//            Log.d(TAG, "onRoomConnecting: onPeersConnected");
//            mRoom = room;
//           /* if (mPlaying) {
//                // add new player to an ongoing game
//            } else if (shouldStartGame(room)) {
//                // start game!
//            }*/
//        }
//
//        @Override
//        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
//            Log.d(TAG, "onRoomConnecting: onPeersDisconnected");
//            mRoom = room;
//            if (mPlaying) {
//                gameOver("Player disconnected.");
//                mPlaying = false;
//                if (mRoom.getRoomId() != null && mRealTimeMultiplayerClient != null && mJoinedRoomConfig != null) {
//                    mRealTimeMultiplayerClient.leave(mJoinedRoomConfig, mRoom.getRoomId())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    // mRoomId = null;
//                                    mJoinedRoomConfig = null;
//                                }
//                            });
//                }
//                mJoinedRoomConfig = null;
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            }
//        }
//
//        @Override
//        public void onP2PConnected(@NonNull String participantId) {
//            Log.d(TAG, "onRoomConnecting: onP2PConnected");
//
//            // Update status due to new peer to peer connection.
//        }
//
//        @Override
//        public void onP2PDisconnected(@NonNull String participantId) {
//            Log.d(TAG, "onRoomConnecting: onP2PDisconnected");
//            // Update status due to  peer to peer connection being disconnected.
//        }
//    };
//    /**
//     * this listener will receives all real time message send by another player
//     */
//    private OnRealTimeMessageReceivedListener mMessageReceivedHandler =
//            new OnRealTimeMessageReceivedListener() {
//                @Override
//                public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
//                    // Handle messages received here.
//                    Log.e(TAG, "onRealTimeMessageReceived: message receive");
//                    byte[] message = realTimeMessage.getMessageData();
//                    String reciveMsg = new String(message);
//                    Log.e(TAG, "onRealTimeMessageReceived: " + reciveMsg);
//                    // if reciveMsg contains row then it will start the game
//                    if (reciveMsg.contains(getString(R.string.row_multiplayer))) {
//                        isRequestSender = false;
//                        String rowCol[] = reciveMsg.split(",");
//                        row = rowCol[0].split("=")[1];
//                        column = rowCol[1].split("=")[1];
//                        if (dialogAcceptReject != null) {
//                            if (dialogAcceptReject.isShowing()) {
//                                dialogAcceptReject.dismiss();
//                            }
//                        }
//
//                        Log.e(TAG, "start game from message recive: ");
//                       /* if (inviteesSize == 0) {
//                            startGame(Integer.parseInt(row), Integer.parseInt(column), mParticipantName, mPlayerName, MusicPlayerActivity.this, false);
//                        } else {*/
//                            startGame(Integer.parseInt(row), Integer.parseInt(column), mPlayerName, mParticipantName, MusicPlayerActivity.this, false);
//                      //  }
//
//
//                    }
//                    // if reciveMsg contains start then it will make the move
//                    if (reciveMsg.contains(getString(R.string.start_multiplayer))) {
//                        String startEnd[] = reciveMsg.split(",");
//                        String start = startEnd[0].split("=")[1];
//                        String end = startEnd[1].split("=")[1];
//                        makeMove(Integer.parseInt(start), Integer.parseInt(end));
//                    }
//                    // if reciveMsg contains "repeat?" then it will show the repeat dialogue
//                    if (reciveMsg.contains(getString(R.string.repeat_multiplayer))) {
//                        showRepeatDialouge();
//                    }
//                    // if reciveMsg contains "repeat:yes" then it will start the new game
//                    if (reciveMsg.contains(getString(R.string.repeat_yes_multiplayer))) {
//                        repeatAccepted();
//                    }
//                    // if reciveMsg contains "repeat:no" then it will cancel the game and throw the user to the home screen
//                    if (reciveMsg.contains(getString(R.string.repeat_no_multiplayer))) {
//                        repeatRejected();
//                    }
//                    // process message contents...
//                }
//            };
//    /**
//     * this will give all callbacks of room
//     */
//    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {
//        @Override
//        public void onRoomCreated(int code, @Nullable Room room) {
//            // Update UI and internal state based on room updates.
//
//            if (code == GamesCallbackStatusCodes.OK && room != null) {
//                mRoom = room;
//                myCreatedRoomId = room.getRoomId();
//                showWaitingRoom(room, 2);
//                Log.d(TAG, "Room " + room.getRoomId() + " created.");
//            } else {
//                Log.w(TAG, "Error creating room: " + code);
//                // let screen go to sleep
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            }
//        }
//
//        @Override
//        public void onJoinedRoom(int code, @Nullable Room room) {
//            // Update UI and internal state based on room updates.
//
//            if (code == GamesCallbackStatusCodes.OK && room != null) {
//                mRoom = room;
//                showWaitingRoom(room, 2);
//                Log.d(TAG, "Room " + room.getRoomId() + " joined.");
//            } else {
//                Log.w(TAG, "Error joining room: " + code);
//                // let screen go to sleep
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            }
//        }
//
//        @Override
//        public void onLeftRoom(int code, @NonNull String roomId) {
//            Log.d(TAG, "Left room" + roomId);
//        }
//
//        @Override
//        public void onRoomConnected(int code, @Nullable Room room) {
//            if (code == GamesCallbackStatusCodes.OK && room != null) {
//                mRoom = room;
//                showWaitingRoom(room, 2);
//                Log.d(TAG, "Room " + room.getRoomId() + " connected.");
//            } else {
//                Log.w(TAG, "Error connecting to room: " + code);
//                // let screen go to sleep
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            }
//        }
//    };
//    /**
//     * this will gives all the callback of invitation receive and removed
//     */
//    private InvitationCallback mInvitationCallback = new InvitationCallback() {
//        // Called when we get an invitation to play a game. We react by showing that to the user.
//        @Override
//        public void onInvitationReceived(@NonNull Invitation invitation) {
//            // We got an invitation to play a game! So, store it in
//            // mIncomingInvitationId
//            // and show the popup on the screen.
//            Log.e(TAG, "onInvitationReceived: invitation received");
//
//         /*   AlertDialog.Builder builder1 = new AlertDialog.Builder(MusicPlayerActivity.this);
//            builder1.setMessage(invitation.getInviter().getDisplayName() + " wants to play with you.");
//            builder1.setCancelable(false);
//
//            builder1.setPositiveButton("Accept",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            row = "";
//                            column = "";
//                            isRequestSender = false;
//                            roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
//                                    .setOnMessageReceivedListener(mMessageReceivedHandler)
//                                    .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
//                                    .setInvitationIdToAccept(invitation.getInvitationId());
//                            mJoinedRoomConfig = roomBuilder.build();
//                            mRealTimeMultiplayerClient
//                                    .join(mJoinedRoomConfig)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.e(TAG, "onSuccess: accepted");
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.e(TAG, "onFailure: failure");
//                                        }
//                                    })
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Log.e(TAG, "onComplete: complete");
//                                        }
//                                    })
//                                    .addOnCanceledListener(new OnCanceledListener() {
//                                        @Override
//                                        public void onCanceled() {
//                                            Log.e(TAG, "onCanceled: cancel");
//                                        }
//                                    });
//                            // prevent screen from sleeping during handshake
//                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                            dialog.cancel();
//                        }
//                    });
//
//            builder1.setNegativeButton("Reject",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
//                                    .setOnMessageReceivedListener(mMessageReceivedHandler)
//                                    .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler);
//                            mJoinedRoomConfig = roomBuilder.build();
//                            mRealTimeMultiplayerClient
//                                    .declineInvitation(invitation.getInvitationId());
//                            dialog.cancel();
//                        }
//                    });
//
//            dialogAcceptReject = builder1.create();
//            dialogAcceptReject.show();*/
//
//            final Dialog dialogSort = new Dialog(MusicPlayerActivity.this);
//            dialogSort.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialogSort.setContentView(R.layout.fragment_game_request);
//            dialogSort.setCancelable(false);
//            dialogSort.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            AppCompatTextView tvRequest = dialogSort.findViewById(R.id.tv_request);
//            AppCompatTextView tvCancel = dialogSort.findViewById(R.id.tv_cancel);
//            AppCompatTextView tvYes = dialogSort.findViewById(R.id.tv_yes);
//            tvRequest.setText(invitation.getInviter().getDisplayName() + " wants to play with you.");
//            tvYes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    row = "";
//                    column = "";
//                    isRequestSender = false;
//                    roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
//                            .setOnMessageReceivedListener(mMessageReceivedHandler)
//                            .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
//                            .setInvitationIdToAccept(invitation.getInvitationId());
//                    mJoinedRoomConfig = roomBuilder.build();
//                    mRealTimeMultiplayerClient
//                            .join(mJoinedRoomConfig)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.e(TAG, "onSuccess: accepted");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.e(TAG, "onFailure: failure");
//                                }
//                            })
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Log.e(TAG, "onComplete: complete");
//                                }
//                            })
//                            .addOnCanceledListener(new OnCanceledListener() {
//                                @Override
//                                public void onCanceled() {
//                                    Log.e(TAG, "onCanceled: cancel");
//                                }
//                            });
//                    // prevent screen from sleeping during handshake
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                    dialogSort.dismiss();
//                }
//            });
//            tvCancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
//                            .setOnMessageReceivedListener(mMessageReceivedHandler)
//                            .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler);
//                    mJoinedRoomConfig = roomBuilder.build();
//                    mRealTimeMultiplayerClient
//                            .declineInvitation(invitation.getInvitationId());
//                    dialogSort.dismiss();
//                }
//            });
//            if (!dialogSort.isShowing())
//                dialogSort.show();
//          /*  Window windowView = dialogSort.getWindow();
//            windowView.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);*/
//
//        }
//
//        @Override
//        public void onInvitationRemoved(@NonNull String invitationId) {
//            Log.e(TAG, "onInvitationReceived: invitation removed");
//            if (dialogAcceptReject != null) {
//                if (dialogAcceptReject.isShowing()) {
//                    dialogAcceptReject.dismiss();
//                }
//            }
//            if (mIncomingInvitationId != null) {
//                if (mIncomingInvitationId.equals(invitationId)) {
//                    mIncomingInvitationId = null;
//                    //   switchToScreen(mCurScreen); // This will hide the invitation popup
//                }
//            }
//        }
//    };


    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    protected void onStop() {
        doUnbindService();
        super.onStop();
    }

    void doBindService() {
        Intent intent = new Intent(this, MusicService.class);

        // start playing music if the user specified so in the settings screen
        if (!serviceBound) {
            boolean playMusic = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_music), true);
            if (playMusic) {
                intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
            } else {
                intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
            }

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            serviceBound = true;
        }
    }

    void doUnbindService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        adsWrapper = new AdsWrapper.Builder().with(this).addTestDeviceIds(new String[]{"FC0135B6D6269BE7C5D5669065FBF72F"}).build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        prefUtils = new PrefUtils(MusicPlayerActivity.this);
        // this will check user is already signed in or not
        //if yes then it will login silently
        //else it will show sign-in manually
//        if (!isSignedIn()) {
//            startSignInIntent();
//        } else {
//            signInSilently();
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        mPlaying = false;
//        if (mRoom != null) {
//            if (mRoom.getRoomId() != null && mRealTimeMultiplayerClient != null && mJoinedRoomConfig != null) {
//                mRealTimeMultiplayerClient
//                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
//            }
//        }
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            mService.stopSelf();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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

    /**
     * This method is used to load image to image view specified
     *
     * @param imageUrl  it is the url of the image
     * @param imageView it is the imageview, in which image should load
     */
    public void setImageToImageView(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl).apply(RequestOptions.circleCropTransform())
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

    /**
     * this will check is user signed in or not before and return true or false
     */
//    private boolean isSignedIn() {
//        return GoogleSignIn.getLastSignedInAccount(this) != null;
//    }
//
//    /**
//     * this will start sign in intent and show user that app is signing in the google play games
//     */
//    public void startSignInIntent() {
//        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
//    }
//
//    /**
//     * this method sign-in silently in google play games
//     */
//    public void signInSilently() {
//
//        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
//                new OnCompleteListener<GoogleSignInAccount>() {
//                    @Override
//                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                        if (task.isSuccessful()) {
//                            //  hideLoader();
//                            prefUtils.setBoolean(Constants.IS_GOOGLE_SIGN_IN, true);
//                            googleSignInAccount = task.getResult();
//                            /*if (mSignedInAccount != googleSignInAccount) {*/
//
//                            mSignedInAccount = googleSignInAccount;
//
//                            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(MusicPlayerActivity.this, googleSignInAccount);
//                            mInvitationsClient = Games.getInvitationsClient(MusicPlayerActivity.this, googleSignInAccount);
//
//                            // get the playerId from the PlayersClient
//                            PlayersClient playersClient = Games.getPlayersClient(MusicPlayerActivity.this, googleSignInAccount);
//                            playersClient.getCurrentPlayer()
//                                    .addOnSuccessListener(new OnSuccessListener<Player>() {
//                                        @Override
//                                        public void onSuccess(Player player) {
//                                            prefUtils.setBoolean(Constants.IS_GOOGLE_SIGN_IN, true);
//                                            mPlayerId = player.getPlayerId();
//                                            mPlayerName = player.getDisplayName();
//                                            prefUtils.setString(Constants.MY_ONLINE_NAME, mPlayerName);
//                                            Log.e(TAG, "onSuccess: " + mPlayerId + " ,name:" + mPlayerName);
//                                            // switchToMainScreen();
//                                        }
//                                    });
//                            //   }
//                            // register listener so we are notified if we receive an invitation to play
//                            // while we are in the game
//                            mInvitationsClient.registerInvitationCallback(mInvitationCallback);
//                            checkForInvitation();
//                            //   onStartMatchClicked();
//                        } else {
//                            prefUtils.setBoolean(Constants.IS_GOOGLE_SIGN_IN, false);
//                            Log.d(TAG, "signInSilently(): failure", task.getException());
//                            //    hideLoader();
//                        }
//                    }
//                });
//    }
//
//    /**
//     * this method will start the select player intent
//     *
//     * @param row    it will come as selected by user
//     * @param column it will come as selected by user
//     */
//    public void invitePlayers(String row, String column) {
//        this.row = row;
//        this.column = column;
//        isRequestSender = true;
//        if (mRealTimeMultiplayerClient != null) {
//            showLoader();
//            mRealTimeMultiplayerClient
//                    .getSelectOpponentsIntent(1, 1, true)
//                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
//                        @Override
//                        public void onSuccess(Intent intent) {
//                            startActivityForResult(intent, RC_SELECT_PLAYERS);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            hideLoader();
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MusicPlayerActivity.this);
//                            builder1.setMessage(getString(R.string.logged_out_from_google_play_games));
//                            builder1.setCancelable(false);
//
//                            builder1.setPositiveButton(getString(R.string.ok),
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            finishAffinity();
//                                            dialog.cancel();
//                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                        }
//                                    });
//
//                            builder1.setNegativeButton(getString(R.string.cancel),
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            AlertDialog alert = builder1.create();
//                            alert.show();
//                        }
//                    });
//        }
//    }
//
//    /**
//     * this will show waiting room once user select players and click on play
//     */
//    private void showWaitingRoom(Room room, int maxPlayersToStartGame) {
//        showLoader();
//        mRealTimeMultiplayerClient
//                .getWaitingRoomIntent(room, maxPlayersToStartGame)
//                .addOnSuccessListener(new OnSuccessListener<Intent>() {
//                    @Override
//                    public void onSuccess(Intent intent) {
//                        startActivityForResult(intent, RC_WAITING_ROOM);
//                    }
//                });
//    }
//
//    /**
//     * this will used for showing the user list of invitations
//     */
//    private void showInvitationInbox() {
//        mInvitationsClient
//                .getInvitationInboxIntent()
//                .addOnSuccessListener(new OnSuccessListener<Intent>() {
//                    @Override
//                    public void onSuccess(Intent intent) {
//                        startActivityForResult(intent, RC_INVITATION_INBOX);
//                    }
//                });
//    }
//
//    /**
//     * this method will check for invitation once user comes in app from notification
//     */
//    private void checkForInvitation() {
//        Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .getActivationHint()
//                .addOnSuccessListener(
//                        new OnSuccessListener<Bundle>() {
//                            @Override
//                            public void onSuccess(Bundle bundle) {
//                                if (bundle != null) {
//                                    Invitation invitation = bundle.getParcelable(Multiplayer.EXTRA_INVITATION);
//                                    if (invitation != null) {
//                                        RoomConfig.Builder builder = RoomConfig.builder(mRoomUpdateCallback)
//                                                .setOnMessageReceivedListener(mMessageReceivedHandler)
//                                                .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
//                                                .setInvitationIdToAccept(invitation.getInvitationId());
//                                        mJoinedRoomConfig = builder.build();
//                                        mRealTimeMultiplayerClient
//                                                .join(mJoinedRoomConfig);
//                                        // prevent screen from sleeping during handshake
//                                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                                    }
//                                }
//                            }
//                        }
//                );
//
//    }
//
//    /**
//     * this method will leave the room when game is over or user is left
//     * or player disconnected or user reject the game repeat-request
//     */
//    public void leaveRoom() {
//        if (mRoom.getRoomId() != null && mRealTimeMultiplayerClient != null && mJoinedRoomConfig != null) {
//            mRealTimeMultiplayerClient
//                    .leave(mJoinedRoomConfig, mRoom.getRoomId());
//        }
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        row = "";
//        column = "";
//        isRequestSender = false;
//        mJoinedRoomConfig = null;
//        mRoom = null;
//    }
//
//    /**
//     * this method will send the message to all players that are connected to room
//     */
//    public void sendToAllReliably(byte[] message) {
//        String sendMsg = new String(message);
//        if (sendMsg.contains(getString(R.string.row_multiplayer))) {
//            if (isRequestSender) {
//                for (String participantId : mRoom.getParticipantIds()) {
//                    if (!participantId.equals(mMyParticipantId)) {
//                        Task<Integer> task = mRealTimeMultiplayerClient.sendReliableMessage(message, mRoom.getRoomId(), participantId,
//                                handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Integer> task) {
//                                if (sendMsg.contains(getString(R.string.row_multiplayer))) {
//                                    if (dialogAcceptReject != null) {
//                                        if (dialogAcceptReject.isShowing()) {
//                                            dialogAcceptReject.dismiss();
//                                        }
//                                    }
//
//                                    Log.e(TAG, "start game from message send: ");
//                                    startGame(Integer.parseInt(row), Integer.parseInt(column), mPlayerName, mParticipantName, MusicPlayerActivity.this, true);
//
//                                }
//                                recordMessageToken(task.getResult());
//                            }
//                        });
//                    }
//                }
//            }
//        } else {
//            for (String participantId : mRoom.getParticipantIds()) {
//                if (!participantId.equals(mMyParticipantId)) {
//                    Task<Integer> task = mRealTimeMultiplayerClient.sendReliableMessage(message, mRoom.getRoomId(), participantId,
//                            handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Integer> task) {
//                            if (sendMsg.contains(getString(R.string.row_multiplayer))) {
//                                if (dialogAcceptReject != null) {
//                                    if (dialogAcceptReject.isShowing()) {
//                                        dialogAcceptReject.dismiss();
//                                    }
//                                }
//                                /*  if (!isGameStarted) {*/
//                                Log.e(TAG, "start game from message send: ");
//                                startGame(Integer.parseInt(row), Integer.parseInt(column), mPlayerName, mParticipantName, MusicPlayerActivity.this, true);
//                                   /* isGameStarted = true;
//                                }*/
//                            }
//                            recordMessageToken(task.getResult());
//                        }
//                    });
//                }
//            }
//        }
//    }

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void startGame(int row, int column, String myName, String opponentName, Context context, boolean isRequestSender);

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void makeMove(int startDot, int endDot);

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void showRepeatDialouge();

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void repeatAccepted();

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void repeatRejected();

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void gameOver(String showInDialogue);

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void showLoader();

    /**
     * this method is abstract and have declaration in MainActivity.java
     */
    abstract void hideLoader();

    /**
     * this method will save the token id in pending message list
     */
    synchronized void recordMessageToken(int tokenId) {
        pendingMessageSet.add(tokenId);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //this will comes true when user is
//        if (requestCode == RC_SIGN_IN) {
//            //hideLoader();
//            Task<GoogleSignInAccount> task =
//                    GoogleSignIn.getSignedInAccountFromIntent(data);
//            if (task.isSuccessful()) {
//                //this will comes true when user is sign in successfully
//                prefUtils.setBoolean(Constants.IS_GOOGLE_SIGN_IN, false);
//                googleSignInAccount = task.getResult();
//                if (mSignedInAccount != googleSignInAccount) {
//
//                    mSignedInAccount = googleSignInAccount;
//
//                    mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(MusicPlayerActivity.this, googleSignInAccount);
//                    mInvitationsClient = Games.getInvitationsClient(MusicPlayerActivity.this, googleSignInAccount);
//
//                    // get the playerId from the PlayersClient
//                    PlayersClient playersClient = Games.getPlayersClient(MusicPlayerActivity.this, googleSignInAccount);
//                    playersClient.getCurrentPlayer()
//                            .addOnSuccessListener(new OnSuccessListener<Player>() {
//                                @Override
//                                public void onSuccess(Player player) {
//                                    prefUtils.setBoolean(Constants.IS_GOOGLE_SIGN_IN, true);
//                                    mPlayerId = player.getPlayerId();
//                                    mPlayerName = player.getDisplayName();
//                                    prefUtils.setString(Constants.MY_ONLINE_NAME, mPlayerName);
//                                    Log.e(TAG, "onSuccess: " + mPlayerId + " ,name:" + mPlayerName);
//                                    // switchToMainScreen();
//                                }
//                            });
//                }
//                // register listener so we are notified if we receive an invitation to play
//                // while we are in the game
//                mInvitationsClient.registerInvitationCallback(mInvitationCallback);
//                checkForInvitation();
//            } else {
//                //this will comes true when user is sign in not successfully
//                prefUtils.setBoolean(Constants.IS_GOOGLE_SIGN_IN, false);
//            }
//        }
//        if (requestCode == RC_SELECT_PLAYERS) {
//            // Returning from 'Select players to Invite' dialog
//
//            if (resultCode != Activity.RESULT_OK) {
//                // Canceled or some other error.
//                hideLoader();
//                return;
//            }
//
//            // Get the invitee list.
//            final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
//
//            // Get Automatch criteria.
//            int minAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
//            int maxAutoPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
//
//            // Create the room configuration.
//            roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
//                    .setOnMessageReceivedListener(mMessageReceivedHandler)
//                    .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
//                    .addPlayersToInvite(invitees);
//            if (minAutoPlayers > 0) {
//                roomBuilder.setAutoMatchCriteria(
//                        RoomConfig.createAutoMatchCriteria(minAutoPlayers, maxAutoPlayers, 0));
//            }
//            inviteesSize = invitees.size();
//            // Save the roomConfig so we can use it if we call leave().
//            mJoinedRoomConfig = roomBuilder.build();
//            //  senderInvitationId = mJoinedRoomConfig.getInvitationId();
//
//            mRealTimeMultiplayerClient
//                    .create(mJoinedRoomConfig);
//
//        }
//        if (requestCode == RC_WAITING_ROOM) {
//            hideLoader();
//            if (resultCode == Activity.RESULT_OK) {
//                // when player accepts the request
//                Log.e(TAG, "onActivityResult RC_WAITING_ROOM : game start");
//                mPlaying = true;
//                ArrayList<Participant> mParticipants = mRoom.getParticipants();
//                for (Participant p : mParticipants) {
//                    String name = p.getDisplayName();
//                    Log.e(TAG, "onActivityResult: " + name + " id:" + p.getParticipantId());
//                    if (!p.getParticipantId().equals(mMyParticipantId)) {
//                        mParticipantName = name;
//                    }
//                }
//                if (inviteesSize == 0) {
//                    if (mParticipants.get(0).getParticipantId().equals(mMyParticipantId)){
//                        if (!TextUtils.isEmpty(row)) {
//                            isRequestSender = true;
//                            String dataToSend = "row=" + row + ",col=" + column;
//                            sendToAllReliably(dataToSend.getBytes());
//                        } else {
//                            isRequestSender = false;
//                        }
//                    }else{
//                        isRequestSender = false;
//                    }
//                } else {
//                    if (!TextUtils.isEmpty(row)) {
//                        isRequestSender = true;
//                        String dataToSend = "row=" + row + ",col=" + column;
//                        sendToAllReliably(dataToSend.getBytes());
//                    } else {
//                        isRequestSender = false;
//                    }
//                }
//                // Start the game!
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                // Waiting room was dismissed with the back button. The meaning of this
//                // action is up to the game. You may choose to leave the room and cancel the
//                // match, or do something else like minimize the waiting room and
//                // continue to connect in the background.
//                // mPlaying=false;
//                //if (isRequestSender) {
//            /*    mRealTimeMultiplayerClient
//                        .dismissInvitation(String.valueOf(mJoinedRoomConfig.getInvitedPlayerIds()));*/
//             /*   mRealTimeMultiplayerClient
//                        .leave(myJoinedRoomConfig, myCreatedRoomId);*/
//              /*  row = "";
//                column = "";
//                isRequestSender=false;*/
//                //  leaveRoom();
//                //  }
//                // in this example, we take the simple approach and just leave the room:
//
//            /*      mRealTimeMultiplayerClient
//                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
//            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
//                // player wants to leave the room.
//                //     mPlaying=false;
//             /*     mRealTimeMultiplayerClient
//                        .leave(mJoinedRoomConfig, mRoom.getRoomId());
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
//            }
//        }
//        if (requestCode == RC_INVITATION_INBOX) {
//            hideLoader();
//            if (resultCode != Activity.RESULT_OK) {
//                // Canceled or some error.
//                Log.e(TAG, "onActivityResult RC_INVITATION_INBOX : invitation cancel");
//                return;
//            }
//            // when user accept invitation from inbox
//            Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
//            if (invitation != null) {
//                Log.e(TAG, "onActivityResult RC_INVITATION_INBOX : invitation accepted");
//                roomBuilder = RoomConfig.builder(mRoomUpdateCallback)
//                        .setOnMessageReceivedListener(mMessageReceivedHandler)
//                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
//                        .setInvitationIdToAccept(invitation.getInvitationId());
//                mJoinedRoomConfig = roomBuilder.build();
//                mRealTimeMultiplayerClient
//                        .join(mJoinedRoomConfig);
//                // prevent screen from sleeping during handshake
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            }
//        }
//    }
}
