package ch.unibe.scg.team3.wordfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import ch.unibe.scg.team3.board.Board;
import ch.unibe.scg.team3.game.Game;
import ch.unibe.scg.team3.gameui.BoardOnTouchListener;
import ch.unibe.scg.team3.gameui.BoardUI;
import ch.unibe.scg.team3.gameui.FoundWordsView;
import ch.unibe.scg.team3.gameui.ScoreView;
import ch.unibe.scg.team3.gameui.Timer;
import ch.unibe.scg.team3.gameui.WordCounterView;
import ch.unibe.scg.team3.localDatabase.DataManager;
import ch.unibe.scg.team3.localDatabase.WordlistManager;

/**
 * @author faerber
 * @author adrian
 * @author nils
 */

@SuppressLint("NewApi")
public class GameActivity extends Activity {

	private Game game;
	private Timer timer;
	private long remainingTime = 5*60000;
	private TextView countDownView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String selectedWordlist = preferences.getString("choose_wordlist", null);
		
		WordlistManager data = new WordlistManager(this);
		
		String wordlistname = "";
		try {
			wordlistname = data.getWordlists()[Integer.parseInt(selectedWordlist) - 1].toString();
		} catch (NumberFormatException e) {
			e.getStackTrace();
		}
		
		game = new Game(data, wordlistname);

		BoardUI boardUI = (BoardUI) findViewById(R.id.tableboardUI);
		FoundWordsView found = (FoundWordsView) findViewById(R.id.foundWordsField);
		ScoreView scoreView = (ScoreView) findViewById(R.id.score_view);
		WordCounterView wordCounter = (WordCounterView) findViewById(R.id.foundCounter);
		countDownView = (TextView) findViewById(R.id.timer_field);
		
		boardUI.setOnTouchListener(new BoardOnTouchListener(this, game));

		
		game.addObserver(boardUI);
		game.addObserver(found);
		game.addObserver(scoreView);
		game.addObserver(wordCounter);
		
		game.notifyObservers();
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		remainingTime = timer.getRemainingTime();
		if (timer != null){
			timer.cancel();
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		timer = new Timer(remainingTime, 1000, countDownView){
			@Override
			public void onFinish() {
				finishGame();
			}
		};
        timer.start();
	}

	public void quit(View view) {
		finishGame();
	}
	
	public void finishGame() {
		Intent intent = new Intent(this, EndGameActivity.class);
		
		intent.putExtra("score", game.getScore());
		intent.putExtra("words_found", game.getFoundWords().size());
		
		startActivity(intent);
		finish();
	}
	
}
