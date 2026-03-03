package com.jash.blog.security;

import com.jash.blog.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = extractToken(request);
            if(token != null){
                UserDetails userDetails = authenticationService.validateToken(token);

               UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                       userDetails,
                       null,
                       userDetails.getAuthorities()
               );
               /*
                Think of SecurityContextHolder as a bulletin board for the current request thread.
                Pinning the user's identity here means anywhere in your app — controllers, services — can ask "who is logged in?"
                and get the answer instantly. Without this line, Spring treats the request as anonymous even with a valid token.
                */
               SecurityContextHolder.getContext().setAuthentication(authentication);
                /*
                  Stamps the user's UUID directly onto the request object so controllers can grab it with
                  request.getAttribute("userId") instead of manually digging through SecurityContextHolder every time.
                 */
               if(userDetails instanceof BlogUserDetails) {
                   request.setAttribute("userId",((BlogUserDetails)userDetails).getId());
               }
            }
        }catch(Exception e){
            //do not throw exception , just don't authenticate the user
            log.warn("Received invalid auth token!!!!");
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        //Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxxxx.yyyyy
        //here "Bearer " is remove and token is extracted from index 7
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
