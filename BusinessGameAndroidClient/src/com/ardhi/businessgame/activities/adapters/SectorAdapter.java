package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ardhi.businessgame.R;

public class SectorAdapter extends BaseAdapter{

	private Activity act;
	private ArrayList<String> sectors,zones;
	private ArrayList<Double> efficiency;
	private ArrayList<Integer> effectivity;
	private static LayoutInflater inflater = null;
	
	public SectorAdapter(Activity a, ArrayList<String> s, ArrayList<String> z, ArrayList<Double> ec, ArrayList<Integer> et) {
		act = a;
		sectors = s;
		zones = z;
		efficiency = ec;
		effectivity = et;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return sectors.size();
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
			v = inflater.inflate(R.layout.listrow_sector_owned, null);
		}
		TextView txtSector = (TextView)v.findViewById(R.id.txt_sector),
				txtZone = (TextView)v.findViewById(R.id.txt_zone),
				txtEfficiency = (TextView)v.findViewById(R.id.txt_efficiency),
				txtEffectivity = (TextView)v.findViewById(R.id.txt_effectivity);
		
		String sector = sectors.get(pos), zone = zones.get(pos);
		double efficient = efficiency.get(pos);
		int effective = effectivity.get(pos);
		
		txtSector.setText(sector);
		txtZone.setText("Zone : "+zone);
		txtEfficiency.setText("Efficiency : "+efficient);
		txtEffectivity.setText("Effectivity : "+effective+"x");
		return v;
	}
}
