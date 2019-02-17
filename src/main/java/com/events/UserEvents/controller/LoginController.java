package com.events.UserEvents.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.events.UserEvents.service.DAORepositoryService;

import com.events.UserEvents.entity.User;

@Controller
public class LoginController {

	@Autowired
	private DAORepositoryService DAORepositoryService;

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView model = new ModelAndView();

		model.setViewName("login");
		return model;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String register(HttpServletRequest request, Model model) {

		User user = DAORepositoryService.findUserByEmail(request.getParameter("email"));

		if (user != null) {
			model.addAttribute("errorExists", "User already exists");
			return "login";
		}

		long userId = DAORepositoryService.addUser(request.getParameter("firstname"), request.getParameter("lastname"),
				request.getParameter("email"), request.getParameter("location"), request.getParameter("state"),
				request.getParameter("password"));

		model.addAttribute("registerSuccess", "Registered successfully. Login to continue");
		return "login";

	}

	@RequestMapping(value = { "/logout" }, method = RequestMethod.GET)
	public ModelAndView logout() {
		ModelAndView model = new ModelAndView();

		model.setViewName("login");
		return model;
	}

}