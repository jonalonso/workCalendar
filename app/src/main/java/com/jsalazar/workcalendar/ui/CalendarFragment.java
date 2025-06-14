package com.jsalazar.workcalendar.ui;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.database.repository.ContractRepository;
import com.jsalazar.workcalendar.database.repository.OverTimeRepository;
import com.jsalazar.workcalendar.database.repository.PaymentRepository;
import com.jsalazar.workcalendar.database.repository.TimeOffRepository;
import com.jsalazar.workcalendar.databinding.FragmentCalendarBinding;
import com.jsalazar.workcalendar.models.Contract;
import com.jsalazar.workcalendar.models.DayEvents;
import com.jsalazar.workcalendar.models.OverTime;
import com.jsalazar.workcalendar.models.PaymentDetail;
import com.jsalazar.workcalendar.models.TimeOff;
import com.jsalazar.workcalendar.ui.adapter.DayEventsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private Calendar selectedDate;
    private Calendar calendar;
    private GridLayout calendarGrid;
    private TextView txtMonthYear;
    private Contract previousContract = null;
    private boolean usePrimaryColor = true;

    private DayEventsAdapter adapter;


    private Animation rotateOpen = null;
    private Animation rotateClose = null;
    private Animation fromBottom = null;
    private Animation toBottom = null;

    private Boolean fabSwitch = false;

    private TextView currentSelectedDay = null;
    private List<Object> items  = new ArrayList<>();

    private ContractRepository contractRepo;
    private OverTimeRepository overTimeRepository;
    private TimeOffRepository timeOffRepo;
    private PaymentRepository paymentRepository;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        selectedDate = Calendar.getInstance();
        calendar = Calendar.getInstance();

        Date date = new Date();
        selectedDate.setTime(date);
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private String formatDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        return sdf.format(selectedDate.getTime());
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contractRepo = new ContractRepository(requireContext());
        overTimeRepository = new OverTimeRepository(requireContext());
        timeOffRepo = new TimeOffRepository(requireContext());
        paymentRepository = new PaymentRepository(requireContext());


        rotateOpen = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this.getContext(),R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this.getContext(),R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this.getContext(),R.anim.to_bottom_anim);

        binding.TextDate.setText(getString(R.string.events_of_day, formatDate()));
        calendarGrid = binding.calendarGrid;
        txtMonthYear = binding.txtMonthYear;

        contractRepo = new ContractRepository(requireContext());
        overTimeRepository = new OverTimeRepository(requireContext());
        timeOffRepo = new TimeOffRepository(requireContext());
        paymentRepository = new PaymentRepository(requireContext());



        binding.fab.setOnClickListener(view1 -> {
            fabSwitch = !fabSwitch;
            setVisibility();
            startAnimation();

        });

        binding.ContractFab.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(CalendarFragment.this)
            .navigate(R.id.action_CalendarFragment_to_ContractFragment);
            fabSwitch = false;
        });

        binding.PaymentFab.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(CalendarFragment.this)
                    .navigate(R.id.action_CalendarFragment_to_PaymentFragment);
            fabSwitch = false;
        });
        binding.TimeOffFab.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(CalendarFragment.this)
                    .navigate(R.id.action_CalendarFragment_to_TimeOffFragment);
            fabSwitch = false;
        });

        binding.OverTimefab.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(CalendarFragment.this)
                    .navigate(R.id.action_CalendarFragment_to_OverTimeFragment);
            fabSwitch = false;
        });

        adapter = new DayEventsAdapter();
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No movemos elementos, solo swipe
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                System.out.println(direction);
                int position = viewHolder.getAdapterPosition();
                Object item = adapter.getItemAt(position);

                if (item instanceof PaymentDetail) {
                    // paymentDetailViewModel.delete((PaymentDetail) item);
                } else if (item instanceof TimeOff) {
                    // timeOffViewModel.delete((TimeOff) item);
                } else if (item instanceof OverTime) {
                    // overTimeViewModel.delete((OverTime) item);
                } else if (item instanceof Contract) {
                    // contractViewModel.delete((Contract) item);
                }

                adapter.remove(item);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.eventList);

        binding.eventList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventList.setAdapter(adapter);
        setupCalendarButtons();
        renderCalendar();

        getParentFragmentManager().setFragmentResultListener("calendar_updated", this, (requestKey, result) -> {
            renderCalendar();
            Toast.makeText(getContext(), "Datos Actualizados, refrescando calendario", Toast.LENGTH_SHORT).show();
        });
    }

    private void startAnimation() {
        if (!fabSwitch) {
            binding.ContractFabContainer.startAnimation(toBottom);
            binding.PaymentFabContainer.startAnimation(toBottom);
            binding.TimeOffFabContainer.startAnimation(toBottom);
            binding.OverTimefabContainer.startAnimation(toBottom);
            binding.fab.startAnimation(rotateClose);
        } else {
            binding.ContractFabContainer.startAnimation(fromBottom);
            binding.PaymentFabContainer.startAnimation(fromBottom);
            binding.TimeOffFabContainer.startAnimation(fromBottom);
            binding.OverTimefabContainer.startAnimation(fromBottom);
            binding.fab.startAnimation(rotateOpen);
        }
    }

    private void setVisibility() {
        int visibility = fabSwitch ? View.VISIBLE : View.INVISIBLE;
        binding.ContractFabContainer.setVisibility(visibility);
        binding.PaymentFabContainer.setVisibility(visibility);
        binding.TimeOffFabContainer.setVisibility(visibility);
        binding.OverTimefabContainer.setVisibility(visibility);
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

    private void renderCalendar(){

        int colorOne = Color.parseColor("#f4d2dd");
        int colorTwo = Color.parseColor("#d4e6ef");

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startOfMonth = String.format(Locale.getDefault(), "%04d-%02d-01", year, month + 1);
        String endOfMonth = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, maxDay);

        List<Contract> contracts = contractRepo.getContractsForDateRange(startOfMonth, endOfMonth);


        for (int day = 1; day <= maxDay; day++) {
            Contract currentContract = null;
            FrameLayout container = createContainer();
            LinearLayout container2 = createContainerTwo();

            Calendar dayCal = Calendar.getInstance();
            dayCal.set(year, month, day,0,0,0);
            dayCal.set(Calendar.MILLISECOND, 0);
            boolean hasEvent = false;
            try {
                for (Contract element : contracts) {
                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();
                    startCal.setTime(Objects.requireNonNull(sdf.parse(element.initialDate)));
                    endCal.setTime(Objects.requireNonNull(sdf.parse(element.endDate)));
                    startCal.set(Calendar.HOUR_OF_DAY, 0);
                    startCal.set(Calendar.MINUTE, 0);
                    startCal.set(Calendar.SECOND, 0);
                    startCal.set(Calendar.MILLISECOND, 0);

                    endCal.set(Calendar.HOUR_OF_DAY, 0);
                    endCal.set(Calendar.MINUTE, 0);
                    endCal.set(Calendar.SECOND, 0);
                    endCal.set(Calendar.MILLISECOND, 0);
                    if (!dayCal.before(startCal) && !dayCal.after(endCal)) {
                        hasEvent = true;
                        currentContract = element;
                        break;
                    }
                }
            } catch (ParseException|NullPointerException e) {

            }


            TextView tv = createBodyText(String.valueOf(day));
            tv.setOnClickListener(v -> {
                if (currentSelectedDay != null) {
                    currentSelectedDay.setTypeface(null, Typeface.NORMAL);
                }
                selectedDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                selectedDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                selectedDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tv.getText().toString()));
                tv.setTypeface(null, Typeface.BOLD);
                currentSelectedDay = tv;
                binding.TextDate.setText(getString(R.string.events_of_day, formatDate()));
                loadDayEvents(selectedDate);
            });
            View eventIndicator = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics())
            );
            params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()); // marginBottom 4dp
            eventIndicator.setLayoutParams(params);
            eventIndicator.setBackgroundResource(R.drawable.event_indicator_red);
            eventIndicator.setVisibility(hasEvent ? View.VISIBLE : View.INVISIBLE);

            container2.addView(tv);
            container2.addView(eventIndicator);
            container.addView(container2);
            if (currentContract != null) {
                if (!currentContract.equals(previousContract)) {
                    usePrimaryColor = !usePrimaryColor;
                }
                previousContract = currentContract;

                int bgColor = usePrimaryColor ? colorOne : colorTwo;
                container.setBackgroundColor(bgColor);
            } else {
                container.setBackgroundColor(Color.TRANSPARENT);
            }
            calendarGrid.addView(container);
        }
        loadDayEvents(selectedDate);
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
            //tv.setBackgroundResource(R.drawable.selected_day_background);
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
        FrameLayout container = new FrameLayout(requireContext());
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

    private void loadDayEvents(Calendar date) {
        String selectedDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.getTime());
        Contract contract = contractRepo.getContractForDate(selectedDateStr);
        List<OverTime> overTimeList = overTimeRepository.getOverTimeForDate(selectedDateStr);
        OverTime overTime = overTimeList.isEmpty() ? null : overTimeList.get(0);
        List<TimeOff> timeOffList = timeOffRepo.getTimeOffForDateRange(selectedDateStr, selectedDateStr);
        List<PaymentDetail> paymentDetails = paymentRepository.getPaymentDetailsForDate(selectedDateStr);

        DayEvents dayEvents = new DayEvents(contract, overTime, paymentDetails, timeOffList);
        items = DayEventsAdapter.flattenDayEvents(dayEvents);
        adapter.updateItems(items);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}