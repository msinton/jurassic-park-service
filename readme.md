
  # Jurassic Park Service

  Simulating a microservice oriented Jurassic Park Service!
  
  The attractions have been implemented as individual services which we
  provide the clients for.
  
  ## Motivations
  - Use library cats to learn some simple functionality for writing lovely scala
  - in particular: `asLeft`, `asRight`, `some`, `none`, `leftMap` and `as`
  - practice for comprehension if desired
  - try scala 2.13 `pipe` and `tap` perhaps?
  - extension exercises for Monad Transformers and State Monad
  
  ## Requirements
  - Create a service that accepts a `visitorId: String` and calls to 3 attractions:
  
    - `TRexClient.visit: Unit`, but must throw an exception when visitor dies
    - `TriceratopsClient.visit: Option[Error]`
    - `VelociraptorsClient.visit: Either[Error, Souvenir]`
  
  - Error is one of `Injured`, `LostLimb` or `Dead(cause: String)`

  - Make up your own souvenirs!
  
  - After a successful visit to the T-Rex our jurassic service provides the visitor with a T-Shirt (which is a Souvenir)

  - When visitor has an accident (Error) no more attractions should be visited

  - Our service defines `visitAttractions(visitorId: String): Either[Error, List[Souvenir]]`
  
  ### Hints
  - You can use Random.nextInt(n) to simulate getting a random service response
  - `import cats._, cats.implicits._`
  
  ### Extension
  - Add an AuditService that is called to log at each client on a successful visit
  - A specific log when the visitor dies
  - A specific log when the visitor has an accident but has not died
  
  ## Ideas for learning more
  1. AuditService returns IO\[Unit] - try with and without Monad Transformers
  2. Use State monad for Random and Audit logs, so that is pure and testable (can you write tests currently?)