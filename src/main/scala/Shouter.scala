import akka.actor.Actor

/**
 * Actor that shouts any strings it receives.
 */
class Shouter(val nodeId: Int) extends Actor {
  import Shouter._

  /**
   * Called when the actor receives a message.
   */
  override def receive = {
    case Shout(text: String) =>
      println(s"${self.path.name} on node ${nodeId} shouts '${text.toUpperCase}'")
    case _ =>
      println("UNEXPECTED MESSAGE!")
  }
}

/**
 * Statics for `Shouter`.
 */
object Shouter {
  /**
   * A message for a `Shouter` to shout.
   * @param message The message.
   */
  case class Shout(message: String)
}
