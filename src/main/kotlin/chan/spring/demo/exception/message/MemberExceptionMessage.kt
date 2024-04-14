package chan.spring.demo.exception.message

enum class MemberExceptionMessage(val status: Int, val message: String) {
    WRONG_PASSWORD(400, "비밀번호를 틀렸습니다. 회원식별자 : "),
    MEMBER_IS_NULL(404, "회원이 존재하지 않습니다. 회원식별자 : "),
    TOKEN_REISSUE_HEADER_IS_NULL(404, "토큰 갱신 헤더가 비어있습니다.")
}
