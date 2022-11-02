import protobuff.checkLogGrpc.checkLogBlockingStub
import protobuff.{checkLogGrpc, LogRequest}

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import org.slf4j.LoggerFactory


object gRPCClient {
  val config: Config  = ConfigFactory.load()
  //Fetching pattern, url, time, delta time from application.conf file
  
  val pattern: String = config.getString("Pattern")
  val url: String = config.getString("URL")
  val time: String = config.getString("T")
  val deltaTime: String = config.getString("dT")

  def apply(host: String, port: Int): gRPCClient = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build
    val blockingStub = checkLogGrpc.blockingStub(channel)
    new gRPCClient(channel, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    //Based on the port in application.conf, create a client on localhost.
    val client = gRPCClient("localhost", ConfigFactory.load().getInt("port"))
    try {
      val user = args.headOption.getOrElse(time+"-"+deltaTime+"-"+pattern)
      println("Final result is: "+client.displayMessage(user))
    } finally {
      client.shutdown()
    }
  }
}

class gRPCClient private(private val channel: ManagedChannel,private val blockingStub: checkLogBlockingStub) {
  val logger = LoggerFactory.getLogger(getClass)
  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  def displayMessage(value: String): String = {
    logger.info("Params String : "+value)
    // "-" delimeter to split the string
    val result = value.split("-")
    logger.info("T="+result(0))
    logger.info("dT="+result(1))
    logger.info("Pattern="+result(2)+"-"+result(3)+"-"+(result(4))+"-"+result(5)+"-"+(result(6))+"-"+result(7)+"-"+result(8))
    
    //Make a request to server
    val request = LogRequest(value)
    // Display the fetched message from Lambda
    try {
      val lambda_response = blockingStub.checkTime(request)
      // Display the log messages based on lambda output
      logger.info("SUCCESS")
      logger.info("The final response is " + lambda_response.message)
      lambda_response.message
    }
    catch {
      // Code to be executed if there are no logs in the given interval/ no logs matching the pattern.
      case e: StatusRuntimeException =>
        logger.info("No logs found with the provided t, dt, Pattern parameters.")
        "FAILURE"
    }
  }
}