package com.ardhi.businessgame.services;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class EngineCall implements Callable<Long> {
	public static final int REQ_TIME = -4, 
			SYNC = -3,
			WORK = -2,
			IDLE = -1,
			BANK = 0, 
			SECTOR_NON_POWER_PLANT = 1,
			SECTOR_POWER_PLANT = 2,
			MARKET_SHARE = 3,
			CONTRACT = 4,
			
			
			TIME_TOTAL = 2,
			TIME_WORK = 1,
			
			BORROW_BANK_REQ_TIME = 540;
	
	public static boolean working = false;
	private int base;
	private DBAccess db;
	
	public EngineCall(int c){
		base = c;
		db = DBAccess.getReadyInstance();
	}
	
	@Override
	public Long call(){
		long time = 0;
		
		switch (base) {
			case REQ_TIME:
				if(working){
					time = System.currentTimeMillis() % TimeUnit.MINUTES.toMillis(TIME_TOTAL);
					time = TimeUnit.MINUTES.toMillis(TIME_TOTAL) - time;
				} else {
					time = -1;
				}
				break;
				
			case SYNC :
				/* Wait until x minutes to sync time with game proposal. */
				time = System.currentTimeMillis() % TimeUnit.MINUTES.toMillis(TIME_TOTAL);
				if(time == 0){
					System.out.println("Keluar");
					break;
				} else {
					time = TimeUnit.MINUTES.toMillis(TIME_TOTAL) - time;
					System.out.println("Synch-ed with system : Wait for "+TimeUnit.MILLISECONDS.toSeconds(time)+" second(s)");
				}
				break;
				
			case WORK :
				time = TimeUnit.MINUTES.toMillis(TIME_WORK)/4;
				System.out.println("All engine should work in the allocated time : "+TIME_WORK+" minutes");
				System.out.println("Awaiting all engine to finish their work..");
				break;

			case IDLE :
				time = TimeUnit.MINUTES.toMillis(TIME_TOTAL - TIME_WORK);
				System.out.println("All engine take a rest for now.");
				System.out.println("Player should interact with their data in aproximately : "+TimeUnit.MILLISECONDS.toMinutes(time)+" minutes");
				break;
				
			case BANK :
//				bankModule();
				break;
				
			case SECTOR_NON_POWER_PLANT :
//				sectorNonPowerPlantModule();				
				break;
				
			case SECTOR_POWER_PLANT :
//				startingRawMaterialFromDemigod();
//				
//				giveRawProductToPPPDemigod();
//				
//				sectorPowerPlantModule();
//				
//				deleteRawProductDemigod();
				break;
				
			case MARKET_SHARE :
//				marketShareModule();
				break;
				
			case CONTRACT :
//				contractModule();
				break;
			
				
			default:
				break;
		}
		return time;
	}

	private String dateNow(String format){
		Calendar tglSkrg = Calendar.getInstance();
		SimpleDateFormat formatTglCari = new SimpleDateFormat(format);
		return formatTglCari.format(tglSkrg.getTime());
	}
	
	private void bankModule(){
		ArrayList<String> sqlL = new ArrayList<String>();		
		Random r = new Random();
		String idInc = "",sqls[];
		double storageCost,interest,buildCost,total,d = 0;
		int res = 0;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select [user],money,sector,prob,cost,raw_turn,storage,req_borrow_bank.[zone] from req_borrow_bank,info_sector,businessgame.dbo.[user] where sector=info_sector.name and businessgame.dbo.[user].name=[user]"),
				srs2 = db.getJdbc().queryForRowSet("select [value] from info_values where name='interest' union select [value] from info_values where name='cost_storage'");
//				srs2 = db.getJdbc().queryForRowSet("select value from info_values where name='cost_storage'");
		
		srs2.next();
		interest = Double.parseDouble(srs2.getString("value"));
		
		srs2.next();
		storageCost = Double.parseDouble(srs2.getString("value"));
		
		System.out.println("Tes");
		
		while(srs1.next()){
			d = srs1.getDouble("prob")*100;
			res = r.nextInt(100);
			total = 0;
			if(res<d){
//			if(true){
				System.out.println("Tanda 1");
				srs2 = db.getJdbc().queryForRowSet("select cost from info_zone where id='"+srs1.getString("zone")+"'");
				srs2.next();
				buildCost = srs2.getDouble("cost")+srs1.getDouble("cost");
				
				if(srs1.getBoolean("storage"))
					total += storageCost;
				
				total += buildCost;
				srs2 = db.getJdbc().queryForRowSet("select items,base_price,base_operational from info_sector_equipment,info_equipment where equipment_type=info_equipment.name and info_sector_equipment.sector='"+srs1.getString("sector")+"'");
				while(srs2.next()){
					total += srs2.getDouble("base_price")*srs2.getInt("items");
					total += srs2.getDouble("base_operational")*srs2.getInt("items")*srs1.getInt("raw_turn");
				}
				
				System.out.println("Tanda 2");
				
				srs2 = db.getJdbc().queryForRowSet("select items,base_price,base_operational from info_sector_employee,info_employee where employee_type=info_employee.name and info_sector_employee.sector='"+srs1.getString("sector")+"'");
				while(srs2.next()){
					total += srs2.getDouble("base_price")*srs2.getInt("items");
					total += srs2.getDouble("base_operational")*srs2.getInt("items")*srs1.getInt("raw_turn");
				}
				
				System.out.println("Tanda 3");
				
				srs2 = db.getJdbc().queryForRowSet("select base_price,size from info_sector_input,info_product where input_type=info_product.name and info_sector_input.sector='"+srs1.getString("sector")+"'");
				while(srs2.next()){
					total += srs2.getDouble("base_price")*srs2.getDouble("size")*srs1.getInt("raw_turn");
				}
				
				sqlL.add("update businessgame.dbo.[user] set money='"+(total+srs1.getDouble("money"))+"' where name='"+srs1.getString("user")+"'");
				idInc = getUniqueIncrementIdNew("borrow_bank");
				sqlL.add("insert into borrow_bank values ('"+BusinessGameService.KEY_BORROW_BANK+idInc+"','"+srs1.getString("user")+"','0','"+total+"')");
				
				System.out.println("Tanda 4");
			}
		}
		sqlL.add("delete from req_borrow_bank");
		
		srs1 = db.getJdbc().queryForRowSet("select id,borrow_bank.[user],turn,money,borrow from borrow_bank,businessgame.dbo.[user] where businessgame.dbo.[user].name=borrow_bank.[user]");
		while(srs1.next()){
			if(srs1.getInt("turn") < BORROW_BANK_REQ_TIME){
				sqlL.add("update borrow_bank set turn='"+(srs1.getInt("turn")+1)+"' where id='"+srs1.getString("id")+"'");
				
//				srs2 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+srs1.getString("user")+"' and type='Interest'");
//				if(srs2.next()){
//					db.getJdbc().execute("update user_finance set total='"+(((srs2.getDouble("total")*-1)+(srs1.getDouble("money")*interest/BORROW_BANK_REQ_TIME))*-1)+"' where id='"+srs2.getString("id")+"'");
//				} else {
//					idInc = getUniqueIncrementIdNew("user_finance");
//					db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+srs1.getString("user")+"','Interest','"+((srs1.getDouble("money")*interest/BORROW_BANK_REQ_TIME)*-1)+"')");
//				}
				accountingFinance(srs1.getString("user"), "Interest", srs1.getDouble("money")*interest/BORROW_BANK_REQ_TIME, false);
				sqlL.add("update businessgame.dbo.[user] set money='"+(srs1.getDouble("money")-((srs1.getDouble("money")*interest/BORROW_BANK_REQ_TIME)))+"' where name='"+srs1.getString("user")+"'");
			} else {
				sqlL.add("delete from borrow_bank where id='"+srs1.getString("id")+"'");
				sqlL.add("update businessgame.dbo.[user] set money='"+(srs1.getDouble("money")-srs1.getDouble("borrow"))+"' where name='"+srs1.getString("user")+"'");
			}
		}
		
		srs1 = db.getJdbc().queryForRowSet("select [value] from info_values where name='turn'");
		if(srs1.next())
			sqlL.add("update info_values set [value]='"+(Integer.parseInt(srs1.getString("value"))+1)+"' where name='turn'");
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		sqls = null;
		r = null;
		srs1 = null;
		srs2 = null;
	}
	
	@SuppressWarnings("unchecked")
	private void sectorNonPowerPlantModule(){
		ArrayList<Object> data = calculateInstallmentNonPowerPlant();
		ArrayList<String> sectors = (ArrayList<String>) data.get(0),
				users = (ArrayList<String>) data.get(1),
				zones = (ArrayList<String>) data.get(2);
		ArrayList<Double> efficiency = (ArrayList<Double>) data.get(3),
				qualityCalc = (ArrayList<Double>) data.get(5),
				totalOperation = (ArrayList<Double>) data.get(6),
				totalWage = (ArrayList<Double>) data.get(7),
				totalDepreciation = (ArrayList<Double>) data.get(8),
				fixed = (ArrayList<Double>) data.get(9);
		ArrayList<Integer> effectivity = (ArrayList<Integer>) data.get(4);
		HashMap<String, ArrayList<String>> installmentSqlL = (HashMap<String, ArrayList<String>>)data.get(10);
		HashMap<String, Double> inputMax = new HashMap<String, Double>(),
				input = new HashMap<String, Double>(),
				inputAvgPrice = new HashMap<String, Double>(),
				outputRatio = new HashMap<String, Double>();
		HashMap<String, Integer> inputQuality = new HashMap<String, Integer>();
		HashMap<String, String> inputId = new HashMap<String, String>();
		
		double tmpd1,tmpd2,tmpd3,tmpd4;
		int tmpi;
		SqlRowSet srs1,srs2;
		String tmps1,tmps2,idInc,sqls[];
		
		System.out.println("Mulai Non Power Plant");
		
		for(int i=0;i<sectors.size();i++){
			inputMax.clear();
			input.clear();
			outputRatio.clear();
			inputQuality.clear();
			inputId.clear();
			
			tmpd1 = qualityCalc.get(i);
			tmpd2 = 0;
			tmpd3 = 0;
			tmpi = 0;
			tmps1 = "";
			tmps2 = "";
			
			srs1 = db.getJdbc().queryForRowSet("select input_type,size from info_sector_input where sector=(select type from installment where id='"+sectors.get(i)+"')");
			while(srs1.next()){
				inputMax.put(srs1.getString("input_type"), srs1.getDouble("size")*effectivity.get(i));
			}
			
			srs1 = db.getJdbc().queryForRowSet("select output_type,size from info_sector_output where sector=(select type from installment where id='"+sectors.get(i)+"')");
			while(srs1.next()){
				outputRatio.put(srs1.getString("output_type"), srs1.getDouble("size"));
			}
			
			srs1 = db.getJdbc().queryForRowSet("select storage_product.id,storage,product,quality,size,avg_price from storage_product,desc_product where storage=(select id from storage where [user]='"+users.get(i)+"' and [zone]='"+zones.get(i)+"') and storage_product.[desc]=desc_product.id");
			while(srs1.next()){
				tmps1 = srs1.getString("storage");
				tmpd2 = srs1.getDouble("size");
				srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs1.getString("id")+"'");
				while(srs2.next()){
					tmpd2 -= srs2.getDouble("size");
				}
				if(tmpd2 > 0){
					if(inputMax.containsKey(srs1.getString("product"))){
						if(input.containsKey(srs1.getString("product"))){
							if(inputQuality.get(srs1.getString("product")) < srs1.getDouble("quality")){
								input.remove(srs1.getString("product"));
								inputQuality.remove(srs1.getString("product"));
								inputId.remove(srs1.getString("product"));
								inputAvgPrice.remove(srs1.getString("product"));
								input.put(srs1.getString("product"), srs1.getDouble("size"));
								inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
								inputId.put(srs1.getString("product"), srs1.getString("id"));
								inputAvgPrice.put(srs1.getString("product"), srs1.getDouble("avg_price"));
							}
						} else {
							input.put(srs1.getString("product"), srs1.getDouble("size"));
							inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
							inputId.put(srs1.getString("product"), srs1.getString("id"));
							inputAvgPrice.put(srs1.getString("product"), srs1.getDouble("avg_price"));
						}
					}
				} else break;
			}
			
			tmpd2 = 0;
			tmpd3 = 0;
			tmpd4 = 0;
			
			srs1 = db.getJdbc().queryForRowSet("select actual_supply,supply from installment where id='"+sectors.get(i)+"'");
			srs1.next();
			if(srs1.getDouble("actual_supply") > 0){
				input.put("Energy", srs1.getDouble("actual_supply"));
				inputQuality.put("Energy", 0);
				inputId.put("Energy", "");
				db.getJdbc().execute("update installment set actual_supply='0' where id='"+sectors.get(i)+"'");
				
				srs2 = db.getJdbc().queryForRowSet("select [user],subscription,tariff from installment where id='"+srs1.getString("supply")+"'");
				if(srs2.next()){
					tmps2 = srs2.getString("user");
					tmpd2 = srs2.getDouble("subscription");
					tmpd4 = srs2.getDouble("tariff");
				}
				
				accountingFinance(users.get(i), "Electricity", tmpd2, false);
				
				srs2 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+users.get(i)+"'");
				srs2.next();
				db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(srs2.getDouble("money")-tmpd2)+"' where name='"+users.get(i)+"'");
				
				accountingFinance(tmps2, "Electricity", tmpd2, true);
				
				srs2 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+tmps2+"'");
				srs2.next();
				db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(srs2.getDouble("money")+tmpd2)+"' where name='"+tmps2+"'");
			}
			
			System.out.println("Storage : ");
			for(String in : input.keySet()){
				System.out.println(in+" : "+input.get(in));
			}
			
			System.out.println("Requirement : ");
			for(String in : inputMax.keySet()){
				System.out.println(in+" : "+inputMax.get(in));
			}
			
			System.out.println("Quality score sementara : "+tmpd1);
			
			tmpd2 = 0;
			
			accountingFinance(users.get(i), "Fixed", fixed.get(i), false);
			
			if(inputMax.size() == input.size()){
				//calculate quality :
				tmpd3 = efficiency.get(i);
				System.out.println("eff produk awal : "+tmpd3);
				for(String in : input.keySet()){
					if(!in.equals("Energy")){
						switch (inputQuality.get(in)) {
							case 1:
								tmpd2 += (20/(input.size()-1));
								break;
							case 2:
								tmpd2 += (30/(input.size()-1));
								break;
							case 3:
								tmpd2 += (50/(input.size()-1));
								break;
							default :
								tmpd2 += 0;
								break;
						}
					}
					if(tmpd3 > (input.get(in)/inputMax.get(in))){
						tmpd3 = input.get(in)/inputMax.get(in);
						System.out.println(in+" "+input.get(in)+" "+inputMax.get(in));
						System.out.println("Eff akhir"+tmpd3);
					}
				}
				tmpd1 += tmpd2*45/100;
				qualityCalc.set(i, tmpd1);
				
				for(String in : input.keySet()){
					if(!in.equals("Energy")){
						tmpd2 = (input.get(in) - (inputMax.get(in)*tmpd3));
						if(tmpd2 > 0)
							db.getJdbc().execute("update storage_product set size='"+tmpd2+"' where id='"+inputId.get(in)+"'");
						else db.getJdbc().execute("delete from storage_product where id='"+inputId.get(in)+"'");
						
						tmpd2 = inputMax.get(in)*tmpd3;
						
						accountingFinance(users.get(i), "Raw Material", (tmpd2*inputAvgPrice.get(in)), false);
					} else {
						accountingFinance(users.get(i), "Electricity", (tmpd2*tmpd4), false);
						srs2 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+users.get(i)+"'");
						srs2.next();
						db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(srs2.getDouble("money")-(tmpd2*tmpd4))+"' where name='"+users.get(i)+"'");
						
						accountingFinance(tmps2, "Electricity", (tmpd2*tmpd4), true);
						srs2 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+tmps2+"'");
						srs2.next();
						db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(srs2.getDouble("money")+(tmpd2*tmpd4))+"' where name='"+tmps2+"'");
					}
				}
				
				if(tmpd1 < 30)
					tmpi = 1;
				else if(tmpd1 < 40 && tmpd1 > 29)
					tmpi = 2;
				else if(tmpd1 > 39)
					tmpi = 3;
				
				for(String ou : outputRatio.keySet()){
					srs1 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]=(select id from desc_product where product='"+ou+"' and quality='"+tmpi+"') and storage='"+tmps1+"'");
					if(srs1.next()){
						db.getJdbc().execute("update storage_product set size='"+(srs1.getDouble("size")+(outputRatio.get(ou)*tmpd3))+"' where id='"+srs1.getString("id")+"'");
					} else {
						idInc = getUniqueIncrementIdNew("storage_product");
						srs2 = db.getJdbc().queryForRowSet("select id,price from desc_product where product='"+ou+"' and quality='"+tmpi+"'");
						srs2.next();
						db.getJdbc().execute("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs2.getString("id")+"','"+tmps1+"','"+(outputRatio.get(ou)*tmpd3)+"','"+srs2.getDouble("price")+"')");
					}
					System.out.println(ou+" "+(outputRatio.get(ou)*tmpd3));
				}
				
