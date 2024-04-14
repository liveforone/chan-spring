package chan.spring.demo.member.dto.request

import jakarta.validation.constraints.NotBlank

data class WithdrawRequest(
    @field:NotBlank(message = "비밀번호를 입력하세요.") val pw: String?
)
