package edu.zsk.zsktycoon;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;

public class GameView extends ViewModel {

    public MutableLiveData<Long> avenidaStudents = new MutableLiveData<>(0L);
    public MutableLiveData<Long> schoolStudents  = new MutableLiveData<>(0L);

    public MutableLiveData<Integer> tramProgress = new MutableLiveData<>(0);
    public MutableLiveData<String>  tramStatus   = new MutableLiveData<>("Tramwaj czeka");
    public MutableLiveData<Boolean> tramActive   = new MutableLiveData<>(false);

    public MutableLiveData<Integer> trainProgress = new MutableLiveData<>(0);
    public MutableLiveData<String>  trainStatus   = new MutableLiveData<>("Pociąg czeka");
    public MutableLiveData<Boolean> trainActive   = new MutableLiveData<>(false);

    public MutableLiveData<Integer> trainCapacityLevel = new MutableLiveData<>(0);
    public MutableLiveData<Integer> trainSpeedLevel    = new MutableLiveData<>(0);
    public MutableLiveData<Integer> tramCapacityLevel  = new MutableLiveData<>(0);
    public MutableLiveData<Integer> tramSpeedLevel     = new MutableLiveData<>(0);

    private static final int   MAX_LEVEL           = 5;
    private static final long  TRAIN_BASE_CAPACITY = 5;
    private static final long  TRAM_BASE_CAPACITY  = 20;
    private static final long  TRAIN_BASE_INTERVAL = 8000;
    private static final long  TRAM_BASE_INTERVAL  = 5000;
    private static final long  TRAIN_BASE_STEP_MS  = 500;
    private static final long  TRAM_BASE_STEP_MS   = 600;

    private static final long TRAIN_CAPACITY_BASE_COST = 20L;
    private static final long TRAIN_SPEED_BASE_COST    = 30L;
    private static final long TRAM_CAPACITY_BASE_COST  = 15L;
    private static final long TRAM_SPEED_BASE_COST     = 25L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable trainLoopRunnable;
    private boolean isTrainRiding = false;
    private boolean isTramRiding = false;

    public GameView() {
        scheduleTrainLoop();
    }

    public long getTrainCapacity() {
        int lvl = safeInt(trainCapacityLevel);
        return TRAIN_BASE_CAPACITY + lvl * 3L;
    }

    public long getTramCapacity() {
        int lvl = safeInt(tramCapacityLevel);
        return TRAM_BASE_CAPACITY + lvl * 10L;
    }

    public long getTrainInterval() {
        int lvl = safeInt(trainSpeedLevel);
        return Math.max(2000, TRAIN_BASE_INTERVAL - lvl * 1000L);
    }

    public long getTrainStepMs() {
        int lvl = safeInt(trainSpeedLevel);
        return Math.max(100, TRAIN_BASE_STEP_MS - lvl * 80L);
    }

    public long getTramInterval() {
        int lvl = safeInt(tramSpeedLevel);
        return Math.max(1500, TRAM_BASE_INTERVAL - lvl * 600L);
    }

    public long getTramStepMs() {
        int lvl = safeInt(tramSpeedLevel);
        return Math.max(100, TRAM_BASE_STEP_MS - lvl * 90L);
    }

    public long getTrainCapacityCost() { return upgradeCost(TRAIN_CAPACITY_BASE_COST, safeInt(trainCapacityLevel)); }
    public long getTrainSpeedCost()    { return upgradeCost(TRAIN_SPEED_BASE_COST,    safeInt(trainSpeedLevel)); }
    public long getTramCapacityCost()  { return upgradeCost(TRAM_CAPACITY_BASE_COST,  safeInt(tramCapacityLevel)); }
    public long getTramSpeedCost()     { return upgradeCost(TRAM_SPEED_BASE_COST,     safeInt(tramSpeedLevel)); }

    private long upgradeCost(long base, int currentLevel) {
        return base * (currentLevel + 1);
    }

    public boolean upgradeTrainCapacity() { return doUpgrade(trainCapacityLevel, getTrainCapacityCost()); }
    public boolean upgradeTrainSpeed()    { return doUpgrade(trainSpeedLevel,    getTrainSpeedCost()); }
    public boolean upgradeTramCapacity()  { return doUpgrade(tramCapacityLevel,  getTramCapacityCost()); }
    public boolean upgradeTramSpeed()     { return doUpgrade(tramSpeedLevel,     getTramSpeedCost()); }

