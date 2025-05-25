package com.jsalazar.workcalendar.ui.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.models.*;

import java.util.ArrayList;
import java.util.List;


public class DayEventsAdapter extends ArrayAdapter<Object> {

    private static final int TYPE_CONTRACT = 0;
    private static final int TYPE_OVERTIME = 1;
    private static final int TYPE_PAYMENT_DETAIL = 2;
    private static final int TYPE_TIME_OFF = 3;

    private final LayoutInflater inflater;
    private final DeleteListener deleteListener;

    public interface DeleteListener {
        void onDelete(Object item);
    }

    public DayEventsAdapter(Context context, List<Object> events, DeleteListener deleteListener) {
        super(context, 0, events);
        this.inflater = LayoutInflater.from(context);
        this.deleteListener = deleteListener;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof Contract) return TYPE_CONTRACT;
        if (item instanceof OverTime) return TYPE_OVERTIME;
        if (item instanceof PaymentDetail) return TYPE_PAYMENT_DETAIL;
        if (item instanceof TimeOff) return TYPE_TIME_OFF;
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Object item = getItem(position);
        convertView = inflater.inflate(R.layout.item_event, parent, false);

        TextView title = convertView.findViewById(R.id.title);
        TextView subtitle = convertView.findViewById(R.id.subtitle);
        ImageView icon = convertView.findViewById(R.id.icon);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        switch (getItemViewType(position)) {
            case TYPE_CONTRACT:
                Contract c = (Contract) item;
                title.setText(c.serviceName);
                subtitle.setText(c.description);
                icon.setImageResource(R.drawable.ic_contract);
                break;

            case TYPE_OVERTIME:
                OverTime ot = (OverTime) item;
                title.setText(ot.serviceName);
                subtitle.setText(ot.description);
                icon.setImageResource(R.drawable.ic_overtime);
                break;

            case TYPE_PAYMENT_DETAIL:
                PaymentDetail pd = (PaymentDetail) item;
                title.setText(pd.type);
                subtitle.setText(String.format("$ %.2f", pd.amount));
                icon.setImageResource(R.drawable.ic_payment);
                break;

            case TYPE_TIME_OFF:
                TimeOff to = (TimeOff) item;
                title.setText(to.type);
                subtitle.setText(to.description);
                icon.setImageResource(R.drawable.ic_time_off);
                break;
        }

        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item);
            }
        });

        return convertView;
    }

    public static List<Object> flattenDayEvents(DayEvents events) {
        List<Object> list = new ArrayList<>();
        if (events.contract != null) list.add(events.contract);
        if (events.overTime != null) list.add(events.overTime);
        if (events.paymentDetails != null) list.addAll(events.paymentDetails);
        if (events.timeOffs != null) list.addAll(events.timeOffs);
        return list;
    }

    public void updateItems(List<Object> newItems) {
        clear();
        addAll(newItems);
        notifyDataSetChanged();
    }

}