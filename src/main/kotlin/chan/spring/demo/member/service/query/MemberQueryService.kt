package chan.spring.demo.member.service.query

import chan.spring.demo.member.repository.MemberCustomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberQueryService
    @Autowired
    constructor(
        private val memberRepository: MemberCustomRepository
    ) {
        fun getMemberById(id: UUID) = memberRepository.findMemberInfoById(id)
    }
