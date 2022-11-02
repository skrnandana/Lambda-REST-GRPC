import json
import boto3
import hashlib
from datetime import datetime, timedelta
import re

#Python SDK for AWS
s3=boto3.client('s3')

#Check if the word is matching with the specified pattern
def patternMatch(pattern ,word):
    findmatch = re.search(pattern,(word))
    if(findmatch != None):
        return True
    return False

#Recursive Binary Search to find the start index of the timestamp from the logs i.e T - dt
def startIndex(logs, targettime, start, end):
    if(start <= end):
        mid = (start + end) // 2
        actualtime = logs[mid].split(" ")[0].split(".")[0]
        nextlogtime = logs[mid + 1].split(" ")[0].split(".")[0]
        if(mid == len(logs) - 1 and actualtime == targettime):
            return mid
        if(actualtime == targettime and actualtime < nextlogtime):
            return mid
        if(actualtime == targettime and actualtime < nextlogtime):
            return startIndex(logs,targettime,mid + 1, end)
        if actualtime > targettime:
            return startIndex(logs,targettime,start, mid - 1)
        else:
            return startIndex(logs,targettime,mid+1, end)

    return -1

#Recursive Binary Search to find the end index of the timestamp from the logs i.e T + dt
def endIndex(logs, targettime, start, end):
    if(start <= end):
        mid = (start + end) // 2
        actualtime = logs[mid].split(" ")[0].split(".")[0]
        prevlogtime = logs[mid - 1].split(" ")[0].split(".")[0]
        if(mid == 0 and actualtime == targettime):
            return mid
        if(actualtime == targettime and actualtime <  prevlogtime):
            return mid
        if(actualtime == targettime and actualtime < prevlogtime):
            return startIndex(logs,targettime,start, mid - 1)
        if actualtime > targettime:
            return startIndex(logs,targettime,start, mid - 1)
        else:
            return startIndex(logs,targettime,mid+ 1, end)

    return -1


def lambda_handler(event, context):
    #Fetch today's date and append to logfile to get today's log file from s3 bucket
    date = datetime.today().strftime('%Y-%m-%d')
    key = 'logfiles/LogFileGenerator.'+date+'.log'

    #Get the s3 object using key and bucket
    logs = s3.get_object(Bucket='logbucket441', Key=key)
    records = logs['Body'].read().decode('utf-8').splitlines()

    #Fetch the t and dT parameters using GET request
    T = event['queryStringParameters']['T']
    dT= event['queryStringParameters']['dT']
    pattern =  event['queryStringParameters']['Pattern']

    # T = "17:38:04"
    # dT = "00:00:20"
    # pattern = r"([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}"

    #Find the start and end index from the log file , using T and DT
    deltastr=dT.split(":")
    initialTime = datetime.strptime(T,'%H:%M:%S')
    startTime = (initialTime - timedelta(hours=int(deltastr[0]))-timedelta(minutes=int(deltastr[1]))-timedelta(seconds=int(deltastr[2]))).strftime("%H:%M:%S")
    endTime = (initialTime + (timedelta(hours=int(deltastr[0]))+timedelta(minutes=int(deltastr[1]))+timedelta(seconds=int(deltastr[2])))).strftime("%H:%M:%S")
    print("start time is ",startTime)
    print("end time is",endTime )

    #Check if end time is exceeding the last record of log file, and start time is below the 1st record in log file
    if((endTime > records[len(records)-1]) or (startTime < records[0])):
        return {
            'statusCode': 404,
            'headers':{},
            'body': json.dumps("No records found in the specified interval")
        }
    else:
        #If only logs are present then the code gets executed for returning hash of logfiles.
        startind = startIndex(records,startTime,0,len(records) - 1)
        endind = endIndex(records,endTime,0,len(records) - 1)
        print("startindex",startind)
        print("endindex",endind)

        if(startTime < (records[0])):
            startTime = (records[0])
        if(endTime > (records[len(records)-1])):
            endTime = (records[len(records)-1])

        #if the start and end index does'nt exist then , no logs are found return 404 message
        if(startind == -1 or endind == -1 or startind == endind):
            return {
                'statusCode': 404,
                'headers':{},
                'body': json.dumps("No logs found in the specified interval")
            }
        else:
            outputlogs = list(records[startind : endind + 1])
            collected_logs = list()

            #Recursive Code to output the logs between startindex and end index. At the end return the md5 hash of output logs
            def printlogs(endindex, collected_logs, itr):
                #Base condition to check if the length of collected logs > length of output logs. If the iterator is incrementing beyond the specified elements
                if(len(outputlogs) < len(collected_logs) or itr > (endindex - startind)):
                    return collected_logs
                else:
                    #If there is a match between the word and pattern, append the logs to collected_logs
                    #Create an array of str words dividing them based on " "
                    wordarray = (outputlogs[itr]).split(" ")
                    #Find the last element of the record, i.e the word to be matched
                    word = wordarray[-1]
                    # #Check for the match
                    if(patternMatch(pattern, word) != False):
                        collected_logs.append(str(hashlib.md5((outputlogs[itr]).encode())))
                    return printlogs(endindex, collected_logs, itr + 1)

        return {
            'statusCode': 200,
            'headers':{},
            'body': json.dumps(printlogs(endind, collected_logs , 0))
        }
