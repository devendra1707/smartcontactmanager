package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Helper.Message;
import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder bycryptpasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String SignUp(Model model) {
		model.addAttribute("title", "SignUp-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "terms", defaultValue = "false") boolean terms, Model model, HttpSession session) {
		if (result1.hasErrors()) {
			System.out.println("Error having" + result1.toString());
			model.addAttribute("user", user);
			return "signup";
		}

		try {
			if (!terms) {
				System.out.println("you have not agreed terms and condition ");
				throw new Exception("you have not agreed terms and condition ");
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bycryptpasswordEncoder.encode(user.getPassword()));
			User result = this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully registered!! ", "alert-success"));
			System.out.println("Terms agreed " + terms);
			System.out.println("User" + user);

			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went Wrong !!" + e.getMessage(), "alert-warning"));
			return "signup";
		}

	}

	@GetMapping("/signin")
	public String customerLogin(Model model) {
		model.addAttribute("title", "Login Page");

		return "login";
	}

	@RequestMapping("/Loginfailed")
	public String customerLoginfailed(Model model) {
		model.addAttribute("failedURl");
		return "Loginfailed";
	}
}