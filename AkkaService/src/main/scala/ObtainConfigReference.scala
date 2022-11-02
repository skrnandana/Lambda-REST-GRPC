
import scala.util.{Failure, Success, Try}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
//Methods used to fetch values from application.conf

object ObtainConfigReference {
  private val config = ConfigFactory.load()
  private val logger = LoggerFactory.getLogger(classOf[ObtainConfigReference.type])

  def apply(confEntry: String): Option[Config] = Try(config.getConfig(confEntry)) match {
    case Failure(exception) => logger.error(s"Failed to retrieve config entry $confEntry for reason $exception"); None
    case Success(_) => Some(config)
  }
}
