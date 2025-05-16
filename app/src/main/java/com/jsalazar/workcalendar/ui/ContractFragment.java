package com.jsalazar.workcalendar.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.database.repository.ContractRepository;
import com.jsalazar.workcalendar.databinding.FragmentContractBinding;

import java.util.Calendar;
import java.util.Locale;


public class ContractFragment extends Fragment {

    private FragmentContractBinding binding;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContractBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editInitialDate.setOnClickListener(v -> showDatePicker(binding.editInitialDate));
        binding.editEndDate.setOnClickListener(v -> showDatePicker(binding.editEndDate));
        binding.editStartTime.setOnClickListener(v -> showTimePicker(binding.editStartTime));
        binding.editEndTime.setOnClickListener(v -> showTimePicker(binding.editEndTime));
        setHourFields("06:00", "14:00", false);
        binding.spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0: // Mañana
                        setHourFields("06:00", "14:00", false);
                        break;
                    case 1: // Tarde
                        setHourFields("14:00", "22:00", false);
                        break;
                    case 2: // Noche
                        setHourFields("22:00", "06:00", false);
                        break;
                    case 3: // Personalizado
                        setHourFields("", "", true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnSaveContract.setOnClickListener(view1 -> {
            binding.btnSaveContract.setEnabled(false);
            if (!validateInputs()){
                binding.btnSaveContract.setEnabled(true);
                return;
            }
            showSavingDialog();


            String initialDate = binding.editInitialDate.getText().toString();
            String endDate = binding.editEndDate.getText().toString();
            String startTime = binding.editStartTime.getText().toString();
            String endTime = binding.editEndTime.getText().toString();
            String serviceName = binding.editServiceName.getText().toString();
            String description = binding.editDescription.getText().toString();

            new Thread(() -> {
                ContractRepository repository = new ContractRepository(requireContext());
                if (repository.isDateRangeOverlapping(initialDate, endDate)) {
                    requireActivity().runOnUiThread(() ->{
                                hideSavingDialog();
                                binding.btnSaveContract.setEnabled(true);
                                Toast.makeText(requireContext(), "Ya existe un contrato para estas fechas.", Toast.LENGTH_LONG).show();
                            }
                    );
                    return;
                }
                repository.insertContract(initialDate, endDate, startTime, endTime, serviceName, description);

                requireActivity().runOnUiThread(() -> {
                    hideSavingDialog();
                    Bundle result = new Bundle();
                    result.putBoolean("success", true);
                    getParentFragmentManager().setFragmentResult("contract_added", result);
                    NavHostFragment.findNavController(this).popBackStack();

                });
            }).start();

        });

    }



    private void showDatePicker(TextInputEditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    targetEditText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker(TextInputEditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    targetEditText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                0,//calendar.get(Calendar.MINUTE)
                false
        );
        timePickerDialog.show();
    }
    private void setHourFields(String start, String end, boolean visible) {
        binding.editStartTime.setText(start);
        binding.editEndTime.setText(end);

        binding.startTimeLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.endTimeLayout.setVisibility(visible ? View.VISIBLE : View.GONE);

        binding.editStartTime.setFocusable(visible);
        binding.editStartTime.setFocusableInTouchMode(visible);
        binding.editEndTime.setFocusable(visible);
        binding.editEndTime.setFocusableInTouchMode(visible);
    }

    private boolean validateInputs() {
        if (binding.editInitialDate.getText().toString().isEmpty()) {
            binding.initialDateLayout.setError("Requerido");
            return false;
        } else {
            binding.initialDateLayout.setError(null);
        }

        if (binding.editEndDate.getText().toString().isEmpty()) {
            binding.endDateLayout.setError("Requerido");
            return false;
        } else {
            binding.endDateLayout.setError(null);
        }

        int shiftPosition = binding.spinnerShift.getSelectedItemPosition();
        if (shiftPosition == 3) { // Personalizado
            if (binding.editStartTime.getText().toString().isEmpty()) {
                binding.startTimeLayout.setError("Requerido");
                return false;
            } else {
                binding.startTimeLayout.setError(null);
            }

            if (binding.editEndTime.getText().toString().isEmpty()) {
                binding.endTimeLayout.setError("Requerido");
                return false;
            } else {
                binding.endTimeLayout.setError(null);
            }
        }

        if (binding.editServiceName.getText().toString().isEmpty()) {
            binding.serviceNameLayout.setError("Requerido");
            return false;
        } else {
            binding.serviceNameLayout.setError(null);
        }

        if (binding.editDescription.getText().toString().isEmpty()) {
            binding.descriptionLayout.setError("Requerido");
            return false;
        } else {
            binding.descriptionLayout.setError(null);
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

}

