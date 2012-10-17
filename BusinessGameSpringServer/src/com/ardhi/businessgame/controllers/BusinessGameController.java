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
		listActionGet.add("loadHeadquarterData");
		listActionGet.add("loadMarketContent");
		listActionGet.add("getSuggestedPrice");
		listActionGet.add("loadSectorOwned");
		listActionGet.add("loadInstallmentOwnedByUser");
		listActionGet.add("loadInstallmentDetails");
		listActionGet.add("loadInstallmentOwnedByEquipment");
		listActionGet.add("queryTotalBundle");
		listActionGet.add("loadUserData");
		listActionGet.add("loadPlayerInfo");
		listActionGet.add("deleteUserData");
//		listActionGet.add("tesBatch");
		
		listActionPost.add("loginUser");
		listActionPost.add("registerUser");
		listActionPost.add("submitProposal");
		listActionPost.add("buildUserStorage");
		listActionPost.add("buyMarketProduct");
		listActionPost.add("buyMarketEquipment");
		listActionPost.add("sellStorageProduct");
		listActionPost.add("sellStorageEquipment");
		listActionPost.add("createNewInstallment");
		listActionPost.add("attachEquipmentToInstallment");
		listActionPost.add("detachEquipment");
		listActionPost.add("hireEmployeeToInstallment");
		listActionPost.add("fireEmployee");
		listActionPost.add("updateTariff");
		listActionPost.add("updateSupplyKwh");
		listActionPost.add("cancelSupplyInstallment");
		listActionPost.add("buySectorBlueprint");
		listActionPost.add("buyBundleEquipmentEmployee");
		listActionPost.add("markMessageAsRead");
		listActionPost.add("advertiseProduct");
		listActionPost.add("sendMessage");
		listActionPost.add("makeContract");
		listActionPost.add("confirmContract");
		listActionPost.add("cancelRejectContract");
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
			return businessGameService.loadHeadquarterData(req);
		case 6:
			return businessGameService.loadMarketContent(req);
		case 7:
			return businessGameService.getSuggestedPrice(req);
		case 8:
			return businessGameService.loadSectorOwned(req);
		case 9:
			return businessGameService.loadInstallmentOwnedByUser(req);
		case 10:
			return businessGameService.loadInstallmentDetails(req);
		case 11:
			return businessGameService.loadInstallmentOwnedByEquipment(req);
		case 12:
			return businessGameService.queryTotalBundle(req);
		case 13:
			return businessGameService.loadUserData(req);
		case 14:
			return businessGameService.loadPlayerInfo(req);
		case 15:
			return businessGameService.deleteUserData(req);
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
				return businessGameService.submitProposal(req);
			case 3:
				return businessGameService.buildUserStorage(req);
			case 4:
				return businessGameService.buyMarketProduct(req);
			case 5:
				return businessGameService.buyMarketEquipment(req);
			case 6:
				return businessGameService.sellStorageProduct(req);
			case 7:
				return businessGameService.sellStorageEquipment(req);
			case 8:
				return businessGameService.createNewInstallment(req);
			case 9:
				return businessGameService.attachEquipmentToInstallment(req);
			case 10:
				return businessGameService.detachEquipment(req);
			case 11:
				return businessGameService.hireEmployeeToInstallment(req);
			case 12:
				return businessGameService.fireEmployee(req);
			case 13:
				return businessGameService.updateTariff(req);
			case 14:
				return businessGameService.updateSupplyKwh(req);
			case 15:
				return businessGameService.cancelSupplyInstallment(req);
			case 16:
				return businessGameService.buySectorBlueprint(req);
			case 17:
				return businessGameService.buyBundleEquipmentEmployee(req);
			case 18:
				return businessGameService.markMessageAsRead(req);
			case 19:
				return businessGameService.advertiseProduct(req);
			case 20:
				return businessGameService.sendMessage(req);
			case 21:
				return businessGameService.makeContract(req);
			case 22:
				return businessGameService.confirmContract(req);
			case 23:
				return businessGameService.cancelRejectContract(req);
			default:
				return "-1";
		}
	}
	
	//Debug zone :
	
//	@RequestMapping(value="getProposeInfo", method=RequestMethod.GET)
//	public String getProposeInfoGet(){
//		return "getProposeInfo";
//	}
}
