package iuh.fit.se.filter;


import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import iuh.fit.se.util.TokenContextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThreadLocalCleanupFilter extends OncePerRequestFilter {
    
    private final TokenContextUtil tokenContextUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
        throws ServletException, IOException {
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            tokenContextUtil.clear();
            log.debug("ThreadLocal context cleaned up");
        }
    }
}