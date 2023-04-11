package my.service.config;
import com.amazon.dax.client.dynamodbv2.AmazonDaxClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

// import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "my.service.repository")
public class DynamoDBConfig {
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        // return AmazonDynamoDBClientBuilder.standard()
        //         .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://34.213.26.95:8000", "us-west-2"))
        //         .build();

        // Create a DAX client object
        // return (AmazonDaxClient) AmazonDaxClientBuilder.standard()
        //     .withEndpointConfiguration(new EndpointConfiguration("dax://dax.iomfja.dax-clusters.us-west-2.amazonaws.com", "us-west-2"))
        //     .build();
        AmazonDaxClientBuilder daxClientBuilder = AmazonDaxClientBuilder.standard();
        daxClientBuilder.withEndpointConfiguration("dax.iomfja.dax-clusters.us-west-2.amazonaws.com:8111");
        AmazonDynamoDB client = daxClientBuilder.build();
        return client;
    }
}