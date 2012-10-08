package com.ardhi.businessgame.services;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.ardhi.businessgame.models.BusinessSectorInfo;
import com.ardhi.businessgame.models.EmployeeInfo;
import com.ardhi.businessgame.models.IndustrialEquipmentInfo;
import com.ardhi.businessgame.models.InputInfo;
import com.ardhi.businessgame.models.InstallmentEmployee;
import com.ardhi.businessgame.models.InstallmentEquipment;
import com.ardhi.businessgame.models.MarketEmployee;
import com.ardhi.businessgame.models.MarketEquipment;
import com.ardhi.businessgame.models.MarketProduct;
import com.ardhi.businessgame.models.OutputInfo;
import com.ardhi.businessgame.models.Sectors;
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
			KEY_USER_SECTOR_BLUEPRINT = "US",
			KEY_USER_MARKET_LICENSE = "UM";
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
		invoker.start();
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
		if(srs.next()){
			do{
				zoneList.add(srs.getString("id"));
			}while(srs.next());
			val = gson.toJson(zoneList);
		} else val = "0";
		
		srs = null;
		zoneList = null;
		
		return val;
	}
	
	public String loadBankData(HttpServletRequest req) {
		String val = "";
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select * from req_borrow_bank where user='"+req.getParameter("user")+"'");
		if(srs.next())
			return "1";
		
		ArrayList<String> sectorList = new ArrayList<String>(),
				data = new ArrayList<String>();
		ArrayList<Double> priceList = new ArrayList<Double>();
		ArrayList<BusinessSectorInfo> bsiList = new ArrayList<BusinessSectorInfo>();
		ArrayList<IndustrialEquipmentInfo> ie;
		ArrayList<EmployeeInfo> e;
		ArrayList<InputInfo> i;
		ArrayList<OutputInfo> o;
		
		srs = db.getJdbc().queryForRowSet("select name, cost from info_sector");
		while(srs.next()){
			sectorList.add(srs.getString("name"));
			priceList.add(srs.getDouble("cost"));
		}
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id=(select zone from user where name='"+req.getParameter("user")+"')");
		if(srs.next()){
			data.add(gson.toJson(sectorList));
			data.add(gson.toJson(priceList));
			data.add(gson.toJson(srs.getDouble("cost")));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='cost_storage'");
		if(srs.next()){
			data.add(gson.toJson(Double.parseDouble(srs.getString("value"))));
		} else return "0";
		
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
		
		data.add(gson.toJson(bsiList));
		
		val = gson.toJson(data);
		
		sectorList = null;
		priceList = null;
		bsiList = null;
		data = null;
		srs = null;
		
		return val;
	}
	
	public String getEntireSector(HttpServletRequest req) {
		String val = "";
		
		ArrayList<String> sectorList = new ArrayList<String>();
		ArrayList<Double> priceList = new ArrayList<Double>();
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name, cost from info_sector");
		if(srs.next()){
			do{
				sectorList.add(srs.getString("name"));
				priceList.add(Double.parseDouble(srs.getString("cost")));
			}while(srs.next());
		} else val = "0";
		
		if(!val.equals("0")){
			srs = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+req.getParameter("zone")+"'");
			if(srs.next())
				val = gson.toJson(new Sectors(sectorList, priceList, Double.parseDouble(srs.getString("cost"))));
			else val = "0";
		}
		
		sectorList = null;
		priceList = null;
		
		return val;
	}
	
	public String checkSubmitProposal(HttpServletRequest req) {
		String val = "Ok";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select * from req_borrow_bank where user='"+req.getParameter("user")+"'");
		if(srs.next())
			val = "0";
		return val;
	}
	
	public String checkUserStorage(HttpServletRequest req) {
		String val = "No",id="";
		boolean isAvailable;
		SqlRowSet srs1, srs2;
		double tmp = 0;
		
		ArrayList<String> data = new ArrayList<String>();
		
//		srs1 = db.getJdbc().queryForRowSet("select id,level from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"'");
		srs1 = db.getJdbc().queryForRowSet("select level from storage where id='"+req.getParameter("storage")+"'");
		isAvailable = srs1.next();
		if(isAvailable){
			id = req.getParameter("storage");
		} else {
			srs1 = db.getJdbc().queryForRowSet("select id,level from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"'");
			isAvailable = srs1.next();
			if(isAvailable)
				id = srs1.getString("id");
		}
		
		if(isAvailable){
			int level = srs1.getInt("level")-1;
			
			double capacity = 0, fill = 0;
			srs1 = db.getJdbc().queryForRowSet("select value from info_values where name='storage'");
			if(srs1.next()){
				capacity = Double.parseDouble(srs1.getString("value"));
			} else return "0";
			
			srs1 = db.getJdbc().queryForRowSet("select value from info_values where name='storage_inc'");
			if(srs1.next()){
				capacity += level*Double.parseDouble(srs1.getString("value"));
			} else return "0";
			
			srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size from storage_product,desc_product where storage='"+id+"' and desc_product.id=storage_product.desc");
			ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
			while(srs1.next()){
				tmp = srs1.getDouble("size");
				srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs1.getString("id")+"'");
				while(srs2.next()){
					tmp -= srs2.getDouble("size");
				}
				if(tmp > 0)
					products.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(tmp)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()));
				fill += srs1.getDouble("size");
			}
			
