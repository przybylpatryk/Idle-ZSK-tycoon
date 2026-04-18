package edu.zsk.zsktycoon;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;

public class GameView extends ViewModel {

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

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable train1LoopRunnable;
    private Runnable train2LoopRunnable;
    private Runnable train3LoopRunnable;
    private Runnable tramLoopRunnable;

    private long tramPassengers = 0;

    public GameView() {
        startTrain1Loop();
        startTramLoop();
    }

    private void startTramLoop() {
        tramLoopRunnable = new Runnable() {
            @Override
            public void run() {
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

        long canTake = 20;
        long willTake = Math.min(onPlatform, canTake);

        tramPassengers = willTake;
        avenidaStudents.setValue(onPlatform - willTake);

        tramActive.setValue(true);
        tramStatus.setValue("Tramwaj jedzie z " + willTake + " osobami");
        tramProgress.setValue(0);

        handler.postDelayed(new Runnable() {
            int progress = 0;
            @Override
            public void run() {
                progress += 10;
                tramProgress.setValue(progress);
                if (progress >= 100) {
                    finishTramRide();
                } else {
                    handler.postDelayed(this, 400);
                }
            }
        }, 400);
    }

    private void finishTramRide() {
        Long inSchool = schoolStudents.getValue();
        if (inSchool != null && tramPassengers > 0) {
            schoolStudents.setValue(inSchool + tramPassengers);
        }

        tramActive.setValue(false);
        tramStatus.setValue("Tramwaj czeka (przewiózł " + tramPassengers + ")");
        tramProgress.setValue(0);
        tramPassengers = 0;
    }

    private void startTrain1Loop() {
        train1LoopRunnable = new Runnable() {
            @Override
            public void run() {
                startTrain1Ride();
                handler.postDelayed(this, 6000);
            }
        };
        handler.postDelayed(train1LoopRunnable, 3000);
    }

    private void startTrain1Ride() {
        if (Boolean.TRUE.equals(train1Active.getValue())) return;
        train1Active.setValue(true);
        train1Status.setValue("Linia Pobiedziska jedzie...");
        train1Progress.setValue(0);

        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override
            public void run() {
                prog += 10;
                train1Progress.setValue(prog);
                if (prog >= 100) {
                    Long cur = avenidaStudents.getValue();
                    if (cur != null) avenidaStudents.setValue(cur + 4);
                    train1Active.setValue(false);
                    train1Status.setValue("Z Pobiedzisk przyjechały 4 osoby");
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    private void startTrain2Loop() {
        train2LoopRunnable = new Runnable() {
            @Override
            public void run() {
                startTrain2Ride();
                handler.postDelayed(this, 9000);
            }
        };
        handler.post(train2LoopRunnable);
    }

    private void startTrain2Ride() {
        if (Boolean.TRUE.equals(train2Active.getValue())) return;
        train2Active.setValue(true);
        train2Status.setValue("Linia Puszczykowo jedzie...");
        train2Progress.setValue(0);

        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override
            public void run() {
                prog += 10;
                train2Progress.setValue(prog);
                if (prog >= 100) {
                    Long cur = avenidaStudents.getValue();
                    if (cur != null) avenidaStudents.setValue(cur + 7);
                    train2Active.setValue(false);
                    train2Status.setValue("Z Puszczykowa przyjechało 7 osób");
                } else {
                    handler.postDelayed(this, 700);
                }
            }
        }, 700);
    }

    private void startTrain3Loop() {
        train3LoopRunnable = new Runnable() {
            @Override
            public void run() {
                startTrain3Ride();
                handler.postDelayed(this, 12000);
            }
        };
        handler.post(train3LoopRunnable);
    }

    private void startTrain3Ride() {
        if (Boolean.TRUE.equals(train3Active.getValue())) return;
        train3Active.setValue(true);
        train3Status.setValue("Linia Opalenica jedzie...");
        train3Progress.setValue(0);

        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override
            public void run() {
                prog += 10;
                train3Progress.setValue(prog);
                if (prog >= 100) {
                    Long cur = avenidaStudents.getValue();
                    if (cur != null) avenidaStudents.setValue(cur + 11);
                    train3Active.setValue(false);
                    train3Status.setValue("Z Opalenicy przyjechało 11 osób");
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (train1LoopRunnable != null) handler.removeCallbacks(train1LoopRunnable);
        if (train2LoopRunnable != null) handler.removeCallbacks(train2LoopRunnable);
        if (train3LoopRunnable != null) handler.removeCallbacks(train3LoopRunnable);
        if (tramLoopRunnable != null) handler.removeCallbacks(tramLoopRunnable);
    }
}