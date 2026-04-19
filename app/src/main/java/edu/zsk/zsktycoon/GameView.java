package edu.zsk.zsktycoon;

import androidx.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;

public class GameView {

    private static GameView instance;

    public static GameView getInstance() {
        if (instance == null) {
            instance = new GameView();
        }
        return instance;
    }

    public MutableLiveData<Long> avenidaStudents = new MutableLiveData<>(0L);
    public MutableLiveData<Long> schoolStudents = new MutableLiveData<>(0L);
    public MutableLiveData<Integer> tramProgress = new MutableLiveData<>(0);
    public MutableLiveData<String> tramStatus = new MutableLiveData<>("Tramwaj czeka");
    public MutableLiveData<Boolean> tramActive = new MutableLiveData<>(false);

    public MutableLiveData<Integer> train1Progress = new MutableLiveData<>(0);
    public MutableLiveData<String> train1Status = new MutableLiveData<>("Linia Pobiedziska czeka");
    public MutableLiveData<Boolean> train1Active = new MutableLiveData<>(false);

    public MutableLiveData<Integer> train2Progress = new MutableLiveData<>(0);
    public MutableLiveData<String> train2Status = new MutableLiveData<>("Linia Puszczykowo (zablokowana)");
    public MutableLiveData<Boolean> train2Active = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> train2Owned = new MutableLiveData<>(false);

    public MutableLiveData<Integer> train3Progress = new MutableLiveData<>(0);
    public MutableLiveData<String> train3Status = new MutableLiveData<>("Linia Opalenica (zablokowana)");
    public MutableLiveData<Boolean> train3Active = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> train3Owned = new MutableLiveData<>(false);

    public MutableLiveData<Long> nextTrainCost = new MutableLiveData<>(50L);
    public MutableLiveData<Integer> ownedTrainsCount = new MutableLiveData<>(1);

    public MutableLiveData<Integer> tramCapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> tramSpeedLevel    = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train1CapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train1SpeedLevel    = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train2CapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train2SpeedLevel    = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train3CapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train3SpeedLevel    = new MutableLiveData<>(1);

    private static final int  MAX_UPGRADE_LEVEL = 5;
    private static final long UPGRADE_BASE_COST = 20L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Runnable train1LoopRunnable;
    private Runnable train2LoopRunnable;
    private Runnable train3LoopRunnable;
    private Runnable tramLoopRunnable;

    private long tramPassengers = 0;

    private GameView() {
        startTrain1Loop();
        startTramLoop();
    }

    public int getTramCapacity() {
        Integer lvl = tramCapacityLevel.getValue();
        return 20 + (lvl != null ? (lvl - 1) * 10 : 0);
    }

    public int getTramStepMs() {
        Integer lvl = tramSpeedLevel.getValue();
        return Math.max(400 - (lvl != null ? (lvl - 1) * 40 : 0), 80);
    }

    public int getTrain1Passengers() {
        Integer lvl = train1CapacityLevel.getValue();
        return 4 + (lvl != null ? (lvl - 1) * 2 : 0);
    }

    public int getTrain1StepMs() {
        Integer lvl = train1SpeedLevel.getValue();
        return Math.max(500 - (lvl != null ? (lvl - 1) * 50 : 0), 100);
    }

    public int getTrain2Passengers() {
        Integer lvl = train2CapacityLevel.getValue();
        return 7 + (lvl != null ? (lvl - 1) * 3 : 0);
    }

    public int getTrain2StepMs() {
        Integer lvl = train2SpeedLevel.getValue();
        return Math.max(700 - (lvl != null ? (lvl - 1) * 70 : 0), 140);
    }

    public int getTrain3Passengers() {
        Integer lvl = train3CapacityLevel.getValue();
        return 11 + (lvl != null ? (lvl - 1) * 4 : 0);
    }

    public int getTrain3StepMs() {
        Integer lvl = train3SpeedLevel.getValue();
        return Math.max(1000 - (lvl != null ? (lvl - 1) * 100 : 0), 200);
    }

    public long getUpgradeCost(int currentLevel) {
        return UPGRADE_BASE_COST * currentLevel;
    }

    public boolean upgradeTramCapacity()    { return doUpgrade(tramCapacityLevel); }
    public boolean upgradeTramSpeed()       { return doUpgrade(tramSpeedLevel); }
    public boolean upgradeTrain1Capacity()  { return doUpgrade(train1CapacityLevel); }
    public boolean upgradeTrain1Speed()     { return doUpgrade(train1SpeedLevel); }
    public boolean upgradeTrain2Capacity()  { return doUpgrade(train2CapacityLevel); }
    public boolean upgradeTrain2Speed()     { return doUpgrade(train2SpeedLevel); }
    public boolean upgradeTrain3Capacity()  { return doUpgrade(train3CapacityLevel); }
    public boolean upgradeTrain3Speed()     { return doUpgrade(train3SpeedLevel); }

    private boolean doUpgrade(MutableLiveData<Integer> levelLiveData) {
        Integer currentLevel = levelLiveData.getValue();
        Long school = schoolStudents.getValue();
        if (currentLevel == null || school == null) return false;
        if (currentLevel >= MAX_UPGRADE_LEVEL) return false;
        long cost = getUpgradeCost(currentLevel);
        if (school < cost) return false;
        schoolStudents.setValue(school - cost);
        levelLiveData.setValue(currentLevel + 1);
        return true;
    }

