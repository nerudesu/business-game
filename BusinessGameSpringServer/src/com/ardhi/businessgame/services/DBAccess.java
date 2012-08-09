package com.ardhi.businessgame.services;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class DBAccess {
	private JdbcTemplate jdbcTemp;
	private static DBAccess instance;
	
	private DBAccess(DataSource ds){
		jdbcTemp = new JdbcTemplate(ds);
	}
	
	public static DBAccess getInstance(DataSource ds){
		if(instance == null){
			instance = new DBAccess(ds);
		}
		return instance;
	}
	
	public static DBAccess getReadyInstance(){
		return instance;
	}
	
	public JdbcTemplate getJdbc(){
		return jdbcTemp;
	}
}
