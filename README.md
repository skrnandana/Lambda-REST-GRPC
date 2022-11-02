## Name : Kavya Rama Nandana Sidda
## UIN : 650426256
## UIC  Email : ksidda2@uic.edu
## Github : https://github.com/skrnandana/Lambda-REST-GRPC
## Video Link : https://www.youtube.com/watch?v=h3swyQ8SCvc

## Introduction
The goal of the project is to locate a set of messages in the Log File created by project 'LogFileGenerator'. The project LogFileGenerator is deployed on a EC2 instance and is connected to S3 to store the log files. We should create a IAM role and policy to allow communication between EC2 and S3. This project has a server Lambda Function which accepts the requests from clients. The Lambda function gets invoked by a pair of clients. One is GRPC client and other one is REST Client.  The project also has GRPC Server where the GRPC Client requests the GRPC Server, and GRPC Server which in turn requests the Lambda function. The protobuf “protobuff.proto” communicates between the  GRPC client and the GRPC server. 

## Prerequisites
+ Services on AWS : Lambda, s3, ec2, AWS API Getway, IAM Roles
+ Install jdk , aws , sbt , on ec2 instance.
+ Install SBT For Scala.
+ Python environment for Lambda.
+ IDEA IntelliJ is used to run the project.

## Project Components and Execution
Clone the git repository using git clone https://github.com/skrnandana/Lambda-REST-GRPC.git. 
The project has the following main components.
### 1.AkkaService

The REST client is written in scala. It has type of request as GET and POST. 
It uses the parameters url, T, dT, Pattern to make an request to Lambda. We create an AWS API gateway as the Endpoint as ‘ANY’. Hence it accepts both POST and GET request.

```
cd AkkaService
sbt clean compile test
```


### Output:
<p align="center">
  <img src="Output1.JPG" />
</p>

### 2.GRPC: 

GRPC Server runs on the port specified in the configuration file. GRPC Server receives request from GRPC Client. The GRPC Server will again invoke lambda server and responds back to the Client. If the process is successful  ,then a success message is shown  ,and if some failure happens, a FAILURE message is shown

```
cd GRPC
sbt clean compile test
```


## Output of GRPC Server:
<p align="center">
  <img src="Output2.JPG" />
</p>

## Output of GRPC Client:
<p align="center">
  <img src="Output3.JPG" />
</p>



### 3.Lambda: 

The Lambda function is written in python language and has two main functionalities. First it will check if the log files are present in the desired timestamp T. The project uses Recursive Binary search of time complexity O(log n) to find the start index(T-dT) and end index(T + dT) of the logs. If the logs are present, then it executes the 2nd part. Else, it will throw an 400 message to the user. The next main functionality is to fetch the logs matching the string regex pattern from the respective start and end indexes. We use hashlib library to convert the logs into md5 hashes. Lambda accesses the corresponding output in s3 generated by the ec2 instance. 
The Lambda code in \Lambda\Lambda.py should be deployed on AWS Lambda console.



### 4.LogFileGenerator:
(https://github.com/0x1DOCD00D/CS441_Fall2022/tree/main/LogFileGenerator)
 We run the logfile generator and build a assembly file of the project using “sbt assembly.” We upload the jar into ec2. We run a cron script for generating log files periodically for every 2nd minute . This updating log file will be read by s3.


## AWS Services

### EC2 and S3
+ Create an EC2 instance on AWS.
+ Login  to EC2 using WinSCP and upload the log file generator jar file using the command : 
 ` java -cp <log file jar name>:/current project directory in ec2 <main method name>`

+ Run the commands to install aws ,sbt and java on ec2.
```
sudo yum update
sudo amazon-linux-extras enable corretto8
sudo yum clean metadata
yum install java-1.8.0-amazon-corretto
sudo yum install -y java-1.8.0-amazon-corretto
sudo rm -f /etc/yum.repos.d/bintray-rpm.repo
curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
sudo mv sbt-rpm.repo /etc/yum.repos.d/
sudo yum install sbt
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

```
+ Upload all the dependency jar files for slf4j.
+ SSH into EC2 as ec2-user using putty.
+ Create an S3 bucket and give access to ec2 using S3 Full access IAM policy.

### API Gateway:
+ We create AWS API Gateway as the Endpoint with a ANY(GET, POST) request. It is used to invoke the Lambda function on AWS.

### Lambda:
+ The Lambda code in \Lambda\Lambda.py should be deployed on AWS Lambda console. We should test and Deploy the Lambda Function.

### IAM Roles and Policies:
+ Create a IAM Role with 2 policies, one which allows accessing S3 to Lambda , and the other one which allows accessing EC2 to Lambda.

Thank you.
