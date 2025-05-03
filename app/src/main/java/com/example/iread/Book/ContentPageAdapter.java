package com.example.iread.Book;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.DataBook;
import com.example.iread.Model.NoteUser;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<DataBook> dataBook;
    private BookChapter bookChapter;
    private Context context;
    private NoteUser noteUser;
    private IAppApiCaller apiCaller;
    private String username;
    public ContentPageAdapter(List<DataBook> dataBook,BookChapter bookChapter,Context context,String username){
        this.dataBook = dataBook;
        this.bookChapter = bookChapter;
        this.context = context;
        this.username = username;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == 1){ // text
            View view = inflater.inflate(R.layout.item_content_page_text , parent , false);
            return new ItemTextHolder(view);
        }
        else {
            View view = inflater.inflate(R.layout.item_content_page_img , parent , false);
            return new ItemUriHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DataBook item = dataBook.get(position);
        if (holder instanceof ItemUriHolder){
            ItemUriHolder holderUri = (ItemUriHolder) holder;
            Glide.with(holderUri.imgView.getContext())
                    .load(dataBook.get(position).getContent())
                    .placeholder(R.drawable.loading_placeholder)
                    .error(R.drawable.error_image)
                    .into(holderUri.imgView);

        } else if (holder instanceof ItemTextHolder) {
            ItemTextHolder holderText = (ItemTextHolder) holder;
            holderText.txtContent.setText(dataBook.get(position).getContent());
            holderText.txtContent.setTextIsSelectable(true);
            selectmode(holderText);

            if (item.getChapterId() != null) {
                selectNote(holderText, item.getChapterId());
                selectNote(holderText, item.getChapterId());
            }
        }

    }
    private void selectNote(ItemTextHolder holderText, String chapterId) {
        if (bookChapter == null || holderText == null || chapterId == null) return;

        int bookId = bookChapter.getBookId();
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);

        apiCaller.GetListNote(username, bookId).enqueue(new Callback<ReponderModel<NoteUser>>() {
            @Override
            public void onResponse(Call<ReponderModel<NoteUser>> call, Response<ReponderModel<NoteUser>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                List<NoteUser> allNotes = response.body().getDataList();
                if (allNotes == null || allNotes.isEmpty()) return;
                Log.d("NOTE_API", "Tổng số ghi chú nhận được: " + allNotes.size());

                String originalText = holderText.txtContent.getText().toString();
                SpannableString spannable = new SpannableString(originalText);

                for (NoteUser note : allNotes) {
                    if (!chapterId.equals(note.getChapterId())) continue;

                    int start = note.getStart();
                    int end = note.getEnd();
                    String selected = note.getSelectedText();

                    if (start >= 0 && end <= originalText.length() && start < end && selected != null) {
                        String actualText = originalText.substring(start, end);
                        if (selected.equals(actualText)) {
                            // Highlight
                            spannable.setSpan(new BackgroundColorSpan(0xFFFFFF99), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            // Clickable
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                    showAddNoteDialog(selected, holderText.txtContent, start, end,bookChapter,note);
                                }

                                @Override
                                public void updateDrawState(@NonNull TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(false); // Bỏ gạch chân
                                }
                            };
                            spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }

                holderText.txtContent.setText(spannable);
                holderText.txtContent.setMovementMethod(LinkMovementMethod.getInstance());

            }

            @Override
            public void onFailure(Call<ReponderModel<NoteUser>> call, Throwable t) {
                Log.e("NOTE_API", "Lỗi khi gọi API lấy ghi chú: " + t.getMessage(), t);
            }
        });
    }

    private void DialogAnalysis(String selectedText) {
        apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);
        apiCaller.GetAnalysis(selectedText).enqueue(new Callback<ReponderModel<String>>() {
            @Override
            public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String BookAnalysis = response.body().getData();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Kết quả phân tích");
                    builder.setMessage(BookAnalysis);
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi gọi API: " + t.getMessage());
            }
        });

    }

    private void selectmode(ItemTextHolder holderText) {
        holderText.txtContent.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            int start, end;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                menu.add(0, 1, 0, "Tô sáng");
                menu.add(0, 2, 1, "Thêm Ghi Chú");
                menu.add(0,3,2,"Phân tích đoạn văn");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                start = holderText.txtContent.getSelectionStart();
                end = holderText.txtContent.getSelectionEnd();
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                start = holderText.txtContent.getSelectionStart();
                end = holderText.txtContent.getSelectionEnd();

                if (start >= 0 && end > start) {
                    String selectedText = holderText.txtContent.getText().subSequence(start, end).toString();

                    switch (item.getItemId()) {
                        case 1: // Tô sáng
                            Spannable spannable = new SpannableString(holderText.txtContent.getText());
                            spannable.setSpan(new BackgroundColorSpan(0xFFFFFF99), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holderText.txtContent.setText(spannable);
                            break;

                        case 2: // Thêm Ghi Chú
                            showAddNoteDialog(selectedText, holderText.txtContent, start, end,bookChapter,null);
                            break;
                        case 3:
                            DialogAnalysis(selectedText);
                            break;
                    }
                }

                mode.finish(); // đóng menu
                return true;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {}
        });
    }

    private void showAddNoteDialog(String selectedText, TextView textView, int start, int end,BookChapter bookChapter, NoteUser note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_note, null);

        TextView tvSelectedText = dialogView.findViewById(R.id.tvSelectedText);
        EditText etNoteContent = dialogView.findViewById(R.id.etNoteContent);
        TextView btnDone = dialogView.findViewById(R.id.btnDone);

        tvSelectedText.setText(selectedText);
        if (note != null && note.getNoteContent() != null) {
            etNoteContent.setText(note.getNoteContent());
            etNoteContent.setSelection(note.getNoteContent().length()); // con trỏ cuối dòng
        }

        AlertDialog dialog = builder.setView(dialogView).create();

        btnDone.setOnClickListener(v -> {
            String noteContent = etNoteContent.getText().toString().trim();
            if (!noteContent.isEmpty()) {
                // Highlight vùng chọn
                Spannable spannable = new SpannableString(textView.getText());
                spannable.setSpan(new BackgroundColorSpan(0xFFFFFF99), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannable);
                noteUser = (note != null) ? note : new NoteUser();
                noteUser.setChapterId(bookChapter.getId());
                noteUser.setUserId(bookChapter.getUserId());
                noteUser.setSelectedText(tvSelectedText.getText().toString());
                noteUser.setNoteContent(etNoteContent.getText().toString());
                noteUser.setEnd(end);
                noteUser.setStart(start);
                noteUser.setUserName(username);
                noteUser.setBookId( bookChapter.getBookId());
                LocalDateTime now = LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String formattedTime = now.format(formatter);

                noteUser.setCreateDate(formattedTime);

                apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);
                Log.d("NOTE_DEBUG", new Gson().toJson(noteUser));

                apiCaller.UpdateNoteUser(noteUser).enqueue(new Callback<ReponderModel<String>>() {

                    @Override
                    public void onResponse(Call<ReponderModel<String>> call, Response<ReponderModel<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            Toast.makeText(context, "Ghi chú đã được thêm!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else {
                            Toast.makeText(context, "Phản hồi không hợp lệ từ server", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ReponderModel<String>> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            } else {
                Toast.makeText(context, "Vui lòng nhập nội dung ghi chú", Toast.LENGTH_SHORT).show();

            }

        });

        dialog.show();
    }



    @Override
    public int getItemCount() {
        return this.dataBook.size();
    }

    @Override
    public int getItemViewType(int position) {
        DataBook checkData = dataBook.get(position);
        if(checkData.getType() == true){ // text
            return 1;
        }
        else {
            return 2; // uri
        }
    }

    public class ItemUriHolder extends RecyclerView.ViewHolder
    {
        private ImageView imgView;
        public ItemUriHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imgContentPage);
        }
    }
    public class ItemTextHolder extends RecyclerView.ViewHolder
    {
        private TextView txtContent;
        public ItemTextHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.textContentInPage);
        }
    }
}
