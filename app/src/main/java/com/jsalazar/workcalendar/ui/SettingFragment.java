package com.jsalazar.workcalendar.ui;

import static com.jsalazar.workcalendar.database.SharedPreferences.KEY_EVENING;
import static com.jsalazar.workcalendar.database.SharedPreferences.KEY_NIGHT;
import static com.jsalazar.workcalendar.database.SharedPreferences.KEY_WAGE;
import static com.jsalazar.workcalendar.database.SharedPreferences.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jsalazar.workcalendar.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        loadPreferences();

        binding.btnSaveSettings.setOnClickListener(v -> {
            String wage = String.valueOf(binding.inputHourlyWage.getText());
            String evening = String.valueOf(binding.inputEveningBonus.getText());
            String night = String.valueOf(binding.inputNightBonus.getText());

            if (TextUtils.isEmpty(wage) || TextUtils.isEmpty(evening) || TextUtils.isEmpty(night)) {
                Toast.makeText(getContext(), "Por favor, complete los tres campos", Toast.LENGTH_SHORT).show();
                return;
            }

            sharedPreferences.edit()
                    .putFloat(KEY_WAGE, Float.parseFloat(wage))
                    .putFloat(KEY_EVENING, Float.parseFloat(evening))
                    .putFloat(KEY_NIGHT, Float.parseFloat(night))
                    .apply();

            Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadPreferences() {
        float wage = sharedPreferences.getFloat(KEY_WAGE, 0);
        float evening = sharedPreferences.getFloat(KEY_EVENING, 0);
        float night = sharedPreferences.getFloat(KEY_NIGHT, 0);

        if (wage != 0) {
            binding.inputHourlyWage.setText(String.valueOf(wage));
        }
        if (evening != 0) {
            binding.inputEveningBonus.setText(String.valueOf(evening));
        }
        if (night != 0) {
            binding.inputNightBonus.setText(String.valueOf(night));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
