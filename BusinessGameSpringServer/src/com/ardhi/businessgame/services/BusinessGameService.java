package com.ardhi.businessgame.services;

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
import com.ardhi.businessgame.models.MarketEmployee;
import com.ardhi.businessgame.models.MarketEquipment;
import com.ardhi.businessgame.models.MarketProduct;
import com.ardhi.businessgame.models.OutputInfo;
import com.ardhi.businessgame.models.Sectors;
import com.ardhi.businessgame.models.StorageEquipment;
import com.ardhi.businessgame.models.StorageProduct;
import com.ardhi.businessgame.models.User;
import com.google.gson.Gson;

@Service("businessGameService")
public class BusinessGameService {
	public static final String KEY_REQUEST_BORROW_BANK = "RB",
			KEY_BORROW_BANK = "BB",
			KEY_STORAGE = "ST",
			KEY_PRODUCT = "PR",
			KEY_EQUIPMENT = "EQ";
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
		
		srs = db.getJdbc().queryForRowSet("select name, cost from info_sector");
		while(srs.next()){
			sectorList.add(srs.getString("name"));
			priceList.add(srs.getDouble("cost"));
		}
		
		srs = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+req.getParameter("zone")+"'");
		if(srs.next()){
			data.add(gson.toJson(sectorList));
			data.add(gson.toJson(priceList));
			data.add(gson.toJson(srs.getDouble("cost")));
		} else return "0";
		
		ArrayList<BusinessSectorInfo> bsiList = new ArrayList<BusinessSectorInfo>();
		ArrayList<IndustrialEquipmentInfo> ie;
		ArrayList<EmployeeInfo> e;
		ArrayList<InputInfo> i;
		ArrayList<OutputInfo> o;
		
		for(String sector : sectorList){
			SqlRowSet srs1 = db.getJdbc().queryForRowSet("select equipment_type, items from info_sector_equipment where name='"+sector+"'"),
					srs2;
			
			ie = new ArrayList<IndustrialEquipmentInfo>();
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select base_price, base_op_cost from info_equipment where name='"+srs1.getString("equipment_type")+"' and quality='2'");
				if(srs2.next()){
					ie.add(new IndustrialEquipmentInfo(srs1.getString("equipment_type"), srs1.getInt("items"), srs2.getDouble("base_price"), srs2.getDouble("base_op_cost")));
				} else return "0";
				
			}
			
			srs1 = db.getJdbc().queryForRowSet("select employee_type, items from info_sector_employee where name='"+sector+"'");
			e = new ArrayList<EmployeeInfo>();
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select base_price, base_op_cost from info_employee where name='"+srs1.getString("employee_type")+"' and quality='2'");
				if(srs2.next()){
					e.add(new EmployeeInfo(srs1.getString("employee_type"), srs1.getInt("items"), srs2.getDouble("base_price"), srs2.getDouble("base_op_cost")));
				} else return "0";
				
			}
			
			srs1 = db.getJdbc().queryForRowSet("select input_type, size from info_sector_input where name='"+sector+"'");
			i = new ArrayList<InputInfo>();
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select base_price from info_product where name='"+srs1.getString("input_type")+"' and quality='2'");
				if(srs2.next()){
					i.add(new InputInfo(srs1.getString("input_type"), srs1.getDouble("size"), srs2.getDouble("base_price")));
				} else return "0";
			}
			
			srs1 = db.getJdbc().queryForRowSet("select output_type, size from info_sector_output where name='"+sector+"'");
			o = new ArrayList<OutputInfo>();
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select base_price from info_product where name='"+srs1.getString("output_type")+"' and quality='2'");
				if(srs2.next()){
					o.add(new OutputInfo(srs1.getString("output_type"), srs1.getDouble("size"), srs2.getDouble("base_price")));
				} else return "0";
			}
			
			bsiList.add(new BusinessSectorInfo(ie, e, i, o));
		}
		
		data.add(gson.toJson(bsiList));
		
		val = gson.toJson(data);
		
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
		String val = "No";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id,level from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"'");
		if(srs.next())
			val = "Yes";
		else return "No";
		
		String storageId = srs.getString("id");
		int level = srs.getInt("level")-1;
		
		ArrayList<String> data = new ArrayList<String>();
		
		double capacity = 0, fill = 0;
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='storage'");
		if(srs.next()){
			capacity = Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='storage_inc'");
		if(srs.next()){
			capacity += level*Double.parseDouble(srs.getString("value"));
		} else return "0";
		
		srs = db.getJdbc().queryForRowSet("select id,product,quality,size,offer from storage_product where storage='"+storageId+"'");
		ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
		while(srs.next()){
			products.add(new StorageProduct(srs.getString("id"), srs.getString("product"), srs.getInt("quality"), srs.getDouble("size"), srs.getBoolean("offer")));
			fill += srs.getDouble("size");
		}
		
		srs = db.getJdbc().queryForRowSet("select id,equipment,quality,durability,size,operational,offer from storage_equipment where storage='"+storageId+"'");
		ArrayList<StorageEquipment> equipments = new ArrayList<StorageEquipment>();
		while(srs.next()){
			equipments.add(new StorageEquipment(srs.getString("id"), srs.getString("equipment"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational"), srs.getBoolean("offer")));
			fill += srs.getDouble("size");
		}
		
		data.add(gson.toJson(capacity));
		data.add(gson.toJson(fill));
		data.add(gson.toJson(products));
		data.add(gson.toJson(equipments));
		
		val = gson.toJson(data);
		
		return val;
	}
	
	public String refreshClientData(HttpServletRequest req) {
		SqlRowSet srs = db.getJdbc().queryForRowSet("select * from user where name='"+req.getParameter("user")+"'");
		String val = "0";
		if(srs.next())
			val = gson.toJson(new User(srs.getString("name"), srs.getString("email"), srs.getString("dob"), srs.getString("about"), srs.getString("avatar"), srs.getDouble("money"), srs.getLong("rep"), srs.getString("zone"), new HashMap<String, String>()));
		return val;
	}
	
	public String checkUserSector(HttpServletRequest req) {
		String val = "0";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name from info_sector");
		ArrayList<String> sectors = new ArrayList<String>();
		if(srs.next()){
			do{
				sectors.add(srs.getString("name"));
			}while(srs.next());
		} else return val;
		
		srs = db.getJdbc().queryForRowSet("select sector from user_sector_license where user='"+req.getParameter("user")+"'");
		ArrayList<String> userSectors = new ArrayList<String>();
		while(srs.next()){
			userSectors.add(srs.getString("sector"));
		}
		
		srs = db.getJdbc().queryForRowSet("select value from info_values where name='sector'");
		ArrayList<String> price = new ArrayList<String>();
		if(srs.next()){
			price.add(srs.getString("value"));
		}
		
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		data.add(sectors);
		data.add(userSectors);
		data.add(price);
		
		val = gson.toJson(data);
		return val;
	}
	
	public String loadMarketContent(HttpServletRequest req) {
		String val = "0", id;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id from market where zone='"+req.getParameter("zone")+"'");
		if(srs.next())
			id = srs.getString("id");
		else return val;
		
		srs = db.getJdbc().queryForRowSet("select id,user,product,price,quality,size from market_product where market='"+id+"'");
		ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
		while(srs.next()){
			products.add(new MarketProduct(srs.getString("id"), srs.getString("user"), srs.getString("product"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("size")));
		}
		
		srs = db.getJdbc().queryForRowSet("select id,user,equipment,price,quality,durability,size,operational from market_equipment where market='"+id+"'");
		ArrayList<MarketEquipment> equipments = new ArrayList<MarketEquipment>();
		while(srs.next()){
			equipments.add(new MarketEquipment(srs.getString("id"), srs.getString("user"), srs.getString("equipment"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("durability"), srs.getDouble("size"), srs.getDouble("operational")));
		}
		
		srs = db.getJdbc().queryForRowSet("select id,employee,price,quality,operational from market_employee where market='"+id+"'");
		ArrayList<MarketEmployee> employees = new ArrayList<MarketEmployee>();
		while(srs.next()){
			employees.add(new MarketEmployee(srs.getString("id"), srs.getString("employee"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("operational")));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(products));
		data.add(gson.toJson(equipments));
		data.add(gson.toJson(employees));
		
		val = gson.toJson(data);
		return val;
	}
	
	public String getSuggestedPrice(HttpServletRequest req) {
		//Suggested price still based on info_product
		//Lately, it must use a DSS based, like AHP, or any easier method..
		String val = "0";
		double price = 0;
		
		//Deciding price starts here :
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select base_price from info_product where name=(select product from storage_product where id='"+req.getParameter("productId")+"') and quality=(select quality from storage_product where id='"+req.getParameter("productId")+"')"),
				srs1;
		if(srs.next()) 
			price = srs.getDouble("base_price");
		else return val;
		
		//Deciding price ends here.
		
		srs = db.getJdbc().queryForRowSet("select market from user_market_license where user='"+req.getParameter("user")+"'");
		ArrayList<String> marketZone = new ArrayList<String>();
		while(srs.next()){
			srs1 = db.getJdbc().queryForRowSet("select zone from market where id='"+srs.getString("market")+"'");
			if(srs1.next())
				marketZone.add(srs1.getString("zone"));
		}
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(gson.toJson(price));
		data.add(gson.toJson(marketZone));
		
		val = gson.toJson(data);
		System.out.println(val);
		
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
				userAcc = new User(srs1.getString("name"), srs1.getString("email"), srs1.getString("dob"), srs1.getString("about"), srs1.getString("avatar"), srs1.getDouble("money"), srs1.getLong("rep"), srs1.getString("zone"), storages);
				val = gson.toJson(userAcc);
				System.out.println(srs1.getDouble("money"));
			} else val = "0";
		} else val = "1";
		return val;
	}

	public String registerUser(HttpServletRequest req) {
		String val = "Ok";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select name from user where name='"+req.getParameter("user")+"'");
		if(srs.next())
			return "0";
		db.getJdbc().execute("insert into user values ('"+req.getParameter("user")+"','"+req.getParameter("pass")+"','"+req.getParameter("email")+"','"+req.getParameter("dob")+"','This is me','','0.00','0','"+req.getParameter("zone")+"')");
		return val;
	}

	public String getProposeInfo(HttpServletRequest req) {
		String val = "0";
		ArrayList<IndustrialEquipmentInfo> ie = new ArrayList<IndustrialEquipmentInfo>();
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select equipment_type, items from info_sector_equipment where name='"+req.getParameter("sector")+"'"),
				srs2;
		if(srs1.next()){
			do{
				srs2 = db.getJdbc().queryForRowSet("select base_price, base_op_cost from info_equipment where name='"+srs1.getString("equipment_type")+"' and quality='2'");
				srs2.next();
				ie.add(new IndustrialEquipmentInfo(srs1.getString("equipment_type"), srs1.getInt("items"), srs2.getDouble("base_price"), srs2.getDouble("base_op_cost")));
			}while(srs1.next());
		} else return val;
		
		ArrayList<EmployeeInfo> e = new ArrayList<EmployeeInfo>();
		srs1 = db.getJdbc().queryForRowSet("select employee_type, items from info_sector_employee where name='"+req.getParameter("sector")+"'");
		if(srs1.next()){
			do{
				srs2 = db.getJdbc().queryForRowSet("select base_price, base_op_cost from info_employee where name='"+srs1.getString("employee_type")+"' and quality='2'");
				srs2.next();
				e.add(new EmployeeInfo(srs1.getString("employee_type"), srs1.getInt("items"), srs2.getDouble("base_price"), srs2.getDouble("base_op_cost")));
			}while(srs1.next());
		} else return val;
		
		ArrayList<InputInfo> i = new ArrayList<InputInfo>();
		srs1 = db.getJdbc().queryForRowSet("select input_type, size from info_sector_input where name='"+req.getParameter("sector")+"'");
		if(srs1.next()){
			do{
				srs2 = db.getJdbc().queryForRowSet("select base_price from info_product where name='"+srs1.getString("input_type")+"' and quality='2'");
				srs2.next();
				i.add(new InputInfo(srs1.getString("input_type"), srs1.getDouble("size"), srs2.getDouble("base_price")));
			}while(srs1.next());
		} else return val;
		
		ArrayList<OutputInfo> o = new ArrayList<OutputInfo>();
		srs1 = db.getJdbc().queryForRowSet("select output_type, size from info_sector_output where name='"+req.getParameter("sector")+"'");
		if(srs1.next()){
			do{
				srs2 = db.getJdbc().queryForRowSet("select base_price from info_product where name='"+srs1.getString("output_type")+"' and quality='2'");
				srs2.next();
				o.add(new OutputInfo(srs1.getString("output_type"), srs1.getDouble("size"), srs2.getDouble("base_price")));
			}while(srs1.next());
		} else return val;
		
		BusinessSectorInfo bsi = new BusinessSectorInfo(ie, e, i, o);
		val = gson.toJson(bsi);
		return val;
	}

	public String submitProposal(HttpServletRequest req) {
		String val = "0", tmp = "";
		SqlRowSet srs = db.getJdbc().queryForRowSet("select value from info_values where name='turn'");
		int counter = 0, tmpC;
		String inc = "";
		String date;
		
		if(srs.next())
			val = srs.getString("value");
		else return val;
		
		srs = db.getJdbc().queryForRowSet("select prob from info_sector where name='"+req.getParameter("sector")+"'");
		if(srs.next()){
			tmp = val;
			val = srs.getString("prob");
		} else return val;
		
		date = dateNow("ddMMyy");
		srs = db.getJdbc().queryForRowSet("select substr(id,9,4) from req_borrow_bank where substr(id,3,6)='"+date+"'");
		
		if(srs.next()){
			do{
				tmpC = Integer.parseInt(srs.getString(1));
				if(counter < tmpC)
					counter = tmpC;
			}while(srs.next());
		} else {
			counter = 0;
		}
		counter++;
		
		if(counter > 999)
			inc = ""+counter;
		else if(counter > 99)
			inc = "0"+counter;
		else if(counter > 9)
			inc = "00"+counter;
		else inc = "000"+counter;
		
		db.getJdbc().execute("insert into req_borrow_bank values ('"+KEY_REQUEST_BORROW_BANK+date+inc+"','"+req.getParameter("user")+"','"+tmp+"','"+req.getParameter("money")+"','"+val+"')");
		val = "Ok";
		
		return val;
	}
	
	public String buildUserStorage(HttpServletRequest req) {
		String val = "No", date = dateNow("ddMMyy"), inc = "";
		int counter;
		SqlRowSet srs = db.getJdbc().queryForRowSet("select count(id) from storage where substr(id,3,6)='"+date+"'");
		srs.next();
		counter = srs.getInt(1)+1;
		if(counter > 999)
			inc = ""+counter;
		else if(counter > 99)
			inc = "0"+counter;
		else if(counter > 9)
			inc = "00"+counter;
		else inc = "000"+counter;
		db.getJdbc().execute("insert into storage values ('"+KEY_STORAGE+date+inc+"','"+req.getParameter("user")+"','"+req.getParameter("zone")+"','1')");
		val = KEY_STORAGE+date+inc;
		return val;
	}
	
	public String buyMarketProduct(HttpServletRequest req) {
		String val = "0", seller, storageId, storageIdSeller, marketId, product, date = dateNow("ddMMyy"), inc = "";
		double userMoney, sellerMoney, price, storage = 0, picked, size;
		int quality, counter;
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select money from user where name='"+req.getParameter("user")+"'");
		if(srs.next())
			userMoney = srs.getDouble("money");
		else return "0";
		
		//0 = internal error
		//1 = produk dah abis
		//2 = produk kurang
		//3 = uang ga cukup
		//4 = storage ga ada
		//5 = storage ga cukup
		
		srs = db.getJdbc().queryForRowSet("select market,storage,user,product,quality,price,size from market_product where id='"+req.getParameter("productId")+"'");
		if(srs.next()){
			price = srs.getDouble("price");
			size = srs.getDouble("size");
			product = srs.getString("product");
			quality = srs.getInt("quality");
			marketId = srs.getString("market");
			storageIdSeller = srs.getString("storage");
			seller = srs.getString("user");
		}
		else return "1";
		
		srs = db.getJdbc().queryForRowSet("select money from user where name='"+seller+"'");
		if(srs.next())
			sellerMoney = srs.getDouble("money");
		else {
			sellerMoney = 0;
			seller = "";
		}
		
		picked = Double.parseDouble(req.getParameter("picked"));
		System.out.println(picked);
		price *= picked;
		
//		price = new BigDecimal(Double.valueOf(price)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		
		if(size < picked)
			return "2";
		
		size -= picked;
		
		if(userMoney < price)
			return "3";
		
		userMoney -= price;
		sellerMoney += price;
		
		srs = db.getJdbc().queryForRowSet("select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"'");
		if(srs.next())
			storageId = srs.getString("id");
		else return "4";
		
		srs = db.getJdbc().queryForRowSet("select size from storage_product where storage='"+storageId+"'");
		while(srs.next()){
			storage += srs.getDouble("size");
		}
		
		srs = db.getJdbc().queryForRowSet("select size from storage_equipment where storage='"+storageId+"'");
		while(srs.next()){
			storage += srs.getDouble("size");
		}
		
		if(storage < picked)
			return "5";
		
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where product='"+product+"' and quality='"+quality+"' and storage='"+storageId+"' and offer='0'");
		if(srs.next()){
			db.getJdbc().execute("update storage_product set size='"+(srs.getDouble("size")+picked)+"' where id='"+srs.getString("id")+"'");
			val = "Ok";
		} else {
			val = "No";
		}
		
		if(val.equals("No")){
			srs = db.getJdbc().queryForRowSet("select count(id) from storage_product where substr(id,3,6)='"+date+"'");
			srs.next();
			counter = srs.getInt(1)+1;
			if(counter > 999)
				inc = ""+counter;
			else if(counter > 99)
				inc = "0"+counter;
			else if(counter > 9)
				inc = "00"+counter;
			else inc = "000"+counter;
			db.getJdbc().execute("insert into storage_product values ('"+KEY_PRODUCT+date+inc+"','"+storageId+"','"+product+"','"+quality+"','"+picked+"','0')");
			val = "Ok";
		}
		
		if(size > 0) db.getJdbc().execute("update market_product set size='"+size+"' where id='"+req.getParameter("productId")+"'");
		else db.getJdbc().execute("delete from market_product where id='"+req.getParameter("productId")+"'");
		
		db.getJdbc().execute("update user set money='"+userMoney+"' where name='"+req.getParameter("user")+"'");
		if(!seller.equals("")){
			db.getJdbc().execute("update user set money='"+sellerMoney+"' where name='"+seller+"'");
			db.getJdbc().execute("update storage_product set size='"+size+"' where storage='"+storageIdSeller+"' and product='"+product+"' and quality='"+quality+"' and offer='1'");
		}
		
		srs = db.getJdbc().queryForRowSet("select id,user,product,price,quality,size from market_product where market='"+marketId+"'");
		ArrayList<MarketProduct> products = new ArrayList<MarketProduct>();
		while(srs.next()){
			products.add(new MarketProduct(srs.getString("id"), srs.getString("user"), srs.getString("product"), srs.getDouble("price"), srs.getInt("quality"), srs.getDouble("size")));
		}
		
		val = gson.toJson(products);
		
		return val;
	}
	
	public String sellStorageProduct(HttpServletRequest req) {
		String val="0", date = dateNow("ddMMyy"), inc="", product;
		int counter, quality;
		double remain;
		
		SqlRowSet srs = db.getJdbc().queryForRowSet("select product,quality,size from storage_product where id='"+req.getParameter("productId")+"'");
		if(srs.next()){
			product = srs.getString("product");
			quality = srs.getInt("quality");
			remain = Double.parseDouble(req.getParameter("offer"));
			remain = srs.getDouble("size") - remain;
			if(remain > 0)
				db.getJdbc().execute("update storage_product set size='"+remain+"' where id='"+req.getParameter("productId")+"'");
			else db.getJdbc().execute("delete from storage_product where id='"+req.getParameter("productId")+"'");
		} else return "0";
				
				
		srs = db.getJdbc().queryForRowSet("select id,size from market_product where market=(select id from market where zone='"+req.getParameter("marketZone")+"') and storage=(select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"') and user='"+req.getParameter("user")+"' and product='"+product+"' and price='"+req.getParameter("price")+"' and quality='"+quality+"'");
		if(srs.next())
			db.getJdbc().execute("update market_product set size='"+(Double.parseDouble(req.getParameter("offer"))+srs.getDouble("size"))+"' where id='"+srs.getString("id")+"'");
		else val = "No";
		
		if(val.equals("No")){
			srs = db.getJdbc().queryForRowSet("select count(id) from market_product where substr(id,3,6)='"+date+"'");
			srs.next();
			counter = srs.getInt(1)+1;
			if(counter > 999)
				inc = ""+counter;
			else if(counter > 99)
				inc = "0"+counter;
			else if(counter > 9)
				inc = "00"+counter;
			else inc = "000"+counter;
			db.getJdbc().execute("insert into market_product values ('"+KEY_PRODUCT+date+inc+"',(select id from market where zone='"+req.getParameter("marketZone")+"'),(select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"'),'"+req.getParameter("user")+"','"+product+"','"+req.getParameter("price")+"','"+quality+"','"+req.getParameter("offer")+"')");
		}
		
		srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage=(select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"') and product=(select product from storage_product where id='"+req.getParameter("productId")+"') and quality=(select quality from storage_product where id='"+req.getParameter("productId")+"') and offer='1'");
		if(srs.next())
			db.getJdbc().execute("update storage_product set size='"+(Double.parseDouble(req.getParameter("offer"))+srs.getDouble("size"))+"' where id='"+srs.getString("id")+"'");
		else val="No";		
		
		if(val.equals("No")){
			srs = db.getJdbc().queryForRowSet("select count(id) from storage_product where substr(id,3,6)='"+date+"'");
			srs.next();
			counter = srs.getInt(1)+1;
			if(counter > 999)
				inc = ""+counter;
			else if(counter > 99)
				inc = "0"+counter;
			else if(counter > 9)
				inc = "00"+counter;
			else inc = "000"+counter;
			db.getJdbc().execute("insert into storage_product values ('"+KEY_PRODUCT+date+inc+"',(select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"'),'"+product+"','"+quality+"','"+req.getParameter("offer")+"','1')");
		}
		
		srs = db.getJdbc().queryForRowSet("select id,product,quality,size,offer from storage_product where storage=(select id from storage where user='"+req.getParameter("user")+"' and zone='"+req.getParameter("zone")+"')");
		ArrayList<StorageProduct> products = new ArrayList<StorageProduct>();
		while(srs.next()){
			products.add(new StorageProduct(srs.getString("id"), srs.getString("product"), srs.getInt("quality"), srs.getDouble("size"), srs.getBoolean("offer")));
		}
		
		val=gson.toJson(products);
		
		return val;
	}
	
	/* Post function ends here : */
	
	/* Library : */
	
	private String dateNow(String format){
		Calendar tglSkrg = Calendar.getInstance();
		SimpleDateFormat formatTglCari = new SimpleDateFormat(format);
		return formatTglCari.format(tglSkrg.getTime());
	}
}
