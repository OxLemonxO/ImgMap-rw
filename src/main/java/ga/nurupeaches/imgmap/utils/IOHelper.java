package ga.nurupeaches.imgmap.utils;

import ga.nurupeaches.imgmap.ImgMapPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class IOHelper {

	private static final Logger logger = Logger.getLogger("ImgMap");
	private static final File cacheFolder = new File(ImgMapPlugin.getPlugin().getDataFolder(), "cache");
	private static final MessageDigest digestor;

	private IOHelper(){}

	static {
		try {
			digestor = MessageDigest.getInstance("SHA-1"); // Use SHA-1 for now; simple and easy.
		} catch (NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}

		if(!cacheFolder.exists()){
			cacheFolder.mkdirs();
		}
	}

	public static BufferedImage fetchImage(String url){
		try{
			return fetchImage(new URL(url));
		} catch (MalformedURLException e){
			return null;
		}
	}

	public static BufferedImage fetchImage(URL url){
		String externalUrl = url.toExternalForm();
		File cachedImage = getCachedImage(externalUrl);
		BufferedImage image;
		HttpURLConnection conn = null;
		InputStream stream = null;
		FileOutputStream fos = null;

		if(!cachedImage.exists()){
			logger.log(Level.INFO, "Downloading image " + cachedImage.getName());
			try{
				conn = (HttpURLConnection)url.openConnection();
				int response = conn.getResponseCode();
				if(!(response >= 200 && response <= 207)){
					logger.log(Level.WARNING, "Received HTTP response code " + response + ". Attempting to read image anyways.");
				}

				stream = new BufferedInputStream(conn.getInputStream());

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int read;
				while((read = stream.read(buff)) != -1){
					baos.write(buff, 0, read);
				}
				baos.close();

				if(!cachedImage.createNewFile()){
					throw new IOException("Failed to create cache image!");
				}

				fos = new FileOutputStream(cachedImage, false);
				fos.write(baos.toByteArray());
				fos.flush();
			} catch (IOException e){
				logger.log(Level.SEVERE, "Failed to fetch image from URL " + externalUrl, e);
			} finally {
				if(conn != null){
					conn.disconnect();
				}

				tryToClose(stream);
				tryToClose(fos);
			}
		} else {
			logger.log(Level.INFO, "Using cached image " + cachedImage.getName());
		}

		try{
			image = ImageIO.read(cachedImage);
		} catch (IOException e){
			logger.log(Level.SEVERE, "Failed to read image from path " + cachedImage, e);
			return null;
		}

		return image;
	}

	public static void tryToClose(Closeable closeable){
		if(closeable != null){
			try{
				closeable.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to close stream " + closeable, e);
			}
		}
	}

	public static BufferedImage resizeImage(BufferedImage image, int width, int height){
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = resized.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.drawImage(image, 0, 0, width, height, null);
		graphics.dispose();
		return resized;
	}

	public static BufferedImage resizeImage(BufferedImage image){
		return resizeImage(image, 128, 128);
	}

	public static File getCachedImage(String url){
		digestor.reset();
		digestor.update(url.getBytes(Charset.forName("UTF-8")));

		String fileName = String.format("%040x", new BigInteger(1, digestor.digest()));
		return new File(cacheFolder, fileName);
	}

}