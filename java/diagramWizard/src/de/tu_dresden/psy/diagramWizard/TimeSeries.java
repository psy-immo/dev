/**
 * TimeSeries.java, (c) 2011, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.tu_dresden.psy.diagramWizard;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * class to hold a time series of events
 * @author immanuel
 * 
 * provides import functionality for CVS files containing time 
 *   information in the first column
 */
public class TimeSeries {
	
	/**
	 * map the time of an event (X coordinate) to its accumulated count (Y coordinate)
	 */
	private  TreeMap<Double,Double> timeXsum;
	
	public static final String sniffyCharset = "UTF-16LE";
	
	public TimeSeries() {
		this.timeXsum = new TreeMap<Double, Double>();
	}
	
	/**
	 * Makes the objects data consistent, i.e. calculates the sum part of timeXsum
	 */
	private void makeConsistent() {
		double count = 1;
		for (Iterator<Double> i=timeXsum.keySet().iterator(); i.hasNext(); count++)
		{
			Double time = i.next();
			timeXsum.put(time, count);
		}
	}
	
	/**
	 * 
	 * @return  a map that maps time points to their respective past events sum
	 */
	public final TreeMap<Double,Double> getPoints() {
		return timeXsum;
	}

	/**
	 * reads a CVS table containing time information in the first column
	 * @param input   input stream containing the file
	 * @param charset input char set string given to InputStreamReader
	 * @throws IOException 
	 */
	public void readCSV(InputStream input, String charset) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(input, charset));
		
		this.timeXsum = new TreeMap<Double, Double>();
		
		String line;
		
		while (null != (line = rd.readLine())) {
			String[] row = line.split("\\t");
			String first_column = row[0].trim();
			try {
				Double time = Double.parseDouble(first_column);
				
				timeXsum.put(time, 0.);
			} catch (NumberFormatException e) {
				/* gently ignore this, may be header of the column */
			}
		}
		
		makeConsistent();
	}
	
	
}
