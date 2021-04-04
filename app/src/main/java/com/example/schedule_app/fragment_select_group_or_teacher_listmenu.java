package com.example.schedule_app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import JSON.Schedule;

public class fragment_select_group_or_teacher_listmenu extends Fragment {


    EditText searchText;
    TextView label, messageText;
    ImageView messageImage;
    RecyclerView listGroupsOrTeachers;
    RelativeLayout messageLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    List<String> teachersOrGroups;

    RecyclerView.Adapter adapter;
    private Context context;
    private static String url = "https://gitlab.com/Zagrtdinov/schedule/-/raw/master/csvjson.json";
    String teacherOrGroupString;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_select_group_or_teacher_listmenu, container, false);

        context = view.getContext().getApplicationContext();

        teachersOrGroups = new ArrayList<>();

        searchText = view.findViewById(R.id.select_group_or_teacher_listmenu_searchtext);
        label = view.findViewById(R.id.select_group_or_teacher_listmenu_label);
        messageText = view.findViewById(R.id.select_group_or_teacher_listmenu_messagelayout_label);
        messageImage = view.findViewById(R.id.select_group_or_teacher_listmenu_messagelayout_image);
        listGroupsOrTeachers = view.findViewById(R.id.select_group_or_teacher_listmenu_listgroups_or_teachers);
        messageLayout = view.findViewById(R.id.select_group_or_teacher_listmenu_messagelayout);
        swipeRefreshLayout = view.findViewById(R.id.select_group_or_teacher_listmenu_refreshlayout);

        listGroupsOrTeachers.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(searchText.getText().toString().trim().equals("")){
                extractSchedule();
            }
            else {
                Search();
            }
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false),2*1000);
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchText.getText().toString().trim().equals("")){
                    SetAdapter();
                }
                Search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


    private void Search(){
        listGroupsOrTeachers.setVisibility(View.VISIBLE);
        ArrayList<String> newArrayList = new ArrayList<>();
        for (int i=0;i < teachersOrGroups.size();i++){
            if(teachersOrGroups.get(i).contains(searchText.getText().toString().trim())){
                newArrayList.add(teachersOrGroups.get(i));
            }
        }
        adapter = new SelectedAdapter(context, newArrayList,teacherOrGroupString);
        listGroupsOrTeachers.setLayoutManager(new LinearLayoutManager(context));
        listGroupsOrTeachers.setAdapter(adapter);
        if(newArrayList.size() <= 0){
            if(teacherOrGroupString.equals("Преподаватель")){
                SetMessage(R.drawable.emptybox,"Преподавателей с данным ФИО не найдено");
            }
            else{
                SetMessage(R.drawable.emptybox,"Групп с таким названием не найдено");
            }
            return;
        }
        messageLayout.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args.getBoolean("boolean",false)) {
            teacherOrGroupString = "Преподаватель";
            label.setText("Выберите\nсвои\nФИО");
        } else {
            teacherOrGroupString = "Группа";
            label.setText("Выберите\nсвою\nгруппу");
        }
        extractSchedule();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SetAdapter(){
        listGroupsOrTeachers.setVisibility(View.VISIBLE);
        teachersOrGroups = teachersOrGroups.stream().distinct().sorted().collect(Collectors.toList());
        adapter = new SelectedAdapter(context, teachersOrGroups,teacherOrGroupString);
        listGroupsOrTeachers.setLayoutManager(new LinearLayoutManager(context));
        listGroupsOrTeachers.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void extractSchedule() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        teachersOrGroups.clear();
        SetAdapter();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject scheduleObj = response.getJSONObject(i);
                    teachersOrGroups.add(scheduleObj.getString(teacherOrGroupString));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(teachersOrGroups.size() <= 0){
                if(teacherOrGroupString.equals("Преподаватель")){
                    SetMessage(R.drawable.emptybox,"Преподавателей с данным ФИО не найдено");
                }
                else{
                    SetMessage(R.drawable.emptybox,"Групп с таким названием не найдено");
                }
            }
            SetAdapter();
        }, error -> {
            Log.d("tag", "onErrorResponse: " + error.getMessage());
            SetMessage(R.drawable.internetconnectionnone,"Отсутствует подключение к интернету");
        });
        queue.add(jsonArrayRequest);
    }

    private void SetMessage(int idImage, String message){
        listGroupsOrTeachers.setVisibility(View.GONE);
        messageLayout.setVisibility(View.VISIBLE);
        messageImage.setImageDrawable(getResources().getDrawable(idImage));
        messageText.setText(message);
    }
}
