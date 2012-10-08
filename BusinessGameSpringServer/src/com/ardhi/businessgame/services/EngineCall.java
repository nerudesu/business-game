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
				bankModule();				
				break;
				
			case SECTOR_NON_POWER_PLANT :
				sectorNonPowerPlantModule();
//				sqlL = new ArrayList<String>();
//				
//				data = calculateInstallmentNonPowerPlant();
//				
//				sectors = (ArrayList<String>) data.get(0);
//				users = (ArrayList<String>) data.get(1);
//				zones = (ArrayList<String>) data.get(2);
//
//				efficiency = (ArrayList<Double>) data.get(3);
//				qualityCalc = (ArrayList<Double>) data.get(5);
//				
//				effectivity = (ArrayList<Integer>) data.get(4);
//				
//				inputMax = new HashMap<String, Double>();
//				input = new HashMap<String, Double>();
//				outputRatio = new HashMap<String, Double>();
//				inputQuality = new HashMap<String, Integer>();
//				inputId = new HashMap<String, String>();
//				
//				System.out.println("Mulai Non Power Plant");
//				
//				for(int i=0;i<sectors.size();i++){
//					inputMax.clear();
//					input.clear();
//					outputRatio.clear();
//					inputQuality.clear();
//					inputId.clear();
//					
//					tmpd = qualityCalc.get(i);
//					tmpd2 = 0;
//					tmpd3 = 0;
//					tmpi = 0;
//					
//					srs = db.getJdbc().queryForRowSet("select input_type,size from info_sector_input where sector=(select type from installment where id='"+sectors.get(i)+"')");
//					while(srs.next()){
//						inputMax.put(srs.getString("input_type"), srs.getDouble("size")*effectivity.get(i));
//					}
//					
//					srs = db.getJdbc().queryForRowSet("select output_type,size from info_sector_output where sector=(select type from installment where id='"+sectors.get(i)+"')");
//					while(srs.next()){
//						outputRatio.put(srs.getString("output_type"), srs.getDouble("size"));
//					}
//					
//					tmps = "";
//					srs = db.getJdbc().queryForRowSet("select storage_product.id,storage,product,quality,size from storage_product,desc_product where storage=(select id from storage where user='"+users.get(i)+"' and zone='"+zones.get(i)+"') and storage_product.desc=desc_product.id");
//					while(srs.next()){
//						tmps = srs.getString("storage");
//						tmpd2 = srs.getDouble("size");
//						srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs.getString("id")+"'");
//						while(srs2.next()){
//							tmpd2 -= srs2.getDouble("size");
//						}
//						if(tmpd2 > 0){
//							if(inputMax.containsKey(srs.getString("product"))){
//								if(input.containsKey(srs.getString("product"))){
//									if(inputQuality.get(srs.getString("product")) < srs.getDouble("quality")){
//										input.remove(srs.getString("product"));
//										inputQuality.remove(srs.getString("product"));
//										inputId.remove(srs.getString("product"));
//										input.put(srs.getString("product"), srs.getDouble("size"));
//										inputQuality.put(srs.getString("product"), srs.getInt("quality"));
//										inputId.put(srs.getString("product"), srs.getString("id"));
//									}
//								} else {
//									input.put(srs.getString("product"), srs.getDouble("size"));
//									inputQuality.put(srs.getString("product"), srs.getInt("quality"));
//									inputId.put(srs.getString("product"), srs.getString("id"));
//								}
//							}
//						} else break;
//					}
//					
//					srs = db.getJdbc().queryForRowSet("select actual_supply from installment where id='"+sectors.get(i)+"'");
//					srs.next();
//					if(srs.getDouble("actual_supply") > 0){
//						input.put("Energy", srs.getDouble("actual_supply"));
//						inputQuality.put("Energy", 0);
//						inputId.put("Energy", "");
//						sqlL.add("update installment set actual_supply='0' where id='"+sectors.get(i)+"'");
//					}
//					
//					System.out.println("Storage : ");
//					for(String in : input.keySet()){
//						System.out.println(in+" : "+input.get(in));
//					}
//					
//					System.out.println("Requirement : ");
//					for(String in : inputMax.keySet()){
//						System.out.println(in+" : "+inputMax.get(in));
//					}
//					
//					tmpd2 = 0;
//					
//					if(inputMax.size() == input.size()){
//						//calculate quality :
//						tmpd3 = efficiency.get(i);
//						System.out.println("eff produk awal : "+tmpd3);
//						for(String in : input.keySet()){
//							if(!in.equals("Energy")){
//								switch (inputQuality.get(in)) {
//									case 1:
//										tmpd2 += (20/input.size());
//										break;
//									case 2:
//										tmpd2 += (30/input.size());
//										break;
//									case 3:
//										tmpd2 += (50/input.size());
//										break;
//									default :
//										tmpd2 += 0;
//										break;
//								}
//							}
//							if(tmpd3 > (input.get(in)/inputMax.get(in))){
//								tmpd3 = input.get(in)/inputMax.get(in);
//								System.out.println(in+" "+input.get(in)+" "+inputMax.get(in));
//								System.out.println("Eff akhir"+tmpd3);
//							}
//						}
//						tmpd += tmpd2*45/100;
//						qualityCalc.set(i, tmpd);
//						
//						for(String in : input.keySet()){
//							if(!in.equals("Energy")){
//								tmpd2 = (input.get(in) - (inputMax.get(in)*tmpd3));
//								System.out.println(in+" "+input.get(in)+" "+inputMax.get(in)+" "+tmpd3);
//								System.out.println(in+" "+tmpd2);
//								if(tmpd2 > 0)
//									sqlL.add("update storage_product set size='"+tmpd2+"' where id='"+inputId.get(in)+"'");
//								else sqlL.add("delete from storage_product where id='"+inputId.get(in)+"'");
//							}
//						}
//						
//						if(tmpd < 30)
//							tmpi = 1;
//						else if(tmpd < 40 && tmpd > 29)
//							tmpi = 2;
//						else if(tmpd > 39)
//							tmpi = 3;
//						
//						for(String ou : outputRatio.keySet()){
//							srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc=(select id from desc_product where product='"+ou+"' and quality='"+tmpi+"') and storage='"+tmps+"'");
//							if(srs.next()){
//								sqlL.add("update storage_product set size='"+(srs.getDouble("size")+(outputRatio.get(ou)*tmpd3))+"' where id='"+srs.getString("id")+"'");
//							} else {
//								idInc = getUniqueIncrementId("inc_storage_product");
//								srs2 = db.getJdbc().queryForRowSet("select id from desc_product where product='"+ou+"' and quality='"+tmpi+"'");
//								srs2.next();
//								sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs2.getString("id")+"','"+tmps+"','"+(outputRatio.get(ou)*tmpd3)+"')");
//							}
//							System.out.println(ou+" "+(outputRatio.get(ou)*tmpd3));
//						}
//					}
//				}
//				
//				for(String x : sqlL)
//					System.out.println(x);
//				
//				sqls = new String[sqlL.size()];
//				sqlL.toArray(sqls);
//				db.getJdbc().batchUpdate(sqls);
//				
//				System.out.println("Selesai Non Power Plant");
//				
//				sqlL = null;
//				data = null;
//				sectors = null;
//				users = null;
//				zones = null;
//				efficiency = null;
//				qualityCalc = null;
//				effectivity = null;
//				
//				inputMax = null;
//				input = null;
//				outputRatio = null;
//				inputQuality = null;
//				inputId = null;
				
				break;
				
			case SECTOR_POWER_PLANT :
				giveRawProductToPPPDemigod();
				
				sectorPowerPlantModule();
