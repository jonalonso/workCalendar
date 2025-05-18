package com.jsalazar.workcalendar.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.database.repository.ContractRepository;
import com.jsalazar.workcalendar.database.repository.TimeOffRepository;
import com.jsalazar.workcalendar.databinding.FragmentTimeOffBinding;
import com.jsalazar.workcalendar.utils.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class TimeOffFragment extends Fragment {

    private FragmentTimeOffBinding binding;
    private TimeOffRepository timeOffRepo;
    private ContractRepository contractRepo;
    private AlertDialog progressDialog;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimeOffBinding.inflate(inflater, container, false);
        timeOffRepo = new TimeOffRepository(requireContext());
        contractRepo = new ContractRepository(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editInitialDate.setOnClickListener(v ->
                DatePicker.showDatePicker(binding.editInitialDate, requireContext()));

        binding.editEndDate.setOnClickListener(v ->
                DatePicker.showDatePicker(binding.editEndDate, requireContext()));

        binding.btnSaveTimeOff.setOnClickListener(v -> saveTimeOff());
    }

    private void saveTimeOff() {
        showSavingDialog();
        binding.btnSaveTimeOff.setEnabled(false);
        String initialDate = String.valueOf(binding.editInitialDate.getText());
        String endDate = String.valueOf(binding.editEndDate.getText());
        String type = binding.spinnerTimeOffType.getSelectedItem().toString();
        String description = String.valueOf(binding.editDescription.getText());

        if (!validateDates(initialDate, endDate)) {
            binding.btnSaveTimeOff.setEnabled(true);
            hideSavingDialog();
            return;
        }

        new Thread(() -> {
            if (!contractRepo.isDateRangeOverlapping(initialDate, endDate)) {
                requireActivity().runOnUiThread(() -> {
                            hideSavingDialog();
                            binding.btnSaveTimeOff.setEnabled(true);
                            Toast.makeText(requireContext(), "Debes tener un nombramiento activo en las fechas establecidas", Toast.LENGTH_LONG).show();
                        }
                );
                return;
            }
            timeOffRepo.insertTimeOff(initialDate, endDate, type, description);

            requireActivity().runOnUiThread(() -> {
                hideSavingDialog();
                Bundle result = new Bundle();
                result.putBoolean("success", true);
                getParentFragmentManager().setFragmentResult("calendar_updated", result);
                NavHostFragment.findNavController(this).popBackStack();

            });
        }).start();
    }

    private boolean validateDates(String initialDate, String endDate) {
        boolean isValid = true;

        if (initialDate.isEmpty()) {
            binding.initialDateLayout.setError(getString(R.string.field_required_error));
            isValid = false;
        } else {
            binding.initialDateLayout.setError(null);
        }

        if (endDate.isEmpty()) {
            binding.endDateLayout.setError(getString(R.string.field_required_error));
            isValid = false;
        } else {
            binding.endDateLayout.setError(null);
        }

        if (isValid) {
            boolean isStartValid = false;
            try {
                Date start = dateFormat.parse(initialDate);
                isStartValid = true;
                Date end = dateFormat.parse(endDate);
                if (start != null && end != null && end.before(start)) {
                    binding.endDateLayout.setError(getString(R.string.end_date_after_start_error));
                    isValid = false;
                }
            } catch (ParseException e) {
                if(isStartValid){
                    binding.endDateLayout.setError(getString(R.string.invalid_date_format_error));
                }else{
                    binding.initialDateLayout.setError(getString(R.string.invalid_date_format_error));
                }
                isValid = false;
            }
        }

        return isValid;
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
}