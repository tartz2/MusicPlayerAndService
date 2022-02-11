package course.examples.Services.KeyService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import course.examples.Services.KeyCommon.KeyGenerator;

public class KeyGeneratorImpl extends Service {

	// All the Artist names, song names, and urls. NOTE: It was really hard to find links that
	// worked easily with mediaPlayer, so i found a few on this free site and used them
	private ArrayList<String> Artists = new ArrayList<String>(
			Arrays.asList("T. Schürger", "T. Schürger", "T. Schürger", "T. Schürger", "T. Schürger")
	);
	private ArrayList<String> Names = new ArrayList<String>(
			Arrays.asList("SoundHelix Song 1", "SoundHelix Song 2", "SoundHelix Song 3",
					"SoundHelix Song 4", "SoundHelix Song 5")
	);
	private ArrayList<String> URLs = new ArrayList<String>(
			Arrays.asList("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
					"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
					"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
					"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
					"https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3")
	);

	// Implement the Stub for this Object
	private final KeyGenerator.Stub mBinder = new KeyGenerator.Stub() {

		@Override
		public String getArtist(int id) throws RemoteException {
			if(id >= 0 && id < 5) {
				return Artists.get(id);
			} else {
				return "INVALID";
			}
		}

		@Override
		public String getName(int id) throws RemoteException {
			if(id >= 0 && id < 5) {
				return Names.get(id);
			} else {
				return "INVALID";
			}
		}

		@Override
		public String getURL(int id) throws RemoteException {
			if(id >= 0 && id < 5) {
				return URLs.get(id);
			} else {
				return "INVALID";
			}
		}

		@Override
		public String[] getAll(int id) throws RemoteException {
			String myStringArr[] = new String[5];
			myStringArr[0] = "tmp 0";
			myStringArr[1] = "tmp 1";

			String myStringArr2[] = new String[5];
			myStringArr2[0] = "tmp 20";
			myStringArr2[1] = "tmp 21";

			if(id == 0)
				return myStringArr;
			else
				return myStringArr2;
		}
	};

	// Return the Stub defined above
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}
