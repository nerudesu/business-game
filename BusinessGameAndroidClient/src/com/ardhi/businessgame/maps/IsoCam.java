package com.ardhi.businessgame.maps;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;

import com.ardhi.businessgame.activities.BankLobbyActivity;
import com.ardhi.businessgame.activities.HeadquarterTabActivity;
import com.ardhi.businessgame.activities.MarketTabActivity;
import com.ardhi.businessgame.activities.SectorActivity;
import com.ardhi.businessgame.activities.StorageTabActivity;
import com.ardhi.businessgame.models.User;
import com.ardhi.businessgame.services.CommunicationService;
import com.ardhi.businessgame.services.TimeSync;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class IsoCam extends AppListener{
	// resolution setting
    private static final int TARGET_WIDTH = 320;
    private static final int TARGET_HEIGHT = 480;
    private static final float UNIT_TO_PIXEL = TARGET_WIDTH * 0.15f;
    
    // user interface
    Texture hud;
    Texture hud_bottom;
    
    // base terrain
    Texture terrain;
    
    // production unit
    Texture water_well;
    Texture oil_well;
    Texture oil_refinery;
    Texture chemical_plant;
    Texture iron_mine;
    Texture silica_mine;
    Texture petrol_powerplant;
    
    // main building
    Texture headquarters;
    Texture storage;
    Texture market;
    Texture bank;
    
    OrthographicCamera cam;
    SpriteBatch batch;
    
    // second camera for font
    OrthographicCamera camUI;
    SpriteBatch batchUI;
    BitmapFont font;
    
    final Sprite[][] terrain_map = new Sprite[10][10];
    
    final Sprite[][] water_map = new Sprite[10][10];
    final Sprite[][] oilW_map = new Sprite[10][10];
    final Sprite[][] oilR_map = new Sprite[10][10];
    final Sprite[][] chemical_map = new Sprite[10][10];
    final Sprite[][] iron_map = new Sprite[10][10];
    final Sprite[][] silica_map = new Sprite[10][10];
    final Sprite[][] petrol_map = new Sprite[10][10];
    
    HashMap<String, Sprite> sectorMaps = new HashMap<String, Sprite>();
    
    final Sprite[][] hq_map = new Sprite[10][10];
    final Sprite[][] storage_map = new Sprite[10][10];
    final Sprite[][] market_map = new Sprite[10][10];
    final Sprite[][] bank_map = new Sprite[10][10];
    
    final Matrix4 matrix = new Matrix4();
    
    private ActionResolver res;
    
    private User user;
    private TimeSync timeSync;
    private Thread t;
    private ArrayList<String> sectors = new ArrayList<String>();
    private ArrayList<Double> costs = new ArrayList<Double>();
    private Bundle b;
    private double price;
    
    public IsoCam(ActionResolver a, User u, TimeSync t){
    	res = a;
    	user = u;
    	timeSync = t;
    }
    
    @Override
    public void resume() {
    	// TODO Auto-generated method stub
    	timeSync.setThreadWork(true);
		t = new Thread(timeSync);
		t.start();
//		Gdx.app.postRunnable(t);
		
    	res.startProgressDialog("Loading");
        new LoadSectorOwned().execute();
    }
    
    @Override
    public void pause() {
    	// TODO Auto-generated method stub
    	timeSync.setThreadWork(false);
		t.interrupt();
    }
    
    @Override
    public void create () {    	
    	// user interface
    	hud = new Texture(Gdx.files.internal("ui/hud.png"));
    	hud_bottom = new Texture(Gdx.files.internal("ui/hud_bottom.png"));
    	
		// base terrain
        terrain = new Texture(Gdx.files.internal("terrain/seamless_grass.jpg"));
        
        // production unit
        water_well = new Texture(Gdx.files.internal("units/water_well.png"));
        oil_well = new Texture(Gdx.files.internal("units/oil_well.png"));
        oil_refinery = new Texture(Gdx.files.internal("units/oil_refinery.png"));
        chemical_plant = new Texture(Gdx.files.internal("units/chemical_plant.png"));
        iron_mine = new Texture(Gdx.files.internal("units/iron_mine.png"));
        silica_mine = new Texture(Gdx.files.internal("units/silica_mine.png"));
        petrol_powerplant = new Texture(Gdx.files.internal("units/petrol_powerplant.png"));
        
        // main building
        headquarters = new Texture(Gdx.files.internal("main/headquarters.png"));
        storage = new Texture(Gdx.files.internal("main/storage.png"));
        market = new Texture(Gdx.files.internal("main/market.png"));
        bank = new Texture(Gdx.files.internal("main/bank.png"));
        
        float unitsOnX = android.util.FloatMath.sqrt(2) * TARGET_WIDTH / (UNIT_TO_PIXEL);
        float pixelsOnX = Gdx.graphics.getWidth() / unitsOnX;
        float unitsOnY = Gdx.graphics.getHeight() / pixelsOnX;
        cam = new OrthographicCamera(unitsOnX, unitsOnY, 30);
        cam.position.mul(30);
        cam.near = 1;
        cam.far = 1000;
        cam.zoom = 0.35f;
        matrix.setToRotation(new Vector3(1, 0, 0), 90);
        batch = new SpriteBatch();
        
        // setup ui camera
        camUI =  new OrthographicCamera(TARGET_WIDTH, TARGET_HEIGHT);
        batchUI = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("font/roboto.fnt"), Gdx.files.internal("font/roboto.png"), false);

        for (int z = 0; z < 10; z++) {
                for (int x = 0; x < 10; x++) {
                    terrain_map[x][z] = new Sprite(terrain);
                    terrain_map[x][z].setPosition(x, z);
                    terrain_map[x][z].setSize(1, 1);
                }
        }
        
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    water_map[x][z] = new Sprite(water_well);
                    water_map[x][z].setPosition(x, z);
                    water_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    oilW_map[x][z] = new Sprite(oil_well);
                    oilW_map[x][z].setPosition(x, z);
                    oilW_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    oilR_map[x][z] = new Sprite(oil_refinery);
                    oilR_map[x][z].setPosition(x, z);
                    oilR_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    chemical_map[x][z] = new Sprite(chemical_plant);
                    chemical_map[x][z].setPosition(x, z);
                    chemical_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    iron_map[x][z] = new Sprite(iron_mine);
                    iron_map[x][z].setPosition(x, z);
                    iron_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    silica_map[x][z] = new Sprite(silica_mine);
                    silica_map[x][z].setPosition(x, z);
                    silica_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    petrol_map[x][z] = new Sprite(petrol_powerplant);
                    petrol_map[x][z].setPosition(x, z);
                    petrol_map[x][z].setSize(1, 1);
            }
        }
        
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    hq_map[x][z] = new Sprite(headquarters);
                    hq_map[x][z].setPosition(x, z);
                    hq_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    storage_map[x][z] = new Sprite(storage);
                    storage_map[x][z].setPosition(x, z);
                    storage_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    market_map[x][z] = new Sprite(market);
                    market_map[x][z].setPosition(x, z);
                    market_map[x][z].setSize(1, 1);
            }
        }
        for (int z = 0; z < 10; z++) {
            for (int x = 0; x < 10; x++) {
                    bank_map[x][z] = new Sprite(bank);
                    bank_map[x][z].setPosition(x, z);
                    bank_map[x][z].setSize(1, 1);
            }
        }

        Gdx.input.setInputProcessor(new IsoCamController(cam));
        
        sectorMaps.put("Water Well", water_map[4][2]);
        sectorMaps.put("Oil Well", oilW_map[2][8]);
        sectorMaps.put("Oil Refinery", oilR_map[4][8]);
        sectorMaps.put("Chemical Plant", chemical_map[2][6]);
        sectorMaps.put("Iron Mine", iron_map[7][2]);
        sectorMaps.put("Silica Mine", silica_map[2][2]);
        sectorMaps.put("Petrol Power Plant", petrol_map[7][8]);
        
        timeSync.setThreadWork(true);
		t = new Thread(timeSync);
