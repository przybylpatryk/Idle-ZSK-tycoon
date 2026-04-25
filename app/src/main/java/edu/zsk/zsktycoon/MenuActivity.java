package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.btn_play).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btn_tram_upgrade).setOnClickListener(v -> startActivity(new Intent(this, TramUpgradeActivity.class)));
        findViewById(R.id.btn_train_upgrade).setOnClickListener(v -> startActivity(new Intent(this, TrainUpgradeActivity.class)));
        findViewById(R.id.btn_exit).setOnClickListener(v -> { finish(); System.exit(0);});
    }
}