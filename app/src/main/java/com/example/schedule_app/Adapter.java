package com.example.schedule_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.net.Proxy;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import JSON.Schedule;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater inflater;
    List<Schedule> schedules;
    boolean teacher;

    public Adapter(Context ctx, List<Schedule> schedules, boolean teacher){
        this.inflater = LayoutInflater.from(ctx);
        this.schedules = schedules;
        this.teacher = teacher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.items,parent,false);
        return new ViewHolder(view);
    }

    public void ColorandTimeSet(@NonNull ViewHolder holder, String timeHour, int id){
        holder.timeHourText.setText(timeHour);
        holder.itemSchedule.setBackgroundResource(id);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat SimpleOurDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            myCalendar.setTime(SimpleOurDateFormat.parse(schedules.get(position).getDatelearn()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);
        if (teacher) {
            holder.teacherText.setText(schedules.get(position).getGroupe());
        } else {
            holder.teacherText.setText(schedules.get(position).getTeacher());
        }
        holder.displineText.setText(schedules.get(position).getDiscipline());
        holder.numberText.setText(String.valueOf(schedules.get(position).getNumber()));
        holder.kabinetText.setText(schedules.get(position).getKabinet());
        if(!schedules.get(position).getUndergroupe().isEmpty()){
            holder.undergroupText.setVisibility(View.VISIBLE);
            holder.undergroupText.setText("Подгруппа: "+schedules.get(position).getUndergroupe());
        }
        switch (schedules.get(position).getNumber()){
            case 1:
                ColorandTimeSet(holder,"08:00\n09:30",R.color.schedule_number_background1);
                break;
            case 2:
                ColorandTimeSet(holder,"09:50\n11:20",R.color.schedule_number_background2);
                break;
            case 3:
                ColorandTimeSet(holder,"11:40\n13:10",R.color.schedule_number_background3);
                break;
            case 4:
                if(dayOfWeek==7){
                    ColorandTimeSet(holder,"13:20\n14:50",R.color.schedule_number_background4);
                }
                else{
                    ColorandTimeSet(holder,"13:50\n15:20",R.color.schedule_number_background4);
                }
                break;
            case 5:
                if(dayOfWeek==7){
                    ColorandTimeSet(holder,"15:10\n16:40",R.color.schedule_number_background5);
                }
                else{
                    ColorandTimeSet(holder,"15:40\n17:10",R.color.schedule_number_background5);
                }
                break;
            case 6:
                if(dayOfWeek==7){
                    ColorandTimeSet(holder,"16:50\n18:20",R.color.schedule_number_background6);
                }
                else{
                    ColorandTimeSet(holder,"17:20\n18:50",R.color.schedule_number_background6);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{

        RelativeLayout itemSchedule;
        TextView groupeText,displineText, numberText, teacherText, undergroupText, timeHourText, kabinetText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemSchedule = itemView.findViewById(R.id.item);
            kabinetText = itemView.findViewById(R.id.kabinet);
            timeHourText = itemView.findViewById(R.id.time_hour);
            groupeText = itemView.findViewById(R.id.group);
            teacherText = itemView.findViewById(R.id.teacher);
            undergroupText = itemView.findViewById(R.id.undergroupe);
            displineText = itemView.findViewById(R.id.disciple);
            numberText = itemView.findViewById(R.id.number);
        }
    }
}