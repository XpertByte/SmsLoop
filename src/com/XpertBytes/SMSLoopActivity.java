package com.XpertBytes;
 
import com.XpertBytes.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText; 
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
  

public class SMSLoopActivity extends Activity {
    /** Called when the activity is first created. */
	
	EditText phno;
	EditText msg;
	EditText num;
	EditText sec;
	Button send;
	Button contact;
	MultiAutoCompleteTextView textView;
	ProgressThread progThread;
    ProgressDialog progDialog;
	String[] phoneNumberList;
	String message;
	String phnum;
	int rep;
	int delay;
	int maxBarVal;
	int total; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        Bundle b=this.getIntent().getExtras();
        phoneNumberList = b.getStringArray("c");
        send = (Button) findViewById(R.id.button);
        contact = (Button) findViewById(R.id.contact);
        msg = (EditText) findViewById(R.id.msgBox);
        num = (EditText) findViewById(R.id.n);
        sec = (EditText) findViewById(R.id.sec);
        
        
        final ArrayAdapter<String> adapter = new ArrayAdapter <String> (this, android.R.layout.simple_dropdown_item_1line ,phoneNumberList);
        
        textView = (MultiAutoCompleteTextView) findViewById(R.id.phno);
        textView.setThreshold(1);
        adapter.setNotifyOnChange(true);
        textView.setAdapter(adapter);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                
        send.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				message = msg.getText().toString();
				phnum = textView.getText().toString();
				
				String x = num.getText().toString();
				String secD = sec.getText().toString();
				rep = 0;
				
				if (!verify(phnum)||phnum.contains("<null>"))
				{
					textView.setText("");
					Toast.makeText(getApplicationContext(), "Phone Number Invalid!!!", Toast.LENGTH_SHORT).show();
				}
				
				if(!x.equals("") && isNumeric(x)==true)
				{
					rep = Integer.parseInt(x);
					maxBarVal = rep;
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please Enter Any Digit!", Toast.LENGTH_SHORT).show();
				}
				
				if(!secD.equals("") && isNumeric(secD)==true)
				{
					delay = Integer.parseInt(secD);
					delay = delay *1000;
					if(delay == 0)
						delay = 40;
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please Enter Delay Time (Seconds)!", Toast.LENGTH_SHORT).show();
				}
				
				if (textView.getText().toString().length() > 0 && message.length() > 0&&secD.length() > 0&&x.length() > 0)
                {
                    showDialog(1);
                }
                else
                    Toast.makeText(getBaseContext(),"Phone Number And Message Fields are Required!",Toast.LENGTH_SHORT).show();
				
				textView.setText("");
				msg.setText("");
				
			}
		});
        
        contact.setOnClickListener(new View.OnClickListener() { 
			public void onClick(View arg0) { 
				
				Intent intent = new Intent(SMSLoopActivity.this,About.class);
	    		startActivity(intent);
				
			}
		}); 
        
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        
    	// Horizontal Dialog Box
    	
        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setMax(maxBarVal);
        progDialog.setMessage("Sending Message....");
        progDialog.setButton("Hide", new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Hidden!", Toast.LENGTH_SHORT).show();
			}
		});
        progThread = new ProgressThread(handler);
        progThread.start();
        return progDialog;
    }
    
    final Handler handler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
        	Log.d(null, "handler");
            //int total = msg.getData().getInt("total");
            progDialog.setProgress(total);
            if (total > maxBarVal){
                //dismissDialog(1);
            	removeDialog(1);
                progThread.setState(ProgressThread.DONE); 
            } 
        }
    };
    
    private class ProgressThread extends Thread {	
        
        final static int DONE = 0;
        final static int RUNNING = 1;
        
        Handler mHandler;
        int mState;
    
        ProgressThread(Handler h) {
            mHandler = h;
        }
        
        @Override
        public void run() { 
        	
            mState = RUNNING;   
            total = 0;
            while (mState == RUNNING) {
            	
                try {
                	if((total<maxBarVal))
                		Thread.sleep(delay);
                } catch (InterruptedException e) {
                //    Log.e("ERROR", "Thread was Interrupted");
                }
                total++;
                
                if((total <= maxBarVal))
                {
                	sendSMS(phnum , message , rep);
                }
                
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total);
                msg.setData(b);
                mHandler.sendMessage(msg);
                // Count down
            }
        }

	
	public void setState(int state) {
        mState = state;
    }
    }
    protected void sendSMS(String phoneNo, String message , int rep) {
		// TODO Auto-generated method stub
		
		SmsManager sms = SmsManager.getDefault();
		
		if(verify(phoneNo)==false)
		{
			//Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_SHORT).show();
			return;
		}
		
		phoneNo = phoneNo.replace(" ", ""); 
		String [] contacts = phoneNo.split(","); 
		String num = "";
		
		for(int i = 0 ; i < contacts.length ; i++)
        { 
            if(contacts[i].indexOf('<')!=-1&&contacts[i].charAt(contacts[i].indexOf('<'))=='<')
            {
                String [] nums = contacts[i].split("<");
                if(contacts[i].charAt(0)!='<' && nums[1]!=null)
                {
                    if(contacts[i].indexOf('>')!=-1&&nums[1].charAt(nums[1].indexOf('>'))=='>')
                    {
                        num = nums[1].substring(0, nums[1].length() - 1);
                        if(isNumeric(num)==true)
                        { 
                        	sms.sendTextMessage(num, null, message, null, null); 
                        } 
                    }
                    else
                    {
                        num = nums[1];
                        if(isNumeric(num)==true)
                        { 
                        	sms.sendTextMessage(num, null, message, null, null); 
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                num = contacts[i];
                if(isNumeric(num)==true)
                { 
                	sms.sendTextMessage(num, null, message, null, null);
            
                }
            } 
        }  
	}
    
    public boolean isNumeric(String num)
    {
    	int flag = 0;
    	if(num.charAt(0)=='+')
    	{
    		flag++;
    	}
    	for(int i = flag ; i < num.length() ;i++)
    	{
    		if (!Character.isDigit(num.charAt(i)))
                return false;
    		
    	}
    	return true;
    }
    
    public boolean verify(String num)
    {
    	for(int i = 0 ; i < num.length() ;i++)
    	{
    		if ((num.charAt(i)>='0' && num.charAt(i)<='9')||(num.charAt(i)>='A' && num.charAt(i)<='Z')||(num.charAt(i)>='a' && num.charAt(i)<='z')||(num.charAt(i)==',')||(num.charAt(i)==' ')||(num.charAt(i)=='<')||(num.charAt(i)=='>')||(num.charAt(i)=='+'))
    		{
    		}
    		else
    		{
    			return false;
    		}
    		
    	}
    	return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
		
	}
	@Override
	  public void onDestroy() { 
	    super.onDestroy();
	  }
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		/*switch (item.getItemId()) {
	        case R.id.about:
	    	{
	    		Intent intent = new Intent(SMSLoopActivity.this,About.class);
	    		startActivity(intent);
	    	}
		}*/
    return true;
    }
	
}