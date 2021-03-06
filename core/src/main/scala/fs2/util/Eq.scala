package fs2
package util

private[fs2] sealed trait Eq[A,B] {
  def flip: Eq[B,A] = this.asInstanceOf[Eq[B,A]]
  def apply(a: A): B = Eq.subst[({type f[x] = x})#f, A, B](a)(this)
}

object Eq {
  private val _instance = new Eq[Unit,Unit] {}
  implicit def refl[A]: Eq[A,A] = _instance.asInstanceOf[Eq[A,A]]

  def subst[F[_],A,B](f: F[A])(implicit Eq: Eq[A,B]): F[B] =
    f.asInstanceOf[F[B]]

  def substStream[F[_],A,B](s: Stream[F,A])(implicit Eq: Eq[A,B]): Stream[F,B] =
    subst[({ type f[x] = Stream[F,x] })#f, A, B](s)

  def substPull[F[_],W,A,B](p: Pull[F,W,A])(implicit Eq: Eq[A,B]): Pull[F,W,B] =
    subst[({ type f[x] = Pull[F,W,x] })#f, A, B](p)

  def substHandler[F[_],A,B](h: Throwable => Stream[F,A])(implicit Eq: Eq[A,B]): Throwable => Stream[F,B] =
    subst[({ type f[x] = Throwable => Stream[F,x] })#f, A, B](h)
}
