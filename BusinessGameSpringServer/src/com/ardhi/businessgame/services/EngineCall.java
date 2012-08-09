package com.ardhi.businessgame.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
			SECTOR_CHEMICAL_PLANT = 1,
			
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
		SqlRowSet srs, srs2, srs3;
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
				time = TimeUnit.MINUTES.toMillis(TIME_WORK);
				System.out.println("All engine should work in the allocated time : "+TIME_WORK+" minutes");
				System.out.println("Awaiting all engine to finish their work..");
				break;
				
			case IDLE :
				time = TimeUnit.MINUTES.toMillis(TIME_TOTAL - TIME_WORK);
				System.out.println("All engine take a rest for now.");
				System.out.println("Player should interact with their data in aproximately : "+TimeUnit.MILLISECONDS.toMinutes(time)+" minutes");
				break;
				
			case BANK :
				
//				for(int i=0;i<10;i++){
//					System.out.println("bank process "+(i+1));
//					try {
//						Thread.sleep(TimeUnit.SECONDS.toMillis(1));
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				srs = db.getJdbc().queryForRowSet("select user,turn,money,prob from req_borrow_bank");
				srs2 = db.getJdbc().queryForRowSet("select value from info_values where name='interest'");
				
				Random r = new Random();
				String date = "", inc = "";
				
				srs2.next();
				int res = 0, counter;
				double d = 0;
				while(srs.next()){
					d = srs.getDouble("prob")*100;
					res = r.nextInt(100);
					if(res<d){
						System.out.println(res+" "+d);
						date = dateNow("ddMMyy");
						srs3 = db.getJdbc().queryForRowSet("select count(id) from borrow_bank where substr(id,3,6)='"+date+"'");
						srs3.next();
						counter = srs3.getInt(1)+1;
						if(counter > 999)
							inc = ""+counter;
						else if(counter > 99)
							inc = "0"+counter;
						else if(counter > 9)
							inc = "00"+counter;
						else inc = "000"+counter;
						db.getJdbc().execute("insert into borrow_bank values ('"+BusinessGameService.KEY_BORROW_BANK+date+inc+"','"+srs.getString("user")+"','"+srs.getString("turn")+"','"+srs.getDouble("money")+"','"+srs2.getDouble("value")+"')");
						
						srs3 = db.getJdbc().queryForRowSet("select money from user where name='"+srs.getString("user")+"'");
						srs3.next();
						db.getJdbc().execute("update user set money='"+(srs3.getDouble("money")+srs.getDouble("money"))+"' where name='"+srs.getString("user")+"'");
					}
				}
				db.getJdbc().execute("delete from req_borrow_bank");
				
				srs = db.getJdbc().queryForRowSet("select id,turn from borrow_bank");
				while(srs.next()){
					if(srs.getInt("turn")+1 < BORROW_BANK_REQ_TIME){
						db.getJdbc().execute("update borrow_bank set turn='"+(srs.getInt("turn")+1)+"' where id='"+srs.getString("id")+"'");
					} else {
						db.getJdbc().execute("delete from borrow_bank where id='"+srs.getString("id")+"'");
					}
				}
				
				srs = db.getJdbc().queryForRowSet("select value from info_values where name='turn'");
				if(srs.next())
					db.getJdbc().execute("update info_values set value='"+(Integer.parseInt(srs.getString("value"))+1)+"' where name='turn'");
				
				break;
				
			case SECTOR_CHEMICAL_PLANT :
				
				for(int i=0;i<10;i++){
					System.out.println("dump process "+(i+1));
					try {
						Thread.sleep(TimeUnit.SECONDS.toMillis(1));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				srs = db.getJdbc().queryForRowSet("select user,turn,money,prob from req_borrow_bank");
//				srs2 = db.getJdbc().queryForRowSet("select value from info_values where name='interest'");
//				Random r = new Random();
//				String date = "", inc = "";
//			
//				srs2.next();
//				int res = 0, counter;
//				double d = 0;
//				while(srs.next()){
//					d = srs.getDouble("prob")*100;
//					res = r.nextInt(100);
//					if(res<d){
//						System.out.println(res+" "+d);
//						date = dateNow("ddMMyy");
//						srs3 = db.getJdbc().queryForRowSet("select count(id) from borrow_bank where substr(id,3,6)='"+date+"'");
//						srs3.next();
//						counter = srs3.getInt(1)+1;
//						if(counter > 999)
//							inc = ""+counter;
//						else if(counter > 99)
//							inc = "0"+counter;
//						else if(counter > 9)
//							inc = "00"+counter;
//						else inc = "000"+counter;
//						db.getJdbc().execute("insert into borrow_bank values ('"+BusinessGameService.KEY_BORROW_BANK+date+inc+"','"+srs.getString("user")+"','"+srs.getString("turn")+"','"+srs.getDouble("money")+"','"+srs2.getDouble("value")+"')");
//						
//						srs3 = db.getJdbc().queryForRowSet("select money from user where name='"+srs.getString("user")+"'");
//						srs3.next();
//						db.getJdbc().execute("update user set money='"+(srs3.getDouble("money")+srs.getDouble("money"))+"' where name='"+srs.getString("user")+"'");
//					}
//				}
//				db.getJdbc().execute("delete from req_borrow_bank");
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
}
