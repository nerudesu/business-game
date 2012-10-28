package com.phionsoft.zentriumph.services;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.phionsoft.zentriumph.models.Installment;
import com.phionsoft.zentriumph.models.SavedUser;
import com.phionsoft.zentriumph.models.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAccess extends SQLiteOpenHelper {
	private static final String DB_NAME = "user.db",
			TB_USER = "user",
			TB_SAVED = "saved",
			TB_STORAGE = "storage",
			TB_MARKET_LICENSE = "marketLicense",
			TB_SECTOR_BLUEPRINT = "sectorBlueprint",
			TB_INSTALLMENT = "installment";
	private static final int VER = 3;
	
	public DBAccess(Context context) {
		super(context, DB_NAME, null, VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+TB_USER+" (name varchar primary key, email varchar, dob varchar, about text, money double, prop_cost double, rep bigint, zone varchar, level int)");
		db.execSQL("create table "+TB_SAVED+" (name varchar primary key, pass varchar, auto_log boolean)");
		db.execSQL("create table "+TB_STORAGE+" (zone varchar primary key, id varchar)");
		db.execSQL("create table "+TB_MARKET_LICENSE+" (zone varchar primary key, id varchar)");
		db.execSQL("create table "+TB_SECTOR_BLUEPRINT+" (sector varchar primary key, id varchar, cost double)");
		db.execSQL("create table "+TB_INSTALLMENT+" (id varchar primary key, type varchar, zone varchar, efficiency double, effectivity double, draw int, active boolean)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("drop table if exists "+TB_USER);
		db.execSQL("drop table if exists "+TB_SAVED);
		db.execSQL("drop table if exists "+TB_STORAGE);
		db.execSQL("drop table if exists "+TB_MARKET_LICENSE);
		db.execSQL("drop table if exists "+TB_SECTOR_BLUEPRINT);
		db.execSQL("drop table if exists "+TB_INSTALLMENT);
		onCreate(db);
	}
	
	//CRUD
	public boolean saveUserData(SavedUser user){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("name", user.getUser());
		val.put("pass", user.getPass());
		val.put("auto_log", user.isAutoLog());
		if(db.insert(TB_SAVED, null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public SavedUser getSavedUserData(){
		SavedUser user = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TB_SAVED, null, null, null, null, null, null, null);
		if(!cursor.equals(null)){
			if(cursor.moveToFirst()){
				user = new SavedUser(cursor.getString(0), cursor.getString(1), cursor.getInt(2)>0 ? true : false);
			}
		}
		db.close();
		return user;
	}
	
	public boolean deleteSavedUserData(){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		if(db.delete(TB_SAVED, null, null) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public boolean addUser(User user){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("name", user.getName());
		val.put("email", user.getEmail());
		val.put("dob", user.getDob());
		val.put("about", user.getAbout());
		val.put("money", user.getMoney());
		val.put("prop_cost", user.getPropCost());
		val.put("rep", user.getRep());
		val.put("zone", user.getZone());
		val.put("level", user.getLevel());
		if(db.insert(TB_USER, null, val) > 0)
			success = true;
		db.close();
		
		for(String key : user.getStorages().keySet()){
			addUserStorage(key, user.getStorages().get(key));
		}
		
		android.util.Log.d("licenses", "wew "+user.getMarketLicenses());
		
		for(String key : user.getMarketLicenses().keySet()){
			addUserMarketLicense(key, user.getMarketLicenses().get(key));
		}

		for(String key : user.getSectorBlueprints().keySet()){
			addUserSectorBlueprintCost(key, user.getSectorBlueprints().get(key), user.getSectorCosts().get(key));
		}
		
		for(Installment i : user.getInstallments()){
			addUserInstallment(i);
		}
		
		return success;
	}
	
	public boolean addUserStorage(String zone, String id){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("zone", zone);
		val.put("id", id);
		if(db.insert(TB_STORAGE, null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public boolean addUserMarketLicense(String zone, String id){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("zone", zone);
		val.put("id", id);
		if(db.insert(TB_MARKET_LICENSE, null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public boolean addUserSectorBlueprintCost(String sector, String id, double cost){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("sector", sector);
		val.put("id", id);
		val.put("cost", cost);
		if(db.insert(TB_SECTOR_BLUEPRINT, null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public boolean addUserInstallment(Installment ins){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("id", ins.getId());
		val.put("type", ins.getInstallment());
		val.put("zone", ins.getZone());
		val.put("efficiency", ins.getEfficiency());
		val.put("effectivity", ins.getEffectivity());
		val.put("draw", ins.getDraw());
		val.put("active", ins.isActive());
		if(db.insert(TB_INSTALLMENT, null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public User getUser(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor1 = db.query(TB_USER, null, null, null, null, null, null, null),
				cursor2;
		if(cursor1 != null){
			HashMap<String, String> storages = new HashMap<String, String>(),
					marketLicenses = new HashMap<String, String>(),
					sectorBlueprints = new HashMap<String, String>();
			HashMap<String, Double> sectorCosts = new HashMap<String, Double>();
			ArrayList<Installment> installments = new ArrayList<Installment>();
			
			cursor2 = db.query(TB_STORAGE, null, null, null, null, null, null, null);
			while(cursor2.moveToNext()){
				storages.put(cursor2.getString(0), cursor2.getString(1));
			}
			
			cursor2 = db.query(TB_MARKET_LICENSE, null, null, null, null, null, null, null);
			while(cursor2.moveToNext()){
				marketLicenses.put(cursor2.getString(0), cursor2.getString(1));
			}
			
			cursor2 = db.query(TB_SECTOR_BLUEPRINT, null, null, null, null, null, null, null);
			while(cursor2.moveToNext()){
				sectorBlueprints.put(cursor2.getString(0), cursor2.getString(1));
				sectorCosts.put(cursor2.getString(0), cursor2.getDouble(2));
			}
			
			cursor2 = db.query(TB_INSTALLMENT, null, null, null, null, null, null, null);
			while(cursor2.moveToNext()){
//				android.util.Log.d("angka", ""+cursor2.getInt(5));
//				android.util.Log.d("hex", ""+Integer.toHexString(cursor2.getInt(5)));
//				android.util.Log.d("hex asli", "0x"+Integer.toHexString(cursor2.getInt(5)));
				installments.add(new Installment(cursor2.getString(0), cursor2.getString(1), cursor2.getString(2), cursor2.getDouble(3), cursor2.getDouble(4), "0x"+Integer.toHexString(cursor2.getInt(5)), cursor2.getInt(6)>0 ? true : false));
			}
			
			android.util.Log.d("insta", new Gson().toJson(installments));
			
			if(cursor1.moveToFirst()){
//				User user = new User(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), Double.parseDouble(cursor1.getString(4)), cursor1.getDouble(5), Long.parseLong(cursor1.getString(5)), cursor1.getString(6), Integer.parseInt(cursor1.getString(7)), storages, marketLicenses, sectorBlueprints, sectorCosts, installments);
				User user = new User(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), Double.parseDouble(cursor1.getString(4)), cursor1.getDouble(5), Long.parseLong(cursor1.getString(6)), cursor1.getString(7), Integer.parseInt(cursor1.getString(8)), storages, marketLicenses, sectorBlueprints, sectorCosts, installments);
				db.close();
				return user;
			} else {
				db.close();
				return null;
			}
		} else {
			db.close();
			return null;
		}
	}
	
	public boolean deleteUserData(){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		if(db.delete(TB_STORAGE, null, null) > 0)
			success = true;
		db.close();
		
		db = getReadableDatabase();
		if(db.delete(TB_MARKET_LICENSE, null, null) > 0)
			success = true;
		db.close();
		
		db = getReadableDatabase();
		if(db.delete(TB_SECTOR_BLUEPRINT, null, null) > 0)
			success = true;
		db.close();
		
		db = getReadableDatabase();
		if(db.delete(TB_INSTALLMENT, null, null) > 0)
			success = true;
		db.close();
		
		db = getReadableDatabase();
		if(db.delete(TB_USER, null, null) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public boolean updateUserData(User user){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("money", user.getMoney());
		val.put("prop_cost", user.getPropCost());
		val.put("rep", user.getRep());
		val.put("level", user.getLevel());
		if (db.update(TB_USER, val, "name=?", new String[]{user.getName()}) > 0){
			success = true;
		} else success = false;
		
		db.close();
		
		for(String sector : user.getSectorBlueprints().keySet()){
			if(updateUserSectorBlueprintCost(sector, user.getSectorCosts().get(sector)))
				success = true;
			else {
				success = false;
				break;
			}
		}
		
		for(Installment ins : user.getInstallments()){
			if(updateUserInstallment(ins))
				success = true;
			else {
				success = false;
				break;
			}
		}
		
		return success;
	}
	
	public boolean updateUserSectorBlueprintCost(String sector, double cost){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("cost", cost);
		if (db.update(TB_SECTOR_BLUEPRINT, val, "sector=?", new String[]{sector}) > 0){
			success = true;
		} else success = false;
		
		db.close();
		
		return success;
	}
	
	public boolean updateUserInstallment(Installment ins){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("type", ins.getInstallment());
		val.put("zone", ins.getZone());
		val.put("efficiency", ins.getEfficiency());
		val.put("effectivity", ins.getEffectivity());
		val.put("draw", ins.getDraw());
		val.put("active", ins.isActive());
		if (db.update(TB_INSTALLMENT, val, "id=?", new String[]{ins.getId()}) > 0){
			success = true;
		} else success = false;
		
		db.close();
		
		return success;
	}
}
