package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.MarketEmployee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class MarketEmployeeAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<MarketEmployee> employees;
	private static LayoutInflater inflater = null;
	
	public MarketEmployeeAdapter(Context c, ArrayList<MarketEmployee> e) {
		context = c;
		employees = e;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return employees.size();
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
			v = inflater.inflate(R.layout.listrow_market_employee, null);
		}
		TextView txtEmployee = (TextView)v.findViewById(R.id.txt_employee),
				txtPrice = (TextView)v.findViewById(R.id.txt_price);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		Button btnHire = (Button)v.findViewById(R.id.btn_hire);
		
		MarketEmployee employee = employees.get(pos);
		
		txtEmployee.setText(employee.getEmployee());
		txtPrice.setText("Price to hire : "+employee.getPrice()+" ZE");
		rateQuality.setRating(employee.getQuality());
		btnHire.setOnClickListener(new OnClickHandler(employee.getId()));
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id;
		
		public OnClickHandler(String i){
			id = i;
		}
		
		@Override
		public void onClick(View v) {
			android.util.Log.d("Equipment Adapter", "Button with id list "+id+" pressed");
		}
	}
}
