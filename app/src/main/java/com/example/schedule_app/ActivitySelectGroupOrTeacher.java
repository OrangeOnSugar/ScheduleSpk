package com.example.schedule_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivitySelectGroupOrTeacher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_or_teacher);

        ImageView selectTeacher = findViewById(R.id.select_group_or_teacher_mail_teacher_image);
        ImageView selectStudent = findViewById(R.id.select_group_or_teacher_mail_group_image);
        TextView selectTeacherLabel = findViewById(R.id.select_group_or_teacher_mail_group_label);
        TextView selectStudentLabel = findViewById(R.id.select_group_or_teacher_mail_teacher_label);

        selectStudent.setOnClickListener(v -> { SelectGroupOrTeacher(false);});
        selectStudentLabel.setOnClickListener(v -> { SelectGroupOrTeacher(false);});
        selectTeacher.setOnClickListener(v -> { SelectGroupOrTeacher(true);});
        selectTeacherLabel.setOnClickListener(v -> { SelectGroupOrTeacher(true);});

    }

    private void SelectGroupOrTeacher(boolean teacher){
        Bundle teacherOrGroupPut = new Bundle();
        teacherOrGroupPut.putBoolean("boolean",teacher);
        fragment_select_group_or_teacher_listmenu fragment_select_group_or_teacher_listmenu = new fragment_select_group_or_teacher_listmenu();
        fragment_select_group_or_teacher_listmenu.setArguments(teacherOrGroupPut);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainform,fragment_select_group_or_teacher_listmenu).commit();
    }
}