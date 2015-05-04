package ga.nurupeaches.imgmap.natives;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DebugCallbackHandler implements CallbackHandler {

    private final SwingNativeVideoTest test;
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final Runnable redrawRunnable = new Runnable() {

        @Override
        public void run() {
            test.paintImmediately(0, 0, test.getWidth(), test.getHeight());
        }

    };
    private Future<?> redraw;

	public DebugCallbackHandler(SwingNativeVideoTest test){
		this.test = test;
	}

	// Called by JNI
	@Override
	public void handleData(){
        if(redraw == null || redraw.isDone()){
            redraw = service.submit(redrawRunnable);
        }
	}

}