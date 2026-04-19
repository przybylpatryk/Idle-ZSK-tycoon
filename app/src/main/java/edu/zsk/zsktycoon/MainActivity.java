package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gm = GameView.getInstance();

        TextView avenidaText  = findViewById(R.id.avenida_text);
        TextView schoolText   = findViewById(R.id.school_text);
        ProgressBar tramProgress = findViewById(R.id.tram_progress);
        TextView tramStatus   = findViewById(R.id.tram_status);

        ProgressBar trainProgress1 = findViewById(R.id.train_progress);
        TextView trainStatus1      = findViewById(R.id.train_status);
        ProgressBar trainProgress2 = findViewById(R.id.train_progress2);
        TextView trainStatus2      = findViewById(R.id.train_status2);
        ProgressBar trainProgress3 = findViewById(R.id.train_progress3);
        TextView trainStatus3      = findViewById(R.id.train_status3);

        Button newTrainBtn = findViewById(R.id.new_train_btn);
        Button upgradeBtn  = findViewById(R.id.upgrade_btn);

        gm.avenidaStudents.observe(this, val -> avenidaText.setText(String.valueOf(val)));
        gm.schoolStudents.observe(this,  val -> schoolText.setText(String.valueOf(val)));

        gm.tramProgress.observe(this, tramProgress::setProgress);
        gm.tramStatus.observe(this,   tramStatus::setText);

        gm.train1Progress.observe(this, trainProgress1::setProgress);
        gm.train1Status.observe(this,   trainStatus1::setText);

        gm.train2Progress.observe(this, trainProgress2::setProgress);
        gm.train2Status.observe(this,   trainStatus2::setText);
        gm.train2Owned.observe(this, owned -> {
            int v = owned ? View.VISIBLE : View.GONE;
            trainProgress2.setVisibility(v);
            trainStatus2.setVisibility(v);
        });

        gm.train3Progress.observe(this, trainProgress3::setProgress);
        gm.train3Status.observe(this,   trainStatus3::setText);
        gm.train3Owned.observe(this, owned -> {
            int v = owned ? View.VISIBLE : View.GONE;
            trainProgress3.setVisibility(v);
            trainStatus3.setVisibility(v);
        });

        gm.nextTrainCost.observe(this, cost ->
                newTrainBtn.setText("Kup nową linię (" + cost + " uczniów w szkole)")
        );

        gm.ownedTrainsCount.observe(this, count -> {
            if (count >= 3) {
                newTrainBtn.setEnabled(false);
                newTrainBtn.setText("Wszystkie linie kupione");
            } else {
                newTrainBtn.setEnabled(true);
            }
        });

        newTrainBtn.setOnClickListener(v -> gm.buyNewTrain());

        upgradeBtn.setOnClickListener(v ->
                startActivity(new Intent(this, UpgradeActivity.class))
        );
    }
}