package ga.nurupeaches.imgmap.context;

import ga.nurupeaches.imgmap.natives.NativeVideo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class WatchableContext extends Context {

	protected Set<UUID> viewers = new HashSet<UUID>();

	public abstract void startThreads();
	public abstract void stopThreads();

	public abstract NativeVideo getVideo();

	public void addViewer(UUID uuid){
		viewers.add(uuid);
	}

	public void clearViewers(){
		viewers.clear();
	}

	@Override
	public void write(DataOutputStream stream) throws IOException {}

	@Override
	public void read(DataInputStream stream) throws IOException {}

}