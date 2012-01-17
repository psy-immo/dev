/**
 * MainWindow.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JToolBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow {

	private JFrame frmCumulativeDiagrams;
	
	private File workingDirectory;
	
	private DiagramPanel diagramPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					
					window.workingDirectory = new File(".");
					
					
					window.frmCumulativeDiagrams.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	
		frmCumulativeDiagrams = new JFrame();
		frmCumulativeDiagrams.setTitle("Combined Cumulative Time Series");
		frmCumulativeDiagrams.setBounds(100, 100, 450, 300);
		frmCumulativeDiagrams.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		frmCumulativeDiagrams.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		/**
		 * Refresh the time series' found in the working directory
		 */
		
		final ActionListener refreshDiagramSeries = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diagramPanel.clearData();
				
				/**
				 * scan for files ending in ".dat"
				 */
				
				String[] files = workingDirectory.list(new FilenameFilter() 
				 {
					
					@Override
					public boolean accept(File arg0, String arg1) {
						
						return arg1.toUpperCase().endsWith(".DAT");
					}
				});
				
				/**
				 * read the time series CSV and add them to the DiagramPanel
				 */
				
				for (int i=0; i<files.length; ++i) {
					TimeSeries ts = new TimeSeries();
					
					try {
						ts.readCSV(new FileInputStream(new File(workingDirectory.getPath()+File.separator+files[i])), TimeSeries.sniffyCharset);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					diagramPanel.addSeries(files[i], ts.getPoints());
					
				}
				diagramPanel.repaint();
			}
		};
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(refreshDiagramSeries);
		toolBar.add(btnRefresh);
		
		JButton btnChooseWorkingDirectory = new JButton("Choose working directory...");
		btnChooseWorkingDirectory.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				/**
				 * choose new working directory, then invoke refresh action
				 */
				JFileChooser chooser = new JFileChooser(workingDirectory);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (chooser.showOpenDialog(frmCumulativeDiagrams) == JFileChooser.APPROVE_OPTION) {
					workingDirectory = chooser.getSelectedFile();
					refreshDiagramSeries.actionPerformed(arg0);
				}
			}
		});
		toolBar.add(btnChooseWorkingDirectory);
		
		diagramPanel = new DiagramPanel();
		frmCumulativeDiagrams.getContentPane().add(diagramPanel, BorderLayout.CENTER);
	}


}
