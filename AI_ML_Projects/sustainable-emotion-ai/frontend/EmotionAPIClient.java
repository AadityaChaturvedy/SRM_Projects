import com.google.gson.Gson;
import okhttp3.*;

public class EmotionAPIClient {
    private final String baseUrl;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public EmotionAPIClient(String baseUrl) { this.baseUrl = baseUrl; }

    public EmotionResult detectEmotion(byte[] imageBytes) throws Exception {
        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "frame.jpg",
                RequestBody.create(MediaType.parse("image/jpeg"), imageBytes))
            .build();

        Request request = new Request.Builder()
            .url(baseUrl + "/detect-emotion")
            .post(requestBody)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) return null;
            String json = response.body().string();
            return gson.fromJson(json, EmotionResult.class);
        }
    }
}
