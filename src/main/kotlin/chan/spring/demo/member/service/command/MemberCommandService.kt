package chan.spring.demo.member.service.command

import chan.spring.demo.logger
import chan.spring.demo.exception.exception.MemberException
import chan.spring.demo.exception.message.MemberExceptionMessage
import chan.spring.demo.globalUtil.isMatchPassword
import chan.spring.demo.jwt.dto.JwtTokenInfo
import chan.spring.demo.jwt.filterLogic.JwtTokenProvider
import chan.spring.demo.jwt.service.JwtTokenService
import chan.spring.demo.member.domain.Member
import chan.spring.demo.member.dto.request.*
import chan.spring.demo.member.log.MemberServiceLog
import chan.spring.demo.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class MemberCommandService
    @Autowired
    constructor(
        private val memberRepository: MemberRepository,
        private val authenticationManagerBuilder: AuthenticationManagerBuilder,
        private val jwtTokenProvider: JwtTokenProvider,
        private val jwtTokenService: JwtTokenService
    ) {
        fun signup(signupRequest: SignupRequest) {
            with(signupRequest) {
                Member.create(email!!, pw!!).also {
                    memberRepository.save(it)
                }
            }
        }

        fun login(loginRequest: LoginRequest): JwtTokenInfo {
            val authentication: Authentication =
                authenticationManagerBuilder
                    .`object`
                    .authenticate(UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.pw))

            return jwtTokenProvider.generateToken(authentication).also {
                jwtTokenService.createRefreshToken(it.id, it.refreshToken)
            }
        }

        fun reissueJwtToken(
            id: UUID,
            refreshToken: String
        ): JwtTokenInfo {
            val auth = memberRepository.findAuthById(id)
            return jwtTokenService.reissueToken(id, refreshToken, auth)
        }

        fun updatePassword(
            updatePassword: UpdatePassword,
            id: UUID
        ) {
            with(updatePassword) {
                memberRepository.findMemberById(id).also { it.updatePw(newPassword!!, oldPassword!!) }
            }
        }

        fun logout(id: UUID) {
            jwtTokenService.removeRefreshToken(id)
        }

        fun recoveryMember(recoveryRequest: RecoveryRequest) {
            with(recoveryRequest) {
                memberRepository.findMemberByEmailIncludeWithdraw(email!!).also { it.recovery(pw!!) }
            }
        }

        fun withdraw(
            withdrawRequest: WithdrawRequest,
            id: UUID
        ) {
            memberRepository.findMemberById(id)
                .takeIf { isMatchPassword(withdrawRequest.pw!!, it.pw) }
                ?.also {
                    it.withdraw()
                    jwtTokenService.removeRefreshToken(id)
                }
                ?: run {
                    logger().warn(MemberServiceLog.WRONG_PW + id)
                    throw MemberException(MemberExceptionMessage.WRONG_PASSWORD, id.toString())
                }
        }
    }
