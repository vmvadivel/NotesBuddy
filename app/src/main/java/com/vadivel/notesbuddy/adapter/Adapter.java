package com.vadivel.notesbuddy.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vadivel.notesbuddy.R;
import com.vadivel.notesbuddy.data.Note;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {
    private ArrayList<Note> notes;
    private ArrayList<Note> searchResultNotes;

    public Adapter(ArrayList<Note> notes) {
        this.notes = notes;
        this.searchResultNotes = notes;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list_items, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String title = searchResultNotes.get(position).getTitle();
        String content = searchResultNotes.get(position).getContent();

        if (!TextUtils.isEmpty(title)) {
            holder.nTitle.setText(title);
            holder.nTitle.setVisibility(View.VISIBLE);
        } else {
            holder.nTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            holder.nContent.setText(content);
            holder.nContent.setVisibility(View.VISIBLE);
        } else {
            holder.nContent.setVisibility(View.GONE);
        }

        holder.nContent.setText(searchResultNotes.get(position).getContent());
        ((GradientDrawable) holder.rootLayout.getBackground()).setColor(Color.parseColor(searchResultNotes.get(position).getColor()));
    }

    @Override
    public int getItemCount() {
        return searchResultNotes.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString();

                if (TextUtils.isEmpty(searchText)) {
                    searchResultNotes = notes;
                } else {
                    ArrayList<Note> searchArray = new ArrayList<>();
                    for (Note note : notes) {
                        if (note.getTitle().toLowerCase().contains(constraint) || note.getContent().toLowerCase().contains(constraint)) {
                            searchArray.add(note);
                        }
                    }
                    searchResultNotes = searchArray;
                }
                FilterResults fR = new FilterResults();
                fR.values = searchResultNotes;
                return fR;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();

            }
        };
    }

    public ArrayList<Note> getNotesFromAdapter() {
        return searchResultNotes;
    }

    public void removeNoteFromAdapter(int position) {
        searchResultNotes.remove(position);
        notifyItemRemoved(position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nTitle, nContent;
        LinearLayout rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nTitle = itemView.findViewById(R.id.nTitle);
            nContent = itemView.findViewById(R.id.nContent);
            rootLayout = itemView.findViewById(R.id.item_content_layout);
        }
    }
}
