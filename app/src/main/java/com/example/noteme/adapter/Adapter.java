package com.example.noteme.adapter;

import android.content.Context;
import android.graphics.Color;
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

import com.example.noteme.R;
import com.example.noteme.data.Note;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {
    private LayoutInflater inflater;
    private ArrayList<Note> notes;
    private ArrayList<Note> searchResultNotes;

    public Adapter(Context context, ArrayList<Note> notes) {
        this.inflater = LayoutInflater.from(context);
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

        //Vadivel: If title is not there removing that appropriate textview

        if (!TextUtils.isEmpty(title)) {
            holder.nTitle.setText(title);
            holder.nTitle.setVisibility(View.VISIBLE);
        } else {
            holder.nTitle.setVisibility(View.GONE);
        }
        holder.nDate.setText(searchResultNotes.get(position).getContent());
        holder.rootLayout.setBackgroundColor(Color.parseColor(searchResultNotes.get(position).getColor()));
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nTitle, nDate, nID;
        LinearLayout rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nTitle = itemView.findViewById(R.id.nTitle);
            nDate = itemView.findViewById(R.id.nDate);
            nID = itemView.findViewById(R.id.listId);
            rootLayout = itemView.findViewById(R.id.item_content_layout);
        }
    }
}
