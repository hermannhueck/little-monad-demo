package tutorial.libMyCatsForExamples01To09

def compute[A, B, F[_]: Monad](fa: F[A], fb: F[B]): F[(A, B)] =
  for
    i1 <- fa
    i2 <- fb
  yield (i1, i2)

def compute2[A, B, F[_]](fa: F[A], fb: F[B])(given Monad[F]): F[(A, B)] =
  fa.flatMap { i1 =>
    fb.map { i2 =>
      (i1, i2)
    }
  }

