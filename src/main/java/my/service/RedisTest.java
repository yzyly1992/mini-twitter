package my.service;

import my.service.dao.RedisDAO;

public class RedisTest {

    public static void main(String[] args) {
        RedisDAO redisDAO = new RedisDAO();

        // Set a key-value pair
        redisDAO.setKey("test-key", "Hello, World!");
        // Retrieve the value
        String value = redisDAO.getKey("test-key");
        System.out.println("Value of test-key: " + value);
    }
}