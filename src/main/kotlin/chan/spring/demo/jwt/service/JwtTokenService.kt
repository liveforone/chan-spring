package chan.spring.demo.jwt.service

import chan.spring.demo.exception.exception.JwtCustomException
import chan.spring.demo.exception.message.JwtExceptionMessage
import chan.spring.demo.globalConfig.redis.RedisKeyValueTimeOut
import chan.spring.demo.globalConfig.redis.RedisRepository
import chan.spring.demo.jwt.cache.JwtCache
import chan.spring.demo.jwt.domain.RefreshToken
import chan.spring.demo.jwt.dto.JwtTokenInfo
import chan.spring.demo.jwt.filterLogic.JwtTokenProvider
import chan.spring.demo.jwt.log.JwtServiceLog
import chan.spring.demo.logger
import chan.spring.demo.member.domain.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class JwtTokenService
    @Autowired
    constructor(
        private val redisRepository: RedisRepository,
        private val jwtTokenProvider: JwtTokenProvider
    ) {
        fun getRefreshToken(id: UUID): RefreshToken =
            redisRepository.getByKey(JwtCache.REFRESH_TOKEN_NAME + id, RefreshToken::class.java)
                ?: throw JwtCustomException(JwtExceptionMessage.NOT_EXIST_REFRESH_TOKEN).apply {
                    logger().warn(JwtServiceLog.NOT_EXIST_REFRESH_TOKEN + id)
                }

        fun createRefreshToken(
            id: UUID,
            refreshToken: String
        ) {
            redisRepository.save(
                JwtCache.REFRESH_TOKEN_NAME + id,
                RefreshToken.create(id, refreshToken),
                RedisKeyValueTimeOut(15, TimeUnit.DAYS)
            )
        }

        fun reissueToken(
            id: UUID,
            refreshToken: String,
            role: Role
        ): JwtTokenInfo {
            jwtTokenProvider.validateToken(refreshToken)
            val key = JwtCache.REFRESH_TOKEN_NAME + id
            redisRepository.getByKey(key, RefreshToken::class.java)
                ?.let {
                    check(it.refreshToken.equals(refreshToken)) {
                        logger().warn(JwtServiceLog.UN_MATCH_REFRESH_TOKEN + id)
                        throw JwtCustomException(JwtExceptionMessage.UN_MATCH_REFRESH_TOKEN)
                    }
                    val reissueToken = jwtTokenProvider.reissueToken(id, role)
                    it.reissueRefreshToken(reissueToken.refreshToken)
                    redisRepository.save(key, it)
                    return reissueToken
                }
                ?: throw JwtCustomException(JwtExceptionMessage.NOT_EXIST_REFRESH_TOKEN).apply {
                    logger().warn(JwtServiceLog.NOT_EXIST_REFRESH_TOKEN + id)
                }
        }

        fun removeRefreshToken(id: UUID) {
            redisRepository.delete(JwtCache.REFRESH_TOKEN_NAME + id)
        }
    }
