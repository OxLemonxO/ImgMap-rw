package ga.nurupeaches.imgmap.context;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

// Basically, factories done wrong!
public abstract class Context {

	private static final Map<Short, Context> CONTEXT_LOOKUP = new HashMap<Short, Context>();

	protected LinkedList<String> history = new LinkedList<String>(){

		// Small work around to making a LinkedList somewhat "unique".
		// This should be classified as "terrible code" since LinkedList.contains() performs at O(n).
		@Override
		public boolean add(String s) {
			return !contains(s) && super.add(s);
		}

	};

	public abstract void updateContent(Notifiable notifiable, String source, BufferedImage image);
	public abstract void update(Object... additionalParams);

	public abstract void write(DataOutputStream stream) throws IOException;
	public abstract void read(DataInputStream stream) throws IOException;

	public static Collection<Context> getContexts(){
		return Collections.unmodifiableMap(CONTEXT_LOOKUP).values();
	}

	public String getImageSource(){
		return history.getLast();
	}

	public static Context getContext(short id){
		synchronized(CONTEXT_LOOKUP){
			return CONTEXT_LOOKUP.get(id);
		}
	}

	public static void registerContext(Context context){
		if(context instanceof MapContext){
			CONTEXT_LOOKUP.put(((MapContext)context).getId(), context);
		} else if(context instanceof MultiMapContext){
			for(short id : ((MultiMapContext)context).getIds()){
				CONTEXT_LOOKUP.put(id, context);
			}
		}

		System.out.println("registered3 " + context);
	}

	// TODO: Find a better way of getting numbers inbetween.
	public static short[] _conv(List<Short> ids){
		Iterator<Short> iter = ids.iterator();
		short[] arr = new short[ids.size()];
		for(int i=0; i < arr.length; i++){
			arr[i] = iter.next();
		}
		return arr;
	}

	public interface Notifiable {

		public void sendMessage(String message);

	}

}