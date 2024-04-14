package chan.spring.demo.exception.exception

import chan.spring.demo.exception.message.JwtExceptionMessage

class JwtCustomException(val jwtExceptionMessage: JwtExceptionMessage) : RuntimeException(jwtExceptionMessage.message)
