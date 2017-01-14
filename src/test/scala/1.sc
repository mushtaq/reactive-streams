
val xs = List(1, 2, 3)

val ys = xs.view

val zs = ys.map(_ + 10)

val dd = zs.filter(_ > 5)

dd.foreach(println)

zs.toString()

dd.map(_.toString)
