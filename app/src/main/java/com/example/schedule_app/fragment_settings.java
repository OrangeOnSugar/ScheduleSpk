package com.example.schedule_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_settings.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_settings newInstance(String param1, String param2) {
        fragment_settings fragment = new fragment_settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RelativeLayout selectGroup, selectTeacher;
    private ImageView miniature, changeGroupOrTeacher;
    private TextView selectedText, groupOrTeacherText;
    private Switch changeTheme;

    private String teacherOrGroupPoint;

    List<String> groups;

    private Context context;

    private static String url = "https://gitlab.com/Zagrtdinov/schedule/-/raw/master/csvjson.json";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final SharedPreferences sett = getContext().getApplicationContext().getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = sett.edit();

        selectGroup = view.findViewById(R.id.select_group);
        selectTeacher = view.findViewById(R.id.select_teacher);
        miniature = view.findViewById(R.id.change_group_or_teacher_image);
        selectedText = view.findViewById(R.id.change_group_or_teacher_select_title);
        groupOrTeacherText = view.findViewById(R.id.change_group_or_teacher_text);
        changeTheme = view.findViewById(R.id.change_theme_switch);
        changeGroupOrTeacher = view.findViewById(R.id.change_group_or_teacher_pencil);

        groups = new ArrayList<>();

        changeTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    editor.putBoolean("DayThem",true);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else
                {
                    editor.putBoolean("DayThem",false);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });



        changeGroupOrTeacher.setOnClickListener(v -> {
            SelectGroupOrTeacherDialog();
        });

        selectGroup.setOnClickListener(v -> SelectRole(false));

        selectTeacher.setOnClickListener(v -> SelectRole(true));

        if (sett.getBoolean("DayThem",false)) {
            changeTheme.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            changeTheme.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        context = getContext().getApplicationContext();

        if(!sett.getString("Link","").equals("")) {
            if (sett.getBoolean("Teacher",false)){
                SelectRole(true);
            }
            else {
                SelectRole(false);
            }
            groupOrTeacherText.setText(sett.getString("Link",""));
        }
        else {
            editor.putBoolean("Teacher",false);
            editor.putString("Link","ИП318(11)");
            editor.apply();
            SelectRole(false);
            groupOrTeacherText.setText("ИП318(11)");
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SelectRole(boolean teacher){
        if(teacher){
            selectTeacher.setBackgroundResource(R.color.settings_change_group_or_teacher_selected);
            selectGroup.setBackgroundResource(R.color.settings_change_group_or_teacher_notselected);
            miniature.setImageDrawable(getResources().getDrawable(R.drawable.image_teacher));
            selectedText.setText("Преподаватель");
            teacherOrGroupPoint = "Преподаватель";
        } else {
            selectTeacher.setBackgroundResource(R.color.settings_change_group_or_teacher_notselected);
            selectGroup.setBackgroundResource(R.color.settings_change_group_or_teacher_selected);
            miniature.setImageDrawable(getResources().getDrawable(R.drawable.image_group));
            selectedText.setText("Группа");
            teacherOrGroupPoint = "Группа";
        }
        extractGroup(teacherOrGroupPoint);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SelectGroupOrTeacherDialog(){
        final SharedPreferences sett = getContext().getApplicationContext().getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = sett.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(teacherOrGroupPoint);
        groups = groups.stream().distinct().sorted().collect(Collectors.toList());
        String[] teachersOrGroupsList = groups.toArray(new String[0]);
        builder.setItems(teachersOrGroupsList, (dialog, which) -> {
            if(teacherOrGroupPoint.equals("Преподаватель")){
                editor.putBoolean("Teacher",true);
            } else{
                editor.putBoolean("Teacher",false);
            }
            groupOrTeacherText.setText(teachersOrGroupsList[which]);
            editor.apply();
            editor.putString("Link",groupOrTeacherText.getText().toString());
            editor.apply();
    });
        AlertDialog dialog = builder.create();
        dialog.show();
}

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void extractGroup(String getString) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        groups.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject scheduleObj = response.getJSONObject(i);
                    groups.add(scheduleObj.getString(getString));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Log.d("tag", "onErrorResponse: " + error.getMessage()));

        queue.add(jsonArrayRequest);
    }
}