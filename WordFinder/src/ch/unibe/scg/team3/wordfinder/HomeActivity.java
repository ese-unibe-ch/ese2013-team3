package ch.unibe.scg.team3.wordfinder;


import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import ch.unibe.scg.team3.localDatabase.MySQLiteHelper;

/**
 * 
 * @author nils
 * 
 */
public class HomeActivity extends Activity {
	MySQLiteHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);   
//        db = new MySQLiteHelper(this);
//        db.getReadableDatabase();
//        db.close();
    }
    
    public void startGame(View view){
    	//Intent intent = new Intent(this, GameActivity.class);
    	Intent intent = new Intent(this, GridActivity.class);
    	startActivity(intent);
    }
    
    public void startPreferences(View view){
    	Intent intent = new Intent(this, PreferencesActivity.class);
    	startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
}
