package testing.Toolbar;

import java.awt.BorderLayout;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * A scrollable toolbar.
 * 
 * @author http://tech.chitgoks.com/2009/12/05/create-a-sideway-scrolling-toolbar-using-java/, Kristofer Schweiger
 */
public class AnotherToolBar extends JPanel implements Observer {

	private static final long	serialVersionUID	= 1L;

	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event dispatch thread.
   */
  private static void createAndShowGUI() {
      //Create and set up the window.
      JFrame frame = new JFrame("AnotherToolBar");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel tmp = new JPanel();

      //Add content to the window.
      frame.add(new AnotherToolBar(tmp));

      //Display the window.
      frame.add(tmp);
      frame.pack();
      frame.setVisible(true);
  }


	private int x;
	private int totalwidth; // total width of the toolbar menu
	private Point point;
	private final int MOVEX = 30;
	private JPanel toolbar;


	public AnotherToolBar(JPanel toolbar) {
		super();
		initComponents();

		this.toolbar = toolbar;
		//CurrentTransaction.processingObservable.addObserver(this);
		point = new Point(0, 0);

		jPanel1.add(toolbar, BorderLayout.NORTH);
		//CurrentTransaction.processingObservable.setControlName("RESIZE_TOOLBAR");
	}

	private void initComponents() {

		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jPanel1 = new javax.swing.JPanel();

		setLayout(new java.awt.BorderLayout());

		jButton1.setText("<");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		add(jButton1, java.awt.BorderLayout.LINE_START);

		jButton2.setText(">");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});
		add(jButton2, java.awt.BorderLayout.LINE_END);

		jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		jPanel1.setLayout(new java.awt.BorderLayout());
		jScrollPane1.setViewportView(jPanel1);

		add(jScrollPane1, java.awt.BorderLayout.CENTER);
	}//                         

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (totalwidth >= (x + jScrollPane1.getVisibleRect().width)) {
			// do nothing
		} else {
			x += MOVEX;
			point.x = x;
			jScrollPane1.getViewport().setViewPosition(point);
		}
	}                                        

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (x != 0) {
			x -= MOVEX;
			point.x = x;
			jScrollPane1.getViewport().setViewPosition(point);
		}
	}                                        

	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	// End of variables declaration                   

	@Override
	public void update(Observable o, Object arg) {
		if (arg.toString().equalsIgnoreCase("RESIZE_TOOLBAR")) {
			totalwidth = toolbar.getBounds().width;
			if (getBounds().width > totalwidth) {
				// added left/right buttons for side scrolling
				jButton1.setVisible(true);
				jButton2.setVisible(true);
			} else {
				jButton1.setVisible(false);
				jButton2.setVisible(false);
				x = 0;
			}
			validate();
		}
	}
}

class MyObserver extends Observable {
	private String controlName = "";

	public void setControlName(String c) {
		this.controlName = c;
		setChanged();
		notifyObservers(this.controlName);
	}

	public String getControlName() {
		return this.controlName;
	}
}
