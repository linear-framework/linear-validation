package com.linearframework.test

import com.linearframework.validation.Validation

case class CreateUserRequest(
  email: String,
  password: String,
  passwordConfirmation: String
) extends Validation {

  override protected def constraints(): Unit = {

    "Email is required" validates that(email) requires { _.email.nonEmpty }

    "Email must be valid format" validates that(email) requires { _.email.matches("^(.+)@(.+)$") }

    "Email must not already exist in the system" validates that(email) prohibits { req => UserService.userExists(req.email) }

    "Password must be at least 8 characters" validates that(password) prohibits { _.password.length < 8 }

    "Password and Confirmation must match" validates that(passwordConfirmation) requires { req => req.password == req.passwordConfirmation }

  }

}
