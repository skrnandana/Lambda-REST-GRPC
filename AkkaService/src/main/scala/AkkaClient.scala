import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity

import scala.concurrent.Await
import scala.concurrent.duration.{DurationInt, FiniteDuration}
//import akka.http.impl.util.JavaAccessors.HttpEntity
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpMethods, HttpRequest, HttpResponse}
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object AkkaClient {
  private val logger = LoggerFactory.getLogger(getClass)
  private val config = ConfigFactory.load("application.conf")
  logger.info("Inside Akka Client")

  //Fetch URL,T,dT,pattern, REST Method, timeout values from application.conf
  val pattern: String = config.getString("Pattern")
  val url: String = config.getString("URL")
  val time: String = config.getString("T")
  val deltaTime: String = config.getString("dT")
  val method: String = config.getString("POST")
  val timesec : FiniteDuration = config.getInt("WAIT").seconds


  //Using Akka ActorSystem
  implicit val actorsystem: ActorSystem = ActorSystem()
  def GET_Request(): Future[String] = {

    logger.info("Inside GET Request")
    logger.info("Pattern :" + config.getString("Pattern"))
    logger.info("T:" + config.getString("T"))
    logger.info("dT:" + config.getString("dT"))

    //Request to API Gateway by passing URL, time, delta time and pattern
    val request = HttpRequest(method = HttpMethods.GET, uri = s"$url?T=$time&dT=$deltaTime&Pattern=$pattern")
    val response: Future[HttpResponse] = Http().singleRequest(request)


    //On getting response from API , the below code will be executed. Logger displays the message based on the response.
    response.onComplete {
        case Success(res) =>
          val HttpResponse(statusCodes, headers, entity, _) = res
          logger.info("SUCCESS")
          entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach(body => {
            logger.info(body.utf8String)
          })
          actorsystem.terminate()
        case Failure(_) => sys.error("FAILURE")
      }

    val entityFuture: Future[HttpEntity.Strict] = response.flatMap(response => response.entity.toStrict(timesec))
    entityFuture.map(entity => entity.data.utf8String)

  }

  def POST_Request():Future[String] = {

    logger.info("Inside Post Request")
    logger.info("Pattern :" + config.getString("Pattern"))
    logger.info("T:" + config.getString("T"))
    logger.info("dT:" + config.getString("dT"))

    //POST Request to API Gateway
    val request = HttpRequest(method = HttpMethods.POST, uri = s"$url", entity = HttpEntity(ContentTypes.`application/json`, "data"))
    val response: Future[HttpResponse] = Http().singleRequest(request)

    //On completion of request , the below code will be executed. Logger displays the message based on the response.
    response.onComplete {
      case Success(res) =>
        val HttpResponse(statusCodes, headers, entity, _) = res
        logger.info("SUCCESS")
        entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach(body => {
          logger.info(body.utf8String)
        })
        actorsystem.terminate()
      case Failure(_) => sys.error("FAILURE")
    }
    val entityFuture: Future[HttpEntity.Strict] = response.flatMap(response => response.entity.toStrict(timesec))
    entityFuture.map(entity => entity.data.utf8String)
  }


  def main(args: Array[String]): Unit =

    if(method == "GET")
      System.out.println(Await.result(GET_Request(),timesec))
    else
      System.out.println(Await.result(POST_Request(),timesec))
}
