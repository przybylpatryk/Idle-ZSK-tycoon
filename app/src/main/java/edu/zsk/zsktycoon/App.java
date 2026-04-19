package edu.zsk.zsktycoon;

import android.app.Application;

public class App extends Application {
    private static GameView gameView;

    public static GameView getGameView() {
        if (gameView == null) {
            gameView = new GameView();
        }
        return gameView;
    }
}