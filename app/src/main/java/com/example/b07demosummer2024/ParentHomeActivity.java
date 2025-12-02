package com.example.b07demosummer2024;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParentHomeActivity extends Fragment {
    private TextView zoneDisplay;
    private Spinner childSpinner;
    private Button setPbButton;
    private Button exportButton;
    private Button scheduleConfigButton;
    private Switch chartToggle;
    private AlertAdapter alertAdapter;
    private RecyclerView recyclerView;
    private List<String> childNameList;
    private FirebaseDatabase db;
    private DatabaseReference childRef;
    private List<AlertInstance> alertList;
    private DatabaseReference zoneRef;
    private LineChart lineChart;
    private ArrayList<Entry> lineEntries;
    private PieChart pieChart;
    private ArrayList<PieEntry> pieEntries;
    private CardView cardOne;
    private CardView cardTwo;
    private CardView cardThree;
    private TextView infoText;
    private TextView rescueDate;
    private TextView weeklyRescues;

    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home, container, false);

        FirebaseUser user = myAuth.getCurrentUser();
        String parentId = user.getUid();
        scheduleConfigButton = view.findViewById(R.id.scheduleConfigButton);
        setPbButton = view.findViewById(R.id.setPbButton);
        exportButton = view.findViewById(R.id.exportReportButton);
        zoneDisplay = view.findViewById(R.id.zoneDisplay);
        childSpinner = view.findViewById(R.id.childSpinner);
        cardOne = view.findViewById(R.id.cardOne);
        cardTwo = view.findViewById(R.id.cardTwo);
        cardThree = view.findViewById(R.id.cardThree);
        chartToggle = view.findViewById(R.id.chartToggle);
        infoText = view.findViewById(R.id.infoText);
        rescueDate = view.findViewById(R.id.rescueDate);
        weeklyRescues = view.findViewById(R.id.weeklyRescues);
        childNameList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_alerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        db = FirebaseDatabase.getInstance("https://smart-air-8a892-default-rtdb.firebaseio.com/");

        childNameList.clear();
        childNameList = fetchChildrenNamesFromDatabase("childProfile");
        childNameList = fetchChildrenNamesFromDatabase("childAccount");
        childNameList.add("Select Child Name");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, childNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childSpinner.setAdapter(adapter);

        alertList = new ArrayList<>();
        alertAdapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(alertAdapter);

        lineEntries = new ArrayList<>();
        pieEntries = new ArrayList<>();
        lineChart = view.findViewById(R.id.lineChart);
        pieChart = view.findViewById(R.id.pieChart);

        reloadPage("blank");

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportReport(requireContext());
            }
        });

        childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String childName = parent.getItemAtPosition(position).toString();
                if (childName.equals("Select Child Name")){
                    reloadPage("blank");
                    return;
                }
                db.getReference("parents/" + parentId + "/childProfile").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.getValue(String.class);
                            String childId = snapshot.getRef().getKey();
                            if (name != null && name.equalsIgnoreCase(childName)) {
                                reloadPage(childId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                db.getReference("parents/" + parentId + "/childAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.getValue(String.class);
                            String childId = snapshot.getRef().getKey();
                            if (name != null && name.equalsIgnoreCase(childName)) {
                                reloadPage(childId);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private List<String> fetchChildrenNamesFromDatabase(String childType) { //get list of string attached to identifiers
        FirebaseUser user = myAuth.getCurrentUser();
        String parentId = user.getUid();
        childRef = db.getReference("parents/" + parentId + "/" + childType);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String childName = snapshot.getValue(String.class);
                    childNameList.add(childName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        return childNameList;
    }
    private void reloadPage(String childId){
        if (childId.equals("blank")){
            hideDisplays();
            return;
        }
        showDisplays();
        setZoneDisplay(childId);
        fetchAlertsFromDatabase(childId);
        setRescueDate(childId);
        generatePieChart(30, childId);
        if (chartToggle.isChecked()) {
            generateLineChart(30, childId);
        } else {
            generateLineChart(7, childId);
        }
        chartToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    generateLineChart(30, childId);
                } else {
                    generateLineChart(7, childId);
                }
            }
        });
        setPbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PbEntryFragment PEF = new PbEntryFragment();
                Bundle args = new Bundle();
                args.putString("childId", childId);
                PEF.setArguments(args);
                loadFragment(PEF);
            }
        });
        scheduleConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PbEntryFragment PEF = new PbEntryFragment();
                Bundle args = new Bundle();
                args.putString("childId", childId);
                PEF.setArguments(args);
                loadFragment(PEF);
            }
        });
        // add onclick listener for export button.
    }

    private void hideDisplays(){
        cardOne.setVisibility(View.INVISIBLE);
        cardTwo.setVisibility(View.INVISIBLE);
        cardThree.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        infoText.setText("Select a child to view the dashboard!");
    }

    private void showDisplays(){
        cardOne.setVisibility(View.VISIBLE);
        cardTwo.setVisibility(View.VISIBLE);
        cardThree.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        infoText.setText("Recent Alerts");
    }

    private void fetchAlertsFromDatabase(String childId) {
        childRef = db.getReference("children/" + childId + "/alertHistory");
        Query alertQuery = childRef.orderByChild("timestamp").limitToLast(10);
        alertQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alertList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AlertInstance alert = snapshot.getValue(AlertInstance.class);
                    alertList.add(alert);
                }

                alertList.sort((a1, a2) ->
                        Long.compare(a2.getTimeStamp(), a1.getTimeStamp()));

                alertAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void setRescueDate(String childId) {
        db.getReference("children/" + childId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String c = dataSnapshot.child("childName").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        db.getReference("parents/medicalLogs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(snapshot.child("userName").getValue(String.class), childId)) {
                        if (Objects.equals(snapshot.child("linkedMedicineType").getValue(String.class), "rescue")){
                            Date date = new Date(snapshot.child("logDate").getValue(long.class));
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
                            rescueDate.setText(sdf.format(date));
                            return;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        db.getReference("parents/medicalLogs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(snapshot.child("userName").getValue(String.class), childId)) {
                        if (Objects.equals(snapshot.child("linkedMedicineType").getValue(String.class), "rescue")){
                            if (System.currentTimeMillis() - 604800000 < snapshot.child("logDate").getValue(long.class)){
                                count += 1;
                            }
                        }
                    }
                }
                weeklyRescues.setText("Weekly Rescue Count: " + String.valueOf(count));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setZoneDisplay(String childId) {
        String currentDate = java.time.LocalDate.now().toString();
        db.getReference("children/" + childId + "/pefHistory/" + currentDate + "/zone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.getValue(String.class), "green")){
                    //do green things
                    zoneDisplay.setTextColor(Color.parseColor("#006400"));
                }
                else if (Objects.equals(dataSnapshot.getValue(String.class), "yellow")){
                    zoneDisplay.setTextColor(Color.parseColor("#8B8000"));
                }
                else if (Objects.equals(dataSnapshot.getValue(String.class), "red")){
                    zoneDisplay.setTextColor(Color.parseColor("#8B0000"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        db.getReference("children/" + childId + "/pefHistory/" + currentDate + "/pefEntry").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null || dataSnapshot.getValue(String.class).isEmpty()){
                    zoneDisplay.setText("Not yet Logged");
                }
                else {
                    zoneDisplay.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        db.getReference("children/" + childId + "/pefHistory/" + currentDate + "/pefEntryOptional").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    if (!dataSnapshot.getValue(String.class).isEmpty()){
                        zoneDisplay.setText(dataSnapshot.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void exportReport(Context context) {
        PdfDocument report = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = report.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(16);

        // All the proper stuff goes here

        // TODO: Remove this
        canvas.drawText("SAMPLE TEXTTTTTTTTT", 75, 100, paint);
        canvas.drawText("PDFPDFPDF", 75, 125, paint);

        // TODO: Ensure charts are positioned correctly
        lineChart.setDrawingCacheEnabled(true);
        lineChart.buildDrawingCache();
        Bitmap chartBitmap = Bitmap.createBitmap(lineChart.getWidth(), lineChart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas chartCanvas = new Canvas(chartBitmap);
        lineChart.draw(chartCanvas);
        Rect src = new Rect(0, 0, chartBitmap.getWidth(), chartBitmap.getHeight()); // original bitmap
        Rect dst = new Rect(75, 150, 75 + 300, 150 + 150);
        canvas.drawBitmap(chartBitmap, src, dst, null);

        report.finishPage(page);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/report.pdf";

        File file = new File(path);

        try (OutputStream out = new FileOutputStream(file)) {
            report.writeTo(out);
            Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            report.close();
        }

    }

    private void generateLineChart(int numOfDays, String childId) {
        // List of last 7/30 days
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < numOfDays; i++) {
            String date = java.time.LocalDate.now().minusDays(i).toString();
            dates.add(date);
        }

        ArrayList<String> daysOfMonth = new ArrayList<>();
        for (int j = 0; j < dates.size(); j++) {
            daysOfMonth.add(dates.get(j).split("-")[2]);
        }

        zoneRef = db.getReference("children/" + childId + "/pefHistory");
        zoneRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(getContext(), "Failed to generate chart", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!lineEntries.isEmpty()) {
                lineEntries.clear();
            }

            DataSnapshot snapshot = task.getResult();
            for (int i = 0; i < dates.size(); i++) {
                String date = dates.get(i);
                DataSnapshot daySnapshot = snapshot.child("/" + date + "/zonePercentage");
                Integer value = daySnapshot.getValue(Integer.class);
                if (value != null) {
                    lineEntries.add(new Entry(numOfDays - 1 - i, value));
                } else {
                    lineEntries.add(new Entry(numOfDays - 1 - i, 0));
                }
            }

            Collections.reverse(lineEntries);
            Collections.reverse(daysOfMonth);

            // Sets up line information
            LineDataSet dataSet = new LineDataSet(lineEntries, "Sample Data");
            dataSet.setColor(Color.parseColor("#0000FF"));
            dataSet.setValueTextColor(Color.parseColor("#000000"));
            dataSet.setLineWidth(2f);

            // Additional configuration for the chart
            lineChart.getDescription().setEnabled(false);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
            lineChart.getAxisLeft().setAxisMinimum(0);
            lineChart.getAxisLeft().setAxisMaximum(100);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(daysOfMonth));

            // Generates chart
            lineChart.setData(new LineData(dataSet));
            lineChart.invalidate();
        });
    }

    private void generatePieChart(int numOfDays, String childId) {
        // List of last 7/30 days
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < numOfDays; i++) {
            String date = java.time.LocalDate.now().minusDays(i).toString();
            dates.add(date);
        }

        zoneRef = db.getReference("children/" + childId + "/pefHistory");
        zoneRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(getContext(), "Failed to generate chart", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pieEntries.isEmpty()) {
                pieEntries.clear();
            }

            int noZoneCounter = 0;
            int badZoneCounter = 0;
            int okayZoneCounter = 0;
            int goodZoneCounter = 0;

            DataSnapshot snapshot = task.getResult();
            for (int i = 0; i < dates.size(); i++) {
                String date = dates.get(i);
                DataSnapshot daySnapshot = snapshot.child("/" + date + "/zonePercentage");
                Integer value = daySnapshot.getValue(Integer.class);
                if (value != null) {
                    if (value < 50) {
                        badZoneCounter++;
                    } else if (value < 80) {
                        okayZoneCounter++;
                    } else {
                        goodZoneCounter++;
                    }
                } else {
                    noZoneCounter++;
                }
            }

            pieEntries.add(new PieEntry(goodZoneCounter, "Good zone days"));
            pieEntries.add(new PieEntry(okayZoneCounter, "Okay zone days"));
            pieEntries.add(new PieEntry(badZoneCounter, "Bad zone days"));
            pieEntries.add(new PieEntry(noZoneCounter, "Days not recorded"));

            pieChart.setDrawEntryLabels(false);
            Description description = new Description();
            description.setText("Zone Distribution");
            pieChart.setDescription(description);

            PieDataSet dataSet = new PieDataSet(pieEntries, "Zones");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(2f);
            dataSet.setValueTextSize(14f);

            pieChart.setData(new PieData(dataSet));
            pieChart.invalidate();
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
