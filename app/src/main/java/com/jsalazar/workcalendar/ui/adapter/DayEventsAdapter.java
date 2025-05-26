package com.jsalazar.workcalendar.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.models.*;

import java.util.ArrayList;
import java.util.List;

public class DayEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CONTRACT = 0;
    private static final int TYPE_OVERTIME = 1;
    private static final int TYPE_PAYMENT_DETAIL = 2;
    private static final int TYPE_TIME_OFF = 3;
    private final List<Object> items = new ArrayList<>();

    public DayEventsAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Contract) return TYPE_CONTRACT;
        if (item instanceof OverTime) return TYPE_OVERTIME;
        if (item instanceof PaymentDetail) return TYPE_PAYMENT_DETAIL;
        if (item instanceof TimeOff) return TYPE_TIME_OFF;
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        ((EventViewHolder) holder).bind(item, getItemViewType(position));
    }

    public Object getItemAt(int position) {
        return items.get(position);
    }

    public void remove(Object item) {
        int index = items.indexOf(item);
        if (index != -1) {
            items.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void updateItems(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public static List<Object> flattenDayEvents(DayEvents events) {
        List<Object> list = new ArrayList<>();
        if (events.contract != null) list.add(events.contract);
        if (events.overTime != null) list.add(events.overTime);
        if (events.paymentDetails != null) list.addAll(events.paymentDetails);
        if (events.timeOffs != null) list.addAll(events.timeOffs);
        return list;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView subtitle;
        private final ImageView icon;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            icon = itemView.findViewById(R.id.icon);
        }

        public void bind(Object item, int viewType) {
            switch (viewType) {
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
        }
    }
}
