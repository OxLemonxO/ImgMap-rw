package ga.nurupeaches.imgmap.context;

public interface MultiMapContext {

	public short[] getIds();

	public int[] getSizes();

	public void updateSizes(int x, int y);

	public void updateIds(short[] ids);

}