//			srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational from storage_equipment,desc_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=desc_equipment.id");
			srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational from storage_equipment,list_equipment,desc_equipment where storage='"+id+"' and storage_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
			ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select id from market_equipment where storage_equipment_id='"+srs1.getString("id")+"'");
				if(!srs2.next()){
					equipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational")));
				}
				fill += srs1.getDouble("size");
			}
			data.add(gson.toJson(isAvailable));
			data.add(gson.toJson(capacity));
			data.add(gson.toJson(fill));
			data.add(gson.toJson(products));
			data.add(gson.toJson(equipments));
			
			val = gson.toJson(data);
			
			products = null;
			equipments = null;
			
		} else {
			srs2 = db.getJdbc().queryForRowSet("select value from info_values where name='cost_storage'");
			if(srs2.next()){
				data.add(gson.toJson(isAvailable));
				data.add(gson.toJson(Double.parseDouble(srs2.getString("value"))));
				val = gson.toJson(data);
			} else return "0";
		}
		
		data = null;
		srs1 = null;
		srs2 = null;
		
		return val;
	}
	
	public String refreshClientData(HttpServletRequest req) {
		SqlRowSet srs = db.getJdbc().queryForRowSet("select * from user where name='"+req.getParameter("user")+"'");
		String val = "0";
		if(srs.next())
			val = gson.toJson(new User(srs.getString("name"), srs.getString("email"), srs.getString("dob"), srs.getString("about"), srs.getString("avatar"), srs.getDouble("money"), srs.getLong("rep"), srs.getString("zone"), new HashMap<String, String>(), srs.getInt("level")));
		else val = "0";
		
		srs = null;
		
		return val;
	}
	
	public String loadHeadquarterData(HttpServletRequest req) {
		String val = "0";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name,level from info_sector");
		ArrayList<String> sectors = new ArrayList<String>();
		ArrayList<Integer> sectorsLvl = new ArrayList<Integer>();
		while(srs.next()){
			sectors.add(srs.getString("name"));
			sectorsLvl.add(srs.getInt("level"));
		}
		
		srs = db.getJdbc().queryForRowSet("select sector from user_sector_blueprint where user='"+req.getParameter("user")+"'");
		ArrayList<String> userSectors = new ArrayList<String>();
		while(srs.next()){
			userSectors.add(srs.getString("sector"));
		}
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='sector'");
		double price;
		if(srs.next()){
			price = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(sectors));
		data.add(gson.toJson(sectorsLvl));
		data.add(gson.toJson(userSectors));
		data.add(gson.toJson(price));
		
		val = gson.toJson(data);
		
		sectors = null;
		sectorsLvl = null;
		userSectors = null;
		data = null;
		
		return val;
	}
	
	public String loadMarketContent(HttpServletRequest req) {
		String val = "0";
		SqlRowSet srs;
		
//		srs = db.getJdbc().queryForRowSet("select id from market where zone='"+req.getParameter("zone")+"'");
//		if(srs.next())
//			id = srs.getString("id");
//		else return val;
		
//		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.user,product,price,quality,market_product.size from market_product,storage_product,desc_product,storage where market='"+id+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.desc and storage.id=storage_product.storage union select market_product.id,'',product,price,quality,market_product.size from market_product,desc_product where market='"+id+"' and desc_product.id=market_product.desc");
		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.user,product,market_product.price,quality,market_product.size from market_product,storage_product,desc_product,storage where market_product.zone='"+req.getParameter("zone")+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.desc and storage.id=storage_product.storage union select market_product.id,'',product,market_product.price,quality,market_product.size from market_product,desc_product where market_product.zone='"+req.getParameter("zone")+"' and desc_product.id=market_product.desc");
		ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
		while(srs.next()){
			products.add(new MarketProduct(srs.getString("id"), srs.getString("user"), srs.getString("product"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("size")));
		}
		
//		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.user,equipment,price,quality,durability,size,operational from market_equipment,storage_equipment,desc_equipment,storage where market='"+id+"' and storage_equipment.id=storage_equipment_id and desc_equipment.id=storage_equipment.id and storage.id=storage_equipment.storage union select market_equipment.id,'',equipment,price,quality,durability,size,operational from market_equipment,desc_equipment where market='"+id+"' and desc_equipment.id=market_equipment.desc");
		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.user,equipment,market_equipment.price,quality,durability,size,operational from market_equipment,storage_equipment,desc_equipment,list_equipment,storage where market_equipment.zone='"+req.getParameter("zone")+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.desc=desc_equipment.id and storage.id=storage_equipment.storage union select market_equipment.id,'',equipment,market_equipment.price,quality,durability,size,operational from market_equipment,desc_equipment,list_equipment where market_equipment.zone='"+req.getParameter("zone")+"' and list_equipment.id=market_equipment.desc and list_equipment.desc=desc_equipment.id");
		ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
		while(srs.next()){
			equipments.add(new MarketEquipment(srs.getString("id"), srs.getString("user"), srs.getString("equipment"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational")));
		}
		
//		srs = db.getJdbc().queryForRowSet("select market_employee.id,employee,price,quality,operational from market_employee,desc_employee where market='"+id+"' and desc_employee.id=market_employee.desc");
		srs = db.getJdbc().queryForRowSet("select market_employee.id,employee,market_employee.price,quality,operational from market_employee,desc_employee,list_employee where zone='"+req.getParameter("zone")+"' and list_employee.id=market_employee.desc and desc_employee.id=list_employee.desc");
		ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
		while(srs.next()){
			employees.add(new MarketEmployee(srs.getString("id"), srs.getString("employee"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("operational")));
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
		
		return val;
	}
	
	public String getSuggestedPrice(HttpServletRequest req) {
		//Suggested price still based on info_product
		//Lately, it must use a DSS based, like AHP, or any easier method..
		String val = "0";
		double price = 0;
		
		//Deciding price starts here :
		
		if(req.getParameter("id").substring(0, 2).equals("PR")){
//			val = "select base_price from info_product where name=(select product from desc_product where id=(select storage_product.desc from storage_product where id='"+req.getParameter("id")+"')) and quality=(select quality from desc_product where id=(select storage_product.desc from storage_product where id='"+req.getParameter("id")+"'))";
			val = "select price from desc_product where id=(select storage_product.desc from storage_product where id='"+req.getParameter("id")+"')";
		}
		else if(req.getParameter("id").substring(0, 2).equals("EQ")){
//			val = "select base_price from info_equipment where name=(select equipment from desc_equipment where id='"+req.getParameter("id")+"') and quality=(select quality from desc_equipment where id='"+req.getParameter("id")+"')";
			val = "select price from desc_equipment where id=(select list_equipment.desc from list_equipment where id='"+req.getParameter("id")+"')";
		}
		
		SqlRowSet srs = db.getJdbc().queryForRowSet(val);
		if(srs.next()) 
			price = srs.getDouble("price");
		else return "0";
		
		//Deciding price ends here.
		
//		srs = db.getJdbc().queryForRowSet("select user,market,zone from user_market_license,market where user_market_license.market=market.id and user='"+req.getParameter("user")+"'");
		srs = db.getJdbc().queryForRowSet("select zone from user_market_license where user='"+req.getParameter("user")+"'");
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
		
		return val;
	}
	
	public String loadSectorOwned(HttpServletRequest req) {
		String val = "0";
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select sector,cost from user_sector_blueprint,info_sector where user='"+req.getParameter("user")+"' and sector=name");
		ArrayList<String> userSectors = new ArrayList<String>();
		ArrayList<Double> sectorCosts = new ArrayList<Double>();
		while(srs.next()){
			userSectors.add(srs.getString("sector"));
			sectorCosts.add(srs.getDouble("cost"));
		}
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id=(select zone from user where name='"+req.getParameter("user")+"')");
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
		
		return val;
	}
	
	public String loadInstallmentOwnedByUser(HttpServletRequest req) {
		String val = "0";
		val = gson.toJson(calculateInstallmentByUser(req.getParameter("user")));
		return val;
	}
	
	public String loadInstallmentDetails(HttpServletRequest req) {
		String val = "0";
		ArrayList<InstallmentEmployee> employees = new ArrayList<InstallmentEmployee>();
		ArrayList<InstallmentEquipment> equipments = new ArrayList<InstallmentEquipment>();
		ArrayList<String> data = new ArrayList<String>();
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select installment_employee.id,employee,quality,operational from installment_employee,list_employee,desc_employee where installment='"+req.getParameter("id")+"' and installment_employee.id=list_employee.id and list_employee.desc=desc_employee.id"),
				srs2;
		while(srs.next()){
			employees.add(new InstallmentEmployee(srs.getString("id"), srs.getString("employee"), srs.getInt("quality"), srs.getDouble("operational")));
		}
		
		srs = db.getJdbc().queryForRowSet("select installment_equipment.id,equipment,quality,durability,size,operational from installment_equipment,desc_equipment,list_equipment where installment='"+req.getParameter("id")+"' and installment_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
		while(srs.next()){
			equipments.add(new InstallmentEquipment(srs.getString("id"), srs.getString("equipment"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational")));
		}
		
		ArrayList<String> installmentIOdata = calculateInstallmentAndIOByIdInstallment(req.getParameter("id"));
		if(installmentIOdata.get(0).equals("Petrol Power Plant")){
			srs = db.getJdbc().queryForRowSet("select tariff from installment where id='"+req.getParameter("id")+"'");
			double tariff;
			if(srs.next())
				tariff = srs.getDouble("tariff");
			else return "0";
			
			srs = db.getJdbc().queryForRowSet("select id,type,user,planned_supply from installment where supply='"+req.getParameter("id")+"'");
			ArrayList<String> types = new ArrayList<String>(),
					users = new ArrayList<String>(),
					idSupplies = new ArrayList<String>();
			ArrayList<Double> supplies = new ArrayList<Double>();
			while(srs.next()){
				idSupplies.add(srs.getString("id"));
				types.add(srs.getString("type"));
				users.add(srs.getString("user"));
				supplies.add(srs.getDouble("planned_supply"));
			}
			
			data.add(gson.toJson(installmentIOdata));
			data.add(gson.toJson(equipments));
			data.add(gson.toJson(employees));
			data.add(gson.toJson(tariff));
			data.add(gson.toJson(types));
			data.add(gson.toJson(users));
			data.add(gson.toJson(supplies));
			data.add(gson.toJson(idSupplies));
			
			val = gson.toJson(data);
			
			types = null;
			users = null;
			supplies = null;
			
		} else {
			srs = db.getJdbc().queryForRowSet("select supply,planned_supply from installment where id='"+req.getParameter("id")+"'");
			ArrayList<String> idSupplies = new ArrayList<String>(),
					users = new ArrayList<String>(),
					tmpSupplies;
			ArrayList<Double> tariffs = new ArrayList<Double>(),
					availables = new ArrayList<Double>();
			JsonParser parser = new JsonParser();
			JsonArray array1;
			int tmp;
			double available,currentKwh;
			String currentSupply;
			
			if(srs.next()){
				currentKwh = srs.getDouble("planned_supply");
				currentSupply = srs.getString("supply");
			}
			else return "0";
			
			srs = db.getJdbc().queryForRowSet("select id,user,tariff from installment where type='Petrol Power Plant'");
			while(srs.next()){
				tmp = 0;
				tmpSupplies = calculateInstallmentAndIOByIdInstallment(srs.getString("id"));
				array1 = parser.parse(tmpSupplies.get(5)).getAsJsonArray();
				for(int i=0;i<array1.size();i++){
					if((new Gson().fromJson(array1.get(i), String.class)).equals("Energy")){
						tmp = i;
						break;
					}
				}
				array1 = parser.parse(tmpSupplies.get(6)).getAsJsonArray();
				available = new Gson().fromJson(array1.get(tmp), Double.class);
				srs2 = db.getJdbc().queryForRowSet("select planned_supply from installment where supply='"+srs.getString("id")+"'");
				while(srs2.next())
					available -= srs2.getDouble("planned_supply");
				
				idSupplies.add(srs.getString("id"));
				users.add(srs.getString("user"));
				tariffs.add(srs.getDouble("tariff"));
				availables.add(new BigDecimal(Double.valueOf(available)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
				tmpSupplies = null;
			}
			
			data.add(gson.toJson(installmentIOdata));
			data.add(gson.toJson(equipments));
			data.add(gson.toJson(employees));
			data.add(gson.toJson(idSupplies));
			data.add(gson.toJson(users));
			data.add(gson.toJson(tariffs));
			data.add(gson.toJson(availables));
			data.add(gson.toJson(currentKwh));
			data.add(gson.toJson(currentSupply));
			
			val = gson.toJson(data);
			
			idSupplies = null;
			users = null;
			tariffs = null;
		}
		
		employees = null;
		equipments = null;
		data = null;
		
		return val;
	}
	
	public String loadInstallmentOwnedByEquipment(HttpServletRequest req) {
		String val = "0";
		val = gson.toJson(calculateInstallmentBySelectedEquipment(req.getParameter("user"), req.getParameter("equipment_type")));
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
		
		return val;
	}
	
	public String deleteUserData(HttpServletRequest req) {
		String val="Ok",sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1,srs2;
		
		sqlL.add("delete from user where name='"+req.getParameter("user")+"'");
		sqlL.add("delete from user_market_license where user='"+req.getParameter("user")+"'");
		sqlL.add("delete from user_sector_blueprint where user='"+req.getParameter("user")+"'");
		sqlL.add("delete from req_borrow_bank where user='"+req.getParameter("user")+"'");
		sqlL.add("delete from borrow_bank where user='"+req.getParameter("user")+"'");
		
		srs1 = db.getJdbc().queryForRowSet("select id from storage where user='"+req.getParameter("user")+"'");
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from storage_product where storage='"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from market_product where storage_product_id='"+srs2.getString("id")+"'");
				sqlL.add("delete from product_advertisement where product_id='"+srs2.getString("id")+"'");
			}
			sqlL.add("delete from storage_product where storage='"+srs1.getString("id")+"'");
			
			srs2 = db.getJdbc().queryForRowSet("select id from storage_equipment where storage='"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from market_equipment where storage_equipment_id='"+srs2.getString("id")+"'");
				sqlL.add("delete from list_equipment where id='"+srs2.getString("id")+"'");
			}
			sqlL.add("delete from storage_equipment where storage='"+srs1.getString("id")+"'");
		}
		
		sqlL.add("delete from storage where user='"+req.getParameter("user")+"'");
		
		srs1 = db.getJdbc().queryForRowSet("select id from installment where user='"+req.getParameter("user")+"'");
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
		
		sqlL.add("delete from installment where user='"+req.getParameter("user")+"'");
		
		for(String x : sqlL)
			System.out.println(x);
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		
		return val;
	}
	
	/* Get function ends here : */
	
	/* Post function starts here : */
	
	public String loginUser(HttpServletRequest req){
		String val = "Ok";
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select * from user where name='"+req.getParameter("user")+"'"),
				srs2 = db.getJdbc().queryForRowSet("select id,zone from storage where user='"+req.getParameter("user")+"'");
		User userAcc;
		if(srs1.next()){
			HashMap<String, String> storages = new HashMap<String, String>();
			while(srs2.next()){
				storages.put(srs2.getString("zone"), srs2.getString("id"));
			}
			if(req.getParameter("pass").equals(srs1.getString("pass"))){
				userAcc = new User(srs1.getString("name"), srs1.getString("email"), srs1.getString("dob"), srs1.getString("about"), srs1.getString("avatar"), srs1.getDouble("money"), srs1.getLong("rep"), srs1.getString("zone"), storages, srs1.getInt("level"));
				val = gson.toJson(userAcc);
			} else val = "0";
			
			storages = null;
			
		} else val = "1";
		
		userAcc = null;
		srs1 = null;
		srs2 = null;
		
		return val;
	}

	public String registerUser(HttpServletRequest req) {
		String val = "Ok",sqls[],idInc;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name from user where name='"+req.getParameter("user")+"'");
		ArrayList<String> sqlL = new ArrayList<String>();
		
		if(srs.next())
			return "1";
		sqlL.add("insert into user values ('"+req.getParameter("user")+"','"+req.getParameter("pass")+"','"+req.getParameter("email")+"','"+req.getParameter("dob")+"','This is me','','0.00','0','"+req.getParameter("zone")+"','1')");
		
		idInc = getUniqueIncrementId("inc_user_market_license");
		
		sqlL.add("insert into user_market_license values ('"+KEY_USER_MARKET_LICENSE+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqls = null;
		sqlL = null;
		srs = null;
		idInc = null;
		
		return val;
	}

	public String submitProposal(HttpServletRequest req) {
		String val = "0", turn, idInc, zone;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select value from info_values where name='turn'");
		
		if(srs.next())
			turn = srs.getString("value");
		else return val;
		
		srs = db.getJdbc().queryForRowSet("select zone from user where name='"+req.getParameter("user")+"'");
		if(srs.next())
			zone = srs.getString("zone");
		else return val;
		
		idInc = getUniqueIncrementId("inc_req_borrow_bank");
		
		db.getJdbc().execute("insert into req_borrow_bank values ('"+KEY_REQUEST_BORROW_BANK+idInc+"','"+req.getParameter("user")+"','"+turn+"','"+req.getParameter("sector")+"','"+req.getParameter("turn")+"','"+(Boolean.parseBoolean(req.getParameter("storage"))? 1 : 0)+"','"+zone+"')");
		val = "Ok";
		
		turn = null;
		zone = null;
		idInc = null;
		srs = null;
		
		return val;
	}
	
	public String buildUserStorage(HttpServletRequest req) {
		String val = "0", idInc, sqls[];
		double money,price;
		SqlRowSet srs;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		srs = db.getJdbc().queryForRowSet("select money from user where name='"+req.getParameter("user")+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='cost_storage'");
		
		if(srs.next())
			price = Double.parseDouble(srs.getString("value"));
		else return "0";
		
		if(money < price)
			return "1";
		
		money -= price;
		
		idInc = getUniqueIncrementId("inc_storage");
		
		sqlL.add("insert into storage values ('"+KEY_STORAGE+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"','1')");
		sqlL.add("update user set money='"+money+"' where name='"+req.getParameter("user")+"'");
		
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
		
		return val;
	}
	
	public String buyMarketProduct(HttpServletRequest req) {
		String val = "0", seller = "", user, userZone, productIdSeller, desc, product, idInc,sqls[];
		double userMoney, sellerMoney, price, storage = 0, picked, size;
		int quality, level;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name,money,zone from user where name=(select user from storage where id='"+req.getParameter("storage")+"')");
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
		
		srs = db.getJdbc().queryForRowSet("select storage_product_id,market_product.desc,price,size from market_product where id='"+req.getParameter("productId")+"'");
		if(srs.next()){
			price = srs.getDouble("price");
			size = srs.getDouble("size");
			productIdSeller = srs.getString("storage_product_id");
			desc = srs.getString("desc");
		} else return "1";
		
		if(!productIdSeller.equals("")){
			srs = db.getJdbc().queryForRowSet("select storage.user,user.money,product,quality from storage_product,desc_product,storage,user where storage_product.id='"+productIdSeller+"' and storage_product.desc=desc_product.id and storage_product.storage=storage.id and storage.user=user.name");
			if(srs.next()){
				seller = srs.getString("user");
				product = srs.getString("product");
				quality = srs.getInt("quality");
				sellerMoney = srs.getDouble("money");
			} else return "0";
		} else {
			srs = db.getJdbc().queryForRowSet("select product,quality from desc_product where id='"+desc+"'");
			if(srs.next()){
				product = srs.getString("product");
				quality = srs.getInt("quality");
			} else return "0";
			sellerMoney = 0;
		}
		
		System.out.println("Tanda 2");
		
		picked = Double.parseDouble(req.getParameter("picked"));
		price *= picked;
		
		if(size < picked)
			return "2";
		
		if(userMoney < price)
			return "3";
		
		size -= picked;
		userMoney -= price;
		sellerMoney += price;
		
		srs = db.getJdbc().queryForRowSet("select id,level from storage where id='"+req.getParameter("storage")+"'");
		if(srs.next()){
			level = srs.getInt("level")-1;
		}
		else return "4";
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='storage' union select value from info_values where name='storage_inc'");
		if(srs.next()){
			storage = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		System.out.println("Tanda 3");
		
		if(srs.next()){
			storage += level*Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		System.out.println("Tanda 4");
		
//		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage='"+req.getParameter("storage")+"' union select storage_equipment.id,size from storage_equipment,desc_equipment,storage where storage='"+req.getParameter("storage")+"' and desc_equipment.id=storage_equipment.id and storage.id=storage_equipment.storage");
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage='"+req.getParameter("storage")+"' union select storage_equipment.id,size from storage_equipment,desc_equipment,list_equipment,storage where storage='"+req.getParameter("storage")+"' and list_equipment.id=storage_equipment.id and list_equipment.desc=desc_equipment.id and storage.id=storage_equipment.storage");
		while(srs.next()){
			storage -= srs.getDouble("size");
		}
		
		if(storage < picked)
			return "5";
		
		srs = db.getJdbc().queryForRowSet("select id from desc_product where product='"+product+"' and quality='"+quality+"'");
		if(srs.next())
			desc = srs.getString("id");
		else return "0";
		
		System.out.println("Tanda 5");
		
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc='"+desc+"' and storage='"+req.getParameter("storage")+"'");
		if(srs.next()){
			sqlL.add("update storage_product set size='"+(srs.getDouble("size")+picked)+"' where id='"+srs.getString("id")+"'");
		}
		
		if(sqlL.size() < 1){
			idInc = getUniqueIncrementId("inc_storage_product");
			sqlL.add("insert into storage_product values ('"+KEY_PRODUCT+idInc+"','"+desc+"','"+req.getParameter("storage")+"','"+picked+"')");
		}
		
		if(size > 0)
			sqlL.add("update market_product set size='"+size+"' where id='"+req.getParameter("productId")+"'");
		else sqlL.add("delete from market_product where id='"+req.getParameter("productId")+"'");
		
		sqlL.add("update user set money='"+userMoney+"' where name='"+user+"'");
		if(!seller.equals("")){
			sqlL.add("update user set money='"+sellerMoney+"' where name='"+seller+"'");
			
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
		}
		
		System.out.println("Tanda 6");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
//		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.user,product,price,quality,market_product.size from market_product,storage_product,desc_product,storage where market='"+marketId+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.desc and storage.id=storage_product.storage union select market_product.id,'',product,price,quality,market_product.size from market_product,desc_product where market='"+marketId+"' and desc_product.id=market_product.desc");
		srs = db.getJdbc().queryForRowSet("select market_product.id,storage.user,product,market_product.price,quality,market_product.size from market_product,storage_product,desc_product,storage where market_product.zone='"+userZone+"' and storage_product.id=storage_product_id and desc_product.id=storage_product.desc and storage.id=storage_product.storage union select market_product.id,'',product,market_product.price,quality,market_product.size from market_product,desc_product where market_product.zone='"+userZone+"' and desc_product.id=market_product.desc");
		ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
		while(srs.next()){
			products.add(new MarketProduct(srs.getString("id"), srs.getString("user"), srs.getString("product"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("size")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(userMoney));
		data.add(gson.toJson(products));
		
		val = gson.toJson(data);
		
		product = null;
		sqlL = null;
		data = null;
		sqls = null;
		
		return val;
	}
	
	public String buyMarketEquipment(HttpServletRequest req) {
		String val = "0", seller, user, userZone, equipmentIdSeller, desc, sqls[];
		double userMoney, sellerMoney, price, storage = 0, size;
		int level;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0 = internal error
		//1 = produk dah abis
		//2 = uang ga cukup
		//3 = storage ga ada
		//4 = storage ga cukup
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name,money,zone from user where name=(select user from storage where id='"+req.getParameter("storage")+"')");
		if(srs.next()){
			user = srs.getString("name");
			userMoney = srs.getDouble("money");
			userZone = srs.getString("zone");
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select storage_equipment_id,market_equipment.desc,market_equipment.price,size from market_equipment,list_equipment,desc_equipment where market_equipment.id='"+req.getParameter("equipmentId")+"' and list_equipment.desc=desc_equipment.id and (storage_equipment_id=list_equipment.id or market_equipment.desc=list_equipment.id)");
		if(srs.next()){
			price = srs.getDouble("price");
			size = srs.getDouble("size");
			equipmentIdSeller = srs.getString("storage_equipment_id");
			desc = srs.getString("desc");
		} else return "1";
		
		if(!equipmentIdSeller.equals("")){
			srs = db.getJdbc().queryForRowSet("select name,money from user where name=(select user from storage where id=(select storage from storage_equipment where id='"+equipmentIdSeller+"'))");
			if(srs.next()){
				seller = srs.getString("name");
				sellerMoney = srs.getDouble("money");
			} else return "0";
		} else {
			seller = "";
			sellerMoney = 0;
		}
		
		if(userMoney < price)
			return "2";
		
		userMoney -= price;
		sellerMoney += price;
		
		srs = db.getJdbc().queryForRowSet("select level from storage where id='"+req.getParameter("storage")+"'");
		if(srs.next()){
			level = srs.getInt("level")-1;
		} else return "3";
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='storage' union select value from info_values where name='storage_inc'");
		if(srs.next()){
			storage = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		if(srs.next()){
			storage += level*Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage='"+req.getParameter("storage")+"' union select storage_equipment.id,size from storage_equipment,desc_equipment,list_equipment,storage where storage='"+req.getParameter("storage")+"' and list_equipment.id=storage_equipment.id and list_equipment.desc=desc_equipment.id and storage.id=storage_equipment.storage");
//		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage='"+req.getParameter("storage")+"' union select storage_equipment.id,size from storage_equipment,desc_equipment,storage where storage='"+req.getParameter("storage")+"' and desc_equipment.id=storage_equipment.id and storage.id=storage_equipment.storage");
		while(srs.next()){
			storage -= srs.getDouble("size");
		}
		
		if(storage < size)
			return "4";
		
		if(!equipmentIdSeller.equals(""))
			sqlL.add("update storage_equipment set storage='"+req.getParameter("storage")+"' where id='"+equipmentIdSeller+"'");
		else sqlL.add("insert into storage_equipment values ('"+desc+"','"+req.getParameter("storage")+"')");
		
		sqlL.add("delete from market_equipment where id='"+req.getParameter("equipmentId")+"'");
		
		sqlL.add("update user set money='"+userMoney+"' where name='"+user+"'");
		if(!seller.equals("")){
			sqlL.add("update user set money='"+sellerMoney+"' where name='"+seller+"'");
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		
//		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.user,equipment,price,quality,durability,size,operational from market_equipment,storage_equipment,desc_equipment,storage where zone='"+req.getParameter("zone")+"' and storage_equipment.id=storage_equipment_id and desc_equipment.id=storage_equipment.id and storage.id=storage_equipment.storage union select market_equipment.id,'',equipment,price,quality,durability,size,operational from market_equipment,desc_equipment where zone='"+req.getParameter("zone")+"' and desc_equipment.id=market_equipment.desc");
		srs = db.getJdbc().queryForRowSet("select market_equipment.id,storage.user,equipment,market_equipment.price,quality,durability,size,operational from market_equipment,storage_equipment,desc_equipment,list_equipment,storage where market_equipment.zone='"+userZone+"' and storage_equipment.id=storage_equipment_id and list_equipment.id=storage_equipment.id and list_equipment.desc=desc_equipment.id and storage.id=storage_equipment.storage union select market_equipment.id,'',equipment,market_equipment.price,quality,durability,size,operational from market_equipment,desc_equipment,list_equipment where market_equipment.zone='"+userZone+"' and list_equipment.id=market_equipment.desc and list_equipment.desc=desc_equipment.id");
		ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
		while(srs.next()){
			equipments.add(new MarketEquipment(srs.getString("id"), srs.getString("user"), srs.getString("equipment"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(userMoney));
		data.add(gson.toJson(equipments));
		
		val = gson.toJson(data);
		
		sqlL = null;
		equipments = null;
		data = null;
		sqls = null;
		
		return val;
	}
	
	public String sellStorageProduct(HttpServletRequest req) {
		String val="0", idInc, sqls[];
		double remain,size,offer;
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1, srs2;
		
		//0 = internal error
		//1 = CBM yg ditawarkan lebih besar dari yg dimiliki
		
		srs1 = db.getJdbc().queryForRowSet("select size from storage_product where id='"+req.getParameter("productId")+"'");
		if(srs1.next())
			size = srs1.getDouble("size");
		else return "0";
		
		srs1 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+req.getParameter("productId")+"'");
		if(srs1.next())
			size -= srs1.getDouble("size");
		
		offer = Double.parseDouble(req.getParameter("offer"));
		
		if(size < offer)
			return "1";
		
		srs1 = db.getJdbc().queryForRowSet("select id,size from market_product where storage_product_id='"+req.getParameter("productId")+"' and zone='"+req.getParameter("marketZone")+"' and price='"+req.getParameter("price")+"'");
		if(srs1.next())
			sqlL.add("update market_product set size='"+(srs1.getDouble("size")+offer)+"' where id='"+srs1.getString("id")+"'");
		else {
			idInc = getUniqueIncrementId("inc_market_product");
			sqlL.add("insert into market_product values ('"+KEY_MARKET_PRODUCT+idInc+"','"+req.getParameter("productId")+"','','"+req.getParameter("marketZone")+"','"+req.getParameter("price")+"','"+offer+"')");
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		val = "Ok";
		
		srs1 = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size from storage_product,desc_product where storage='"+req.getParameter("storage")+"' and desc_product.id=storage_product.desc");
		ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
		while(srs1.next()){
			remain = srs1.getDouble("size");
			srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs1.getString("id")+"'");
			while(srs2.next()){
				remain -= srs2.getDouble("size");
			}
			if(remain > 0)
				products.add(new StorageProduct(srs1.getString("id"), srs1.getString("product"), srs1.getInt("quality"), new BigDecimal(Double.valueOf(remain)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()));
		}
		
		val=gson.toJson(products);
		
		sqlL = null;
		products = null;
		sqls = null;
		
		return val;
	}
	
	public String sellStorageEquipment(HttpServletRequest req) {
		String val="0", idInc, sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1, srs2;
		
//		srs = db.getJdbc().queryForRowSet("select id from market where zone='"+req.getParameter("marketZone")+"'");
//		if(srs.next())
//			marketId = srs.getString("id");
//		else return "0";
		
		idInc = getUniqueIncrementId("inc_market_equipment");
		sqlL.add("insert into market_equipment values ('"+KEY_MARKET_EQUIPMENT+idInc+"','"+req.getParameter("equipmentId")+"','','"+req.getParameter("marketZone")+"','"+req.getParameter("price")+"')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);

//		srs = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational from storage_equipment,desc_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=desc_equipment.id");
		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational from storage_equipment,list_equipment,desc_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
		ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from market_equipment where storage_equipment_id='"+srs1.getString("id")+"'");
			if(!srs2.next())
				equipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational")));
		}
		
		val=gson.toJson(equipments);
		
		equipments = null;
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		
		return val;
	}
	
	public String createNewInstallment(HttpServletRequest req) {
		String val = "", idInc, sqls[];
		double money, cost;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0=internal error
		//1=uang ga cukup
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select money from user where name='"+req.getParameter("user")+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select cost from info_sector where name='"+req.getParameter("type")+"'");
		if(srs.next())
			cost = srs.getDouble("cost");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+req.getParameter("zone")+"'");
		if(srs.next())
			cost += srs.getDouble("cost");
		else return "0";
		
		if(money < cost)
			return "1";
		
		money -= cost;
		
		sqlL.add("update user set money='"+money+"' where name='"+req.getParameter("user")+"'");
		idInc = getUniqueIncrementId("inc_installment");
		sqlL.add("insert into installment values ('"+KEY_INSTALLMENT+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"','"+req.getParameter("type")+"','','0','0','0')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		
		val = loadInstallmentOwnedByUser(req);
		
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
		
//		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational from storage_equipment,desc_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=desc_equipment.id");
		srs1 = db.getJdbc().queryForRowSet("select storage_equipment.id,equipment,quality,durability,size,operational from storage_equipment,list_equipment,desc_equipment where storage='"+req.getParameter("storage")+"' and storage_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
		
		ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from market_equipment where storage_equipment_id='"+srs1.getString("id")+"'");
			if(!srs2.next())
				equipments.add(new StorageEquipment(srs1.getString("id"), srs1.getString("equipment"), srs1.getInt("quality"), srs1.getDouble("durability"), srs1.getDouble("size"), srs1.getDouble("operational")));
		}
		
		val=gson.toJson(equipments);
		
		sqlL = null;
		equipments = null;
		srs1 = null;
		srs2 = null;
		sqls = null;
		
		return val;
	}
	
	public String detachEquipment(HttpServletRequest req) {
		String val="0",idStorage,sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs; 
		
		sqlL.add("delete from installment_equipment where id='"+req.getParameter("idEquipment")+"'");
		
		srs = db.getJdbc().queryForRowSet("select id from storage where user='"+req.getParameter("user")+"' and zone=(select zone from user where name='"+req.getParameter("user")+"')");
		if(srs.next())
			idStorage = srs.getString("id");
		else {
			sqlL = null;
			return "0";
		}
		
		sqlL.add("insert into storage_equipment values ('"+req.getParameter("idEquipment")+"','"+idStorage+"')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select installment_equipment.id,equipment,quality,durability,size,operational from installment_equipment,desc_equipment where installment='"+req.getParameter("id")+"' and installment_equipment.id=desc_equipment.id");
		ArrayList<InstallmentEquipment> equipments = new ArrayList<InstallmentEquipment>();
		while(srs.next()){
			equipments.add(new InstallmentEquipment(srs.getString("id"), srs.getString("equipment"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational")));
		}
		
		val = gson.toJson(equipments);
		
		equipments = null;
		sqlL = null;
		
		return val;
	}
	
	public String hireEmployeeToInstallment(HttpServletRequest req) {
		String val = "",idEmployee,sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs;
		double money,price;
		
		System.out.println("test");
		
		//0=internal error
		//1=karyawan keburu diambil orang
		//2=uang ga cukup
		
		srs = db.getJdbc().queryForRowSet("select money from user where name='"+req.getParameter("user")+"'");
		if(srs.next())
			money = srs.getDouble("money");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select market_employee.desc,price from market_employee where id='"+req.getParameter("idEmployee")+"'");
		if(srs.next()){
			idEmployee = srs.getString("desc");
			price = srs.getDouble("price");
		} else return "1";
		
		if(money < price)
			return "2";
		
		money -= price;
		
		System.out.println("test 2");
		
		sqlL.add("insert into installment_employee values ('"+idEmployee+"','"+req.getParameter("idInstallment")+"')");
		sqlL.add("delete from market_employee where id='"+req.getParameter("idEmployee")+"'");
		sqlL.add("update user set money='"+money+"' where name='"+req.getParameter("user")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select market_employee.id,employee,market_employee.price,quality,operational from market_employee,desc_employee,list_employee where zone='"+req.getParameter("zone")+"' and list_employee.id=market_employee.desc and desc_employee.id=list_employee.desc");
//		srs = db.getJdbc().queryForRowSet("select market_employee.id,employee,price,quality,operational from market_employee,desc_employee where market=(select id from market where zone='"+req.getParameter("zone")+"') and desc_employee.id=market_employee.desc");
		ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
		while(srs.next()){
			employees.add(new MarketEmployee(srs.getString("id"), srs.getString("employee"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("operational")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(money));
		data.add(gson.toJson(employees));
		
		val = gson.toJson(data);
		
		data = null;
		employees = null;
		sqlL = null;
		
		return val;
	}
	
	public String fireEmployee(HttpServletRequest req) {
		String val = "",idMarket,idInc,sqls[];
		ArrayList<String> sqlL = new ArrayList<String>();
		double price;
		SqlRowSet srs;
		
		srs = db.getJdbc().queryForRowSet("select base_price from info_employee,desc_employee where desc_employee.id='"+req.getParameter("idEmployee")+"' and desc_employee.employee=info_employee.name and desc_employee.quality=info_employee.quality");
		if(srs.next())
			price = srs.getDouble("base_price");
		else {
			sqlL = null;
			return "0";
		}
		
		srs = db.getJdbc().queryForRowSet("select id from market where zone=(select zone from user where name='"+req.getParameter("user")+"')");
		if(srs.next())
			idMarket = srs.getString("id");
		else {
			sqlL = null;
			return "0";
		}
		
		idInc = getUniqueIncrementId("inc_market_employee");
		sqlL.add("delete from installment_employee where id='"+req.getParameter("idEmployee")+"'");
		sqlL.add("insert into market_employee values ('"+KEY_MARKET_EMPLOYEE+idInc+"','"+req.getParameter("idEmployee")+"','"+idMarket+"','"+price+"')");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		srs = db.getJdbc().queryForRowSet("select installment_employee.id,employee,quality,operational from installment_employee,desc_employee where installment='"+req.getParameter("id")+"' and installment_employee.id=desc_employee.id");
		ArrayList<InstallmentEmployee> employees = new ArrayList<InstallmentEmployee>();
		while(srs.next()){
			employees.add(new InstallmentEmployee(srs.getString("id"), srs.getString("employee"), srs.getInt("quality"), srs.getDouble("operational")));
		}
		
		val = gson.toJson(employees);
		
		employees = null;
		sqlL = null;
		
		return val;
	}
	
	public String updateTariff(HttpServletRequest req) {
		String val="Ok";
		db.getJdbc().execute("update installment set tariff='"+req.getParameter("tariff")+"' where id='"+req.getParameter("id")+"'");
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
		double money, price;
		int level,userLevel;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0=internal error
		//1=uang ga cukup
		//2=level belum cukup
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select money,level from user where name='"+req.getParameter("user")+"'");
		if(srs.next()){
			money = srs.getDouble("money");
			userLevel = srs.getInt("level");
		}
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='sector'");
		if(srs.next()){
			price = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select level from info_sector where name='"+req.getParameter("sector")+"'");
		if(srs.next())
			level = srs.getInt("level");
		else return "0";
		
		srs = db.getJdbc().queryForRowSet("select sector from user_sector_blueprint where user='"+req.getParameter("user")+"'");
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
		
		idInc = getUniqueIncrementId("inc_user_sector_blueprint");
		sqlL.add("insert into user_sector_blueprint values ('"+KEY_USER_SECTOR_BLUEPRINT+idInc+"','"+req.getParameter("user")+"','"+req.getParameter("sector")+"')");
		sqlL.add("update user set money='"+money+"', level='"+userLevel+"' where name='"+req.getParameter("user")+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		val = "Ok";
		
		sqlL = null;
		sqls = null;
		idInc = null;
		srs = null;
		
		return val;
	}
	
	public String buyBundleEquipmentEmployee(HttpServletRequest req) {
		String val="",sqls[],user,idInc;
		int quality,items;
		double total = 0,userMoney;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		//0=internal error
		//1=uang ga cukup
		
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select money,type,user from installment,user where id='"+req.getParameter("installment")+"' and user=name"),
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
					idInc = getUniqueIncrementId("inc_equipment");
					sqlL.add("insert into list_equipment values('"+KEY_EQUIPMENT+idInc+"','"+srs2.getString("id")+"','100')");
					sqlL.add("insert into installment_equipment values ('"+KEY_EQUIPMENT+idInc+"','"+req.getParameter("installment")+"')");
				}
			}
			
			System.out.println("Tes 3");
			
			srs2 = db.getJdbc().queryForRowSet("select desc_employee.id,price,items from desc_employee,info_sector_employee where sector='"+srs1.getString("type")+"' and quality='"+quality+"' and employee=employee_type");
			while(srs2.next()){
				items = srs2.getInt("items");
				total += srs2.getDouble("price")*items;
				for(int i=0;i<items;i++){
					idInc = getUniqueIncrementId("inc_employee");
					sqlL.add("insert into list_employee values ('"+KEY_EMPLOYEE+idInc+"','"+srs2.getString("id")+"')");
					sqlL.add("insert into installment_employee values ('"+KEY_EMPLOYEE+idInc+"','"+req.getParameter("installment")+"')");
				}
			}
			
			System.out.println("Tes 4");
			
			if(userMoney < total)
				return "1";
			
			userMoney -= total;
			sqlL.add("update user set money='"+userMoney+"' where name='"+user+"'");
			
			sqls = new String[sqlL.size()];
			sqlL.toArray(sqls);
			db.getJdbc().batchUpdate(sqls);
			
			val = gson.toJson(userMoney);
			
		} else return "0";
		
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs2 = null;
		
		return val;
	}
	
	public String cancelSellStorageProduct(HttpServletRequest req){
		String val="";
		double remain;
		
		db.getJdbc().execute("delete from market_product where id='"+req.getParameter("id")+"'");
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select storage_product.id,product,quality,size from storage_product,desc_product where storage=(select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"') and desc_product.id=storage_product.desc"),
				srs1;
		ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
		while(srs.next()){
			remain = srs.getDouble("size");
			srs1 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs.getString("id")+"'");
			while(srs1.next()){
				remain -= srs1.getDouble("size");
			}
			if(remain > 0)
				products.add(new StorageProduct(srs.getString("id"), srs.getString("product"), srs.getInt("quality"), new BigDecimal(Double.valueOf(remain)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()));
		}
		
		val=gson.toJson(products);
		
		products = null;
		
		return val;
	}
	
	/* Post function ends here : */
	
	/* Library : */
	
	private String dateNow(String format){
		Calendar tglSkrg = Calendar.getInstance();
		SimpleDateFormat formatTglCari = new SimpleDateFormat(format);
		return formatTglCari.format(tglSkrg.getTime());
	}
	
	private ArrayList<String> calculateInstallmentByUser(String user){
		String hiElement="";
		double hiVal = 0;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,zone,type from installment where user='"+user+"' and zone=(select zone from user where name='"+user+"')"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		ArrayList<String> id = new ArrayList<String>(), 
				installment = new ArrayList<String>(),
				zones = new ArrayList<String>(),
				data = new ArrayList<String>();
		ArrayList<Double> efficiency = new ArrayList<Double>();
		ArrayList<Integer> effectivity = new ArrayList<Integer>();
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
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+srs1.getString("id")+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
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
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+srs1.getString("id")+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.desc=desc_employee.id");
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
					id.add(srs1.getString("id"));
					installment.add(srs1.getString("type"));
					zones.add(srs1.getString("zone"));
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							efficiency.add(new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
						else efficiency.add(0.0);
						effectivity.add(eff+1);
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							efficiency.add(new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
						else efficiency.add(0.0);
						effectivity.add(eff);
					}
					break;
				}
			}
		}
		data.add(gson.toJson(id));
		data.add(gson.toJson(installment));
		data.add(gson.toJson(zones));
		data.add(gson.toJson(efficiency));
		data.add(gson.toJson(effectivity));
		return data;
	}
	
	private ArrayList<String> calculateInstallmentBySelectedEquipment(String user, String equipmentType){
		String hiElement="";
		double hiVal = 0;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select installment.id,zone,type from info_sector_equipment,installment where equipment_type='"+equipmentType+"' and user='"+user+"' and zone=(select zone from user where name='"+user+"') and installment.type=sector"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>();
		ArrayList<String> id = new ArrayList<String>(), 
				installment = new ArrayList<String>(),
				zones = new ArrayList<String>(),
				data = new ArrayList<String>();
		ArrayList<Double> efficiency = new ArrayList<Double>();
		ArrayList<Integer> effectivity = new ArrayList<Integer>();
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
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+srs1.getString("id")+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
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
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+srs1.getString("id")+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.desc=desc_employee.id");
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
					id.add(srs1.getString("id"));
					installment.add(srs1.getString("type"));
					zones.add(srs1.getString("zone"));
					eff = elements.get(hiElement).intValue()/elementsRatio.get(hiElement).intValue();
					if(elements.get(hiElement) % elementsRatio.get(hiElement) > 0){
						hiVal = (elementsRatio.get(hiElement)*(eff+1));
						if(hiVal > 0)
							efficiency.add(new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*(eff+1)))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
						else efficiency.add(0.0);
						effectivity.add(eff+1);
					} else {
						hiVal = (elementsRatio.get(hiElement)*eff);
						if(hiVal > 0)
							efficiency.add(new BigDecimal(Double.valueOf(elementsCalc.get(hiElement)/(elementsRatio.get(hiElement)*eff))).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
						else efficiency.add(0.0);
						effectivity.add(eff);
					}
					break;
				}
			}
		}
		data.add(gson.toJson(id));
		data.add(gson.toJson(installment));
		data.add(gson.toJson(zones));
		data.add(gson.toJson(efficiency));
		data.add(gson.toJson(effectivity));
		return data;
	}
	
	private ArrayList<String> calculateInstallmentAndIOByIdInstallment(String idInstallment){
		String hiElement="";
		double hiVal = 0,efficiency = 0,effectivity = 0;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select type from installment where id='"+idInstallment+"'"),
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
				srs3 = db.getJdbc().queryForRowSet("select count(installment_equipment.id) from installment_equipment,list_equipment,desc_equipment where installment='"+idInstallment+"' and desc_equipment.equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
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
				srs3 = db.getJdbc().queryForRowSet("select count(installment_employee.id) from installment_employee,list_employee,desc_employee where installment='"+idInstallment+"' and desc_employee.employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.desc=desc_employee.id");
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
		return data;
	}
	
	private String getUniqueIncrementId(String inc_table){
		String val = "", curDate, date, sqls[];
		int counter;
		ArrayList<String> sqlL = new ArrayList<String>();
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select value from info_values where name='"+inc_table+"' union select value from info_values where name='last_inc_set_date'");
		if(srs.next())
			counter = Integer.parseInt(srs.getString("value"));
		else return val;
		
		curDate = dateNow("ddMMyy");
		
		if(srs.next())
			date = srs.getString("value");
		else return val;
		
		if(!date.equals(curDate)){
			date = curDate;
			counter = 0;
			sqlL.add("update info_values set value='"+date+"' where name='last_inc_set_date'");
			
			//update all inc into 0 value
			sqlL.add("update info_values set value='0' where substr(name,1,4)='inc_'");
		}
		
		if(counter > 999)
			val = ""+counter;
		else if(counter > 99)
			val = "0"+counter;
		else if(counter > 9)
			val = "00"+counter;
		else val = "000"+counter;
		sqlL.add("update info_values set value='"+(counter+1)+"' where name='"+inc_table+"'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		return date+val;
	}
}
