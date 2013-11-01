package ch.unibe.scg.team3.localDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class WordlistHandler extends DataHandler {

	/**
	 * Words with length smaller than SMALL_WORD are small words
	 */
	public static final int SMALL_WORD = 5;
	public static final String SHORT_WORD_TABLE_SUFFIX = "short";
	public static final String LONG_WORD_TABLE_SUFFIX = "long";

	// TODO: check for injections
	public WordlistHandler(Context context) {
		super(context);

	}

	public void addEmptyWordlist(String name)
			throws WordlistAlreadyInDataBaseException {

		if (!isWordlistInDatabase(name)) {
			SQLiteDatabase db = helper.getWritableDatabase();
			// TODO: use ? to prevent injection
			db.execSQL("INSERT INTO Dictionary VALUES(NULL,'" + name + "')");
			db.close();
		} else {
			throw new WordlistAlreadyInDataBaseException();
		}
	}

	// TODO: check if needed after v1 release
	public void addWordlistByFileInRaw(String name, String filename)
			throws WordlistAlreadyInDataBaseException {

		addEmptyWordlist(name);

		int resID = context.getResources().getIdentifier(filename, "raw",
				context.getPackageName());

		int wordlistId = getWordlistId(name);

		InputStream inputStream = context.getResources().openRawResource(resID);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);

		String word;
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();

		try {
			while ((word = buffreader.readLine()) != null) {
				addWordToOpenDb(word, wordlistId, db);
			}
			db.setTransactionSuccessful();
		}

		catch (IOException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public boolean addWordToWordlist(String word, String wordlistname) {

		int wordlistId = getWordlistId(wordlistname);
		SQLiteDatabase db = helper.getWritableDatabase();

		try {
			addWordToOpenDb(word, wordlistId, db);
		} catch (SQLException e) {
			db.close();
			return false;
		} finally {
			db.close();
		}

		return true;
	}

	private void addWordToOpenDb(String word, int wordlistId, SQLiteDatabase db)
			throws SQLException {
		if (word.length() < SMALL_WORD && word.length() > 0) {

			db.execSQL("INSERT INTO " + getFirstLetter(word)
					+ SHORT_WORD_TABLE_SUFFIX + " VALUES(NULL, '" + wordlistId
					+ "', '" + word + "')");

		} else if (word.length() > SMALL_WORD) {
			db.execSQL("INSERT INTO " + getFirstLetter(word)
					+ LONG_WORD_TABLE_SUFFIX + " VALUES(NULL, '" + wordlistId
					+ "', '" + word + "')");

		} else {
			throw new SQLException();
		}
	}

	private String getFirstLetter(String word) {
		return word.substring(0, 1).toLowerCase();
	}

	public void removeWordlist(String name) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE IF EXISTS FROM Dictionary WHERE Name = '" + name
				+ "'");
		db.close();
	}

	public void removeWordFromWordlist(String word, String wordlist) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE IF EXISTS FROM" + getFirstLetter(word)
				+ "WHERE Name = '" + wordlist + "' AND content = '" + word
				+ "'");
		db.close();
	}

	public boolean isWordInWordlist(String word, String wordlist) {

		if (word.length() == 0)
			return false;

		String table = getFirstLetter(word);

		if (word.length() < SMALL_WORD) {
			table += SHORT_WORD_TABLE_SUFFIX;
		} else {
			table += LONG_WORD_TABLE_SUFFIX;
		}

		int wordlistId = getWordlistId(wordlist);
		SQLiteDatabase db = helper.getReadableDatabase();

		String[] contents = { Integer.toString(wordlistId), word.toLowerCase() };

		Cursor cursor = db.rawQuery("SELECT content FROM " + table
				+ " WHERE Dictionary = ? AND content = ?", contents);

		if (cursor.getCount() != 0) {
			cursor.close();
			db.close();
			return true;
		}

		db.close();
		return false;

	}

	// TODO: test, can use in addWordToWordlist
	public boolean isWordlistInDatabase(String wordlistname) {

		SQLiteDatabase db = helper.getReadableDatabase();

		String[] content = { wordlistname };

		Cursor cursor = db.rawQuery(
				"SELECT _id FROM Dictionary WHERE Name = ?", content);
		
		if (cursor.getCount() != 0) {
			cursor.close();
			db.close();
			return true;
		}
		db.close();
		return false;

	}

	//TODO: look at code downwards here
	public int getWordlistId(String wordlistname) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT _id FROM Dictionary WHERE Name = ?",
				new String[] { wordlistname });
		if (c.getCount() != 0) {
			c.moveToFirst();
			int id = c.getInt(0);
			c.close();
			db.close();
			return id;
		}
		db.close();
		return 0;

	}

	public CharSequence[] getWordlists() {
		CharSequence[] lists;
		ArrayList<String> tmp = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT _id , Name FROM Dictionary", null);
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				tmp.add(c.getString(1));
				c.moveToNext();
			}

			c.close();
			db.close();
			return (CharSequence[]) tmp.toArray(new CharSequence[tmp.size()]);
		}
		db.close();
		return null;

	}

	public CharSequence[] getWordlistids() {
		CharSequence[] lists;
		ArrayList<String> tmp = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT _id , Name FROM Dictionary", null);
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				tmp.add(Integer.toString(c.getInt(0)));
				c.moveToNext();
			}
			c.close();
			db.close();
			return (CharSequence[]) tmp.toArray(new CharSequence[tmp.size()]);
		}
		db.close();
		return null;

	}

	public void copyDB() throws IOException {
		SQLiteDatabase db = helper.getWritableDatabase();
		helper.importDatabase();
		db.close();
	}

	public SQLiteDatabase getDb() {
		return helper.getWritableDatabase();
	}

}