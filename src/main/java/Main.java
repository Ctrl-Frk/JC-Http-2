import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String LINK =
            "https://api.nasa.gov/planetary/apod?api_key=IYjAxszg9zhRxSgh6ZvAbL4i3vEVMExZmgA0VIZv";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(LINK);

        CloseableHttpResponse response = httpClient.execute(request);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        NasaResponse nasaResponse = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );
        System.out.println(nasaResponse);

        String imageUrl = nasaResponse.getUrl();
        String[] imageName = imageUrl.split("/");
        String imgName = imageName[imageName.length - 1];
        request = new HttpGet(imageUrl);

        response = httpClient.execute(request);
        FileOutputStream out = new FileOutputStream(imgName);
        out.write(response.getEntity().getContent().readAllBytes());

        response.close();
        httpClient.close();
    }
}