//				sqlL = new ArrayList<String>();
//				
//				data = calculateInstallmentPowerPlant();
//				
//				sectors = (ArrayList<String>) data.get(0);
//				users = (ArrayList<String>) data.get(1);
//				zones = (ArrayList<String>) data.get(2);
//
//				efficiency = (ArrayList<Double>) data.get(3);
//				qualityCalc = (ArrayList<Double>) data.get(5);
//				
//				effectivity = (ArrayList<Integer>) data.get(4);
//				
//				inputMax = new HashMap<String, Double>();
//				input = new HashMap<String, Double>();
//				outputRatio = new HashMap<String, Double>();
//				inputQuality = new HashMap<String, Integer>();
//				inputId = new HashMap<String, String>();
//				
//				System.out.println("Mulai Power Plant");
//				
//				for(int i=0;i<sectors.size();i++){
//					inputMax.clear();
//					input.clear();
//					outputRatio.clear();
//					inputQuality.clear();
//					inputId.clear();
//					
//					tmpd = qualityCalc.get(i);
//					tmpd2 = 0;
//					tmpd3 = 0;
//					tmpi = 0;
//					
//					srs = db.getJdbc().queryForRowSet("select input_type,size from info_sector_input where sector=(select type from installment where id='"+sectors.get(i)+"') and input_type!='Energy'");
//					while(srs.next()){
//						inputMax.put(srs.getString("input_type"), srs.getDouble("size")*effectivity.get(i));
//					}
//					
//					srs = db.getJdbc().queryForRowSet("select output_type,size from info_sector_output where sector=(select type from installment where id='"+sectors.get(i)+"')");
//					while(srs.next()){
//						outputRatio.put(srs.getString("output_type"), srs.getDouble("size"));
//					}
//					
//					tmps = "";
//					srs = db.getJdbc().queryForRowSet("select storage_product.id,storage,product,quality,size from storage_product,desc_product where storage=(select id from storage where user='"+users.get(i)+"' and zone='"+zones.get(i)+"') and storage_product.desc=desc_product.id");
//					while(srs.next()){
//						tmps = srs.getString("storage");
//						tmpd2 = srs.getDouble("size");
//						srs2 = db.getJdbc().queryForRowSet("select size from market_product where storage_product_id='"+srs.getString("id")+"'");
//						while(srs2.next()){
//							tmpd2 -= srs2.getDouble("size");
//						}
//						if(tmpd2 > 0){
//							if(inputMax.containsKey(srs.getString("product"))){
//								if(input.containsKey(srs.getString("product"))){
//									if(inputQuality.get(srs.getString("product")) < srs.getDouble("quality")){
//										input.remove(srs.getString("product"));
//										inputQuality.remove(srs.getString("product"));
//										inputId.remove(srs.getString("product"));
//										input.put(srs.getString("product"), srs.getDouble("size"));
//										inputQuality.put(srs.getString("product"), srs.getInt("quality"));
//										inputId.put(srs.getString("product"), srs.getString("id"));
//									}
//								} else {
//									input.put(srs.getString("product"), srs.getDouble("size"));
//									inputQuality.put(srs.getString("product"), srs.getInt("quality"));
//									inputId.put(srs.getString("product"), srs.getString("id"));
//								}
//							}
//						} else break;
//					}
//					
//					System.out.println("Storage : ");
//					for(String in : input.keySet()){
//						System.out.println(in+" : "+input.get(in));
//					}
//					
//					System.out.println("Requirement : ");
//					for(String in : inputMax.keySet()){
//						System.out.println(in+" : "+inputMax.get(in));
//					}
//					
//					tmpd2 = 0;
//					
//					if(inputMax.size() == input.size()){
//						//calculate quality :
//						tmpd3 = efficiency.get(i);
//						System.out.println("eff produk awal : "+tmpd3);
//						for(String in : input.keySet()){
//							switch (inputQuality.get(in)) {
//							case 1:
//								tmpd2 += (20/input.size());
//								break;
//							case 2:
//								tmpd2 += (30/input.size());
//								break;
//							case 3:
//								tmpd2 += (50/input.size());
//								break;
//							default :
//								tmpd2 += 0;
//								break;
//							}
//							if(tmpd3 > (input.get(in)/inputMax.get(in))){
//								tmpd3 = input.get(in)/inputMax.get(in);
//								System.out.println("Eff akhir"+tmpd3);
//							}
//						}
//						tmpd += tmpd2*45/100;
//						qualityCalc.set(i, tmpd);
//						
//						for(String in : input.keySet()){
//							tmpd2 = (input.get(in) - (inputMax.get(in)*tmpd3));
//							System.out.println(in+" "+tmpd2);
//							if(tmpd2 > 0)
//								sqlL.add("update storage_product set size='"+tmpd2+"' where id='"+inputId.get(in)+"'");
//							else sqlL.add("delete from storage_product where id='"+inputId.get(in)+"'");
//						}
//						
//						if(tmpd < 30){
//							tmpi = 1;
//							tmpd2 = -0.2;
//						}
//						else if(tmpd < 40 && tmpd > 29){
//							tmpi = 2;
//							tmpd2 = 1;
//						}
//						else if(tmpd > 39){
//							tmpi = 3;
//							tmpd2 = 0.5;
//						}
//						
//						for(String ou : outputRatio.keySet()){
//							if(ou.equals("Energy")){
//								tmpd2 = outputRatio.get(ou)+(outputRatio.get(ou)*tmpd2);
//								srs = db.getJdbc().queryForRowSet("select id,planned_supply from installment where supply='"+sectors.get(i)+"'");
//								while(srs.next()){
//									if((tmpd2 - srs.getDouble("planned_supply")) > 0){
//										tmpd2 -= srs.getDouble("planned_supply");
//										sqlL.add("update installment set actual_supply='"+srs.getDouble("planned_supply")+"' where id='"+srs.getString("id")+"'");
//									} else {
//										sqlL.add("update installment set actual_supply='"+tmpd2+"' where id='"+srs.getString("id")+"'");
//										tmpd2 = 0;
//									}
//								}
//							} else {
//								srs = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc=(select id from desc_product where product='"+ou+"' and quality='"+tmpi+"') and storage='"+tmps+"'");
//								if(srs.next()){
//									sqlL.add("update storage_product set size='"+(srs.getDouble("size")+(outputRatio.get(ou)*tmpd3))+"' where id='"+srs.getString("id")+"'");
//								} else {
//									idInc = getUniqueIncrementId("inc_storage_product");
//									srs2 = db.getJdbc().queryForRowSet("select id from desc_product where product='"+ou+"' and quality='"+tmpi+"'");
//									srs2.next();
//									sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs2.getString("id")+"','"+tmps+"','"+(outputRatio.get(ou)*tmpd3)+"')");
//								}
//							}
//						}
//					}
//				}
//				
//				for(String x : sqlL)
//					System.out.println(x);
//				
//				sqls = new String[sqlL.size()];
//				sqlL.toArray(sqls);
//				db.getJdbc().batchUpdate(sqls);
//				
//				System.out.println("Selesai Power Plant");
//				
//				sqlL = null;
//				data = null;
//				sectors = null;
//				users = null;
//				zones = null;
//				efficiency = null;
//				qualityCalc = null;
//				effectivity = null;
//				
//				inputMax = null;
//				input = null;
//				outputRatio = null;
//				inputQuality = null;
//				inputId = null;
				
				deleteRawProductDemigod();
				
				break;
				
			case MARKET_SHARE :
				marketShareModule();
