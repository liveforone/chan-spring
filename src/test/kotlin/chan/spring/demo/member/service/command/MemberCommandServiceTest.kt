package chan.spring.demo.member.service.command

import chan.spring.demo.exception.exception.JwtCustomException
import chan.spring.demo.exception.exception.MemberException
import chan.spring.demo.jwt.service.JwtTokenService
import chan.spring.demo.member.domain.Role
import chan.spring.demo.member.dto.request.*
import chan.spring.demo.member.service.query.MemberQueryService
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class MemberCommandServiceTest @Autowired constructor(
    private val entityManager: EntityManager,
    private val memberCommandService: MemberCommandService,
    private val memberQueryService: MemberQueryService,
    private val jwtTokenService: JwtTokenService
) {

    private fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @Transactional
    fun signup() {
        // given
        val email = "signup_test@gmail.com"
        val pw = "1234"
        val request = SignupDto(email, pw)

        // when
        memberCommandService.signup(request)
        flushAndClear()

        // then
        val loginDto = LoginDto(email, pw)
        val jwtTokenInfo = memberCommandService.login(loginDto)
        Assertions.assertThat(memberQueryService.getMemberById(jwtTokenInfo.id).auth)
            .isEqualTo(Role.MEMBER)
    }

    @Test
    @Transactional
    fun reissueJwtToken() {
        // given
        val email = "reissue_token_test@gmail.com"
        val pw = "1234"
        val request = SignupDto(email, pw)
        memberCommandService.signup(request)
        flushAndClear()
        val loginDto = LoginDto(email, pw)
        val jwtTokenInfo = memberCommandService.login(loginDto)

        // when
        val reissueJwtToken = memberCommandService.reissueJwtToken(jwtTokenInfo.id, jwtTokenInfo.refreshToken)
        flushAndClear()

        // then
        val jwtTokenInfo2 = memberCommandService.login(loginDto)
        Assertions.assertThat(reissueJwtToken.refreshToken.equals(jwtTokenInfo2.refreshToken)).isTrue()
    }

    @Test
    @Transactional
    fun updatePassword() {
        // given
        val email = "recovery_test@gmail.com"
        val pw = "1234"
        val request = SignupDto(email, pw)
        memberCommandService.signup(request)
        flushAndClear()
        val loginDto = LoginDto(email, pw)
        val id = memberCommandService.login(loginDto).id

        //when
        val newPw = "1111"
        val updatePasswordDto = UpdatePasswordDto(newPw, pw)
        memberCommandService.updatePassword(updatePasswordDto, id)
        flushAndClear()

        //then
        Assertions.assertThat(memberCommandService.login(LoginDto(email, newPw)).id).isEqualTo(id)
    }

    @Test
    @Transactional
    fun recoveryMember() {
        // given
        val email = "recovery_test@gmail.com"
        val pw = "1234"
        val request = SignupDto(email, pw)
        memberCommandService.signup(request)
        flushAndClear()
        val loginDto = LoginDto(email, pw)
        val id = memberCommandService.login(loginDto).id
        val withdrawDto = WithdrawDto(pw)
        memberCommandService.withdraw(withdrawDto, id)
        flushAndClear()

        // when
        memberCommandService.recoveryMember(RecoveryDto(email, pw))
        flushAndClear()

        // then
        Assertions.assertThat(memberQueryService.getMemberById(id)).isNotNull
    }

    @Test
    @Transactional
    fun withdraw() {
        // given
        val email = "withdraw_test@gmail.com"
        val pw = "1234"
        val request = SignupDto(email, pw)
        memberCommandService.signup(request)
        flushAndClear()
        val loginDto = LoginDto(email, pw)
        val id = memberCommandService.login(loginDto).id

        // when
        val withdrawDto = WithdrawDto(pw)
        memberCommandService.withdraw(withdrawDto, id)
        flushAndClear()

        // then
        Assertions.assertThatThrownBy { jwtTokenService.getRefreshToken(id) }
            .isInstanceOf(JwtCustomException::class.java)
        Assertions.assertThatThrownBy { (memberQueryService.getMemberById(id)) }
            .isInstanceOf(MemberException::class.java)
    }
}
