package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.model.User;
import com.example.ebankingbackend.repository.UserRepository;
import com.example.ebankingbackend.service.JwtUserDetailsService;
import com.example.ebankingbackend.util.JwtTokenUtil;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.impl.DefaultClaims;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    protected final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

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
                responseMap.put("error", false);
                responseMap.put("message", "Logged In");
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("error", true);
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            responseMap.put("error", true);
            responseMap.put("message", "User is disabled");
            return ResponseEntity.status(500).body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("error", true);
            responseMap.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("error", true);
            responseMap.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(responseMap);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestParam("username") String username
            , @RequestParam("password") String password) {
        Map<String, Object> responseMap = new HashMap<>();
        if (userRepository.findUserByUsername(username) == null) {
            User user = new User();
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            user.setRole("USER");
            user.setUsername(username);
            UserDetails userDetails = userDetailsService.createUserDetails(username, user.getPassword());
            String token = jwtTokenUtil.generateToken(userDetails);
            userRepository.save(user);
            responseMap.put("error", false);
            responseMap.put("username", username);
            responseMap.put("message", "Account created successfully");
            responseMap.put("token", token);
            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("error", true);
            responseMap.put("message", "user exists!");
            return ResponseEntity.status(500).body(responseMap);
        }

    }

    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();

        // From the HttpRequest get the claims
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");

        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = jwtTokenUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
        responseMap.put("error", false);
        responseMap.put("message", "Token refresh");
        responseMap.put("token", token);
        return ResponseEntity.ok(responseMap);
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }
}
