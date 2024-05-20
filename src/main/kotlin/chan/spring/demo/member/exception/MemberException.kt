package chan.spring.demo.member.exception

class MemberException(
    val memberExceptionMessage: MemberExceptionMessage,
    val memberIdentifier: String?
) : RuntimeException(memberExceptionMessage.message)
