package com.tiptop.dotsandboxes.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiptop.dotsandboxes.DotsAndBoxesApplication;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.activities.MainActivity;
import com.tiptop.dotsandboxes.database.GameScore;
import com.tiptop.dotsandboxes.database.GameScoreDao;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    @BindView(R.id.ll_no_history)
    LinearLayout llNoHistory;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.activity_history, container, false);
        ButterKnife.bind(this, root);
        initToolbar();
        init();
        return root;
    }

    /**
     * this method initialize toolbar
     */
    private void initToolbar() {
        ((MainActivity) getActivity()).commonToolbar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarBack.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarMusic.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarSound.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarVibrate.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarFacebook.setVisibility(View.GONE);
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).tvToolbarTitle.setText(getString(R.string.history_screen));
    }

    @SuppressLint("WrongConstant")
    private void init() {

        GameScoreDao gameScoreDao = ((DotsAndBoxesApplication) getActivity().getApplication()).getDb().gameScoreDao();
        List<GameScore> gameScoreEntries = gameScoreDao.getAll();

        if (gameScoreEntries.size() > 0) {
            llNoHistory.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            llNoHistory.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new HistoryAdapter(getContext(), gameScoreEntries));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    /**
     * This class is used to fill recycler view
     */
    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.GameScoreViewHolder> {
        List<GameScore> gameScoreEntries;
        Context context;

        HistoryAdapter(Context context, List<GameScore> gameScoreEntries) {
            this.context = context;
            this.gameScoreEntries = gameScoreEntries;
        }

        @Override
        public GameScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GameScoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game_score, parent, false));
        }

        @Override
        public void onBindViewHolder(GameScoreViewHolder holder, int position) {
            GameScore entry = gameScoreEntries.get(position);

            Timber.e("Local Database Entry = " + entry.toString());

            holder.tvGameOpponent.setText(entry.getOpponent());
            String result = entry.getResult();
            holder.tvGameResult.setText(result);
            switch (result) {
                case "Won":
                    holder.tvGameResult.setTextColor(ContextCompat.getColor(context, R.color.wonColor));
                    break;
                case "Lost":
                    holder.tvGameResult.setTextColor(ContextCompat.getColor(context, R.color.lostColor));
                    break;
                case "Tie":
                    holder.tvGameResult.setTextColor(ContextCompat.getColor(context, R.color.tieColor));
                    break;

            }
            holder.tvGameScore.setText(entry.getScore());
            holder.tvGameTime.setText(DateUtils.getRelativeTimeSpanString(entry.getDate()));
        }

        @Override
        public int getItemCount() {
            return gameScoreEntries.size();
        }

        class GameScoreViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_game_opponent)
            TextView tvGameOpponent;
            @BindView(R.id.tv_score_lable)
            TextView tvScoreLable;
            @BindView(R.id.tv_game_score)
            TextView tvGameScore;
            @BindView(R.id.tv_game_result)
            TextView tvGameResult;
            @BindView(R.id.tv_game_time)
            TextView tvGameTime;

            GameScoreViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}