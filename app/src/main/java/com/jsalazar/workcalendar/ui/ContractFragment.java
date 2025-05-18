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
import com.jsalazar.workcalendar.databinding.FragmentContractBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ContractFragment extends Fragment {

    private FragmentContractBinding binding;
    private AlertDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContractBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editInitialDate.setOnClickListener(v -> showDatePicker(binding.editInitialDate, requireContext()));
        binding.editEndDate.setOnClickListener(v -> showDatePicker(binding.editEndDate, requireContext()));
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
            String initialDate = String.valueOf(binding.editInitialDate.getText());
            String endDate = String.valueOf(binding.editEndDate.getText());
            String startTime = String.valueOf(binding.editStartTime.getText());
            String endTime = String.valueOf(binding.editEndTime.getText());
            String serviceName = String.valueOf(binding.editServiceName.getText());
            String description = String.valueOf(binding.editDescription.getText());
            if (!validateInputs(initialDate, endDate, startTime, endTime, serviceName, description)) {
                binding.btnSaveContract.setEnabled(true);
                return;
            }
            showSavingDialog();


            new Thread(() -> {
                ContractRepository repository = new ContractRepository(requireContext());
                if (repository.isDateRangeOverlapping(initialDate, endDate)) {
                    requireActivity().runOnUiThread(() -> {
                                hideSavingDialog();
                                binding.btnSaveContract.setEnabled(true);
                                Toast.makeText(requireContext(), getString(R.string.contract_exists_message), Toast.LENGTH_LONG).show();
                            }
                    );
                    return;
                }
                repository.insertContract(initialDate, endDate, startTime, endTime, serviceName, description);

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

    private boolean validateInputs(String initialDate, String endDate, String startTime, String endTime, String serviceName, String description) {
        if (initialDate.isEmpty()) {
            binding.initialDateLayout.setError(getString(R.string.field_required_error));
            return false;
        } else {
            binding.initialDateLayout.setError(null);
        }

        if (endDate.isEmpty()) {
            binding.endDateLayout.setError(getString(R.string.field_required_error));
            return false;
        } else {
            binding.endDateLayout.setError(null);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date initial = sdf.parse(initialDate);
            Date end = sdf.parse(endDate);

            if (initial != null && end != null && end.before(initial)) {
                binding.endDateLayout.setError(getString(R.string.end_date_after_start_error));
                return false;
            } else {
                binding.endDateLayout.setError(null);
            }
        } catch (ParseException e) {
            binding.endDateLayout.setError(getString(R.string.invalid_date_format_error));
            return false;
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

