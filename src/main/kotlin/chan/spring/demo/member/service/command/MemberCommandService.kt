package chan.spring.demo.member.service.command

import chan.spring.demo.logger
import chan.spring.demo.member.exception.MemberException
import chan.spring.demo.member.exception.MemberExceptionMessage
import chan.spring.demo.global.util.isMatchPassword
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
        fun signup(signupDto: SignupDto) {
            with(signupDto) {
                Member.create(email!!, pw!!).also {
                    memberRepository.save(it)
                }
            }
        }

        fun login(loginDto: LoginDto): JwtTokenInfo {
            val authentication: Authentication =
                authenticationManagerBuilder
                    .`object`
                    .authenticate(UsernamePasswordAuthenticationToken(loginDto.email, loginDto.pw))

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
            updatePasswordDto: UpdatePasswordDto,
            id: UUID
        ) {
            with(updatePasswordDto) {
                memberRepository.findMemberById(id).also { it.updatePw(newPassword!!, oldPassword!!) }
            }
        }

        fun logout(id: UUID) {
            jwtTokenService.removeRefreshToken(id)
        }

        fun recoveryMember(recoveryDto: RecoveryDto) {
            with(recoveryDto) {
                memberRepository.findMemberByEmailIncludeWithdraw(email!!).also { it.recovery(pw!!) }
            }
        }

        fun withdraw(
            withdrawDto: WithdrawDto,
            id: UUID
        ) {
            memberRepository.findMemberById(id)
                .takeIf { isMatchPassword(withdrawDto.pw!!, it.pw) }
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
