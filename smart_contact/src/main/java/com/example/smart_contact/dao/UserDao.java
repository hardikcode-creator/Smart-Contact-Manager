package com.example.smart_contact.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smart_contact.entity.User;

public interface UserDao extends JpaRepository<User,Integer>{
    
    @Query("select u from User u where u.email=:email")
    public User getUserByUserName(@Param("email") String email);


}