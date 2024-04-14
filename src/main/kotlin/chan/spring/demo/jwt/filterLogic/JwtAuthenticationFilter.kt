package chan.spring.demo.jwt.filterLogic

import com.fasterxml.jackson.databind.ObjectMapper
import chan.spring.demo.exception.exception.JwtCustomException
import chan.spring.demo.jwt.constant.JwtConstant
import chan.spring.demo.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter
    @Autowired
    constructor(
        private val jwtTokenProvider: JwtTokenProvider
    ) : GenericFilterBean() {
        override fun doFilter(
            request: ServletRequest,
            response: ServletResponse,
            chain: FilterChain
        ) {
            try {
                resolveToken(request as HttpServletRequest)?.let {
                    jwtTokenProvider.validateToken(it)
                    val authentication = jwtTokenProvider.getAuthentication(it)
                    SecurityContextHolder.getContext().authentication = authentication
                }
                chain.doFilter(request, response)
            } catch (e: JwtCustomException) {
                val httpResponse = response as HttpServletResponse
                httpResponse.status = e.jwtExceptionMessage.status
                httpResponse.contentType = MediaType.APPLICATION_JSON_VALUE
                val errorResponse = e.message
                logger().warn(e.message)
                val objectMapper = ObjectMapper()
                httpResponse.writer.write(objectMapper.writeValueAsString(errorResponse))
            }
        }

        private fun resolveToken(request: HttpServletRequest): String? {
            return request.getHeader(JwtConstant.HEADER)
                ?.takeIf { it.startsWith(JwtConstant.BEARER_TOKEN) }
                ?.substring(JwtConstant.TOKEN_SUB_INDEX)
        }
    }
