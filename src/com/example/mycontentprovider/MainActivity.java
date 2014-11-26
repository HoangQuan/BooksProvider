package com.example.mycontentprovider;

import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btnAdd = (Button) findViewById(R.id.btnAddTitle);
		Button btnRetrieve = (Button) findViewById(R.id.btnRetrieveTitle);
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// add new book
				ContentValues values = new ContentValues();
				values.put(BooksProvider.TITLE, ((EditText) findViewById(R.id.txtTitle)).getText().toString());
				values.put(BooksProvider.ISBN, ((EditText) findViewById(R.id.txtISNB)).getText().toString());
				Uri uri = getContentResolver().insert(BooksProvider.CONTENT_URI, values);
				
				Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
			}
		});
		
		btnRetrieve.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// retrieve the title
				Uri allTitles = Uri.parse("content://com.example.provider.Books/books");
				Cursor c = managedQuery(allTitles, null, null, null, "title desc");
				if (c.moveToFirst()){
//				while (c.moveToNext()){DisplayBooks(c);}	
//					DisplayBooks(c);
					Toast.makeText(getBaseContext(),
							c.getCount(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	public void DisplayBooks(Cursor c){
		Toast.makeText(getBaseContext(),
				c.getString(c.getColumnIndex(BooksProvider._ID)) +
				c.getString(c.getColumnIndex(BooksProvider.TITLE)) +
				c.getString(c.getColumnIndex(BooksProvider.ISBN)),
				Toast.LENGTH_LONG).show();
//		String[] book_ids = new String[] {
//				c.getString(c.getColumnIndex(BooksProvider._ID)) 
//			};
//			String[] book_titles =  new String[] { c.getString(c.getColumnIndex(BooksProvider.TITLE)) };
//			String[] book_isbn =  new String[] { c.getString(c.getColumnIndex(BooksProvider.ISBN)) };
//			int[] views =  new int[] {R.id.txtTitle, R.id.txtISNB};
//			SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_main, c, book_ids, views);
//					ListView lw = (ListView) findViewById(R.id.listItem);
//					lw.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
