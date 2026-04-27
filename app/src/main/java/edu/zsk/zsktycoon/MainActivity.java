package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        TextView teacherText  = findViewById(R.id.teacher_text);

        ProgressBar tramProgress   = findViewById(R.id.tram_progress);
        TextView    tramStatus     = findViewById(R.id.tram_status);
        ImageView   tramIcon       = findViewById(R.id.tram_progress_icon);

        ProgressBar trainProgress1 = findViewById(R.id.train_progress);
        TextView    trainStatus1   = findViewById(R.id.train_status);
        ImageView   train1Icon     = findViewById(R.id.train1_progress_icon);

        ProgressBar trainProgress2 = findViewById(R.id.train_progress2);
        TextView    trainStatus2   = findViewById(R.id.train_status2);

        ProgressBar trainProgress3 = findViewById(R.id.train_progress3);
        TextView    trainStatus3   = findViewById(R.id.train_status3);

        LinearLayout train2Card    = findViewById(R.id.train2_card);
        LinearLayout train3Card    = findViewById(R.id.train3_card);

        Button newTrainBtn     = findViewById(R.id.new_train_btn);
        Button tramUpgradeBtn  = findViewById(R.id.tram_upgrade_btn);
        Button train1UpgradeBtn = findViewById(R.id.train1_upgrade_btn);
        Button train2UpgradeBtn = findViewById(R.id.train2_upgrade_btn);
        Button train3UpgradeBtn = findViewById(R.id.train3_upgrade_btn);

        // --- Dane ---
        gm.avenidaStudents.observe(this, val -> avenidaText.setText(String.valueOf(val)));
        gm.schoolStudents .observe(this, val -> schoolText .setText(String.valueOf(val)));
        gm.teachers       .observe(this, val -> teacherText.setText("Nauczyciele: " + val));

        // --- Tramwaj ---
        gm.tramProgress.observe(this, val -> {
            tramProgress.setProgress(val);
            moveIconWithProgress(tramProgress, tramIcon, val);
        });
        gm.tramStatus.observe(this, tramStatus::setText);

        // --- Pociąg 1 ---
        gm.train1Progress.observe(this, val -> {
            trainProgress1.setProgress(val);
            moveIconWithProgress(trainProgress1, train1Icon, val);
        });
        gm.train1Status.observe(this, trainStatus1::setText);

        // --- Pociąg 2 ---
        gm.train2Progress.observe(this, trainProgress2::setProgress);
        gm.train2Status  .observe(this, trainStatus2::setText);

        // --- Pociąg 3 ---
        gm.train3Progress.observe(this, trainProgress3::setProgress);
        gm.train3Status  .observe(this, trainStatus3::setText);

        // Widoczność kart
        gm.train2Owned.observe(this, owned ->
                train2Card.setVisibility(owned ? View.VISIBLE : View.GONE));
        gm.train3Owned.observe(this, owned ->
                train3Card.setVisibility(owned ? View.VISIBLE : View.GONE));

        // --- Kupno nowej linii ---
        gm.nextTrainCost  .observe(this, cost ->
                newTrainBtn.setText("🚆 Kup nową linię (" + cost + " uczniów)"));
        gm.ownedTrainsCount.observe(this, cnt -> {
            newTrainBtn.setEnabled(cnt < 3);
            if (cnt >= 3) newTrainBtn.setText("Wszystkie linie kupione ✓");
        });
        newTrainBtn.setOnClickListener(v -> gm.buyNewTrain());

        tramUpgradeBtn .setOnClickListener(v -> startActivity(new Intent(this, TramUpgradeActivity.class)));

        train1UpgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainUpgradeActivity.class);
            intent.putExtra("nbr", 1);
            startActivity(intent);
        });

        train2UpgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainUpgradeActivity.class);
            intent.putExtra("nbr", 2);
            startActivity(intent);
        });

        train3UpgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainUpgradeActivity.class);
            intent.putExtra("nbr", 3);
            startActivity(intent);
        });
        newTrainBtn.setOnClickListener(v -> gm.buyNewTrain());

        tramUpgradeBtn.setOnClickListener(v ->
                startActivity(new Intent(this, TramUpgradeActivity.class)));
        trainUpgradeBtn.setOnClickListener(v ->
                startActivity(new Intent(this, TrainUpgradeActivity.class)));
    }

    /**
     * Przesuwa ikonę (tramwaj/pociąg) wzdłuż paska postępu.
     *
     * Jak dodać własny obrazek:
     * 1. Skopiuj plik tram.png / train.png do res/drawable/
     * 2. W activity_main.xml zmień android:src na @drawable/tram lub @drawable/train
     *
     * @param bar      ProgressBar po którym jeździ ikona
     * @param icon     ImageView z ikonką pojazdu
     * @param progress wartość 0-100
     */
    private void moveIconWithProgress(ProgressBar bar, ImageView icon, int progress) {
        if (icon == null) return;

        // Czekaj aż widok będzie zmierzony
        if (bar.getWidth() == 0) {
            bar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    bar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    applyIconPosition(bar, icon, progress);
                }
            });
        } else {
            applyIconPosition(bar, icon, progress);
        }
    }

    private void applyIconPosition(ProgressBar bar, ImageView icon, int progress) {
        float ratio    = progress / 100f;
        float barWidth = bar.getWidth();
        float iconW    = icon.getWidth() > 0 ? icon.getWidth() : 56f;

        // Pozycja X ikony: od lewego brzegu paska do miejsca odpowiadającego progress
        float x = bar.getX() + ratio * (barWidth - iconW);
        icon.setX(Math.max(bar.getX(), x));
    }
}
