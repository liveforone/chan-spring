package chan.spring.demo.global.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun encodePassword(password: String): String = BCryptPasswordEncoder().encode(password)

fun isMatchPassword(
    password: String,
    originalEncodedPassword: String
) = BCryptPasswordEncoder().matches(password, originalEncodedPassword)
