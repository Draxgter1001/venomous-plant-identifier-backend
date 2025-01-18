package com.example.taf.VPI.controller;

import com.example.taf.VPI.model.User;
import com.example.taf.VPI.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        if(userRepository.findByEmail(user.getEmail()) != null){
            return ResponseEntity.badRequest().body("Email Already Exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body("User Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
        User existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())){
            return ResponseEntity.badRequest().body("Incorrect email or password");
        }
        return ResponseEntity.ok().body("User Logged In");
    }
}
