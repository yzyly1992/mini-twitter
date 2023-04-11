package my.service.repository;

import my.service.dto.FeedDTO;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

@EnableScan
public interface FeedRepository extends DynamoDBCrudRepository<FeedDTO, String> {
}