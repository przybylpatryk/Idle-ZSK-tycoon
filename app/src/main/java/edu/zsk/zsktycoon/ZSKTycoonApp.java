package edu.zsk.zsktycoon;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class ZSKTycoonApp extends Application implements ViewModelStoreOwner {

    private final ViewModelStore appViewModelStore = new ViewModelStore();

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return appViewModelStore;
    }
}
