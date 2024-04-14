package chan.spring.demo.member.controller

import chan.spring.demo.logger
import chan.spring.demo.member.dto.request.*
import chan.spring.demo.exception.exception.MemberException
import chan.spring.demo.exception.message.MemberExceptionMessage
import chan.spring.demo.jwt.dto.JwtTokenInfo
import chan.spring.demo.member.controller.constant.MemberControllerConstant
import chan.spring.demo.member.controller.constant.MemberRequestHeaderConstant
import chan.spring.demo.member.controller.constant.MemberUrl
import chan.spring.demo.member.controller.response.MemberResponse
import chan.spring.demo.member.dto.response.MemberInfo
import chan.spring.demo.member.log.MemberControllerLog
import chan.spring.demo.member.service.command.MemberCommandService
import chan.spring.demo.member.service.query.MemberQueryService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@RestController
class MemberController
    @Autowired
    constructor(
        private val memberQueryService: MemberQueryService,
        private val memberCommandService: MemberCommandService
    ) {
        @GetMapping(MemberUrl.INFO)
        fun getMemberInfo(principal: Principal): ResponseEntity<MemberInfo> {
            val member = memberQueryService.getMemberById(id = UUID.fromString(principal.name))
            return MemberResponse.infoSuccess(member)
        }

        @PostMapping(MemberUrl.SIGNUP)
        fun signup(
            @RequestBody @Valid signupRequest: SignupRequest
        ): ResponseEntity<String> {
            memberCommandService.signup(signupRequest)
            logger().info(MemberControllerLog.SIGNUP_SUCCESS + signupRequest.email)

            return MemberResponse.signupSuccess()
        }

        @PostMapping(MemberUrl.LOGIN)
        fun login(
            @RequestBody @Valid loginRequest: LoginRequest,
            response: HttpServletResponse
        ): ResponseEntity<String> {
            val tokenInfo = memberCommandService.login(loginRequest)
            response.apply {
                addHeader(MemberControllerConstant.ACCESS_TOKEN, tokenInfo.accessToken)
                addHeader(MemberControllerConstant.REFRESH_TOKEN, tokenInfo.refreshToken)
                addHeader(MemberControllerConstant.MEMBER_ID, tokenInfo.id.toString())
            }
            logger().info(MemberControllerLog.LOGIN_SUCCESS + loginRequest.email)

            return MemberResponse.loginSuccess()
        }

        @PutMapping(MemberUrl.JWT_TOKEN_REISSUE)
        fun jwtTokenReissue(
            @RequestHeader(MemberRequestHeaderConstant.ID) id: String?,
            @RequestHeader(MemberRequestHeaderConstant.REFRESH_TOKEN) refreshToken: String?
        ): ResponseEntity<JwtTokenInfo> {
            if (id.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                throw MemberException(MemberExceptionMessage.TOKEN_REISSUE_HEADER_IS_NULL, "UNRELIABLE-MEMBER")
            }

            val memberId = UUID.fromString(id)
            val reissueJwtToken = memberCommandService.reissueJwtToken(memberId, refreshToken)
            logger().info(MemberControllerLog.JWT_TOKEN_REISSUE_SUCCESS + memberId)

            return ResponseEntity.ok(reissueJwtToken)
        }

        @PatchMapping(MemberUrl.UPDATE_PASSWORD)
        fun updatePassword(
            @RequestBody @Valid updatePassword: UpdatePassword,
            principal: Principal
        ): ResponseEntity<String> {
            val memberId = UUID.fromString(principal.name)
            memberCommandService.updatePassword(updatePassword, memberId)
            logger().info(MemberControllerLog.UPDATE_PW_SUCCESS + memberId)

            return MemberResponse.updatePwSuccess()
        }

        @PostMapping(MemberUrl.LOGOUT)
        fun logout(principal: Principal): ResponseEntity<String> {
            val memberId = UUID.fromString(principal.name)
            memberCommandService.logout(memberId)
            logger().info(MemberControllerLog.LOGOUT_SUCCESS + memberId)

            return MemberResponse.logOutSuccess()
        }

        @PostMapping(MemberUrl.RECOVERY_MEMBER)
        fun recoveryMember(
            @RequestBody @Valid recoveryRequest: RecoveryRequest
        ): ResponseEntity<String> {
            memberCommandService.recoveryMember(recoveryRequest)
            logger().info(MemberControllerLog.RECOVERY_SUCCESS + recoveryRequest.email)

            return MemberResponse.recoverySuccess()
        }

        @DeleteMapping(MemberUrl.WITHDRAW)
        fun withdraw(
            @RequestBody @Valid withdrawRequest: WithdrawRequest,
            principal: Principal
        ): ResponseEntity<String> {
            val memberId = UUID.fromString(principal.name)
            memberCommandService.withdraw(withdrawRequest, memberId)
            logger().info(MemberControllerLog.WITHDRAW_SUCCESS + memberId)

            return MemberResponse.withdrawSuccess()
        }
    }
