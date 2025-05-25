package com.jsalazar.workcalendar.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.models.PaymentDetail;

import java.util.List;

public class PaymentDetailAdapter extends RecyclerView.Adapter<PaymentDetailAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(PaymentDetail detail);
    }

    private final List<PaymentDetail> detailList;
    private final OnDeleteClickListener deleteClickListener;
    private final Context context;

    public PaymentDetailAdapter(Context context, List<PaymentDetail> detailList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.detailList = detailList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentDetail detail = detailList.get(position);
        System.out.println(detail);
        holder.textType.setText(detail.type);

        boolean isStartEmpty = detail.startDate == null || detail.startDate.isEmpty();
        boolean isEndEmpty = detail.endDate == null || detail.endDate.isEmpty();

        if (isStartEmpty && isEndEmpty) {
            holder.textDates.setVisibility(View.GONE);
        } else {
            holder.textDates.setVisibility(View.VISIBLE);
            if (detail.startDate.equals(detail.endDate)) {
                holder.textDates.setText(context.getString(R.string.detail_date_single, detail.startDate));
            } else {
                holder.textDates.setText(context.getString(R.string.detail_date_range, detail.startDate, detail.endDate));
            }
        }

        holder.textAmount.setText(context.getString(R.string.detail_amount_format, detail.amount));

        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDelete(detail));
    }

    @Override
    public int getItemCount() {
        System.out.println(detailList.size());
        return detailList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textType, textDates, textAmount;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textDetailType);
            textDates = itemView.findViewById(R.id.textDateRange);
            textAmount = itemView.findViewById(R.id.textDetailAmount);
            btnDelete = itemView.findViewById(R.id.btnDeleteDetail);
        }
    }
}
