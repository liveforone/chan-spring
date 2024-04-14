package chan.spring.demo.member.service.command

import chan.spring.demo.member.domain.Member
import chan.spring.demo.member.domain.Role
import chan.spring.demo.member.repository.MemberCustomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService
    @Autowired
    constructor(
        private val memberRepository: MemberCustomRepository
    ) : UserDetailsService {
        override fun loadUserByUsername(email: String): UserDetails {
            val member = memberRepository.findMemberByEmail(email)
            return createUserDetails(member)
        }

        private fun createUserDetails(member: Member): UserDetails {
            return when (member.auth) {
                Role.ADMIN -> {
                    createAdmin(member)
                }
                else -> {
                    createMember(member)
                }
            }
        }

        private fun createAdmin(member: Member): UserDetails {
            return User.builder()
                .username(member.id.toString())
                .password(member.password)
                .roles(Role.ADMIN.name)
                .build()
        }

        private fun createMember(member: Member): UserDetails {
            return User.builder()
                .username(member.id.toString())
                .password(member.password)
                .roles(Role.MEMBER.name)
                .build()
        }
    }
