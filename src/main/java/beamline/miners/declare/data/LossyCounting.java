package beamline.miners.declare.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Triple;

/**
 * This data structure is used to manage the different replayer.
 * 
 * This data structure is composed of:
 * <ul>
 * 	<li>case-id;</li>
 * 	<li>replayers data structure + frequency of the case id + current
 * 		bucket.</li>
 * </ul>
 * 
 * @author Andrea Burattin
 */
public class LossyCounting<T> extends ConcurrentHashMap<String, Triple<T, Integer, Integer>> {
	
	private static final long serialVersionUID = -973856497040719491L;
	private int size = 0;

	/**
	 * 
	 * @param caseId
	 * @param currentBucket
	 * @param inCaseOfNull
	 */
	public void addObservation(String caseId, Integer currentBucket, T inCaseOfNull) {
		if (containsKey(caseId)) {
			Triple<T, Integer, Integer> v = get(caseId);
			put(caseId, Triple.of(v.getLeft(), v.getMiddle() + 1, v.getRight()));
		} else {
			put(caseId, Triple.of(inCaseOfNull, 1, currentBucket - 1));
			size++;
		}
	}

	/**
	 * 
	 * @param caseId
	 * @param currentBucket
	 * @param inCaseOfNull
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void addObservation(String caseId, Integer currentBucket, Class<?> inCaseOfNull) throws InstantiationException, IllegalAccessException {
		addObservation(caseId, currentBucket, (T) inCaseOfNull.newInstance());
	}
	
	/**
	 * 
	 * @param currentBucket
	 */
	public void cleanup(Integer currentBucket) {
		for (Iterator<String> it = keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Triple<T, Integer, Integer> v = get(key);
			Integer age = v.getMiddle() + v.getRight();
			if (age <= currentBucket) {
				it.remove();
				size--;
			}
		}
	}
	
	/**
	 * 
	 * @param caseId
	 * @return
	 */
	public T getItem(String caseId) {
		return get(caseId).getLeft();
	}
	
	/**
	 * 
	 * @param caseId
	 * @param item
	 * @param currentBucket
	 */
	public void putItem(String caseId, T item) {
		Triple<T, Integer, Integer> v = get(caseId);
		put(caseId, Triple.of(item, v.getMiddle(), v.getRight()));
//		put(caseId, new Triple<HashMap<String, HashMap<String, Integer>>, Integer, Integer>(trace, 1, currentBucket - 1));
	}
	
	/**
	 * 
	 * @return
	 */
//	public Integer getSize() {
//		return size;
//	}
	public Integer getSize() {
		int tmp = super.size();
		if (tmp > 0) {
			for (Triple<T, Integer, Integer> i : values()) {
				T item = i.getLeft();
				if (item instanceof Collection<?>) {
					tmp += ((Collection<?>) item).size();
				}
			}
		}
		return tmp;
	}
}