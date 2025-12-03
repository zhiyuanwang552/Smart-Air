package com.example.b07demosummer2024;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SymptomHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private SymptomHistoryAdapter adapter;

    private final List<DailyCheckInModel> allEntries = new ArrayList<>();
    private final List<DailyCheckInModel> filteredEntries = new ArrayList<>();

    // Multi-select filters
    private AutoCompleteTextView autoCompleteSymptoms;
    private ChipGroup chipGroupSymptoms;

    private AutoCompleteTextView autoCompleteTriggers;
    private ChipGroup chipGroupTriggers;

    // Date pickers
    private TextInputEditText fromDateInput, toDateInput;

    // Export buttons
    private Button exportPdfBtn, exportCsvBtn;

    // Firebase date-range bounds
    private long minTimestamp = Long.MAX_VALUE;
    private long maxTimestamp = Long.MIN_VALUE;

    private static final int STORAGE_PERMISSION_CODE = 100;

    // Possible filter values
    private final String[] symptomsList = {
            "activity_limits", "cough_wheeze", "night_waking", "none"
    };
    private final String[] triggersList = {
            "cold_air", "dust_pets", "exercise", "illness", "odors", "smoke"
    };

    public SymptomHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_symptom_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerSymptomHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SymptomHistoryAdapter(filteredEntries);
        recyclerView.setAdapter(adapter);

        autoCompleteSymptoms = view.findViewById(R.id.autoCompleteSymptoms);
        chipGroupSymptoms    = view.findViewById(R.id.chipGroupSymptoms);

        autoCompleteTriggers = view.findViewById(R.id.autoCompleteTriggers);
        chipGroupTriggers    = view.findViewById(R.id.chipGroupTriggers);

        fromDateInput = view.findViewById(R.id.textFromDate);
        toDateInput   = view.findViewById(R.id.textToDate);

        exportPdfBtn = view.findViewById(R.id.buttonExportPdf);
        exportCsvBtn = view.findViewById(R.id.buttonExportCsv);

        setupMultiSelectFilters();

        fromDateInput.setOnClickListener(v -> showDatePicker(true));
        toDateInput.setOnClickListener(v -> showDatePicker(false));

        exportCsvBtn.setOnClickListener(v -> exportCSV());
        exportPdfBtn.setOnClickListener(v -> exportPDF());

        loadFirebaseData();

        return view;
    }
    private void loadFirebaseData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://smart-air-8a892-default-rtdb.firebaseio.com/daily_check_ins/");

        ref.orderByChild("uid").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allEntries.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            DailyCheckInModel model = child.getValue(DailyCheckInModel.class);
                            if (model != null && model.getUid().equals(uid)) {
                                allEntries.add(model);
                            }
                        }

                        applyFilters();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "DB Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // solves the issue with dropdown not closing on focus loss
    private void fixDropdownFocusBehavior(AutoCompleteTextView dropdown) {
        dropdown.setOnDismissListener(() -> {
            TextInputLayout parent = (TextInputLayout) dropdown.getParent().getParent();
            parent.clearFocus();
            parent.setEndIconActivated(false);
        });

        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                TextInputLayout parent = (TextInputLayout) dropdown.getParent().getParent();
                parent.setEndIconActivated(false);
            }
        });
    }


    // Setup multi-select filters UI
    private void setupMultiSelectFilters() {
        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, symptomsList);
        autoCompleteSymptoms.setAdapter(symptomAdapter);

        autoCompleteSymptoms.setOnItemClickListener((parent, v, pos, id) -> {
            String sel = (String) parent.getItemAtPosition(pos);
            addChipToGroup(sel, chipGroupSymptoms);
            autoCompleteSymptoms.setText("");
            applyFilters();
        });

        ArrayAdapter<String> triggerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, triggersList);
        autoCompleteTriggers.setAdapter(triggerAdapter);

        autoCompleteTriggers.setOnItemClickListener((parent, v, pos, id) -> {
            String sel = (String) parent.getItemAtPosition(pos);
            addChipToGroup(sel, chipGroupTriggers);
            autoCompleteTriggers.setText("");
            applyFilters();
        });

        fixDropdownFocusBehavior(autoCompleteSymptoms);
        fixDropdownFocusBehavior(autoCompleteTriggers);
    }

    private void addChipToGroup(String value, ChipGroup group) {
        // avoid duplicates
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip c = (Chip) group.getChildAt(i);
            if (c.getText().equals(value)) return;
        }

        Chip chip = new Chip(getContext());
        chip.setText(value);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            applyFilters();
        });
        group.addView(chip);
    }

    // Show MaterialDatePicker for date filter
    private void showDatePicker(boolean isFrom) {
        Calendar today = Calendar.getInstance();
        long maxTimestamp = today.getTimeInMillis();  // END = today

        Calendar fiveYearsAgo = Calendar.getInstance();
        fiveYearsAgo.add(Calendar.YEAR, -5);
        long minTimestamp = fiveYearsAgo.getTimeInMillis(); // START = 5 years ago

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setStart(minTimestamp)
                .setEnd(maxTimestamp)
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                .datePicker()
                .setCalendarConstraints(constraints)
                .setTitleText(isFrom ? "Select start date" : "Select end date")
                .build();

        picker.show(getParentFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            long chosen = selection;
            String formatted = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date(chosen));

            if (isFrom) {
                fromDateInput.setText(formatted);
                fromDateInput.setTag(chosen);
            } else {
                toDateInput.setText(formatted);
                toDateInput.setTag(chosen);
            }

            applyFilters();
        });
    }

    // Filtering Logic (multi-select + date range)
    private void applyFilters() {
        long fromTime = fromDateInput.getTag() instanceof Long ? (Long) fromDateInput.getTag() : Long.MIN_VALUE;
        long toTime   = toDateInput.getTag()   instanceof Long ? (Long) toDateInput.getTag()   : Long.MAX_VALUE;

        // Collect selected symptoms
        Set<String> selSymptoms = new HashSet<>();
        for (int i = 0; i < chipGroupSymptoms.getChildCount(); i++) {
            Chip c = (Chip) chipGroupSymptoms.getChildAt(i);
            selSymptoms.add(c.getText().toString());
        }

        // Collect selected triggers
        Set<String> selTriggers = new HashSet<>();
        for (int i = 0; i < chipGroupTriggers.getChildCount(); i++) {
            Chip c = (Chip) chipGroupTriggers.getChildAt(i);
            selTriggers.add(c.getText().toString());
        }

        filteredEntries.clear();

        for (DailyCheckInModel e : allEntries) {
            if (e.timestamp < fromTime || e.timestamp > toTime) continue;

            // Symptoms filter: if any selected, require at least one match; else pass
            boolean symptomOk = selSymptoms.isEmpty();
            if (!symptomOk) {
                for (String s : selSymptoms) {
                    Boolean has = e.symptoms.get(s);
                    if (has != null && has) { symptomOk = true; break; }
                }
            }
            if (!symptomOk) continue;

            // Triggers filter: similar
            boolean triggerOk = selTriggers.isEmpty();
            if (!triggerOk) {
                for (String t : selTriggers) {
                    Boolean has = e.triggers.get(t);
                    if (has != null && has) { triggerOk = true; break; }
                }
            }
            if (!triggerOk) continue;

            // Passed all filters
            filteredEntries.add(e);
        }

        adapter.notifyDataSetChanged();
    }

    // Export CSV & PDF
    private void exportCSV() {
        if (!checkPermission()) return;

        File file = new File(Environment.getExternalStorageDirectory(), "symptom_history.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("timestamp,email,author,notes,symptoms,triggers\n");
            for (DailyCheckInModel e : filteredEntries) {
                writer.write(e.timestamp + "," +
                        e.email + "," +
                        e.author + "," +
                        "\"" + e.notes + "\"," +
                        e.symptoms + "," +
                        e.triggers + "\n");
            }
            Toast.makeText(getContext(), "CSV exported!", Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void exportPDF() {
        if (!checkPermission()) return;
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(30);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 1800, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 80;
        canvas.drawText("Symptom History Report", 40, y, paint);
        y += 60;

        for (DailyCheckInModel e : filteredEntries) {
            canvas.drawText("Date: " + e.timestamp, 40, y, paint); y += 40;
            canvas.drawText("Author: " + e.author, 40, y, paint); y += 40;
            canvas.drawText("Notes: " + e.notes, 40, y, paint); y += 40;
            canvas.drawText("Symptoms: " + e.symptoms, 40, y, paint); y += 40;
            canvas.drawText("Triggers: " + e.triggers, 40, y, paint); y += 60;
        }

        pdf.finishPage(page);

        File pdfFile = new File(Environment.getExternalStorageDirectory(), "symptom_history.pdf");
        try {
            pdf.writeTo(new java.io.FileOutputStream(pdfFile));
            Toast.makeText(getContext(), "PDF exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdf.close();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
            return false;
        }
        return true;
    }
}