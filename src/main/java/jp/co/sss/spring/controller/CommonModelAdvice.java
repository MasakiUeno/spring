package jp.co.sss.spring.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.spring.repository.CategoryRepository;
import jp.co.sss.spring.repository.UserRepository;

@ControllerAdvice
public class CommonModelAdvice {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

	@ModelAttribute
	public void addCommonAttributes(HttpSession session, Model model) {
		model.addAttribute("categories", categoryRepository.findAll());

		Integer loginUserId = (Integer) session.getAttribute("loginUserId");
		if (loginUserId != null) {
			userRepository.findById(loginUserId)
					.ifPresent(user -> model.addAttribute("loginUserName", user.getUserName()));
		}
	}
}