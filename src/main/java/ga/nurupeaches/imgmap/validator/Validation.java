package ga.nurupeaches.imgmap.validator;

import java.io.IOException;
import java.net.URL;

/**
 * Simple class to validate objects.
 */
public final class Validation {

    private Validation(){}

    /**
     * Validates a URL.
     * @param stringUrl The string to validate whether or not it's an URL.
     * @return A URL object if it was. <code>null</code> otherwise.
     */
    public static URL validateURL(String stringUrl){
        try {
            URL url = new URL(stringUrl);
            return url;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}