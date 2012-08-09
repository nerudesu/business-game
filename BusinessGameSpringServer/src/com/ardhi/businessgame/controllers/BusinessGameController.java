package com.ardhi.businessgame.controllers;

import java.util.ArrayList;
//import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ardhi.businessgame.services.BusinessGameService;

@Controller
@RequestMapping("businessGame")
public class BusinessGameController {
	private ArrayList<String> listActionPost = new ArrayList<String>(),
			listActionGet = new ArrayList<String>();
	
//	private HashMap<String, Long> users = new HashMap<String, Long>();
	public static boolean working = false;
	
	@Resource(name="businessGameService")
	private BusinessGameService businessGameService;
	private @Autowired HttpServletRequest req;
	
	@PostConstruct
	public void setListMappingRequest(){
		listActionGet.add("getGameTime");
		listActionGet.add("getEntireZone");
		listActionGet.add("loadBankData");
		listActionGet.add("checkUserStorage");
		listActionGet.add("refreshClientData");
		listActionGet.add("checkUserSector");
		listActionGet.add("loadMarketContent");
		listActionGet.add("getSuggestedPrice");
		
		listActionPost.add("loginUser");
		listActionPost.add("registerUser");
		listActionPost.add("getProposeInfo");
		listActionPost.add("submitProposal");
		listActionPost.add("buildUserStorage");
		listActionPost.add("buyMarketProduct");
		listActionPost.add("sellStorageProduct");
	}
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public String getResolver(){
		String action = req.getParameter("action");
		int act;
//		if(working)
			act = listActionGet.indexOf(action);
//		else act = -1;
		switch (act) {
		case 0:
			return businessGameService.getGameTime();
		case 1:
			return businessGameService.getEntireZone();
		case 2:
			return businessGameService.loadBankData(req);
		case 3:
			return businessGameService.checkUserStorage(req);
		case 4:
			return businessGameService.refreshClientData(req);
		case 5:
			return businessGameService.checkUserSector(req);
		case 6:
			return businessGameService.loadMarketContent(req);
		case 7:
			return businessGameService.getSuggestedPrice(req);
		default:
			return "-1";
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public String postResolver(){
		String action = req.getParameter("action");
//		System.out.println(req.getRemoteAddr());
		int act;
//		if(working)
			act = listActionPost.indexOf(action);
//		else act = -1;
		switch (act) {
			case 0:
				return businessGameService.loginUser(req);
			case 1:
				return businessGameService.registerUser(req);
			case 2:
				return businessGameService.getProposeInfo(req);
			case 3:
				return businessGameService.submitProposal(req);
			case 4:
				return businessGameService.buildUserStorage(req);
			case 5:
				return businessGameService.buyMarketProduct(req);
			case 6:
				return businessGameService.sellStorageProduct(req);
			default:
				return "-1";
		}
	}
	
	//Debug zone :
	
	@RequestMapping(value="getProposeInfo", method=RequestMethod.GET)
	public String getProposeInfoGet(){
		return "getProposeInfo";
	}
}
