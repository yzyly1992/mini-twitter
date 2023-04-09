package my.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import my.service.dto.FollowDTO;
import my.service.dto.TweetDTO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.Random;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TwitterClient {
    private final static int NUMTHREAD = 10;
    private final static int REQUESTPERTHREAD = 10;
    private final static int ATTEMPT = 5;
    static int successTimes = 0;
    static int failedTimes = 0;
    static int successTimesFollow = 0;
    static int failedTimesFollow = 0;
    static int successTimesFeed = 0;
    static int failedTimesFeed = 0;
    static int successTimesFollower = 0;
    static int failedTimesFollower = 0;

    private static final String SERVER_URL = "https://m7idjcaij7.execute-api.us-west-2.amazonaws.com/Prod/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException, IOException {

        CountDownLatch completedFollow = new CountDownLatch(NUMTHREAD);
        long startFollow = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    FollowDTO follow = new FollowDTO();
                    Random rand = new Random();
                    int follower = rand.nextInt(5000);
                    int followee = rand.nextInt(5000);
                    while (followee == follower) {
                        followee = rand.nextInt(5000);
                    }

                    follow.setFollower(Integer.toString(follower + 1));
                    follow.setFollowee(Integer.toString(followee + 1));
                    try {
                        postToServerFollow(follow);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completedFollow.countDown();
            };
            new Thread(thread).start();
        }
        completedFollow.await();
        long endFollow = System.currentTimeMillis();
        double throughputFollow = 1000 * NUMTHREAD * REQUESTPERTHREAD / (endFollow - startFollow);
        System.out.println("The post follow requests' successful times is " + successTimesFollow + ", failed times is: "
                + failedTimesFollow);
        System.out.println("The post follow average throughput is " + throughputFollow + " requests per second.");


        CountDownLatch completed = new CountDownLatch(NUMTHREAD);
        long start = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    TweetDTO tweet = new TweetDTO();
                    tweet.setUserID(Integer.toString(new Random().nextInt(5000) + 1));
                    tweet.setContent("I like it!");
                    try {
                        postToServerTweet(tweet);
                    } catch (Exception e) {
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
        System.out.println(
                "The post tweet requests' successful times is " + successTimes + ", failed times is: " + failedTimes);
        System.out.println("The post tweet average throughput is " + throughput + " requests per second.");



        CountDownLatch completedFeed = new CountDownLatch(NUMTHREAD);
        long startFeed = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    try {
                        getFeed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completedFeed.countDown();
            };
            new Thread(thread).start();
        }
        completedFeed.await();
        long endFeed = System.currentTimeMillis();
        double throughputFeed = 1000 * NUMTHREAD * REQUESTPERTHREAD / (endFeed - startFeed);
        System.out.println("The get feed requests' successful times is " + successTimesFeed + ", failed times is: "
                + failedTimesFeed);
        System.out.println("The get feed average throughput is " + throughputFeed + " requests per second.");

        CountDownLatch completedFollower = new CountDownLatch(NUMTHREAD);
        long startFollower = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    try {
                        getFollowers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completedFollower.countDown();
            };
            new Thread(thread).start();
        }
        completedFollower.await();
        long endFollower = System.currentTimeMillis();
        double throughputFollower = 1000 * NUMTHREAD * REQUESTPERTHREAD / (endFollower - startFollower);
        System.out.println(
                "The get followerlist requests' successful times is " + successTimesFollower + ", failed times is: "
                        + failedTimesFollower);
        System.out
                .println("The get followerlist average throughput is " + throughputFollower + " requests per second.");

    }

    private static void postToServerTweet(TweetDTO tweet) throws Exception {
        String data = objectMapper.writeValueAsString(tweet);
        URL url = new URL(SERVER_URL + "tweet");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // write the request body
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data.getBytes());

        // get the response code and body
        int statusCode = conn.getResponseCode();
        // BufferedReader bufferedReader;
        if (statusCode >= 200 && statusCode < 300) {
            // bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            successTimes += 1;
        } else {
            // bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            failedTimes += 1;
        }

        // StringBuilder responseBuilder = new StringBuilder();
        // String line;
        // while ((line = bufferedReader.readLine()) != null) {
        //     responseBuilder.append(line);
        // }
        // bufferedReader.close();
        conn.disconnect();
    }

    private static void postToServerFollow(FollowDTO follow) throws Exception {
        String data = objectMapper.writeValueAsString(follow);
        URL url = new URL(SERVER_URL + "follow");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // write the request body
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data.getBytes());

        // get the response code and body
        int statusCode = conn.getResponseCode();
        // BufferedReader bufferedReader;
        if (statusCode >= 200 && statusCode < 300) {
            // bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            successTimesFollow += 1;
        } else {
            // bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            failedTimesFollow += 1;
        }

        // StringBuilder responseBuilder = new StringBuilder();
        // String line;
        // while ((line = bufferedReader.readLine()) != null) {
        //     responseBuilder.append(line);
        // }
        // bufferedReader.close();
        conn.disconnect();
    }

    private static void getFeed() throws IOException {
        URL url = new URL(SERVER_URL + "feed/" + (new Random().nextInt(5000) + 1));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int statusCode = con.getResponseCode();
        if (statusCode >= 200 && statusCode < 300) {
            successTimesFeed += 1;
        } else {
            failedTimesFeed += 1;
        }
    }

    private static void getFollowers() throws IOException {
        URL url = new URL(SERVER_URL + "followers/" + (new Random().nextInt(5000) + 1));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int statusCode = con.getResponseCode();
        if (statusCode >= 200 && statusCode < 300) {
            successTimesFollower += 1;
        } else {
            failedTimesFollower += 1;
        }
    }

}