//				srs1 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+users.get(i)+"' and type='Wage'");
//				if(srs1.next()){
//					db.getJdbc().execute("update user_finance set total='"+(((srs1.getDouble("total")*-1)+totalWage.get(i))*-1)+"' where id='"+srs1.getString("id")+"'");
//				} else {
//					idInc = getUniqueIncrementId("inc_user_finance");
//					db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+users.get(i)+"','Wage','"+(totalWage.get(i)*-1)+"')");
//				}
				accountingFinance(users.get(i), "Wage", totalWage.get(i), false);
				
//				srs1 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+users.get(i)+"' and type='Operation'");
//				if(srs1.next()){
//					db.getJdbc().execute("update user_finance set total='"+(((srs1.getDouble("total")*-1)+totalOperation.get(i))*-1)+"' where id='"+srs1.getString("id")+"'");
//				} else {
//					idInc = getUniqueIncrementId("inc_user_finance");
//					db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+users.get(i)+"','Operation','"+(totalOperation.get(i)*-1)+"')");
//				}
				accountingFinance(users.get(i), "Operation", totalOperation.get(i), false);
				
//				srs1 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+users.get(i)+"' and type='Depreciation'");
//				if(srs1.next()){
//					db.getJdbc().execute("update user_finance set total='"+(((srs1.getDouble("total")*-1)+totalDepreciation.get(i))*-1)+"' where id='"+srs1.getString("id")+"'");
//				} else {
//					idInc = getUniqueIncrementId("inc_user_finance");
//					db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+users.get(i)+"','Depreciation','"+(totalDepreciation.get(i)*-1)+"')");
//				}
				accountingFinance(users.get(i), "Depreciation", totalDepreciation.get(i), false);
				
				srs1 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+users.get(i)+"'");
				srs1.next();
				db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(srs1.getDouble("money")-totalOperation.get(i)-totalWage.get(i)-fixed.get(i))+"' where name='"+users.get(i)+"'");
				
				sqls = new String[installmentSqlL.get(sectors.get(i)).size()];
				installmentSqlL.get(sectors.get(i)).toArray(sqls);
				db.getJdbc().batchUpdate(sqls);
			}
		}
		
		System.out.println("Selesai Non Power Plant");
		
		data = null;
		sectors = null;
		users = null;
		zones = null;
		efficiency = null;
		qualityCalc = null;
		effectivity = null;
		
		inputMax = null;
		input = null;
		outputRatio = null;
		inputQuality = null;
		inputId = null;
		
		gc();
	}
	
	@SuppressWarnings("unchecked")
	private void sectorPowerPlantModule(){
		ArrayList<Object> data = calculateInstallmentPowerPlant();
		ArrayList<String> sectors = (ArrayList<String>) data.get(0),
				users = (ArrayList<String>) data.get(1),
				zones = (ArrayList<String>) data.get(2);
		ArrayList<Double> efficiency = (ArrayList<Double>) data.get(3),
				qualityCalc = (ArrayList<Double>) data.get(5),
				totalOperation = (ArrayList<Double>) data.get(6),
				totalWage = (ArrayList<Double>) data.get(7),
				totalDepreciation = (ArrayList<Double>) data.get(8),
				fixed = (ArrayList<Double>) data.get(9);
		ArrayList<Integer> effectivity = (ArrayList<Integer>) data.get(4);
		HashMap<String, ArrayList<String>> installmentSqlL = (HashMap<String, ArrayList<String>>)data.get(10);
		HashMap<String, Double> inputMax = new HashMap<String, Double>(),
				input = new HashMap<String, Double>(),
				inputAvgPrice = new HashMap<String, Double>(),
				outputRatio = new HashMap<String, Double>();
		HashMap<String, Integer> inputQuality = new HashMap<String, Integer>();
		HashMap<String, String> inputId = new HashMap<String, String>();
		
		double tmpd1,tmpd2,tmpd3;
		int tmpi;
		String tmps1,idInc,sqls[];
		SqlRowSet srs1,srs2;
		
		System.out.println("Mulai Power Plant");
		
		for(int i=0;i<sectors.size();i++){
			inputMax.clear();
			input.clear();
			outputRatio.clear();
			inputQuality.clear();
			inputId.clear();
			
			tmpd1 = qualityCalc.get(i);
			tmpd2 = 0;
			tmpd3 = 0;
			tmpi = 0;
			
			srs1 = db.getJdbc().queryForRowSet("select input_type,size from info_sector_input where sector=(select type from installment where id='"+sectors.get(i)+"') and input_type!='Energy'");
			while(srs1.next()){
				inputMax.put(srs1.getString("input_type"), srs1.getDouble("size")*effectivity.get(i));
			}
			
			srs1 = db.getJdbc().queryForRowSet("select output_type,size from info_sector_output where sector=(select type from installment where id='"+sectors.get(i)+"')");
			while(srs1.next()){
				outputRatio.put(srs1.getString("output_type"), srs1.getDouble("size"));
			}
			
			tmps1 = "";
			srs1 = db.getJdbc().queryForRowSet("select storage_product.id,storage,product,quality,size,avg_price from storage_product,desc_product where storage=(select id from storage where [user]='"+users.get(i)+"' and [zone]='"+zones.get(i)+"') and storage_product.[desc]=desc_product.id");
			while(srs1.next()){
				tmps1 = srs1.getString("storage");
				tmpd2 = srs1.getDouble("size");
				srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs1.getString("id")+"'");
				while(srs2.next()){
					tmpd2 -= srs2.getDouble("size");
				}
				if(tmpd2 > 0){
					if(inputMax.containsKey(srs1.getString("product"))){
						if(input.containsKey(srs1.getString("product"))){
							if(inputQuality.get(srs1.getString("product")) < srs1.getDouble("quality")){
								input.remove(srs1.getString("product"));
								inputQuality.remove(srs1.getString("product"));
								inputId.remove(srs1.getString("product"));
								inputAvgPrice.remove(srs1.getString("product"));
								input.put(srs1.getString("product"), srs1.getDouble("size"));
								inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
								inputId.put(srs1.getString("product"), srs1.getString("id"));
								inputAvgPrice.put(srs1.getString("product"), srs1.getDouble("avg_price"));
							}
						} else {
							input.put(srs1.getString("product"), srs1.getDouble("size"));
							inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
							inputId.put(srs1.getString("product"), srs1.getString("id"));
							inputAvgPrice.put(srs1.getString("product"), srs1.getDouble("avg_price"));
						}
					}
				} else break;
			}
			
			System.out.println("Storage : ");
			for(String in : input.keySet()){
				System.out.println(in+" : "+input.get(in));
			}
			
			System.out.println("Requirement : ");
			for(String in : inputMax.keySet()){
				System.out.println(in+" : "+inputMax.get(in));
			}
			
			tmpd2 = 0;
			
			accountingFinance(users.get(i), "Fixed", fixed.get(i), false);
			
			if(inputMax.size() == input.size()){
				//calculate quality :
				tmpd3 = efficiency.get(i);
				System.out.println("eff produk awal : "+tmpd3);
				for(String in : input.keySet()){
					switch (inputQuality.get(in)) {
					case 1:
						tmpd2 += (20/input.size());
						break;
					case 2:
						tmpd2 += (30/input.size());
						break;
					case 3:
						tmpd2 += (50/input.size());
						break;
					default :
						tmpd2 += 0;
						break;
					}
					if(tmpd3 > (input.get(in)/inputMax.get(in))){
						tmpd3 = input.get(in)/inputMax.get(in);
						System.out.println("Eff akhir "+tmpd3);
					}
				}
				tmpd1 += tmpd2*45/100;
				qualityCalc.set(i, tmpd1);
				
				for(String in : input.keySet()){
					tmpd2 = (input.get(in) - (inputMax.get(in)*tmpd3));
					System.out.println(in+" "+tmpd2);
					if(tmpd2 > 0)
						db.getJdbc().execute("update storage_product set size='"+tmpd2+"' where id='"+inputId.get(in)+"'");
					else db.getJdbc().execute("delete from storage_product where id='"+inputId.get(in)+"'");
					
					tmpd2 = inputMax.get(in)*tmpd3;
					
					accountingFinance(users.get(i), "Raw Material", (tmpd2*inputAvgPrice.get(in)), false);
				}
				
				if(tmpd1 < 30){
					tmpi = 1;
					tmpd2 = -0.2;
				}
				else if(tmpd1 < 40 && tmpd1 > 29){
					tmpi = 2;
					tmpd2 = 1;
				}
				else if(tmpd1 > 39){
					tmpi = 3;
					tmpd2 = 0.5;
				}
				
				for(String ou : outputRatio.keySet()){
					if(ou.equals("Energy")){
						tmpd2 = outputRatio.get(ou)+(outputRatio.get(ou)*tmpd2);
						System.out.println(ou+" 1 "+tmpd2);
						srs1 = db.getJdbc().queryForRowSet("select id,planned_supply from installment where supply='"+sectors.get(i)+"'");
						while(srs1.next()){
							if((tmpd2 - srs1.getDouble("planned_supply")) > 0){
								tmpd2 -= srs1.getDouble("planned_supply");
								db.getJdbc().execute("update installment set actual_supply='"+srs1.getDouble("planned_supply")+"' where id='"+srs1.getString("id")+"'");
							} else {
								db.getJdbc().execute("update installment set actual_supply='"+tmpd2+"' where id='"+srs1.getString("id")+"'");
								tmpd2 = 0;
							}
						}
						System.out.println(ou+" 2 "+tmpd2);
					} else {
						System.out.println(ou+" 1 "+(outputRatio.get(ou)*tmpd3));
						srs1 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]=(select id from desc_product where product='"+ou+"' and quality='"+tmpi+"') and storage='"+tmps1+"'");
						if(srs1.next()){
							db.getJdbc().execute("update storage_product set size='"+(srs1.getDouble("size")+(outputRatio.get(ou)*tmpd3))+"' where id='"+srs1.getString("id")+"'");
						} else {
							idInc = getUniqueIncrementIdNew("storage_product");
							srs2 = db.getJdbc().queryForRowSet("select id,price from desc_product where product='"+ou+"' and quality='"+tmpi+"'");
							srs2.next();
							db.getJdbc().execute("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs2.getString("id")+"','"+tmps1+"','"+(outputRatio.get(ou)*tmpd3)+"','"+srs2.getDouble("price")+"')");
						}
						System.out.println(ou+" 2 "+(outputRatio.get(ou)*tmpd3));
					}
				}
				
				accountingFinance(users.get(i), "Wage", totalWage.get(i), false);
				accountingFinance(users.get(i), "Operation", totalOperation.get(i), false);
				accountingFinance(users.get(i), "Depreciation", totalDepreciation.get(i), false);
				
				srs1 = db.getJdbc().queryForRowSet("select money from businessgame.dbo.[user] where name='"+users.get(i)+"'");
				srs1.next();
				db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(srs1.getDouble("money")-totalOperation.get(i)-totalWage.get(i)-fixed.get(i))+"' where name='"+users.get(i)+"'");
				
				sqls = new String[installmentSqlL.get(sectors.get(i)).size()];
				installmentSqlL.get(sectors.get(i)).toArray(sqls);
				db.getJdbc().batchUpdate(sqls);
			}
		}
		
		System.out.println("Selesai Power Plant");
		
		data = null;
		sectors = null;
		users = null;
		zones = null;
		efficiency = null;
		qualityCalc = null;
		effectivity = null;
		
		inputMax = null;
		input = null;
		outputRatio = null;
		inputQuality = null;
		inputId = null;
		
		srs1 = null;
		srs2 = null;
		
		gc();
	}
	
	private void marketShareModule(){
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select [zone],product,[value],transport_in,transport_out,retribution,transport from market_share,info_zone,info_product where [zone]=info_zone.id and name=product"),
				srs2;
		ArrayList<String> users = new ArrayList<String>(),
				products = new ArrayList<String>(),
				productUsers = new ArrayList<String>();
		ArrayList<Double> userMoneys = new ArrayList<Double>(), 
				userScores = new ArrayList<Double>(),
				productScores = new ArrayList<Double>(),
				productSizes = new ArrayList<Double>(),
				productPrices = new ArrayList<Double>(),
				basePrices = new ArrayList<Double>();
		ArrayList<Integer> baseQualities = new ArrayList<Integer>();
		double offset = 0.25,totalProduct,tmpd1,tmpd2,tmpd3,tmpd4,tmpd5,tmpd6,tmpd7;
		int tmpi;
		String tmps1,tmps2,idInc;

		while(srs1.next()){
			users.clear();
			userMoneys.clear();
			userScores.clear();
			products.clear();
			productUsers.clear();
			productScores.clear();
			productSizes.clear();
			productPrices.clear();
			basePrices.clear();
			baseQualities.clear();
			
			System.out.println("Zone "+srs1.getString("zone")+" : ");
			tmpd2 = 0;
			totalProduct = srs1.getDouble("value");
			
			srs2 = db.getJdbc().queryForRowSet("select quality,price from desc_product where product='"+srs1.getString("product")+"'");
			while(srs2.next()){
				baseQualities.add(srs2.getInt("quality"));
				basePrices.add(srs2.getDouble("price"));
			}
			
			System.out.println("Marker 0");
			
			srs2 = db.getJdbc().queryForRowSet("select [user],money,market_product.id,market_product.price,quality,market_product.size from market_product,storage_product,storage,desc_product,businessgame.dbo.[user] where market_product.[zone]='"+srs1.getString("zone")+"' and desc_product.product='"+srs1.getString("product")+"' and storage_product_id=storage_product.id and storage=storage.id and storage_product.[desc]=desc_product.id and [user]=name");
			while(srs2.next()){
				if(!users.contains(srs2.getString("user"))){
					users.add(srs2.getString("user"));
					userScores.add(9.0);
					userMoneys.add(srs2.getDouble("money"));
				}
				tmpd1 = srs2.getDouble("price")/basePrices.get(baseQualities.indexOf(srs2.getInt("quality")));
				tmpd1 = ((1+offset)-tmpd1)*100;
				products.add(srs2.getString("id"));
				productScores.add(tmpd1);
				productUsers.add(srs2.getString("user"));
				productSizes.add(srs2.getDouble("size"));
				productPrices.add(srs2.getDouble("price"));
			}
			
			System.out.println("Marker 1");
			
//			srs2 = db.getJdbc().queryForRowSet("select product_id,multiplier,count from product_advertisement,desc_advertisement,storage_product,desc_product where adv=desc_advertisement.id and product_id=storage_product.id and storage_product.desc=desc_product.id and zone='"+srs1.getString("zone")+"' and desc_product.product='"+srs1.getString("product")+"'");
			srs2 = db.getJdbc().queryForRowSet("select [user],multiplier from product_advertisement,desc_advertisement where product='"+srs1.getString("product")+"' and [zone]='"+srs1.getString("zone")+"' and adv=desc_advertisement.id");
			while(srs2.next()){
				if(users.contains(srs2.getString("user"))){
					tmpi = users.indexOf(srs2.getString("user"));
					tmpd1 = userScores.get(tmpi);
					tmpd1 += srs2.getInt("multiplier");
					userScores.set(tmpi, tmpd1);
				}
			}
			
			System.out.println("Marker 2");
			
			//ranking product
			for(int i=0;i<products.size();i++){
				for(int j=i+1;j<products.size();j++){
					if(productScores.get(i) < productScores.get(j)){
						tmps1 = products.get(i);
						tmps2 = productUsers.get(i);
						tmpd1 = productScores.get(i);
						tmpd2 = productSizes.get(i);
						tmpd3 = productPrices.get(i);
						products.set(i, products.get(j));
						productUsers.set(i, productUsers.get(j));
						productScores.set(i, productScores.get(j));
						productSizes.set(i, productSizes.get(j));
						productPrices.set(i, productPrices.get(j));
						products.set(j, tmps1);
						productUsers.set(j, tmps2);
						productScores.set(j, tmpd1);
						productSizes.set(j, tmpd2);
						productPrices.set(j, tmpd3);
					}
				}
			}
			
			for(int i=0;i<products.size();i++){
				System.out.println(products.get(i)+" : "+productScores.get(i)+" - "+productPrices.get(i)+" ZE - "+productSizes.get(i)+" CBM - "+productUsers.get(i));
			}
			
			tmpd2 = 0;
			
			for(int i=0;i<users.size();i++){
				tmpd2 += userScores.get(i);
			}
			
			System.out.println("Marker 3");
			
			//sebaran 100:
			tmpd2 = 100/tmpd2;
			System.out.println(tmpd2);
			for(int i=0;i<users.size();i++){
				tmpd1 = userScores.get(i)*tmpd2;
				tmpd3 = tmpd1*totalProduct/100;
				System.out.println(users.get(i)+" has Gasoline's share of "+new BigDecimal(Double.valueOf(tmpd1)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()+"%, or "+new BigDecimal(Double.valueOf(tmpd3)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()+" CBM");
				for(int j=0;j<products.size();j++){
					if(productUsers.get(j).equals(users.get(i))){
						if(tmpd3 > 0){
							System.out.println("Awal "+tmpd3);
							
							System.out.println("Tes 1");
							System.out.println("select storage_product_id,storage_product.size,storage.[zone] from market_product,storage_product,storage where market_product.id='"+products.get(j)+"' and storage_product_id=storage_product.id and storage=storage.id");
							srs2 = db.getJdbc().queryForRowSet("select storage_product_id,storage_product.size,storage.[zone] from market_product,storage_product,storage where market_product.id='"+products.get(j)+"' and storage_product_id=storage_product.id and storage=storage.id");
//							+"select storage_product_id,storage_product.size,zone from market_product,storage_product,storage where market_product.id='"+products.get(j)+"' and storage_product_id=storage_product.id and storage=storage.id");
							if(srs2.next()){
								tmps1 = srs2.getString("storage_product_id");
								tmpd6 = srs2.getDouble("size");
								tmps2 = srs2.getString("zone");
							} else return;
							
							if(productSizes.get(j) > tmpd3){
								tmpd4 = tmpd3;
								tmpd5 = tmpd3;
								db.getJdbc().execute("update market_product set size='"+(productSizes.get(j) - tmpd3)+"' where id='"+products.get(j)+"'");
								tmpd3 = 0;
								System.out.println("Habis");
							} else {
								tmpd4 = tmpd3 - productSizes.get(j);
								tmpd5 = productSizes.get(j);
								db.getJdbc().execute("delete from market_product where id='"+products.get(j)+"'");
								tmpd3 = tmpd4;
								System.out.println("Sisa "+tmpd3);
							}
							
							System.out.println("Tes 1 2");
							
							if(tmps2.equals(srs1.getString("zone")))
								tmpd7 = srs1.getDouble("transport_in")*srs1.getDouble("transport");
							else tmpd7 = srs1.getDouble("transport_out")*srs1.getDouble("transport");
							
							tmpd6 -= tmpd5;
							if(tmpd6 > 0)
								db.getJdbc().execute("update storage_product set size='"+tmpd6+"' where id='"+tmps1+"'");
							else db.getJdbc().execute("delete from storage_product where id='"+tmps1+"'");
							
							System.out.println("Tes 2");
							
//							srs2 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+users.get(i)+"' and type='Sales'");
//							if(srs2.next()){
//								db.getJdbc().execute("update user_finance set total='"+(srs2.getDouble("total")+(tmpd5*productPrices.get(j)))+"' where id='"+srs2.getString("id")+"'");
//							} else {
//								idInc = getUniqueIncrementIdNew("user_finance");
//								db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+users.get(i)+"','Sales','"+(tmpd5*productPrices.get(j))+"')");
//							}
							
							accountingFinance(users.get(i), "Sales", tmpd5*productPrices.get(j), true);
							
							System.out.println("Tes 3");
							
//							srs2 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+users.get(i)+"' and type='Transport'");
//							if(srs2.next()){
//								db.getJdbc().execute("update user_finance set total='"+(((srs2.getDouble("total")*-1)+(tmpd5*tmpd7))*-1)+"' where id='"+srs2.getString("id")+"'");
//							} else {
//								idInc = getUniqueIncrementIdNew("user_finance");
//								db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+users.get(i)+"','Transport','"+(tmpd5*tmpd7*-1)+"')");
//							}
							
							accountingFinance(users.get(i), "Transport", tmpd5*tmpd7, false);
							
							System.out.println("Tes 4");
							
//							srs2 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+users.get(i)+"' and type='Retribution'");
//							if(srs2.next()){
//								db.getJdbc().execute("update user_finance set total='"+(((srs2.getDouble("total")*-1)+(tmpd5*srs1.getDouble("retribution")))*-1)+"' where id='"+srs2.getString("id")+"'");
//							} else {
//								idInc = getUniqueIncrementIdNew("user_finance");
//								db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+users.get(i)+"','Retribution','"+(tmpd5*srs1.getDouble("retribution")*-1)+"')");
//							}
							
							accountingFinance(users.get(i), "Retribution", tmpd5*srs1.getDouble("retribution"), false);
							
							System.out.println("Tes 5");
							
							tmpd6 = (tmpd5*productPrices.get(j)) - (tmpd5*tmpd7) - (tmpd5*srs1.getDouble("retribution"));
							
							db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(userMoneys.get(i)+tmpd6)+"' where name='"+users.get(i)+"'");
							
							System.out.println("Tes 6");
						}
					}
				}
			}
		}
		
		srs1 = null;
		srs2 = null;
		users = null;
		products = null;
		productUsers = null;
		userMoneys = null; 
		userScores = null;
		productScores = null;
		productSizes = null;
		productPrices = null;
		basePrices = null;
		baseQualities = null;
		
		gc();
	}
	
	private void contractModule(){
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select request_storage,supplier_storage,product_desc,size,price,turn from user_contract where accept='1'"),
				srs2,srs3;
		String user,seller,idInc,userZone,sellerZone;
		double totalPrice,userMoney,sellerMoney,transport;
		long sellerRep;
		
		while(srs1.next()){
			System.out.println("Marker 1");
			srs2 = db.getJdbc().queryForRowSet("select [user],money,rep,storage.[zone],transport_in,transport_out from storage,businessgame.dbo.[user],info_zone where id='"+srs1.getString("request_storage")+"' and name=[user] and storage.[zone]=info_zone.id union select [user],money,rep,storage.[zone],transport_in,transport_out from storage,businessgame.dbo.[user],info_zone where id='"+srs1.getString("supplier_storage")+"' and name=[user] and storage.[zone]=info_zone.id");
			if(srs2.next()){
				user = srs2.getString("user");
				userMoney = srs2.getDouble("money");
				userZone = srs2.getString("zone");
			}
			else return;
			
			System.out.println("Marker 2");
			
			if(srs2.next()){
				seller = srs2.getString("user");
				sellerMoney = srs2.getDouble("money");
				sellerRep = srs2.getLong("rep");
				sellerZone = srs2.getString("zone");
				if(userZone.equals(sellerZone))
					transport = srs2.getDouble("transport_in");
				else transport = srs2.getDouble("transport_out");
			}
			else return;
			
			System.out.println("Marker 3");
			
			srs2 = db.getJdbc().queryForRowSet("select id,transport,size from storage_product,desc_product,info_product where storage_product.[desc]='"+srs1.getString("product_desc")+"' and storage='"+srs1.getString("supplier_storage")+"' and desc_product.id=storage_product.[desc] and name=desc_product.product");
			if(srs2.next()){				
				if(srs2.getDouble("size") >= srs1.getDouble("size")){
					System.out.println("Marker 4");
					
					totalPrice = srs1.getDouble("price")*srs1.getDouble("size");
					db.getJdbc().execute("update storage_product set size='"+(srs2.getDouble("size")-srs1.getDouble("size"))+"' where id='"+srs2.getString("id")+"'");
					
//					srs3 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+seller+"' and type='Sales'");
//					if(srs3.next()){
//						db.getJdbc().execute("update user_finance set total='"+(srs3.getDouble("total")+(totalPrice))+"' where id='"+srs2.getString("id")+"'");
//					} else {
//						idInc = getUniqueIncrementIdNew("user_finance");
//						db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+seller+"','Sales','"+(totalPrice)+"')");
//					}
					
					accountingFinance(seller, "Sales", totalPrice, true);
					
					System.out.println("Tes 3");
					
//					srs2 = db.getJdbc().queryForRowSet("select id,total from user_finance where user='"+seller+"' and type='Transport'");
//					if(srs2.next()){
//						db.getJdbc().execute("update user_finance set total='"+(((srs2.getDouble("total")*-1)+(srs1.getDouble("size")*transport*srs2.getDouble("transport")))*-1)+"' where id='"+srs2.getString("id")+"'");
//					} else {
//						idInc = getUniqueIncrementIdNew("user_finance");
//						db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+seller+"','Transport','"+(srs1.getDouble("size")*transport*srs2.getDouble("transport")*-1)+"')");
//					}
					
					accountingFinance(seller, "Transport", srs1.getDouble("size")*transport*srs2.getDouble("transport"), false);
					
					totalPrice -= srs1.getDouble("size")*transport*srs2.getDouble("transport");
					
					db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(sellerMoney+totalPrice)+"', rep='"+(sellerRep+1)+"' where name='"+seller+"'");
					
					srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='"+srs1.getString("product_desc")+"' and storage='"+srs1.getString("request_storage")+"'");
					System.out.println("Marker 5");
					if(srs3.next()){
						db.getJdbc().execute("update storage_product set size='"+(srs3.getDouble("size")+srs1.getDouble("size"))+"' where id='"+srs3.getString("id")+"'");
					} else {
						idInc = getUniqueIncrementIdNew("storage_product");
						db.getJdbc().execute("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs1.getString("product_desc")+"','"+srs1.getString("request_storage")+"','"+srs1.getString("size")+"')");
					}
					
//					srs3 = db.getJdbc().queryForRowSet("select total from user_finance where user='"+user+"' and type='Raw Material'");
//					if(srs3.next()){
//						db.getJdbc().execute("update user_finance set total='"+(((srs3.getDouble("total")*-1)+totalPrice)*-1)+"' where user='"+user+"' and type='Raw Material'");
//					} else {
//						idInc = getUniqueIncrementIdNew("user_finance");
//						db.getJdbc().execute("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+user+"','Raw Material','"+(-1*totalPrice)+"')");
//					}
					
					accountingFinance(user, "Raw Material", totalPrice, false);
					
					db.getJdbc().execute("update businessgame.dbo.[user] set money='"+(userMoney-totalPrice)+"' where name='"+user+"'");
					db.getJdbc().execute("update user_contract set turn='"+(srs1.getInt("turn")-1)+"' where id='"+srs1.getString("id")+"'");
					if(srs1.getInt("turn") < 1)
						db.getJdbc().execute("delete from user_contract where id='"+srs1.getString("id")+"'");
					System.out.println("Marker 6");
				} else {
					db.getJdbc().execute("update businessgame.dbo.[user] set rep='"+(sellerRep-5)+"' where name='"+seller+"'");
				}
			}
		}
		
		srs1 = null;
		srs2 = null;
		srs3 = null;
		
		gc();
	}
	
	private ArrayList<Object> calculateInstallmentPowerPlant(){
		String hiElement="";
		double hiVal = 0, tmpQuality = 0, tmpSum = 0, quality = 0, tmpTotalOperation, tmpTotalWage, tmpTotalDepreciation;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,[user],[zone],fixed from installment,info_sector where type='Petrol Power Plant' and type=name"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>(),
				elementsTmpQuality = new HashMap<String, Double>();
		HashMap<String, ArrayList<String>> tmpInstallmentSqlL = new HashMap<String, ArrayList<String>>();
		ArrayList<String> sectors = new ArrayList<String>(),
				user = new ArrayList<String>(),
				zone = new ArrayList<String>(),
				tmpSqlL = new ArrayList<String>();
		ArrayList<Double> efficiency = new ArrayList<Double>(),
				qualityCalc = new ArrayList<Double>(),
				totalWage = new ArrayList<Double>(),
				totalOperation = new ArrayList<Double>(),
				totalDepreciation = new ArrayList<Double>(),
				fixed = new ArrayList<Double>();
		ArrayList<Integer> effectivity = new ArrayList<Integer>();
		ArrayList<Object> data = new ArrayList<Object>();
		boolean pass = false;
		
		while(srs1.next()){
			hiElement = "";
			hiVal = 0;
			tmpQuality = 0;
			tmpSum = 0;
			quality = 0;
			tmpTotalOperation = 0;
			tmpTotalWage = 0;
			tmpTotalDepreciation = 0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			elementsTmpQuality.clear();
			tmpSqlL = new ArrayList<String>();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='Petrol Power Plant'");
			while(srs2.next()){
				tmpQuality = 0;
				tmpSum = 0;
				
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select installment_equipment.id,quality,durability,operational,buy_price from installment_equipment,desc_equipment,list_equipment where installment='"+srs1.getString("id")+"' and equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id");
				while(srs3.next()){
					tmpSqlL.add("update list_equipment set durability='"+(srs3.getDouble("durability")-0.01)+"' where id='"+srs3.getString("id")+"'");
					tmpTotalOperation += srs3.getDouble("operational");
					tmpTotalDepreciation += srs3.getDouble("buy_price")*0.01;
					switch (srs3.getInt("quality")) {
					case 1:
						tmpQuality += 20;
						break;
					case 2:
						tmpQuality += 30;
						break;
					case 3:
						tmpQuality += 50;
						break;
					default :
						tmpQuality += 0;
						break;
					}
					tmpSum++;
				}
				tmpQuality /= tmpSum;
				elements.put(srs2.getString("equipment_type"), tmpSum);
				elementsTmpQuality.put(srs2.getString("equipment_type"), tmpQuality);
			}
			
			tmpQuality = 0;
			for(String element : elementsTmpQuality.keySet())
				tmpQuality += (elementsTmpQuality.get(element)/elementsTmpQuality.size());
			
			quality += (tmpQuality*35/100);
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='Petrol Power Plant'");
			while(srs2.next()){
				tmpQuality = 0;
				tmpSum = 0;
				
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select quality,operational from installment_employee,desc_employee,list_employee where installment='"+srs1.getString("id")+"' and employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				while(srs3.next()){
					tmpTotalWage += srs3.getDouble("operational");
					switch (srs3.getInt("quality")) {
					case 1:
						tmpQuality += 20;
						break;
					case 2:
						tmpQuality += 30;
						break;
					case 3:
						tmpQuality += 50;
						break;
					default :
						tmpQuality += 0;
						break;
					}
					tmpSum++;
				}
				tmpQuality /= tmpSum;
				tmpQuality /= 2;
				quality += (tmpQuality*20/100);
				elements.put(srs2.getString("employee_type"), tmpSum);
				elementsTmpQuality.put(srs2.getString("employee_type"), tmpQuality);
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
					sectors.add(srs1.getString("id"));
					user.add(srs1.getString("user"));
					zone.add(srs1.getString("zone"));
					totalOperation.add(tmpTotalOperation);
					totalWage.add(tmpTotalWage);
					totalDepreciation.add(tmpTotalDepreciation);
					fixed.add(srs1.getDouble("fixed"));
					tmpInstallmentSqlL.put(srs1.getString("id"), tmpSqlL);
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
					qualityCalc.add(quality);
					break;
				}
			}
			tmpSqlL = null;
		}
		data.add(sectors);
		data.add(user);
		data.add(zone);
		data.add(efficiency);
		data.add(effectivity);
		data.add(qualityCalc);
		data.add(totalOperation);
		data.add(totalWage);
		data.add(totalDepreciation);
		data.add(fixed);
		data.add(tmpInstallmentSqlL);
		
		return data;
	}
	
	private ArrayList<Object> calculateInstallmentNonPowerPlant(){
		String hiElement="";
		double hiVal = 0, tmpQuality = 0, tmpSum = 0, quality = 0, tmpTotalOperation, tmpTotalWage, tmpTotalDepreciation;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,[user],[zone],type,fixed from installment,info_sector where type!='Petrol Power Plant' and type=name"),
				srs2,srs3;
		HashMap<String, Double> elementsRatio = new HashMap<String, Double>(),
				elements = new HashMap<String, Double>(),
				elementsCalc = new HashMap<String, Double>(),
				elementsTmpQuality = new HashMap<String, Double>();
		HashMap<String, ArrayList<String>> tmpInstallmentSqlL = new HashMap<String, ArrayList<String>>();
		ArrayList<String> sectors = new ArrayList<String>(),
				user = new ArrayList<String>(),
				zone = new ArrayList<String>(),
				tmpSqlL = new ArrayList<String>();
		ArrayList<Double> efficiency = new ArrayList<Double>(),
				qualityCalc = new ArrayList<Double>(),
				totalWage = new ArrayList<Double>(),
				totalOperation = new ArrayList<Double>(),
				totalDepreciation = new ArrayList<Double>(),
				fixed = new ArrayList<Double>();
		ArrayList<Integer> effectivity = new ArrayList<Integer>();
		ArrayList<Object> data = new ArrayList<Object>();
		boolean pass = false;
		
		System.out.println("Calculating installment non-powerplant");
		
		while(srs1.next()){
			System.out.println("Marker 1");
			hiElement = "";
			hiVal = 0;
			tmpQuality = 0;
			tmpSum = 0;
			quality = 0;
			tmpTotalOperation = 0;
			tmpTotalWage = 0;
			tmpTotalDepreciation = 0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			elementsTmpQuality.clear();
			tmpSqlL = new ArrayList<String>();
			pass = true;
			
			srs2 = db.getJdbc().queryForRowSet("select equipment_type,items from info_sector_equipment where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				tmpQuality = 0;
				tmpSum = 0;
				
				elementsRatio.put(srs2.getString("equipment_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("equipment_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select installment_equipment.id,quality,durability,operational,buy_price from installment_equipment,desc_equipment,list_equipment where installment='"+srs1.getString("id")+"' and equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.[desc]=desc_equipment.id and durability > 0");
				while(srs3.next()){
					tmpSqlL.add("update list_equipment set durability='"+(srs3.getDouble("durability")-0.01)+"' where id='"+srs3.getString("id")+"'");
					tmpTotalOperation += srs3.getDouble("operational");
					tmpTotalDepreciation += srs3.getDouble("buy_price")*0.01;
					switch (srs3.getInt("quality")) {
					case 1:
						tmpQuality += 20;
						break;
					case 2:
						tmpQuality += 30;
						break;
					case 3:
						tmpQuality += 50;
						break;
					default :
						tmpQuality += 0;
						break;
					}
					tmpSum++;
				}
				tmpQuality /= tmpSum;
				elements.put(srs2.getString("equipment_type"), tmpSum);
				elementsTmpQuality.put(srs2.getString("equipment_type"), tmpQuality);
			}
			
			System.out.println("Marker 2");
			
			tmpQuality = 0;
			for(String element : elementsTmpQuality.keySet())
				tmpQuality += (elementsTmpQuality.get(element)/elementsTmpQuality.size());
			
			quality += (tmpQuality*35/100);
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				tmpQuality = 0;
				tmpSum = 0;
				
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select quality,operational from installment_employee,desc_employee,list_employee where installment='"+srs1.getString("id")+"' and employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.[desc]=desc_employee.id");
				while(srs3.next()){
					tmpTotalWage += srs3.getDouble("operational");
					switch (srs3.getInt("quality")) {
					case 1:
						tmpQuality += 20;
						break;
					case 2:
						tmpQuality += 30;
						break;
					case 3:
						tmpQuality += 50;
						break;
					default :
						tmpQuality += 0;
						break;
					}
					tmpSum++;
				}
				tmpQuality /= tmpSum;
				tmpQuality /= 2;
				quality += (tmpQuality*20/100);
				elements.put(srs2.getString("employee_type"), tmpSum);
				elementsTmpQuality.put(srs2.getString("employee_type"), tmpQuality);
			}
			
			System.out.println("Marker 3");
			
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
					sectors.add(srs1.getString("id"));
					user.add(srs1.getString("user"));
					zone.add(srs1.getString("zone"));
					totalOperation.add(tmpTotalOperation);
					totalWage.add(tmpTotalWage);
					totalDepreciation.add(tmpTotalDepreciation);
					fixed.add(srs1.getDouble("fixed"));
					tmpInstallmentSqlL.put(srs1.getString("id"), tmpSqlL);
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
					qualityCalc.add(quality);
					break;
				}
			}
			tmpSqlL = null;
		}
		
		System.out.println("Done calculating non-powerplant");
		
		data.add(sectors);
		data.add(user);
		data.add(zone);
		data.add(efficiency);
		data.add(effectivity);
		data.add(qualityCalc);
		data.add(totalOperation);
		data.add(totalWage);
		data.add(totalDepreciation);
		data.add(fixed);
		data.add(tmpInstallmentSqlL);
		return data;
	}
	
