aws dynamodb create-table \
    --table-name feeds \
    --attribute-definitions \
        AttributeName=userID,AttributeType=S \
    --key-schema AttributeName=userID,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=10000,WriteCapacityUnits=10000 \
    --endpoint-url http://localhost:8000

aws dynamodb create-table \
    --table-name followers \
    --attribute-definitions \
        AttributeName=userID,AttributeType=S \
    --key-schema AttributeName=userID,KeyType=HASH  \
    --provisioned-throughput ReadCapacityUnits=10000,WriteCapacityUnits=10000 \
    --endpoint-url http://localhost:8000

aws dynamodb create-table \
    --table-name tweets \
    --attribute-definitions \
        AttributeName=tweetID,AttributeType=S \
    --key-schema AttributeName=tweetID,KeyType=HASH  \
    --provisioned-throughput ReadCapacityUnits=10000,WriteCapacityUnits=10000 \
    --endpoint-url http://localhost:8000

