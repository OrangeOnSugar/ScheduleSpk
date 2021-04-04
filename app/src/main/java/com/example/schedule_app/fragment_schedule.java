package com.example.schedule_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import JSON.Schedule;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_schedule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_schedule extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_schedule() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_schedule.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_schedule newInstance(String param1, String param2) {
        fragment_schedule fragment = new fragment_schedule();
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

    private boolean End;
    private RecyclerView SchdeduelLust;
    private TextView MessageText, HeadTitle, DatePath;
    private ImageView MessageImage;
    private ImageButton OpenCalendarB;
    private RelativeLayout MessageLay;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String link;
    private boolean Teacher;
    private String teacherOrGroup;


    DatePickerDialog DatePickerDialogOpen;
    Calendar CalendarCurrent;

    private static String url = "https://gitlab.com/Zagrtdinov/schedule/-/raw/master/csvjson.json";

    List<Schedule> scheduleList;

    SimpleDateFormat df;

    RecyclerView.Adapter adapter;

    private Context context;

    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_schedule, container, false);


        CalendarCurrent = Calendar.getInstance();
        df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        final SharedPreferences sett = getContext().getApplicationContext().getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = sett.edit();

        if (sett.getBoolean("DayThem",false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        context = getContext().getApplicationContext();

        if(!sett.getString("Link","").equals("")) {
            if (sett.getBoolean("Teacher",false)){
                Teacher = true;
            }
            else {
                Teacher = false;
            }
            link = sett.getString("Link","");
        }


        MessageText = view.findViewById(R.id.message_text);
        MessageImage = view.findViewById(R.id.message_image);
        MessageLay = view.findViewById(R.id.message);

        HeadTitle = view.findViewById(R.id.title);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            extractSchedule(link, df.format(CalendarCurrent.getTime()), Teacher, teacherOrGroup);
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false),2*1000);
        });
        OpenCalendarB = view.findViewById(R.id.open_calendar);

        OpenCalendarB.setOnClickListener(v -> {
            int Day = CalendarCurrent.get(Calendar.DAY_OF_MONTH);
            int Month = CalendarCurrent.get(Calendar.MONTH);
            int Year = CalendarCurrent.get(Calendar.YEAR);
            DatePickerDialogOpen = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                String DateChange = dayOfMonth + "." + (month+1) + "." + year;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat SimpleOurDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date norma = null;
                try {
                    norma = SimpleOurDateFormat.parse(DateChange);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DatePath.setText(getCurrentDay(norma));
                CalendarCurrent.setTime(norma);
                extractSchedule(link,SimpleOurDateFormat.format(norma),Teacher, teacherOrGroup);
            },Year,Month,Day);
            DatePickerDialogOpen.show();
        });

        DatePath = view.findViewById(R.id.date_path);

        SchdeduelLust = view.findViewById(R.id.scheduleList);
        scheduleList = new ArrayList<>();

        Date date = Calendar.getInstance().getTime();
        DatePath.setText(getCurrentDay(date));

        if(Teacher){
            HeadTitle.setText("Преподаватель: "+link);
            teacherOrGroup = "Преподаватель";
        } else {
            HeadTitle.setText("Группа: "+link);
            teacherOrGroup = "Группа";
        }

        view.setOnTouchListener(new OnSwipeTouchListner(context) {
            public void onSwipeRight() {
                CalendarCurrent.add(Calendar.DATE,-1);
                DatePath.setText(getCurrentDay(CalendarCurrent.getTime()));
                extractSchedule(link,df.format(CalendarCurrent.getTime()),Teacher,teacherOrGroup);
            }
            public void onSwipeLeft() {
                CalendarCurrent.add(Calendar.DATE,1);
                DatePath.setText(getCurrentDay(CalendarCurrent.getTime()));
                extractSchedule(link,df.format(CalendarCurrent.getTime()),Teacher,teacherOrGroup);
            }
        });

        extractSchedule(link,df.format(date),Teacher,teacherOrGroup);

        return view;
    }

    public String getCurrentDay(Date date){

        String DaysArray[] = {"Воскресенье","Понедельник","Вторник", "Среда","Четверг","Пятница", "Суббота"};
        String MonthArray[] = {"Января","Февраля","Марта","Апреля","Мая","Июня","Июля","Августа","Сентября","Октября","Ноября","Декабря"};

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int DayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int Month = calendar.get(Calendar.MONTH);
        int DayofMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if(Calendar.getInstance().get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
            return DaysArray[DayOfWeek]+", "+DayofMonth+" "+ MonthArray[Month]+"(Сегодня)";
        }
        else {
            return DaysArray[DayOfWeek]+", "+DayofMonth+" "+ MonthArray[Month];
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getActivity().getString("kk");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SetAdapter(boolean teacher){
        Collections.sort(scheduleList, (first, second) -> Integer.compare(first.getNumber(),second.getNumber()));
        adapter = new Adapter(mContext, scheduleList, teacher);
        SchdeduelLust.setLayoutManager(new LinearLayoutManager(mContext));
        SchdeduelLust.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void extractSchedule(String group, String date, boolean teacher, String teachOrGroup) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        scheduleList.clear();
        SetAdapter(teacher);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject scheduleObj = response.getJSONObject(i);
                    Schedule schedule = new Schedule();

                    if (scheduleObj.getString(teachOrGroup).equals(group) && scheduleObj.getString("Дата").equals(date)) {
                        SchdeduelLust.setVisibility(View.VISIBLE);
                        MessageLay.setVisibility(View.GONE);
                        schedule.setGroupe(scheduleObj.getString("Группа"));
                        schedule.setDiscipline(scheduleObj.getString("Дисциплина"));
                        schedule.setNumber(scheduleObj.getInt("№ пары"));
                        schedule.setDatelearn(scheduleObj.getString("Дата"));
                        schedule.setTeacher(scheduleObj.getString("Преподаватель"));
                        schedule.setKabinet(scheduleObj.getString("Кабинет"));
                        schedule.setUndergroupe(scheduleObj.getString("Подгруппа"));
                        scheduleList.add(schedule);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(scheduleList.size() <= 0){
                SetMessage(R.drawable.none_schedule,"Расписание на данный день пока что отсутствует");
            }
            SetAdapter(teacher);
        }, error -> {
            Log.d("tag", "onErrorResponse: " + error.getMessage());
            SetMessage(R.drawable.none_internet,"Доступ в интернет отсутствует");
        });
        queue.add(jsonArrayRequest);
    }

    private void SetMessage(int idImage, String message){
        SchdeduelLust.setVisibility(View.GONE);
        MessageLay.setVisibility(View.VISIBLE);
        MessageImage.setImageDrawable(getResources().getDrawable(idImage));
        MessageText.setText(message);
    }

}