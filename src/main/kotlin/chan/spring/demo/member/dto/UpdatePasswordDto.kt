package chan.spring.demo.member.dto

import jakarta.validation.constraints.NotBlank

data class UpdatePasswordDto(
    @field:NotBlank(message = "새 비밀번호를 입력하세요.") var newPassword: String?,
    @field:NotBlank(message = "기존 비밀번호를 입력하세요.") var oldPassword: String?
)
