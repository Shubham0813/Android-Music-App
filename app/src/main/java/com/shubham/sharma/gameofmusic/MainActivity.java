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

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.search_box_view)
    EditText searchBoxView;

    private Audio mCurrentAudio;
    private List<Audio> mAudioList;
    private RecyclerView recyclerView;
    private RecyclerView_Adapter adapter;

    private MediaMetadataRetriever mMetaDataRetreiver;

    private String mNewPlaylist;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PICK_SOUND_FILE = 2;

    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initSugarDB();
        initRecyclerView();
        initDrawer();

        ButterKnife.bind(this);

        initMusicComponents();

        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        searchBoxView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString().toLowerCase());
            }
        });
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Audio> filteredAudios = new ArrayList<>();

        //looping through existing elements
        for (Audio audio : mAudioList) {
            //if the existing elements contains the search input
            if (audio.getTitle().toLowerCase().contains(text)
                    || audio.getArtist().toLowerCase().contains(text)
                    || audio.getAlbum().toLowerCase().contains(text)
                    || audio.getGenre().toLowerCase().contains(text)) {
                //adding the element to filtered list
                filteredAudios.add(audio);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.setFilteredList(filteredAudios);
    }

    private void initSugarDB() {
        SugarDb db = new SugarDb(this);
        db.onCreate(db.getDB());

        SugarContext.terminate();
        SchemaGenerator schemaGenerator = new SchemaGenerator(getApplicationContext());
        schemaGenerator.deleteTables(new SugarDb(getApplicationContext()).getDB());
        SugarContext.init(getApplicationContext());
        schemaGenerator.createDatabase(new SugarDb(getApplicationContext()).getDB());
    }

    private void initRecyclerView() {
        mAudioList = Audio.listAll(Audio.class);

        if (mAudioList.size() > 0) {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            adapter = new RecyclerView_Adapter(mAudioList, this);
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

    private void initMusicComponents() {
        mMetaDataRetreiver = new MediaMetadataRetriever();
        ImageView collapsingImageView = (ImageView) findViewById(R.id.collapsingImageView);
        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.drawer_bg, null);
        collapsingImageView.setImageDrawable(image);
    }

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

    private void startFileChooser() {
        Intent intent;
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Choose audio file"), REQUEST_CODE_PICK_SOUND_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera to capture picture
                    startFileChooser();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_SOUND_FILE && resultCode == Activity.RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                Uri audioFileUri = data.getData();

                mMetaDataRetreiver.setDataSource(this, audioFileUri);
                Audio audio = new Audio( audioFileUri,
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                        mMetaDataRetreiver.getEmbeddedPicture(),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                );

                audio.save();

                mCurrentAudio = audio;
                mAudioList = Audio.listAll(Audio.class);

                adapter.list.add(audio);
                adapter.notifyItemInserted((mAudioList.size()-1));
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_new_playlist:
                openAlertForUserInput();
                onBackPressed();
                return super.onOptionsItemSelected(item);
            case R.id.navigate_to_library:
                mAudioList = Audio.listAll(Audio.class);
                adapter.list = mAudioList;
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
                adapter.notifyDataSetChanged();
                break;
        }
        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private void refreshDrawerItems() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu m = navigationView.getMenu();

        for (Playlist p: Playlist.listAll(Playlist.class)) {
            if (notInMenu(p, m)) {
                m.add(p.name);
                MenuItem mi = m.getItem(m.size()-1);
                mi.setTitle(mi.getTitle());
                mi.setIcon(R.drawable.ic_notification);
            }
        }

        navigationView.invalidate();
    }

    private boolean notInMenu(Playlist p, Menu m) {
        for (int i = 0; i < m.size(); i++) {
            if (m.getItem(i).getTitle().equals(p.name))
                return false;
        }
        return true;
    }

    private void openAlertForUserInput() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //Alert's title and label
        alert.setTitle("Adding new node.");
        alert.setMessage("Description");

        //Setup the Alert Edit for the user's input
        final EditText edtNewItem = new EditText(MainActivity.this);
        alert.setView(edtNewItem);

        //Setup the Alert Ok button
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mNewPlaylist = edtNewItem.getText().toString();

                //Create a new item with the user input
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addToPlaylist(long audioID) {
        // close existing dialog fragments
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("choose_playlist_dialog_fragment");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        ChoosePlaylistFragmentDialog choosePlaylistDialog = new ChoosePlaylistFragmentDialog();
        Bundle args = new Bundle();
        args.putLong("audioId", audioID);
        choosePlaylistDialog.setArguments(args);
        choosePlaylistDialog.show(manager, "choose_playlist");
    }

    @Override
    public void onFinishChoosingPlaylist(long audioId, long playlistId) {
        Audio audio = Audio.findById(Audio.class, audioId);
        Playlist playlist = Playlist.findById(Playlist.class, playlistId);

        audio.setPlaylist(playlist);
        audio.save();

        String audioTitle = audio.getTitle();
        String playlistTitle = playlist.toString();

        Toast.makeText(this, audioTitle + " added to " + playlistTitle, Toast.LENGTH_SHORT).show();
    }

    public void editAudio(long audioID) {
        // close existing dialog fragments
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("edit_audio_dialog_fragment");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        EditAudioFragment editAudioFragment = new EditAudioFragment();
        Bundle args = new Bundle();
        args.putLong("audioId", audioID);
        editAudioFragment.setArguments(args);
        editAudioFragment.show(manager, "edit_audio");
    }

    @Override
    public void onAudioEdit() {
        Toast.makeText(this, "Song Updated", Toast.LENGTH_SHORT).show();
        mAudioList = Audio.listAll(Audio.class);
        adapter.list = mAudioList;
        adapter.notifyDataSetChanged();
    }
}
