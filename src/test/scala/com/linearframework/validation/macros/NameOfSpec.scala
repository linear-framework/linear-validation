package com.linearframework.validation.macros

import com.linearframework.BaseSpec
import scala.language.experimental.macros

class NameOfSpec extends BaseSpec {

  private def nameOf(expr: Any): String = macro NameOf.nameOf

  case class Person(name: String, age: Int)

  "nameOf" should "return a String value for the name of the given expression" in {
    val var1 = "hello"
    nameOf(var1) should be ("var1")

    val steve = Person("Steve", 33)
    nameOf(steve) should be ("steve")
    nameOf(steve.name) should be ("name")
    nameOf(steve.age) should be ("age")
  }

}
