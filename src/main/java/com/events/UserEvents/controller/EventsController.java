package com.events.UserEvents.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//import com.events.UserEvents.LoggedInUser;
import com.events.UserEvents.entity.Event;
import com.events.UserEvents.entity.User;

import com.events.UserEvents.service.DAORepositoryService;
import com.events.UserEvents.service.EventRepository;
import com.events.UserEvents.util.DateUtils;

@Controller
//@RequestMapping("/events")
public class EventsController {

	@Autowired
	private DAORepositoryService DAORepositoryService;

	private User loggedInUser;
	private long loggedInUserId;

	Map mapEvents;

	private void fetchEventData() {
		mapEvents = DAORepositoryService.getAllEvents(getLoggedInUserId());
	}

	private void updateLists(Model theModel) {

		theModel.addAttribute("eventsCreated", mapEvents.get("createdInState"));
		theModel.addAttribute("eventsJoining", mapEvents.get("joiningInState"));
		theModel.addAttribute("eventsToJoin", mapEvents.get("toJoinInState"));

		theModel.addAttribute("eventsCreatedNotState", mapEvents.get("createdOtherStates"));
		theModel.addAttribute("eventsJoiningNotState", mapEvents.get("joiningOtherStates"));
		theModel.addAttribute("eventsToJoinNotState", mapEvents.get("toJoinOtherStates"));

		
		theModel.addAttribute("currentUser", mapEvents.get("currentUser"));
	}

	

	@GetMapping("/events")
	public String listEvents(HttpServletRequest request, Model theModel) {

		//System.out.println("Request " );

		//	if (loggedInUser == null) { // This is only for demo purpose as logout not working as expected	
		setLoggedInUserID();
		
		//	}

		fetchEventData();

		updateLists(theModel);

		return "displayevents";
	}

	@GetMapping("/events/join/{id}")
	public String joinEvent(@PathVariable("id") String eventId, Model theModel) {

		mapEvents = DAORepositoryService.addJoiningEvent(getLoggedInUserId(), Integer.parseInt(eventId));

		// fetchEventData();
		updateLists(theModel);

		return "displayevents";

	}

	@GetMapping("/events/cancel/{id}")
	public String cancelEvent(@PathVariable("id") String eventId, Model theModel) {

		mapEvents = DAORepositoryService.deleteJoiningEvent(getLoggedInUserId(), Long.parseLong(eventId));

		// fetchEventData();
		updateLists(theModel);

		return "displayevents";

	}

	@RequestMapping(value = "/events/createevent/", method = RequestMethod.POST)
	public String createevent(HttpServletRequest request, Model model) throws Exception {

		updateEvent(request.getParameter("eventname"), request.getParameter("date"), request.getParameter("location"),
				request.getParameter("state"));

		mapEvents = DAORepositoryService.getEventsMap();
		// fetchEventData();
		updateLists(model);

		return "displayevents";
	}

	private void updateEvent(String name, String date, String location, String state) throws Exception {

		Event event = new Event(name, DateUtils.formatDate(date), location, state);

		event = DAORepositoryService.addEvent(getLoggedInUserId(), event);

	}

	
	@RequestMapping(value = "/events/edit/", method = RequestMethod.POST)
	public String editEvent(HttpServletRequest request, Model model) throws Exception {

		mapEvents = DAORepositoryService.editEvent(Long.parseLong(request.getParameter("eventId")),
				request.getParameter("name"),
				request.getParameter("date"), request.getParameter("location"), request.getParameter("state"),
				getLoggedInUserId());

		// fetchEventData();
		updateLists(model);

		return "displayevents";

	}

	@GetMapping("/events/{id}/edit")

	public String showEventEditPage(@PathVariable("id") String eventId, Model theModel) {

		Map eventDataMap = DAORepositoryService.checkEventCreatedByLoggedInUser(getLoggedInUserId(), eventId);

		if (Boolean.TRUE.equals(eventDataMap.get("eventCreatedLoggedUser"))) { // Event created by logged in user

			theModel.addAttribute("eventId", eventDataMap.get("eventId"));
			theModel.addAttribute("eventName", eventDataMap.get("eventName"));
			theModel.addAttribute("date", eventDataMap.get("date"));
			theModel.addAttribute("location", eventDataMap.get("location"));
			theModel.addAttribute("state", eventDataMap.get("state"));

			return "editevents";
		}

		else {
			updateLists(theModel);
			return "displayevents";
		}

	}

	//@DeleteMapping("/events/delete/{id}") -- cannot give  DELETE on link in html
	@RequestMapping(value = "/events/delete/{id}")
	
	public String deleteEvent(@PathVariable("id") Long eventId, Model theModel) {

		DAORepositoryService.deleteEvent(eventId, getLoggedInUserId());
		mapEvents = DAORepositoryService.getEventsMap();

		updateLists(theModel);
		return "displayevents";

	}

	@GetMapping("/events/{id}")

	public String showEventDetailsAndWall(@PathVariable("id") String eventId, Model theModel) {

		
		
		Map dataMap = DAORepositoryService.getEventWall(getLoggedInUserId(), Long.parseLong(eventId));

		fillEventWallData(theModel, dataMap);

		return "eventwall";
	}

	private void fillEventWallData(Model theModel, Map dataMap) {

		theModel.addAttribute("joiningUsers", dataMap.get("joiningUsers"));
		theModel.addAttribute("countJoiners", dataMap.get("countJoiners"));
		theModel.addAttribute("eventData", dataMap.get("eventData"));
		theModel.addAttribute("wallmessages", dataMap.get("wallmessages"));

	}

	@RequestMapping(value = "/events/comment/", method = RequestMethod.POST)

	public String addComment(HttpServletRequest request, Model model) throws Exception {

		DAORepositoryService.addMessage(getLoggedInUserId(), Long.parseLong(request.getParameter("eventId")),
				request.getParameter("message"));

		Map eventWallMap = DAORepositoryService.getEventWallMap();

		fillEventWallData(model, eventWallMap);

		return "eventwall";
	}

	private void setLoggedInUserID() {

		//For not get loggedInID everytime as no log out implemented yet and same instance update due to multiple
		//logins for demo purpose.
		//if (loggedInUser == null) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("Logged In User : " + auth.getName());

		loggedInUser = DAORepositoryService.findUserByEmail(auth.getName());

		loggedInUserId = loggedInUser.getId();
			
			
		//}
	}

	private long getLoggedInUserId() {

	//	loggedInUserId = 3;
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("Logged In User : " + auth.getName());

		loggedInUser = DAORepositoryService.findUserByEmail(auth.getName());

		loggedInUserId = loggedInUser.getId();
			
		//!loggedInUserId = 1;
		
		return loggedInUserId;
	}

}
