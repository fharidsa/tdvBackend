package org.tdv.tdvbackend.web

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tdv.tdvbackend.web.dto.ApiHealthResponse

@RestController
@RequestMapping("/api/v1/health")
class HealthApiController(
    private val jdbcTemplate: JdbcTemplate,
) {

    @GetMapping
    fun health(): ApiHealthResponse {
        val database =
            runCatching {
                jdbcTemplate.queryForObject("SELECT 1", Int::class.java) == 1
            }.getOrDefault(false)
        return ApiHealthResponse(
            status = if (database) "UP" else "DEGRADED",
            database = if (database) "UP" else "DOWN",
        )
    }
}
