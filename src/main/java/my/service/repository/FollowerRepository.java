package my.service.repository;

import my.service.dto.FollowerDTO;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

@EnableScan
public interface FollowerRepository extends DynamoDBCrudRepository<FollowerDTO, String> {
}