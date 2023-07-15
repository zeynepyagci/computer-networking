import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class MenuGui extends JFrame implements ActionListener{
	
	ProxyServer ps = new ProxyServer();

	TextField text = new TextField(50);
	
	JMenuBar menubar;
	
	JMenu file;
	JMenu help;
	
	JMenuItem info;
	JMenuItem start_item;
	JMenuItem stop_item;
	JMenuItem report_item;
	JMenuItem host_item;
	JMenuItem display_item;
	JMenuItem exit_item;
	
	String IP;
	String report;
	static String host;
	
	MenuGui(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400,250);
		this.setTitle("Proxy Server");
		this.setLocationRelativeTo(null);
		
		menubar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		
		start_item = new JMenuItem("Start");
		stop_item = new JMenuItem("Stop");
		report_item = new JMenuItem("Report");
		host_item = new JMenuItem("Add host to filter");
		display_item = new JMenuItem("Display current filtered hosts");
		exit_item = new JMenuItem("Exit");
		
		info = new JMenuItem("Info");
		
		this.setJMenuBar(menubar);
		
		menubar.add(file);
		menubar.add(help);
		
		file.add(start_item);
		file.add(stop_item);
		file.add(report_item);
		file.add(host_item);
		file.add(display_item);
		file.add(exit_item);
		
		help.add(info);
		
		file.addActionListener(this);
		help.addActionListener(this);
		
		start_item.addActionListener(this);
		stop_item.addActionListener(this);
		report_item.addActionListener(this);
		host_item.addActionListener(this);
		display_item.addActionListener(this);
		exit_item.addActionListener(this);
		
		info.addActionListener(this);
		
		
		this.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource()==exit_item) {
			System.exit(0);
		}
		else if(e.getSource()==info) {
			JOptionPane.showMessageDialog(menubar, "Developed by Zeynep Yaðcý");
		}
		else if(e.getSource()==start_item) {
			try {
				ps.startmethod();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(e.getSource()==stop_item) {
			try {
				ps.stopmethod();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(e.getSource()==report_item) {
			
			IP = JOptionPane.showInputDialog("Enter client's IP address");
			
			try {
				String x = ServerHandler.birlestir();
				String completereport = IP + "\n" + x;
				System.out.println(completereport);
				
				PrintWriter writer = new PrintWriter("report.txt"); 
				writer.println(completereport);
				writer.close();
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
		}
		else if(e.getSource()==host_item) {
			host = JOptionPane.showInputDialog("Add host");
		}
		else if(e.getSource()==display_item) {
			JOptionPane.showMessageDialog(MenuGui.this,ServerHandler.forbidden);
		}
		
	}
	
}
