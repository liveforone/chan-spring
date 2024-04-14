package chan.spring.demo.exception.exception

import chan.spring.demo.exception.message.MemberExceptionMessage

class MemberException(
    val memberExceptionMessage: MemberExceptionMessage,
    val memberIdentifier: String?
) : RuntimeException(memberExceptionMessage.message)
