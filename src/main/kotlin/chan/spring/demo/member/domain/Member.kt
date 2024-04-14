package chan.spring.demo.member.domain

import chan.spring.demo.converter.RoleConverter
import chan.spring.demo.exception.exception.MemberException
import chan.spring.demo.exception.message.MemberExceptionMessage
import chan.spring.demo.globalUtil.UUID_TYPE
import chan.spring.demo.globalUtil.createUUID
import chan.spring.demo.globalUtil.encodePassword
import chan.spring.demo.globalUtil.isMatchPassword
import chan.spring.demo.member.domain.constant.MemberConstant
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
class Member private constructor(
    @Id @Column(columnDefinition = UUID_TYPE) val id: UUID = createUUID(),
    @Convert(converter = RoleConverter::class) @Column(
        nullable = false,
        columnDefinition = MemberConstant.ROLE_TYPE
    ) var auth: Role,
    @Column(nullable = false, unique = true) val email: String,
    @Column(nullable = false, columnDefinition = MemberConstant.PW_TYPE) var pw: String
) : UserDetails {
    companion object {
        private fun findFitAuth(email: String) = if (email == MemberConstant.ADMIN_EMAIL) Role.ADMIN else Role.MEMBER

        fun create(
            email: String,
            pw: String
        ): Member {
            return Member(
                auth = findFitAuth(email),
                email = email,
                pw = encodePassword(pw)
            )
        }
    }

    fun isAdmin() = auth == Role.ADMIN

    fun updatePw(
        newPassword: String,
        oldPassword: String
    ) {
        require(isMatchPassword(oldPassword, pw)) {
            throw MemberException(MemberExceptionMessage.WRONG_PASSWORD, id.toString())
        }
        pw = encodePassword(newPassword)
    }

    fun withdraw() {
        this.auth = Role.WITHDRAW
    }

    fun recovery(inputPw: String) {
        require(
            isMatchPassword(inputPw, pw)
        ) { throw MemberException(MemberExceptionMessage.WRONG_PASSWORD, id.toString()) }
        this.auth = Role.MEMBER
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        arrayListOf<GrantedAuthority>(SimpleGrantedAuthority(auth.auth))

    override fun getUsername() = id.toString()

    override fun getPassword() = pw

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
