package chan.spring.demo.member.controller.constant

object MemberUrl {
    const val SIGNUP = "/members/signup"
    const val LOGIN = "/members/login"
    const val INFO = "/members/info"
    const val JWT_TOKEN_REISSUE = "/auth/reissue"
    const val UPDATE_PASSWORD = "/members/update/password"
    const val LOGOUT = "/members/logout"
    const val RECOVERY_MEMBER = "/members/recovery"
    const val WITHDRAW = "/members/withdraw"
    const val PROHIBITION = "/prohibition"
}

object MemberRequestHeader {
    const val ID = "id"
    const val ACCESS_TOKEN = "access-token"
    const val REFRESH_TOKEN = "refresh-token"
    const val MEMBER_ID = "member-id"
}
