package foo

class Test {
  private val a = object {
      fun c() = 3
      fun b() = 2
  }

  fun doTest() : Boolean {
    return a.c() == 3 && a.b() == 2;
  }
}

fun box(): Boolean {
    return Test().doTest();
}