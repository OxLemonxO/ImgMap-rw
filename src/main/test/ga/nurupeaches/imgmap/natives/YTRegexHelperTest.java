package ga.nurupeaches.imgmap.natives;

import ga.nurupeaches.imgmap.utils.YTRegexHelper;

import java.util.List;

public class YTRegexHelperTest {

	public static final String ID = "rnQBF2CIygg";

	public static void main(String[] args){
		List<String> test = YTRegexHelper.getDirectLinks(ID);
		System.out.println(test.get(0));
	}

}