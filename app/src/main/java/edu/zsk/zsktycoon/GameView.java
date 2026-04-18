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
    public MutableLiveData<Integer> trainProgress = new MutableLiveData<>(0);
    public MutableLiveData<String> trainStatus = new MutableLiveData<>("Pociąg czeka");
    public MutableLiveData<Boolean> trainActive = new MutableLiveData<>(false);

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable trainLoopRunnable;
    private Runnable trainDriveRunnable;
    private Runnable tramLoopRunnable;
    private Runnable tramDriveRunnable;

    public GameView() {
        startTrainLoop();
        startTramLoop();
    }

    private void startTrainLoop() {
        trainLoopRunnable = new Runnable() {
            @Override
            public void run() {
                startTrainRide();
                handler.postDelayed(this, 8000);
            }
        };
        handler.postDelayed(trainLoopRunnable, 8000);
    }

    private void startTrainRide() {
        Boolean active = trainActive.getValue();
        if (active != null && active) return;

        trainActive.setValue(true);
        trainStatus.setValue("Pociąg jedzie...");
        trainProgress.setValue(0);

        trainDriveRunnable = new Runnable() {
            int progress = 0;
            @Override
            public void run() {
                progress += 10;
                trainProgress.setValue(progress);

                if (progress >= 100) {
                    finishTrainRide();
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.postDelayed(trainDriveRunnable, 400);
    }

    private void finishTrainRide() {
        Long current = avenidaStudents.getValue();
        if (current != null) {
            avenidaStudents.setValue(current + 5);
        }

        trainActive.setValue(false);
        trainStatus.setValue("Pociąg przywiózł 5 osób");
        trainProgress.setValue(0);
    }

    private void startTramLoop() {
        tramLoopRunnable = new Runnable() {
            @Override
            public void run() {
                startTramRide();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(tramLoopRunnable, 5000);
    }

    private void startTramRide() {
        Boolean active = tramActive.getValue();
        if (active != null && active) return;

        tramActive.setValue(true);
        tramStatus.setValue("Tramwaj jedzie...");
        tramProgress.setValue(0);

        tramDriveRunnable = new Runnable() {
            int progress = 0;
            @Override
            public void run() {
                progress += 20;
                tramProgress.setValue(progress);

                if (progress >= 100) {
                    finishTramRide();
                } else {
                    handler.postDelayed(this, 600);
                }
            }
        };
        handler.postDelayed(tramDriveRunnable, 600);
    }

    private void finishTramRide() {
        Long onPlatform = avenidaStudents.getValue();
        Long inSchool = schoolStudents.getValue();

        long canTake = 20;
        long willTake = 0;

        if (onPlatform != null && inSchool != null) {
            willTake = onPlatform;
            if (willTake > canTake) willTake = canTake;
            if (willTake > 0) {
                avenidaStudents.setValue(onPlatform - willTake);
                schoolStudents.setValue(inSchool + willTake);
            }
        }

        tramActive.setValue(false);
        tramStatus.setValue("Tramwaj czeka");
        tramProgress.setValue(0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacksAndMessages(null);
    }
}