import akka.actor.{Cancellable, Actor}
import concurrent.duration._

/**
 * Actor that tells shouter(s), via broadcaster, about cluster events.
 */
class Witness extends Actor {
  import context.dispatcher

  var cancellation: Cancellable = null

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()

    cancellation = context.system.scheduler.schedule(3.seconds, 3.seconds, self, DoBroadcast)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    if (cancellation != null) {
      cancellation.cancel()
      cancellation = null
    }

    super.postStop()
  }

  override def receive: Receive = {
    case DoBroadcast =>
      context.actorSelection("../broadcaster")
        .tell(s"Hello from ${self.path.name}", sender = self)
  }

  case object DoBroadcast
}
