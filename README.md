retrieve
========

Spray Service for Creating Media Lists.

Interface for pulling lists from Freebase and other sources, and doing interesting things with them. Will keep fleshing it out. 

At the moment the codebase can fetch and demarhsall data from a few sources, it can get TV show episode lists and Oscar winners from Freebase. A simple DSL exists to create Freebase queries of interest, though I might get rid of it if the queries aren't too diverse in shape. The requests are made via Spray IO. The code is being developed and tested via the SBT console but a Spray Server also exists as eventually that is how the system should be interacted with.

I have only worked on this for a few hours =D


    scala> import retrieve.freebase._
    scala> val q = Awards() AND BetweenDates("2013")
    scala> val qq = FreeBaseQueries.movieQuery(q).asCase.await
    scala> FreeBaseQueries.movieQuery(q).asJson.await
    scala> FreeBaseQueries.movieQuery(q).asString.await
    
with named queries 

    scala> import retrieve.freebase._
    import retrieve.freebase._
    
    scala> println(CannesFestival("1990").awaitShow)
    1990-05       Wild at Heart
    1990-05-11    Dreams
    1989-11-15    The Little Mermaid
    1989-10-11    Longtime Companion
    1990-03-28    Cyrano de Bergerac
    1990-01-31    The Voice of the Moon
    1990-11-21    Hidden Agenda
    1990-05-06    Korczak
    1990-04-28    The Sting of Death
    1990          The Lunch Date
    1990-09-07    Everybody's Fine
    1989-12       Interrogation
    1990          Mother
    1990-09-07    Taxi Blues
    1989          Freeze Die Come to Life
    1990-04-06    On Tour

or 

    scala> val x = FreeBaseQueries.tVShowQuery(TVShowQuery("the walking dead")).asCase.await

    scala> x.seasons(3).show
    res37: scalaz.Cord =
    Season 3 Episodes : 16
    ----------------------------
    2012-10-28   Walk with Me
    2012-11-04   Killer Within
    2012-11-11   Say the Word
    2012-11-18   Hounded
    2012-11-25   When the Dead Come Knocking
    2012-12-02   Made to Suffer
    2013-02-10   The Suicide King
    2013-03-31   Welcome to the Tombs
    2012-10-14   Seed
    2012-10-21   Sick
    2013-02-17   Home
    2013-02-24   I Ain't a Judas
    2013-03-10   Arrow on the Doorpost
    2013-03-24   This Sorrowful Life
    2013-03-03   Clear
    2013-03-17   Prey
