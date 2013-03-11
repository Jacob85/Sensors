package com.example.sensors;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private SensorManager sensorManager;
	private Sensor lightSensor;
	private Sensor pressureSensor;
	private Sensor Accelerometer;
	private Sensor magneticFirldSensor;
	private SensorEventListener lightListener;
	private SensorEventListener azimuthListener;
	
	private static final int matrix_size = 9;
	float[] Rotation;
	float[] I;
	float azimuth;
	float[] accels = new float[3];
	float[] mag = new float[3];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		this.lightListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				
				// check if the event type is light sensor
				if (event.sensor.getType() == Sensor.TYPE_LIGHT)
				{
					float currentreading = event.values[0];
					TextView lightText = (TextView) findViewById(R.id.textView_light);
					lightText.setText( "Light Read: " +String.valueOf(currentreading));
					return;
				}
				else if (event.sensor.getType() == Sensor.TYPE_PRESSURE)
				{
					// we have pressure change
					float currentreading = event.values[0];
					TextView pressureText = (TextView) findViewById(R.id.textView_pressure);
					pressureText.setText( "Pressure Reaging: " +String.valueOf(currentreading));
					return;
				}
				else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				{
					// we have ACCELEROMETER change
					float[] currentreading = event.values;
					TextView x = (TextView) findViewById(R.id.textView_Accelerometer_x);
					TextView y = (TextView) findViewById(R.id.textView_Accelerometer_y);
					TextView z = (TextView) findViewById(R.id.textView_Accelerometer_z);
					
					x.setText("X: " +String.valueOf(currentreading[0]));
					y.setText("Y: "+ String.valueOf(currentreading[1]));
					z.setText("Z: "+ String.valueOf(currentreading[2]));
					return;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
		};
		
		 this.azimuthListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				

				
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					Sensor accelerometer = event.sensor;
					accels = lowPass( event.values.clone(), accels );
				}
				if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
					Sensor magnetometer = event.sensor;
					mag = lowPass( event.values.clone(), mag );
				}
			    if ((accels != null) && (mag != (null))) {
					Rotation = new float[matrix_size];
					I = new float[matrix_size];

			        boolean success = SensorManager.getRotationMatrix(Rotation, I, accels, mag);
			        if (success) {
			          float orientation[] = new float[3];
			          SensorManager.getOrientation(Rotation, orientation);
			          azimuth = (float)Math.toDegrees(orientation[0]);
			          if(azimuth < 0)
			        	  azimuth = azimuth + 360; 
			          Log.i("tg",String.valueOf(azimuth));	
			          mag = new float[3];
			          accels = new float[3];
			          
					TextView azimith_result = (TextView)findViewById(R.id.textView_Azimuth);	      
			          azimith_result.setText(String.valueOf(azimuth));
			        }
			    }
				
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		
	
	}
	
	
	
	/*
	 * time smoothing constant for low-pass filter
	 * 0 ² alpha ² 1 ; a smaller value basically means more smoothing
	 * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
	 */
	static final float ALPHA = 0.15f;

	protected float[] lowPass( float[] input, float[] output ) {
	    if ( output == null ) return input;
	     
	    for ( int i=0; i<input.length; i++ ) {
	        output[i] = output[i] + ALPHA * (input[i] - output[i]);
	    }
	    return output;
	}
	
	
	
	
	//onClick Light Sensor
	public void StartLight (View v)
	{	
		Button b = (Button) findViewById(R.id.button_light);
		
		if (b.getText().equals("Start Light Sensor"))
		{
			if (lightSensor == null)
			{
				// so we are here for the first time and didnt yet try to run the Light sensor
				b.setText("Stop");
				lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
				if (lightSensor == null)
				{
					// no Such Sensor exists 
					myAlertDialog("Error", "No Such Sensor Exists");
					b.setText("Start Light Sensor");
					return;
				}
			}
			// the sensor exists 
			b.setText("Stop");
			sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		else
		{
			// if we are here means that the Stop button was preased
			b.setText("Start Light Sensor");
			sensorManager.unregisterListener(lightListener, lightSensor);
			TextView lightText = (TextView) findViewById(R.id.textView_light);
			lightText.setText("");

		}
	}

	
	//onClick Pressure Sensor
	public void StartPressure(View v)
	{
		Button b = (Button) findViewById(R.id.button_pressure);
		
		if (b.getText().equals("Start Pressure Sensor"))
		{
			if (pressureSensor == null)
			{
				// so we are here for the first time and didnt yet try to run the Light sensor
				b.setText("Stop");
				pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
				if (pressureSensor == null)
				{
					// no Such Sensor exists 
					myAlertDialog("Error", "No Such Sensor Exists");
					b.setText("Start Pressure Sensor");
					return;
				}
			}
			// the sensor exists 
			b.setText("Stop");
			sensorManager.registerListener(lightListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		else
		{
			// if we are here means that the Stop button was preased
			b.setText("Start Pressure Sensor");
			sensorManager.unregisterListener(lightListener, pressureSensor);
			TextView pressureText = (TextView) findViewById(R.id.textView_pressure);
			pressureText.setText("");
		}
		
	}
	
	//onClick Accelerometer Sensor
	public void StartAccelerometer(View v) 
	{
		Button b = (Button) findViewById(R.id.button_Accelerometer);
		
		if (b.getText().equals("Start Accelerometer Sensor"))
		{
			if (Accelerometer == null)
			{
				// so we are here for the first time and didnt yet try to run the Light sensor
				b.setText("Stop");
				Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				if (Accelerometer == null)
				{
					// no Such Sensor exists 
					myAlertDialog("Error", "No Such Sensor Exists");
					b.setText("Start Accelerometer Sensor");
					return;
				}
			}
			// the sensor exists 
			b.setText("Stop");
			sensorManager.registerListener(lightListener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}
		else
		{
			// if we are here means that the Stop button was preased
			b.setText("Start Accelerometer Sensor");
			sensorManager.unregisterListener(lightListener, Accelerometer);
			TextView AccelerometerText = (TextView) findViewById(R.id.textView_Accelerometer_x);
			AccelerometerText.setText("");
			AccelerometerText = (TextView) findViewById(R.id.textView_Accelerometer_y);
			AccelerometerText.setText("");
			AccelerometerText = (TextView) findViewById(R.id.textView_Accelerometer_z);
			AccelerometerText.setText("");

		}
		
	}
	
	public void StartAzimuth(View v) 
	{
		Button b = (Button) findViewById(R.id.button_Azimuth);
		
		if (b.getText().equals("Start Azimuth Sensor"))
		{
			if (Accelerometer == null)
			{
				// so we are here for the first time and didnt yet try to run the Light sensor
				//b.setText("Stop");
				Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				if (Accelerometer == null)
				{
					// no Such Sensor exists 
					myAlertDialog("Error", "No Such Sensor Exists");
					b.setText("Start Azimuth Sensor");
					return;
				}
			}
			if (magneticFirldSensor == null)
			{
				// so we are here for the first time and didnt yet try to run the Light sensor
				b.setText("Stop");
				magneticFirldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				if (magneticFirldSensor == null)
				{
					// no Such Sensor exists 
					myAlertDialog("Error", "No Such Sensor Exists");
					b.setText("Start Azimuth Sensor");
					return;
				}
			}
				
			
			// the sensor exists 
			b.setText("Stop");
			sensorManager.registerListener(azimuthListener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(azimuthListener, magneticFirldSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		else
		{
			// if we are here means that the Stop button was preased
			b.setText("Start Azimuth Sensor");
			sensorManager.unregisterListener(azimuthListener, Accelerometer);
			sensorManager.unregisterListener(azimuthListener, magneticFirldSensor);
			TextView azimuthText = (TextView) findViewById(R.id.textView_Azimuth);
			azimuthText.setText("");

		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void myAlertDialog(String titel, String message)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle(titel);  	
		// set dialog message
		alertDialogBuilder.setMessage(message)
						  .setCancelable(false)
						  .setPositiveButton("OK",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close the dilaog
								dialog.cancel();
								return;
							}
						  });
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
