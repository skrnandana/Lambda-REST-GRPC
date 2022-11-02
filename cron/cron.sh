#!/bin/bash
java -cp "/home/ec2-user:/home/ec2-user/*" runLogGenerator
date=$(date '+%Y-%m-%d')
aws s3 cp /home/ec2-user/log/LogFileGenerator.$date.log s3://logbucket441/logfiles/
