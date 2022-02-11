package course.examples.Services.KeyClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyServiceUser extends Activity {

	protected static final String TAG = "KeyServiceUser";
	protected static final int PERMISSION_REQUEST = 0;
	private KeyGenerator mKeyGeneratorService;
	private boolean mIsBound = false;
	private boolean generated = false;

	private Button goButton;
	private Button stopButton;
	private Button bind;

	private ArrayList<String> Artists = new ArrayList<>();
	private ArrayList<String> Songs = new ArrayList<>();
	private ArrayList<String> URLs = new ArrayList<>();
	int currentSelected = -1;

	MediaPlayer mediaPlayer;

	RecyclerView titleList;
	MyAdapter adapter;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		// initialize our buttons
		goButton = (Button) findViewById(R.id.button);
		stopButton = (Button) findViewById(R.id.button2);
		bind = (Button) findViewById(R.id.button3);

		// disable the unbind and music button while not bound
		goButton.setEnabled(false);
		stopButton.setEnabled(false);

		// initialize recyclerView and our mediaPlayer
		titleList = (RecyclerView) findViewById(R.id.myRecycle);
		mediaPlayer = new MediaPlayer();

		// reference for context later
		Activity myAc = this;

		// do the binding to the service
		bind.setOnClickListener(new OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.O)
			@Override
			public void onClick(View v) {
				if(!mIsBound) {
					if (checkSelfPermission("course.examples.Services.KeyService.GEN_ID")
							!= PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(myAc,
								new String[]{"course.examples.Services.KeyService.GEN_ID"},
								PERMISSION_REQUEST);
						Toast.makeText(v.getContext(), "DMAN", Toast.LENGTH_SHORT);
					} else {

						checkBindingAndBind();
						Toast.makeText(v.getContext(), "WOOO", Toast.LENGTH_SHORT);
						bind.setEnabled(false);
						goButton.setEnabled(true);
						stopButton.setEnabled(true);
					}
				}
			}
		});

		// do all the setup with recyclerView and handling how to play songs
		goButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					// If we are connected, and we never generated the recyclerView, we do so
					if (mIsBound && !generated) {
						int i = 0;
						while(!mKeyGeneratorService.getArtist(i).equals("INVALID")){
							Artists.add(mKeyGeneratorService.getArtist(i));
							Songs.add(mKeyGeneratorService.getName(i));
							URLs.add(mKeyGeneratorService.getURL(i));
							i++;
							Log.i("HERE", "HERE " + i);
						}

						// the listener for playing the tracks
						RVClickListener listener = (view,position)->{
							Toast.makeText(view.getContext(), URLs.get(position),Toast.LENGTH_SHORT).show();
							String url = URLs.get(position);

							// if something is playing, we need to stop and reset it
							if(mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
								mediaPlayer.reset();
								mediaPlayer.release();

								// reinitialize it
								mediaPlayer = new MediaPlayer();

								// if we clicked on the same one as previously selected, it should
								// just stop playing, otherwise play new track
								if(currentSelected != position){
									mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
									mediaPlayer.setDataSource(url);
									mediaPlayer.prepare(); // might take long! (for buffering, etc)
									mediaPlayer.start();
								}
							// if nothing is playing, just set the audio stream type and play it
							} else {
								mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
								mediaPlayer.setDataSource(url);
								mediaPlayer.prepare();
								mediaPlayer.start();
							}
							currentSelected = position;

						};

						// the song title and artist as one string in a list
						ArrayList<String> myList = new ArrayList<>();

						for(int j = 0; j < Artists.size(); j++){
							myList.add(Artists.get(j) + " - " + Songs.get(j));
						}
						adapter = new MyAdapter(myList, listener);
						titleList.setHasFixedSize(true);
						titleList.setAdapter(adapter);
						titleList.setLayoutManager(new GridLayoutManager(v.getContext(),1));
						generated = true;

						// button no longer needed
						goButton.setEnabled(false);
					} else {
						Log.i(TAG, "service was not bound or list has been generated");
					}

				} catch (RemoteException e) {

					Log.e(TAG, e.toString());

				}
			}
		});

		// unbind our service
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mIsBound){
					// unbind the service
					Log.i("In stop", "HERE");
					unbindService(((KeyServiceUser) myAc).mConnection);
					bind.setEnabled(true);
					stopButton.setEnabled(false);
				} else {

				}
			}
		});

	}

	// Bind to KeyGenerator Service
	@Override
	protected void onStart() {
		super.onStart();
	}

	protected void checkBindingAndBind() {
		if (!mIsBound) {

			boolean b = false;
			Intent i = new Intent(KeyGenerator.class.getName());


			// UB:  Stoooopid Android API-21 no longer supports implicit intents
			// to bind to a service #@%^!@..&**!@
			// Must make intent explicit or lower target API level to 20.
			ResolveInfo info = getPackageManager().resolveService(i, 0);
			i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

			b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);




			if (b) {
				goButton.setEnabled(true);
			} else {
				Log.i(TAG, "Ugo says bindService() failed!");
			}
		} else {

		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST: {

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// Permission granted, go ahead and display map

					checkBindingAndBind();
				}
				else {
					Toast.makeText(this, "BUMMER: No Permission :-(", Toast.LENGTH_LONG).show() ;
				}
			}
			default: {
				// do nothing
			}
		}
	}
	// Unbind from KeyGenerator Service
	@Override
	protected void onStop() {

		super.onStop();
		//super.onPause();

		if (mIsBound) {
			unbindService(this.mConnection);
		}
	}

	private final ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder iservice) {

			mKeyGeneratorService = KeyGenerator.Stub.asInterface(iservice);
			Log.i("CONNECT", "FOR NOW");
			mIsBound = true;

		}

		public void onServiceDisconnected(ComponentName className) {

			mKeyGeneratorService = null;
			Log.i("DISCONNECT", "FOR NOW");
			mIsBound = false;

		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
