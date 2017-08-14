package com.shubham.sharma.gameofmusic;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ChoosePlaylistFragmentDialog.ChoosePlaylistListener,
        EditAudioFragment.OnAudioEditListener {
/*
* JOEL MATSU - 7711831 - java class for the main activity
* */

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.search_box_view)
    EditText searchBoxView;

    private List<Audio> mAudioList;
    private RecyclerView recyclerView;
    private RecyclerView_Adapter adapter;

    private MediaMetadataRetriever mMetaDataRetreiver;

    private String mNewPlaylist;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PICK_SOUND_FILE = 2;

    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    //JOEL MATSU - 7711831 - App initialization, where the execution starts
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Code auto-generated, just kept unchanged
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializations
        initSugarDB();
        initRecyclerView();
        initDrawer();

        // Bind all views
        ButterKnife.bind(this);

        //Initializations
        initMusicComponents();

        // Adding a TextChangedListener to call a method whenever there is some change on the
        // search box EditText
        searchBoxView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // After the change calling the filter method and passing the search input
                filter(editable.toString().toLowerCase());
            }
        });
    }

    /**
     * This method gets a string and search for songs in the library whose title, artist, album
     * or genre match the string
     * @param text
     */
    private void filter(String text) {
        // New array list that will hold the filtered data
        ArrayList<Audio> filteredAudios = new ArrayList<>();

        // Looping through existing elements
        for (Audio audio : mAudioList) {
            // If the existing elements contains the search input
            if (audio.getTitle().toLowerCase().contains(text)
                    || audio.getArtist().toLowerCase().contains(text)
                    || audio.getAlbum().toLowerCase().contains(text)
                    || audio.getGenre().toLowerCase().contains(text)) {
                //Adding the element to filtered list
                filteredAudios.add(audio);
            }
        }

        // Calling a method of the adapter class and passing the filtered list
        adapter.setFilteredList(filteredAudios);
    }

    //BURAK KARAHAN - 7711062 - SugarORM/SQL Lite database framework setup
    private void initSugarDB() {
        SugarDb db = new SugarDb(this);
        db.onCreate(db.getDB());

        SugarContext.terminate();
        SchemaGenerator schemaGenerator = new SchemaGenerator(getApplicationContext());
        schemaGenerator.deleteTables(new SugarDb(getApplicationContext()).getDB());
        SugarContext.init(getApplicationContext());
        schemaGenerator.createDatabase(new SugarDb(getApplicationContext()).getDB());
    }

    //JOEL MATSU - 7711831 - Setup the main view with list of musics
    private void initRecyclerView() {
        mAudioList = Audio.listAll(Audio.class);
        adapter = new RecyclerView_Adapter(new ArrayList<Audio>(), this);

        if (mAudioList.size() > 0) {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            adapter.list = mAudioList;
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                    new ItemTouchHelper.SimpleCallback(0,
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                      RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    Audio.delete(mAudioList.get(viewHolder.getAdapterPosition()));
                    adapter.list.remove(viewHolder.getAdapterPosition());
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                    Snackbar snackbar = Snackbar
                            .make(mCoordinatorLayout, "Song Deleted", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    //AMRITPAL SINGH - 7758071 - Drawer and Side Menu setup
    private void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        refreshDrawerItems();
    }

    //JOEL MATSU - 7711831 - setup the collapsing image at the top of songs
    private void initMusicComponents() {
        mMetaDataRetreiver = new MediaMetadataRetriever();
        ImageView collapsingImageView = (ImageView) findViewById(R.id.collapsingImageView);
        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.drawer_bg, null);
        collapsingImageView.setImageDrawable(image);
    }

    //JOEL MATSU - 7711831 - open the file explorer to load songs from the phone to the library
    public void onclick_loadSongFromLibrary(View view) {

        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startFileChooser();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void onclick_addSongsToPlaylist(View view) {
        if(adapter.selectedAudios.size() == 0) {
            Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show();
        } else {
            openAlertForUserInput();
        }
    }

    //JOEL MATSU - 7711831 - create intent to open file explorer
    private void startFileChooser() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Choose audio file"), REQUEST_CODE_PICK_SOUND_FILE);
    }

    //JOEL MATSU - 7711831 - request permission to open file explorer
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    startFileChooser();
                } else {
                    //Permission denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    //JOEL MATSU - 7711831 - handle the file returned by the file explorer
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_SOUND_FILE && resultCode == Activity.RESULT_OK){

            if ((data != null) && (data.getData() != null)){

                //Get the song URI
                Uri audioFileUri = data.getData();

                //Extract the song attributes and create a new Audio
                mMetaDataRetreiver.setDataSource(this, audioFileUri);
                Audio audio = new Audio( audioFileUri,
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                        mMetaDataRetreiver.getEmbeddedPicture(),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                );

                //BURAK KARAHAN - 7711062 - persist the new audio object
                audio.save();

                //Update the screen items
                mAudioList = Audio.listAll(Audio.class);

                adapter.list.add(audio);
                adapter.notifyItemInserted((mAudioList.size()-1));
            }
        }
    }

    //AMRITPAL SINGH - 7758071 - handling the drawer menu actions
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Create a new Playlist
        switch (id) {
            case R.id.nav_new_playlist:
                openAlertForUserInput();
                onBackPressed();
                return super.onOptionsItemSelected(item);
            case R.id.navigate_to_library:
                mAudioList = Audio.listAll(Audio.class);
                adapter.list = mAudioList;
                adapter.currentPlaylist = "";
                adapter.notifyDataSetChanged();
                break;
            case R.id.action_settings:
                return true;
            default:
                List<Playlist> matchingPlaylists  = Playlist.find(Playlist.class, "name = ?", item.toString());
                Playlist playlist = matchingPlaylists.get(0);

                List<Audio> matchingAudios = Audio.find(Audio.class, "m_playlist = ?", playlist.getId().toString());
                mAudioList = matchingAudios;
                adapter.list = matchingAudios;
                adapter.currentPlaylist = playlist.getId().toString();
                adapter.notifyDataSetChanged();
                break;
        }
        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    //AMRITPAL SINGH - 7758071 - update the drawer menu with all persisted playlist
    private void refreshDrawerItems() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu m = navigationView.getMenu();

        //Get all persisted playlist and update the drawer menu
        for (Playlist p: Playlist.listAll(Playlist.class)) {
            if (notInMenu(p, m)) {
                m.add(p.name);
                MenuItem mi = m.getItem(m.size()-1);
                mi.setTitle(mi.getTitle());
                mi.setIcon(R.drawable.ic_notification);
            }
        }

        //Invalidate for refresh
        navigationView.invalidate();
    }

    //AMRITPAL SINGH - 7758071 - items not in menu yet
    private boolean notInMenu(Playlist p, Menu m) {
        for (int i = 0; i < m.size(); i++) {
            if (m.getItem(i).getTitle().equals(p.name))
                return false;
        }
        return true;
    }

    //AMRITPAL SINGH - 7758071 - Open alert dialog so the user can provide the playlist name
    private void openAlertForUserInput() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //Alert's title and label
        alert.setTitle("New playlist");
        alert.setMessage("Name");

        //Setup the Alert Edit for the user's input
        final EditText edtNewItem = new EditText(MainActivity.this);
        alert.setView(edtNewItem);

        //Setup the Alert Ok button
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mNewPlaylist = edtNewItem.getText().toString();

                //Create a new playlist with the user input
                Playlist playlist = new Playlist(mNewPlaylist);
                playlist.save();

                if(adapter.selectedAudios.size() > 0) {
                    for(Audio audio : adapter.selectedAudios) {
                        audio.setPlaylist(playlist);
                        audio.isSelected = false;
                        audio.save();
                    }
                }

                refreshDrawerItems();
                onStart();
            }
        });

        //Setup the Alert Cancel button
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mNewPlaylist = "";
            }
        });

        //Show the alert
        alert.show();
    }

    //AMRITPAL SINGH - 7758071 - setup the back button to close drawer before closing app
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //JOEL MATSU - 7711831 - destructor
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /***
     * This method opens Dialog fragment passing the ID of audio and shows lists of existing
     * playlists to select from.
     * @param audioID
     */
    public void addToPlaylist(long audioID) {
        // Close existing dialog fragments
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("choose_playlist_dialog_fragment");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        // Create new ChoosePlaylistDialogFragment
        ChoosePlaylistFragmentDialog choosePlaylistDialog = new ChoosePlaylistFragmentDialog();

        // Add the audio ID in a bundle
        Bundle args = new Bundle();
        args.putLong("audioId", audioID);

        // Set bundle as fragment's argument
        choosePlaylistDialog.setArguments(args);

        // Show the dialog fragment
        choosePlaylistDialog.show(manager, "choose_playlist");
    }

    /***
     * Callback method when user chooses a playlist in ChoosePlaylistDialogFragment
     * @param audioId
     * @param playlistId
     */
    @Override
    public void onFinishChoosingPlaylist(long audioId, long playlistId) {
        // Get audio object from database using ID
        Audio audio = Audio.findById(Audio.class, audioId);

        // Get playlist object from database using ID
        Playlist playlist = Playlist.findById(Playlist.class, playlistId);

        // Set audio's playlist
        audio.setPlaylist(playlist);

        // Save audio object
        audio.save();

        // Get titles for audio and playlist
        String audioTitle = audio.getTitle();
        String playlistTitle = playlist.toString();

        // Display success toast
        Toast.makeText(this, audioTitle + " added to " + playlistTitle, Toast.LENGTH_SHORT).show();
    }

    /***
     * This method opens a dialog fragment to edit an audio object
     * @param audioID
     */
    public void editAudio(long audioID) {
        // close existing dialog fragments
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("edit_audio_dialog_fragment");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        // Create new EditAudioFragment object
        EditAudioFragment editAudioFragment = new EditAudioFragment();

        // Create bundle, add audioID and set as argument on fragment object
        Bundle args = new Bundle();
        args.putLong("audioId", audioID);
        editAudioFragment.setArguments(args);

        // Show the fragment
        editAudioFragment.show(manager, "edit_audio");
    }

    /***
     * Callback method for EditAudioFragment called when audio saved after edit.
     */
    @Override
    public void onAudioEdit() {
        // Get updated list of audio files
        mAudioList = Audio.listAll(Audio.class);

        // Update adapter's list
        adapter.list = mAudioList;

        // Notify adapter about change
        adapter.notifyDataSetChanged();

        // Display success toast
        Toast.makeText(this, "Song Updated", Toast.LENGTH_SHORT).show();
    }
}
