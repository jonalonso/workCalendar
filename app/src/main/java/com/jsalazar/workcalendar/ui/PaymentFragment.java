package com.jsalazar.workcalendar.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.database.repository.PaymentRepository;
import com.jsalazar.workcalendar.databinding.FragmentPaymentBinding;
import com.jsalazar.workcalendar.models.Payment;
import com.jsalazar.workcalendar.models.PaymentDetail;
import com.jsalazar.workcalendar.ui.adapter.PaymentDetailAdapter;
import com.jsalazar.workcalendar.utils.DatePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PaymentFragment extends Fragment {


    private FragmentPaymentBinding binding;
    private final List<PaymentDetail> paymentDetails = new ArrayList<>();
    private PaymentDetailAdapter adapter;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editPaymentDate.setOnClickListener(v ->
                DatePicker.showDatePicker(binding.editPaymentDate, requireContext())
        );

        binding.editDetailStartDate.setOnClickListener(v ->
                DatePicker.showDatePicker(binding.editDetailStartDate, requireContext())
        );

        binding.editDetailEndDate.setOnClickListener(v ->
                DatePicker.showDatePicker(binding.editDetailEndDate, requireContext())
        );

        adapter = new PaymentDetailAdapter(requireContext(), paymentDetails, detail -> {
            paymentDetails.remove(detail);
            adapter.notifyDataSetChanged();
            updateNetAmount();
        });
        binding.detailRecyclerView.setAdapter(adapter);
        binding.detailRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.btnAddDetail.setOnClickListener(v -> addPaymentDetail());

        binding.btnSavePayment.setOnClickListener(v -> {
            binding.btnSavePayment.setEnabled(false);
            String date = binding.editPaymentDate.getText() != null ? binding.editPaymentDate.getText().toString().trim() : "";
            String amountStr = binding.editNetAmount.getText() != null ? binding.editNetAmount.getText().toString().trim() : "";

            if (!validatePaymentInputs(date, amountStr)) {
                binding.btnSavePayment.setEnabled(true);
                return;
            }

            showSavingDialog();

            new Thread(() -> {
                double amount = Double.parseDouble(amountStr);
                Payment payment = new Payment(0, amount, date, new ArrayList<>(paymentDetails));
                PaymentRepository repository = new PaymentRepository(requireContext());
                repository.insertPayment(payment);

                requireActivity().runOnUiThread(() -> {
                    hideSavingDialog();
                    Bundle result = new Bundle();
                    result.putBoolean("success", true);
                    getParentFragmentManager().setFragmentResult("calendar_updated", result);
                    NavHostFragment.findNavController(this).popBackStack();
                });
            }).start();
        });
    }

    private boolean validatePaymentInputs(String date, String amountStr) {
        if (date.isEmpty()) {
            binding.paymentDateLayout.setError(getString(R.string.error_date_required));
            return false;
        } else {
            binding.paymentDateLayout.setError(null);
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.error_amount_required), Toast.LENGTH_SHORT).show();
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), getString(R.string.error_amount_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (amount <= 0 || paymentDetails.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.error_no_details), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showSavingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_saving);
        progressDialog = builder.create();
        progressDialog.show();
    }

    private void hideSavingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void addPaymentDetail() {
        String type = binding.spinnerDetailType.getSelectedItem().toString();
        String startDate = binding.editDetailStartDate.getText() != null ? binding.editDetailStartDate.getText().toString().trim() : "";
        String endDate = binding.editDetailEndDate.getText() != null ? binding.editDetailEndDate.getText().toString().trim() : "";
        String amountStr = binding.editDetailAmount.getText() != null ? binding.editDetailAmount.getText().toString().trim() : "";

        if (amountStr.isEmpty()) {
            binding.detailAmountLayout.setError(getString(R.string.error_amount_required));
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            binding.detailAmountLayout.setError(getString(R.string.error_amount_positive));
            return;
        } else {
            binding.detailAmountLayout.setError(null);
        }

        boolean hasStart = !startDate.isEmpty();
        boolean hasEnd = !endDate.isEmpty();

        if (hasStart ^ hasEnd) {
            binding.detailStartDateLayout.setError(getString(R.string.error_both_dates_required));
            binding.detailEndDateLayout.setError(getString(R.string.error_both_dates_required));
            return;
        }

        if (hasStart && hasEnd && startDate.compareTo(endDate) > 0) {
            binding.detailStartDateLayout.setError(null);
            binding.detailEndDateLayout.setError(getString(R.string.error_end_date_after_start));
            return;
        }

        for (PaymentDetail existing : paymentDetails) {
            if (existing.type.equals(type)) {
                Toast.makeText(requireContext(), getString(R.string.error_duplicate_type), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        binding.detailStartDateLayout.setError(null);
        binding.detailEndDateLayout.setError(null);

        PaymentDetail detail = new PaymentDetail(0,0,type, hasStart ? startDate : null, hasEnd ? endDate : null, amount);
        paymentDetails.add(detail);
        adapter.notifyItemInserted(paymentDetails.size() - 1);

        updateNetAmount();
        clearDetailInputs();
    }

    private void updateNetAmount() {
        double total = 0;
        for (PaymentDetail detail : paymentDetails) {
            total += detail.amount;
        }
        binding.editNetAmount.setText(String.format(Locale.getDefault(), "%.2f", total));
    }

    private void clearDetailInputs() {
        binding.spinnerDetailType.setSelection(0);
        binding.editDetailStartDate.setText("");
        binding.editDetailEndDate.setText("");
        binding.editDetailAmount.setText("");
    }
}