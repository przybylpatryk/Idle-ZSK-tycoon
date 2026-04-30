package edu.zsk.zsktycoon;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {

    private static final String URL =
            "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=52.4064&longitude=16.9252" +
                    "&current=weather_code" +
                    "&forecast_days=1";

    public interface WeatherCallback {
        void onResult(float delayMultiplier, String description, String emoji);
    }

    public static void fetchWeatherMultiplier(WeatherCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postResult(callback, 1.0f, "Brak połączenia", "❓");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    int code = json
                            .getJSONObject("current")
                            .getInt("weather_code");

                    float multiplier = getMultiplierFromCode(code);
                    String description = getDescriptionFromCode(code, multiplier);
                    String emoji = getEmojiFromCode(code);
                    postResult(callback, multiplier, description, emoji);
                } catch (Exception e) {
                    postResult(callback, 1.0f, "Błąd danych", "❓");
                }
            }
        });
    }

    private static String getDescriptionFromCode(int code, float multiplier) {
        String condition;
        if (code == 0) {
            condition = "Bezchmurnie";
        } else if (code <= 3) {
            condition = "Zachmurzenie";
        } else if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
            condition = "Deszcz";
        } else if ((code >= 71 && code <= 77) || code == 85 || code == 86) {
            condition = "Śnieg";
        } else {
            condition = "Zmienne warunki";
        }
        if (multiplier == 1.0f) {
            return condition + " — brak opóźnień";
        } else {
            int pct = Math.round((multiplier - 1.0f) * 100);
            return condition + " — pojazdy +" + pct + "% wolniej";
        }
    }

    private static String getEmojiFromCode(int code) {
        if (code == 0) return "☀️";
        if (code <= 3) return "⛅";
        if ((code >= 71 && code <= 77) || code == 85 || code == 86) return "❄️";
        if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) return "🌧️";
        return "🌤️";
    }

    private static float getMultiplierFromCode(int code) {
        // WMO weather codes:
        // 51-67 = deszcz/mżawka
        // 80-82 = przelotny deszcz
        // 71-77 = śnieg
        // 85-86 = opady śniegu
        if ((code >= 71 && code <= 77) || code == 85 || code == 86) {
            return 1.3f;
        } else if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
            return 1.1f;
        }
        return 1.0f;
    }

    private static void postResult(WeatherCallback cb, float value, String description, String emoji) {
        new Handler(Looper.getMainLooper()).post(() -> cb.onResult(value, description, emoji));
    }
}