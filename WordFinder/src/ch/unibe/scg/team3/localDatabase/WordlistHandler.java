package ch.unibe.scg.team3.localDatabase;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

/**
 * This class gives the possibility to manage wordlists in the database.
 * 
 * @author nils
 * 
 */
@SuppressLint("DefaultLocale")
public class WordlistHandler extends DataHandler {

	/**
	 * Words with length smaller than SMALL_WORD are small words
	 */
	public static final int SMALL_WORD = 5;
	public static final String SHORT_WORD_TABLE_SUFFIX = "short";
	public static final String LONG_WORD_TABLE_SUFFIX = "long";
	private static final int MINIMUM_WORD_LENGTH = 1;
	
	private String selectedWordlist;
	
	public WordlistHandler(Context context) {
		super(context);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		selectedWordlist = preferences.getString("choose_wordlist", null);

	}

	/**
	 * // * Adds a new Wordlistentry in Database
	 * 
	 * @param name
	 * @throws WordlistAlreadyInDataBaseException
	 */
	public void addEmptyWordlist(String name)
			throws WordlistAlreadyInDataBaseException {

		if (!isWordlistInDatabase(name)) {
			helper.execSQL("INSERT INTO Dictionary VALUES(NULL, ?)",
					new String[] { name });
		} else {
			throw new WordlistAlreadyInDataBaseException();
		}
	}

