package chan.spring.demo.member.dto.request

import jakarta.validation.constraints.NotBlank

data class SignupDto(
    @field:NotBlank(message = "이메일을 입력하세요.") var email: String?,
    @field:NotBlank(message = "비밀번호를 입력하세요.") var pw: String?
)
