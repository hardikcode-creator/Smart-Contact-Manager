package com.example.smart_contact.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.smart_contact.dao.UserDao;
import com.example.smart_contact.entity.User;
import com.example.smart_contact.helpder.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class Homecontroller {


    @Autowired
    public UserDao userDao;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
                
           
            if (principal != null) {
                
                User user = userDao.getUserByUserName(principal.getName());
                model.addAttribute("user", user);
            }
    }
   
    @GetMapping("/")
    public String getHome(Model model) {
        model.addAttribute("title","Home");
       return "home";
    }

    @GetMapping("/signup")
    public String getSignup(Model model, Principal principal){

        
            if (principal != null) {
        // User is already logged in, redirect to user dashboard (or wherever)
        return "redirect:/user/";
    }

        model.addAttribute("title","Signup");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user  ,BindingResult result ,@RequestParam(value= "agreement" , defaultValue = "false") boolean agreement , Model model , HttpSession session  ){

        try{

                if(!agreement){
                    System.out.println("You have not agreed t&c");
                    throw new Exception("You have not agreed t&c");
                }
                if(result.hasErrors()){
                    System.out.println("Error:"+result.toString());
                    model.addAttribute("user", user);
                    return "signup";
                }
                System.out.println(agreement);
                System.out.println(user);
                user.setRole("ROLE_USER");
                user.setEnabled(true);
                user.setPassword(encoder.encode(user.getPassword()));
                this.userDao.save(user);
                model.addAttribute("user", user);
                session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
        }

        catch(Exception e){
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message",new Message("Something went wrong :"+e.getMessage(),"alert-danger"));
        }
        return "signup";
    }
    

    @GetMapping("/login")
    public String getLoginPage(Model model){
        model.addAttribute("title", "User Login");
        return "loginPage";
    }
    
}
