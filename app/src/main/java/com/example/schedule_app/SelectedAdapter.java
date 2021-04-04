package com.example.schedule_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.ViewHolder> {
    static LayoutInflater layoutInflater;
    List<String> stringList;
    public static String teacher;

    public SelectedAdapter(Context ctx, List<String> stringList,String teacher){
        this.layoutInflater = LayoutInflater.from(ctx);
        this.stringList = stringList;
        this.teacher = teacher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.selected_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.label.setText(stringList.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context ctx;
            ctx = layoutInflater.getContext();
            final SharedPreferences sett = ctx.getSharedPreferences("Settings", 0);
            final SharedPreferences.Editor editor = sett.edit();
            label = itemView.findViewById(R.id.select_text);
            label.setOnClickListener(v -> {
                if(teacher.equals("Преподаватель")){
                    editor.putBoolean("Teacher",true);
                }
                else{
                    editor.putBoolean("Teacher",false);
                }
                editor.putString("Link",label.getText().toString());
                editor.apply();
                ctx.startActivity(new Intent(ctx,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            });
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
