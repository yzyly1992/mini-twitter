package my.service.repository;

import my.service.dto.TweetDTO;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

@EnableScan
public interface TweetRepository extends DynamoDBCrudRepository<TweetDTO, String> {
}