package com.shubham.sharma.gameofmusic;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shubham on 2017-08-13.
 */

public class ChoosePlaylistFragmentDialog extends DialogFragment {

    ListView listView;

    private List<Playlist> playlists;
    private ChoosePlaylistListener listener;

    public interface ChoosePlaylistListener {
        void onFinishChoosingPlaylist(long audioId, long playlistId);
    }

    // Empty constructor required for DialogFragment
    public ChoosePlaylistFragmentDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_playlist, container);

        listener = (ChoosePlaylistListener) getActivity();

        playlists = Playlist.listAll(Playlist.class);

        // Get audio ID from bundle
        final long audioId =  getArguments().getLong("audioId");

        // Get audio from database by ID
        final Audio audio = Audio.findById(Audio.class, audioId);

        // Create adapter with list of all existing playlists
        final ArrayAdapter adapter = new ArrayAdapter<Playlist>(getActivity(),
                android.R.layout.simple_list_item_1, playlists);

        // Find list view and set adapter
        listView = (ListView) view.findViewById(R.id.choose_playlist_list_view);
        listView.setAdapter(adapter);

        // Add onclick listener on list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get chosen playlist
                Playlist playlist = (Playlist) adapter.getItem(position);

                // Call parent activity's callback method passing the playlist ID
                listener.onFinishChoosingPlaylist(audioId, playlist.getId());

                // Close the fragment dialog
                dismiss();
            }
        });
        return view;
    }
}
