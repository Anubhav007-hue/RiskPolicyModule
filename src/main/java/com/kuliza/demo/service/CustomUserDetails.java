package com.kuliza.demo.service;

import com.kuliza.demo.model.userDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    //  INSTANCE OF THE USERDETAILS ENTITY CLASS......................
    private userDetails userdetails;

//    private String user_name;
//
//    public String getUser_name() {
//        return user_name;
//    }
//
//    public void setUser_name(String user_name) {
//        this.user_name = user_name;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    private String password;

//    public void setUser_name(String user_name)
//    {
//        this.user_name=user_name;
//
//    }
//    public void setPassword(String password)
//    {
//        this.password=password;
//    }

    //CONSTRUTOR OF THE CUSTOMUSERDETAILS CLASS.......................
    public CustomUserDetails(userDetails userdetails) {
        this.userdetails = userdetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userdetails.getUser_password();
    }

    @Override
    public String getUsername() {
        return userdetails.getUser_name();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public userDetails giveUserDetail() {
        return userdetails;
    }


}
