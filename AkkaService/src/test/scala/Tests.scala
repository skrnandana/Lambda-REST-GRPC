import AkkaClient.{GET_Request, time, timesec}
import org.scalatest.funsuite.AnyFunSuite
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.Console.in
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import java.util
import java.util.List
import java.util.regex.Pattern
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.collection.mutable.Stack

class Tests extends AnyFunSuite {
  val config: Config = ConfigFactory.load()

  //Test to assert the URL response
  test("test URL") {
    // Calling API Gateway Request
    val response = Await.result(GET_Request(), timesec)
    assert(response.contains("md5"))
  }

  //Test to assert the Pattern from configuration
  test("PatternTest") {
    val actual = config.getString("Pattern")
    val expected = "([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}"
    assert(expected == actual)
  }


  //Test to assert the URL from configuration.
  test("URL test") {
    val actual = config.getString("URL")
    val expected = "https://ayhehnrx27.execute-api.us-east-1.amazonaws.com/teststage/441resource"
    assert(expected == actual)
  }
}
