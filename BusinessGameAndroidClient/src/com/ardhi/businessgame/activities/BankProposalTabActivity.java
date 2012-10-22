package com.ardhi.businessgame.activities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.ardhi.businessgame.R;
import com.ardhi.businessgame.models.BusinessSectorInfo;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.DBAccess;
import com.ardhi.businessgame.services.SystemService;
import com.ardhi.businessgame.services.TimeSync;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class BankProposalTabActivity extends TabActivity {
	private DBAccess db;
	private EditText zone, money, nextTurn, total;
	private User user;
	private TimeSync timeSync;
	private Handler h;
	private Thread t;
	private ProgressDialog progressDialog;
	private ArrayList<String> sectors;
	private ArrayList<Double> prices;
	private ArrayList<BusinessSectorInfo> bsis;
	private double pCost,ieCost,ieTurn,eCost,eTurn,rCost,sCost;
	private int turn = 1;
	private boolean storage;
	private Spinner spinSector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_bankproposal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        db = new DBAccess(this);
        zone = (EditText)findViewById(R.id.zone);
        money = (EditText)findViewById(R.id.money);
        nextTurn = (EditText)findViewById(R.id.next_turn);
        spinSector = (Spinner)findViewById(R.id.spin_sector);
        total = (EditText)findViewById(R.id.txt_sum_borrow);
        
        ((Button)findViewById(R.id.btn_submit)).setOnClickListener(onClickHandler);
		
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
        
        h = new Handler();
        timeSync = new TimeSync(h, nextTurn, money, db);
        bindService(new Intent(this, SystemService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        if(CommunicationService.isOnline(this)){
        	progressDialog = ProgressDialog.show(this, "", "Loading Bank's data..");
    		new LoadBankData().execute();
        } else {
        	Toast.makeText(getApplicationContext(), "Device is offline..", Toast.LENGTH_SHORT).show();
        	finish();
        }
    }
    
    @Override
	protected void onPause() {
		super.onPause();
		timeSync.setThreadWork(false);
		t.interrupt();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timeSync.setThreadWork(true);
		t = new Thread(timeSync);
		t.start();
		user = db.getUser();
		zone.setText(user.getZone());
        money.setText(user.getMoney()+" ZE");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tab_bankproposal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	public Dialog onCreateDialog(int id){
		switch (id) {
		case 1:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textView = factory.inflate(R.layout.question_borrow, null);
			return new AlertDialog.Builder(this)
				.setView(textView)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doPositiveClickDialog();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.create();

		default:
			break;
		}
		return null;
	}
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			timeSync.setGlobalServices(null);
			timeSync.setServiceBound(false);
		}
		
		public void onServiceConnected(ComponentName name, IBinder binder) {
			timeSync.setGlobalServices(((SystemService.MyBinder)binder).getService());
			timeSync.setServiceBound(true);
		}
	};
	
	private AdapterView.OnItemSelectedListener onItemSelectedHandler = new AdapterView.OnItemSelectedListener(){
    	public void onItemSelected(AdapterView<?> spinner, View v, int i, long id) {
    		int tmp = getTabHost().getCurrentTab();
    		getTabHost().setCurrentTab(0);
    		setLayout(i);
    		getTabHost().setCurrentTab(tmp);
    		storage = true;
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
    };
    
    private View.OnClickListener onClickHandler = new View.OnClickListener(){
		public void onClick(View v) {
			showDialog(1);
		}
    };
    
    private void doPositiveClickDialog(){
    	if(CommunicationService.isOnline(this)){
    		progressDialog = ProgressDialog.show(this, "", "Submitting the proposal, please wait...");
    		new SubmitProposal().execute();
    	} else {
    		Toast.makeText(this, "Device is offline..", Toast.LENGTH_SHORT).show();
    	}
    }

    private void loadSector() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(BankProposalTabActivity.this, android.R.layout.simple_spinner_item, sectors);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinSector.setAdapter(adapter);
		spinSector.setOnItemSelectedListener(onItemSelectedHandler);
	}
	
	
	private void setLayout(int pos){
		TabHost.TabSpec spec;
		
		getTabHost().clearAllTabs();
		
		spec = getTabHost().newTabSpec("Equipment").setIndicator("Equipment", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabEquipment(pos));
        getTabHost().addTab(spec);
        
		spec = getTabHost().newTabSpec("Employee").setIndicator("Employee", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabEmployee(pos));
        getTabHost().addTab(spec);
        
		spec = getTabHost().newTabSpec("Product").setIndicator("Product", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabProduct(pos));
        getTabHost().addTab(spec);
        
		spec = getTabHost().newTabSpec("Additional").setIndicator("Additional", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new TabAdditional());
        getTabHost().addTab(spec);
        getTabHost().setCurrentTab(1);
        getTabHost().setCurrentTab(2);
        getTabHost().setCurrentTab(3);
        getTabHost().setCurrentTab(0);
//        android.util.Log.d("eq", ieCost+"+"+eCost+"+"+rCost);
        total.setText((ieCost+eCost+rCost+ieTurn+eTurn+sCost+prices.get(spinSector.getSelectedItemPosition())+pCost)+" ZE");
	}
	
	private class TabEquipment implements TabHost.TabContentFactory {
		BusinessSectorInfo bsi;
		Context c;
		
		public TabEquipment(int p){
			bsi = bsis.get(p);
			c = BankProposalTabActivity.this;
		}
		@Override
		public View createTabContent(String tag) {
//			android.util.Log.d("eq", "jalan");
			ScrollView layout = new ScrollView(c);
			TableLayout detailsData = new TableLayout(c);
			TableRow.LayoutParams params = new TableRow.LayoutParams();
			TableRow row = new TableRow(c);
			TextView text = new TextView(c);
			double tmp1, tmp2, sumPrice = 0, sumCostTurn = 0;
			
			detailsData.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			
			text.setText("Equipment");
			params.setMargins(5, 5, 5, 5);
			
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("Unit(s)");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("Price");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("Cost per turn");
			text.setLayoutParams(params);
			row.addView(text);
			
			detailsData.addView(row);
			
			for(int i=0;i<bsi.getListIndustrialEquipment().size();i++){
				row = new TableRow(c);
				text = new TextView(c);
				text.setText(bsi.getListIndustrialEquipment().get(i).getType());
				text.setLayoutParams(params);
				row.addView(text);
				
				text = new TextView(c);
				text.setText(bsi.getListIndustrialEquipment().get(i).getQuantity()+"");
				tmp1 = bsi.getListIndustrialEquipment().get(i).getQuantity();
				text.setLayoutParams(params);
				row.addView(text);
				
				text = new TextView(c);
				text.setText(bsi.getListIndustrialEquipment().get(i).getBasePrice()+" ZE");
				tmp2 = tmp1*bsi.getListIndustrialEquipment().get(i).getBaseOp();
				tmp1 *= bsi.getListIndustrialEquipment().get(i).getBasePrice();
				text.setLayoutParams(params);
				row.addView(text);
				
				text = new TextView(c);
				text.setText(bsi.getListIndustrialEquipment().get(i).getBaseOp()+" ZE");
				text.setLayoutParams(params);
				row.addView(text);
				
				detailsData.addView(row);
				sumPrice += tmp1;
				sumCostTurn += tmp2;
			}
			
			row = new TableRow(c);
			
			text = new TextView(c);
			text.setText("Subtotal");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			sumPrice = new BigDecimal(Double.valueOf(sumPrice)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
			text.setText(sumPrice+" ZE");
			text.setLayoutParams(params);
			text.setId(10);
			android.util.Log.d("id", ""+text.getId());
			row.addView(text);
			
			text = new TextView(c);
			sumCostTurn = new BigDecimal(Double.valueOf(sumCostTurn)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
			text.setText(sumCostTurn+" ZE");
			text.setLayoutParams(params);
			row.addView(text);
			detailsData.addView(row);
			
			ieCost = sumPrice;
			ieTurn = sumCostTurn;
			
			layout.addView(detailsData);
			return layout;
		}
	}
	
	private class TabEmployee implements TabHost.TabContentFactory {
		BusinessSectorInfo bsi;
		Context c;
		
		public TabEmployee(int p){
			bsi = bsis.get(p);
			c = BankProposalTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
//			android.util.Log.d("em", "jalan");
			ScrollView layout = new ScrollView(c);
			TableLayout detailsData = new TableLayout(c);
			TableRow.LayoutParams params = new TableRow.LayoutParams();
			TableRow row;
			TextView text;
			double tmp1, tmp2, sumPrice = 0, sumCostTurn = 0;
			
			detailsData.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			params.setMargins(5, 5, 5, 5);
			
			text = new TextView(c);
			text.setText("Employee");
			row = new TableRow(c);
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("Unit(s)");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("Price");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("Cost per turn");
			text.setLayoutParams(params);
			row.addView(text);
			
			detailsData.addView(row);
			
			for(int i=0;i<bsi.getListEmployee().size();i++){				
				row = new TableRow(c);
				text = new TextView(c);
				text.setText(bsi.getListEmployee().get(i).getType());
				text.setLayoutParams(params);
				row.addView(text);
				
				text = new TextView(c);
				text.setText(bsi.getListEmployee().get(i).getQuantity()+"");
				tmp1 = bsi.getListEmployee().get(i).getQuantity();
				text.setLayoutParams(params);
				row.addView(text);
				
				text = new TextView(c);
				text.setText(bsi.getListEmployee().get(i).getBasePrice()+" ZE");
				tmp2 = tmp1 * bsi.getListEmployee().get(i).getBaseOp();
				tmp1 *= bsi.getListEmployee().get(i).getBasePrice();
				text.setLayoutParams(params);
				row.addView(text);
				
				text = new TextView(c);
				text.setText(bsi.getListEmployee().get(i).getBaseOp()+" ZE");
				text.setLayoutParams(params);
				row.addView(text);
				
				detailsData.addView(row);
				sumPrice += tmp1;
				sumCostTurn += tmp2;
			}
			
			row = new TableRow(c);
			text = new TextView(c);
			text.setText("Subtotal");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText("");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			sumPrice = new BigDecimal(Double.valueOf(sumPrice)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
			text.setText(sumPrice+" ZE");
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			sumCostTurn = new BigDecimal(Double.valueOf(sumCostTurn)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
			text.setText(sumCostTurn+" ZE");
			text.setLayoutParams(params);
			row.addView(text);
			detailsData.addView(row);
			
			eCost = sumPrice;
			eTurn = sumCostTurn;
			
			layout.addView(detailsData);
			return layout;
		}
	}
	
	private class TabProduct implements TabHost.TabContentFactory{
		BusinessSectorInfo bsi;
		Context c;
		
		public TabProduct(int p){
			bsi = bsis.get(p);
			c = BankProposalTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
//			android.util.Log.d("pr", "jalan");
			ScrollView layout = new ScrollView(c);
			LinearLayout subLayout = new LinearLayout(c), turnLayout = new LinearLayout(c);
			
			subLayout.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			subLayout.setOrientation(LinearLayout.VERTICAL);
			
			turnLayout.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			turnLayout.setOrientation(LinearLayout.HORIZONTAL);
			
			TextView text = new TextView(c);
			text.setText(R.string.turn);
			turnLayout.addView(text);
			
			Spinner spinTurn = new Spinner(c);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(c, R.array.turn_prod, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinTurn.setAdapter(adapter);
			turnLayout.addView(spinTurn);
			subLayout.addView(turnLayout);
			
			TableLayout detailsData = new TableLayout(c);
			detailsData.setLayoutParams(new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			subLayout.addView(detailsData);
			layout.addView(subLayout);
			
			setLayoutProduct(detailsData, bsi, 0, BankProposalTabActivity.this);
			spinTurn.setOnItemSelectedListener(new OnItemSelectedHandlerTurn(detailsData, bsi));
			
			return layout;
		}
	}
	
	private void setLayoutProduct(TableLayout detailsData, BusinessSectorInfo bsi, int pos, Context c){
		android.util.Log.d("pr sub", "jalan");
		double tmp1, sumPrice = 0, total1 = 0;
		
		detailsData.removeAllViews();
		
		TextView text = new TextView(c);
		text.setText("Input");
		TableRow row = new TableRow(c);
		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.setMargins(5, 5, 5, 5);
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("Unit(s)");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("Price");
		text.setLayoutParams(params);
		row.addView(text);
		
		detailsData.addView(row);
		
		for(int i=0;i<bsi.getListInput().size();i++){				
			row = new TableRow(c);
			text = new TextView(c);
			text.setText(bsi.getListInput().get(i).getType());
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText(""+(pos+1)*bsi.getListInput().get(i).getSize());
			tmp1 = (pos+1)*bsi.getListInput().get(i).getSize();
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText(bsi.getListInput().get(i).getBasePrice()+" ZE");
			tmp1 *= bsi.getListInput().get(i).getBasePrice();
			text.setLayoutParams(params);
			row.addView(text);
			
			detailsData.addView(row);
			sumPrice += tmp1;
		}
		
		row = new TableRow(c);
		
		text = new TextView(c);
		text.setText("Subtotal");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		sumPrice = new BigDecimal(Double.valueOf(sumPrice)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		text.setText(sumPrice+" ZE");
		text.setLayoutParams(params);
		row.addView(text);
		
		detailsData.addView(row);
		
		rCost = sumPrice;
		if(storage)
			total.setText((ieCost+eCost+rCost+ieTurn+eTurn+prices.get(spinSector.getSelectedItemPosition())+pCost+sCost)+" ZE");
		else total.setText((ieCost+eCost+rCost+ieTurn+eTurn+prices.get(spinSector.getSelectedItemPosition())+pCost)+" ZE");
		
		sumPrice = 0;
		
		text = new TextView(c);
		text.setText("Output");
		row = new TableRow(c);
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("Unit(s)");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("Price");
		text.setLayoutParams(params);
		row.addView(text);
		
		detailsData.addView(row);
		
		for(int i=0;i<bsi.getListOutput().size();i++){				
			row = new TableRow(c);
			text = new TextView(c);
			text.setText(bsi.getListOutput().get(i).getType());
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText(""+(pos+1)*bsi.getListOutput().get(i).getSize());
			tmp1 = (pos+1)*bsi.getListOutput().get(i).getSize();
			text.setLayoutParams(params);
			row.addView(text);
			
			text = new TextView(c);
			text.setText(bsi.getListOutput().get(i).getBasePrice()+" ZE");
			tmp1 *= bsi.getListOutput().get(i).getBasePrice();
			text.setLayoutParams(params);
			row.addView(text);
			
			detailsData.addView(row);
			sumPrice += tmp1;
		}
		
		row = new TableRow(c);
		
		text = new TextView(c);
		text.setText("Subtotal");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		sumPrice = new BigDecimal(Double.valueOf(sumPrice)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
		text.setText(sumPrice+" ZE");
		text.setLayoutParams(params);
		row.addView(text);
		
		detailsData.addView(row);
		
		total1 = sumPrice - total1;
		
		row = new TableRow(c);
		
		text = new TextView(c);
		text.setText("Gross Margin");
		text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		text.setTextColor(0xffffffff);
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setText("");
		text.setLayoutParams(params);
		row.addView(text);
		
		text = new TextView(c);
		text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		text.setTextColor(0xffffffff);
		text.setText(total1+" ZE");
		text.setLayoutParams(params);
		row.addView(text);
		detailsData.addView(row);
	}
	
	private class TabAdditional implements TabHost.TabContentFactory{
		Context c;
		
		public TabAdditional(){
			c = BankProposalTabActivity.this;
		}
		
		@Override
		public View createTabContent(String tag) {
			ScrollView layout = new ScrollView(c);
			TableLayout detailsData = new TableLayout(c);
			TableRow row = new TableRow(c);
			TextView text;
			EditText edit;
			
			detailsData.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			
			text = new TextView(c);
			text.setText(R.string.build_cost);
			row.addView(text);
			
			edit = new EditText(c);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0xff000000);
			edit.setTextColor(0xffffffff);
			edit.setText(prices.get(spinSector.getSelectedItemPosition())+" ZE");
			edit.setId(100);
			row.addView(edit);
			detailsData.addView(row);
			
			text = new TextView(c);
			text.setText(R.string.prop_cost);
			row = new TableRow(c);
			row.addView(text);
			
			edit = new EditText(c);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0xff000000);
			edit.setTextColor(0xffffffff);
			edit.setText(pCost+" ZE");
			edit.setId(200);
			row.addView(edit);
			detailsData.addView(row);
			
			CheckBox checkStorage = new CheckBox(c);
			checkStorage.setText(R.string.buy_storage);
			checkStorage.setLayoutParams(new TableRow.LayoutParams(1));
			checkStorage.setChecked(true);
			checkStorage.setOnCheckedChangeListener(new OnCheckedChangeHandler());
			
			row = new TableRow(c);
			row.addView(checkStorage);
			detailsData.addView(row);
			
			row = new TableRow(c);
			text = new TextView(c);
			text.setText(R.string.turn_cost);
			row.addView(text);
			
			edit = new EditText(c);
			edit.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			edit.setFocusable(false);
			edit.setBackgroundColor(0xff000000);
			edit.setTextColor(0xffffffff);
			edit.setText((ieTurn+eTurn)+" ZE");
			row.addView(edit);
			detailsData.addView(row);
			
			layout.addView(detailsData);
			return layout;
		}
		
	}
	
	private class OnItemSelectedHandlerTurn implements AdapterView.OnItemSelectedListener {
		TableLayout detailsData;
		Context c;
		BusinessSectorInfo bsi;
		
		public OnItemSelectedHandlerTurn(TableLayout t, BusinessSectorInfo b){
			detailsData = t;
			c = BankProposalTabActivity.this;
			bsi = b;
		}
		@Override
		public void onItemSelected(AdapterView<?> spinner, View v, int pos, long id) {
			setLayoutProduct(detailsData, bsi, pos, c);
			turn = pos+1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class OnCheckedChangeHandler implements CompoundButton.OnCheckedChangeListener{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			storage = isChecked;
			if(isChecked){
				total.setText((ieCost+eCost+rCost+ieTurn+eTurn+prices.get(spinSector.getSelectedItemPosition())+pCost+sCost)+" ZE");
			} else {
				total.setText((ieCost+eCost+rCost+ieTurn+eTurn+prices.get(spinSector.getSelectedItemPosition())+pCost)+" ZE");
			}
			android.util.Log.d("bool storage", ""+storage);
		}
	};
	
	private class LoadBankData extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_BANK_DATA+"&user="+user.getName());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.toString().equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.toString().equals("0")){
				Toast.makeText(getApplicationContext(), "Internal server error..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.toString().equals("1")){
				Toast.makeText(getApplicationContext(), "You have submit a proposal. Please wait until next turn..", Toast.LENGTH_SHORT).show();
				finish();
			} else if(res.toString().equals("2")){
				Toast.makeText(getApplicationContext(), "You still have loan. Please pay your debt first..", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray(),
						array3 = parser.parse(new Gson().fromJson(array.get(4), String.class)).getAsJsonArray();
				sectors = new ArrayList<String>();
				for(int i=0;i<array1.size();i++){
					sectors.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				prices = new ArrayList<Double>();
				for(int i=0;i<array2.size();i++){
					prices.add(new Gson().fromJson(array2.get(i), Double.class));
				}
				
				pCost = new Gson().fromJson(array.get(2), Double.class);
				sCost = new Gson().fromJson(array.get(3), Double.class);
				storage = false;
				
				bsis = new ArrayList<BusinessSectorInfo>();
				for(int i=0;i<array3.size();i++){
					bsis.add(new Gson().fromJson(array3.get(i), BusinessSectorInfo.class));
				}
				
				parser = null;
				array = null;
				array1 = null;
				array2 = null;
				array3 = null;
				
				loadSector();
			}
		}
	}
	
	private class SubmitProposal extends AsyncTask<String, Void, Object>{

		@Override
		protected Object doInBackground(String... params) {
			HashMap<String, String> postParameters = new HashMap<String, String>();
			postParameters.put("user", user.getName());
			postParameters.put("sector", spinSector.getSelectedItem().toString());
			postParameters.put("turn", ""+turn);
			postParameters.put("storage", ""+storage);
			
			String res = null;
			try {
				res = CommunicationService.post(CommunicationService.POST_SUBMIT_PROPOSAL, postParameters);
			} catch (Exception e) {
				e.printStackTrace();
				res = null;
			}
			
			postParameters = null;
			
			return res;
		}
		
		@Override
		protected void onPostExecute(Object res) {
			progressDialog.dismiss();
			if(res == null){
				Toast.makeText(getApplicationContext(), "No response from server. Try again later.", Toast.LENGTH_SHORT).show();
			} else if(res.equals("-1")){
				Toast.makeText(getApplicationContext(), "Server is not ready..", Toast.LENGTH_SHORT).show();
			} else if(res.equals("0")){
				Toast.makeText(getApplicationContext(), "Internal server error..", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "The proposal has been submitted. Please wait until this turn finished.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
    }
}
