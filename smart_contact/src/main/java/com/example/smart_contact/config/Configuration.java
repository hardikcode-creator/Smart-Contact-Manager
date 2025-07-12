package com.example.smart_contact.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@org.springframework.context.annotation.Configuration
@EnableWebSecurity
public class Configuration {
    // You can override additional methods here if needed, such as configure(HttpSecurity http)

    @Autowired
    private UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
   .requestMatchers("/signup", "/register", "/login", "/css/**", "/js/**", "/img/**", "/styles/**").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/user/**").hasRole("USER")
    .anyRequest().authenticated()
)
.formLogin(form -> form .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/user/")
            )
.logout(logout -> logout
            .logoutUrl("/logout")             // optional: default is /logout
            .logoutSuccessUrl("/login?logout")     // redirect here after logout
            .invalidateHttpSession(true)      // clear session
               
        )
            .build();
        // return http
        //           .csrf(customizer->customizer.disable())
        //           .authorizeHttpRequests(request->request.requestMatchers("/signup", "/register", "/css/**", "/js/**").permitAll().anyRequest().authenticated())
        //           .httpBasic(Customizer.withDefaults())
        //           .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //           .build();
                    

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
        
    }




}
