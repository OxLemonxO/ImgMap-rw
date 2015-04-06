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

	public abstract short getId();

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
		if(context instanceof ImageMultiMapContext){
			for(short id : ((ImageMultiMapContext)context).getIds()){
				CONTEXT_LOOKUP.put(id, context);
			}
		} else if(context instanceof AnimatedMultiMapContext){
			for(short id : ((AnimatedMultiMapContext)context).getIds()){
				CONTEXT_LOOKUP.put(id, context);
			}
		} else {
			CONTEXT_LOOKUP.put(context.getId(), context);
			System.out.println("registered3 " + context);
		}
	}

	public interface Notifiable {

		public void sendMessage(String message);

	}

}