package jp.co.sss.spring.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.spring.entity.User;
import jp.co.sss.spring.repository.UserRepository;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/login")
	public String loginForm() {
		return "user/login";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam String email,
			@RequestParam String password,
			HttpSession session, Model model) {

		User user = userRepository.findByEmail(email).orElse(null);

		if (user == null || !passwordEncoder.matches(password, user.getPasswords())) {
			model.addAttribute("error", "メールアドレスまたはパスワードが違います");
			return "user/login";
		}

		session.setAttribute("loginUserId", user.getUserId());
		return "redirect:/top";
	}

	@GetMapping("/users/new")
	public String registerForm() {
		return "user/register";
	}

	@PostMapping("/users")
	public String register(
			@RequestParam String userName,
			@RequestParam String userNameKana,
			@RequestParam String email,
			@RequestParam String phone,
			@RequestParam String password,
			@RequestParam String passwordConfirm,
			Model model) {

		if (!password.equals(passwordConfirm)) {
			model.addAttribute("error", "パスワードが一致しません");
			return "user/register";
		}

		if (userRepository.findByEmail(email).isPresent()) {
			model.addAttribute("error", "このメールアドレスは既に登録されています");
			return "user/register";
		}

		User user = new User();
		user.setUserName(userName);
		user.setUserNameKana(userNameKana);
		user.setEmail(email);
		user.setPhone(phone);
		user.setPasswords(passwordEncoder.encode(password));
		userRepository.save(user);

		return "redirect:/login";
	}

	@GetMapping("/mypage")
	public String mypage(HttpSession session, Model model) {
		Integer userId = (Integer) session.getAttribute("loginUserId");
		if (userId == null) {
			return "redirect:/login";
		}

		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		model.addAttribute("userName", user.getUserName());
		model.addAttribute("userNameKana", user.getUserNameKana());
		model.addAttribute("phone", user.getPhone());
		return "user/mypage";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}