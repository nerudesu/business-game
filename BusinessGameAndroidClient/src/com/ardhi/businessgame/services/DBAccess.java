package com.ardhi.businessgame.services;

import java.util.HashMap;

import com.ardhi.businessgame.models.SavedUser;
import com.ardhi.businessgame.models.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAccess extends SQLiteOpenHelper {
	private static final String DB_NAME = "user.db";
	private static final int VER = 2;
	
	public DBAccess(Context context) {
		super(context, DB_NAME, null, VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table user (name varchar primary key, email varchar, dob varchar, about text, avatar text, money double, rep bigint, zone varchar, level int)");
		db.execSQL("create table saved_user (name varchar primary key, pass varchar, auto_log boolean)");
		db.execSQL("create table user_storage(zone varchar primary key, id varchar)");
//		db.execSQL("create table user (name varchar primary key, email varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists saved_user");
		db.execSQL("drop table if exists user_storage");
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
		if(db.insert("saved_user", null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public SavedUser getSavedUserData(){
		SavedUser user = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("saved_user", null, null, null, null, null, null, null);
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
		if(db.delete("saved_user", null, null) > 0)
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
		val.put("avatar", user.getAvatar());
		val.put("money", user.getMoney());
		val.put("rep", user.getRep());
		val.put("zone", user.getZone());
		val.put("level", user.getLevel());
		android.util.Log.d("level", ""+user.getLevel());
		if(db.insert("user", null, val) > 0)
			success = true;
		db.close();
		
		for(String key : user.getStorages().keySet()){
			addUserStorage(key, user.getStorages().get(key));
		}
		
		return success;
	}
	
	public boolean addUserStorage(String zone, String id){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("zone", zone);
		val.put("id", id);
		if(db.insert("user_storage", null, val) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public User getUser(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor1 = db.query("user", null, null, null, null, null, null, null),
				cursor2 = db.query("user_storage", null, null, null, null, null, null, null);
		if(cursor1 != null && cursor2 != null){
			HashMap<String, String> storages = new HashMap<String, String>();
			while(cursor2.moveToNext()){
				storages.put(cursor2.getString(0), cursor2.getString(1));
			}
			if(cursor1.moveToFirst()){
				User user = new User(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), cursor1.getString(4), Double.parseDouble(cursor1.getString(5)), Long.parseLong(cursor1.getString(6)), cursor1.getString(7), storages, Integer.parseInt(cursor1.getString(8)));
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
		if(db.delete("user_storage", null, null) > 0)
			success = true;
		db.close();
		
		db = getReadableDatabase();
		if(db.delete("user", null, null) > 0)
			success = true;
		db.close();
		return success;
	}
	
	public boolean updateUserData(User user){
		boolean success = false;
		SQLiteDatabase db = getReadableDatabase();
		ContentValues val = new ContentValues();
		val.put("name", user.getName());
		val.put("email", user.getEmail());
		val.put("dob", user.getDob());
		val.put("about", user.getAbout());
		val.put("avatar", user.getAvatar());
		val.put("money", user.getMoney());
		val.put("rep", user.getRep());
		val.put("zone", user.getZone());
		val.put("level", user.getLevel());
		if (db.update("user", val, "name=?", new String[]{user.getName()}) > 0){
			success = true;
		} else success = false;
		
		db.close();
		
		return success;
	}
//	
//	public User getUser(String id){
//		SQLiteDatabase db = getReadableDatabase();
//		Cursor cursor = db.query("tes", new String[]{"id","name","val"}, "id=?", new String[]{id}, null, null, null, null);
//		if(!cursor.equals(null)){
//			if(cursor.moveToFirst()){
//				User user = new User(cursor.getString(0), cursor.getString(1), Double.parseDouble(cursor.getString(2)));
//				return user;
//			} else {
//				return null;
//			}
//		} else {
//			return null;
//		}
//	}	
}
