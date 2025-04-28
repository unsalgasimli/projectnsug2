package com.unsalgasimliapplicationsnsug.projectnsug;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SendNudgeFragment extends Fragment {
    private NudgeRepository repo;

    public SendNudgeFragment() {
        super(R.layout.fragment_send_nudge);
    }

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_send_nudge, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        repo = new NudgeRepository();

        RecyclerView rv = view.findViewById(R.id.rv_available_nudges);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // start with an empty list
        List<Nudge> templates = new ArrayList<>();
        NudgeAdapter adapter = new NudgeAdapter(templates, template -> {
            // pick first partner for demo
            PartnerRepository.getMyPartners(partners -> {
                if (partners.isEmpty()) {
                    Toast.makeText(requireContext(),
                            "You have no partners!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String toUid = partners.get(0).getPartnerId();
                repo.send(template, toUid, new NudgeRepository.Callback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(),
                                "Sent “" + template.getTitle() + "”",
                                Toast.LENGTH_SHORT).show();
                        // optionally pop back to the previous screen:
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(),
                                "Failed to send: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
        rv.setAdapter(adapter);

        // now load *all* your custom templates and update the list
        repo.getAllTemplates(new NudgeRepository.ListCallback() {
            @Override
            public void onResult(List<Nudge> items) {
                adapter.update(items);
            }
        });
    }
}
