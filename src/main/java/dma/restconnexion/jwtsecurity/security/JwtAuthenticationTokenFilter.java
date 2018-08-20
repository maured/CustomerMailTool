package dma.restconnexion.jwtsecurity.security;

import dma.restconnexion.jwtsecurity.model.JwtAuthenticationToken;
import logger.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter{

	@Autowired
    public JwtAuthenticationTokenFilter() {
        super("/api/**");
    } 

    @Override 
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) 
            throws IOException, ServletException
    {
        MyLogger logger = new MyLogger();
        String header = httpServletRequest.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
        {
            logger.errorLevel("JWT Token is missing");
            throw new RuntimeException("JWT Token is missing");
        }
        String authenticationToken = header.substring(7);
        JwtAuthenticationToken token = new JwtAuthenticationToken(authenticationToken);
        return getAuthenticationManager().authenticate(token);
    }

    @Override 
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
