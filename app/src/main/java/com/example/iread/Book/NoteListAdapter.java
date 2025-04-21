package com.example.iread.Book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.NoteUser;
import com.example.iread.R;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ItemHolder> {
    private List<NoteUser> listnoteuser;
    Context context;
    public NoteListAdapter(Context context, List<NoteUser> listnoteuser) {
        this.context = context;
        this.listnoteuser = listnoteuser;
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_note_line , parent , false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        NoteUser note = listnoteuser.get(position);
        holder.selectedText.setText(note.getSelectedText());
        holder.noteContent.setText(note.getNoteContent());
    }

    @Override
    public int getItemCount() {
        return listnoteuser.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView selectedText;
        private TextView noteContent ;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            selectedText = itemView.findViewById(R.id.selectedText);
            noteContent = itemView.findViewById(R.id.noteContent);

        }
    }
}
