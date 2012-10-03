package com.ardhi.businessgame.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommunicationService {
	
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds
	public static final String URL = "http://192.168.1.12:8080/BusinessGame/businessGame",
			GET_GET_GAME_TIME = "?action=getGameTime",
			GET_GET_ENTIRE_ZONE = "?action=getEntireZone",
			GET_LOAD_BANK_DATA = "?action=loadBankData",
			GET_CHECK_USER_STORAGE = "?action=checkUserStorage",
			GET_REFRESH_CLIENT_DATA = "?action=refreshClientData",
			GET_LOAD_HEADQUARTER_DATA = "?action=loadHeadquarterData",
			GET_LOAD_MARKET_CONTENT = "?action=loadMarketContent",
			GET_GET_SUGGESTED_PRICE = "?action=getSuggestedPrice",
			GET_LOAD_SECTOR_OWNED = "?action=loadSectorOwned",
			GET_LOAD_INSTALLMENT_OWNED_BY_USER = "?action=loadInstallmentOwnedByUser",
			GET_LOAD_INSTALLMENT_DETAILS = "?action=loadInstallmentDetails",
			GET_GET_AVAILABLE_INSTALLMENT = "?action=getAvailableInstallment",
			GET_LOAD_INSTALLMENT_OWNED_BY_EQUIPMENT = "?action=loadInstallmentOwnedByEquipment",
			
			POST_LOGIN = "loginUser",
			POST_REGISTER_USER = "registerUser",
			POST_SUBMIT_PROPOSAL = "submitProposal",
			POST_BUILD_USER_STORAGE = "buildUserStorage",
			POST_BUY_MARKET_PRODUCT = "buyMarketProduct",
			POST_BUY_MARKET_EQUIPMENT = "buyMarketEquipment",
			POST_SELL_STORAGE_PRODUCT = "sellStorageProduct",
			POST_SELL_STORAGE_EQUIPMENT = "sellStorageEquipment",
			POST_CREATE_NEW_INSTALLMENT = "createNewInstallment",
			POST_ATTACH_EQUIPMENT_TO_INSTALLMENT = "attachEquipmentToInstallment",
			POST_DETACH_EQUIPMENT="detachEquipment",
			POST_HIRE_EMPLOYEE_TO_INSTALLMENT = "hireEmployeeToInstallment",
			POST_FIRE_EMPLOYEE = "fireEmployee",
			POST_BUY_SECTOR_BLUEPRINT = "buySectorBlueprint";
	
	public static boolean isOnline(Activity act){
		ConnectivityManager conn = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conn.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected())
			return true;
		else return false;
	}
	
	public static String post(String act,HashMap<String,String> postParameters) throws IOException{
		android.util.Log.d("posting", "started");
		String str = "action="+URLEncoder.encode(act, "UTF-8");
		for(String key : postParameters.keySet()){
			str += "&"+key+"="+URLEncoder.encode(postParameters.get(key), "UTF-8");
		}
		URL url = new URL(URL);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setFixedLengthStreamingMode(str.getBytes().length);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(str);
		out.close();
		str = "";
		Scanner in = new Scanner(conn.getInputStream());
		while(in.hasNextLine()){
			str+=(in.nextLine());
		}
		in.close();
		conn.disconnect();
		
		in = null;
		out = null;
		conn = null;
		url = null;
		
		android.util.Log.d("posting", "finished");
		return str;
	}
	
	public static String get(String act) throws IOException{
		android.util.Log.d("getting", "started");
		String str = "";
		URL url = new URL(URL+act);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Scanner in = new Scanner(conn.getInputStream());
		while(in.hasNextLine()){
			str+=(in.nextLine());
		}
		in.close();
		conn.disconnect();
		
		in = null;
		conn = null;
		url = null;
		
		android.util.Log.d("getting", "finished");
		return str;
	}
}
