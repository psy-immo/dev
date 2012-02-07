package de.tu_dresden.psy.regexp;

import java.util.Set;

public interface StringSplitter {
	/**
	 * split given String into its (matched) parts, give all possible splittings
	 * 
	 * @param chain  String to split
	 * @return  possible splittings
	 */
	public Set<String[]> split(String chain);

}
