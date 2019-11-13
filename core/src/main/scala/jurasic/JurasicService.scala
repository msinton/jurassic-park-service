package jurasic

import scala.util.Random
import scala.util.Try

import cats.instances.either._
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.option._

object Service extends App {

  sealed trait Souvenir
  case object Toothbrush extends Souvenir
  case object TShirt extends Souvenir

  sealed trait Error
  case object Injured extends Error
  case object LostLimb extends Error
  case class Dead(cause: String) extends Error

  object AuditService {
    private var logs_ = List.empty[String]

    def log(visitorId: String, message: String): Unit =
      logs_ = logs_ :+ s"Visitor $visitorId: $message"

    def logs: List[String] = logs_
  }

  object TRexClient {
    def visit(visitorId: String): Unit =
      new Random().nextBoolean() match {
        case true => AuditService.log(visitorId, "visited T-Rex")
        case _    => throw new Exception("Not again!")
      }
  }

  object TriceratopsClient {
    def visit(visitorId: String): Option[Error] =
      new Random().nextInt(4) match {
        case 0 =>
          AuditService.log(visitorId, "visited triceratops")
          none
        case 1 => LostLimb.some
        case 2 => Injured.some
        case _ => Dead("Attacked by triceratops").some
      }
  }

  object VelociraptorsClient {
    def visit(visitorId: String): Either[Error, Souvenir] =
      new Random().nextInt(4) match {
        case 0 =>
          AuditService.log(visitorId, "visited velociraptors")
          Toothbrush.asRight
        case 1 => LostLimb.asLeft
        case 2 => Injured.asLeft
        case _ => Dead("Attacked by velociraptors").asLeft
      }
  }

  def visitAttractions(visitorId: String): Either[Error, List[Souvenir]] =
    for {
      tShirt <- Try(TRexClient.visit(visitorId)).toEither
        .leftMap(err => Dead(err.getMessage()))
        .as(TShirt)
      _ <- TriceratopsClient.visit(visitorId).toLeft(())
      toothbrush <- VelociraptorsClient.visit(visitorId)
    } yield List(tShirt, toothbrush)

  def parkAudit(visitorId: String, visitResult: Either[Error, List[Souvenir]]): Unit =
    visitResult.fold(
      error =>
        error match {
          case Dead(cause) => AuditService.log(visitorId, s"Died :( $cause")
          case x           => AuditService.log(visitorId, s"woopsie, nevermind: $x")
        },
      souvernirs => AuditService.log(visitorId, s"success with $souvernirs")
    )

  def visitService(visitorId: String): Unit = {
    val result = visitAttractions(visitorId)
    parkAudit(visitorId, result)
  }

  // Test
  (1 to 10)
    .map("Donald Gennaro " + _.toString)
    .foreach(visitService)

  AuditService.logs.foreach(println)

  /**
  * Ideas for learning more:
  * 1. AuditService returns IO[Unit] - try with and without Monad Transformers
  * 2. Use State monad for Random and Audit logs, so that is pure and testable
  * 3.
   **/

}
