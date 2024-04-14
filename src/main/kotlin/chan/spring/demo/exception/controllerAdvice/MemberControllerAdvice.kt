package chan.spring.demo.exception.controllerAdvice

import chan.spring.demo.exception.controllerAdvice.constant.MemberAdviceConstant
import chan.spring.demo.exception.exception.JwtCustomException
import chan.spring.demo.exception.exception.MemberException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class MemberControllerAdvice {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleLoginFail(): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(MemberAdviceConstant.LOGIN_FAIL)
    }

    @ExceptionHandler(MemberException::class)
    fun handleMemberException(memberException: MemberException): ResponseEntity<String> {
        return ResponseEntity
            .status(memberException.memberExceptionMessage.status)
            .body(memberException.message + memberException.memberIdentifier)
    }

    @ExceptionHandler(JwtCustomException::class)
    fun handleJwtCustomException(jwtCustomException: JwtCustomException): ResponseEntity<String> {
        return ResponseEntity
            .status(jwtCustomException.jwtExceptionMessage.status)
            .body(jwtCustomException.message)
    }
}
