package com.example.smart_contact.helpder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession;



@Component
public class Message {

    private String message;
    private String type;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Message(String message , String type){
        this.message=message;
        this.type=type;
    }
    public Message(){

    }

    public void removeMessage(){
        try{

            HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
        }
        catch(Exception e){
            System.out.println("error removing message");
        }
    }

}
