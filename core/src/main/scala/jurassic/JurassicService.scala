package jurasic

import scala.util.Random
import scala.util.Try
import scala.util.chaining._

import cats.instances.either._
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.option._

/**
  * Simulating a microservice oriented Jurassic Park Service!
  *
  * The attractions have been implemented as individual services which we
  * provide the clients for.
  *
  * ## Motivations
  * - Use library cats to learn some simple functionality for writing lovely scala
  * - in particular: `asLeft`, `asRight`, `some`, `none`, `leftMap` and `as`
  * - practice for comprehension if desired
  * - try scala 2.13 `pipe` and `tap` perhaps?
  * - extension exercises for Monad Transformers and State Monad
  *
  * ## Requirements
  * - Create a service that accepts a `visitorId: String` and calls to 3 attractions:
  *
  *   - `TRexClient.visit: Unit`, but must throw an exception when visitor dies
  *   - `TriceratopsClient.visit: Option[Error]`
  *   - `VelociraptorsClient.visit: Either[Error, Souvenir]`
  *
  * - Error is one of `Injured`, `LostLimb` or `Dead(cause: String)`
  *
  * - Make up your own souvenirs!
  *
  * - After a successful visit to the T-Rex our jurassic service provides the visitor with a T-Shirt (which is a Souvenir)
  *
  * - When visitor has an accident (Error) no more attractions should be visited
  *
  * - Our service defines `visitAttractions(visitorId: String): Either[Error, List[Souvenir]]`
  *
  * ### Hints
  * - You can use Random.nextInt(n) to simulate getting a random service response
  * - `import cats._, cats.implicits._`
  *
  * ### Extension
  * - Add an AuditService that is called to log at each client on a successful visit
  * - A specific log when the visitor dies
  * - A specific log when the visitor has an accident but has not died
  *
  * ## Ideas for learning more
  * 1. AuditService returns IO\[Unit] - try with and without Monad Transformers
  * 2. Use State monad for Random and Audit logs, so that is pure and testable (can you write tests currently?)
  */
object JurassicService extends App {

  sealed trait Souvenir
  case object Toothbrush extends Souvenir
  case object Lamp extends Souvenir
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
      (new Random().nextInt(5) match {
        case 0 => Toothbrush.asRight
        case 1 => Lamp.asRight
        case 2 => LostLimb.asLeft
        case 3 => Injured.asLeft
        case _ => Dead("Attacked by velociraptors").asLeft
      }).map(_.tap(x => AuditService.log(visitorId, s"visited velociraptors and got $x")))
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
    visitResult
      .leftMap(_ match {
        case Dead(cause) => AuditService.log(visitorId, s"Died :( $cause")
        case x           => AuditService.log(visitorId, s"woopsie, nevermind: $x")
      })
      .foreach(souvernirs => AuditService.log(visitorId, s"success with $souvernirs"))

  def visitService(visitorId: String): Unit =
    visitAttractions(visitorId)
      .pipe(parkAudit(visitorId, _))

  // Tests
  (1 to 10)
    .map("Donald Gennaro " + _.toString)
    .foreach(visitService)

  AuditService.logs.foreach(println)

}
