package com.example.iread.Book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.NoteUser;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ItemHolder> {
    private List<NoteUser> listnoteuser;
    private IAppApiCaller apiCaller ;
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
        deletenote(position,holder);

    }

    private void deletenote(int position,ItemHolder holder) {
        ImageView btnDelete = holder.itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            // Xử lý logic xóa ghi chú tại vị trí này
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            NoteUser note = listnoteuser.get(currentPos);
            deleteNoteById(note, currentPos);

        });
    }

    private void deleteNoteById(NoteUser note,int position) {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);
        apiCaller.DeleteNoteUser(note.getId()).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful()) {
                    listnoteuser.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listnoteuser.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView selectedText;
        private TextView noteContent ;
        ImageView btnDelete;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            selectedText = itemView.findViewById(R.id.selectedText);
            noteContent = itemView.findViewById(R.id.noteContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}