//		Gdx.app.postRunnable(t);
		t.start();
        
        res.startProgressDialog("Loading");
        new LoadSectorOwned().execute();
        
    }
    
    @Override
    public void dispose () {
		hud.dispose();
		hud_bottom.dispose();
        terrain.dispose();
        water_well.dispose();
        oil_well.dispose();
        oil_refinery.dispose();
        chemical_plant.dispose();
        iron_mine.dispose();
        silica_mine.dispose();
        petrol_powerplant.dispose();
        headquarters.dispose();
        storage.dispose();
        market.dispose();
        bank.dispose();
        batch.dispose();
        batchUI.dispose();
    }
    
    @Override
    public void render () {
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            cam.update();
            batch.setProjectionMatrix(cam.combined);
            batch.setTransformMatrix(matrix);
            
            batch.begin();
            for (int z = 0; z < 10; z++) {
                    for (int x = 0; x < 10; x++) {
                            terrain_map[x][z].draw(batch);
                    }
            }
            
            // if unlocked draw
            for(int i=0;i<sectors.size();i++){
            	sectorMaps.get(sectors.get(i)).draw(batch);
            }
//            water_map[4][2].draw(batch);
//            
//            oilW_map[2][8].draw(batch);
//            
//            oilR_map[4][8].draw(batch);
//            
//            chemical_map[2][6].draw(batch);
//            
//            iron_map[7][2].draw(batch);
//            
//            silica_map[2][2].draw(batch);
//            
//            petrol_map[7][8].draw(batch);
            
            hq_map[4][4].draw(batch);
            market_map[7][4].draw(batch);
            storage_map[2][4].draw(batch);
            bank_map[4][6].draw(batch);
            batch.end();

            checkTileTouched();
            
            camUI.update();
            batchUI.setProjectionMatrix(camUI.combined);
            batchUI.begin();
            // upper hud
            batchUI.draw(hud, -TARGET_WIDTH/2, (TARGET_HEIGHT/2)-32);
            font.draw(batchUI, "Cash", -(TARGET_WIDTH/4), (TARGET_HEIGHT/2)-10);
            font.draw(batchUI, ": " + user.getMoney() + " ZE", -(TARGET_WIDTH/4)+32, (TARGET_HEIGHT/2)-10);
            // merah font.setColor(1.0f, 0f, 0f, 1.0f);
            // hijau font.setColor(0f, 1.0f, 0f, 1.0f);
            font.draw(batchUI, "Time", -(TARGET_WIDTH/2)+2, (TARGET_HEIGHT/2)-10);
            font.draw(batchUI, ": " + timeSync.getTime(), -(TARGET_WIDTH/2)+32, (TARGET_HEIGHT/2)-10);
            
            // bottom hud
            batchUI.draw(hud_bottom, (TARGET_WIDTH/2)-128, -(TARGET_HEIGHT/2));                   
            font.draw(batchUI, "T", (TARGET_WIDTH/2)-32, -(TARGET_HEIGHT/2)+18);
            batchUI.end();
    }

    final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
    final Vector3 intersection = new Vector3();
    Sprite lastSelectedTile = null;

    private void checkTileTouched () {
            if (Gdx.input.justTouched()) {
                    Ray pickRay = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
                    Intersector.intersectRayPlane(pickRay, xzPlane, intersection);
                    // System.out.println(intersection);
                    int x = (int)intersection.x;
                    int z = (int)intersection.z;
                    if (x >= 0 && x < 10 && z >= 0 && z < 10) {
                            if (lastSelectedTile != null) lastSelectedTile.setColor(1, 1, 1, 1);
                            Sprite sprite = terrain_map[x][z];
                            sprite.setColor(0, 120/255f, 240/255f, 1);
                            lastSelectedTile = sprite;
                    }
                    
                    // production units (if coordinate && unlocked)
                    if (x == 4 && z == 2 && sectors.contains("Water Well")){
//                    	System.out.println("Water Well");
                    	b = new Bundle();
                    	b.putString("sector", "Water Well");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Water Well")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);
                    }
                    if (x == 2 && z == 8 && sectors.contains("Oil Well")){
//                    	System.out.println("Oil Well");
                    	b = new Bundle();
                    	b.putString("sector", "Oil Well");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Oil Well")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);
                    }
                    if (x == 4 && z == 8 && sectors.contains("Oil Refinery")){
//                    	System.out.println("Oil Refinery");
                    	b = new Bundle();
                    	b.putString("sector", "Oil Refinery");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Oil Refinery")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);
                    }
                    if (x == 2 && z == 6 && sectors.contains("Chemical Plant")){
//                    	System.out.println("Chemical Plant");
                    	b = new Bundle();
                    	b.putString("sector", "Chemical Plant");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Chemical Plant")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);
                    }
                    if (x == 7 && z == 2 && sectors.contains("Iron Mine")){
//                    	System.out.println("Iron Mine");
                    	b = new Bundle();
                    	b.putString("sector", "Iron Mine");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Iron Mine")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);
                    }
                    if (x == 2 && z == 2 && sectors.contains("Silica Mine")){
//                    	System.out.println("Silica Mine");
                    	b = new Bundle();
                    	b.putString("sector", "Silica Mine");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Silica Mine")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);;
                    }
                    if (x == 7 && z == 8 && sectors.contains("Petrol Power Plant")){
//                    	System.out.println("Petrol Plant");
                    	b = new Bundle();
                    	b.putString("sector", "Petrol Power Plant");
                    	b.putDouble("buildCost", costs.get(sectors.indexOf("Petrol Power Plant")));
                    	b.putDouble("propCost", price);
                    	res.startAct(SectorActivity.class, b);
                    }
                    
                    // main units (if coordinate && unlocked)
                    if (x == 4 && z == 4){
                    	System.out.println("Headquarters");
                    	res.startAct(HeadquarterTabActivity.class);
                    }
                    if (x == 7 && z == 4){
                    	System.out.println("Market");
                    	res.startAct(MarketTabActivity.class);
                    }
                    if (x == 2 && z == 4){
                    	System.out.println("Storage");
                    	res.startAct(StorageTabActivity.class);
                    }
                    if (x == 4 && z == 6){
                    	System.out.println("Bank");
                    	res.startAct(BankLobbyActivity.class);
                    }
            }
    }
    
    public class IsoCamController extends InputAdapter {
            final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
            final Vector3 intersection = new Vector3();
            final Vector3 curr = new Vector3();
            final Vector3 last = new Vector3(-1, -1, -1);
            final Vector3 delta = new Vector3();
            final Camera camera;
            
            public IsoCamController (Camera camera) {
                    this.camera = camera;
            }

            @Override
            public boolean touchDragged (int x, int y, int pointer) {
                    Ray pickRay = camera.getPickRay(x, y);
                    Intersector.intersectRayPlane(pickRay, xzPlane, curr);

                    if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
                            pickRay = camera.getPickRay(last.x, last.y);
                            Intersector.intersectRayPlane(pickRay, xzPlane, delta);
                            delta.sub(curr);
                            camera.position.add(delta.x, 0, delta.z);
                    }
                    last.set(x, y, 0);
                    return false;
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                    last.set(-1, -1, -1);
                    return false;
            }
    }
    
    @Override
    public boolean needsGL20 () {
                return false;
        }
    
    private class LoadSectorOwned extends AsyncTask<String, Void, Object>{
		
		@Override
		protected Object doInBackground(String... params) {
			try {
				return CommunicationService.get(CommunicationService.GET_LOAD_SECTOR_OWNED+"&user="+user.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Object res) {
			if(res == null){
				IsoCam.this.res.showToast("No response from server. Try again later.");
			} else if(res.toString().equals("-1")){
				IsoCam.this.res.showToast("Server is not ready..");
			} else if(res.toString().equals("0")){
				IsoCam.this.res.showToast("Internal error..");
			} else {
				sectors = null;
				sectors = new ArrayList<String>();
				costs = null;
				costs = new ArrayList<Double>();
				JsonParser parser = new JsonParser();
				JsonArray array = parser.parse(res.toString()).getAsJsonArray(),
						array1 = parser.parse(new Gson().fromJson(array.get(0), String.class)).getAsJsonArray(),
						array2 = parser.parse(new Gson().fromJson(array.get(1), String.class)).getAsJsonArray();
				
				for(int i=0;i<array1.size();i++){
					sectors.add(new Gson().fromJson(array1.get(i), String.class));
				}
				
				for(int i=0;i<array2.size();i++){
					costs.add(new Gson().fromJson(array2.get(i), Double.class));
				}
				
				price = new Gson().fromJson(array.get(2), Double.class);
			}
			IsoCam.this.res.stopProgressDialog();
		}
		
	}
}
