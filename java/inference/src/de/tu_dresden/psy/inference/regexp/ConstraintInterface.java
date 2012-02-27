package de.tu_dresden.psy.inference.regexp;

import java.util.Vector;

import de.tu_dresden.psy.inference.AssertionInterface;

/**
 * interface for checking whether a given premise-vector is compatible with
 * the conclusion rules
 * 
 * @author albrecht
 * 
 */

public interface ConstraintInterface {
	/**
	 * 
	 * @param premises
	 * @return true, if the premises are compatible
	 */
	public boolean check(Vector<AssertionInterface> premises);
}