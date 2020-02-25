package com.linearframework.validation

/**
 * The result of a failed validation attempt.
 * @param errors the list of validation errors, keyed on their that name
 */
class ValidationException(val errors: Map[String, List[String]]) extends RuntimeException() {
  override def getMessage: String = {
    errors.map { case (field, errors: List[String]) =>
      s"$field -> [${errors.mkString(", ")}]"
    }
    .mkString(";  ")
  }
}