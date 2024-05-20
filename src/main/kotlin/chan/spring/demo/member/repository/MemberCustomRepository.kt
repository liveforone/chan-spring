package chan.spring.demo.member.repository

import chan.spring.demo.member.domain.Member
import chan.spring.demo.member.domain.Role
import chan.spring.demo.member.domain.vo.MemberInfo
import java.util.*

interface MemberCustomRepository {
    fun findMemberByEmail(email: String): Member

    fun findMemberById(id: UUID): Member

    fun findMemberByEmailIncludeWithdraw(email: String): Member

    fun findMemberInfoById(id: UUID): MemberInfo

    fun findAuthById(id: UUID): Role
}
