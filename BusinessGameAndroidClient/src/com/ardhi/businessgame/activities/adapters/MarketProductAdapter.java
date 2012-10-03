package com.ardhi.businessgame.activities.adapters;

import java.util.ArrayList;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.activities.MarketTabContentActivity;
import com.ardhi.businessgame.models.MarketProduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class MarketProductAdapter extends BaseAdapter{

	private MarketTabContentActivity act;
	private ArrayList<MarketProduct> products;
	private static LayoutInflater inflater = null;
	private String user;
	
	public MarketProductAdapter(MarketTabContentActivity a, ArrayList<MarketProduct> p, String u) {
		act = a;
		products = p;
		inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		user = u;
	}
	
	public int getCount() {
		return products.size();
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
			v = inflater.inflate(R.layout.listrow_market_product, null);
		}
		TextView txtProduct = (TextView)v.findViewById(R.id.txt_product),
				txtUser = (TextView)v.findViewById(R.id.txt_user),
				txtSize = (TextView)v.findViewById(R.id.txt_size);
		RatingBar rateQuality = (RatingBar)v.findViewById(R.id.rate_quality);
		Button btnBuy = (Button)v.findViewById(R.id.btn_buy);
		
		MarketProduct product = products.get(pos);
		
		txtProduct.setText(product.getProduct());
		txtUser.setText("Seller : "+product.getUser());
		txtSize.setText("Stock : "+product.getSize()+" CBM ("+product.getPrice()+" ZE/CBM)");
		rateQuality.setRating(product.getQuality());
		btnBuy.setOnClickListener(new OnClickHandler(product.getId(), product.getSize(), product.getPrice(), product.getQuality()));
		btnBuy.setClickable(!user.equals(product.getUser()));
		btnBuy.setEnabled(!user.equals(product.getUser()));
		return v;
	}
	
	private class OnClickHandler implements View.OnClickListener{
		private String id;
		private double size, price;
		private int quality;
		
		public OnClickHandler(String i, double s, double p, int q){
			id = i;
			size = s;
			price = p;
			quality = q;
		}
		
		@Override
		public void onClick(View v) {
			act.dialog(id, 1, "", size, price, 0, quality, 0).show();
		}
	}
}
