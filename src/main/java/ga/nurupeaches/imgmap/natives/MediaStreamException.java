package ga.nurupeaches.imgmap.natives;

import java.io.IOException;

/**
 * Represents an error involving a video's stream
 */
public class MediaStreamException extends IOException {

	public MediaStreamException(){
		super();
	}

	public MediaStreamException(String str){
		super(str);
	}

}