    private boolean doUpgrade(MutableLiveData<Integer> levelLive, long cost) {
        int lvl = safeInt(levelLive);
        if (lvl >= MAX_LEVEL) return false;

        Long inSchool = schoolStudents.getValue();
        if (inSchool == null || inSchool < cost) return false;

        schoolStudents.setValue(inSchool - cost);
        levelLive.setValue(lvl + 1);

        rescheduleLoops();
        return true;
    }

    public int getMaxLevel() { return MAX_LEVEL; }

    private void scheduleTrainLoop() {
        handler.removeCallbacks(trainLoopRunnable != null ? trainLoopRunnable : () -> {});
        trainLoopRunnable = new Runnable() {
            @Override
            public void run() {
                startTrainRide();
                handler.postDelayed(this, getTrainInterval());
            }
        };
        handler.postDelayed(trainLoopRunnable, getTrainInterval());
    }

    private void startTrainRide() {
        if (isTrainRiding || Boolean.TRUE.equals(trainActive.getValue())) return;

        isTrainRiding = true;
        trainActive.setValue(true);
        trainStatus.setValue("Pociąg jedzie...");
        trainProgress.setValue(0);

        final long stepMs = getTrainStepMs();
        final int totalSteps = 100 / 10;
        final int[] stepCount = {0};

        Runnable driveRunnable = new Runnable() {
            @Override
            public void run() {
                stepCount[0]++;
                trainProgress.setValue(stepCount[0] * 10);

                if (stepCount[0] >= totalSteps) {
                    finishTrainRide();
                } else {
                    handler.postDelayed(this, stepMs);
                }
            }
        };
        handler.postDelayed(driveRunnable, stepMs);
    }

    private void finishTrainRide() {
        long capacity = getTrainCapacity();
        Long current = avenidaStudents.getValue();
        if (current != null) {
            avenidaStudents.setValue(current + capacity);
        }

        isTrainRiding = false;
        trainActive.setValue(false);
        trainStatus.setValue("Pociąg przywiózł " + capacity + " osób");
        trainProgress.setValue(0);

        handler.postDelayed(() -> startTramRide(), 1000);
    }

    private void startTramRide() {
        if (isTramRiding || Boolean.TRUE.equals(tramActive.getValue())) return;

        Long onPlatform = avenidaStudents.getValue();
        if (onPlatform == null || onPlatform == 0) {
            tramStatus.setValue("Brak pasażerów na przystanku");
            return;
        }

        isTramRiding = true;
        tramActive.setValue(true);
        tramStatus.setValue("Tramwaj jedzie...");
        tramProgress.setValue(0);

        final long stepMs = getTramStepMs();
        final int totalSteps = 100 / 20;
        final int[] stepCount = {0};

        Runnable driveRunnable = new Runnable() {
            @Override
            public void run() {
                stepCount[0]++;
                tramProgress.setValue(stepCount[0] * 20);

                if (stepCount[0] >= totalSteps) {
                    finishTramRide();
                } else {
                    handler.postDelayed(this, stepMs);
                }
            }
        };
        handler.postDelayed(driveRunnable, stepMs);
    }

    private void finishTramRide() {
        Long onPlatform = avenidaStudents.getValue();
        Long inSchool = schoolStudents.getValue();
        long canTake = getTramCapacity();

        if (onPlatform != null && inSchool != null && onPlatform > 0) {
            long willTake = Math.min(onPlatform, canTake);
            avenidaStudents.setValue(onPlatform - willTake);
            schoolStudents.setValue(inSchool + willTake);
            tramStatus.setValue("Tramwaj przywiózł " + willTake + " uczniów do szkoły");
        } else {
            tramStatus.setValue("Tramwaj czeka");
        }

        isTramRiding = false;
        tramActive.setValue(false);
        tramProgress.setValue(0);
    }

    private void rescheduleLoops() {
        handler.removeCallbacks(trainLoopRunnable);
        scheduleTrainLoop();
    }

    private int safeInt(MutableLiveData<Integer> live) {
        Integer v = live.getValue();
        return v != null ? v : 0;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
        isTrainRiding = false;
        isTramRiding = false;
    }
}