    private void startTramLoop() {
        tramLoopRunnable = new Runnable() {
            @Override public void run() {
                startTramRide();
                handler.postDelayed(this, 6000);
            }
        };
        handler.postDelayed(tramLoopRunnable, 2000);
    }

    private void startTramRide() {
        if (Boolean.TRUE.equals(tramActive.getValue())) return;
        Long onPlatform = avenidaStudents.getValue();
        if (onPlatform == null || onPlatform == 0) return;

        long willTake = Math.min(onPlatform, getTramCapacity());
        tramPassengers = willTake;
        avenidaStudents.setValue(onPlatform - willTake);
        tramActive.setValue(true);
        tramStatus.setValue("Tramwaj jedzie z " + willTake + " osobami");
        tramProgress.setValue(0);

        final int stepMs = getTramStepMs();
        handler.postDelayed(new Runnable() {
            int progress = 0;
            @Override public void run() {
                progress += 10;
                tramProgress.setValue(progress);
                if (progress >= 100) finishTramRide();
                else handler.postDelayed(this, stepMs);
            }
        }, stepMs);
    }

    private void finishTramRide() {
        Long inSchool = schoolStudents.getValue();
        if (inSchool != null && tramPassengers > 0)
            schoolStudents.setValue(inSchool + tramPassengers);
        tramActive.setValue(false);
        tramStatus.setValue("Tramwaj czeka (przewiózł " + tramPassengers + ")");
        tramProgress.setValue(0);
        tramPassengers = 0;
    }

    private void startTrain1Loop() {
        train1LoopRunnable = new Runnable() {
            @Override public void run() {
                startTrain1Ride();
                handler.postDelayed(this, 6000);
            }
        };
        handler.postDelayed(train1LoopRunnable, 3000);
    }

    private void startTrain1Ride() {
        if (Boolean.TRUE.equals(train1Active.getValue())) return;
        train1Active.setValue(true);
        final int passengers = getTrain1Passengers();
        final int stepMs = getTrain1StepMs();
        train1Status.setValue("Linia Pobiedziska jedzie...");
        train1Progress.setValue(0);
        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override public void run() {
                prog += 10;
                train1Progress.setValue(prog);
                if (prog >= 100) {
                    Long cur = avenidaStudents.getValue();
                    if (cur != null) avenidaStudents.setValue(cur + passengers);
                    train1Active.setValue(false);
                    train1Status.setValue("Z Pobiedzisk przyjechało " + passengers + " osób");
                } else handler.postDelayed(this, stepMs);
            }
        }, stepMs);
    }

    private void startTrain2Loop() {
        train2LoopRunnable = new Runnable() {
            @Override public void run() {
                startTrain2Ride();
                handler.postDelayed(this, 9000);
            }
        };
        handler.post(train2LoopRunnable);
    }

    private void startTrain2Ride() {
        if (Boolean.TRUE.equals(train2Active.getValue())) return;
        train2Active.setValue(true);
        final int passengers = getTrain2Passengers();
        final int stepMs = getTrain2StepMs();
        train2Status.setValue("Linia Puszczykowo jedzie...");
        train2Progress.setValue(0);
        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override public void run() {
                prog += 10;
                train2Progress.setValue(prog);
                if (prog >= 100) {
                    Long cur = avenidaStudents.getValue();
                    if (cur != null) avenidaStudents.setValue(cur + passengers);
                    train2Active.setValue(false);
                    train2Status.setValue("Z Puszczykowa przyjechało " + passengers + " osób");
                } else handler.postDelayed(this, stepMs);
            }
        }, stepMs);
    }

    private void startTrain3Loop() {
        train3LoopRunnable = new Runnable() {
            @Override public void run() {
                startTrain3Ride();
                handler.postDelayed(this, 12000);
            }
        };
        handler.post(train3LoopRunnable);
    }

    private void startTrain3Ride() {
        if (Boolean.TRUE.equals(train3Active.getValue())) return;
        train3Active.setValue(true);
        final int passengers = getTrain3Passengers();
        final int stepMs = getTrain3StepMs();
        train3Status.setValue("Linia Opalenica jedzie...");
        train3Progress.setValue(0);
        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override public void run() {
                prog += 10;
                train3Progress.setValue(prog);
                if (prog >= 100) {
                    Long cur = avenidaStudents.getValue();
                    if (cur != null) avenidaStudents.setValue(cur + passengers);
                    train3Active.setValue(false);
                    train3Status.setValue("Z Opalenicy przyjechało " + passengers + " osób");
                } else handler.postDelayed(this, stepMs);
            }
        }, stepMs);
    }

    public void buyNewTrain() {
        Long school = schoolStudents.getValue();
        Long cost = nextTrainCost.getValue();
        Integer owned = ownedTrainsCount.getValue();
        if (school == null || cost == null || owned == null) return;
        if (school < cost || owned >= 3) return;

        schoolStudents.setValue(school - cost);
        ownedTrainsCount.setValue(owned + 1);

        if (owned == 1) {
            train2Owned.setValue(true);
            train2Status.setValue("Linia Puszczykowo czeka");
            startTrain2Loop();
        } else if (owned == 2) {
            train3Owned.setValue(true);
            train3Status.setValue("Linia Opalenica czeka");
            startTrain3Loop();
        }

        nextTrainCost.setValue((long)(cost * 2.5));
    }
}