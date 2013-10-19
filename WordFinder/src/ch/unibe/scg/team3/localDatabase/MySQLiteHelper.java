package ch.unibe.scg.team3.localDatabase;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ch.unibe.scg.team3.wordlist.Wordlist;
import ch.unibe.scg.team3.wordlist.WordlistBuilder;

/**
 * 
 * @author nils
 * @author adrian
 * 
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String dictable = "CREATE TABLE Dictionary (_id INTEGER PRIMARY KEY, Name TEXT)";
		db.execSQL(dictable);
		for (int i = 0; i < ALPHABET.length(); i++) {
			String shorttable = "CREATE TABLE " + ALPHABET.charAt(i)
					+ "short(_id INTEGER PRIMARY KEY ASC, Dictionary, "
					+ "Content TEXT, FOREIGN KEY(Dictionary) REFERENCES Dictionary(Name))" ;
			db.execSQL(shorttable);
			String longtable = "CREATE TABLE "
					+ ALPHABET.charAt(i)
					+ "long(_id INTEGER PRIMARY KEY ASC,Dictionary,"
					+ "Content TEXT, FOREIGN KEY(_id) REFERENCES Dictionary(Name))";
			db.execSQL(longtable);
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	private static final String DATABASE_NAME = "localDatabase.db";
	private static final int DATABASE_VERSION = 1;
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

}
