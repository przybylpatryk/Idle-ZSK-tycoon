package edu.zsk.zsktycoon;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SharedViewModelFactory implements ViewModelProvider.Factory {
    private final GameView gameView;

    public SharedViewModelFactory(GameView gameView) {
        this.gameView = gameView;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameView.class)) {
            return (T) gameView;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}