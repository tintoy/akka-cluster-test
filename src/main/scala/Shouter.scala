import akka.actor.Actor

/**
 * Actor that shouts any strings it receives.
 */
class Shouter extends Actor {
  override def receive: Receive = {
    case (text: String) => println(s"${self.path.name} shouts '${text.toUpperCase}'")
    case _ => println("UNEXPECTED!")
  }
}