	/**
	 * Adds a word to the given wordlist in main database. Pay attention that
	 * database is closed before invoke and it will be closed after execution.
	 * 
	 * @param word
	 *            should not be empty or null
	 * @param wordlistname
	 *            should not be empty or null
	 * @return returns boolean value whether adding entry in database was
	 *         successful
	 */
	@SuppressLint("DefaultLocale")
	public boolean addWordToWordlist(String word, String wordlistname) {
		int wordlistId = getWordlistId(wordlistname);

		try {
			addWordToDb(word.toLowerCase(), wordlistId);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * Adds a word to an OPEN! database. Its important to use this method
	 * carefully! Database will NOT! be closed after execution!
	 * 
	 * @param word
	 *            should not be empty or null
	 * @param wordlistId
	 *            should not be empty or null
	 * @param db
	 *            should be a valid dataBase
	 * @throws SQLException
	 */
	
	private void addWordToDb(String word, int wordlistId)
			throws SQLException {
		if (word.length() < SMALL_WORD && word.length() > 0) {

			helper.execSQL("INSERT INTO "
					+ getFirstLetterFromInputToLowerCase(word)
					+ SHORT_WORD_TABLE_SUFFIX + " VALUES(NULL, '" + wordlistId
					+ "', ?)", new String[] { word.toLowerCase() });

		} else if (word.length() >= SMALL_WORD) {
			helper.execSQL("INSERT INTO "
					+ getFirstLetterFromInputToLowerCase(word)
					+ LONG_WORD_TABLE_SUFFIX + " VALUES(NULL, '" + wordlistId
					+ "', ?)", new String[] { word.toLowerCase() });

		} else {
			throw new SQLException();
		}
	}

	public String getFirstLetterFromInputToLowerCase(String word) {
		return word.substring(0, 1).toLowerCase();
	}

	/**
	 * Removes a wordlist given by name in main database. Pay attention that
	 * database is closed before invoke and it will be closed after execution.
	 * 
	 * @param name
	 *            name of wordlist to be removed from main database
	 */
	public void removeWordlist(String name) {
		
		helper.execSQL("DELETE FROM Dictionary WHERE Name = '" + name + "'");
	}

	/**
	 * Removes a word from the given wordlist in main database. Pay attention
	 * that database is closed before invoke and it will be closed after
	 * execution.
	 * 
	 * @param word
	 *            word to be removed from a wordlist given by name
	 * @param wordlist
	 *            name of wordlist which contains the word to remove
	 */
	public void removeWordFromWordlist(String word, String wordlist) {
		int wordlistId = getWordlistId(wordlist);
		String table;

		if (word.length() < SMALL_WORD) {
			table = getFirstLetterFromInputToLowerCase(word)
					+ SHORT_WORD_TABLE_SUFFIX;
		} else {
			table = getFirstLetterFromInputToLowerCase(word)
					+ LONG_WORD_TABLE_SUFFIX;
		}
		helper.execSQL("DELETE FROM " + table + " WHERE Dictionary = '"
				+ wordlistId + "' AND content = '" + word + "'");
	}

	/**
	 * 
	 * @param word
	 * @param wordlistId
	 * @return
	 */
	public boolean isWordInWordlist(String word, int wordlistId) {

		if (word.length() < MINIMUM_WORD_LENGTH)
			return false;

		String table = getFirstLetterFromInputToLowerCase(word);

		if (word.length() < SMALL_WORD) {
			table += SHORT_WORD_TABLE_SUFFIX;
		} else {
			table += LONG_WORD_TABLE_SUFFIX;
		}


		String[] contents = { word.toLowerCase() };

		Cursor cursor = helper.rawQuery("SELECT Dictionary, Content FROM " + table
				+ " WHERE Dictionary = '" + wordlistId + "' AND Content = ? ",
				contents);

		if (cursor.getCount() != 0) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}

	}

	// TODO: test, can use in addWordToWordlist
	public boolean isWordlistInDatabase(String wordlistname) {

		String[] content = { wordlistname };

		Cursor cursor = helper.rawQuery(
				"SELECT _id FROM Dictionary WHERE Name = ?", content);

		if (cursor.getCount() != 0) {
			cursor.close();
			return true;
		}else
		{
		cursor.close();
		return false;
		}

	}

	// TODO: look at code downwards here
	public int getWordlistId(String wordlistname) {
		Cursor c = helper.rawQuery("SELECT _id FROM Dictionary WHERE Name = ?",
				new String[] { wordlistname });
		if (c.getCount() != 0) {
			c.moveToFirst();
			int id = c.getInt(0);
			c.close();
			return id;
		}else{
		c.close();
		return 0;
		}

	}

	public CharSequence[] getWordlists() {
		CharSequence[] lists = null;
		ArrayList<String> tmp = new ArrayList<String>();
		Cursor c = helper.rawQuery("SELECT _id , Name FROM Dictionary", null);
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				tmp.add(c.getString(1));
				c.moveToNext();
			}

			c.close();
			return (CharSequence[]) tmp.toArray(new CharSequence[tmp.size()]);
		}else
		{
		c.close();
		return lists;
		}

	}

	public CharSequence[] getWordlistIds() {
		CharSequence[] lists = null;
		ArrayList<String> tmp = new ArrayList<String>();
		Cursor c = helper.rawQuery("SELECT _id , Name FROM Dictionary", null);
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				tmp.add(Integer.toString(c.getInt(0)));
				c.moveToNext();
			}
			c.close();
			return (CharSequence[]) tmp.toArray(new CharSequence[tmp.size()]);
		}else{
		c.close();
		return lists;
		}

	}

	public String getRandomWordFromWordlist() {
		Random r = new Random();
		int randomint = r.nextInt(26);
		String table = MySQLiteHelper.ALPHABET.substring(randomint,
				randomint + 1);
		return getRandomWordFromWordlistByLetter(table);

	}

	public String getRandomWordFromWordlistByLetter(String letter) {
		Random r = new Random();
		int random = r.nextInt(2);
		boolean rboolean;
		switch (random) {
		case 0:
			rboolean = true;
			break;
		default:
			rboolean = false;
			break;
		}
		return getRandomWordFromDatabaseByLetterAndLength(letter, rboolean);
	}

	public String getRandomWordFromDatabaseByLetterAndLength(String letter,
			boolean isShort) {
		String word = "";
		String table = "";
		if (isShort) {
			table = letter + SHORT_WORD_TABLE_SUFFIX;
		} else {
			table = letter + LONG_WORD_TABLE_SUFFIX;
		}
			Cursor c = helper.rawQuery("SELECT Content FROM " + table
					+ " WHERE Dictionary = '" + selectedWordlist
					+ "' ORDER BY RANDOM() LIMIT 1", null);
			if (c != null && c.getCount() != 0) {
				c.moveToFirst();
				word = c.getString(0);
				c.close();
			} else {
				c.close();
			}
		return word;
	}
	
	public ArrayList<String> getWordsStartingWith(String suffix){
		ArrayList<String> list = new ArrayList<String>();
	
		if(suffix.length() == 0){
			return list;
		}
		
		String letter = String.valueOf(suffix.charAt(0));
		String table = "";
		
		if (suffix.length() < SMALL_WORD) {
			table = letter + SHORT_WORD_TABLE_SUFFIX;
		} else {
			table = letter + LONG_WORD_TABLE_SUFFIX;
		}
		Cursor c = helper.rawQuery("SELECT Content FROM " + table + " WHERE Dictionary = '" + selectedWordlist +"' AND Content LIKE '" + suffix + "%'", null);
		
		while(c != null && c.moveToNext()){
			list.add(c.getString(0));
		}
		c.close();
		return list;
	}

	public Object getWordlistNameById(int wordlistId) {
		String name = "";
		Cursor c = helper.rawQuery("SELECT Name FROM Dictionary WHERE _id ='"
				+ wordlistId + "'", null);
		if (c.getCount() != 0) {
			c.moveToFirst();
			name = c.getString(0);
			c.close();
			return name;
		}
		c.close();
		return name;
	}

}
