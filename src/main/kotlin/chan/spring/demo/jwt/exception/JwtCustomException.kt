package chan.spring.demo.jwt.exception

class JwtCustomException(val jwtExceptionMessage: JwtExceptionMessage) : RuntimeException(jwtExceptionMessage.message)
