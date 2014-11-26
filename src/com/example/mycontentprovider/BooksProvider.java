package com.example.mycontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BooksProvider extends ContentProvider {
	public static final String PROVIDER_NAME = "com.example.provider.Books";
	public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME +"/books");
	public static final String _ID = "_id";
	public static final String TITLE = "title";
	public static final String ISBN = "isbn";
	public static final int BOOKS = 1;
	public static final int BOOK_ID = 2;
	public static final UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "books", BOOKS);
		uriMatcher.addURI(PROVIDER_NAME, "books/#", BOOK_ID);
	}
	
	// Tao databases cho Content Providers
	
	private static final String DATABASE_NAME = "Books";
	private static final String DATABASE_TABLE = "titels";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE =
			"create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
			+ " title text not null, isbn text not null);";
	
	
	// tao database help
	
	private SQLiteDatabase booksDB;
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Upgrate provider", " Upgrate database from " + oldVersion +" to "+ newVersion);
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}
	
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		int count = 0;
		switch (uriMatcher.match(uri)){
		case BOOKS:
			count = booksDB.delete(DATABASE_NAME, selection, selectionArgs);
			break;
		case BOOK_ID:
			String id = uri.getPathSegments().get(1);
			count = booksDB.delete(
					DATABASE_NAME, _ID + " = " + id +
					(!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
			break;
			default: throw new IllegalArgumentException("Unknw Uri" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	public String getType(Uri uri){
		switch(uriMatcher.match(uri)){
			case BOOKS:
				return "vnd.android.cursor.dir/vnd.example.books";
			case BOOK_ID:
				return "vnd.android.cursor.dir/vnd.example.books";
			default: throw new  IllegalArgumentException("Unknw Uri" + uri);
	    }
	}
	
	public Uri insert(Uri uri, ContentValues values){
		// a new book
		long rowID = booksDB.insert(DATABASE_TABLE, "", values);
		
		if (rowID > 0)
		{
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Fail insert "+ uri);
	}
	
	public boolean onCreate(){
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		booksDB = dbHelper.getWritableDatabase();
		return (booksDB == null)? false:true;
	}
	
	public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(DATABASE_TABLE);
		if (uriMatcher.match(uri) == BOOK_ID)
		sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
		if (sortOrder == null || sortOrder=="")
			sortOrder = TITLE;
		Cursor c = sqlBuilder.query(booksDB, projections, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch(uriMatcher.match(uri)){
		case BOOKS:
			count = booksDB.update(DATABASE_TABLE, values, selection, selectionArgs);
			break;
		case BOOK_ID:
			count = booksDB.update(DATABASE_TABLE, values,
					_ID + " = " + uri.getPathSegments().get(1) +
					(!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
			break;
		default: throw new IllegalArgumentException("Unknow Uri" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
