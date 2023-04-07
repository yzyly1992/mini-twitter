# Mini-Twitter Scalable System
Team: Lin Li, Fangxiao Guo, Zhiyuan Yang  

##### Project Introduction
Mini-Twitter project design and develop a highly scalable system to provide services of post tweet, read feed, and follow user. We will experiment and test on two different caching strategies, analysis the system's performance and trade-off. 

##### Architecture
![architecture](https://i.postimg.cc/fbT2f43C/Snipaste-2023-04-06-16-36-30.jpg)

##### API Design
- POST /tweet
- POST /follow
- GET /followers/{userID}
- GET /feed/{userID}

##### Database Design
- followers: userID, followersList(list of IDs)
- feeds: userID, tweetsList(list of tweets)
- tweets: tweetID, userID, content, timestamp

##### Database Update Logic
Push Model:
User postTweet -> getFollower -> write new tweet to followers' feed

##### Redis/Cache Design
- feeds: userID, tweetsList

##### Cache Strategy
Write-through:
User postTweet -> update cache  
User getFeed -> directly getFeed from cache



##### Usage

Build the project with Maven:
```bash
$ mvn clean package
```

Install [AWS SAM Local](https://github.com/awslabs/aws-sam-local) to start your project locally.

Next, from the project root folder - where the `sam.yaml` file is located - start the API with the SAM Local CLI.

```bash
$ sam local start-api --template sam.yaml

...
Mounting my.service.StreamLambdaHandler::handleRequest (java8) at http://127.0.0.1:3000/{proxy+} [OPTIONS GET HEAD POST PUT DELETE PATCH]
...
```

Using a new shell, you can send a test ping request to your API:

```bash
$ curl -s http://127.0.0.1:3000/ping | python -m json.tool

{
    "pong": "Hello, World!"
}
``` 

You can use the [AWS CLI](https://aws.amazon.com/cli/) to quickly deploy your application to AWS Lambda and Amazon API Gateway with your SAM template.

You will need an S3 bucket to store the artifacts for deployment. Once you have created the S3 bucket, run the following command from the project's root folder - where the `sam.yaml` file is located:

```
$ aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket <YOUR S3 BUCKET NAME>
Uploading to xxxxxxxxxxxxxxxxxxxxxxxxxx  6464692 / 6464692.0  (100.00%)
Successfully packaged artifacts and wrote output template to file output-sam.yaml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file /your/path/output-sam.yaml --stack-name <YOUR STACK NAME>
```

As the command output suggests, you can now use the cli to deploy the application. Choose a stack name and run the `aws cloudformation deploy` command from the output of the package command.
 
```
$ aws cloudformation deploy --template-file output-sam.yaml --stack-name MiniTwitterApi --capabilities CAPABILITY_IAM
```

Once the application is deployed, you can describe the stack to show the API endpoint that was created. The endpoint should be the `MiniTwitterApi` key of the `Outputs` property:

```
$ aws cloudformation describe-stacks --stack-name MiniTwitterApi
{
    "Stacks": [
        {
            "StackId": "arn:aws:cloudformation:us-west-2:xxxxxxxx:stack/MiniTwitterApi/xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx", 
            "Description": "AWS Serverless Spring API - my.service::lambda-springboot", 
            "Tags": [], 
            "Outputs": [
                {
                    "Description": "URL for application",
                    "ExportName": "LambdaSpringbootApi",  
                    "OutputKey": "LambdaSpringbootApi",
                    "OutputValue": "https://xxxxxxx.execute-api.us-west-2.amazonaws.com/Prod/ping"
                }
            ], 
            "CreationTime": "2016-12-13T22:59:31.552Z", 
            "Capabilities": [
                "CAPABILITY_IAM"
            ], 
            "StackName": "MiniTwitterApi", 
            "NotificationARNs": [], 
            "StackStatus": "UPDATE_COMPLETE"
        }
    ]
}

```

Copy the `OutputValue` into a browser or use curl to test your first request:

```bash
$ curl -s https://xxxxxxx.execute-api.us-west-2.amazonaws.com/Prod/ping | python -m json.tool

{
    "pong": "Hello, World!"
}
```