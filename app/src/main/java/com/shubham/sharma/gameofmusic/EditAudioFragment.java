package com.shubham.sharma.gameofmusic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shubham on 2017-08-13.
 */

public class EditAudioFragment extends DialogFragment {

    @BindView(R.id.edit_audio_title_view)
    EditText titleView;

    @BindView(R.id.edit_audio_artist_view)
    EditText artistView;

    @BindView(R.id.edit_audio_album_view)
    EditText albumView;

    @BindView(R.id.edit_audio_image_view)
    ImageView imageView;

    @BindView(R.id.edit_song_choose_image_button)
    Button chooseImageButton;

    @BindView(R.id.edit_audio_genre_view)
    EditText genreView;

    @BindView(R.id.edit_song_save_button)
    Button saveButton;

    private Audio mAudio;

    private EditAudioFragment.OnAudioEditListener listener;

    private static final int REQUEST_CODE_PICK_IMAGE_FILE = 1;

    public interface OnAudioEditListener {
        void onAudioEdit();
    }

    // Empty constructor required for DialogFragment
    public EditAudioFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_audio, container);

        // Bind all views
        ButterKnife.bind(this, view);

        //  Get listener from parent activity
        listener = (EditAudioFragment.OnAudioEditListener) getActivity();

        // Get audio ID from passed bundle
        long audioId = getArguments().getLong("audioId");

        // Get audio object from the database
        mAudio = Audio.findById(Audio.class, audioId);

        // Update all views with audio details
        titleView.setText(mAudio.getTitle());
        artistView.setText(mAudio.getArtist());
        albumView.setText(mAudio.getAlbum());
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(mAudio.getImage(), 0,
                mAudio.getImage().length));
        genreView.setText(mAudio.getGenre());

        // Start file chooser when choose image button clicked
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileChooser();
            }
        });

        // Update audio object when save button clicked
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAudio();
            }
        });

        return view;
    }

    /***
     * Thus method starts intent to choose image file
     */
    private void startFileChooser() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose audio file"), REQUEST_CODE_PICK_IMAGE_FILE);
    }

    /***
     * This method gets called when an image file is chosen
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE_FILE && resultCode == Activity.RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                // Get the image and set on imageView
                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
            }
        }
    }


    /***
     * This method updates the audio object with new data and saves it.
     */
    private void updateAudio() {
        // Get data from input fields
        String title = titleView.getText().toString();
        String artist = artistView.getText().toString();
        String album = albumView.getText().toString();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInBytes = baos.toByteArray();

        String genre = genreView.getText().toString();

        // Update audio object and save
        mAudio.setTitle(title);
        mAudio.setArtist(artist);
        mAudio.setAlbum(album);
        mAudio.setImage(imageInBytes);
        mAudio.setGenre(genre);
        mAudio.save();

        // Call implementing activity's onAudioEdit method
        listener.onAudioEdit();

        // Close the fragment dialog
        dismiss();
    }
}