package retrieve

/**
 * Created by hassan on 08/03/2014.
 */
sealed trait PredicateComposition {
  implicit class myPredicate[T] (leftPredicate : (T) => Boolean ) {
    def &&(rightPredicate : (T) => Boolean)  = (x:T) => leftPredicate(x) && rightPredicate(x)
    def ||(rightPredicate : (T) => Boolean ) = (x:T) => leftPredicate(x) || rightPredicate(x)
    def unary_! = (x:T) => ! leftPredicate(x)
  }
}

sealed trait Scalay extends PredicateComposition

package object scalay extends Scalay
