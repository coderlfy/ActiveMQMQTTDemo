package com.activemq.mqtthelper;



import android.util.Log;

import com.example.activemqmqttdemo.MainActivity;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;

import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;

import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;


public class MQTTHelper {
	
	private CallbackConnection _connection = null;
	private String _address = "";
	private String _addressFormat = "tcp://%s:1883";
	private MainActivity _act = null;
	
	public MQTTHelper(MainActivity act)
	{
		this._act = act;
	}
	
	public MQTTHelper()
	{}
	
	public void SetServerIP(String ip)
	{
		this._address = String.format(_addressFormat, ip);
	}
	
	public void Disconnect()
	{
		
		_connection.disconnect(new Callback<Void>(){

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				_act.Log("disconnect failed!!");
			}

			@Override
			public void onSuccess(Void arg0) {
				// TODO Auto-generated method stub
				_act.Log("disconnect success!!");
			}});
	}
	
	public void Connect()
	{
		try
		{
			MQTT mqtt = new MQTT();
			//mqtt.setKeepAlive((short)2);
			//mqtt.setReconnectDelay(2000);
			mqtt.setHost(this._address);		
			mqtt.setClientId(android.os.Build.SERIAL);
			mqtt.setCleanSession(false);
			
			_connection = mqtt.callbackConnection();	
			
			_connection.connect(new Callback<Void>(){
				
				public void onSuccess(Void value) {
					_act.Log("connect Success!!!");
					
					_connection.listener(MQTTHelper.this.createListener());					
	        		
					Topic[] topics = new Topic[1]; 
					topics[0] = new Topic(UTF8Buffer.utf8("class123"), QoS.EXACTLY_ONCE);
					_connection.subscribe(topics, new Callback<byte[]>(){

						@Override
						public void onFailure(Throwable arg0) {
							_act.Log("subscribe fail!!");
						}

						@Override
						public void onSuccess(byte[] arg0) {
							_act.Log("subscribe success!!");
						}}
					);
				}
				public void onFailure(Throwable e) {
					
					_act.Log(String.format("connected to %s failed!!!", 
							MQTTHelper.this._address));
				}
			});
		}
		catch(Exception e)
		{
			_act.Log(e.toString());
		}
	}
	
	public void SendMsg(final String msg)
	{
		try
		{
			if(_connection != null)
			{
				_connection.publish("class123", msg.getBytes(), QoS.EXACTLY_ONCE, false, new Callback<Void>(){

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
						_act.Log(String.format("send message %s failed!!!", msg));
					}

					@Override
					public void onSuccess(Void arg0) {
						// TODO Auto-generated method stub
						_act.Log(String.format("send message %s success!!!", msg));
					}});
			}
			else
			{
				_act.Log("not connect!!!");
				
			}

		}
		catch(Exception ex)
		{
			_act.Log(ex.toString());
			
		}
	}

	private Listener createListener()
	{
		return new org.fusesource.mqtt.client.Listener(){

			@Override
			public void onConnected() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDisconnected() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPublish(UTF8Buffer topic, Buffer payload,
					Runnable ack) {
				// TODO Auto-generated method stub
				String fullPayLoad = new String(payload.data);
				//_act.Log(String.format("receiver content = %s!!", fullPayLoad));
							
				
				// but the payload seems to in fact consists of 0x32 0xlen (maybe more than a byte) 0x(topic) 0x(message number - in 2 bytes) 0x(message)   
				String receivedMesageTopic =  topic.toString();
	            String[] fullPayLoadParts = fullPayLoad.split(receivedMesageTopic);// TODO: I should probably check if there are characters that needs to be scaped
	                        
	            //Log.d(TAG, "fullpayload = " + fullPayLoad);
	            if(fullPayLoadParts.length == 2){
	            	// sometimes the payload includes the message ID (2 bytes), sometimes it doesnt....
	            	// if the first character is a "{" then it didnt
	            	String messagePayLoad;
	            	if(fullPayLoadParts[1].charAt(0) == '{')
	            		messagePayLoad = fullPayLoadParts[1];
	            	else
	            		messagePayLoad = fullPayLoadParts[1].substring(2);
	            	
	            	_act.Log(String.format("receiver content = %s!!", messagePayLoad));

	                // TODO: SEND AN UPDATE MESSAGE TO ACTIVITY
	            }
				
		        // Once process execute the ack runnable.
	            ack.run();
			}
			
			
		};
		
	}
}
