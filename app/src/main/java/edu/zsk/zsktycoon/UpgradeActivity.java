package edu.zsk.zsktycoon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class UpgradeActivity extends AppCompatActivity {

    private GameView gm;

    private Button btnTrainCapacity, btnTrainSpeed;
    private Button btnTramCapacity,  btnTramSpeed;

    private TextView tvTrainCapacityLevel, tvTrainSpeedLevel;
    private TextView tvTramCapacityLevel,  tvTramSpeedLevel;

    private TextView tvTrainCapacityCost, tvTrainSpeedCost;
    private TextView tvTramCapacityCost,  tvTramSpeedCost;

    private TextView tvSchoolBalance;

    private TextView tvTrainCapacityVal, tvTrainSpeedVal;
    private TextView tvTramCapacityVal,  tvTramSpeedVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);


        gm = App.getGameView();

        bindViews();
        setupObservers();
        setupButtons();
    }

    private void bindViews() {
        tvSchoolBalance = findViewById(R.id.tv_school_balance);

        tvTrainCapacityLevel = findViewById(R.id.tv_train_capacity_level);
        tvTrainSpeedLevel    = findViewById(R.id.tv_train_speed_level);
        tvTramCapacityLevel  = findViewById(R.id.tv_tram_capacity_level);
        tvTramSpeedLevel     = findViewById(R.id.tv_tram_speed_level);

        tvTrainCapacityCost  = findViewById(R.id.tv_train_capacity_cost);
        tvTrainSpeedCost     = findViewById(R.id.tv_train_speed_cost);
        tvTramCapacityCost   = findViewById(R.id.tv_tram_capacity_cost);
        tvTramSpeedCost      = findViewById(R.id.tv_tram_speed_cost);

        tvTrainCapacityVal   = findViewById(R.id.tv_train_capacity_val);
        tvTrainSpeedVal      = findViewById(R.id.tv_train_speed_val);
        tvTramCapacityVal    = findViewById(R.id.tv_tram_capacity_val);
        tvTramSpeedVal       = findViewById(R.id.tv_tram_speed_val);

        btnTrainCapacity     = findViewById(R.id.btn_upgrade_train_capacity);
        btnTrainSpeed        = findViewById(R.id.btn_upgrade_train_speed);
        btnTramCapacity      = findViewById(R.id.btn_upgrade_tram_capacity);
        btnTramSpeed         = findViewById(R.id.btn_upgrade_tram_speed);
    }

    private void setupObservers() {
        int max = gm.getMaxLevel();

        gm.schoolStudents.observe(this, val -> {
            if (val != null) {
                tvSchoolBalance.setText("Uczniowie w szkole: " + val);
                refreshButtonStates();
            }
        });

        gm.trainCapacityLevel.observe(this, lvl -> {
            if (lvl != null) {
                tvTrainCapacityLevel.setText("Poziom: " + lvl + "/" + max);
                tvTrainCapacityCost.setText(lvl >= max ? "MAX" : "Koszt: " + gm.getTrainCapacityCost());
                tvTrainCapacityVal.setText("Ładowność: " + gm.getTrainCapacity() + " os.");
                refreshButtonStates();
            }
        });

        gm.trainSpeedLevel.observe(this, lvl -> {
            if (lvl != null) {
                tvTrainSpeedLevel.setText("Poziom: " + lvl + "/" + max);
                tvTrainSpeedCost.setText(lvl >= max ? "MAX" : "Koszt: " + gm.getTrainSpeedCost());
                tvTrainSpeedVal.setText("Interwał: " + (gm.getTrainInterval() / 1000) + "s");
                refreshButtonStates();
            }
        });

        gm.tramCapacityLevel.observe(this, lvl -> {
            if (lvl != null) {
                tvTramCapacityLevel.setText("Poziom: " + lvl + "/" + max);
                tvTramCapacityCost.setText(lvl >= max ? "MAX" : "Koszt: " + gm.getTramCapacityCost());
                tvTramCapacityVal.setText("Ładowność: " + gm.getTramCapacity() + " os.");
                refreshButtonStates();
            }
        });

        gm.tramSpeedLevel.observe(this, lvl -> {
            if (lvl != null) {
                tvTramSpeedLevel.setText("Poziom: " + lvl + "/" + max);
                tvTramSpeedCost.setText(lvl >= max ? "MAX" : "Koszt: " + gm.getTramSpeedCost());
                tvTramSpeedVal.setText("Interwał: " + (gm.getTramInterval() / 1000) + "s");
                refreshButtonStates();
            }
        });
    }

    private void setupButtons() {
        btnTrainCapacity.setOnClickListener(v -> {
            boolean ok = gm.upgradeTrainCapacity();
            if (!ok) showError();
        });
        btnTrainSpeed.setOnClickListener(v -> {
            boolean ok = gm.upgradeTrainSpeed();
            if (!ok) showError();
        });
        btnTramCapacity.setOnClickListener(v -> {
            boolean ok = gm.upgradeTramCapacity();
            if (!ok) showError();
        });
        btnTramSpeed.setOnClickListener(v -> {
            boolean ok = gm.upgradeTramSpeed();
            if (!ok) showError();
        });
    }

    private void refreshButtonStates() {
        Long balance = gm.schoolStudents.getValue();
        if (balance == null) balance = 0L;

        int max = gm.getMaxLevel();

        setButtonEnabled(btnTrainCapacity,
                safeInt(gm.trainCapacityLevel) < max && balance >= gm.getTrainCapacityCost());
        setButtonEnabled(btnTrainSpeed,
                safeInt(gm.trainSpeedLevel) < max && balance >= gm.getTrainSpeedCost());
        setButtonEnabled(btnTramCapacity,
                safeInt(gm.tramCapacityLevel) < max && balance >= gm.getTramCapacityCost());
        setButtonEnabled(btnTramSpeed,
                safeInt(gm.tramSpeedLevel) < max && balance >= gm.getTramSpeedCost());
    }

    private void setButtonEnabled(Button btn, boolean enabled) {
        btn.setEnabled(enabled);
        btn.setAlpha(enabled ? 1f : 0.4f);
    }

    private void showError() {
        Toast.makeText(this,
                "Za mało uczniów w szkole lub osiągnięto MAX poziom!",
                Toast.LENGTH_SHORT).show();
    }

    private int safeInt(androidx.lifecycle.MutableLiveData<Integer> live) {
        Integer v = live.getValue();
        return v != null ? v : 0;
    }
}