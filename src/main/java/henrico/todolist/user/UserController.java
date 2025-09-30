package henrico.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = userRepository.findByUsername(userModel.getUsername());

        if (user != null) // Pode ser assim tbm ".status(HttpStatus.BAD_REQUEST)"
            return ResponseEntity.status(400).body("Usuario ja existe!");

        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashed);

        UserModel userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(201).body(userCreated);
    }
}
