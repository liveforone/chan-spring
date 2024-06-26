package chan.spring.demo.member.dto.response

import chan.spring.demo.member.domain.Role
import java.util.*

data class MemberInfo(
    val id: UUID,
    val auth: Role,
    val email: String
)