//				srs = db.getJdbc().queryForRowSet("select zone,product,value from market_share");
//				sqlL = new ArrayList<String>();
//				HashMap<String, Double> productScore,productSize,producPrice;
//				HashMap<String, String> productMarketId;
//				HashMap<Integer, Double> baseQualityPrice;
//				double offset = 0.25,totalProduct;
//				int highestQuality = 0;
//				
//				while(srs.next()){
//					baseQualityPrice = new HashMap<Integer, Double>();
//					productScore = new HashMap<String, Double>();
//					productSize = new HashMap<String, Double>();
//					producPrice = new HashMap<String, Double>();
//					productMarketId = new HashMap<String, String>();
//					
//					System.out.println("Zone "+srs.getString("zone")+" : ");
//					tmpd2 = 0;
//					totalProduct = srs.getDouble("value");
//					
//					srs2 = db.getJdbc().queryForRowSet("select quality,base_price from info_product where name='"+srs.getString("product")+"'");
//					while(srs2.next()){
//						baseQualityPrice.put(srs2.getInt("quality"), srs2.getDouble("base_price"));
//						if(highestQuality < srs2.getInt("quality"))
//							highestQuality = srs2.getInt("quality");
//					}
//					
//					srs2 = db.getJdbc().queryForRowSet("select market_product.id,storage_product_id,price,quality,market_product.size from market_product,market,storage_product,desc_product where storage_product_id=storage_product.id and storage_product.desc=desc_product.id and market.id=market_product.market and market.zone='"+srs.getString("zone")+"' and desc_product.product='"+srs.getString("product")+"'");
//					while(srs2.next()){
//						tmpd = srs2.getDouble("price")/baseQualityPrice.get(srs2.getInt("quality"));
//						tmpd = ((1+offset)-tmpd)*100;
//						productScore.put(srs2.getString("storage_product_id"), tmpd);
//						productSize.put(srs2.getString("storage_product_id"), srs2.getDouble("size"));
//						producPrice.put(srs2.getString("storage_product_id"), srs2.getDouble("price"));
//						productMarketId.put(srs2.getString("storage_product_id"), srs2.getString("id"));
//					}
//					
//					System.out.println("Marker 2");
//					
//					srs2 = db.getJdbc().queryForRowSet("select product_id,multiplier,count from product_advertisement,desc_advertisement,storage_product,desc_product where adv=desc_advertisement.id and product_id=storage_product.id and storage_product.desc=desc_product.id and zone='"+srs.getString("zone")+"' and desc_product.product='"+srs.getString("product")+"'");
//					while(srs2.next()){
//						if(productScore.get(srs2.getString("product_id")) != null){
//							tmpd = productScore.get(srs2.getString("product_id"));
//							tmpd += (srs2.getInt("multiplier")*srs2.getLong("count"));
//							tmpd2 += tmpd;
//							productScore.remove(srs2.getString("product_id"));
//							productScore.put(srs2.getString("product_id"), tmpd);
//						}
//					}
//					
//					//sebaran 100:
//					tmpd2 = 100/tmpd2;
//					System.out.println(tmpd2);
//					for(String productId : productScore.keySet()){
//						tmpd = productScore.get(productId)*tmpd2;
//						System.out.println(productId+" has Gasoline's share of "+new BigDecimal(Double.valueOf(tmpd)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()+"%, or "+new BigDecimal(Double.valueOf(tmpd*totalProduct/100)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()+" CBM");
//					}
//					
//					baseQualityPrice = null;
//					producPrice = null;
//					productScore = null;
//					productSize = null;
//				}
//				
//				sqls = new String[sqlL.size()];
//				sqlL.toArray(sqls);
//				db.getJdbc().batchUpdate(sqls);
//				
//				sqlL = null;
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
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select user,money,sector,prob,cost,raw_turn,storage,req_borrow_bank.zone from req_borrow_bank,info_sector,user where sector=info_sector.name and user.name=user"),
				srs2 = db.getJdbc().queryForRowSet("select value from info_values where name='interest' union select value from info_values where name='cost_storage'");
		
		srs2.next();
		interest = Double.parseDouble(srs2.getString("value"));
		
		srs2.next();
		storageCost = Double.parseDouble(srs2.getString("value"));
		
		while(srs1.next()){
			d = srs1.getDouble("prob")*100;
			res = r.nextInt(100);
			total = 0;
			if(res<d){
//				System.out.println("Tanda 1");
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
				
//				System.out.println("Tanda 2");
				
				srs2 = db.getJdbc().queryForRowSet("select items,base_price,base_operational from info_sector_employee,info_employee where employee_type=info_employee.name and info_sector_employee.sector='"+srs1.getString("sector")+"'");
				while(srs2.next()){
					total += srs2.getDouble("base_price")*srs2.getInt("items");
					total += srs2.getDouble("base_operational")*srs2.getInt("items")*srs1.getInt("raw_turn");
				}
				
//				System.out.println("Tanda 3");
				
				srs2 = db.getJdbc().queryForRowSet("select base_price,size from info_sector_input,info_product where input_type=info_product.name and info_sector_input.sector='"+srs1.getString("sector")+"'");
				while(srs2.next()){
					total += srs2.getDouble("base_price")*srs2.getDouble("size")*srs1.getInt("raw_turn");
				}
				
				sqlL.add("update user set money='"+(total+srs1.getDouble("money"))+"' where name='"+srs1.getString("user")+"'");
				total += total*interest;
				idInc = getUniqueIncrementId("inc_borrow_bank");
				sqlL.add("insert into borrow_bank values ('"+BusinessGameService.KEY_BORROW_BANK+idInc+"','"+srs1.getString("user")+"','0','"+total+"')");
				
//				System.out.println("Tanda 4");
			}
		}
		sqlL.add("delete from req_borrow_bank");
		
		srs1 = db.getJdbc().queryForRowSet("select id,borrow_bank.user,turn,money,borrow from borrow_bank,user where user.name=borrow_bank.user");
		while(srs1.next()){
			if(srs1.getInt("turn") < BORROW_BANK_REQ_TIME){
				sqlL.add("update borrow_bank set turn='"+(srs1.getInt("turn")+1)+"' where id='"+srs1.getString("id")+"'");
			} else {
				sqlL.add("delete from borrow_bank where id='"+srs1.getString("id")+"'");
				sqlL.add("update user set money='"+(srs1.getDouble("money")-srs1.getDouble("borrow"))+"' where name='"+srs1.getString("user")+"'");
			}
		}
		
		srs1 = db.getJdbc().queryForRowSet("select value from info_values where name='turn'");
		if(srs1.next())
			sqlL.add("update info_values set value='"+(Integer.parseInt(srs1.getString("value"))+1)+"' where name='turn'");
		
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
				totalCost = (ArrayList<Double>) data.get(6);
		ArrayList<Integer> effectivity = (ArrayList<Integer>) data.get(4);
		HashMap<String, ArrayList<String>> installmentSqlL = (HashMap<String, ArrayList<String>>)data.get(7);
		HashMap<String, Double> inputMax = new HashMap<String, Double>(),
				input = new HashMap<String, Double>(),
				outputRatio = new HashMap<String, Double>();
		
		HashMap<String, Integer> inputQuality = new HashMap<String, Integer>();
		HashMap<String, String> inputId = new HashMap<String, String>();
		
		double tmpd1,tmpd2,tmpd3;
		int tmpi;
		SqlRowSet srs1,srs2;
		String tmps,idInc,sqls[];
		
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
			
			srs1 = db.getJdbc().queryForRowSet("select input_type,size from info_sector_input where sector=(select type from installment where id='"+sectors.get(i)+"')");
			while(srs1.next()){
				inputMax.put(srs1.getString("input_type"), srs1.getDouble("size")*effectivity.get(i));
			}
			
			srs1 = db.getJdbc().queryForRowSet("select output_type,size from info_sector_output where sector=(select type from installment where id='"+sectors.get(i)+"')");
			while(srs1.next()){
				outputRatio.put(srs1.getString("output_type"), srs1.getDouble("size"));
			}
			
			tmps = "";
			srs1 = db.getJdbc().queryForRowSet("select storage_product.id,storage,product,quality,size from storage_product,desc_product where storage=(select id from storage where user='"+users.get(i)+"' and zone='"+zones.get(i)+"') and storage_product.desc=desc_product.id");
			while(srs1.next()){
				tmps = srs1.getString("storage");
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
								input.put(srs1.getString("product"), srs1.getDouble("size"));
								inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
								inputId.put(srs1.getString("product"), srs1.getString("id"));
							}
						} else {
							input.put(srs1.getString("product"), srs1.getDouble("size"));
							inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
							inputId.put(srs1.getString("product"), srs1.getString("id"));
						}
					}
				} else break;
			}
			
			srs1 = db.getJdbc().queryForRowSet("select actual_supply from installment where id='"+sectors.get(i)+"'");
			srs1.next();
			if(srs1.getDouble("actual_supply") > 0){
				input.put("Energy", srs1.getDouble("actual_supply"));
				inputQuality.put("Energy", 0);
				inputId.put("Energy", "");
				db.getJdbc().execute("update installment set actual_supply='0' where id='"+sectors.get(i)+"'");
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
			
			if(inputMax.size() == input.size()){
				//calculate quality :
				tmpd3 = efficiency.get(i);
				System.out.println("eff produk awal : "+tmpd3);
				for(String in : input.keySet()){
					if(!in.equals("Energy")){
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
						System.out.println(in+" "+input.get(in)+" "+inputMax.get(in)+" "+tmpd3);
						System.out.println(in+" "+tmpd2);
						if(tmpd2 > 0)
							db.getJdbc().execute("update storage_product set size='"+tmpd2+"' where id='"+inputId.get(in)+"'");
						else db.getJdbc().execute("delete from storage_product where id='"+inputId.get(in)+"'");
					}
				}
				
				if(tmpd1 < 30)
					tmpi = 1;
				else if(tmpd1 < 40 && tmpd1 > 29)
					tmpi = 2;
				else if(tmpd1 > 39)
					tmpi = 3;
				
				for(String ou : outputRatio.keySet()){
					srs1 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc=(select id from desc_product where product='"+ou+"' and quality='"+tmpi+"') and storage='"+tmps+"'");
					if(srs1.next()){
						db.getJdbc().execute("update storage_product set size='"+(srs1.getDouble("size")+(outputRatio.get(ou)*tmpd3))+"' where id='"+srs1.getString("id")+"'");
					} else {
						idInc = getUniqueIncrementId("inc_storage_product");
						srs2 = db.getJdbc().queryForRowSet("select id from desc_product where product='"+ou+"' and quality='"+tmpi+"'");
						srs2.next();
						db.getJdbc().execute("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs2.getString("id")+"','"+tmps+"','"+(outputRatio.get(ou)*tmpd3)+"')");
					}
					System.out.println(ou+" "+(outputRatio.get(ou)*tmpd3));
				}
				
				srs1 = db.getJdbc().queryForRowSet("select money from user where name='"+users.get(i)+"'");
				srs1.next();
				db.getJdbc().execute("update user set money='"+(srs1.getDouble("money")-totalCost.get(i))+"' where name='"+users.get(i)+"'");
				
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
	}
	
	@SuppressWarnings("unchecked")
	private void sectorPowerPlantModule(){
		ArrayList<Object> data = calculateInstallmentPowerPlant();
		ArrayList<String> sectors = (ArrayList<String>) data.get(0),
				users = (ArrayList<String>) data.get(1),
				zones = (ArrayList<String>) data.get(2);
		ArrayList<Double> efficiency = (ArrayList<Double>) data.get(3),
				qualityCalc = (ArrayList<Double>) data.get(5),
				totalCost = (ArrayList<Double>) data.get(6);
		ArrayList<Integer> effectivity = (ArrayList<Integer>) data.get(4);
		HashMap<String, ArrayList<String>> installmentSqlL = (HashMap<String, ArrayList<String>>)data.get(7);
		HashMap<String, Double> inputMax = new HashMap<String, Double>(),
				input = new HashMap<String, Double>(),
				outputRatio = new HashMap<String, Double>();
		HashMap<String, Integer> inputQuality = new HashMap<String, Integer>();
		HashMap<String, String> inputId = new HashMap<String, String>();
		
		double tmpd1,tmpd2,tmpd3;
		int tmpi;
		String tmps,idInc,sqls[];
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
			
			tmps = "";
			srs1 = db.getJdbc().queryForRowSet("select storage_product.id,storage,product,quality,size from storage_product,desc_product where storage=(select id from storage where user='"+users.get(i)+"' and zone='"+zones.get(i)+"') and storage_product.desc=desc_product.id");
			while(srs1.next()){
				tmps = srs1.getString("storage");
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
								input.put(srs1.getString("product"), srs1.getDouble("size"));
								inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
								inputId.put(srs1.getString("product"), srs1.getString("id"));
							}
						} else {
							input.put(srs1.getString("product"), srs1.getDouble("size"));
							inputQuality.put(srs1.getString("product"), srs1.getInt("quality"));
							inputId.put(srs1.getString("product"), srs1.getString("id"));
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
						System.out.println("Eff akhir"+tmpd3);
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
					} else {
						srs1 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc=(select id from desc_product where product='"+ou+"' and quality='"+tmpi+"') and storage='"+tmps+"'");
						if(srs1.next()){
							db.getJdbc().execute("update storage_product set size='"+(srs1.getDouble("size")+(outputRatio.get(ou)*tmpd3))+"' where id='"+srs1.getString("id")+"'");
						} else {
							idInc = getUniqueIncrementId("inc_storage_product");
							srs2 = db.getJdbc().queryForRowSet("select id from desc_product where product='"+ou+"' and quality='"+tmpi+"'");
							srs2.next();
							db.getJdbc().execute("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','"+srs2.getString("id")+"','"+tmps+"','"+(outputRatio.get(ou)*tmpd3)+"')");
						}
					}
				}
				
				srs1 = db.getJdbc().queryForRowSet("select money from user where name='"+users.get(i)+"'");
				srs1.next();
				db.getJdbc().execute("update user set money='"+(srs1.getDouble("money")-totalCost.get(i))+"' where name='"+users.get(i)+"'");
				
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
	}
	
	private void marketShareModule(){
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select zone,product,value from market_share"),
				srs2;
		ArrayList<String> sqlL = new ArrayList<String>();
		HashMap<String, Double> productScore,productSize,producPrice;
		HashMap<String, String> productId;
		HashMap<Integer, Double> baseQualityPrice;
		double offset = 0.25,totalProduct,tmpd1,tmpd2;
		int highestQuality = 0;
		String sqls[];
		
		while(srs1.next()){
			baseQualityPrice = new HashMap<Integer, Double>();
			productScore = new HashMap<String, Double>();
			productSize = new HashMap<String, Double>();
			producPrice = new HashMap<String, Double>();
			productId = new HashMap<String, String>();
			
			System.out.println("Zone "+srs1.getString("zone")+" : ");
			tmpd2 = 0;
			totalProduct = srs1.getDouble("value");
			
			srs2 = db.getJdbc().queryForRowSet("select quality,price from desc_product where product='"+srs1.getString("product")+"'");
			while(srs2.next()){
				baseQualityPrice.put(srs2.getInt("quality"), srs2.getDouble("price"));
				if(highestQuality < srs2.getInt("quality"))
					highestQuality = srs2.getInt("quality");
			}
			
			System.out.println("Marker 1");
			
			srs2 = db.getJdbc().queryForRowSet("select market_product.id,storage_product_id,market_product.price,quality,market_product.size from market_product,storage_product,desc_product where storage_product_id=storage_product.id and storage_product.desc=desc_product.id and market_product.zone='"+srs1.getString("zone")+"' and desc_product.product='"+srs1.getString("product")+"'");
//			+"select market_product.id,storage_product_id,price,quality,market_product.size from market_product,market,storage_product,desc_product where storage_product_id=storage_product.id and storage_product.desc=desc_product.id and market.id=market_product.market and market.zone='"+srs1.getString("zone")+"' and desc_product.product='"+srs1.getString("product")+"'");
			while(srs2.next()){
				tmpd1 = srs2.getDouble("price")/baseQualityPrice.get(srs2.getInt("quality"));
				tmpd1 = ((1+offset)-tmpd1)*100;
				tmpd2 += tmpd1;
				productScore.put(srs2.getString("storage_product_id"), tmpd1);
				productSize.put(srs2.getString("storage_product_id"), srs2.getDouble("size"));
				producPrice.put(srs2.getString("storage_product_id"), srs2.getDouble("price"));
				productId.put(srs2.getString("storage_product_id"), srs2.getString("id"));
			}
			
			System.out.println("Marker 2");
			
			srs2 = db.getJdbc().queryForRowSet("select product_id,multiplier,count from product_advertisement,desc_advertisement,storage_product,desc_product where adv=desc_advertisement.id and product_id=storage_product.id and storage_product.desc=desc_product.id and zone='"+srs1.getString("zone")+"' and desc_product.product='"+srs1.getString("product")+"'");
			while(srs2.next()){
				if(productScore.get(srs2.getString("product_id")) != null){
					tmpd1 = productScore.get(srs2.getString("product_id"));
					tmpd1 += (srs2.getInt("multiplier")*srs2.getLong("count"));
					tmpd2 += (srs2.getInt("multiplier")*srs2.getLong("count"));
					productScore.remove(srs2.getString("product_id"));
					productScore.put(srs2.getString("product_id"), tmpd1);
				}
			}
			
			//sebaran 100:
			tmpd2 = 100/tmpd2;
			System.out.println(tmpd2);
			for(String id : productScore.keySet()){
				tmpd1 = productScore.get(id)*tmpd2;
				System.out.println(id+" has Gasoline's share of "+new BigDecimal(Double.valueOf(tmpd1)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()+"%, or "+new BigDecimal(Double.valueOf(tmpd1*totalProduct/100)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue()+" CBM");
			}
			
			baseQualityPrice = null;
			producPrice = null;
			productScore = null;
			productSize = null;
			productId = null;
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
	}
	
	private ArrayList<Object> calculateInstallmentPowerPlant(){
		String hiElement="";
		double hiVal = 0, tmpQuality = 0, tmpSum = 0, quality = 0,tmpTotal;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,user,zone from installment where type='Petrol Power Plant'"),
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
				totalCost = new ArrayList<Double>();
		ArrayList<Integer> effectivity = new ArrayList<Integer>();
		ArrayList<Object> data = new ArrayList<Object>();
		boolean pass = false;
		
		while(srs1.next()){
			hiElement = "";
			hiVal = 0;
			tmpQuality = 0;
			tmpSum = 0;
			quality = 0;
			tmpTotal = 0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			elementsTmpQuality.clear();
			tmpSqlL.clear();
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
				srs3 = db.getJdbc().queryForRowSet("select installment_equipment.id,quality,durability,operational from installment_equipment,desc_equipment,list_equipment where installment='"+srs1.getString("id")+"' and equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
				while(srs3.next()){
					tmpSqlL.add("update list_equipment set durability='"+(srs3.getDouble("durability")-0.01)+"' where id='"+srs3.getString("id")+"'");
					tmpTotal += srs3.getDouble("operational");
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
				srs3 = db.getJdbc().queryForRowSet("select quality,operational from installment_employee,desc_employee,list_employee where installment='"+srs1.getString("id")+"' and employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.desc=desc_employee.id");
				while(srs3.next()){
					tmpTotal += srs3.getDouble("operational");
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
					totalCost.add(tmpTotal);
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
		}
		data.add(sectors);
		data.add(user);
		data.add(zone);
		data.add(efficiency);
		data.add(effectivity);
		data.add(qualityCalc);
		data.add(totalCost);
		data.add(tmpInstallmentSqlL);
		return data;
	}
	
	private ArrayList<Object> calculateInstallmentNonPowerPlant(){
		String hiElement="";
		double hiVal = 0, tmpQuality = 0, tmpSum = 0, quality = 0, tmpTotal;
		int eff;
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id,user,zone,type from installment where type!='Petrol Power Plant'"),
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
				totalCost = new ArrayList<Double>();
		ArrayList<Integer> effectivity = new ArrayList<Integer>();
		ArrayList<Object> data = new ArrayList<Object>();
		boolean pass = false;
		
		while(srs1.next()){
			hiElement = "";
			hiVal = 0;
			tmpQuality = 0;
			tmpSum = 0;
			quality = 0;
			tmpTotal = 0;
			elementsRatio.clear();
			elements.clear();
			elementsCalc.clear();
			elementsTmpQuality.clear();
			tmpSqlL.clear();
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
				srs3 = db.getJdbc().queryForRowSet("select installment_equipment.id,quality,durability,operational from installment_equipment,desc_equipment,list_equipment where installment='"+srs1.getString("id")+"' and equipment='"+srs2.getString("equipment_type")+"' and installment_equipment.id=list_equipment.id and list_equipment.desc=desc_equipment.id");
				while(srs3.next()){
					tmpSqlL.add("update list_equipment set durability='"+(srs3.getDouble("durability")-0.01)+"' where id='"+srs3.getString("id")+"'");
					tmpTotal += srs3.getDouble("operational");
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
			
			srs2 = db.getJdbc().queryForRowSet("select employee_type,items from info_sector_employee where sector='"+srs1.getString("type")+"'");
			while(srs2.next()){
				tmpQuality = 0;
				tmpSum = 0;
				
				elementsRatio.put(srs2.getString("employee_type"), srs2.getDouble("items"));
				if(hiVal < srs2.getDouble("items")){
					hiElement = srs2.getString("employee_type");
					hiVal = srs2.getDouble("items");
				}
				srs3 = db.getJdbc().queryForRowSet("select quality,operational from installment_employee,desc_employee,list_employee where installment='"+srs1.getString("id")+"' and employee='"+srs2.getString("employee_type")+"' and installment_employee.id=list_employee.id and list_employee.desc=desc_employee.id");
				while(srs3.next()){
					tmpTotal += srs3.getDouble("operational");
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
					totalCost.add(tmpTotal);
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
		}
		
		data.add(sectors);
		data.add(user);
		data.add(zone);
		data.add(efficiency);
		data.add(effectivity);
		data.add(qualityCalc);
		data.add(totalCost);
		data.add(tmpInstallmentSqlL);
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
	
	private void giveRawProductToPPPDemigod(){
		ArrayList<String> sqlL = new ArrayList<String>();
		SqlRowSet srs = db.getJdbc().queryForRowSet("select id from info_zone"),
				srs1,srs2;
		String sqls[],idInc;
		
		while(srs.next()){
			srs1 = db.getJdbc().queryForRowSet("select id from storage where user='demigod"+srs.getString("id")+"'");
			while(srs1.next()){
				srs2 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc='PRWATR02' and storage='"+srs1.getString("id")+"'");
				if(srs2.next())
					sqlL.add("update storage_product set size='"+(srs2.getDouble("size")+70)+"' where id='"+srs2.getString("id")+"'");
				else {
					idInc = getUniqueIncrementId("inc_storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRWATR02','"+srs1.getString("id")+"','70')");
				}
				srs2 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc='PRGSLN02' and storage='"+srs1.getString("id")+"'");
				if(srs2.next())
					sqlL.add("update storage_product set size='"+(srs2.getDouble("size")+19)+"' where id='"+srs2.getString("id")+"'");
				else {
					idInc = getUniqueIncrementId("inc_storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRGSLN02','"+srs1.getString("id")+"','19')");
				}
				srs2 = db.getJdbc().queryForRowSet("select id,size from storage_product where storage_product.desc='PRCMCL02' and storage='"+srs1.getString("id")+"'");
				if(srs2.next())
					sqlL.add("update storage_product set size='"+(srs2.getDouble("size")+17)+"' where id='"+srs2.getString("id")+"'");
				else {
					idInc = getUniqueIncrementId("inc_storage_product");
					sqlL.add("insert into storage_product values ('"+BusinessGameService.KEY_PRODUCT+idInc+"','PRCMCL02','"+srs1.getString("id")+"','17')");
				}
			}
		}
		
		for(String sql : sqlL)
			System.out.println(sql);
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		sqlL = null;
		System.out.println("Demigod getting all raw..");
	}
	
	private void deleteRawProductDemigod(){
		SqlRowSet srs1 = db.getJdbc().queryForRowSet("select id from info_zone"),
				srs2;
		ArrayList<String> sqlL = new ArrayList<String>();
		String sqls[];
		
		while(srs1.next()){
			srs2 = db.getJdbc().queryForRowSet("select id from storage where user='demigod"+srs1.getString("id")+"'");
			while(srs2.next()){
				sqlL.add("delete from storage_product where id='"+srs2.getString("id")+"'");
			}
		}
		
		sqls = new String[sqlL.size()];
		sqlL.toArray(sqls);
		db.getJdbc().batchUpdate(sqls);
		
		System.out.println("Demigod deleting all product..");
	}
}
