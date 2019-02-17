package com.events.UserEvents.controller;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.events.UserEvents.entity.Event;
import com.events.UserEvents.entity.User;

import com.events.UserEvents.service.DAORepositoryService;

import com.events.UserEvents.util.DateUtils;
import com.events.UserEvents.util.State;

@Controller

public class EventsController {

	@Autowired
	private DAORepositoryService DAORepositoryService;

	
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
		theModel.addAttribute("states", State.getStates() );
	}

	@GetMapping("/events")
	public String listEvents(HttpServletRequest request, Model theModel) {

		fetchEventData();

		updateLists(theModel);

		return "displayevents";
	}

	@GetMapping("/events/join/{id}")
	public String joinEvent(@PathVariable("id") String eventId, Model theModel) {

		DAORepositoryService.addJoiningEvent(getLoggedInUserId(), Integer.parseInt(eventId));

		return "redirect:/events";
	}

	@GetMapping("/events/cancel/{id}")
	public String cancelEvent(@PathVariable("id") String eventId, Model theModel) {

		DAORepositoryService.deleteJoiningEvent(getLoggedInUserId(), Long.parseLong(eventId));

		return "redirect:/events";

	}

	@RequestMapping(value = "/events/createevent/", method = RequestMethod.POST)
	public String createevent(HttpServletRequest request, Model model) throws Exception {

		updateEvent(request.getParameter("eventname"), request.getParameter("date"), request.getParameter("location"),
				request.getParameter("state"));

		return "redirect:/events";
	}

	private void updateEvent(String name, String date, String location, String state) throws Exception {

		Event event = new Event(name, DateUtils.formatDate(date), location, state);
		event = DAORepositoryService.addEvent(getLoggedInUserId(), event);
	}

	@RequestMapping(value = "/events/edit/", method = RequestMethod.POST)
	public String editEvent(HttpServletRequest request, Model model) throws Exception {

		DAORepositoryService.editEvent(Long.parseLong(request.getParameter("eventId")), request.getParameter("name"),
				request.getParameter("date"), request.getParameter("location"), request.getParameter("state"),
				getLoggedInUserId());

		return "redirect:/events";

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
			
			theModel.addAttribute("states", State.getStates() );

			return "editevents";
		}

		else {
			updateLists(theModel);
			return "displayevents";
		}

	}

	
	@RequestMapping(value = "/events/delete/{id}")

	public String deleteEvent(@PathVariable("id") Long eventId, Model theModel) {

		DAORepositoryService.deleteEvent(eventId, getLoggedInUserId());

		return "redirect:/events";

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

	
	private long getLoggedInUserId() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User loggedInUser = DAORepositoryService.findUserByEmail(auth.getName());

		loggedInUserId = loggedInUser.getId();

		return loggedInUserId;
	}

}
