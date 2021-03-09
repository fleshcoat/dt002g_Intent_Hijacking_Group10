package se.miun.dt002g.group10.appb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  private RecyclerView.Adapter mAdapter;
  private ArrayList<LogItem> logItemList;
  private String intentMsg = null;
  private SimpleDateFormat sdf;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Set time format
    sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    addListenerButton();

    logItemList = new ArrayList<>();

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
      logItemList.add(new LogItem(currentTime, intentMsg));
    } catch (NullPointerException e) {
      // No intent received or unable to retrieve content
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

        logItemList.add(new LogItem(currentTime, intentMsg));

        // Update the view
        mAdapter.notifyDataSetChanged();

        // Create the text message from the value of the radiobutton
        Intent implicitIntent = new Intent();
        implicitIntent.setAction(Intent.ACTION_SEND);
        implicitIntent.putExtra(Intent.EXTRA_TEXT, intentMsg);
        implicitIntent.setType("text/plain");

        // Try to send implicit intent
        try {
          startActivity(implicitIntent);
        } catch (ActivityNotFoundException e) {
          // Temporary toast
          Toast.makeText(getApplicationContext(), "Failed to start activity!", Toast.LENGTH_SHORT).show();
        }
      }
    });
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
    Toast.makeText(getApplicationContext(), R.string.clear_list, Toast.LENGTH_SHORT).show();
    mAdapter.notifyDataSetChanged();
  }

}