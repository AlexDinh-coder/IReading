package com.example.iread.Book;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iread.Model.BookChapter;
import com.example.iread.Model.NoteUser;
import com.example.iread.R;
import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.apicaller.RetrofitClient;
import com.example.iread.basemodel.ReponderModel;
import com.example.iread.helper.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteFragment extends Fragment {
    private List<NoteUser> listnoteuser = new ArrayList<>();
    ;
    private List<BookChapter> listchapter = new ArrayList<>();
    private  IAppApiCaller apiCaller ;
    List<NoteUser> allNotes = new ArrayList<>();

    RecyclerView recyclerView;
    private int bookid;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewNote);

        Bundle bundle = getArguments();


        getlistnote();

        return view;

    }

    private void getlistnote() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        Bundle bundle = getArguments();
        Context context = requireContext();
        if (bundle != null) {
                 bookid = bundle.getInt("bookid",0);


                apiCaller = RetrofitClient.getInstance(Utils.BASE_URL, context).create(IAppApiCaller.class);
                apiCaller.GetListNote(username,bookid).enqueue(new Callback<ReponderModel<NoteUser>>() {

                    @Override
                    public void onResponse(Call<ReponderModel<NoteUser>> call, Response<ReponderModel<NoteUser>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("NOTE_JSON", new Gson().toJson(response.body()));
                            //listnoteuser = response.body().getDataList();
                            List<NoteUser> listnoteuser = response.body().getDataList();
                            NoteListAdapter adapter = new NoteListAdapter(getContext(), listnoteuser);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            // TODO: cập nhật adapter nếu có
                            Log.d("NOTE_API", "Số ghi chú: " + listnoteuser.size());
                        } else {
                            Log.e("NOTE_API", "Phản hồi không thành công: " + response.code());
                        }
                    }




                    @Override
                    public void onFailure(Call<ReponderModel<NoteUser>> call, Throwable t) {
                        Log.e("NOTE_API", "Lỗi gọi API: " + t.getMessage());
                        Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();   }
                });


            }

            // sử dụng noteList (hiển thị lên RecyclerView, v.v.)





    }
}