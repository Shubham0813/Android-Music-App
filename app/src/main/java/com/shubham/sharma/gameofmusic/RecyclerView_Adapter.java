package com.shubham.sharma.gameofmusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder>  {

    List<Audio> list;
    List<Audio> selectedAudios;
    Context context;

    public RecyclerView_Adapter(List<Audio> list, Context context) {
        this.list = list;
        this.context = context;
        selectedAudios = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Audio audio = list.get(holder.getAdapterPosition());

        if(!(audio.isSelected)) {
            holder.linearLayout.setBackgroundColor(Color.WHITE);
        }

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(audio.getTitle());

        holder.play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(audio.isSelected)) {
                    audio.isSelected = true;
                    holder.linearLayout.setBackgroundColor(Color.CYAN);
                    selectedAudios.add(audio);
                } else {
                    audio.isSelected = false;
                    holder.linearLayout.setBackgroundColor(Color.WHITE);
                    selectedAudios.remove(audio);
                }
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("Song", audio.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.itemOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Show Popup Menu here", Toast.LENGTH_SHORT).show();
                  //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.itemOptions);

                // inflating menu from xml resource

                popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        long audioID = (audio.getId());
                        switch (item.getItemId()) {
                            case R.id.add_to_playlist:
                                ((MainActivity)context).addToPlaylist(audioID);
                                break;
                            case R.id.edit_song:
                                ((MainActivity)context).editAudio(audioID);
                                break;
                        }
                        return false;
                    }
                });

                //displaying the popup
                popup.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setFilteredList(List<Audio> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        for(Audio audio : selectedAudios) {
            audio.isSelected = false;
        }
        notifyDataSetChanged();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    LinearLayout linearLayout;
    TextView title;
    TextView itemOptions;
    ImageView play_pause;

    ViewHolder(View itemView) {
        super(itemView);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.item_layout_linear_layout);
        title = (TextView) itemView.findViewById(R.id.title);
        play_pause = (ImageView) itemView.findViewById(R.id.play_pause);
        itemOptions = (TextView) itemView.findViewById(R.id.textViewOptions);
    }
}