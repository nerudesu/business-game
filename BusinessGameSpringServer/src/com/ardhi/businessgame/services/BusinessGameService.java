package com.ardhi.businessgame.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.ardhi.businessgame.models.BusinessSectorInfo;
import com.ardhi.businessgame.models.Contract;
import com.ardhi.businessgame.models.EmployeeInfo;
import com.ardhi.businessgame.models.IndustrialEquipmentInfo;
import com.ardhi.businessgame.models.InputInfo;
import com.ardhi.businessgame.models.Installment;
import com.ardhi.businessgame.models.InstallmentEmployee;
import com.ardhi.businessgame.models.InstallmentEquipment;
import com.ardhi.businessgame.models.MarketEmployee;
import com.ardhi.businessgame.models.MarketEquipment;
import com.ardhi.businessgame.models.MarketProduct;
import com.ardhi.businessgame.models.Message;
import com.ardhi.businessgame.models.OutputInfo;
import com.ardhi.businessgame.models.StorageEquipment;
import com.ardhi.businessgame.models.StorageProduct;
import com.ardhi.businessgame.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@Service("businessGameService")
public class BusinessGameService {
	public static final String KEY_REQUEST_BORROW_BANK = "RB",
			KEY_BORROW_BANK = "BB",
			KEY_STORAGE = "ST",
			KEY_PRODUCT = "PR",
			KEY_EQUIPMENT = "EQ",
			KEY_EMPLOYEE = "EM",
			KEY_MARKET_PRODUCT = "MP",
			KEY_MARKET_EQUIPMENT = "MQ",
			KEY_MARKET_EMPLOYEE = "MM",
			KEY_INSTALLMENT = "IN",
			KEY_USER_FINANCE = "UF",
			KEY_USER_SECTOR_BLUEPRINT = "US",
			KEY_USER_MARKET_LICENSE = "UM",
			KEY_USER_MESSAGE = "UP",
			KEY_USER_CONTRACT = "UC",
			KEY_PRODUCT_ADVERTISEMENT = "PA";
	private DBAccess db;
	private Gson gson;
	private ThreadInvoker invoker;
	
	@Resource(name="dataSource")
	public void setDataSource(DataSource dataSource){
		db = DBAccess.getInstance(dataSource);
		gson = new Gson();
		invoker = new ThreadInvoker();
	}
	
	/* Engine configuration below */
	
	@PostConstruct
	public void startEngine() throws Exception{
		System.out.println("Starting EngineDaemon..");
		invoker.setThreadWork(true);
		invoker.start();
	}
	
	@PreDestroy
	public void stopEngine() throws Exception{
		invoker.setThreadWork(false);
		invoker.interrupt();
	}

	/* End of engine configuration */
	
	/* Get function starts here : */
	
	public String getGameTime() {
		String val = gson.toJson(invoker.getGameTime());
		return val;
	}
	
	public String getEntireZone() {
		String val = "";
		ArrayList<String> zoneList = new ArrayList<String>();
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id from info_zone");
		while(srs.next()){
			zoneList.add(srs.getString("id"));
		}
		
		val = gson.toJson(zoneList);
		
		srs = null;
		zoneList = null;
		gc();
		
		return val;
	}
	
	public String loadBankData(HttpServletRequest req) {
		String val = "";
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id from req_borrow_bank where [user]='"+req.getParameter("user")+"'");
		if(srs.next())
			return "1";
		
		srs = db.getJdbc().queryForRowSet("select id from borrow_bank where [user]='"+req.getParameter("user")+"'");
		if(srs.next())
			return "2";
		
		ArrayList<String> sectorList = new ArrayList<String>(),
				data = new ArrayList<String>();
		ArrayList<Double> priceList = new ArrayList<Double>();
		ArrayList<BusinessSectorInfo> bsiList = new ArrayList<BusinessSectorInfo>();
		ArrayList<IndustrialEquipmentInfo> ie;
		ArrayList<EmployeeInfo> e;
		ArrayList<InputInfo> i;
		ArrayList<OutputInfo> o;
		double tmpd1,tmpd2;
		
		srs = db.getJdbc().queryForRowSet("select name, cost from info_sector");
		while(srs.next()){
			sectorList.add(srs.getString("name"));
			priceList.add(srs.getDouble("cost"));
		}
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"')");
		if(srs.next())	
			tmpd1 = srs.getDouble("cost");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='cost_storage'");
		if(srs.next())
			tmpd2 = Double.parseDouble(srs.getString("value"));
		else return "0";
		
		for(String sector : sectorList){
			srs = db.getJdbc().queryForRowSet("select equipment_type,items,base_price,base_operational from info_sector_equipment,info_equipment where equipment_type=info_equipment.name and info_sector_equipment.sector='"+sector+"'");
			
			ie = new ArrayList<IndustrialEquipmentInfo>();
			while(srs.next()){
				ie.add(new IndustrialEquipmentInfo(srs.getString("equipment_type"), srs.getInt("items"), srs.getDouble("base_price"), srs.getDouble("base_operational")));
			}
			
			srs = db.getJdbc().queryForRowSet("select employee_type,items,base_price,base_operational from info_sector_employee,info_employee where employee_type=info_employee.name and info_sector_employee.sector='"+sector+"'");
			e = new ArrayList<EmployeeInfo>();
			while(srs.next()){
				e.add(new EmployeeInfo(srs.getString("employee_type"), srs.getInt("items"), srs.getDouble("base_price"), srs.getDouble("base_operational")));
			}
			
			srs = db.getJdbc().queryForRowSet("select input_type,base_price,size from info_sector_input,info_product where input_type=info_product.name and info_sector_input.sector='"+sector+"'");
			i = new ArrayList<InputInfo>();
			while(srs.next()){
				i.add(new InputInfo(srs.getString("input_type"), srs.getDouble("size"), srs.getDouble("base_price")));
			}
			
			srs = db.getJdbc().queryForRowSet("select output_type,base_price,size from info_sector_output,info_product where output_type=info_product.name and info_sector_output.sector='"+sector+"'");
			o = new ArrayList<OutputInfo>();
			while(srs.next()){
				o.add(new OutputInfo(srs.getString("output_type"), srs.getDouble("size"), srs.getDouble("base_price")));
			}
			
			bsiList.add(new BusinessSectorInfo(ie, e, i, o));
			ie = null;
			e = null;
			i = null;
			o = null;
		}
		
		data.add(gson.toJson(sectorList));
		data.add(gson.toJson(priceList));
		data.add(gson.toJson(tmpd1));
		data.add(gson.toJson(tmpd2));
		data.add(gson.toJson(bsiList));
		
		val = gson.toJson(data);
		
		sectorList = null;
		priceList = null;
		bsiList = null;
		data = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String checkUserStorage(HttpServletRequest req) {
		String val = "No",id="";
		boolean isAvailable;
		SqlRowSet srs1, srs2;
		double tmp = 0;
		
		ArrayList<String> data = new ArrayList<String>();
		srs1 = db.getJdbc().queryForRowSet("select [level] from storage where id='"+req.getParameter("storage")+"'");
		isAvailable = srs1.next();
		if(isAvailable){
			id = req.getParameter("storage");
		} else {
			srs1 = db.getJdbc().queryForRowSet("select id,[level] from storage where [user]='"+req.getParameter("user")+"' and [zone]='"+req.getParameter("zone")+"'");
			isAvailable = srs1.next();
			if(isAvailable)
				id = srs1.getString("id");
		}
		
		if(isAvailable){
			int level = srs1.getInt("level")-1;
			
			double capacity = 0, fill = 0, upgrade = 0, inc = 0;
			srs1 = db.getJdbc().queryForRowSet("select [value] from info_values where name='storage' union select [value] from info_values where name='storage_inc' union select [value] from info_values where name='cost_storage_upgrade'");
			if(srs1.next()){
				capacity = Double.parseDouble(srs1.getString("value"));
			} else return "0";
			
			if(srs1.next()){
				capacity += level*Double.parseDouble(srs1.getString("value"));
				inc = Double.parseDouble(srs1.getString("value"));
			} else return "0";
			
			if(srs1.next()){
				upgrade = Double.parseDouble(srs1.getString("value"));
			} else return "0";
			
			srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size,draw from storage_product,desc_product,info_product where storage='"+id+"' and desc_product.id=storage_product.[desc] and product=name");
			ArrayList<StorageProduct> storageProducts = new ArrayList<StorageProduct>();
			ArrayList<MarketProduct> marketProducts = new ArrayList<MarketProduct>();
			while(srs1.next()){
				tmp = srs1.getDouble("size");
				srs2 = db.getJdbc().queryForRowSet("select market_product.id,product,market_product.price,quality,market_product.size,draw from market_product,desc_product,info_product,storage_product where storage_product_id='"+srs1.getString("id")+"' and storage_product_id=storage_product.id and desc_product.id=storage_product.[desc] and product=name");
				while(srs2.next()){
					tmp -= srs2.getDouble("size");
					marketProducts.add(new MarketProduct(srs2.getString("id"), "", srs2.getString("product"), srs2.getDouble("price"), srs2.getInt("quality"), srs2.getDouble("size"), srs2.getString("draw")));
				}
				if(tmp > 0)
					storageProducts.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(tmp)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue(), srs1.getString("draw")));
				fill += srs1.getDouble("size");
			}
			
			srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational,draw from storage_equipment,list_equipment,desc_equipment,info_equipment where storage='"+id+"' and storage_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
			ArrayList<StorageEquipment> storageEquipments = new ArrayList<StorageEquipment>();
			ArrayList<MarketEquipment> marketEquipments = new ArrayList<MarketEquipment>();
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select market_equipment.id,equipment,market_equipment.price,quality,durability,size,operational,draw from storage_equipment,market_equipment,desc_equipment,list_equipment,info_equipment where storage_equipment_id='"+srs1.getString("id")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and equipment=name");
				if(srs2.next()){
					marketEquipments.add(new MarketEquipment(srs2.getString("id"), "", srs2.getString("equipment"), srs2.getDouble("price"), srs2.getInt("quality"), srs2.getDouble("durability"), srs2.getDouble("size"), srs2.getDouble("operational"), srs2.getString("draw")));
				} else {
					storageEquipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
				}
				fill += srs1.getDouble("size");
			}
			data.add(gson.toJson(isAvailable));
			data.add(gson.toJson(capacity));
			data.add(gson.toJson(fill));
			data.add(gson.toJson(upgrade));
			data.add(gson.toJson(inc));
			data.add(gson.toJson(level+1));
			data.add(gson.toJson(storageProducts));
			data.add(gson.toJson(storageEquipments));
			data.add(gson.toJson(marketProducts));
			data.add(gson.toJson(marketEquipments));
			
			val = gson.toJson(data);
			
			storageProducts = null;
			storageEquipments = null;
			marketProducts = null;
			marketEquipments = null;
			
		} else {
			srs2 = db.getJdbc().queryForRowSet("select [value] from info_values where name='cost_storage'");
			if(srs2.next()){
				data.add(gson.toJson(isAvailable));
				data.add(gson.toJson(Double.parseDouble(srs2.getString("value"))));
				val = gson.toJson(data);
			} else return "0";
		}
		
		data = null;
		srs1 = null;
		srs2 = null;
		gc();
		
		return val;
	}
	
	public String refreshClientData(HttpServletRequest req) {
		SqlRowSet srs;
//		srs2 = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+srs1.getString("zone")+"'");
//		double propCost;
//		if(srs2.next()){
//			propCost = srs2.getDouble("cost");
//		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select sector,cost from user_sector_blueprint,info_sector where [user]='"+req.getParameter("user")+"' and sector=name");
		HashMap<String, Double> sectorCosts = new HashMap<String, Double>();
		while(srs.next()){
			sectorCosts.put(srs.getString("sector"), srs.getDouble("cost"));
		}
		
		String val = "0";
		srs = db.getJdbc().queryForRowSet("select money,rep,cost from [user],info_zone where name='"+req.getParameter("user")+"' and id=[zone]");
		User user;
		if(srs.next())
			user = new User("", "", "", "", srs.getDouble("money"), srs.getDouble("cost"), srs.getLong("rep"), "", 0, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), sectorCosts, new ArrayList<Installment>());
		else return "0";

		val = gson.toJson(user);
		
		srs = null;
		sectorCosts = null;
		user = null;
		
		gc();
		
		return val;
	}
	
	public String loadHeadquarterData(HttpServletRequest req) {
		String val = "0",contractType,user,zone;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select name,[level] from info_sector"),
				srs2,srs3;
		ArrayList<String> sectors = new ArrayList<String>();
		ArrayList<Integer> sectorsLvl = new ArrayList<Integer>();
		while(srs1.next()){
			sectors.add(srs1.getString("name"));
			sectorsLvl.add(srs1.getInt("level"));
		}
		
		srs1 = db.getJdbc().queryForRowSet("select [value] from info_values where name='sector'");
		double price;
		if(srs1.next()){
			price = Double.parseDouble(srs1.getString("value"));
		} else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select user_contract.id,request_storage,supplier_storage,product,quality,size,user_contract.price,turn from user_contract,storage,desc_product where accept='1' and [user]='"+req.getParameter("user")+"' and product_desc=desc_product.id and (request_storage=storage.id or supplier_storage=storage.id)");
		ArrayList<Contract> contracts = new ArrayList<Contract>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select [user],[zone] from storage where id='"+srs1.getString("request_storage")+"' union select [user],[zone] from storage where id='"+srs1.getString("supplier_storage")+"'");
			if(srs2.next()){
				if(srs2.getString("user").equals(req.getParameter("user"))){
					contractType = "from";
					srs2.next();
					user = srs2.getString("user");
					zone = srs2.getString("zone");
				} else {
					contractType = "to";
					user = srs2.getString("user");
					zone = srs2.getString("zone");
				}
				contracts.add(new Contract(srs1.getString("id"), user, zone, contractType, srs1.getString("product"), srs1.getInt("quality"), srs1.getDouble("size"), srs1.getDouble("price")));
			} else return "0";
		}
		
		srs1 = db.getJdbc().queryForRowSet("select user_contract.id,request_storage,supplier_storage,product,quality,size,user_contract.price,turn from user_contract,storage,desc_product where accept='0' and [user]='"+req.getParameter("user")+"' and product_desc=desc_product.id and (request_storage=storage.id or supplier_storage=storage.id)");
		ArrayList<Contract> pendingContracts = new ArrayList<Contract>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select [user],[zone] from storage where id='"+srs1.getString("request_storage")+"' union select [user],[zone] from storage where id='"+srs1.getString("supplier_storage")+"'");
			if(srs2.next()){
				if(srs2.getString("user").equals(req.getParameter("user"))){
					contractType = "from";
					srs2.next();
					user = srs2.getString("user");
					zone = srs2.getString("zone");
				} else {
					contractType = "to";
					user = srs2.getString("user");
					zone = srs2.getString("zone");
				}
				pendingContracts.add(new Contract(srs1.getString("id"), user, zone, contractType, srs1.getString("product"), srs1.getInt("quality"), srs1.getDouble("size"), srs1.getDouble("price")));
			} else return "0";
		}
		
		double sales = 0,raw = 0,electricity = 0,fixed = 0,wage = 0,
				operation = 0,transport = 0,retribution = 0,advertisement = 0,interest = 0,
				depreciation = 0,tax = 0;
		srs1 = db.getJdbc().queryForRowSet("select type,total from user_finance where [user]='"+req.getParameter("user")+"'");
		while(srs1.next()){
			if(srs1.getString("type").equals("Sales"))
				sales = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Raw Material"))
				raw = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Electricity"))
				electricity = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Fixed"))
				fixed = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Wage"))
				wage = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Operation"))
				operation = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Transport"))
				transport = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Retribution"))
				retribution = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Advertisement"))
				advertisement = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Interest"))
				interest = srs1.getDouble("total");
			else if(srs1.getString("type").equals("Depreciation"))
				depreciation = srs1.getDouble("total");
		}
		
		srs1 = db.getJdbc().queryForRowSet("select [value] from info_values where name='tax'");
		if(srs1.next())
			tax = Double.parseDouble(srs1.getString("value"));
		else return "0";
		
		double cash,rawOnStorage = 0,equipmentOnStorage = 0,loan = 0,storage = 0,equipment = 0,sector = 0,
			tmpd1,tmpd2,tmpd3;
		srs1 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		if(srs1.next())
			cash = srs1.getDouble("money");
		else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select [value] from info_values where name='cost_storage' union select [value] from info_values where name='cost_storage_upgrade'");
		if(srs1.next())
			tmpd1 = Double.parseDouble(srs1.getString("value"));
		else return "0";
		if(srs1.next())
			tmpd2 = Double.parseDouble(srs1.getString("value"));
		else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select id,[level] from storage where [user]='"+req.getParameter("user")+"'");
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select size,avg_price from storage_product where storage='"+srs1.getString("id")+"'");
			while(srs2.next()){
				rawOnStorage += (srs2.getDouble("size")*srs2.getDouble("avg_price"));
			}
			srs2 = db.getJdbc().queryForRowSet("select buy_price,durability from storage_equipment,list_equipment where storage='"+srs1.getString("id")+"' and storage_equipment.id=list_equipment.id");
			while(srs2.next()){
				equipmentOnStorage += srs2.getDouble("buy_price")*(srs2.getDouble("durability")/100.00);
			}
			
			storage += tmpd1;
			for(int i=1;i<srs1.getLong("level");i++){
				storage += tmpd2;
			}
		}
		
		srs1 = db.getJdbc().queryForRowSet("select borrow from borrow_bank where [user]='"+req.getParameter("user")+"'");
		while(srs1.next()){
			loan += srs1.getDouble("borrow")*-1;
		}
		
		srs1 = db.getJdbc().queryForRowSet("select installment.id,info_zone.cost,info_sector.cost from installment,info_zone,info_sector where [user]='"+req.getParameter("user")+"' and info_zone.id=[zone] and info_sector.name=type");
		while(srs1.next()){
			sector += srs1.getDouble(2)+srs1.getDouble(3);
			srs2 = db.getJdbc().queryForRowSet("select buy_price,durability from installment_equipment,list_equipment where installment='"+srs1.getString(1)+"' and installment_equipment.id=list_equipment.id");
			while(srs2.next()){
				equipment += srs2.getDouble("buy_price")*(srs2.getDouble("durability")/100.00);
			}			
		}
		
		srs1 = db.getJdbc().queryForRowSet("select name from info_product");
		ArrayList<String> products = new ArrayList<String>();
		while(srs1.next()){
			products.add(srs1.getString("name"));
		}
		
		srs1 = db.getJdbc().queryForRowSet("select id,advertise,price from desc_advertisement");
		ArrayList<String> advertises = new ArrayList<String>(),
				idAds = new ArrayList<String>();
		ArrayList<Double> prices = new ArrayList<Double>();
		while(srs1.next()){
			idAds.add(srs1.getString("id"));
			advertises.add(srs1.getString("advertise"));
			prices.add(srs1.getDouble("price"));
		}
		
		srs1 = db.getJdbc().queryForRowSet("select name from businessgame.dbo.[user] where [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"')");
		ArrayList<String> players = new ArrayList<String>();
		ArrayList<Double> assets = new ArrayList<Double>();
		while(srs1.next()){
			tmpd3 = 0;
			srs2 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+srs1.getString("name")+"'");
			if(srs2.next())
				tmpd3 += srs2.getDouble("money");
			else return "0";
			
			srs2 = db.getJdbc().queryForRowSet("select id,[level] from storage where [user]='"+srs1.getString("name")+"'");
			while(srs2.next()){
				srs3 = db.getJdbc().queryForRowSet("select size,avg_price from storage_product where storage='"+srs2.getString("id")+"'");
				while(srs3.next()){
					tmpd3 += (srs3.getDouble("size")*srs3.getDouble("avg_price"));
				}
				srs3 = db.getJdbc().queryForRowSet("select buy_price,durability from storage_equipment,list_equipment where storage='"+srs2.getString("id")+"' and storage_equipment.id=list_equipment.id");
				while(srs3.next()){
					tmpd3 += srs3.getDouble("buy_price")*(srs3.getDouble("durability")/100.00);
				}
				
				tmpd3 += tmpd1;
				for(int i=1;i<srs2.getLong("level");i++){
					tmpd3 += tmpd2*i;
				}
			}
			
			srs2 = db.getJdbc().queryForRowSet("select borrow from borrow_bank where [user]='"+srs1.getString("name")+"'");
			while(srs2.next()){
				tmpd3 += srs2.getDouble("borrow")*-1;
			}
			
			srs2 = db.getJdbc().queryForRowSet("select installment.id,info_zone.cost,info_sector.cost from installment,info_zone,info_sector where [user]='"+srs1.getString("name")+"' and info_zone.id=[zone] and info_sector.name=type");
			while(srs2.next()){
				tmpd3 += srs2.getDouble(2)+srs2.getDouble(3);
				srs3 = db.getJdbc().queryForRowSet("select buy_price,durability from installment_equipment,list_equipment where installment='"+srs2.getString(1)+"' and installment_equipment.id=list_equipment.id");
				while(srs3.next()){
					tmpd3 += srs3.getDouble("buy_price")*(srs3.getDouble("durability")/100.00);
				}			
			}
			players.add(srs1.getString("name"));
			assets.add(tmpd3);
		}
		
		for(int i=0;i<assets.size();i++){
			for(int j=i+1;j<assets.size();j++){
				if(assets.get(i) < assets.get(j)){
					user = players.get(i);
					tmpd3 = assets.get(i);
					players.set(i, players.get(j));
					assets.set(i, assets.get(j));
					players.set(j, user);
					assets.set(j, tmpd3);
				}
			}
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(sectors));
		data.add(gson.toJson(sectorsLvl));
		data.add(gson.toJson(price));
		data.add(gson.toJson(contracts));
		data.add(gson.toJson(pendingContracts));
		data.add(gson.toJson(sales));
		data.add(gson.toJson(raw));
		data.add(gson.toJson(electricity));
		data.add(gson.toJson(fixed));
		data.add(gson.toJson(wage));
		data.add(gson.toJson(operation));
		data.add(gson.toJson(transport));
		data.add(gson.toJson(retribution));
		data.add(gson.toJson(advertisement));
		data.add(gson.toJson(interest));
		data.add(gson.toJson(depreciation));
		data.add(gson.toJson(tax));
		data.add(gson.toJson(cash));
		data.add(gson.toJson(rawOnStorage));
		data.add(gson.toJson(equipmentOnStorage));
		data.add(gson.toJson(loan));
		data.add(gson.toJson(storage));
		data.add(gson.toJson(equipment));
		data.add(gson.toJson(sector));
		data.add(gson.toJson(products));
		data.add(gson.toJson(idAds));
		data.add(gson.toJson(advertises));
		data.add(gson.toJson(prices));
		data.add(gson.toJson(players));
		
		val = gson.toJson(data);
		
		sectors = null;
		sectorsLvl = null;
		contracts = null;
		pendingContracts = null;
		data = null;
		contractType = null;
		user = null;
		products = null;
		advertises = null;
		prices = null;
		players = null;
		assets = null;
		zone = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}
	
	public String loadMarketContent(HttpServletRequest req) {
		String val = "0";
		SqlRowSet srs;

//		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.[user],product,market_product.price,quality,market_product.size,draw from market_product,storage_product,desc_product,storage,info_product where market_product.[zone]='"+req.getParameter("zone")+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.[desc] and storage.id=storage_product.storage and product=name union select market_product.id,'',product,market_product.price,quality,market_product.size,draw from market_product,desc_product,info_product where market_product.[zone]='"+req.getParameter("zone")+"' and desc_product.id=market_product.[desc] and product=name");
		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.[user],product,market_product.price,quality,market_product.size,draw from market_product,storage_product,desc_product,storage,info_product where market_product.[zone]='"+req.getParameter("zone")+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.[desc] and storage.id=storage_product.storage and product=name");
		ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
		while(srs.next()){
			products.add(new MarketProduct(srs.getString("id"), srs.getString("user"), srs.getString("product"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("size"), srs.getString("draw")));
		}

//		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.[user],equipment,market_equipment.price,quality,durability,size,operational,draw from market_equipment,storage_equipment,desc_equipment,list_equipment,storage,info_equipment where market_equipment.[zone]='"+req.getParameter("zone")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and storage.id=storage_equipment.storage and equipment=name union select market_equipment.id,'',equipment,market_equipment.price,quality,durability,size,operational,draw from market_equipment,desc_equipment,list_equipment,info_equipment where market_equipment.[zone]='"+req.getParameter("zone")+"' and list_equipment.id=market_equipment.[desc] and list_equipment.[desc]=desc_equipment.id and equipment=name");
		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.[user],equipment,market_equipment.price,quality,durability,size,operational,draw from market_equipment,storage_equipment,desc_equipment,list_equipment,storage,info_equipment where market_equipment.[zone]='"+req.getParameter("zone")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and storage.id=storage_equipment.storage and equipment=name");
		ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
		while(srs.next()){
			equipments.add(new MarketEquipment(srs.getString("id"), srs.getString("user"), srs.getString("equipment"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational"), srs.getString("draw")));
		}

		srs = db.getJdbc().queryForRowSet("select market_employee.id,employee,market_employee.price,quality,operational,draw from market_employee,desc_employee,list_employee,info_employee where [zone]='"+req.getParameter("zone")+"' and list_employee.id=market_employee.[desc] and desc_employee.id=list_employee.[desc] and name=employee");
		ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
		while(srs.next()){
			employees.add(new MarketEmployee(srs.getString("id"), srs.getString("employee"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("operational"), srs.getString("draw")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(products));
		data.add(gson.toJson(equipments));
		data.add(gson.toJson(employees));
		
		val = gson.toJson(data);
		
		products = null;
		equipments = null;
		employees = null;
		data = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String getSuggestedPrice(HttpServletRequest req) {
		//Suggested price still based on info_product
		//Lately, it must use a DSS based, like AHP, or any easier method..
		String val = "0";
		double price = 0;
		
		//Deciding price starts here :
		System.out.println(req.getParameter("id"));
		System.out.println(req.getParameter("id").substring(0, 2));
		if(req.getParameter("id").substring(0, 2).equals("PR")){
			val = "select price from desc_product where id=(select storage_product.[desc] from storage_product where id='"+req.getParameter("id")+"')";
		}
		
		else if(req.getParameter("id").substring(0, 2).equals("EQ")){
			val = "select price from desc_equipment where id=(select list_equipment.[desc] from list_equipment where id='"+req.getParameter("id")+"')";
		}
		
		SqlRowSet srs = db.getJdbc().queryForRowSet(val);
		if(srs.next()) 
			price = srs.getDouble("price");
		else return "0";
		
		//Deciding price ends here.

		srs = db.getJdbc().queryForRowSet("select [zone] from user_market_license where [user]='"+req.getParameter("user")+"'");
		ArrayList<String> marketZone = new ArrayList<String>();
		while(srs.next()){
			marketZone.add(srs.getString("zone"));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(price));
		data.add(gson.toJson(marketZone));
		
		val = gson.toJson(data);
		
		marketZone = null;
		data = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String loadSectorOwned(HttpServletRequest req) {
		String val = "0";
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select sector,cost from user_sector_blueprint,info_sector where [user]='"+req.getParameter("user")+"' and sector=name");
		ArrayList<String> userSectors = new ArrayList<String>();
		ArrayList<Double> sectorCosts = new ArrayList<Double>();
		while(srs.next()){
			userSectors.add(srs.getString("sector"));
			sectorCosts.add(srs.getDouble("cost"));
		}
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"')");
		double price;
		if(srs.next()){
			price = srs.getDouble("cost");
		} else return "0";
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(userSectors));
		data.add(gson.toJson(sectorCosts));
		data.add(gson.toJson(price));
		
		val = gson.toJson(data);
		
		userSectors = null;
		sectorCosts = null;
		data = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String loadInstallmentOwnedByUser(HttpServletRequest req) {
		String val = gson.toJson(getUserInstallments(req.getParameter("user")));
//		String val = "0",hiElement="";
//
//		double hiVal = 0,tmpd1,tmpd2;
//		int eff;
//		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,[zone],type,draw from installment,info_sector where [user]='"+req.getParameter("user")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"') and name=type"),
//				srs2,srs3;
//		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
//				elements = new HashMap<String, Double>(),
//				elementsCalc = new HashMap<String, Double>();
//		ArrayList<Installment> installments = new ArrayList<Installment>();
//		boolean pass = false;
//		
//		while(srs1.next()){
//			hiElement="";
//			hiVal=0;
//			elementsRatio.clear();
//			elements.clear();
//			elementsCalc.clear();
//			pass = true;
//			
//			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
//			while(srs2.next()){
//				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
//				if(hiVal < srs2.getDouble("items")){
//					hiElement = srs2.getString("equipment_type");
//					hiVal = srs2.getDouble("items");
//				}
//				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+srs1.getString("id")+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
//				srs3.next();
//				elements.put(srs2.getString("equipment_type"), srs3.getDouble(1));
//			}
//			
//			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
//			while(srs2.next()){
//				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
//				if(hiVal < srs2.getDouble("items")){
//					hiElement = srs2.getString("employee_type");
//					hiVal = srs2.getDouble("items");
//				}
//				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+srs1.getString("id")+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
//				srs3.next();
//				elements.put(srs2.getString("employee_type"), srs3.getDouble(1));
//			}
//			
//			//calculating:
//			while(true){
//				for(String element : elementsRatio.keySet()){
//					if(element.equals(hiElement)){
//						elementsCalc.put(element, elements.get(element));
//					} else {
//						elementsCalc.put(element, (elementsRatio.get(element)*elements.get(hiElement))/elementsRatio.get(hiElement));
//					}
//				}
//				
//				for(String element : elements.keySet()){
//					if(elements.get(element) < elementsCalc.get(element)){
//						pass = false;
//						hiElement = element;
//						hiVal = elements.get(element);
//						break;
//					} else {
//						pass = true;
//					}
//				}
//				if(pass){
//					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
//					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
//						hiVal = (elementsRatio.get(hiElement)*(eff+1));
//						if(hiVal > 0)
//							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
//						else tmpd1 = 0;
//						tmpd2 = eff+1;
//					} else {
//						hiVal = (elementsRatio.get(hiElement)*eff);
//						if(hiVal > 0)
//							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
//						else tmpd1 = 0;
//						tmpd2 = eff;
//					}
//					installments.add(new Installment(srs1.getString("id"), srs1.getString("type"), srs1.getString("zone"), tmpd1, tmpd2, srs1.getString("draw")));
//					break;
//				}
//			}
//		}
//		
//		val = gson.toJson(installments);
//		
//		installments = null;
//		hiElement = null;
//		srs1 = null;
//		srs2 = null;
//		srs3 = null;
//		elements = null;
//		elementsCalc = null;
//		elementsRatio = null;
//		
//		gc();
//		
		return val;
	}
	
	public String loadInstallmentDetails(HttpServletRequest req) {
		String val = "0";
		ArrayList<InstallmentEmployee> employees = new ArrayList<InstallmentEmployee>();
		ArrayList<InstallmentEquipment> equipments = new ArrayList<InstallmentEquipment>();
		ArrayList<String> data = new ArrayList<String>();
		
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select installment_employee.id,employee,quality,operational,draw from installment_employee,list_employee,desc_employee,info_employee where installment='"+req.getParameter("id")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id and name=employee"),
				srs2;
		
		while(srs1.next()){
			employees.add(new InstallmentEmployee(srs1.getString("id"), srs1.getString("employee"), srs1.getInt("quality"), srs1.getDouble("operational"), srs1.getString("draw")));
		}
		
		srs1 = db.getJdbc().queryForRowSet("select installment_equipment.id,equipment,quality,durability,size,operational,draw from installment_equipment,desc_equipment,list_equipment,info_equipment where installment='"+req.getParameter("id")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
		while(srs1.next()){
			equipments.add(new InstallmentEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
		}
		
		ArrayList<String> installmentIOdata = calculateInstallmentAndIOByIdInstallment(req.getParameter("id"));
		data.add(installmentIOdata.get(0));
		data.add(installmentIOdata.get(1));
		data.add(installmentIOdata.get(2));
		data.add(installmentIOdata.get(3));
		data.add(installmentIOdata.get(4));
		data.add(installmentIOdata.get(5));
		data.add(installmentIOdata.get(6));
		data.add(installmentIOdata.get(7));
		data.add(gson.toJson(equipments));
		data.add(gson.toJson(employees));
		
		if(installmentIOdata.get(0).equals("Petrol Power Plant")){
			srs1 = db.getJdbc().queryForRowSet("select subscription,tariff from installment where id='"+req.getParameter("id")+"'");
			double tariff,subscription;
			if(srs1.next()){
				subscription = srs1.getDouble("subscription");
				tariff = srs1.getDouble("tariff");
			}
			else return "0";
			
			srs1 = db.getJdbc().queryForRowSet("select id,type,[user],planned_supply from installment where supply='"+req.getParameter("id")+"'");
			ArrayList<String> types = new ArrayList<String>(),
					users = new ArrayList<String>(),
					idSupplies = new ArrayList<String>();
			ArrayList<Double> supplies = new ArrayList<Double>();
			while(srs1.next()){
				idSupplies.add(srs1.getString("id"));
				types.add(srs1.getString("type"));
				users.add(srs1.getString("user"));
				supplies.add(srs1.getDouble("planned_supply"));
			}
			
			data.add(gson.toJson(subscription));
			data.add(gson.toJson(tariff));
			data.add(gson.toJson(types));
			data.add(gson.toJson(users));
			data.add(gson.toJson(supplies));
			data.add(gson.toJson(idSupplies));
			
			val = gson.toJson(data);
			
			types = null;
			users = null;
			supplies = null;
			idSupplies = null;
			
		} else {
			srs1 = db.getJdbc().queryForRowSet("select supply,planned_supply from installment where id='"+req.getParameter("id")+"'");
			ArrayList<String> idSupplies = new ArrayList<String>(),
					users = new ArrayList<String>(),
					tmpSupplies;
			ArrayList<Double> subscriptions = new ArrayList<Double>(), 
					tariffs = new ArrayList<Double>(),
					availables = new ArrayList<Double>();
			JsonParser parser = new JsonParser();
			JsonArray array1;
			int tmp;
			double available,currentKwh;
			String currentSupply;
			
			if(srs1.next()){
				currentKwh = srs1.getDouble("planned_supply");
				currentSupply = srs1.getString("supply");
			}
			else return "0";
			
			srs1 = db.getJdbc().queryForRowSet("select id,[user],subscription,tariff from installment where type='Petrol Power Plant'");
			while(srs1.next()){
				tmp = 0;
				tmpSupplies = calculateInstallmentAndIOByIdInstallment(srs1.getString("id"));
				array1 = parser.parse(tmpSupplies.get(5)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					if((new Gson().fromJson(array1.get(i), String.class)).equals("Energy")){
						tmp = i;
						break;
					}
				}
				array1 = parser.parse(tmpSupplies.get(6)).getAsJsonArray();
				available = new Gson().fromJson(array1.get(tmp), Double.class);
				srs2 = db.getJdbc().queryForRowSet("select planned_supply from installment where supply='"+srs1.getString("id")+"'");
				while(srs2.next())
					available -= srs2.getDouble("planned_supply");
				
				idSupplies.add(srs1.getString("id"));
				users.add(srs1.getString("user"));
				subscriptions.add(srs1.getDouble("subscription"));
				tariffs.add(srs1.getDouble("tariff"));
				availables.add(new BigDecimal(Double.valueOf(available)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
				tmpSupplies = null;
			}
			
			data.add(gson.toJson(idSupplies));
			data.add(gson.toJson(users));
			data.add(gson.toJson(subscriptions));
			data.add(gson.toJson(tariffs));
			data.add(gson.toJson(availables));
			data.add(gson.toJson(currentKwh));
			data.add(gson.toJson(currentSupply));
			
			val = gson.toJson(data);
			
			idSupplies = null;
			users = null;
			tariffs = null;
			availables = null;
			currentSupply = null;
		}
		
		installmentIOdata = null;
		employees = null;
		equipments = null;
		data = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}
	
	public String loadInstallmentOwnedByEquipment(HttpServletRequest req) {
		String val = "0", hiElement="", equipmentType;
		double hiVal = 0, tmpd1,tmpd2;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select equipment from list_equipment,desc_equipment where list_equipment.id='"+req.getParameter("id")+"' and list_equipment.[desc]=desc_equipment.id"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		ArrayList<Installment> installments = new ArrayList<Installment>();
		boolean pass = false;
		
		srs1.next();
		equipmentType = srs1.getString("equipment");
		
		srs1 = db.getJdbc().queryForRowSet("select installment.id,[zone],type,draw,active from info_sector_equipment,installment,info_sector where equipment_type='"+equipmentType+"' and [user]='"+req.getParameter("user")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"') and installment.type=sector and type=name");
		while(srs1.next()){
			hiElement="";
			hiVal=0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+srs1.getString("id")+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
				srs3.next();
				elements.put(srs2.getString("equipment_type"), srs3.getDouble(1));
			}
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+srs1.getString("id")+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				srs3.next();
				elements.put(srs2.getString("employee_type"), srs3.getDouble(1));
			}
			
			//calculating:
			while(true){
				for(String element : elementsRatio.keySet()){
					if(element.equals(hiElement)){
						elementsCalc.put(element, elements.get(element));
					} else {
						elementsCalc.put(element, (elementsRatio.get(element)*elements.get(hiElement))/elementsRatio.get(hiElement));
					}
				}
				
				for(String element : elements.keySet()){
					if(elements.get(element) < elementsCalc.get(element)){
						pass = false;
						hiElement = element;
						hiVal = elements.get(element);
						break;
					} else {
						pass = true;
					}
				}
				if(pass){
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0; 
						tmpd2 = eff+1;
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0; 
						tmpd2 = eff;
					}
					installments.add(new Installment(srs1.getString("id"), srs1.getString("type"), srs1.getString("zone"), tmpd1, tmpd2, srs1.getString("draw"), srs1.getBoolean("active")));
					break;
				}
			}
		}
		val = gson.toJson(installments);
		
		hiElement = null;
		elementsRatio = null;
		elements = null;
		elementsCalc = null;
		installments = null;
		
		gc();
		
		return val;
	}
	
	public String queryTotalBundle(HttpServletRequest req) {
		String val="";
		int quality;
		double total = 0;
		ArrayList<String> data = new ArrayList<String>();
		
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select type from installment where id='"+req.getParameter("installment")+"'"),
				srs2;
		if(srs1.next()){
			if(req.getParameter("quality") == null){
				srs2 = db.getJdbc().queryForRowSet("select quality from info_quality where from_base='1'");
				if(srs2.next())
					quality = srs2.getInt("quality");
				else return "0";
			} else quality = Integer.parseInt(req.getParameter("quality"));
			
			srs2 = db.getJdbc().queryForRowSet("select price,items from desc_equipment,info_sector_equipment where sector='"+srs1.getString("type")+"' and quality='"+quality+"' and desc_equipment.equipment=equipment_type");
			while(srs2.next()){
				total += srs2.getDouble("price")*srs2.getInt("items");
			}
			
			srs2 = db.getJdbc().queryForRowSet("select price,items from desc_employee,info_sector_employee where sector='"+srs1.getString("type")+"' and quality='"+quality+"' and desc_employee.employee=employee_type");
			while(srs2.next()){
				total += srs2.getDouble("price")*srs2.getInt("items");
			}
			
			data.add(srs1.getString("type"));
			data.add(gson.toJson(total));
			
			val = gson.toJson(data);
			
		} else return "0";
		
		srs1 = null;
		srs2 = null;
		data = null;
		
		gc();
		
		return val;
	}
	
	public String loadUserData(HttpServletRequest req) {
		String val = "";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id,sender,message,unread from user_message where recipient='"+req.getParameter("user")+"'");
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<Message> messages = new ArrayList<Message>();
		while(srs.next()){
			messages.add(new Message(srs.getString("id"), srs.getString("sender"), srs.getString("message"), (srs.getBoolean("unread"))));
		}
		
		data.add(gson.toJson(messages));
		val = gson.toJson(data);
		
		messages = null;
		data = null;
		
		gc();
		
		return val;
	}
	
	public String loadPlayerInfo(HttpServletRequest req) {
		String val = "",email,dob,about;
		long rep;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select email,dob,about,rep from businessgame.dbo.[user] where name='"+req.getParameter("player")+"'"),
				srs2,srs3;
		if(srs1.next()){
			email = srs1.getString("email");
			dob = srs1.getString("dob");
			about = srs1.getString("about");
			rep = srs1.getLong("rep");
		} else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select type from installment where [user]='"+req.getParameter("player")+"'");
		ArrayList<String> installments = new ArrayList<String>(),
				outputs = new ArrayList<String>();
		ArrayList<ArrayList<Double>> prices = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> tmpPrices;
		while(srs1.next()){
			if(!installments.contains(srs1.getString("type")))
				installments.add(srs1.getString("type"));
			srs2 = db.getJdbc().queryForRowSet("select output_type from info_sector_output where sector='"+srs1.getString("type")+"' and output_type!='Energy'");
			while(srs2.next()){
				if(!outputs.contains(srs2.getString("output_type"))){
					tmpPrices = new ArrayList<Double>();
					outputs.add(srs2.getString("output_type"));
					srs3 = db.getJdbc().queryForRowSet("select price from desc_product where product='"+srs2.getString("output_type")+"' order by quality asc");
//					System.out.println("select price from desc_product where product='"+srs2.getString("output_type")+"' order by quality asc");
					while(srs3.next()){
//						System.out.println(srs3.getDouble("price"));
						tmpPrices.add(srs3.getDouble("price"));
					}
					prices.add(tmpPrices);
					tmpPrices = null;
				}
			}
		}
		
		srs1 = db.getJdbc().queryForRowSet("select quality from info_quality order by quality asc");
		ArrayList<Integer> qualities = new ArrayList<Integer>();
		while(srs1.next()){
			qualities.add(srs1.getInt("quality"));
		}
		
		for(int i=0;i<outputs.size();i++){
			for(int j=0;j<qualities.size();j++){
//				System.out.println(outputs.get(i)+" - "+qualities.get(j)+" at "+prices.get(i).get(j));
			}
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(email);
		data.add(dob);
		data.add(about);
		data.add(gson.toJson(rep));
		data.add(gson.toJson(installments));
		data.add(gson.toJson(outputs));
		data.add(gson.toJson(qualities));
		data.add(gson.toJson(prices));
		
		val = gson.toJson(data);
		
		srs1 = null;
		data = null;
		email = null;
		about = null;
		installments = null;
		outputs = null;
		qualities = null;
		prices = null;
		tmpPrices = null;
		
		gc();
		
		return val;
	}
	
	public String loadInstallmentOwnedByUserFromSelectedType(HttpServletRequest req) {
		System.out.println("Tes");
		String val = "0",hiElement="";
//		val = gson.toJson(calculateInstallmentByUser(req.getParameter("user")));
		double hiVal = 0,tmpd1,tmpd2;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,[zone],type,draw,active from installment,info_sector where [user]='"+req.getParameter("user")+"' and type='"+req.getParameter("type")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"') and name=type"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		ArrayList<Installment> installments = new ArrayList<Installment>();
		boolean pass = false;
		
		while(srs1.next()){
			hiElement="";
			hiVal=0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+srs1.getString("id")+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
				srs3.next();
				elements.put(srs2.getString("equipment_type"), srs3.getDouble(1));
			}
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+srs1.getString("id")+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				srs3.next();
				elements.put(srs2.getString("employee_type"), srs3.getDouble(1));
			}
			
			//calculating:
			while(true){
				for(String element : elementsRatio.keySet()){
					if(element.equals(hiElement)){
						elementsCalc.put(element, elements.get(element));
					} else {
						elementsCalc.put(element, (elementsRatio.get(element)*elements.get(hiElement))/elementsRatio.get(hiElement));
					}
				}
				
				for(String element : elements.keySet()){
					if(elements.get(element) < elementsCalc.get(element)){
						pass = false;
						hiElement = element;
						hiVal = elements.get(element);
						break;
					} else {
						pass = true;
					}
				}
				if(pass){
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0;
						tmpd2 = eff+1;
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0;
						tmpd2 = eff;
					}
					installments.add(new Installment(srs1.getString("id"), srs1.getString("type"), srs1.getString("zone"), tmpd1, tmpd2, srs1.getString("draw"), srs1.getBoolean("active")));
					break;
				}
			}
		}
		
		val = gson.toJson(installments);
		
		installments = null;
		hiElement = null;
		srs1 = null;
		srs2 = null;
		srs3 = null;
		elements = null;
		elementsCalc = null;
		elementsRatio = null;
		
		gc();
		
		return val;
	}
	
	public String calculateFixPrice(HttpServletRequest req) {
		String val = "";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select durability,buy_price from list_equipment where id='"+req.getParameter("id")+"'");
		double price;
		if(srs.next())
			price = ((100 - srs.getDouble("durability"))/100)*srs.getDouble("buy_price");
		else return "0";
		
		val = gson.toJson(price);
		
		return val;
	}
	
	public String getBorrowedMoney(HttpServletRequest req) {
		String val = "";
		double borrow;
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select borrow from borrow_bank where [user]='"+req.getParameter("user")+"'");
		if(srs.next())
			borrow = srs.getDouble("borrow");
		else return "1";
		
		val = gson.toJson(borrow);
			
		return val;
	}
	
	public String deleteUserData(HttpServletRequest req) {
		String val="Ok",sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1,srs2;
		
		sqlL.add("delete from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		sqlL.add("delete from user_market_license where [user]='"+req.getParameter("user")+"'");
		sqlL.add("delete from user_sector_blueprint where [user]='"+req.getParameter("user")+"'");
		sqlL.add("delete from req_borrow_bank where [user]='"+req.getParameter("user")+"'");
		sqlL.add("delete from borrow_bank where [user]='"+req.getParameter("user")+"'");
		sqlL.add("delete from product_advertisement where [user]='"+req.getParameter("user")+"'");
		sqlL.add("delete from user_message where sender='"+req.getParameter("user")+"' or recipient='"+req.getParameter("user")+"'");
		sqlL.add("delete from user_finance where [user]='"+req.getParameter("user")+"'");
		
		srs1 = db.getJdbc().queryForRowSet("select id from storage where [user]='"+req.getParameter("user")+"'");
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from storage_product where storage='"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from market_product where storage_product_id='"+srs2.getString("id")+"'");
			}
			sqlL.add("delete from storage_product where storage='"+srs1.getString("id")+"'");
			
			srs2 = db.getJdbc().queryForRowSet("select id from storage_equipment where storage='"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from market_equipment where storage_equipment_id='"+srs2.getString("id")+"'");
				sqlL.add("delete from list_equipment where id='"+srs2.getString("id")+"'");
			}
			sqlL.add("delete from storage_equipment where storage='"+srs1.getString("id")+"'");
			sqlL.add("delete from user_contract where request_storage='"+srs1.getString("id")+"' or supplier_storage='"+srs1.getString("id")+"'");
		}
		
		sqlL.add("delete from storage where [user]='"+req.getParameter("user")+"'");
		
		srs1 = db.getJdbc().queryForRowSet("select id from installment where [user]='"+req.getParameter("user")+"'");
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from installment_equipment where installment='"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from list_equipment where id='"+srs2.getString("id")+"'");
			}
			sqlL.add("delete from installment_equipment where installment='"+srs1.getString("id")+"'");
			
			srs2 = db.getJdbc().queryForRowSet("select id from installment_employee where installment='"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from list_employee where id='"+srs2.getString("id")+"'");
			}
			sqlL.add("delete from installment_employee where installment='"+srs1.getString("id")+"'");
		}
		
		sqlL.add("delete from installment where [user]='"+req.getParameter("user")+"'");
		
		for(String x : sqlL)
			System.out.println(x);
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}
	
	/* Get function ends here : */
	
	/* Post function starts here : */
	
	public String loginUser(HttpServletRequest req){
		String val = "Ok";
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select * from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'"),
				srs2;
		User userAcc;
		if(srs1.next()){
			if(req.getParameter("pass").equals(srs1.getString("pass"))){
				HashMap<String, String> storages = new HashMap<String, String>(),
						marketLicenses = new HashMap<String, String>(),
						sectorBlueprints = new HashMap<String, String>();
				HashMap<String, Double> sectorCost = new HashMap<String, Double>();
				ArrayList<Installment> installments = getUserInstallments(req.getParameter("user"));
				
				srs2 = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+srs1.getString("zone")+"'");
				double propCost;
				if(srs2.next()){
					propCost = srs2.getDouble("cost");
				} else return "0";
				
				srs2 = db.getJdbc().queryForRowSet("select id,[zone] from storage where [user]='"+req.getParameter("user")+"'");
				while(srs2.next()){
					storages.put(srs2.getString("zone"), srs2.getString("id"));
				}
				
				srs2 = db.getJdbc().queryForRowSet("select id,[zone] from user_market_license where [user]='"+req.getParameter("user")+"'");
				while(srs2.next()){
					marketLicenses.put(srs2.getString("zone"), srs2.getString("id"));
				}
				
				System.out.println(marketLicenses);
				
				srs2 = db.getJdbc().queryForRowSet("select id,sector,cost from user_sector_blueprint,info_sector where [user]='"+req.getParameter("user")+"' and sector=name");
				while(srs2.next()){
					sectorBlueprints.put(srs2.getString("sector"), srs2.getString("id"));
					sectorCost.put(srs2.getString("sector"), srs2.getDouble("cost"));
				}
				
				userAcc = new User(srs1.getString("name"), srs1.getString("email"), srs1.getString("dob"), srs1.getString("about"), srs1.getDouble("money"), propCost, srs1.getLong("rep"), srs1.getString("zone"), srs1.getInt("level"), storages, marketLicenses, sectorBlueprints, sectorCost, installments);
				val = gson.toJson(userAcc);
				
				storages = null;
				marketLicenses = null;
				sectorBlueprints = null;
				sectorCost = null;
				installments = null;
				
			} else val = "0";
			
		} else val = "1";
		
		userAcc = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}

	public String registerUser(HttpServletRequest req) {
		String val = "",sqls[],idInc;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		ArrayList<String> sqlL = new ArrayList<String>();
		
		if(srs.next())
			return "1";
		sqlL.add("insert into businessgame.dbo.[user] values ('"+req.getParameter("user")+"','"+req.getParameter("pass")+"','"+req.getParameter("email")+"','"+req.getParameter("dob")+"','This is me',0.00,0,'"+req.getParameter("zone")+"',1)");
		
		idInc = getUniqueIncrementIdNew("user_market_license");
		
		HashMap<String, String> marketLicense = new HashMap<String, String>();
		sqlL.add("insert into businessgame.dbo.[user_market_license] values ('"+KEY_USER_MARKET_LICENSE+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"')");
		marketLicense.put(req.getParameter("zone"), KEY_USER_MARKET_LICENSE+idInc);
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+req.getParameter("zone")+"'");
		double propCost;
		if(srs.next()){
			propCost = srs.getDouble("cost");
		} else return "0";
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		User user = new User(req.getParameter("user"), req.getParameter("email"), req.getParameter("dob"), "This is me", 0.00, propCost, 0, req.getParameter("zone"), 1, new HashMap<String, String>(), marketLicense, new HashMap<String, String>(), new HashMap<String, Double>(), new ArrayList<Installment>());
		
		val = gson.toJson(user);
		
		sqls = null;
		sqlL = null;
		srs = null;
		idInc = null;
		user = null;
		
		gc();
		
		return val;
	}

	public String submitProposal(HttpServletRequest req) {
		String val = "0", turn, idInc, zone;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='turn'");
		
		if(srs.next())
			turn = srs.getString("value");
		else return val;
		
		srs = db.getJdbc().queryForRowSet("select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		if(srs.next())
			zone = srs.getString("zone");
		else return val;
		
		idInc = getUniqueIncrementIdNew("req_borrow_bank");
		
		db.getJdbc().execute("insert into req_borrow_bank values ('"+KEY_REQUEST_BORROW_BANK+idInc+"','"+req.getParameter("user")+"','"+turn+"','"+req.getParameter("sector")+"','"+req.getParameter("turn")+"','"+(Boolean.parseBoolean(req.getParameter("storage"))? 1 : 0)+"','"+zone+"')");
		val = "Ok";
		
		turn = null;
		zone = null;
		idInc = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String buildUserStorage(HttpServletRequest req) {
		String val = "0", idInc, sqls[];
		double money,price;
		SqlRowSet srs;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		srs = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='cost_storage'");
		
		if(srs.next())
			price = Double.parseDouble(srs.getString("value"));
		else return "0";
		
		if(money < price)
			return "1";
		
		money -= price;
		
		idInc = getUniqueIncrementIdNew("storage");
		
		sqlL.add("insert into storage values ('"+KEY_STORAGE+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"','1')");
		sqlL.add("update businessgame.dbo.[user] set money='"+money+"' where name='"+req.getParameter("user")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(KEY_STORAGE+idInc);
		data.add(gson.toJson(money));
		data.add(checkUserStorage(req));
		
		val = gson.toJson(data);
		
		data = null;
		sqlL = null;
		sqls = null;
		srs = null;
		idInc = null;
		
		gc();
		
		return val;
	}
	
	public String buyMarketProduct(HttpServletRequest req) {
		String val = "0", seller = "", sellerZone = "", user, userZone, product, idInc,sqls[], desc;
		double userMoney, sellerMoney, price, storage = 0, picked, size, total, transport = 0;
		int level;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name,money,[zone] from businessgame.dbo.[user] where name=(select [user] from storage where id='"+req.getParameter("storage")+"')");
		if(srs.next()){
			user = srs.getString("name");
			userMoney = srs.getDouble("money");
			userZone = srs.getString("zone");
		} else return "0";
		
		System.out.println("Tanda 1");
		
		//0 = internal error
		//1 = produk dah abis
		//2 = produk kurang
		//3 = uang ga cukup
		//4 = storage ga ada
		//5 = storage ga cukup
		
		srs = db.getJdbc().queryForRowSet("select desc_product.id,market_product.price,market_product.size,storage.[user],storage.[zone],[user].money,product,quality from market_product,storage_product,desc_product,storage,[user] where market_product.id='"+req.getParameter("productId")+"' and storage_product.id=storage_product_id and storage_product.[desc]=desc_product.id and storage_product.storage=storage.id and storage.[user]=[user].name");
		if(srs.next()){
			desc = srs.getString("id");
			price = srs.getDouble("price");
			size = srs.getDouble("size");
			seller = srs.getString("user");
			product = srs.getString("product");
			sellerMoney = srs.getDouble("money");
			sellerZone = srs.getString("zone");
		} else return "1";
		
		srs = db.getJdbc().queryForRowSet("select transport from info_product where name='"+product+"'");
		if(srs.next())
			transport = srs.getDouble("transport");
		else return "0";
		
		System.out.println("Tanda 2");
		
		picked = Double.parseDouble(req.getParameter("picked"));
		total = price*picked;
		
		if(size < picked)
			return "2";
		
		if(userMoney < total)
			return "3";
		
		size -= picked;
		userMoney -= total;
		
		srs = db.getJdbc().queryForRowSet("select id,[level] from storage where id='"+req.getParameter("storage")+"'");
		if(srs.next()){
			level = srs.getInt("level")-1;
		}
		else return "4";
		
		srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='storage' union select [value] from info_values where name='storage_inc'");
		if(srs.next()){
			storage = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		System.out.println("Tanda 3");
		
		if(srs.next()){
			storage += level*Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		System.out.println("Tanda 4");

		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage='"+req.getParameter("storage")+"' union select storage_equipment.id,size from storage_equipment,desc_equipment,list_equipment,storage where storage='"+req.getParameter("storage")+"' and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and storage.id=storage_equipment.storage");
		while(srs.next()){
			storage -= srs.getDouble("size");
		}
		
		if(storage < picked)
			return "5";
		
		System.out.println("Tanda 5");
		
		srs = db.getJdbc().queryForRowSet("select id,size,avg_price from storage_product where storage_product.[desc]='"+desc+"' and storage='"+req.getParameter("storage")+"'");
		if(srs.next()){
			price = (total + (srs.getDouble("size")*srs.getDouble("avg_price")))/(picked+srs.getDouble("size"));
			sqlL.add("update storage_product set size='"+(srs.getDouble("size")+picked)+"',price='"+price+"' where id='"+srs.getString("id")+"'");
		}
		
		if(sqlL.size() < 1){
			idInc = getUniqueIncrementIdNew("storage_product");
			sqlL.add("insert into storage_product values ('"+KEY_PRODUCT+idInc+"','"+desc+"','"+req.getParameter("storage")+"','"+picked+"','"+price+"')");
		}
		
		if(size > 0)
			sqlL.add("update market_product set size='"+size+"' where id='"+req.getParameter("productId")+"'");
		else sqlL.add("delete from market_product where id='"+req.getParameter("productId")+"'");
		
		sqlL.add("update businessgame.dbo.[user] set money='"+userMoney+"' where name='"+user+"'");
		
//		srs = db.getJdbc().queryForRowSet("select total from user_finance where user='"+user+"' and type='Raw Material'");
//		if(srs.next()){
//			sqlL.add("update user_finance set total='"+(((srs.getDouble("total")*-1)+total)*-1)+"' where user='"+user+"' and type='Raw Material'");
//		} else {
//			idInc = getUniqueIncrementIdNew("user_finance");
//			sqlL.add("insert into user_finance values ('"+KEY_USER_FINANCE+idInc+"','"+user+"','Raw Material','"+(-1*total)+"')");
//		}
//		accountingFinance(user, "Raw Material", total, false);
		
		sellerMoney += total;
		
		double tmpd1;
		
		srs = db.getJdbc().queryForRowSet("select transport_in,transport_out,retribution from info_zone where id='"+userZone+"'");
		if(srs.next()){
			if(userZone.equals(sellerZone))
				transport *= srs.getDouble("transport_in");
			else transport *= srs.getDouble("transport_out");
			tmpd1 = srs.getDouble("retribution");
		} else return "0";
		accountingFinance(seller, "Sales", total, true);
		accountingFinance(seller, "Transport", picked*transport, false);
		accountingFinance(seller, "Retribution", picked*tmpd1, false);
		
		sqlL.add("update businessgame.dbo.[user] set money='"+sellerMoney+"' where name='"+seller+"'");
		
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where id=(select storage_product_id from market_product where id='"+req.getParameter("productId")+"')");
		if(srs.next()){
			val = srs.getString("id");
			size = srs.getDouble("size");
		}
		else return "0";
		
		size -= picked;
		if(size > 0)
			sqlL.add("update storage_product set size='"+size+"' where id='"+val+"'");
		else sqlL.add("delete from storage_product where id='"+val+"'");
		
		System.out.println("Tanda 6");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);

		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.[user],product,market_product.price,quality,market_product.size,draw from market_product,storage_product,desc_product,storage,info_product where market_product.[zone]='"+userZone+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.[desc] and storage.id=storage_product.storage and product=name");
//		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.[user],product,market_product.price,quality,market_product.size,draw from market_product,storage_product,desc_product,storage,info_product where market_product.[zone]='"+userZone+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.[desc] and storage.id=storage_product.storage and product=name union select market_product.id,'',product,market_product.price,quality,market_product.size,draw from market_product,desc_product,info_product where market_product.[zone]='"+userZone+"' and desc_product.id=market_product.[desc] and product=name");
		ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
		while(srs.next()){
			products.add(new MarketProduct(srs.getString("id"), srs.getString("user"), srs.getString("product"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("size"), srs.getString("draw")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(userMoney));
		data.add(gson.toJson(products));
		
		val = gson.toJson(data);
		
		product = null;
		sqlL = null;
		data = null;
		sqls = null;
		srs = null;
		seller = null;
		sellerZone = null;
		user = null;
		userZone = null;
		desc = null;
		product = null;
		idInc = null;
		
		gc();
		
		return val;
	}
	
	public String buyMarketEquipment(HttpServletRequest req) {
		String val = "0", seller, user, userZone, equipmentIdSeller, sqls[];
		double userMoney, sellerMoney, price, storage = 0, size;
		int level;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0 = internal error
		//1 = produk dah abis
		//2 = uang ga cukup
		//3 = storage ga ada
		//4 = storage ga cukup
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name,money,[zone] from businessgame.dbo.[user] where name=(select [user] from storage where id='"+req.getParameter("storage")+"')");
		if(srs.next()){
			user = srs.getString("name");
			userMoney = srs.getDouble("money");
			userZone = srs.getString("zone");
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select storage_equipment_id,market_equipment.price,size,name,money from market_equipment,list_equipment,desc_equipment,[user],storage,storage_equipment where market_equipment.id='"+req.getParameter("equipmentId")+"' and storage_equipment.id=list_equipment.id and storage_equipment_id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and storage_equipment_id=list_equipment.id and storage.id=storage_equipment.storage and [user].name=storage.[user]");
		if(srs.next()){
			price = srs.getDouble("price");
			size = srs.getDouble("size");
			equipmentIdSeller = srs.getString("storage_equipment_id");
			seller = srs.getString("name");
			sellerMoney = srs.getDouble("money");
		} else return "1";
		
		if(userMoney < price)
			return "2";
		
		userMoney -= price;
		
		srs = db.getJdbc().queryForRowSet("select [level] from storage where id='"+req.getParameter("storage")+"'");
		if(srs.next()){
			level = srs.getInt("level")-1;
		} else return "3";
		
		srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='storage' union select [value] from info_values where name='storage_inc'");
		if(srs.next()){
			storage = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		if(srs.next()){
			storage += level*Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage='"+req.getParameter("storage")+"' union select storage_equipment.id,size from storage_equipment,desc_equipment,list_equipment,storage where storage='"+req.getParameter("storage")+"' and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and storage.id=storage_equipment.storage");
		while(srs.next()){
			storage -= srs.getDouble("size");
		}
		
		if(storage < size)
			return "4";
		
		sqlL.add("update storage_equipment set storage='"+req.getParameter("storage")+"' where id='"+equipmentIdSeller+"'");
		
		sqlL.add("update list_equipment set buy_price='"+price+"' where id='"+equipmentIdSeller+"'");
		
		sqlL.add("delete from market_equipment where id='"+req.getParameter("equipmentId")+"'");
		
		sqlL.add("update businessgame.dbo.[user] set money='"+userMoney+"' where name='"+user+"'");
		
		if(!seller.equals("")){
			sellerMoney += price;
			sqlL.add("update businessgame.dbo.[user] set money='"+sellerMoney+"' where name='"+seller+"'");
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.[user],equipment,market_equipment.price,quality,durability,size,operational,draw from market_equipment,storage_equipment,desc_equipment,list_equipment,storage,info_equipment where market_equipment.[zone]='"+userZone+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and storage.id=storage_equipment.storage and equipment=name");
		ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
		while(srs.next()){
			equipments.add(new MarketEquipment(srs.getString("id"), srs.getString("user"), srs.getString("equipment"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational"), srs.getString("draw")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(userMoney));
		data.add(gson.toJson(equipments));
		
		val = gson.toJson(data);
		
		sqlL = null;
		equipments = null;
		data = null;
		sqls = null;
		srs = null;
		seller = null;
		user = null;
		userZone = null;
		equipmentIdSeller = null;
		
		gc();
		
		return val;
	}
	
	public String sellStorageProduct(HttpServletRequest req) {
		String val="0", idInc, sqls[];
		double remain,size,offer,basePrice,price,lowest,highest;
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1, srs2;
		
		//0 = internal error
		//1 = CBM yg ditawarkan lebih besar dari yg dimiliki
		//2 = harga melampaui 25% lebih murah dari harga jual yang disarankan
		//3 = harga melampaui 25% lebih mahal dari harga jual yang disarankan
		
		srs1 = db.getJdbc().queryForRowSet("select price,size from storage_product,desc_product where id='"+req.getParameter("productId")+"' and storage_product.[desc]=desc_product.id");
		if(srs1.next()){
			size = srs1.getDouble("size");
			basePrice = srs1.getDouble("price");
		} else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+req.getParameter("productId")+"'");
		if(srs1.next())
			size -= srs1.getDouble("size");
		
		offer = Double.parseDouble(req.getParameter("offer"));
		price = Double.parseDouble(req.getParameter("price"));
		lowest = basePrice - (basePrice*0.25);
		highest = basePrice + (basePrice*0.25);
		
		if(size < offer)
			return "1";
		
		if(price < lowest)
			return "2";
		
		if(price > highest)
			return "3";
		
		srs1 = db.getJdbc().queryForRowSet("select id,size from market_product where storage_product_id='"+req.getParameter("productId")+"' and [zone]='"+req.getParameter("zone")+"' and price='"+req.getParameter("price")+"'");
		if(srs1.next())
			sqlL.add("update market_product set size='"+(srs1.getDouble("size")+offer)+"' where id='"+srs1.getString("id")+"'");
		else {
			idInc = getUniqueIncrementIdNew("market_product");
			sqlL.add("insert into market_product values ('"+KEY_MARKET_PRODUCT+idInc+"','"+req.getParameter("productId")+"','','"+req.getParameter("zone")+"','"+req.getParameter("price")+"','"+offer+"')");
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		val = "Ok";
		
//		srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size,draw from storage_product,desc_product,info_product where storage='"+req.getParameter("storage")+"' and desc_product.id=storage_product.desc and product=name");
//		ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
//		while(srs1.next()){
//			remain = srs1.getDouble("size");
//			srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs1.getString("id")+"'");
//			while(srs2.next()){
//				remain -= srs2.getDouble("size");
//			}
//			if(remain > 0)
//				products.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(remain)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue(), srs1.getString("draw")));
//		}
		
		srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size,draw from storage_product,desc_product,info_product where storage='"+req.getParameter("storage")+"' and desc_product.id=storage_product.[desc] and product=name");
		ArrayList<StorageProduct> storageProducts = new ArrayList<StorageProduct>();
		ArrayList<MarketProduct> marketProducts = new ArrayList<MarketProduct>();
		while(srs1.next()){
			remain = srs1.getDouble("size");
			
			srs2 = db.getJdbc().queryForRowSet("select market_product.id,product,market_product.price,quality,market_product.size,draw from market_product,desc_product,info_product,storage_product where storage_product_id='"+srs1.getString("id")+"' and storage_product_id=storage_product.id and desc_product.id=storage_product.[desc] and product=name");
			while(srs2.next()){
				remain -= srs2.getDouble("size");
				marketProducts.add(new MarketProduct(srs2.getString("id"), "", srs2.getString("product"), srs2.getDouble("price"), srs2.getInt("quality"), srs2.getDouble("size"), srs2.getString("draw")));
			}
			if(remain > 0)
				storageProducts.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(remain)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue(), srs1.getString("draw")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(storageProducts));
		data.add(gson.toJson(marketProducts));
		val=gson.toJson(data);
		
		sqlL = null;
		storageProducts = null;
		marketProducts = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		idInc = null;
		
		gc();
		
		return val;
	}
	
	public String sellStorageEquipment(HttpServletRequest req) {
		String val="0", idInc, sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1, srs2;
		
		idInc = getUniqueIncrementIdNew("market_equipment");
		sqlL.add("insert into market_equipment values ('"+KEY_MARKET_EQUIPMENT+idInc+"','"+req.getParameter("equipmentId")+"','"+req.getParameter("marketZone")+"',"+req.getParameter("price")+")");

		System.out.println("insert into market_equipment values ('"+KEY_MARKET_EQUIPMENT+idInc+"','"+req.getParameter("equipmentId")+"','"+req.getParameter("marketZone")+"',"+req.getParameter("price")+")");
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);

//		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational,draw from storage_equipment,list_equipment,desc_equipment,info_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id and name=equipment");
//		ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
//		while(srs1.next()){
//			srs2 = db.getJdbc().queryForRowSet("select id from market_equipment where storage_equipment_id='"+srs1.getString("id")+"'");
//			if(!srs2.next())
//				equipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
//		}
		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational,draw from storage_equipment,list_equipment,desc_equipment,info_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
		ArrayList<StorageEquipment> storageEquipments = new ArrayList<StorageEquipment>();
		ArrayList<MarketEquipment> marketEquipments = new ArrayList<MarketEquipment>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select market_equipment.id,equipment,market_equipment.price,quality,durability,size,operational,draw from storage_equipment,market_equipment,desc_equipment,list_equipment,info_equipment where storage_equipment_id='"+srs1.getString("id")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and equipment=name");
			if(srs2.next()){
				marketEquipments.add(new MarketEquipment(srs2.getString("id"), "", srs2.getString("equipment"), srs2.getDouble("price"), srs2.getInt("quality"), srs2.getDouble("durability"), srs2.getDouble("size"), srs2.getDouble("operational"), srs2.getString("draw")));
			} else {
				storageEquipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
			}
		}
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(storageEquipments));
		data.add(gson.toJson(marketEquipments));
		val=gson.toJson(data);
		
		storageEquipments = null;
		marketEquipments = null;
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		idInc = null;
		
		gc();
		
		return val;
	}
	
	public String createNewInstallment(HttpServletRequest req) {
		String val = "", idInc, sqls[], draw;
		double money, cost;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0=internal error
		//1=blueprint ga punya (kemungkinan user nge-cheat)
		//2=uang ga cukup
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id from user_sector_blueprint where user='"+req.getParameter("user")+"' and sector='"+req.getParameter("type")+"'");
		if(!srs.next())
			return "1";
		
		srs = db.getJdbc().queryForRowSet("select money from [user] where name='"+req.getParameter("user")+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select cost,draw from info_sector where name='"+req.getParameter("type")+"'");
		if(srs.next()){
			cost = srs.getDouble("cost");
			draw = srs.getString("draw");
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+req.getParameter("zone")+"'");
		if(srs.next())
			cost += srs.getDouble("cost");
		else return "0";
		
		if(money < cost)
			return "2";
		
		money -= cost;
		
		sqlL.add("update [user] set money='"+money+"' where name='"+req.getParameter("user")+"'");
		idInc = getUniqueIncrementIdNew("installment");
		sqlL.add("insert into installment values ('"+KEY_INSTALLMENT+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"','"+req.getParameter("type")+"','',0,0,0,0,0)");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		Installment tmp = new Installment(KEY_INSTALLMENT+idInc, req.getParameter("type"), req.getParameter("zone"), 0, 0, draw, false);
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(money));
		data.add(gson.toJson(tmp));
		
		val = gson.toJson(data);
		
		sqlL = null;
		sqls = null;
		idInc = null;
		srs = null;
		tmp = null;
		
		gc();
		
		return val;
	}
	
	public String attachEquipmentToInstallment(HttpServletRequest req) {
		String val="0",sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		
		sqlL.add("insert into installment_equipment values ('"+req.getParameter("idEquipment")+"','"+req.getParameter("idInstallment")+"')");
		sqlL.add("delete from storage_equipment where id='"+req.getParameter("idEquipment")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		SqlRowSet srs1,srs2;
		
		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational,draw from storage_equipment,list_equipment,desc_equipment,info_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
		
		ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from market_equipment where storage_equipment_id='"+srs1.getString("id")+"'");
			if(!srs2.next())
				equipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
		}
		
		Installment installment = getSingleUserInstallments(req.getParameter("idInstallment"));

		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(equipments));
		data.add(gson.toJson(installment));
		
		val=gson.toJson(data);
		
		sqlL = null;
		equipments = null;
		srs1 = null;
		srs2 = null;
		sqls = null;
		installment = null;
		data = null;
		
		gc();
		
		return val;
	}
	
	public String detachEquipment(HttpServletRequest req) {
		String val="0",idStorage,sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs;
		
		//0 = internal error
		//1 = user belum memiliki storage
		
		srs = db.getJdbc().queryForRowSet("select id from storage where [user]='"+req.getParameter("user")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"')");
		if(srs.next())
			idStorage = srs.getString("id");
		else return "1";
		
		sqlL.add("delete from installment_equipment where id='"+req.getParameter("idEquipment")+"'");
		
		sqlL.add("insert into storage_equipment values ('"+req.getParameter("idEquipment")+"','"+idStorage+"')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select installment_equipment.id,equipment,quality,durability,size,operational,draw from installment_equipment,desc_equipment,list_equipment,info_equipment where installment='"+req.getParameter("id")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
		ArrayList<InstallmentEquipment> equipments = new ArrayList<InstallmentEquipment>();
		while(srs.next()){
			equipments.add(new InstallmentEquipment(srs.getString("id"), srs.getString("equipment"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational"), srs.getString("draw")));
		}
		
		Installment installment = getSingleUserInstallments(req.getParameter("id"));
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(equipments));
		data.add(gson.toJson(installment));
		
		val=gson.toJson(data);
		
		equipments = null;
		installment = null;
		data = null;
		sqlL = null;
		sqls = null;
		idStorage = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String hireEmployeeToInstallment(HttpServletRequest req) {
		String val = "",idEmployee,sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs;
		double money,price;
		
		//0=internal error
		//1=karyawan keburu diambil orang
		//2=uang ga cukup
		
		srs = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select market_employee.[desc],price from market_employee where id='"+req.getParameter("idEmployee")+"'");
		if(srs.next()){
			idEmployee = srs.getString("desc");
			price = srs.getDouble("price");
		} else return "1";
		
		if(money < price)
			return "2";
		
		money -= price;
		
		sqlL.add("insert into installment_employee values ('"+idEmployee+"','"+req.getParameter("idInstallment")+"')");
		sqlL.add("delete from market_employee where id='"+req.getParameter("idEmployee")+"'");
		sqlL.add("update businessgame.dbo.[user] set money='"+money+"' where name='"+req.getParameter("user")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select market_employee.id,employee,market_employee.price,quality,operational,draw from market_employee,desc_employee,list_employee,info_employee where [zone]='"+req.getParameter("zone")+"' and list_employee.id=market_employee.[desc] and desc_employee.id=list_employee.[desc] and name=employee");
		ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
		while(srs.next()){
			employees.add(new MarketEmployee(srs.getString("id"), srs.getString("employee"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("operational"), srs.getString("draw")));
		}
		
		Installment installment = getSingleUserInstallments(req.getParameter("idInstallment"));
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(money));
		data.add(gson.toJson(employees));
		data.add(gson.toJson(installment));
		
		val = gson.toJson(data);
		
		data = null;
		employees = null;
		installment = null;
		sqlL = null;
		sqls = null;
		idEmployee = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String fireEmployee(HttpServletRequest req) {
		String val = "",zone,idInc,sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		double price;
		SqlRowSet srs;

		srs = db.getJdbc().queryForRowSet("select price from list_employee,desc_employee where list_employee.id='"+req.getParameter("idEmployee")+"' and desc_employee.id=list_employee.[desc]");
		if(srs.next())
			price = srs.getDouble("price");
		else return "0";

		srs = db.getJdbc().queryForRowSet("select [zone] from installment where id='"+req.getParameter("id")+"'");
		if(srs.next())
			zone = srs.getString("zone");
		else return "0";
		
		idInc = getUniqueIncrementIdNew("market_employee");
		sqlL.add("delete from installment_employee where id='"+req.getParameter("idEmployee")+"'");
		sqlL.add("insert into market_employee values ('"+KEY_MARKET_EMPLOYEE+idInc+"','"+req.getParameter("idEmployee")+"','"+zone+"','"+price+"')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select installment_employee.id,employee,quality,operational,draw from installment_employee,desc_employee,list_employee,info_employee where installment='"+req.getParameter("id")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id and name=employee");
		ArrayList<InstallmentEmployee> employees = new ArrayList<InstallmentEmployee>();
		while(srs.next()){
			employees.add(new InstallmentEmployee(srs.getString("id"), srs.getString("employee"), srs.getInt("quality"), srs.getDouble("operational"), srs.getString("draw")));
		}
		
		Installment installment = getSingleUserInstallments(req.getParameter("id"));
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(employees));
		data.add(gson.toJson(installment));
		
		val = gson.toJson(data);
		
		employees = null;
		sqlL = null;
		sqls = null;
		idInc = null;
		zone = null;
		srs = null;
		data = null;
		installment = null;
		
		gc();
		
		return val;
	}
	
	public String updateSubscriptionTariff(HttpServletRequest req) {
		String val="Ok";
		db.getJdbc().execute("update installment set subscription='"+req.getParameter("subscription")+"', tariff='"+req.getParameter("tariff")+"' where id='"+req.getParameter("id")+"'");
		return val;
	}
	
	public String updateSupplyKwh(HttpServletRequest req) {
		String val="Ok";
		db.getJdbc().execute("update installment set supply='"+req.getParameter("idSupply")+"', planned_supply='"+req.getParameter("supply")+"' where id='"+req.getParameter("id")+"'");
		return val;
	}
	
	public String cancelSupplyInstallment(HttpServletRequest req) {
		String val="Ok";
		db.getJdbc().execute("update installment set supply='', planned_supply='0', actual_supply='0' where id='"+req.getParameter("idInstallment")+"'");
		return val;
	}
	
	public String buySectorBlueprint(HttpServletRequest req) {
		String val="", idInc, sqls[];
		double money, price, cost;
		int level,userLevel;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0=internal error
		//1=uang ga cukup
		//2=level belum cukup
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select money,[level] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		if(srs.next()){
			money = srs.getDouble("money");
			userLevel = srs.getInt("level");
		}
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='sector'");
		if(srs.next()){
			price = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select [level] from info_sector where name='"+req.getParameter("sector")+"'");
		if(srs.next())
			level = srs.getInt("level");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select sector from user_sector_blueprint where [user]='"+req.getParameter("user")+"'");
		ArrayList<String> userSectors = new ArrayList<String>();
		while(srs.next()){
			userSectors.add(srs.getString("sector"));
		}
		
		price *= userSectors.size();
		
		if(money < price)
			return "1";
		
		if(userLevel < level)
			return "2";
		
		money -= price;
		if(userLevel == level)
			userLevel = level+1;
		
		idInc = getUniqueIncrementIdNew("user_sector_blueprint");
		sqlL.add("insert into user_sector_blueprint values ('"+KEY_USER_SECTOR_BLUEPRINT+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("sector")+"')");
		sqlL.add("update businessgame.dbo.[user] set money='"+money+"', [level]='"+userLevel+"' where name='"+req.getParameter("user")+"'");
		System.out.println("update businessgame.dbo.[user] set money='"+money+"', [level]='"+userLevel+"' where name='"+req.getParameter("user")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select cost from info_sector where name='"+req.getParameter("sector")+"'");
		if(srs.next()){
			cost = srs.getDouble("cost");
		} else return "0";
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(money));
		data.add(KEY_USER_SECTOR_BLUEPRINT+idInc);
		data.add(gson.toJson(cost));
		
		val = gson.toJson(data);
		
		sqlL = null;
		sqls = null;
		idInc = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String buyBundleEquipmentEmployee(HttpServletRequest req) {
		String val="",sqls[],user,idInc;
		int quality,items;
		double total = 0,userMoney;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0=internal error
		//1=uang ga cukup
		
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select money,type,[user] from installment,[user] where id='"+req.getParameter("installment")+"' and [user]=name"),
				srs2;
		if(srs1.next()){
			System.out.println("Tes 1");
			userMoney = srs1.getDouble("money");
			user = srs1.getString("user");
			if(req.getParameter("quality") == null){
				srs2 = db.getJdbc().queryForRowSet("select quality from info_quality where from_base='1'");
				if(srs2.next())
					quality = srs2.getInt("quality");
				else return "0";
			} else quality = Integer.parseInt(req.getParameter("quality"));
			
			System.out.println("Tes 2");
			
			srs2 = db.getJdbc().queryForRowSet("select desc_equipment.id,price,items from desc_equipment,info_sector_equipment where sector='"+srs1.getString("type")+"' and quality='"+quality+"' and equipment=equipment_type");
			while(srs2.next()){
				items = srs2.getInt("items");
				total += srs2.getDouble("price")*items;
				for(int i=0;i<items;i++){
					idInc = getUniqueIncrementIdNew("equipment");
					sqlL.add("insert into list_equipment values('"+KEY_EQUIPMENT+idInc+"','"+srs2.getString("id")+"','100','"+srs2.getDouble("price")+"')");
					sqlL.add("insert into installment_equipment values ('"+KEY_EQUIPMENT+idInc+"','"+req.getParameter("installment")+"')");
				}
			}
			
			System.out.println("Tes 3");
			
			srs2 = db.getJdbc().queryForRowSet("select desc_employee.id,price,items from desc_employee,info_sector_employee where sector='"+srs1.getString("type")+"' and quality='"+quality+"' and employee=employee_type");
			while(srs2.next()){
				items = srs2.getInt("items");
				total += srs2.getDouble("price")*items;
				for(int i=0;i<items;i++){
					idInc = getUniqueIncrementIdNew("employee");
					sqlL.add("insert into list_employee values ('"+KEY_EMPLOYEE+idInc+"','"+srs2.getString("id")+"')");
					sqlL.add("insert into installment_employee values ('"+KEY_EMPLOYEE+idInc+"','"+req.getParameter("installment")+"')");
				}
			}
			
			System.out.println("Tes 4");
			
			if(userMoney < total)
				return "1";
			
			userMoney -= total;
			sqlL.add("update businessgame.dbo.[user] set money='"+userMoney+"' where name='"+user+"'");
			
			sqls = new String[sqlL.size()];
			sqlL.toArray(sqls);
			db.getJdbc().batchUpdate(sqls);
			
			val = gson.toJson(userMoney);
			
		} else return "0";
		
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		user = null;
		idInc = null;
		
		gc();
		
		return val;
	}
	
	public String markMessageAsRead(HttpServletRequest req) {
		String val="Ok";
		db.getJdbc().execute("update user_message set unread='0' where id='"+req.getParameter("id")+"'");
		return val;
	}
	
	public String advertiseProduct(HttpServletRequest req) {
		String val="Ok",zone,idInc,sqls[];
		double money,price;
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs = db.getJdbc().queryForRowSet("select money,[zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
		if(srs.next()){
			zone = srs.getString("zone");
			money = srs.getDouble("money");
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select price from desc_advertisement where id='"+req.getParameter("ads")+"'");
		if(srs.next())
			price = srs.getDouble("price")*Double.parseDouble(req.getParameter("turn"));
		else return "0";
		
		if(money < price)
			return "1";
		
		money -= price;
		
		sqlL.add("update businessgame.dbo.[user] set money='"+money+"' where name='"+req.getParameter("user")+"'");
		
//		srs = db.getJdbc().queryForRowSet("select total from user_finance where user='"+req.getParameter("user")+"' and type='Advertisement'");
//		if(srs.next()){
//			sqlL.add("update user_finance set total='"+(((srs.getDouble("total")*-1)+price)*-1)+"' where user='"+req.getParameter("user")+"' and type='Advertisement'");
//		} else {
//			idInc = getUniqueIncrementIdNew("user_finance");
//			sqlL.add("insert into user_finance values ('"+KEY_USER_FINANCE+idInc+"','"+req.getParameter("user")+"','Raw Material','"+(-1*price)+"')");
//		}
		accountingFinance(req.getParameter("user"), "Advertisement", price, false);
		
		srs = db.getJdbc().queryForRowSet("select id,turn from product_advertisement where [user]='"+req.getParameter("user")+"' and product='"+req.getParameter("product")+"' and [zone]='"+zone+"' and ads='"+req.getParameter("ads")+"'");
		if(srs.next())
			sqlL.add("update product_advertisement set turn='"+(srs.getLong("turn")+(Long.parseLong(req.getParameter("turn"))))+"' where id='"+srs.getString("id")+"'");
		else {
			idInc = getUniqueIncrementIdNew("product_advertisement");
			sqlL.add("insert into product_advertisement values ('"+KEY_PRODUCT_ADVERTISEMENT+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("product")+"','"+zone+"','"+req.getParameter("ads")+"','"+req.getParameter("turn")+"')");
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		val = gson.toJson(money);
		
		return val;
	}
	
	public String sendMessage(HttpServletRequest req) {
		String val = "",idInc;
		idInc = getUniqueIncrementIdNew("user_message");
		db.getJdbc().execute("insert into user_message values ('"+KEY_USER_MESSAGE+idInc+"','"+req.getParameter("sender")+"','"+req.getParameter("recipient")+"','"+req.getParameter("message")+"','1')");
		val = "Ok";
		return val;
	}
	
	public String makeContract(HttpServletRequest req) {
		String val = "",idInc,storageSeller,storageUser,desc;
		SqlRowSet srs;
		
		//0=internal error
		//1=supplier ga punya storage
		//2=user ga punya storage
		
		srs = db.getJdbc().queryForRowSet("select id from storage where [user]='"+req.getParameter("supplier")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("supplier")+"')");
		if(srs.next())
			storageSeller = srs.getString("id");
		else return "1";
		
		srs = db.getJdbc().queryForRowSet("select id from storage where [user]='"+req.getParameter("user")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"')");
		if(srs.next())
			storageUser = srs.getString("id");
		else return "2";
		
		srs = db.getJdbc().queryForRowSet("select id from desc_product where product='"+req.getParameter("product")+"' and quality='"+req.getParameter("quality")+"'");
		if(srs.next())
			desc = srs.getString("id");
		else return "0";
		
		idInc = getUniqueIncrementIdNew("user_contract");
		db.getJdbc().execute("insert into user_contract values ('"+KEY_USER_CONTRACT+idInc+"','"+storageUser+"','"+storageSeller+"','"+desc+"','"+req.getParameter("quantity")+"','"+req.getParameter("price")+"','"+req.getParameter("turn")+"','0')");
		val = "Ok";
		return val;
	}
	
	public String confirmContract(HttpServletRequest req) {
		String val = "0";
		db.getJdbc().execute("update user_contract set accept='1' where id='"+req.getParameter("id")+"'");
//		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select user_contract.id,request_storage,supplier_storage,product,quality,size,user_contract.price,turn from user_contract,storage,desc_product where accept='0' and user='"+req.getParameter("user")+"' and product_desc=desc_product.id and (request_storage=storage.id or supplier_storage=storage.id)"),
//				srs2;
//		String contractType,user,zone;
//		
//		ArrayList<Contract> pendingContracts = new ArrayList<Contract>();
//		while(srs1.next()){
//			srs2 = db.getJdbc().queryForRowSet("select user,zone from storage where id='"+srs1.getString("request_storage")+"' union select user,zone from storage where id='"+srs1.getString("supplier_storage")+"'");
//			if(srs2.next()){
//				if(srs2.getString("user").equals(req.getParameter("user"))){
//					contractType = "from";
//					srs2.next();
//					user = srs2.getString("user");
//					zone = srs2.getString("zone");
//				} else {
//					contractType = "to";
//					user = srs2.getString("user");
//					zone = srs2.getString("zone");
//				}
//				pendingContracts.add(new Contract(srs1.getString("id"), user, zone, contractType, srs1.getString("product"), srs1.getInt("quality"), srs1.getDouble("size"), srs1.getDouble("price")));
//			} else return "0";
//		}
//		val = gson.toJson(pendingContracts);
		val = "Ok";
		return val;
	}
	
	public String cancelRejectContract(HttpServletRequest req) {
		String val = "0";
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select request_storage,supplier_storage,turn from user_contract where id='"+req.getParameter("id")+"' and accept='1'"),
				srs2, srs3;
		if(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select [user],[zone] from storage where id='"+srs1.getString("request_storage")+"' union select [user],[zone] from storage where id='"+srs1.getString("supplier_storage")+"'");
			if(srs2.next()){
				if(!srs2.getString("user").equals(req.getParameter("user"))){
					srs3 = db.getJdbc().queryForRowSet("select rep from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'");
					db.getJdbc().execute("update businessgame.dbo.[user] set rep='"+(srs3.getLong("rep")-(srs1.getInt("turn")*5))+"' where name='"+req.getParameter("user")+"'");
				}
			} else return "0";
		}
			
		db.getJdbc().execute("delete from user_contract where id='"+req.getParameter("id")+"'");
		val = "Ok";
		
		srs1 = null;
		srs2 = null;
		srs3 = null;
		
		gc();
		
		return val;
	}
	
	public String cancelOfferProduct(HttpServletRequest req){
		String val="";
		double remain;
		
		db.getJdbc().execute("delete from market_product where id='"+req.getParameter("id")+"'");
		
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size,draw from storage_product,desc_product,info_product where storage=(select id from storage where [user]='"+req.getParameter("user")+"' and [zone]='"+req.getParameter("zone")+"') and desc_product.id=storage_product.[desc] and product=name"),
				srs2;
		
		srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size,draw from storage_product,desc_product,info_product where storage=(select id from storage where [user]='"+req.getParameter("user")+"' and [zone]='"+req.getParameter("zone")+"') and desc_product.id=storage_product.[desc] and product=name");
		ArrayList<StorageProduct> storageProducts = new ArrayList<StorageProduct>();
		ArrayList<MarketProduct> marketProducts = new ArrayList<MarketProduct>();
		while(srs1.next()){
			remain = srs1.getDouble("size");
			srs2 = db.getJdbc().queryForRowSet("select market_product.id,product,market_product.price,quality,market_product.size,draw from market_product,desc_product,info_product,storage_product where storage_product_id='"+srs1.getString("id")+"' and storage_product_id=storage_product.id and desc_product.id=storage_product.[desc] and product=name");
			while(srs2.next()){
				remain -= srs2.getDouble("size");
				marketProducts.add(new MarketProduct(srs2.getString("id"), "", srs2.getString("product"), srs2.getDouble("price"), srs2.getInt("quality"), srs2.getDouble("size"), srs2.getString("draw")));
			}
			if(remain > 0)
				storageProducts.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(remain)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue(), srs1.getString("draw")));
		}
		
//		ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
//		while(srs1.next()){
//			remain = srs1.getDouble("size");
//			srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs1.getString("id")+"'");
//			while(srs2.next()){
//				remain -= srs2.getDouble("size");
//			}
//			if(remain > 0)
//				products.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(remain)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue(), srs2.getString("draw")));
//		}
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(storageProducts));
		data.add(gson.toJson(marketProducts));
		val=gson.toJson(data);
		
		data = null;
		marketProducts = null;
		storageProducts = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}
	
	public String cancelOfferEquipment(HttpServletRequest req) {
		String val = "";
		db.getJdbc().execute("delete from market_equipment where id='"+req.getParameter("id")+"'");
		
		SqlRowSet srs1,srs2;
		
		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational,draw from storage_equipment,list_equipment,desc_equipment,info_equipment where storage=(select id from storage where [user]='"+req.getParameter("user")+"' and [zone]='"+req.getParameter("zone")+"') and storage_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
		ArrayList<StorageEquipment> storageEquipments = new ArrayList<StorageEquipment>();
		ArrayList<MarketEquipment> marketEquipments = new ArrayList<MarketEquipment>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select market_equipment.id,equipment,market_equipment.price,quality,durability,size,operational,draw from storage_equipment,market_equipment,desc_equipment,list_equipment,info_equipment where storage_equipment_id='"+srs1.getString("id")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and equipment=name");
			if(srs2.next()){
				marketEquipments.add(new MarketEquipment(srs2.getString("id"), "", srs2.getString("equipment"), srs2.getDouble("price"), srs2.getInt("quality"), srs2.getDouble("durability"), srs2.getDouble("size"), srs2.getDouble("operational"), srs2.getString("draw")));
			} else {
				storageEquipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
			}
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(storageEquipments));
		data.add(gson.toJson(marketEquipments));
		val=gson.toJson(data);
		
		data = null;
		marketEquipments = null;
		storageEquipments = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}
	
	public String fixEquipment(HttpServletRequest req) {
		String val = "", sqls[];
		double money,price;
		
		//0=internal error
		//1=uang user kurang
		
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+req.getParameter("user")+"'"),
				srs2;
		if(srs1.next())
			money = srs1.getDouble("money");
		else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select durability,buy_price from list_equipment where id='"+req.getParameter("id")+"'");
		if(srs1.next())
			price = ((100 - srs1.getDouble("durability"))/100)*srs1.getDouble("buy_price");
		else return "0";
		
		if(money < price)
			return "1";
		
		money -= price;
		
		ArrayList<String> sqlL = new ArrayList<String>();
		sqlL.add("update businessgame.dbo.[user] set money='"+(money)+"' where name='"+req.getParameter("user")+"'");
		sqlL.add("update list_equipment set durability='95.00' where id='"+req.getParameter("id")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational,draw from storage_equipment,list_equipment,desc_equipment,info_equipment where storage=(select id from storage where [user]='"+req.getParameter("user")+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+req.getParameter("user")+"')) and storage_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and name=equipment");
		ArrayList<StorageEquipment> storageEquipments = new ArrayList<StorageEquipment>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select market_equipment.id,equipment,market_equipment.price,quality,durability,size,operational,draw from storage_equipment,market_equipment,desc_equipment,list_equipment,info_equipment where storage_equipment_id='"+srs1.getString("id")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.[desc]=desc_equipment.id and equipment=name");
			if(!srs2.next()){
				storageEquipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational"), srs1.getString("draw")));
			}
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(money));
		data.add(gson.toJson(storageEquipments));
		
		val = gson.toJson(data);
		
		data = null;
		storageEquipments = null;
		srs1 = null;
		srs2 = null;
		
		gc();
		
		return val;
	}
	
	public String payBorrowedMoney(HttpServletRequest req) {
		String val = "", sqls[],idBorrow;
		double money, loan, pay;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0 = internal error
		//1 = uang lebih kecil dari pay
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id,money,borrow from businessgame.dbo.[user],borrow_bank where name='"+req.getParameter("user")+"' and name=[user]");
		if(srs.next()){
			money = srs.getDouble("money");
			loan = srs.getDouble("borrow");
			idBorrow = srs.getString("id");
		} else return "0";
		
		pay = Double.parseDouble(req.getParameter("pay"));
		
		if(money < pay)
			return "1";
		
		if(pay > loan)
			pay = loan;
		
		loan -= pay;
		
		money -= pay;
		
		sqlL.add("update businessgame.dbo.[user] set money="+money+" where name='"+req.getParameter("user")+"'");
		if(loan > 0)
			sqlL.add("update borrow_bank set borrow="+loan+" where id='"+idBorrow+"'");
		else sqlL.add("delete from borrow_bank where id='"+idBorrow+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		val = gson.toJson(money);
		
		sqlL = null;
		sqls = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String upgradeStorage(HttpServletRequest req) {
		String val = "", sqls[], user;
		long level;
		double money,price;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select [user],level from storage where id='"+req.getParameter("storage")+"'");
		ArrayList<String> sqlL = new ArrayList<String>();
		
		if(srs.next()){
			user = srs.getString("user");
			level = srs.getLong("level");
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+user+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='cost_storage_upgrade'");
		
		if(srs.next())
			price = Double.parseDouble(srs.getString("value"));
		else return "0";
		
		if(money < price)
			return "1";
		
		money -= price;
		
		sqlL.add("update businessgame.dbo.[user] set money="+money+" where name='"+user+"'");
		sqlL.add("update storage set level="+(level+1)+" where id='"+req.getParameter("storage")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(money));
		data.add(checkUserStorage(req));
		
		val = gson.toJson(data);
		
		data = null;
		sqlL = null;
		sqls = null;
		srs = null;
		
		gc();
		
		return val;
	}
	
	public String activateDeactivateInstallment(HttpServletRequest req) {
		String val = "";
		boolean active;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select active from installment where id='"+req.getParameter("id")+"'");
		if(srs.next())
			active = !srs.getBoolean("active");
		else return "0";
		
		db.getJdbc().execute("update installment set active='"+(active? 1 : 0)+"' where id='"+req.getParameter("id")+"'");
		
		val = gson.toJson(active);
		
		srs = null;
		
		return val;
	}
	
	/* Post function ends here : */
	
	/* Library : */
	
	/**
	 * Calculate the installment's details. More likely, a ratio of equipment, employee, input and
	 * output provided in one installment.
	 * 
	 * @param idInstallment The id of an installment being called.
	 * @return List of information regarding the details of an installment. The output will come as
	 * follow : (0) type installment, (1) calculated efficiency, (2) calculated effectivity,
	 * (3) list of input, (4) list of input ratio, (5) list of output, (6) list of output ratio, 
	 * (7) whether the installment is active/inactive state.
	 * 
	 */
	private ArrayList<String> calculateInstallmentAndIOByIdInstallment(String idInstallment){
		String hiElement="";
		double hiVal = 0,efficiency = 0,effectivity = 0;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select type,active from installment where id='"+idInstallment+"'"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		ArrayList<String> data = new ArrayList<String>(),
				input = new ArrayList<String>(),
				output = new ArrayList<String>();
		ArrayList<Double> inputVal = new ArrayList<Double>(),
				outputVal = new ArrayList<Double>(); 
		boolean pass = false;
		
		if(srs1.next()){
			hiElement="";
			hiVal=0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+idInstallment+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
				srs3.next();
				elements.put(srs2.getString("equipment_type"), srs3.getDouble(1));
			}
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+idInstallment+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				srs3.next();
				elements.put(srs2.getString("employee_type"), srs3.getDouble(1));
			}
			
			//calculating:
			while(true){
				for(String element : elementsRatio.keySet()){
					if(element.equals(hiElement)){
						elementsCalc.put(element, elements.get(element));
					} else {
						elementsCalc.put(element, (elementsRatio.get(element)*elements.get(hiElement))/elementsRatio.get(hiElement));
					}
				}
				
				for(String element : elements.keySet()){
					if(elements.get(element) < elementsCalc.get(element)){
						pass = false;
						hiElement = element;
						hiVal = elements.get(element);
						break;
					} else {
						pass = true;
					}
				}
				if(pass){
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							efficiency = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else efficiency = 0;
						effectivity = eff+1;
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							efficiency = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else efficiency = 0;
						effectivity = eff;
					}
					srs2 = db.getJdbc().queryForRowSet("select input_type,size from info_sector_input where sector='"+srs1.getString("type")+"'");
					while(srs2.next()){
						input.add(srs2.getString("input_type"));
						inputVal.add(new BigDecimal(Double.valueOf(srs2.getDouble("size")*effectivity*efficiency)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
					}
					srs2 = db.getJdbc().queryForRowSet("select output_type,size from info_sector_output where sector='"+srs1.getString("type")+"'");
					while(srs2.next()){
						output.add(srs2.getString("output_type"));
						outputVal.add(new BigDecimal(Double.valueOf(srs2.getDouble("size")*effectivity*efficiency)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
					}
					break;
				}
			}
		}
		data.add(srs1.getString("type"));
		data.add(gson.toJson(efficiency));
		data.add(gson.toJson(effectivity));
		data.add(gson.toJson(input));
		data.add(gson.toJson(inputVal));
		data.add(gson.toJson(output));
		data.add(gson.toJson(outputVal));
		data.add(gson.toJson(srs1.getBoolean("active")));
		
		input = null;
		inputVal = null;
		output = null;
		outputVal = null;
		srs1 = null;
		srs2 = null;
		srs3 = null;
		
		gc();
		
		return data;
	}
	
	/**
	 * Getting all user's installments, regardless of its type (or sector, in specifically)..
	 * @param user
	 * @return list of installment in ArrayList<Installment>
	 */
	private ArrayList<Installment> getUserInstallments(String user){
		String hiElement="";

		double hiVal = 0,tmpd1,tmpd2;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,[zone],type,draw,active from installment,info_sector where [user]='"+user+"' and [zone]=(select [zone] from businessgame.dbo.[user] where name='"+user+"') and name=type"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		ArrayList<Installment> installments = new ArrayList<Installment>();
		boolean pass = false;
		
		while(srs1.next()){
			hiElement="";
			hiVal=0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+srs1.getString("id")+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
				srs3.next();
				elements.put(srs2.getString("equipment_type"), srs3.getDouble(1));
			}
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+srs1.getString("id")+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				srs3.next();
				elements.put(srs2.getString("employee_type"), srs3.getDouble(1));
			}
			
			//calculating:
			while(true){
				for(String element : elementsRatio.keySet()){
					if(element.equals(hiElement)){
						elementsCalc.put(element, elements.get(element));
					} else {
						elementsCalc.put(element, (elementsRatio.get(element)*elements.get(hiElement))/elementsRatio.get(hiElement));
					}
				}
				
				for(String element : elements.keySet()){
					if(elements.get(element) < elementsCalc.get(element)){
						pass = false;
						hiElement = element;
						hiVal = elements.get(element);
						break;
					} else {
						pass = true;
					}
				}
				if(pass){
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0;
						tmpd2 = eff+1;
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0;
						tmpd2 = eff;
					}
					installments.add(new Installment(srs1.getString("id"), srs1.getString("type"), srs1.getString("zone"), tmpd1, tmpd2, srs1.getString("draw"), srs1.getBoolean("active")));
					break;
				}
			}
		}
		
		hiElement = null;
		srs1 = null;
		srs2 = null;
		srs3 = null;
		elements = null;
		elementsCalc = null;
		elementsRatio = null;
		
		gc();
		
		return installments;
	}
	
	private Installment getSingleUserInstallments(String id){
		String hiElement="";

		double hiVal = 0,tmpd1,tmpd2;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select [zone],type,draw,active from installment,info_sector where id='"+id+"' and name=type"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		Installment installment = null;
		boolean pass = false;
		
		if(srs1.next()){
			hiElement="";
			hiVal=0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+id+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
				srs3.next();
				elements.put(srs2.getString("equipment_type"), srs3.getDouble(1));
			}
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+id+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				srs3.next();
				elements.put(srs2.getString("employee_type"), srs3.getDouble(1));
			}
			
			//calculating:
			while(true){
				for(String element : elementsRatio.keySet()){
					if(element.equals(hiElement)){
						elementsCalc.put(element, elements.get(element));
					} else {
						elementsCalc.put(element, (elementsRatio.get(element)*elements.get(hiElement))/elementsRatio.get(hiElement));
					}
				}
				
				for(String element : elements.keySet()){
					if(elements.get(element) < elementsCalc.get(element)){
						pass = false;
						hiElement = element;
						hiVal = elements.get(element);
						break;
					} else {
						pass = true;
					}
				}
				if(pass){
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0;
						tmpd2 = eff+1;
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							tmpd1 = new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
						else tmpd1 = 0;
						tmpd2 = eff;
					}
					installment = new Installment(id, srs1.getString("type"), srs1.getString("zone"), tmpd1, tmpd2, srs1.getString("draw"), srs1.getBoolean("active"));
					break;
				}
			}
		}
		
		hiElement = null;
		srs1 = null;
		srs2 = null;
		srs3 = null;
		elements = null;
		elementsCalc = null;
		elementsRatio = null;
		
		gc();
		
		return installment;
	}
	
	/**
	 * Clean up the mess we've been created. XD
	 */
	private void gc(){
		System.gc();
	}
	
	
	/**
	 * Generate the 13-digit unique id based on the current time millis provided by the system
	 * (System.currentTimeMillis()) plus 3-digit incremental value.
	 * 
	 * @param table The table that'll be used.
	 * @return The 13-digit unique id plus 3-digit incremental value.
	 */
	private String getUniqueIncrementIdNew(String table){
		String val = "", sqls[];
		int counter;
		long curMillis, millis;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select [value] from info_values where name='inc_"+table+"' union select [value] from info_values where name='last_inc_set_millis'");
		if(srs.next()){
			counter = Integer.parseInt(srs.getString("value"));
		}
		else return val;
		
		curMillis = System.currentTimeMillis();
		
		if(srs.next())
			millis = Long.parseLong(srs.getString("value"));
		else return val;
		
		if(millis < curMillis){
			millis = curMillis;
			counter = 0;
			sqlL.add("update info_values set [value]='"+millis+"' where name='last_inc_set_millis'");
			sqlL.add("update info_values set [value]='0' where substring(name,1,4)='inc_'");
		}

		if(counter > 99)
			val = ""+counter;
		else if(counter > 9)
			val = "0"+counter;
		else val = "00"+counter;
		sqlL.add("update info_values set [value]='"+(counter+1)+"' where name='inc_"+table+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		return millis+val;
	}
	
	/**
	 * Take the user's activity into account. Specifically, record the flow of money to/from
	 * specified user. 
	 * 
	 * @param user The user that take into account.
	 * @param type The type of account.
	 * @param amount The amount of money that take into account.
	 * @param isIncome If it is an income, mark as true, otherwise must be false.
	 */
	private void accountingFinance(String user, String type, double amount, boolean isIncome){
		String sqls[],idInc;
		SqlRowSet srs;
		ArrayList<String> sqlL = new ArrayList<String>();
		int factor = isIncome? 1 : -1;
		
		srs = db.getJdbc().queryForRowSet("select id,total from user_finance where [user]='"+user+"' and type='"+type+"'");
		if(srs.next()){
			sqlL.add("update user_finance set total='"+(((srs.getDouble("total")*factor)+amount)*factor)+"' where id='"+srs.getString("id")+"'");
		} else {
			idInc = getUniqueIncrementIdNew("user_finance");
			sqlL.add("insert into user_finance values ('"+KEY_USER_FINANCE+idInc+"','"+user+"','"+type+"','"+(factor*amount)+"')");
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = null;
		sqlL = null;
		sqls = null;
		idInc = null;
		
		gc();
	}
}
