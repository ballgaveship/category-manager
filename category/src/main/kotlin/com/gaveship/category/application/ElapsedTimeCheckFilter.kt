package com.gaveship.category.application

import mu.KotlinLogging
import org.apache.catalina.connector.RequestFacade
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.Duration
import java.time.Instant
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest

@Component
@WebFilter("/*")
class ElapsedTimeCheckFilter : Filter {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
        val start = Instant.now()
        try {
            chain.doFilter(req, resp)
        } finally {
            val finish = Instant.now()
            val time: Long = Duration.between(start, finish).toMillis()
            log.trace("[elapsed-check-filter]uri: [${(req as RequestFacade).method}]${(req as HttpServletRequest).requestURI}, elapsedTime: ${time}ms")
        }
    }
}