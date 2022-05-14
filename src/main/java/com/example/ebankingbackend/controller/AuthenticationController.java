package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.model.User;
import com.example.ebankingbackend.repository.UserRepository;
import com.example.ebankingbackend.service.JwtUserDetailsService;
import com.example.ebankingbackend.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam("username") String username,
                                       @RequestParam("password") String password) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username
                    , password));
            if (auth.isAuthenticated()) {
                logger.info("Logged In");
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String token = jwtTokenUtil.generateToken(userDetails);
                responseMap.put("code", "success");
                responseMap.put("message", "Logged In");
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("code", "error");
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            responseMap.put("code", "error");
            responseMap.put("message", "User is disabled");
            return ResponseEntity.status(500).body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("code", "error");
            responseMap.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("code", "error");
            responseMap.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(responseMap);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      @RequestParam("email") String email) {
        Map<String, Object> responseMap = new HashMap<>();
        if (userRepository.findUserByUsername(username).isEmpty()) {
            User user = new User();
            user.setUserId("");
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setUsername(username);
            user.setEmail(email);
            UserDetails userDetails = userDetailsService.createUserDetails(username, user.getPassword());
            String token = jwtTokenUtil.generateToken(userDetails);
            userRepository.save(user);
            responseMap.put("code", "success");
            responseMap.put("username", username);
            responseMap.put("message", "Account created successfully");
            responseMap.put("token", token);
            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("code", "error");
            responseMap.put("message", "user exists!");
            return ResponseEntity.status(500).body(responseMap);
        }

    }
}
