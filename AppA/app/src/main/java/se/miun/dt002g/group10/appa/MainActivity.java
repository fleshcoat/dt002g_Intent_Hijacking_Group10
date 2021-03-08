package se.miun.dt002g.group10.appa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  private RecyclerView.Adapter mAdapter;

  private RadioGroup radioGroup;
  private RadioButton radioButton;
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
    radioGroup = findViewById(R.id.radioGroup);

    intentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);

        // Create the text message from the value of the radiobutton
        Intent implicitIntent = new Intent();
        implicitIntent.setAction(Intent.ACTION_SEND);
        implicitIntent.putExtra(Intent.EXTRA_TEXT, radioButton.getText());
        implicitIntent.setType("text/plain");

        // Get the current system time
        String currentTime = sdf.format(new Date());

        logItemList.add(new LogItem(currentTime, (String) radioButton.getText()));

        // Update the view
        mAdapter.notifyDataSetChanged();

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
}