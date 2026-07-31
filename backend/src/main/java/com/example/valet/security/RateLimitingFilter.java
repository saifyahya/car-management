package com.example.valet.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String PUBLIC_PATH_PREFIX = "/public/";

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> publicBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> apiBuckets = new ConcurrentHashMap<>();



    private Bucket createBucket(long capacity, long refillTokens, Duration refillDuration) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(refillTokens, refillDuration)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createLoginBucket() {
        return createBucket(5, 5, Duration.ofMinutes(1));
    }

    private Bucket createPublicBucket() {
        return createBucket(30, 30, Duration.ofMinutes(1));
    }

    private Bucket createApiBucket() {
        return createBucket(120, 120, Duration.ofMinutes(1));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIp(request);

        Bucket bucket = resolveBucket(path, clientIp);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        writeRateLimitResponse(response);
    }

    private Bucket resolveBucket(String path, String clientIp) {
        if (LOGIN_PATH.equals(path)) {
            return loginBuckets.computeIfAbsent(
                    clientIp,
                    ignored -> createLoginBucket()
            );
        }

        if (path.startsWith(PUBLIC_PATH_PREFIX)) {
            return publicBuckets.computeIfAbsent(
                    clientIp,
                    ignored -> createPublicBucket()
            );
        }

        return apiBuckets.computeIfAbsent(
                clientIp,
                ignored -> createApiBucket()
            );
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletResponse response)
            throws IOException {

        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", "60");

        response.getWriter().write("""
        {
          "status": 429,
          "message": "Too many requests. Please try again later."
        }
        """);
    }
}