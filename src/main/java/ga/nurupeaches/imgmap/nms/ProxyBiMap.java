package ga.nurupeaches.imgmap.nms;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ProxyBiMap<K, V> implements BiMap<K, V> {

	private BiMap<Object, Object> proxy;
	private BiMap<K, V> delegate;
	private ProxyBiMap<V, K> inverse;

	public ProxyBiMap(BiMap<K, V> original){
		delegate = original;
		proxy = HashBiMap.create();
	}

	public void addProxy(Object target, Object orig){
		proxy.put(target, orig);
	}

	@Override
	public V get(Object key) {
		Object key2 = proxy.get(key);
		return key2 != null ? delegate.get(key2) : delegate.get(key);
	}

	@Override
	public V put(@Nullable K k, @Nullable V v) {
		return delegate.put(k, v);
	}

	@Override
	public V forcePut(@Nullable K k, @Nullable V v) {
		return delegate.forcePut(k, v);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		delegate.putAll(map);
	}

	@Override
	public Set<V> values() {
		return delegate.values();
	}

	@Override
	public BiMap<V, K> inverse() {
		if(inverse == null){
			inverse = new ProxyBiMap<V, K>(delegate.inverse());
			inverse.proxy = proxy.inverse();
		}

		return inverse;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(key);
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

}