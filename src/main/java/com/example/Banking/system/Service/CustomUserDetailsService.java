package com.example.Banking.system.Service;

import com.example.Banking.system.Model.CustomUser;
import com.example.Banking.system.Model.CustomUserDetails;
import com.example.Banking.system.Repository.CustomUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    CustomUserRepo customUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUser customUser = customUserRepo.findByUserName(username);

        if(customUser == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        return new CustomUserDetails(customUser);
    }
}
