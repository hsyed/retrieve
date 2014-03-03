retrieve
========

Spray Service for Creating Media Lists.

Interface for pulling lists from Freebase and other sources, and doing interesting things with them. Will keep fleshing it out. 

    scala> import retrieve.freebase._
    scala> val q = Awards() AND BetweenDates("2013")
    scala> val qq = FreeBaseQueries.movieQuery(q).asCase.await
    scala> FreeBaseQueries.movieQuery(q).asJson.await
    scala> FreeBaseQueries.movieQuery(q).asString.await

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
