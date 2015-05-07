package ga.nurupeaches.imgmap;

import ga.nurupeaches.imgmap.cmd.DrawCommand;
import ga.nurupeaches.imgmap.context.AnimatedMapContext;
import ga.nurupeaches.imgmap.context.Context;
import ga.nurupeaches.imgmap.context.ImageMapContext;
import ga.nurupeaches.imgmap.context.ImageMultiMapContext;
import ga.nurupeaches.imgmap.natives.NativeCallbackHandler;
import ga.nurupeaches.imgmap.natives.NativeVideo;
import ga.nurupeaches.imgmap.utils.IOHelper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImgMapPlugin extends JavaPlugin {

	public static final Charset IO_CHARSET = Charset.forName("UTF-8");
    private static ImgMapPlugin SINGLETON;

    @Override
    public void onEnable(){
        SINGLETON = this;
		DrawCommand command = new DrawCommand();
		getCommand("drawimage").setExecutor(command);
		getCommand("drawvideo").setExecutor(command);
        getCommand("joinvideo").setExecutor(command);
        getCommand("drawdesktop").setExecutor(command);
		loadContexts();

        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().contains("win")){
            System.load(new File(getDataFolder(), "NativeVideo.dll").getAbsolutePath());
        } else {
            System.load(new File(getDataFolder(), "libNativeVideo.so").getAbsolutePath());
        }
		NativeVideo.initialize(NativeCallbackHandler.class);
    }

    @Override
    public void onDisable(){
        SINGLETON = null;
		saveContexts();
		try{
			unloadJNI();
		} catch (Exception e){
			logger().log(Level.SEVERE, "Failed to unload native JNI library!", e);
		}
    }

	public void loadContexts(){
		File input = new File(this.getDataFolder(), "contexts");
		DataInputStream dis = null;
		FileInputStream fis;
		Context.Notifiable dummy = new Context.Notifiable() {
			@Override
			public void sendMessage(String message) {}
		};

		try {
			if(!input.exists()){
				input.createNewFile();
			}

			fis = new FileInputStream(input);
			dis = new DataInputStream(fis);

			int contextType;
			while((contextType = dis.read()) != -1){
				Context context = null;

				switch(contextType){
					case 0x01:
						context = new ImageMapContext();
						break;
					case 0x02:
						context = new ImageMultiMapContext();
						break;
				}

				if(context != null){
					context.read(dis);
					context.updateContent(dummy, context.getImageSource(), IOHelper.fetchImage(context.getImageSource()));
					context.update();
					Context.registerContext(context);
				}
			}
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to parse contexts file!", e);
		} finally {
			if(dis != null){
				try {
					dis.close(); // Closes fis
				} catch (IOException e){
					getLogger().log(Level.SEVERE, "Failed to close \"dis\"!", e);
				}
			}
		}
	}

	public void saveContexts(){
		File output = new File(this.getDataFolder(), "contexts");
		HashSet<Context> contexts = new LinkedHashSet<>(Context.getContexts());
		// You may ask yourself: what the hell are you doing above?
		// Duplicate MultiMapContexts exist when we call Context.getContexts().
		// By definition, LinkedHashSet does not allow and will remove duplicates.
		DataOutputStream dos = null;
		FileOutputStream fos;

		try {
			if(!output.exists()){
				output.createNewFile();
			}

			fos = new FileOutputStream(output, false);
			dos = new DataOutputStream(fos);

			byte id = 0x7F; // 7F is limit for IDs
			for(Context context : contexts){
				if(context instanceof ImageMapContext){
					id = 0x01;
				} else if(context instanceof ImageMultiMapContext){
					id = 0x02;
				} else if(context instanceof AnimatedMapContext){
					((AnimatedMapContext)context).stopThreads(); // Don't write it.
					((AnimatedMapContext)context).getVideo().close();
				}

				if(id != 0x7F){
					dos.write(id);
					context.write(dos);
				}

				id = 0x7F; // Reset it for the next context.
			}
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to parse contexts file!", e);
		} finally {
			if(dos != null){
				try {
					dos.flush();
					dos.close();
					// Closes fos (we still call flush() since we don't know if it'll
					// follow the same behavior on all platforms.
				} catch (IOException e){
					getLogger().log(Level.SEVERE, "Failed to close \"dis\"!", e);
				}
			}
		}
	}

    public static ImgMapPlugin getPlugin(){
        synchronized(ImgMapPlugin.class){
            return SINGLETON;
        }
    }

	public static Logger logger(){
		synchronized(ImgMapPlugin.class){
			return SINGLETON.getLogger();
		}
	}

	public void unloadJNI() throws Exception {
		Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
		field.setAccessible(true);
		Vector libs = (Vector)field.get(this.getClassLoader());
		Method finalizeMethod;
		for (Object o : libs) {
			finalizeMethod = o.getClass().getDeclaredMethod("finalize");
			finalizeMethod.setAccessible(true);
			finalizeMethod.invoke(o);
		}
	}

}