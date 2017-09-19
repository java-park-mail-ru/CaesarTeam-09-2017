package server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import server.User;
import server.UserService;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
public class AccountController {

    private UserService userService = new UserService();

    @RequestMapping(method = RequestMethod.POST, path = "reg")
    public ResponseEntity register(@RequestBody User user) { // как поймать исключения в конструкторе?

        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();

        Map<String, String> response = new LinkedHashMap<>();

        if (userService.validation(email, username, password, response)) {
            userService.setUser(username, user);

            return ResponseEntity.ok(user);  // http response code 200
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // http response code 400
        }

    }

    @RequestMapping(method = RequestMethod.POST, path = "auth")
    public ResponseEntity authorize(@RequestBody User user, HttpSession httpSession) {

        Map<String, String> response = new LinkedHashMap<>();

        String username = user.getUsername();
        String password = user.getPassword();

        String usernameBySession = (String) httpSession.getAttribute("username");

        if (usernameBySession != null) {
            response.put("Cause", "You are already authorized");
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(response); // http response code 418
        }

        if (!userService.containsUsername(username)) {
            response.put("Cause", " \"" + username + "\" not registrated :( "
                    + "register: {\"email\",\"username\",\"password\"} on POST localhost:8081/registr");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // http response code 403
        } else if (!password.equals(userService.getPassword(username))) {
            response.put("Cause", "Wrong password! Check CapsLock :) and try again.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (httpSession.getAttribute("username") == null) {
            httpSession.setAttribute("username", username);
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST, path = "info")
    public ResponseEntity requestUserCurrentSession(HttpSession httpSession) {

        Map<String, String> response = new LinkedHashMap<>();

        String username = (String) httpSession.getAttribute("username");

        if (username == null) {
            response.put("Cause", "You haven't authorized. authorize: {\"username\",\"password\"} on POST localhost:8081/auth");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // http response code 401
        } else {
            response.put("username", "Hello " + username);
            response.put("sessionId", "Your sessionId: " + httpSession.getId());
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST, path = "exit")
    public ResponseEntity logOut(HttpSession httpSession) {

        Map<String, String> response = new LinkedHashMap<>();

        String username = (String) httpSession.getAttribute("username");

        if (username == null) {
            response.put("Cause", "You haven't authorized. authorize: {\"username\",\"password\"} on POST localhost:8081/auth");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        httpSession.removeAttribute("username");

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST, path = "rename")
    public ResponseEntity rename(@RequestBody User user, HttpSession httpSession) {

        Map<String, String> response = new LinkedHashMap<>();

        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();

        String oldUsername = (String) httpSession.getAttribute("username");

        if (oldUsername == null) {
            response.put("Cause", "You must authorize before change your personal Information\n");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (userService.validation(email, username, password, response)) {
            userService.removeUser(oldUsername);

            userService.setUser(username, user);
            httpSession.setAttribute("username", username);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @RequestMapping(method = RequestMethod.POST, path = "allUsers")
    public ResponseEntity printAllUsers() {

        Map<String, String> response = new LinkedHashMap<>();

        List<User> list = userService.getAllUsers();
        list.forEach((value) -> {
            response.put(value.getEmail(), "email");
            response.put(value.getUsername(), "username");
        });

        return ResponseEntity.ok(response);

    }
}
