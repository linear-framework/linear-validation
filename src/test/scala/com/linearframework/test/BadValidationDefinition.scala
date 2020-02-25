package com.linearframework.test

import com.linearframework.validation.Validation

case class BadValidationDefinition(name: String) extends Validation {
  override protected def constraints(): Unit = {
    "Name cannot be blank" validates "bad_field" requires { _.name.nonEmpty }
  }
}
