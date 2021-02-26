package se.miun.dt002g.group10.appa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  private Button intentButton;
  private RadioGroup radioGroup;
  private RadioButton radioButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    addListenerButton();
  }

  private void addListenerButton() {
    intentButton = findViewById(R.id.intentButton);
    radioGroup = findViewById(R.id.radioGroup);

    intentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);

        Toast.makeText(getApplicationContext(), radioButton.getText(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}