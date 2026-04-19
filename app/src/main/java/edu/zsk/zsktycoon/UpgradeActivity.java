package edu.zsk.zsktycoon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpgradeActivity extends AppCompatActivity {

    private GameView gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        gm = GameView.getInstance();

        TextView schoolCount = findViewById(R.id.upgrade_school_count);

        TextView tramCapLvl  = findViewById(R.id.tram_cap_level);
        TextView tramCapCost = findViewById(R.id.tram_cap_cost);
        Button   tramCapBtn  = findViewById(R.id.tram_cap_btn);
        TextView tramSpdLvl  = findViewById(R.id.tram_spd_level);
        TextView tramSpdCost = findViewById(R.id.tram_spd_cost);
        Button   tramSpdBtn  = findViewById(R.id.tram_spd_btn);

        TextView t1CapLvl  = findViewById(R.id.t1_cap_level);
        TextView t1CapCost = findViewById(R.id.t1_cap_cost);
        Button   t1CapBtn  = findViewById(R.id.t1_cap_btn);
        TextView t1SpdLvl  = findViewById(R.id.t1_spd_level);
        TextView t1SpdCost = findViewById(R.id.t1_spd_cost);
        Button   t1SpdBtn  = findViewById(R.id.t1_spd_btn);

        TextView t2CapLvl  = findViewById(R.id.t2_cap_level);
        TextView t2CapCost = findViewById(R.id.t2_cap_cost);
        Button   t2CapBtn  = findViewById(R.id.t2_cap_btn);
        TextView t2SpdLvl  = findViewById(R.id.t2_spd_level);
        TextView t2SpdCost = findViewById(R.id.t2_spd_cost);
        Button   t2SpdBtn  = findViewById(R.id.t2_spd_btn);

        TextView t3CapLvl  = findViewById(R.id.t3_cap_level);
        TextView t3CapCost = findViewById(R.id.t3_cap_cost);
        Button   t3CapBtn  = findViewById(R.id.t3_cap_btn);
        TextView t3SpdLvl  = findViewById(R.id.t3_spd_level);
        TextView t3SpdCost = findViewById(R.id.t3_spd_cost);
        Button   t3SpdBtn  = findViewById(R.id.t3_spd_btn);

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());


        gm.schoolStudents.observe(this, val -> {
            schoolCount.setText("Uczniowie w szkole: " + val);
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.tramCapacityLevel.observe(this, lvl -> {
            tramCapLvl.setText("Poziom: " + lvl + " / 5  (pojemność: " + gm.getTramCapacity() + " osób)");
            tramCapCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.tramSpeedLevel.observe(this, lvl -> {
            tramSpdLvl.setText("Poziom: " + lvl + " / 5  (krok: " + gm.getTramStepMs() + " ms)");
            tramSpdCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train1CapacityLevel.observe(this, lvl -> {
            t1CapLvl.setText("Poziom: " + lvl + " / 5  (" + gm.getTrain1Passengers() + " osób/kurs)");
            t1CapCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train1SpeedLevel.observe(this, lvl -> {
            t1SpdLvl.setText("Poziom: " + lvl + " / 5  (krok: " + gm.getTrain1StepMs() + " ms)");
            t1SpdCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train2CapacityLevel.observe(this, lvl -> {
            t2CapLvl.setText("Poziom: " + lvl + " / 5  (" + gm.getTrain2Passengers() + " osób/kurs)");
            t2CapCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train2SpeedLevel.observe(this, lvl -> {
            t2SpdLvl.setText("Poziom: " + lvl + " / 5  (krok: " + gm.getTrain2StepMs() + " ms)");
            t2SpdCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train3CapacityLevel.observe(this, lvl -> {
            t3CapLvl.setText("Poziom: " + lvl + " / 5  (" + gm.getTrain3Passengers() + " osób/kurs)");
            t3CapCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train3SpeedLevel.observe(this, lvl -> {
            t3SpdLvl.setText("Poziom: " + lvl + " / 5  (krok: " + gm.getTrain3StepMs() + " ms)");
            t3SpdCost.setText(lvl >= 5 ? "MAKS" : "Koszt: " + gm.getUpgradeCost(lvl) + " uczniów");
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train2Owned.observe(this, owned -> {
            findViewById(R.id.section_train2).setAlpha(owned ? 1f : 0.4f);
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        gm.train3Owned.observe(this, owned -> {
            findViewById(R.id.section_train3).setAlpha(owned ? 1f : 0.4f);
            refreshAll(tramCapBtn, tramSpdBtn, t1CapBtn, t1SpdBtn, t2CapBtn, t2SpdBtn, t3CapBtn, t3SpdBtn);
        });

        tramCapBtn.setOnClickListener(v -> { if (!gm.upgradeTramCapacity())   toast(); });
        tramSpdBtn.setOnClickListener(v -> { if (!gm.upgradeTramSpeed())      toast(); });
        t1CapBtn.setOnClickListener(v ->   { if (!gm.upgradeTrain1Capacity()) toast(); });
        t1SpdBtn.setOnClickListener(v ->   { if (!gm.upgradeTrain1Speed())    toast(); });
        t2CapBtn.setOnClickListener(v ->   { if (!gm.upgradeTrain2Capacity()) toast(); });
        t2SpdBtn.setOnClickListener(v ->   { if (!gm.upgradeTrain2Speed())    toast(); });
        t3CapBtn.setOnClickListener(v ->   { if (!gm.upgradeTrain3Capacity()) toast(); });
        t3SpdBtn.setOnClickListener(v ->   { if (!gm.upgradeTrain3Speed())    toast(); });
    }

    private void toast() {
        Toast.makeText(this, "Za mało uczniów w szkole!", Toast.LENGTH_SHORT).show();
    }

    private void refreshAll(Button... btns) {
        Long school = gm.schoolStudents.getValue();
        if (school == null) return;
        checkBtn(btns[0], gm.tramCapacityLevel.getValue(),   school, null);
        checkBtn(btns[1], gm.tramSpeedLevel.getValue(),      school, null);
        checkBtn(btns[2], gm.train1CapacityLevel.getValue(), school, null);
        checkBtn(btns[3], gm.train1SpeedLevel.getValue(),    school, null);
        checkBtn(btns[4], gm.train2CapacityLevel.getValue(), school, gm.train2Owned.getValue());
        checkBtn(btns[5], gm.train2SpeedLevel.getValue(),    school, gm.train2Owned.getValue());
        checkBtn(btns[6], gm.train3CapacityLevel.getValue(), school, gm.train3Owned.getValue());
        checkBtn(btns[7], gm.train3SpeedLevel.getValue(),    school, gm.train3Owned.getValue());
    }

    private void checkBtn(Button btn, Integer level, long school, Boolean owned) {
        if (level == null) return;
        boolean maxed    = level >= 5;
        boolean notOwned = owned != null && !owned;
        btn.setEnabled(!maxed && !notOwned && school >= gm.getUpgradeCost(level));
    }
}