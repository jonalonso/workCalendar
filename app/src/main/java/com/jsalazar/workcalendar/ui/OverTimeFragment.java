package com.jsalazar.workcalendar.ui;

import static com.jsalazar.workcalendar.utils.DatePicker.showDatePicker;
import static com.jsalazar.workcalendar.utils.DatePicker.showTimePicker;

import android.app.AlertDialog;
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

import com.jsalazar.workcalendar.R;
import com.jsalazar.workcalendar.database.repository.ContractRepository;
import com.jsalazar.workcalendar.database.repository.OverTimeRepository;
import com.jsalazar.workcalendar.databinding.FragmentOverTimeBinding;
import com.jsalazar.workcalendar.models.OverTime;

import java.util.List;

public class OverTimeFragment extends Fragment {

    private FragmentOverTimeBinding binding;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOverTimeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editInitialDate.setOnClickListener(v -> showDatePicker(binding.editInitialDate, requireContext()));
        binding.editStartTime.setOnFocusChangeListener((v, s) -> {
            if (s) showTimePicker(binding.editStartTime, requireContext());
        });
        binding.editEndTime.setOnFocusChangeListener((v, s) -> {
            if (s) showTimePicker(binding.editEndTime, requireContext());
        });

        setHourFields("06:00", "14:00", false);

        binding.spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        setHourFields("06:00", "14:00", false);
                        break;
                    case 1:
                        setHourFields("14:00", "22:00", false);
                        break;
                    case 2:
                        setHourFields("22:00", "06:00", false);
                        break;
                    case 3:
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
            String date = String.valueOf(binding.editInitialDate.getText());
            String startTime = String.valueOf(binding.editStartTime.getText());
            String endTime = String.valueOf(binding.editEndTime.getText());
            String serviceName = String.valueOf(binding.editServiceName.getText());
            String description = String.valueOf(binding.editDescription.getText());

            if (!validateInputs(date, startTime, endTime, serviceName, description)) {
                binding.btnSaveContract.setEnabled(true);
                return;
            }

            showSavingDialog();

            new Thread(() -> {
                ContractRepository contractRepo = new ContractRepository(requireContext());
                if (!contractRepo.isDateRangeOverlapping(date, date)) {
                    requireActivity().runOnUiThread(() -> {
                        hideSavingDialog();
                        binding.btnSaveContract.setEnabled(true);
                        Toast.makeText(requireContext(), getString(R.string.no_contract_exists), Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                OverTimeRepository overtimeRepo = new OverTimeRepository(requireContext());
                List<OverTime> overTimes = overtimeRepo.getOverTimeForDate(date);
                if (overTimes.size()>1) {
                    requireActivity().runOnUiThread(() -> {
                        hideSavingDialog();
                        binding.btnSaveContract.setEnabled(true);
                        Toast.makeText(requireContext(), getString(R.string.overtime_exists_message), Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                overtimeRepo.insertOverTime(date, startTime, endTime, serviceName, description);

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

    private boolean validateInputs(String date, String startTime, String endTime, String serviceName, String description) {
        if (date.isEmpty()) {
            binding.initialDateLayout.setError(getString(R.string.field_required_error));
            return false;
        } else {
            binding.initialDateLayout.setError(null);
        }

        int shiftPosition = binding.spinnerShift.getSelectedItemPosition();
        if (shiftPosition == 3) {
            if (startTime.isEmpty()) {
                binding.startTimeLayout.setError(getString(R.string.field_required_error));
                return false;
            } else {
                binding.startTimeLayout.setError(null);
            }

            if (endTime.isEmpty()) {
                binding.endTimeLayout.setError(getString(R.string.field_required_error));
                return false;
            } else {
                binding.endTimeLayout.setError(null);
            }
        }

        if (serviceName.isEmpty()) {
            binding.serviceNameLayout.setError(getString(R.string.field_required_error));
            return false;
        } else {
            binding.serviceNameLayout.setError(null);
        }

        if (description.isEmpty()) {
            binding.descriptionLayout.setError(getString(R.string.field_required_error));
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
