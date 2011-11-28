/**
 * DiagramPanel.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.SampleModel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JPanel;
/**
 * component that displays multiple labeled x-f(x)-point series with common axes,
 * connected with lines
 * @author immanuel
 *
 */
public class DiagramPanel extends JPanel {

	public static final Color colorBg = new Color(255,255,255);
	public static final Color colorAxis = new Color(0,0,0);
	public static final Color colorText = new Color(0,0,0);
	public static final Color[] colorLabels = {new Color(255,0,0),
		new Color(0,255,0),new Color(0,0,255),new Color(192,192,0),new Color(0,192,192),new Color(192,0,192)
	};
	
	public static final int small_gap = 3;
	public static final int small_square = 10;
	public static final int arrow_length = 12;
	public static final int arrow_half_height = 5;
	public static final int point_radius = 2;
	
	public static Color getPointColor(int label) {
		int darker_count = label / colorLabels.length;
		Color c = colorLabels[label % colorLabels.length];
		for (int i=0;i<darker_count;++i)
			c = c.darker();
		return c;
	}
	
	private Vector<String> labels;
	private Vector<Map<Double,Double>> points;
	
	/**
	 * Create the panel.
	 */
	public DiagramPanel() {
		
		clearData();
		
	}
	
	/**
	 * delete all diagram data
	 */
	public void clearData() {
		labels = new Vector<String>();
		points = new Vector<Map<Double,Double>>();
		
	}
	/**
	 * adds another data series to the diagram
	 * @param label   displayed label
	 * @param data    X-Y-point data
	 */
	public void addSeries(String label, Map<Double,Double> data) {
		labels.add(label);
		points.add(data);
	}
	
	/**
	 * Put a line on screen
	 * @param s   text
	 * @param occupied  currently occupied screen area
	 * @param x   x coordinate
	 * @param g   context
	 * @return  new occupied screen area
	 */
	private int putLine(String s, int occupied, int x, Graphics g) {
		int height = (int) g.getFontMetrics().getLineMetrics(s, g).getHeight();
		g.drawString(s, x, occupied+height);
		return occupied + small_gap + height;
	}
	
	private int scaleAxis(Double p, Double min, Double max, int length) {
		double x = p-min;
		double w = max-min;
		return (int)(x*length/w+0.5);
	}
	
	/**
	 * paint the diagram
	 */
	@Override
	protected void paintComponent(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		/**
		 * y-coordinate of already occupied draw area
		 */
		int occupied = small_gap; 
		
		/**
		 * Paint background
		 */
		g.setColor(colorBg);		
		g.fillRect(0, 0, width, height);
		
		/**
		 * Paint the legends
		 */
		for (int i=0;i<labels.size();++i) {
			g.setColor(getPointColor(i));
			g.fillRect(small_gap, occupied+small_gap, small_square, small_square);
			
			g.setColor(colorText);
			occupied = putLine(labels.get(i), occupied, small_square+2*small_gap, g);
		}
		
		/**
		 * Calculate the diagram
		 */
		
		occupied += small_gap;
		int height_diagram = height - occupied - 2*small_gap - arrow_half_height;
		int width_diagram = width - 2*small_gap - arrow_half_height;
		int x_diagram = small_gap + arrow_half_height;
		int y_diagram = occupied+small_gap;
		
		Double minX = 0.;
		Double maxX = 1.;
		Double minY = 0.;
		Double maxY = 1.;
		
		for (int i=0;i<points.size();++i) {
			Map<Double,Double> pts = points.get(i);
			for (Iterator<Double> x_it = pts.keySet().iterator(); x_it.hasNext(); ) {
				Double x = x_it.next();
				Double y = pts.get(x);
				if (minX > x) minX = x;
				if (maxX < x) maxX = x;
				if (minY > y) minY = y;
				if (minY < y) maxY = y;
			}
		}
		
		/**
		 * Draw axes
		 */
		int[] x_pts = new int[3];
		int[] y_pts = new int[3];
		
		g.setColor(colorAxis);
		
		g.drawLine(x_diagram, scaleAxis(0.,maxY,minY,height_diagram)+y_diagram,
				x_diagram+width_diagram, scaleAxis(0.,maxY,minY,height_diagram)+y_diagram);
		
		x_pts[0] = x_diagram+width_diagram;
		x_pts[1] = x_diagram+width_diagram-arrow_length;
		x_pts[2] = x_diagram+width_diagram-arrow_length;
		
		y_pts[0] = scaleAxis(0.,maxY,minY,height_diagram)+y_diagram;
		y_pts[1] = scaleAxis(0.,maxY,minY,height_diagram)+y_diagram+arrow_half_height;
		y_pts[2] = scaleAxis(0.,maxY,minY,height_diagram)+y_diagram-arrow_half_height;
		g.fillPolygon(x_pts, y_pts, 3);
		
		
		
		g.drawLine(scaleAxis(0.,minX,maxX,width_diagram)+x_diagram,y_diagram+height_diagram,
				scaleAxis(0.,minX,maxX,width_diagram)+x_diagram, y_diagram);
		
		y_pts[0] = y_diagram;
		y_pts[1] = y_diagram+arrow_length;
		y_pts[2] = y_diagram+arrow_length;
		
		x_pts[0] = scaleAxis(0.,minX,maxX,width_diagram)+x_diagram;
		x_pts[1] = scaleAxis(0.,minX,maxX,width_diagram)+x_diagram+arrow_half_height;
		x_pts[2] = scaleAxis(0.,minX,maxX,width_diagram)+x_diagram-arrow_half_height;
		g.fillPolygon(x_pts, y_pts, 3);
		
		/**
		 * Draw points
		 */
		
		for (int i=0;i<points.size();++i) {
			g.setColor(getPointColor(i));
			int last_x=0,last_y=0;
			boolean first_point = true;			
			
			Map<Double,Double> pts = points.get(i);
			for (Iterator<Double> x_it = pts.keySet().iterator(); x_it.hasNext(); ) {
				Double x = x_it.next();
				int x_coord = scaleAxis(x, minX, maxX, width_diagram)+x_diagram;
				int y_coord = scaleAxis(pts.get(x), maxY, minY,height_diagram)+y_diagram;
				
				g.drawOval(x_coord-point_radius/2,y_coord-point_radius/2, point_radius, point_radius);
				
				if (first_point) {
					first_point = false;
				} else {
					g.drawLine(last_x, last_y, x_coord, y_coord);
				}
				
				
				last_x = x_coord;
				last_y = y_coord;
			}
		}
		
	}

}
