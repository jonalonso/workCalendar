package com.jsalazar.workcalendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jsalazar.workcalendar.databinding.FragmentFirstBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Calendar selectedDate;
    private Calendar calendar;
    private GridLayout calendarGrid;
    private TextView txtMonthYear;


    private Animation rotateOpen = null;
    private Animation rotateClose = null;
    private Animation fromBottom = null;
    private Animation toBottom = null;

    private Boolean fabSwitch = false;
    //private Set<Calendar> eventDates = new HashSet<>();

    private TextView currentSelectedDay = null;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        selectedDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        // Agregar algunas fechas con eventos

        Date date = new Date();
        selectedDate.setTime(date);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private String formatDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        return sdf.format(selectedDate.getTime());
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rotateOpen = AnimationUtils.loadAnimation(this.getContext(),R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this.getContext(),R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this.getContext(),R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this.getContext(),R.anim.to_bottom_anim);


        //binding.buttonFirst.setOnClickListener(v ->
        //        NavHostFragment.findNavController(FirstFragment.this)
        //                .navigate(R.id.action_FirstFragment_to_SecondFragment)
        //);


        binding.TextDate.setText("Eventos del día "+formatDate());
        calendarGrid = binding.calendarGrid;
        txtMonthYear = binding.txtMonthYear;

        setupCalendarButtons();
        renderCalendar();



        //binding.mainCalendar.date((widget, calendar, selected) -> {
            //month is 0 based
        //    selectedDate.set(calendar.getYear(),calendar.getMonth(),calendar.getDay());
        //    binding.TextDate.setText("Eventos del día "+formatDate());
        //});


        binding.fab.setOnClickListener(view1 -> {
            fabSwitch = !fabSwitch;
            setVisibility();
            startAnimation();

        });

        binding.fab1.setOnClickListener(view1 -> Toast.makeText(FirstFragment.this.getContext(),"click1",Toast.LENGTH_SHORT).show());

        binding.fab2.setOnClickListener(view1 -> Toast.makeText(FirstFragment.this.getContext(),"click2",Toast.LENGTH_SHORT).show());
        binding.fab3.setOnClickListener(view1 -> Toast.makeText(FirstFragment.this.getContext(),"click3",Toast.LENGTH_SHORT).show());
        binding.fab4.setOnClickListener(view1 -> Toast.makeText(FirstFragment.this.getContext(),"click4",Toast.LENGTH_SHORT).show());

    }

    private void startAnimation() {
        if(!fabSwitch){
            binding.fab1.startAnimation(toBottom);
            binding.fab2.startAnimation(toBottom);
            binding.fab3.startAnimation(toBottom);
            binding.fab4.startAnimation(toBottom);
            binding.fab.startAnimation(rotateClose);
        }else{
            binding.fab1.startAnimation(fromBottom);
            binding.fab2.startAnimation(fromBottom);
            binding.fab3.startAnimation(fromBottom);
            binding.fab4.startAnimation(fromBottom);
            binding.fab.startAnimation(rotateOpen);
        }
    }

    private void setVisibility() {
        if(!fabSwitch){
            binding.fab1.setVisibility(View.INVISIBLE);
            binding.fab2.setVisibility(View.INVISIBLE);
            binding.fab3.setVisibility(View.INVISIBLE);
            binding.fab4.setVisibility(View.INVISIBLE);
        }else{
            binding.fab1.setVisibility(View.VISIBLE);
            binding.fab2.setVisibility(View.VISIBLE);
            binding.fab3.setVisibility(View.VISIBLE);
            binding.fab4.setVisibility(View.VISIBLE);
        }
    }

    private void setupCalendarButtons() {
        binding.btnPrev.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            renderCalendar();
        });

        binding.btnNext.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            renderCalendar();
        });
    }

    private void renderCalendar() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Domingo = 1 → 0-based

        txtMonthYear.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime()));

        calendarGrid.removeAllViews();

        String[] weekDays = {"L", "M", "K", "J", "V", "S", "D"};
        for (String day : weekDays) {
            FrameLayout container = createContainer();

            TextView tv = createHeaderText(day);

            container.addView(tv);
            calendarGrid.addView(container);
        }

        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGrid.addView(createContainer());
        }

        for (int day = 1; day <= maxDay; day++) {
            FrameLayout container = createContainer();
            LinearLayout container2 = createContainerTwo();


            TextView tv = createBodyText(String.valueOf(day));
            tv.setOnClickListener(v -> {
                if (currentSelectedDay != null) {
                    currentSelectedDay.setTypeface(null, Typeface.NORMAL);
                    currentSelectedDay.setBackgroundResource(0);
                }
                selectedDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                selectedDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                selectedDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tv.getText().toString()));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setBackgroundResource(R.drawable.selected_day_background);
                currentSelectedDay = tv;
                binding.TextDate.setText("Eventos del día " + formatDate());
            });

            View eventIndicator = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics())
            );
            params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()); // marginBottom 4dp
            eventIndicator.setLayoutParams(params);
            eventIndicator.setBackgroundResource(R.drawable.event_indicator_red); // Color inicial (opcional)
            eventIndicator.setVisibility(View. VISIBLE); // Oculto por defecto

            container2.addView(tv);
            container2.addView(eventIndicator);
            container.addView(container2);
            calendarGrid.addView(container);
        }
    }

    private TextView createHeaderText(String day) {
        TextView tv = new TextView(getContext());
        tv.setText(day);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private TextView createBodyText(String day) {
        TextView tv = new TextView(getContext());
        tv.setText(String.valueOf(day));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(8, 8, 8, 8);
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

        if (isSameDay(tempCal, selectedDate)) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundResource(R.drawable.selected_day_background);
            currentSelectedDay = tv;
        }
        return tv;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private FrameLayout createContainer() {
        FrameLayout container = new FrameLayout(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 100;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(2, 2, 2, 2);
        container.setLayoutParams(params);
        return container;
    }

    private LinearLayout createContainerTwo() {
        LinearLayout container = new LinearLayout(getContext());
        container.setGravity(Gravity.CENTER);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        container.setOrientation(LinearLayout.VERTICAL);
        return container;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}