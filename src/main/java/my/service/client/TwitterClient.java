package my.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import my.service.dto.FollowDTO;
import my.service.dto.TweetDTO;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class TwitterClient {
    private final static int NUMTHREAD = 1;
    private final static int REQUESTPERTHREAD = 10;
    static int successTimes = 0;
    static int failedTimes = 0;
    static int successTimesFollow = 0;
    static int failedTimesFollow = 0;
    static int successTimesFeed = 0;
    static int failedTimesFeed = 0;
    static int successTimesFollower = 0;
    static int failedTimesFollower = 0;

    private static final String SERVER_URL = "https://vyn4aphu0g.execute-api.us-west-2.amazonaws.com/Prod/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException, IOException {
        File filePostTweet = new File("./recordPostTweet.csv");
        File filePostFollower = new File("./recordPostFollower.csv");
        File fileGetFeed = new File("./recordGetFeed.csv");
        File fileGetFollower = new File("./recordGetFollower.csv");

        FileWriter outputPostTweet = new FileWriter(filePostTweet);
        CSVWriter writerPostTweet = new CSVWriter(outputPostTweet);
        FileWriter outputPostFollower = new FileWriter(filePostFollower);
        CSVWriter writerPostFollower = new CSVWriter(outputPostFollower);
        FileWriter outputGetFeed = new FileWriter(fileGetFeed);
        CSVWriter writerGetFeed = new CSVWriter(outputGetFeed);
        FileWriter outputGetFollower = new FileWriter(fileGetFollower);
        CSVWriter writerGetFollower = new CSVWriter(outputGetFollower);

        CountDownLatch completed = new CountDownLatch(NUMTHREAD);
        long start = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    long singleStartTweet = System.currentTimeMillis();
                    TweetDTO tweet = new TweetDTO();
                    tweet.setUserID(Integer.toString(new Random().nextInt(5000) + 1));
                    tweet.setContent(RandomStringUtils.randomAlphanumeric(256));
                    try {
                        postToServerTweet(tweet);
                        long latency = System.currentTimeMillis() - singleStartTweet;
                        writerPostTweet.writeNext(new String[] { String.valueOf(singleStartTweet), "POST",
                                String.valueOf(latency), String.valueOf(200) });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completed.countDown();
            };
            new Thread(thread).start();
        }
        ;
        writerPostTweet.close();
        completed.await();
        long end = System.currentTimeMillis();
        double throughput = 1000 * NUMTHREAD * REQUESTPERTHREAD / (end - start);
        System.out.println("There are " + NUMTHREAD + " threads, each thread has " + REQUESTPERTHREAD + " requests.");
        System.out.println(
                "The post tweet requests' successful times is " + successTimes + ", failed times is: " + failedTimes);
        System.out.println("The post tweet average throughput is " + throughput + " requests per second.");
        printPerformance("postTweet");

        CountDownLatch completedFollow = new CountDownLatch(NUMTHREAD);
        long startFollow = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    long singlePostFollower = System.currentTimeMillis();
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
                        long latency = System.currentTimeMillis() - singlePostFollower;
                        writerPostFollower.writeNext(new String[] { String.valueOf(singlePostFollower), "POST",
                                String.valueOf(latency), String.valueOf(200) });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completedFollow.countDown();
            };
            new Thread(thread).start();
        }
        writerPostFollower.close();
        completed.await();
        long endFollow = System.currentTimeMillis();
        double throughputFollow = 1000 * NUMTHREAD * REQUESTPERTHREAD / (endFollow - startFollow);
        System.out.println("The post follow requests' successful times is " + successTimesFollow + ", failed times is: "
                + failedTimesFollow);
        System.out.println("The post follow average throughput is " + throughputFollow + " requests per second.");
        printPerformance("postFollower");

        CountDownLatch completedFeed = new CountDownLatch(NUMTHREAD);
        long startFeed = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    long singleGetFeed = System.currentTimeMillis();
                    try {
                        getFeed();
                        long latency = System.currentTimeMillis() - singleGetFeed;
                        writerGetFeed.writeNext(new String[] { String.valueOf(singleGetFeed), "GET",
                                String.valueOf(latency), String.valueOf(200) });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completedFeed.countDown();
            };
            new Thread(thread).start();
        }
        ;
        writerGetFeed.close();
        completedFeed.await();
        long endFeed = System.currentTimeMillis();
        double throughputFeed = 1000 * NUMTHREAD * REQUESTPERTHREAD / (endFeed - startFeed);
        System.out.println("The get feed requests' successful times is " + successTimesFeed + ", failed times is: "
                + failedTimesFeed);
        System.out.println("The get feed average throughput is " + throughputFeed + " requests per second.");
        printPerformance("getFeed");

        CountDownLatch completedFollower = new CountDownLatch(NUMTHREAD);
        long startFollower = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREAD; i++) {
            Runnable thread = () -> {
                for (int j = 0; j < REQUESTPERTHREAD; j++) {
                    long singleGetFollower = System.currentTimeMillis();
                    try {
                        getFollowers();
                        long latency = System.currentTimeMillis() - singleGetFollower;
                        writerGetFollower.writeNext(new String[] { String.valueOf(singleGetFollower), "GET",
                                String.valueOf(latency), String.valueOf(200) });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                completedFollower.countDown();
            };
            new Thread(thread).start();
        }
        ;
        writerGetFollower.close();
        completedFollower.await();
        long endFollower = System.currentTimeMillis();
        double throughputFollower = 1000 * NUMTHREAD * REQUESTPERTHREAD / (endFollower - startFollower);
        System.out.println(
                "The get followerlist requests' successful times is " + successTimesFollower + ", failed times is: "
                        + failedTimesFollower);
        System.out
                .println("The get followerlist average throughput is " + throughputFollower + " requests per second.");
        printPerformance("getFollower");
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
        BufferedReader bufferedReader;
        if (statusCode >= 200 && statusCode < 300) {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            successTimes += 1;
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            failedTimes += 1;
        }

        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            responseBuilder.append(line);
        }
        bufferedReader.close();
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
        BufferedReader bufferedReader;
        if (statusCode >= 200 && statusCode < 300) {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            successTimesFollow += 1;
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            failedTimesFollow += 1;
        }

        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            responseBuilder.append(line);
        }
        bufferedReader.close();
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

    private static void printPerformance(String file) throws IOException {
        String filePath = "/";
        if (file.equals("postTweet")) {
            filePath = "./recordPostTweet.csv";
        } else if (file.equals("postFollower")) {
            filePath = "./recordPostFollower.csv";
        } else if (file.equals("getFeed")) {
            filePath = "./recordGetFeed.csv";
        } else if (file.equals("getFollower")) {
            filePath = "./recordGetFollower.csv";
        }

        FileReader filereader = new FileReader((filePath));
        CSVReader csvReader = new CSVReader(filereader);
        String[] nextRecord;
        int total = 0;
        List<Integer> arr = new ArrayList<>();

        while ((nextRecord = csvReader.readNext()) != null) {
            int duration = Integer.valueOf(nextRecord[2]);
            total += duration;
            arr.add(duration);
        }

        csvReader.close();
        Collections.sort(arr);
        int count = arr.size();
        int min = arr.get(0);
        int max = arr.get(arr.size() - 1);
        int median = (arr.get(arr.size() / 2) + arr.get((arr.size() / 2) - 1)) / 2;
        int mean = total / count;
        int p99Index = (int) Math.ceil(99 / 100.0 * arr.size());
        int p99 = arr.get(p99Index - 1);
        System.out.println(file + " " + count + " requests' Latency:");
        System.out.println("mean response time is " + mean + "ms,\nmedian response time is "
                + median + "ms,\np99 (99th percentile) response time is " + p99 +
                "ms,\nmin and max response time are " + min + "ms and " + max + "ms");
    }
}