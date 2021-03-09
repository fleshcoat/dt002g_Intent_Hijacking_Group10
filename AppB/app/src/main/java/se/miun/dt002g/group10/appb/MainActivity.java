package se.miun.dt002g.group10.appb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  private static final String SHARED_PREF_APP_B = "sharedPrefAppB";
  private static final String LOG_LIST_KEY_APP_B = "logListKeyAppB";

  private RecyclerView.Adapter mAdapter;
  private ArrayList<LogItem> logItemList;
  private String intentMsg = null;
  private SimpleDateFormat sdf;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Get the current system time
    String currentTime = sdf.format(new Date());

    try {
      intentMsg = this.getIntent().getExtras().getString(Intent.EXTRA_TEXT);
      logItemList.add(new LogItem(currentTime, getResources().getString(R.string.received) + intentMsg));
    } catch (NullPointerException e) {
      // No intent received or unable to retrieve content
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveLogListItem();
  }

  private void saveLogListItem() {
    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_APP_B, MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    Gson gson = new Gson();

    // Store logListItem in json format
    String json = gson.toJson(logItemList);

    // Add json to shared preferences
    editor.putString(LOG_LIST_KEY_APP_B, json);

    editor.apply();
  }

  private void loadLogListItem() {
    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_APP_B, MODE_PRIVATE);
    Gson gson = new Gson();
    String json = sharedPreferences.getString(LOG_LIST_KEY_APP_B, null);

    // Specify that Gson should convert json to ArrayList of LogItems
    Type type = new TypeToken<ArrayList<LogItem>>() {}.getType();
    logItemList = gson.fromJson(json, type);

    if (logItemList == null) {
      logItemList = new ArrayList<>();
    }
  }

  private void addListenerButton() {
    Button intentButton = findViewById(R.id.intentButton);

    intentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        // Do nothing if no intent is received
        if (intentMsg == null) {
          return;
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
    mAdapter.notifyDataSetChanged();
  }

}