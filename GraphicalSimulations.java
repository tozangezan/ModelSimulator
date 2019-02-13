import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public abstract class GraphicalSimulations extends JFrame{
	double dt;
	double total;
	double V0;
	boolean isPlot;
	double minX;
	double maxX;
	double minY;
	double maxY;

	GraphicalSimulations(){
		this(0.0);
	}
	GraphicalSimulations(double V0_in){
		V0=V0_in;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		setVisible(true);
		setBackground(Color.WHITE);
		isPlot = true;
		total = 200;
		dt = 0.1;

		minX=-10;
		maxX=10;
		minY=-10;
		maxY=110;
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		run(g, V0);
	}

	void Plot(Graphics g, double x1, double y1, double x2, double y2){
		double px1=640*(x1-minX)/(maxX-minX);
		double px2=640*(x2-minX)/(maxX-minX);
		double py1=480*(y1-minY)/(maxY-minY);
		double py2=480*(y2-minY)/(maxY-minY);
		g.drawLine((int)px1, 480-(int)py1, (int)px2, 480-(int)py2);
		System.out.println((int)px1 +" "+ (480-(int)py1) +" "+ (int)px2 +" "+ (480-(int)py2));
	}
	abstract double dV(double x, double t);
	void run(Graphics g, double V0){
		double t=0;
		double V=V0;
		double old_change=0;
		boolean first_time=true;
		while(t<total){
			double change = dV(V,t);
			double new_V = V + change * dt;
			if(isPlot&&!first_time){
				Plot(g, old_change, V, change, new_V);
			}
			first_time = false;
			old_change = change;
			V = new_V;

			t += dt;
		}
	}
}