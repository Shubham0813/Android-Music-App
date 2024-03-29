package com.shubham.sharma.gameofmusic;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    SeekBar mSeekBar;

    @BindView(R.id.previousButton)
    ImageView mPreviousButton;

    @BindView(R.id.rewindButton)
    ImageView mRewindButton;


    @BindView(R.id.playPauseButton)
    ImageView mPlayPauseButton;

    @BindView(R.id.fastForwardButton)
    ImageView mFastForwardButton;

    @BindView(R.id.nextButton)
    ImageView mNextButton;

    private Audio mAudio;
    private ArrayList<Audio> mAudioList;
    private int mCurrentIndex;
    private MediaPlayer mMediaPlayer;

    private Drawable mPlayDrawable;
    private Drawable mPauseDrawable;
    private Handler mHandler = new Handler();

    private boolean isSeekBarMoving;
    private boolean isPlaying = true;

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;

    private static final String LOG_TAG = MusicPlayerActivity.class.getCanonicalName();

    private final Runnable updatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Bind all views
        ButterKnife.bind(this);

        // Get drawables for play and pause buttons
        mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_play_arrow_white_48dp);
        mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_pause_white_48dp);

        // Get list of all audios from the database
        //mAudioList = (ArrayList<Audio>) Audio.listAll(Audio.class);

        // Get the song ID from intent
        String playlistID = getIntent().getStringExtra("Playlist");
        if(playlistID.equals("")) {
            mAudioList = (ArrayList<Audio>) Audio.listAll(Audio.class);
        } else {
            mAudioList = (ArrayList<Audio>) Audio.find(Audio.class, "m_playlist = ?", playlistID);
        }

        // Get the song ID from intent
        long mAudioId = getIntent().getLongExtra("Song", 0);

        // Find the audio from database with ID
        mAudio = Audio.findById(Audio.class, mAudioId);

        // Store index of the audio in list
        mCurrentIndex = mAudioList.indexOf(mAudio);

        // Set change listener on SeekBar
        mSeekBar.setOnSeekBarChangeListener(seekBarOnChange);

        // Set change listener on all buttons
        mPreviousButton.setOnClickListener(this);
        mRewindButton.setOnClickListener(this);
        mPlayPauseButton.setOnClickListener(this);
        mFastForwardButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);

        // Create object new media player and set listeners
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(onCompletion);
        mMediaPlayer.setOnErrorListener(onError);

        // Start playing media by calling startPlay method
        startPlay();
    }

    private void startPlay() {
        // Setup music details on UI controls
        setupMusicDetails();

        // Reset seekbar
        mSeekBar.setProgress(0);

        // Stop and reset media player
        mMediaPlayer.stop();
        mMediaPlayer.reset();

        try {
            // Set data source on media player
            mMediaPlayer.setDataSource(this, mAudio.getData());

            // Start the media player
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSeekBar.setMax(mMediaPlayer.getDuration());
        updatePosition();
    }

    /***
     * This method gets data from audio object and displays it on UI views
     */
    private void setupMusicDetails(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //Bitmap source = BitmapFactory.decodeResource(a.getResources(), path, options);
        mImageView.setImageBitmap( BitmapFactory.decodeByteArray(mAudio.getImage(), 0,
                mAudio.getImage().length, options));



        mTitleView.setText(mAudio.getTitle());
        mArtistView.setText(mAudio.getArtist());
        mAlbumView.setText(mAudio.getAlbum());

        mStart.setText(R.string.song_start_duration);

        mEnd.setText(DateUtils.formatElapsedTime(Integer.parseInt(mAudio.getDuration())/1000));

    }

    /***
     * Stop playing media player
     */
    private void stopPlay() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
        mHandler.removeCallbacks(updatePositionRunnable);
        mSeekBar.setProgress(0);
        isPlaying = false;
    }

    /***
     * Release media player when activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(updatePositionRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlay();
        }
    };

    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };


    /***
     * Update SeekBar position as song is played
     */
    private void updatePosition() {
        int currentDuration = mMediaPlayer.getCurrentPosition();
        mStart.setText(DateUtils.formatElapsedTime(currentDuration/1000));
        mHandler.removeCallbacks(updatePositionRunnable);
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        mHandler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }

    private SeekBar.OnSeekBarChangeListener seekBarOnChange =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (isSeekBarMoving) {
                        // Update media player when SeekBar progress changed
                        mMediaPlayer.seekTo(progress);
                        Log.i("OnSeekBarChangeListener", "OnProgressChanged");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeekBarMoving = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isSeekBarMoving = false;
                }
            };

    /***
     * OnClick code for different buttons
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.previousButton:
                // Play previous song in the list
                if((mCurrentIndex - 1) < 0)
                    mCurrentIndex = mAudioList.size() - 1;
                else {
                    mCurrentIndex--;
                }
                mAudio = mAudioList.get(mCurrentIndex);
                startPlay();
                break;
            case R.id.rewindButton:
                // Seek media player to 5 sec before
                int rewindDuration = mMediaPlayer.getCurrentPosition() - STEP_VALUE;
                if (rewindDuration < 0)
                    rewindDuration = 0;
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(rewindDuration);
                mMediaPlayer.start();
                break;
            case R.id.playPauseButton:
                // If audio is playing, pause media player and update button image to play drawable,
                // else, start playing and update button image to pause drawable
                if(isPlaying) {
                    mPlayPauseButton.setImageDrawable(mPlayDrawable);
                    mMediaPlayer.pause();
                    isPlaying = false;
                } else {
                    mPlayPauseButton.setImageDrawable(mPauseDrawable);
                    mMediaPlayer.start();
                    isPlaying = true;
                }
                break;
            case R.id.fastForwardButton:
                // Add 5 seconds to duration and seek media player to updated duration
                int fastForwardDuration = mMediaPlayer.getCurrentPosition() + STEP_VALUE;
                if (fastForwardDuration > mMediaPlayer.getDuration()) {
                    fastForwardDuration = mMediaPlayer.getDuration();
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(fastForwardDuration);
                    mPlayPauseButton.setImageDrawable(mPlayDrawable);
                } else {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(fastForwardDuration);
                    mMediaPlayer.start();
                }
                break;
            case R.id.nextButton:
                // Play next song in the list
                if((mCurrentIndex + 1) == mAudioList.size())
                    mCurrentIndex = 0;
                else {
                    mCurrentIndex++;
                }
                mAudio = mAudioList.get(mCurrentIndex);
                startPlay();
                break;
            default:
                break;
        }
    }
}
