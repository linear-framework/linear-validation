package com.linearframework.validation

import com.linearframework.validation.macros.NameOf

/**
 * When mixed in with a class, provides a mechanism for DSL-based validation.
 * <br/><br/>
 * Example usage:
 * {{{
 * case class Person(name: String, age: Int) extends Validation {
 *   override protected def constraints(): Unit = {
 *
 *     "Name cannot be blank" validates that(name) requires {
 *       _.name.nonEmpty
 *     }
 *
 *     "Age must be 18+" validates that(age) prohibits {
 *       _.age < 18
 *     }
 *
 *     "Anyone named 'Gertrude' should be at least 90 years old" validates that(age) requires { person =>
 *       person.name == "Gertrude" && person.age >= 90
 *     }
 *
 *   }
 * }
 * }}}
 */
trait Validation {
  import scala.language.implicitConversions
  import scala.language.experimental.macros

  /** The key used to identify a field undergoing validation */
  protected type Key = String

  /** The test used to determine whether or not a given object is valid */
  protected type Predicate[T] = T => Boolean

  /** The rule used to run validation against an object, returning a message if validation failed */
  protected type Rule[T] = T => Option[String]

  /** The record format for rules */
  protected type KeyedRule[T] = (Key, Rule[T])

  /** The runtime type of the class undergoing validation */
  protected type This = this.type

  /**
   * Contains the name of a field undergoing validation
   * @param name the name of the field being validated
   */
  protected class Field private[validation](val name: String)

  @transient private var validationRules: List[KeyedRule[This]] = List()

  /**
   * Provides the name of a field undergoing validation
   */
  def that(expr: Any): String = macro NameOf.nameOf

  protected implicit def stringToField(field: String): Field = {
    val fields = this.getClass.getDeclaredFields.map(_.getName) ++ this.getClass.getFields.map(_.getName)
    if (!fields.contains(field)) {
      throw new IllegalStateException(s"Field [$field] is not a member of [$getClass]")
    }
    new Field(field)
  }

  protected implicit class RequirementImplicits(message: String) {

    /**
     * Sets the field undergoing validation
     * @param field the field undergoing validation
     */
    def validates(field: Field): RequirementBuilder = new RequirementBuilder(message, field)

  }

  protected class RequirementBuilder private[validation](message: String, field: Field) {

    /**
     * Sets a requirement on this object.
     * If the given behavior is satisfied, this requirement is considered valid.
     * @param test The test required to pass validation
     * @see [[com.linearframework.validation.Validation.RequirementBuilder#prohibits]]
     */
    def requires(test: Predicate[This]): Unit = {
      val key: Key = field.name
      val rule: Rule[This] = (obj: This) => if (test(obj)) None else Some(message)
      val keyedRule = key -> rule
      validationRules = validationRules :+ keyedRule
    }

    /**
     * Sets a requirement on this object.
     * If the given behavior is satisfied, this requirement is considered invalid.
     * @param test The test required to fail validation
     * @see [[com.linearframework.validation.Validation.RequirementBuilder#requires]]
     */
    def prohibits(test: Predicate[This]): Unit = {
      val key: Key = field.name
      val rule: Rule[This] = (obj: This) => if (test(obj)) Some(message) else None
      val keyedRule = key -> rule
      validationRules = validationRules :+ keyedRule
    }
  }

  /**
   * Defines the validation constraints on this object.  Implementations of this method should only
   * contain calls to `"message".validates()`, and should otherwise not have any side effects.
   */
  protected def constraints(): Unit

  /**
   * Returns this object if it passes validation.
   * @throws com.linearframework.validation.ValidationException if this object did not pass validation
   * @see [[com.linearframework.validation.Validation#validate]]
   */
  def validated: This = {
    validate match {
      case Left(e) => throw e
      case _ => this
    }
  }

  /**
   * Returns either:<br/>
   *  - this object if it passes validation; or <br/>
   *  - an exception object if this object fails validation
   * @see [[com.linearframework.validation.Validation#validated]]
   */
  def validate: Either[ValidationException, This] = {
    validationRules = List()
    constraints()

    val validationErrors =
      validationRules
        .map { case (key, rule) => key -> rule(this) }
        .filter { case (_, result) => result.isDefined }
        .map { case (key, result) => key -> result.get}

    if (validationErrors.nonEmpty) {
      val groupedErrors: Map[String, List[String]] =
        validationErrors
          .groupBy { case (key, _) => key }
          .map { case (key: String, group: List[(String, String)]) =>
            key -> group.map(_._2)
          }
      Left(new ValidationException(groupedErrors))
    }
    else {
      Right(this)
    }
  }

}
