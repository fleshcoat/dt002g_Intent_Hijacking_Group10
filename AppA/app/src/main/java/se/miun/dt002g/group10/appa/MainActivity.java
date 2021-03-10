package se.miun.dt002g.group10.appa;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static final String SHARED_PREF_APP_A = "sharedPrefAppA";
    private static final String LOG_LIST_KEY_APP_A = "logListKeyAppA";
    private static final String ACTION_VICTIM_INTENT = "se.miun.dt002g.group10.VICTIM_INTENT";

    private RecyclerView.Adapter mAdapter;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ArrayList<LogItem> logItemList;
    private String intentMsg = null;
    private SimpleDateFormat sdf;
    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.log_view);
        loadLogListItem();

        // Set time format
        sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        addListenerButton();

        RecyclerView mRecyclerView = findViewById(R.id.log_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new LogItemAdapter(logItemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if (logItemList.isEmpty()) {
            logTextView.setVisibility(View.VISIBLE);
        } else {
            logTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLogListItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            try {
                String receivedMsg = data.getExtras().getString(Intent.EXTRA_TEXT);
                String currentTime = sdf.format(new Date());
                LogItem logItem = new LogItem(currentTime,
                    getResources().getString(R.string.received) + receivedMsg);

                if (!receivedMsg.equals(this.intentMsg)) {
                    logItem.setTextColor(Color.RED);
                }

                logItemList.add(logItem);

                // Update the view
                mAdapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                // Do nothing. Unable to retrieve content
            }
        }
    }

    private void saveLogListItem() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_APP_A, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Store logListItem in json format
        String json = gson.toJson(logItemList);

        // Add json to shared preferences
        editor.putString(LOG_LIST_KEY_APP_A, json);

        editor.apply();
    }


    private void loadLogListItem() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_APP_A, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(LOG_LIST_KEY_APP_A, null);


        // Specify that Gson should convert json to ArrayList of LogItems
        Type type = new TypeToken<ArrayList<LogItem>>() {
        }.getType();
        logItemList = gson.fromJson(json, type);

        if (logItemList == null) {
            logItemList = new ArrayList<>();
        }
    }

    private void addListenerButton() {
        Button intentButton = findViewById(R.id.intentButton);
        radioGroup = findViewById(R.id.radioGroup);

        intentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                intentMsg = (String) radioButton.getText();

                // Create the text message from the value of the radiobutton
                Intent implicitIntent = new Intent(ACTION_VICTIM_INTENT);
                implicitIntent.putExtra(Intent.EXTRA_TEXT, intentMsg);
                implicitIntent.setType("text/plain");

                // Get the current system time
                String currentTime = sdf.format(new Date());

                logItemList.add(new LogItem(currentTime,
                        getResources().getString(R.string.sent) + intentMsg));

                if (logTextView.getVisibility() == View.VISIBLE) {
                    logTextView.setVisibility(View.INVISIBLE);
                }

                // Update the view
                mAdapter.notifyDataSetChanged();

                // Try to send implicit intent
                try {
                    startActivityForResult(implicitIntent, REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    // Temporary toast
                    Toast.makeText(getApplicationContext(), "Failed to start activity!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about:
                showAbout();
                break;
            case R.id.action_clear:
                clearData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {

        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);


        adb.setTitle(getString(R.string.about_str));
        adb.setMessage(getString(R.string.about_information));
        adb.setCancelable(true);
        adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog aboutDialog = adb.create();
        aboutDialog.show();
    }

    private void clearData() {
        logItemList.clear();
        logTextView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }
}
