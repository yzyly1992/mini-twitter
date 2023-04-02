package my.service.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class DynamoConnectionFactory extends BasePooledObjectFactory<AmazonDynamoDB> {


    @Override
    public AmazonDynamoDB create() throws Exception {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();
    }

    @Override
    public PooledObject<AmazonDynamoDB> wrap(AmazonDynamoDB client) {
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<AmazonDynamoDB> p) throws Exception {
        p.getObject().shutdown();
    }
}

