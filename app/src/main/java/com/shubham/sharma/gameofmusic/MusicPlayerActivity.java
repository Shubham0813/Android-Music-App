package com.shubham.sharma.gameofmusic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.background_image)
    ImageView mImageView;

    @BindView(R.id.line1)
    TextView mTitleView;

    @BindView(R.id.line2)
    TextView mArtistView;

    @BindView(R.id.line3)
    TextView mAlbumView;

    @BindView(R.id.startText)
    TextView mStart;

    @BindView(R.id.endText)
    TextView mEnd;

    @BindView(R.id.seekBar1)
    SeekBar seekBar;

    @BindView(R.id.play_pause)
    ImageView mPlayPauseButton;

    private Audio mAudio;
    private MediaPlayer mMediaPlayer;
    private boolean isPlaying = true;

    private Drawable mPlayDrawable;
    private Drawable mPauseDrawable;

    private static final String LOG_TAG = MusicPlayerActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        ButterKnife.bind(this);

        mMediaPlayer = new MediaPlayer();

        mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_play_arrow_white_48dp);
        mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_pause_white_48dp);

        mAudio = getIntent().getParcelableExtra("Song");
        Log.d(LOG_TAG, mAudio.toString());

        mImageView.setImageBitmap( BitmapFactory.decodeByteArray(mAudio.getImage(), 0,
                mAudio.getImage().length));
        mTitleView.setText(mAudio.getTitle());
        mArtistView.setText(mAudio.getArtist());
        mAlbumView.setText(mAudio.getAlbum());
        long durationInMS = Long.parseLong(mAudio.getDuration());
        double durationInMin = ((double) durationInMS / 1000.0) / 60.0;
        durationInMin = new BigDecimal(Double.toString(durationInMin)).
                setScale(2, BigDecimal.ROUND_UP).doubleValue();
        mStart.setText(R.string.song_start_duration);
        mEnd.setText("" + durationInMin);

        mPlayPauseButton.setOnClickListener(this);

        startPlay();
    }

    private void startPlay() {
        Log.d(LOG_TAG, "playing");

        mMediaPlayer.stop();
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(this, mAudio.getData());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // updatePosition();
        // isStarted = true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.play_pause:
                if(isPlaying) {
                    mPlayPauseButton.setImageDrawable(mPauseDrawable);
                    mMediaPlayer.pause();
                    isPlaying = false;
                } else {
                    mPlayPauseButton.setImageDrawable(mPlayDrawable);
                    mMediaPlayer.start();
                    isPlaying = true;
                }
                break;
        }
    }
}
