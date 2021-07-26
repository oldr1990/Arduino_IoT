package com.github.oldr1990.util

import android.util.Patterns
import java.util.regex.Pattern


fun String.isValidName(): Boolean {
    return trim().length > 2
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return Patterns.PHONE.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    val str = this
    var valid = true

    // Password policy check
    // Password should be minimum minimum 6 characters long
    if (str.length <= 6) {
        valid = false
    }
    // Password should contain at least one number
    var exp = ".*[0-9].*"
    var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
    var matcher = pattern.matcher(str)
    if (!matcher.matches()) {
        valid = false
    }

    // Password should contain at least one capital letter
    exp = ".*[A-Z].*"
    pattern = Pattern.compile(exp)
    matcher = pattern.matcher(str)
    if (!matcher.matches()) {
        valid = false
    }

    // Password should contain at least one small letter
    exp = ".*[a-z].*"
    pattern = Pattern.compile(exp)
    matcher = pattern.matcher(str)
    if (!matcher.matches()) {
        valid = false
    }
/*
    // Password should contain at least one special character
    // Allowed special characters : "~!@#$%^&*()-_=+|/,."';:{}[]<>?"
    exp = ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
    pattern = Pattern.compile(exp)
    matcher = pattern.matcher(str)
    if (!matcher.matches()) {
        valid = false
    }
*/
    return valid
}

