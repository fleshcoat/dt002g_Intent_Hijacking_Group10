package se.miun.dt002g.group10.appc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  private static final String SHARED_PREF_APP_C = "sharedPrefAppC";
  private static final String LOG_LIST_KEY_APP_C = "logListKeyAppC";

  private RecyclerView.Adapter mAdapter;
  private ArrayList<LogItem> logItemList;
  private String intentMsg = null;
  private SimpleDateFormat sdf;
  private Button authButton;
  private TextView logTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    logTextView = findViewById(R.id.log_view);
    loadLogListItem();

    // Set time format
    sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    authButton = findViewById(R.id.intentButton);
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
  protected void onResume() {
    super.onResume();

    // Get the current system time
    String currentTime = sdf.format(new Date());

    try {
      intentMsg = this.getIntent().getExtras().getString(Intent.EXTRA_TEXT);
      logItemList.add(new LogItem(currentTime, getResources().getString(R.string.received) + intentMsg));
      logTextView.setVisibility(View.INVISIBLE);
    } catch (NullPointerException e) {
      // No intent received or unable to retrieve content
      authButton.setEnabled(false);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveLogListItem();
  }

  private void saveLogListItem() {
    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_APP_C, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    Gson gson = new Gson();

    // Store logListItem in json format
    String json = gson.toJson(logItemList);

    // Add json to shared preferences
    editor.putString(LOG_LIST_KEY_APP_C, json);

    editor.apply();
  }

  private void loadLogListItem() {
    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_APP_C, MODE_PRIVATE);
    Gson gson = new Gson();
    String json = sharedPreferences.getString(LOG_LIST_KEY_APP_C, null);

    // Specify that Gson should convert json to ArrayList of LogItems
    Type type = new TypeToken<ArrayList<LogItem>>() {}.getType();
    logItemList = gson.fromJson(json, type);

    if (logItemList == null) {
      logItemList = new ArrayList<>();
    }
  }

  private void addListenerButton() {
    authButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        // Do nothing if no intent is received
        if (intentMsg == null) {
          return;
        }

        // Manipulate the intent received
        if (intentMsg.equals("True")) {
          intentMsg = "False";
        } else if (intentMsg.equals("False")){
          intentMsg = "True";
        }

        // Get the current system time
        String currentTime = sdf.format(new Date());

        logItemList.add(new LogItem(currentTime, getResources().getString(R.string.sent) + intentMsg));

        // Update the view
        mAdapter.notifyDataSetChanged();

        // Return the same intentMsg and close the app
        setResult(1, new Intent().putExtra(Intent.EXTRA_TEXT, intentMsg));
        finish();
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