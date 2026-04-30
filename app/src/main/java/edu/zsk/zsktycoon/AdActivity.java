package edu.zsk.zsktycoon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ad);

        Button claim = findViewById(R.id.claim_button);
        TextView timer = findViewById(R.id.timer_text);
        ImageView image = findViewById(R.id.ad_image);
        CountDownTimer countDownTimer;

        claim.setEnabled(false);
        claim.setBackgroundColor(0xFFA0AEC0);

        loadImgFromUrl("https://picsum.photos/400/300", image);

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onFinish() {
                timer.setText("Nagroda gotowa");
                claim.setEnabled(true);
                claim.setBackgroundColor(0xFF10B981);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int)(millisUntilFinished / 1000);
                timer.setText("Pozostało: " + secondsLeft + " s");
            }
        }.start();

        claim.setOnClickListener(v -> {
            GameView gm = GameView.getInstance();
            Long current = gm.teachers.getValue();
            if (current != null) {
                gm.teachers.setValue(current + 500);
            }
            if (countDownTimer != null) countDownTimer.cancel();
            finish();
        });
    }
    private void loadImgFromUrl(String ImgUrl, ImageView imageView) {
        new Thread(() -> {
            try {
                URL url = new URL(ImgUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();

                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {

            }
        }).start();
    }
}