package zerodowntime;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class TestHelper {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.parse("application/json");

    private final OkHttpClient client;
    private final String baseUrl;

    public TestHelper(OkHttpClient client, String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    public Response postJson(String path, Object body) throws IOException {
        String json = mapper.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public Response get(String path) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .build();

        return client.newCall(request).execute();
    }

    public Response post(String pathTemplate, String... params) throws IOException {
        String path = pathTemplate;
        for (String param : params) {
            path = path.replaceFirst("\\{[^}]+\\}", param);
        }

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        return client.newCall(request).execute();
    }

    public <T> T readJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }
}