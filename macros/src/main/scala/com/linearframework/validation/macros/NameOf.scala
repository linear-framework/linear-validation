package com.linearframework.validation.macros

import scala.annotation.tailrec
import scala.reflect.macros.blackbox

object NameOf {

  /**
   * Gets the name of the given variable
   */
  def nameOf(c: blackbox.Context)(expr: c.Expr[Any]): c.Expr[String] = {
    import c.universe._

    @tailrec def extract(tree: c.Tree): c.Name = tree match {
      case Ident(n) => n
      case Select(_, n) => n
      case Function(_, body) => extract(body)
      case Block(_, exp) => extract(exp)
      case Apply(func, _) => extract(func)
      case TypeApply(func, _) => extract(func)
      case _ => c.abort(c.enclosingPosition, s"Unsupported expression: $expr")
    }

    val name = extract(expr.tree).decodedName.toString
    reify {
      c.Expr[String] {
        Literal(Constant(name))
      }.splice
    }
  }

}
