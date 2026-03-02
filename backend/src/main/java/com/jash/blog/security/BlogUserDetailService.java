package com.jash.blog.security;

import com.jash.blog.domain.entities.User;
import com.jash.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
//the bridge between Spring Security and your UserRepository
public class BlogUserDetailService implements UserDetailsService { // connects data layer and security with authentication

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new BlogUserDetails(user);
    }
}