//	private String getUniqueIncrementId(String inc_table){
//		String val = "", curDate, date, sqls[];
//		int counter;
//		ArrayList<String> sqlL = new ArrayList<String>();
//		
//		SqlRowSet srs = db.getJdbc().queryForRowSet("select value from info_values where name='"+inc_table+"' union select value from info_values where name='last_inc_set_date'");
//		if(srs.next())
//			counter = Integer.parseInt(srs.getString("value"));
//		else return val;
//		
//		curDate = dateNow("ddMMyy");
//		
//		if(srs.next())
//			date = srs.getString("value");
//		else return val;
//		
//		if(!date.equals(curDate)){
//			date = curDate;
//			counter = 0;
//			sqlL.add("update info_values set value='"+date+"' where name='last_inc_set_date'");
//			
//			//update all inc into 0 value
//			sqlL.add("update info_values set value='0' where substr(name,1,4)='inc_'");
//		}
//		
//		if(counter > 999)
//			val = ""+counter;
//		else if(counter > 99)
//			val = "0"+counter;
//		else if(counter > 9)
//			val = "00"+counter;
//		else val = "000"+counter;
//		sqlL.add("update info_values set value='"+(counter+1)+"' where name='"+inc_table+"'");
//		
//		sqls = new String[sqlL.size()];
//		sqlL.toArray(sqls);
//		db.getJdbc().batchUpdate(sqls);
//		
//		return date+val;
//	}
	
	private void giveRawProductToPPPDemigod(){
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id from info_zone"),
				srs2,srs3;
		String sqls[],idInc;
		
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from storage where [user]='demigod"+srs1.getString("id")+"'");
			while(srs2.next()){
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRWATR02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next())
					sqlL.add("update storage_product set size='"+(srs3.getDouble("size")+70)+"' where id='"+srs3.getString("id")+"'");
				else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRWATR02','"+srs2.getString("id")+"','70','1.00')");
				}
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRGSLN02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next())
					sqlL.add("update storage_product set size='"+(srs3.getDouble("size")+19)+"' where id='"+srs3.getString("id")+"'");
				else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRGSLN02','"+srs2.getString("id")+"','19','21.00')");
				}
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRCMCL02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next())
					sqlL.add("update storage_product set size='"+(srs3.getDouble("size")+17)+"' where id='"+srs3.getString("id")+"'");
				else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRCMCL02','"+srs2.getString("id")+"','17','18.05')");
				}
			}
		}
		
		for(String sql : sqlL)
			System.out.println(sql);
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs1 = null;
		srs2 = null;
		srs3 = null;
		
		System.out.println("Demigod getting all raw..");
	}
	
	private void startingRawMaterialFromDemigod(){
		String idInc, tmp;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id from info_zone"),
				srs2,srs3;
		ArrayList<String> sqlL = new ArrayList<String>();
		String sqls[];
		
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from storage where [user]='demigod"+srs1.getString("id")+"'");
			while(srs2.next()){
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRWATR02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next()){
					sqlL.add("update storage_product set size=400 where id='"+srs3.getString("id")+"'");
					sqlL.add("update market_product set size=400 wher storage_product_id='"+srs3.getString("id")+"'");
				} else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRWATR02','"+srs2.getString("id")+"',400,1.00)");
					tmp = idInc;
					idInc = getUniqueIncrementIdNew("market_product");
					sqlL.add("insert into market_product values ('"+BusinessGameService.KEY_MARKET_PRODUCT+idInc+"','"+tmp+"','"+srs1.getString("id")+"',1.00,400)");
				}
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRCROL02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next()){
					sqlL.add("update storage_product set size=400 where id='"+srs3.getString("id")+"'");
					sqlL.add("update market_product set size=400 wher storage_product_id='"+srs3.getString("id")+"'");
				} else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRCROL02','"+srs2.getString("id")+"',400,3.00)");
					tmp = idInc;
					idInc = getUniqueIncrementIdNew("market_product");
					sqlL.add("insert into market_product values ('"+BusinessGameService.KEY_MARKET_PRODUCT+idInc+"','"+tmp+"','"+srs1.getString("id")+"',3.00,400)");
				}
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRIRON02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next()){
					sqlL.add("update storage_product set size=400 where id='"+srs3.getString("id")+"'");
					sqlL.add("update market_product set size=400 wher storage_product_id='"+srs3.getString("id")+"'");
				} else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRIRON02','"+srs2.getString("id")+"',400,1.9)");
					tmp = idInc;
					idInc = getUniqueIncrementIdNew("market_product");
					sqlL.add("insert into market_product values ('"+BusinessGameService.KEY_MARKET_PRODUCT+idInc+"','"+tmp+"','"+srs1.getString("id")+"',1.9,400)");
				}
				srs3 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.[desc]='PRSLCA02' and storage='"+srs2.getString("id")+"'");
				if(srs3.next()){
					sqlL.add("update storage_product set size=400 where id='"+srs3.getString("id")+"'");
					sqlL.add("update market_product set size=400 wher storage_product_id='"+srs3.getString("id")+"'");
				} else {
					idInc = getUniqueIncrementIdNew("storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRSLCA02','"+srs2.getString("id")+"',400,2.5)");
					tmp = idInc;
					idInc = getUniqueIncrementIdNew("market_product");
					sqlL.add("insert into market_product values ('"+BusinessGameService.KEY_MARKET_PRODUCT+idInc+"','"+tmp+"','"+srs1.getString("id")+"',2.5,400)");
				}
			}
		}
		
		for(String sql : sqlL)
			System.out.println(sql);
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		sqls = null;
		srs1 = null;
		srs1 = null;
		srs2 = null;
		srs3 = null;
		
		gc();
		
		System.out.println("Demigod release initial raw material at most 1000 CBM");
	}
	
	private void deleteRawProductDemigod(){
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id from info_zone"),
				srs2;
		ArrayList<String> sqlL = new ArrayList<String>();
		String sqls[];
		
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from storage where [user]='demigod"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from storage_product where id='"+srs2.getString("id")+"'");
			}
		}
		
		for(String sql : sqlL)
			System.out.println(sql);
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		System.out.println("Demigod deleting all product..");
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
		if(srs.next())
			counter = Integer.parseInt(srs.getString("value"));
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
			sqlL.add("insert into user_finance values ('"+BusinessGameService.KEY_USER_FINANCE+idInc+"','"+user+"','"+type+"','"+(factor*amount)+"')");
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
	
	/**
	 * Clean up the mess we've been created. XD
	 */
	private void gc(){
		System.gc();
	}
}
