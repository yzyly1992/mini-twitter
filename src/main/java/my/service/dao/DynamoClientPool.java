package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DynamoClientPool {

    private static final GenericObjectPool<AmazonDynamoDB> dbClientPool;
    static {
        // GenericObjectPoolConfig<AmazonDynamoDB> dbPoolConfig = new GenericObjectPoolConfig<>();
        // dbPoolConfig.setMaxTotal(20);
        PooledObjectFactory<AmazonDynamoDB> clientPoolFactory = new DynamoConnectionFactory();
        dbClientPool = new GenericObjectPool<>(clientPoolFactory);
        // dbClientPool = new GenericObjectPool<>(clientPoolFactory, dbPoolConfig);
    }

    public static GenericObjectPool<AmazonDynamoDB> getInstance() {
        return dbClientPool;
    }
}
