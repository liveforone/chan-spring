package chan.spring.demo.member.domain

import chan.spring.demo.global.util.isMatchPassword
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MemberTest {
    @Test
    fun isAdmin() {
        // given
        val email = "admin@gmail.com"
        val pw = "1234"

        // when
        val admin = Member.create(email, pw)

        // then
        Assertions.assertThat(admin.isAdmin()).isTrue()
    }

    @Test
    fun updatePw() {
        // given
        val email = "pw_test@gmail.com"
        val pw = "1234"
        val member = Member.create(email, pw)

        // when
        val updatedPw = "1111"
        member.updatePw(updatedPw, pw)

        // then
        Assertions.assertThat(isMatchPassword(updatedPw, member.pw)).isTrue()
    }

    @Test
    fun withdraw() {
        // given
        val email = "withdraw_test@gmail.com"
        val pw = "1234"
        val member = Member.create(email, pw)

        // when
        member.withdraw()

        // then
        Assertions.assertThat(member.auth).isEqualTo(Role.WITHDRAW)
    }

    @Test
    fun recovery() {
        // given
        val email = "recovery_test@gmail.com"
        val pw = "1234"
        val member = Member.create(email, pw)
        member.withdraw()

        // when
        member.recovery(pw)

        // then
        Assertions.assertThat(member.auth).isEqualTo(Role.MEMBER)
    }
}
