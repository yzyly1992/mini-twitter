package my.service.client;

import com.google.gson.Gson;
import my.service.dto.TweetDTO;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TwitterClient {
    private final static int NUMTHREAD = 1;
    private final static int REQUESTPERTHREAD = 1;
    private final static int ATTEMPT = 5;
    static int successTimes = 0;
    static int failedTimes = 0;

    public static void main(String[] args) throws InterruptedException, IOException {
        CountDownLatch completed = new CountDownLatch(NUMTHREAD);
        long start = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    TweetDTO tweet = new TweetDTO();
                    tweet.setUserID(Integer.toString(new Random().nextInt(5000) + 1));
                    tweet.setContent(RandomStringUtils.randomAlphanumeric(256));
                    String jsonPayload = new Gson().toJson(tweet);

                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        HttpPost httpPost = new HttpPost(
                                "https://vyn4aphu0g.execute-api.us-west-2.amazonaws.com/Prod/tweet");
                        httpPost.setHeader("Content-type", "application/json");

                        StringEntity stringEntity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
                        httpPost.setEntity(stringEntity);
                        boolean status = false;
                        for (int k = 0; k < ATTEMPT; k++) {
                            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                                int statusCode = response.getStatusLine().getStatusCode();
                                System.out.println("statusCode:" + statusCode);
                                if (statusCode == 200) {
                                    status = true;
                                    successTimes += 1;
                                    // HttpEntity entity = response.getEntity();
                                    // if (entity != null) {
                                    // String responseString = EntityUtils.toString(entity);
                                    // System.out.println(responseString);
                                    // }
                                    break;
                                }
                            }
                        }
                        if (!status) {
                            failedTimes += 1;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                completed.countDown();
            };
            new Thread(thread).start();
        }
        completed.await();
        long end = System.currentTimeMillis();
        double throughput = 1000 * NUMTHREAD * REQUESTPERTHREAD / (end - start);
        System.out.println("There are " + NUMTHREAD + " threads, each thread has " + REQUESTPERTHREAD + " requests.");
        System.out.println("The requests' successful times is " + successTimes + ", failed times is: " + failedTimes);
        System.out.println("The average throughput is " + throughput + " requests per second.");
    }
}
