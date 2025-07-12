package com.example.smart_contact.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.smart_contact.dao.UserDao;
import com.example.smart_contact.entity.User;


@Service
public class CustomUserDetailsService  implements UserDetailsService{


    @Autowired
    private UserDao userDao;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDao.getUserByUserName(username);
        if(user==null){
            throw new UsernameNotFoundException("Could not find user");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
    
    
}
