package gpk.practice.spring.bootmvc.controller;

import gpk.practice.spring.bootmvc.model.User;
import gpk.practice.spring.bootmvc.service.SecurityService;
import gpk.practice.spring.bootmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    SecurityService securityService;

    @GetMapping(value="/registration")
    public ModelAndView registration(ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView("registration");
        modelMap.put("username", securityService.getCurrentUserName());
        User user = new User();
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping(value="/register")
    public ModelAndView register(@Valid User user, BindingResult bindingResult,  ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelMap.put("username", securityService.getCurrentUserName());
        if (bindingResult.hasErrors()) {
            modelMap.put("registered_f", false);
            modelMap.put("error_msg", "Ошибка регистрации. Пожалуйста, проверьте правильность заполнения полей.");
            return modelAndView;
        }
        try {
            User userFoundByLogin = userService.findByLogin(user.getUsername());
            User userFoundByEmail = userService.findByEmail(user.getEmail());
            if ( userFoundByLogin != null ) {
                modelMap.put("registered_f", false);
                modelMap.put("error_msg", "Пользователь с таким логином уже существует");
            } else if (userFoundByEmail != null ) {
                modelMap.put("registered_f", false);
                modelMap.put("error_msg", "Пользователь с таким email уже существует");
            } else {
                modelMap.put("registered_f", true);
                userService.registerNewUserAccount(user);
            }
        } catch (Exception /* NonUniqueResultException */ e) {
            modelMap.put("registered_f", false);
            modelMap.put("error_msg", "Ошибка регистрации.");
        }
        return modelAndView;
    }

    @GetMapping(value="/access-denied")
    public String accesDenied(ModelMap modelMap) {
        modelMap.put("username", securityService.getCurrentUserName());
        return "access-denied";
    }

    @GetMapping(value="/login")
    public ModelAndView logIn(ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelMap.put("username", securityService.getCurrentUserName());
        User user = new User();
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping(value="/logout")
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }


}
