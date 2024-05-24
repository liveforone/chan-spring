package chan.spring.demo.member.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import chan.spring.demo.member.exception.MemberException
import chan.spring.demo.member.exception.MemberExceptionMessage
import chan.spring.demo.member.domain.Member
import chan.spring.demo.member.domain.QMember
import chan.spring.demo.member.domain.Role
import chan.spring.demo.member.dto.response.MemberInfo
import java.util.*

class MemberCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val member: QMember = QMember.member
) : MemberCustomRepository {
    override fun findMemberByEmail(email: String): Member {
        return jpaQueryFactory.selectFrom(member)
            .where(member.email.eq(email).and(member.auth.ne(Role.WITHDRAW)))
            .fetchOne() ?: throw MemberException(MemberExceptionMessage.MEMBER_IS_NULL, email)
    }

    override fun findMemberById(id: UUID): Member {
        return jpaQueryFactory.selectFrom(member)
            .where(member.id.eq(id).and(member.auth.ne(Role.WITHDRAW)))
            .fetchOne() ?: throw MemberException(MemberExceptionMessage.MEMBER_IS_NULL, id.toString())
    }

    override fun findMemberByEmailIncludeWithdraw(email: String): Member {
        return jpaQueryFactory.selectFrom(member)
            .where(member.email.eq(email))
            .fetchOne() ?: throw MemberException(MemberExceptionMessage.MEMBER_IS_NULL, email)
    }

    override fun findMemberInfoById(id: UUID): MemberInfo {
        return jpaQueryFactory.select(
            Projections.constructor(
                MemberInfo::class.java,
                member.id,
                member.auth,
                member.email
            )
        )
            .from(member)
            .where(member.id.eq(id).and(member.auth.ne(Role.WITHDRAW)))
            .fetchOne() ?: throw MemberException(MemberExceptionMessage.MEMBER_IS_NULL, id.toString())
    }

    override fun findAuthById(id: UUID): Role {
        return jpaQueryFactory.select(member.auth)
            .from(member)
            .where(member.id.eq(id).and(member.auth.ne(Role.WITHDRAW)))
            .fetchOne() ?: throw MemberException(MemberExceptionMessage.MEMBER_IS_NULL, id.toString())
    }
}
