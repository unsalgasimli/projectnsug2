package com.unsalgasimliapplicationsnsug.projectnsug;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class EditNoteDialog {

    /**
     * Show a modal dialog allowing the user to edit a short piece of text.
     *
     * @param context     Any Context (e.g. your Activity).
     * @param currentText The initial text to populate.
     * @param callback    Called if the user taps “Save” with the new text.
     */
    public static void show(Context context, String currentText, OnNoteUpdated callback) {
        // Create an EditText
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setText(currentText);
        input.setSelection(currentText != null ? currentText.length() : 0);

        new AlertDialog.Builder(context)
                .setTitle("Edit Profile Note")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String updated = input.getText().toString().trim();
                    callback.onNoteUpdated(updated);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Callback invoked when the user saves the edited text. */
    public interface OnNoteUpdated {
        void onNoteUpdated(String newText);
    }
}
