package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateNudgeFragment extends Fragment {
    public CreateNudgeFragment() {
        super(R.layout.fragment_create_nudge);
    }

    private EditText etTitle, etBody;
    private Button   btnSave;
    private NudgeRepository repo;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind to the IDs in your XML
        etTitle = view.findViewById(R.id.etTitle);
        etBody  = view.findViewById(R.id.etBody);
        btnSave = view.findViewById(R.id.btnSave);

        repo = new NudgeRepository();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String body  = etBody.getText().toString().trim();

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(requireContext(),
                                "Both title and body are required",
                                Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            repo.createTemplate(title, body, new NudgeRepository.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireContext(),
                                    "Template saved!",
                                    Toast.LENGTH_SHORT)
                            .show();
                    // go back to the list
                    requireActivity().getSupportFragmentManager()
                            .popBackStack();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                            .show();
                }
            });
        });
    }
}
