package io.sammanak.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/user/{id}/")
    public User getUser(@PathVariable String id) {
        return userRepository.findById(id).orElse(null);
    }
    @PostMapping("/user/register")
    public User register(@RequestBody User body) {
        // list all found items to array
        List<User> user = userRepository.findByEmail(body.getEmail());
        // System.out.println("user.size() " + user.size());
        // check if user length > 0
        if (user.size() > 0) {
            throw new ResponseStatusException(HttpStatus.FOUND, "user existed");
        } else {
            if (body.getPassword() != null) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String encodedPassword = passwordEncoder.encode(body.getPassword());
                body.setPassword(encodedPassword);
            }
            return userRepository.save(body);
        }
    }
    @PostMapping("/user/login")
    public User login(@RequestBody User body) {
        // list all found items to array
        List<User> user = userRepository.findByEmail(body.getEmail());
        // check if user length > 0
        if (user.size() > 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean isPasswordMatch = passwordEncoder.matches(body.getPassword(), user.get(0).getPassword());
            if (isPasswordMatch) {
                return user.get(0);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "password not match");
            }
        } else {
            // throw new UserNotFoundException("user not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
    }
    @PutMapping("/user/update")
    public User update(@RequestBody User user) {
        User getUser = userRepository.findById(user.getId()).orElse(null);
        getUser.setEmail(user.getEmail());
        getUser.setPassword(user.getPassword());
        return userRepository.save(getUser);
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        userRepository.deleteById(id);
        return id;
    }@GetMapping("/bcrypt")
    public String bcrypt() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "myPassword";
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println();
        System.out.println("Password is         : " + password);
        System.out.println("Encoded Password is : " + encodedPassword);
        System.out.println();

        boolean isPasswordMatch = passwordEncoder.matches(password, encodedPassword);
        System.out.println("Password : " + password + "     isPasswordMatch    : " + isPasswordMatch);

        password = "myPasswordLol";
        isPasswordMatch = passwordEncoder.matches(password, encodedPassword);
        System.out.println("Password : " + password + "     isPasswordMatch    : " + isPasswordMatch);
        return "OK";
    }
}