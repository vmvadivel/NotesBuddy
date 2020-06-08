package com.example.noteme.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteme.R;
import com.example.noteme.activity.Edit;
import com.example.noteme.data.Note;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater inflater;
    List<Note> notes;

    public Adapter(Context context, List<Note> notes){
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = new inflater.inflate(R.layout.custom_list_view, parent, false);

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list_items, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String title = notes.get(position).getTitle();
    //    String date = notes.get(position).getDate();
   //     String time = notes.get(position).getTime();
        String date = notes.get(position).getContent();
        long    id       = notes.get(position).getID();

        //Vadivel: If title is not there removing that appropriate textview
        if(!title.isEmpty()) {
            holder.nTitle.setText(title);
            holder.nTitle.setVisibility(View.VISIBLE);
        }
        else {
            holder.nTitle.setVisibility(View.GONE);
        }

        holder.nDate.setText(date);
     //   holder.nTime.setText(time);
        //holder.nID.setText(String.valueOf(notes.get(position).getID()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //TextView nTitle, nDate, nTime, nID;

        TextView nTitle, nDate,  nID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nTitle = itemView.findViewById(R.id.nTitle);
            nDate = itemView.findViewById(R.id.nDate);
          //  nTime = itemView.findViewById(R.id.nTime);
            nID     = itemView.findViewById(R.id.listId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent i = new Intent(v.getContext(), Edit.class);
                   i.putExtra("ID",notes.get(getAdapterPosition()).getID());
                   v.getContext().startActivity(i);
                  // Toast.makeText(v.getContext(), "Item Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
