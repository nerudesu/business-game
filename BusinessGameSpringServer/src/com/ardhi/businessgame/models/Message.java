package com.ardhi.businessgame.models;

public class Message {
	private String id,sender,message;
	private boolean unread;
	
	public Message(String i,String s,String m,boolean u){
		setId(i);
		setSender(s);
		setMessage(m);
		setUnead(u);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String i) {
		id = i;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String s) {
		sender = s;
	}
	public boolean isUnread() {
		return unread;
	}
	public void setUnead(boolean u) {
		unread = u;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String m) {
		message = m;
	}
}
