package com.ardhi.businessgame.activities.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.MyProfileTabActivity;
import com.ardhi.businessgame.models.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter{

	private MyProfileTabActivity act;
	private ArrayList<Message> messages;
	private static LayoutInflater inflater = null;
	
	public MessageAdapter(MyProfileTabActivity a, ArrayList<Message> m) {
		act = a;
		messages = m;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return messages.size();
	}

	public Object getItem(int pos) {
		return pos;
	}

	public long getItemId(int pos) {
		return pos;
	}

	public View getView(int pos, View newView, ViewGroup parent) {
		View v = newView;
		if(newView == null){
			v = inflater.inflate(R.layout.listrow_message, null);
		}
		ImageView img = (ImageView)v.findViewById(R.id.img);
		TextView txtSender = (TextView)v.findViewById(R.id.txt_sender),
				txtMessage = (TextView)v.findViewById(R.id.txt_message);
		
		Message message = messages.get(pos);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(message.getId().substring(2, 15)));
		SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss");
		
		if(message.isUnread())
			img.setImageResource(R.drawable.ic_message_unread);
		else img.setImageResource(R.drawable.ic_message_read);
		txtSender.setText("From : "+message.getSender()+" in "+format.format(c.getTime()));
		if(message.getMessage().length() > 5)
			txtMessage.setText(message.getMessage().substring(0, 4)+"..");
		else txtMessage.setText(message.getMessage());
		
		
		return v;
	}
}
