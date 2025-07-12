package com.example.smart_contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.smart_contact.dao.ContactDao;
import com.example.smart_contact.dao.UserDao;
import com.example.smart_contact.entity.Contact;
import com.example.smart_contact.entity.User;
import com.example.smart_contact.helpder.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private ContactDao contactDao;

    @ModelAttribute
    public void addCommonData(Model model , Principal principal){
              String userName = principal.getName();
        User user = userDao.getUserByUserName(userName);
        model.addAttribute("user", user);
    }
    @RequestMapping("/")
    public String getDashboard(Model model , Principal principal){
        return "dashboard";
    }

    @RequestMapping("/add-contacts")
    public String getAddContactPage(Model model,HttpSession session){


        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "add-contact-form";
        
    }


    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute("contact") Contact contact , BindingResult result, Principal principal,Model model, HttpSession session , @RequestParam("profileImage") MultipartFile file){


    try{

            if(result.hasErrors()){
                System.out.println("Error:"+result.toString());
                model.addAttribute("contact", contact);
                return "add-contact-form";
            }

            // for uploading the image
            if(!file.isEmpty())
            {
                contact.setImage(file.getOriginalFilename());
              File saveFile =  new ClassPathResource("static/image").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

              Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);

            }



            String name = principal.getName();
            User user = this.userDao.getUserByUserName(name);
            session.setAttribute("message", new Message("Contact Added Successfully!!", "alert-success"));
            contact.setUser(user);
            this.contactDao.save(contact);
            model.addAttribute("contact", contact);
            
        }
        catch(Exception e){
            model.addAttribute("contact", contact);
            session.setAttribute("message", new Message("Error Adding contact:"+e.getMessage(), "alert-danger"));
        }
        return "add-contact-form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model , Principal principal){
        model.addAttribute("title", "All Contacts");
        String userName = principal.getName();
        User user = this.userDao.getUserByUserName(userName);
        int userId = user.getId();

        Pageable pageable = PageRequest.of(page,5);
        Page<Contact>contacts = this.contactDao.findContactsByUser(userId,pageable);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", contacts.getTotalPages());
        model.addAttribute("contacts", contacts);
        return "show-contacts";
    }

    @GetMapping("/contact/{Id}")
  public String showContactDetails(@PathVariable("Id") Integer cid, Model model , Principal principal)
  { 

     Optional<Contact>contactOptional = this.contactDao.findById(cid);
    Contact contact = contactOptional.get();

    String userName = principal.getName();
    User user = this.userDao.getUserByUserName(userName);
    if(user.getId()==contact.getUser().getId()){
           
         model.addAttribute("contact",contact);
         model.addAttribute("title", user.getName());
         
    }

   return "profile";

   
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid ,Model model, Principal principal , HttpSession session )
        {
            Optional<Contact> contactopOptional = this.contactDao.findById(cid);
            Contact contact = contactopOptional.get();
            String userName = principal.getName();
           
            if(userName.equals(contact.getUser().getEmail())){
                
                    // contact.setUser(null);
                    //  this.contactDao.delete(contact);
                    User user = this.userDao.getUserByUserName(userName);
                    user.getContacts().remove(contact);// This will check equals method for finding the contact to remove hence we need to overrride the contact method.
                    session.setAttribute("message", new Message("Contact Deleted Successfully!!","alert-success"));
                    
                    
            }
            else{
           session.setAttribute("message", new Message("Permission Denied for deleteing the contact","alert-danger"));
            }
            return "redirect:/user/show-contacts/0";
            
        }


        @PostMapping("/update-contact/{cid}")
        public String updateForm(@PathVariable("cid") Integer cid, Model model){


                model.addAttribute("title","Update Contact");

                Contact contact = this.contactDao.findById(cid).get();
                model.addAttribute("contact", contact);
                return "add-contact-form";


        }

        @GetMapping("/profile")
        public String getProfilePage(Model model){
            model.addAttribute("title", "Profile Page");
            return "user-profile";
        }
            

    
}
