package my.service.config;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dax.AmazonDaxClient;
import com.amazonaws.services.dax.AmazonDaxClientBuilder;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "my.service.repository")
public class DynamoDBConfig {
    @Bean
    public AmazonDaxClient amazonDynamoDB() {
        // return AmazonDynamoDBClientBuilder.standard()
        //         .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://34.213.26.95:8000", "us-west-2"))
        //         .build();

        // Create a DAX client object
        return (AmazonDaxClient) AmazonDaxClientBuilder.standard()
            .withEndpointConfiguration(new EndpointConfiguration("dax://dax.iomfja.dax-clusters.us-west-2.amazonaws.com", "us-west-2"))
            .build();
    }
}