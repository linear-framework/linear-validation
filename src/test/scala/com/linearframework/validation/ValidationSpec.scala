package com.linearframework.validation

import com.linearframework.BaseSpec
import com.linearframework.test.{BadValidationDefinition, CreateUserRequest}

class ValidationSpec extends BaseSpec {

  "Validation errors" should "fire for a bad request" in {
    val request = CreateUserRequest(email = "", password = "", passwordConfirmation = "asdf")

    a [ValidationException] should be thrownBy request.validated

    val e = request.validate.left.getOrElse(null)
    e.errors should be (Map(
      "email" -> List(
        "Email is required",
        "Email must be valid format"
      ),
      "password" -> List(
        "Password must be at least 8 characters"
      ),
      "passwordConfirmation" -> List(
        "Password and Confirmation must match"
      )
    ))
  }

  it should "fire for an email in use" in {
    val request = CreateUserRequest(email = "Gertrude@AOL.com", password = "password", passwordConfirmation = "password")

    a [ValidationException] should be thrownBy request.validated

    val e = request.validate.left.getOrElse(null)
    e.errors should be (Map(
      "email" -> List(
        "Email must not already exist in the system"
      )
    ))
  }

  it should "not fire for a valid request" in {
    val request = CreateUserRequest(email = "keith@linearframework.com", password = "password", passwordConfirmation = "password")

    val validatedResult = request.validated
    val validateResult = request.validate.getOrElse(null)

    request should be (validatedResult)
    request should be (validateResult)
  }

  "Validation" should "fail if the provided field name does not exist on the class being validated" in {
    an [IllegalStateException] should be thrownBy BadValidationDefinition("Steve").validated
  }

}

