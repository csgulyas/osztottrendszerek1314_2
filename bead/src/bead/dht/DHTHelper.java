package bead.dht;

import java.util.*;

public class DHTHelper {
    public static Map sortDht(Map dht) {
        List list = new LinkedList(dht.entrySet());
        
        Collections.sort(list, new Comparator() {
                        @Override
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
                                       .compareTo(((Map.Entry) (o2)).getValue());
			}
		});
        
        Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
    }
}
