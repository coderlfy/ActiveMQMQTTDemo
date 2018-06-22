package com.example.activemqmqttdemo;


import com.activemq.mqtthelper.MQTTHelper;

import android.app.Activity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mtvLog = null;
	private MQTTHelper _mqtt = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mtvLog = (TextView) findViewById(R.id.textView1);
		
		mtvLog.setText("");
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
	
	public void Log(final String log)
	{
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mtvLog.append(String.format("%s \n", log));
				
			}  
			
		});
		
		
	}
	
	public void loginMQ(View view){
		_mqtt = new MQTTHelper(this);
		_mqtt.SetServerIP("192.168.18.124");
		_mqtt.Connect();
	}
	
	public void Disconnect(View view){
		if(_mqtt != null)
			_mqtt.Disconnect();
		else
			Log("mqtt is null!!");
	}
	
	public void Sendmessage(View view){
		if(_mqtt != null)
		{
			EditText mEtTitle = (EditText) findViewById(R.id.et_title);
			_mqtt.SendMsg(mEtTitle.getText().toString());
		}
		else
		{
			Log("mqtt is null!!");
		}
	}

}
