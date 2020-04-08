package com.divvipay.app;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Rewards extends Fragment {

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;

    private int coinCount,count;
    private TextView coinCountText;
    private CountDownTimer countDownTimer;
    private boolean gameOver;
    private boolean gamePaused;

    private RewardedAd rewardedAd;
    private Button retryButton;
    private Button showVideoButton;
    private long timeRemaining;
    boolean isLoading;
     ProgressDialog dialog;


    public Rewards() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("VideoCount", MODE_PRIVATE);
         count = prefs.getInt("VideoCount", 0);
         if(count==30)
         {
             ((Dashboard)getActivity()).UpdateBalance(7);
             SharedPreferences.Editor editor = getActivity().getSharedPreferences("VideoCount", MODE_PRIVATE).edit();
             editor.putInt("VideoCount",0);
             editor.apply();
             count=0;
         }


        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        loadRewardedAd();

        retryButton = getActivity().findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startGame();
                        retryButton.setVisibility(View.INVISIBLE);
                    }
                });

        // Create the "show" button, which shows a rewarded video if one is loaded.
        showVideoButton = getActivity().findViewById(R.id.show_video_button);
        showVideoButton.setVisibility(View.INVISIBLE);
        showVideoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showRewardedVideo();
                    }
                });

        // Display current coin count to user.
        coinCountText = getActivity().findViewById(R.id.coin_count_text);
        coinCount = count;
        coinCountText.setText("Video Watched :- " + coinCount);
        startGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!gameOver && gamePaused) {
            resumeGame();
        }
    }

    private void pauseGame() {
        countDownTimer.cancel();
        gamePaused = true;
    }

    private void resumeGame() {
        createTimer(timeRemaining);
        gamePaused = false;
    }
    private void loadRewardedAd() {
        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            rewardedAd = new RewardedAd(getActivity(), AD_UNIT_ID);
            isLoading = true;
            rewardedAd.loadAd(
                    new AdRequest.Builder().build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {
                            // Ad successfully loaded.
                           isLoading = false;
                         //   Toast.makeText(getActivity(), "onRewardedAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(int errorCode) {
                            // Ad failed to load.
                           isLoading = false;
                         //   Toast.makeText(getActivity(), "onRewardedAdFailedToLoad", Toast.LENGTH_SHORT)
                           //         .show();
                        }
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    private void addCoins(int coins) {
        coinCount += coins;
        coinCountText.setText("Video Watched :- " + coinCount);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("VideoCount", MODE_PRIVATE).edit();
        editor.putInt("VideoCount",coinCount);
        editor.apply();
    }

    private void startGame() {
        // Hide the retry button, load the ad, and start the timer.
        retryButton.setVisibility(View.INVISIBLE);
        showVideoButton.setVisibility(View.INVISIBLE);
        if (!rewardedAd.isLoaded() && !isLoading) {
            loadRewardedAd();
        }
        createTimer(COUNTER_TIME);
        gamePaused = false;
        gameOver = false;
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private void createTimer(long time) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        dialog = ProgressDialog.show(getActivity(), "Please Wait",
                "Loading Ad....", true);
        dialog.show();
        countDownTimer =
                new CountDownTimer(time * 500, 50) {
                    @Override
                    public void onTick(long millisUnitFinished) {
                        timeRemaining = ((millisUnitFinished / 1000) + 1);
                    }

                    @Override
                    public void onFinish() {
                        if (rewardedAd.isLoaded()) {
                            showVideoButton.setVisibility(View.VISIBLE);
                        }
                        dialog.dismiss();
                        gameOver = true;
                    }
                };
        countDownTimer.start();
    }

    private void showRewardedVideo() {
        showVideoButton.setVisibility(View.INVISIBLE);
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback =
                    new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                           // Toast.makeText(getActivity(), "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                         //   Toast.makeText(getActivity(), "onRewardedAdClosed", Toast.LENGTH_SHORT).show();
                            // Preload the next video ad.
                            retryButton.setVisibility(View.VISIBLE);

                            loadRewardedAd();
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            // User earned reward.
                           // Toast.makeText(getActivity(), "onUserEarnedReward", Toast.LENGTH_SHORT).show();
                            retryButton.setVisibility(View.VISIBLE);
                            addCoins(1);
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            //Toast.makeText(getActivity(), "onRewardedAdFailedToShow", Toast.LENGTH_SHORT)
                           //         .show();
                            retryButton.setVisibility(View.VISIBLE);

                        }
                    };
            rewardedAd.show(getActivity(), adCallback);
        }
    }
}
