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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Audio mCurrentAudio;
    public List<Audio> mAudioList;
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
            adapter = new RecyclerView_Adapter(mAudioList, getApplication());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                            //intent.putExtra("Song", mAudioList.get(position));
                            intent.putExtra("Song", mAudioList.get(position).getId());
                            startActivity(intent);
                        }
                    })
            );
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
        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.image1, null);
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
                        mMetaDataRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
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
            case R.id.action_settings:
                return true;
        }

        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

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
}
