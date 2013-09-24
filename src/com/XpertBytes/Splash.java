package com.XpertBytes;
  

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Button;

public class Splash extends Activity{

	String[] phoneNumberList;
	Button about;
	Button sms;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
        final int sleeptimer = 1000;
        
        Thread  myThread = new Thread()
        {
        	@Override
			public void run()
        	{
        		try
        		{
        			int currentwait = 0;
        			while(currentwait < sleeptimer)
    				{
    					sleep(200);
    					currentwait+=200;
    				
	        			if(currentwait == 200)
	        			{
	        				Uri uri=ContactsContract.CommonDataKinds.Phone.CONTENT_URI;;
	        				ContentResolver cr=getContentResolver();
	        				String sortOrder = Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	        				String[] projection = new String[] { BaseColumns._ID, Phone.DISPLAY_NAME,Phone.NUMBER };
	        				Cursor cur=cr.query(uri, projection, null, null, sortOrder);
	        				phoneNumberList = new String[cur.getCount()];
	        				
	        				if(cur.getCount()>0)
	        				{ 
	        					String name;
	        					int i = 0;
	        					while(cur.moveToNext())
	        					{
	        						String ph = null;
	        						//String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
	        						name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
	        						ph = cur.getString(cur.getColumnIndex(Phone.NUMBER));
	        						phoneNumberList[i] = name+"< "+ph+">";
	        						i++;
	        						
	        					}
	        				}
	        				cur.close();
	        				
	        			}
        			}
        		}
        		catch(Exception e)
        		{		
        		}
        		finally{
        			Intent main = new Intent(Splash.this , SMSLoopActivity.class);
        			Bundle b = new Bundle();
        			b.putStringArray("c", phoneNumberList);
        			main.putExtras(b);
        			startActivity(main);
        			finish();
        		}
        	}
        };
        myThread.start();
	} 
}
