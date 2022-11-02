import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}
import com.typesafe.config.{Config, ConfigFactory}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import com.typesafe.config.{Config, ConfigFactory}
import gRPCClient.config
import gRPCServer.{config, getClass, logger, url}
import io.grpc.{Server, ServerBuilder}
import org.slf4j.LoggerFactory
import protobuff.{LogReply, LogRequest, checkLogGrpc}

import java.util.logging.{Level, Logger}
import scala.concurrent.{ExecutionContext, Future}
import spray.json._

object gRPCServer {

  private val config = ConfigFactory.load("application.conf")
  private val logger = LoggerFactory.getLogger(getClass)
  logger.info("Inside GRPC Server")

  //Fetch URL,T,dT,pattern, port values from application.conf
  val pattern: String = config.getString("Pattern")
  val url: String = config.getString("URL")
  val time: String = config.getString("T")
  val deltaTime: String = config.getString("dT")
  val port: Int = config.getInt("port")

  def main(args: Array[String]): Unit = {
    val server: gRPCServer = new gRPCServer(ExecutionContext.global)
    startServer(server)
    blockServerUntilShutdown(server)
  }

  // Function to start the server
  def startServer(server: gRPCServer): Unit = {
    logger.info("Starting the server")
    server.start()
  }

  //Function to block the server until shutdown
  def blockServerUntilShutdown(server: gRPCServer): Unit = {
    server.blockUntilShutdown()
  }

  //Function to stop the server
  def stopServer(server: gRPCServer): Unit = {
    logger.info("Stopping the server")
    server.stop()
  }
}
class gRPCServer(executionContext: ExecutionContext) {self => private[this] var server: Server = _
  //Initiate the server
  private def start(): Unit = {
    //Server uses the port which is mentioned in the application.conf file
    val logger = LoggerFactory.getLogger(getClass)
    server = ServerBuilder.forPort(gRPCServer.port).addService(checkLogGrpc.bindService(new checkLogImpl, executionContext)).build.start
    gRPCServer.logger.info("Server successfully stated on port " + gRPCServer.port)
    sys.addShutdownHook {
      logger.info("SYSTEM SHUTTING DOWN")
      self.stop()
    }
  }
  //Function to stop the server
  private def stop(): Unit = {
    logger.info("Stopping the server")
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }
}
  private class checkLogImpl extends checkLogGrpc.checkLog {
    val config: Config = ConfigFactory.load()

    override def checkTime(req: LogRequest): Future[LogReply] = {
      //Fetch and read the URL parameters from request
      val params = req.msgstr.split("-")
      val T = params(0)
      val dT = params(1)
      val pattern = params(2)
      val logger = LoggerFactory.getLogger(getClass)
      logger.info("T"+T)
      logger.info("dT"+dT)
      logger.info("Pattern"+pattern)
      //Invoke API Gateway
      val APIresponse = scala.io.Source.fromURL(url + "?T=" + T + "&dT=" + dT + "&Pattern=" + pattern)
      val result = APIresponse.mkString
      val reply = LogReply(message = result.toString())
      APIresponse.close()
      Future.successful(reply)
    }
  }
