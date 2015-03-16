package ga.nurupeaches.imgmap.utils;

import java.lang.reflect.Field;

public final class Reflection {

	private Reflection(){}

	public static Object fetchField(Object instance, String fieldName){
		try{
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(instance);
		} catch (Exception e){ // BADBADBADBAD
			throw new IllegalArgumentException(e); // REALLYBADREALLYBADREALLYBAD
		}
	}

}