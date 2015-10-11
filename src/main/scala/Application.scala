import akka.actor.{Props, ActorSystem}
import akka.routing.FromConfig
import concurrent.duration._
import io.StdIn
import scala.concurrent.Await

/**
 * The main application entry-point.
 */
object Application extends App {
  val clusterNode = ActorSystem("Cluster")

  clusterNode.actorOf(
    Props[Shouter],
    "shouter1"
  )
  clusterNode.actorOf(
    Props[Shouter],
    "shouter2"
  )

  clusterNode.actorOf(
    FromConfig.props(),
    "broadcaster"
  )

  clusterNode.actorOf(
    Props[Witness],
    "witness"
  )

  println("Press enter to terminate.")
  StdIn.readLine

  clusterNode.terminate()
  Await.result(clusterNode.whenTerminated, 5.seconds)
}
