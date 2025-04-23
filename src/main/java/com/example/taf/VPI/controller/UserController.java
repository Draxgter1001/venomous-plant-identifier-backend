package com.example.taf.VPI.controller;

import com.example.taf.VPI.model.User;
import com.example.taf.VPI.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "https://venomous-plant-identifier-frontend.vercel.app"}, allowCredentials = "true")
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

        if(user.getEmail().isEmpty() || !user.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")){
            return ResponseEntity.badRequest().body("Invalid Email Format");
        }

        if(user.getPassword().isEmpty() || user.getPassword().length() < 8){
            return ResponseEntity.badRequest().body("Password needs to be at least 8 characters");
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

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload){
        String email = payload.get("email");
        String newPassword = payload.get("newPassword");

        if(email == null || newPassword == null || newPassword.length() < 8 ){
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        User user = userRepository.findByEmail(email);
        if(user == null){
            return ResponseEntity.badRequest().body("User Not Found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok().body("Password Reset Successful");
    }
}
