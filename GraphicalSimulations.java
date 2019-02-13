import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public abstract class GraphicalSimulations extends JFrame{
	double dt;
	double total;
	double V;
	boolean isPlot;
	double minX;
	double maxX;
	double minY;
	double maxY;

	GraphicalSimulations(){
		this(0.0);
	}
	GraphicalSimulations(double V0){
		V=V0;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		setVisible(true);
		isPlot = true;

		minX=-10;
		maxX=10;
		minY=-10;
		maxY=110;
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		run(g);
	}

	void Plot(Graphics g, double x1, double y1, double x2, double y2){
		double px1=640*(x1-minX)/(maxX-minX);
		double px2=640*(x2-minX)/(maxX-minX);
		double py1=480*(y1-minY)/(maxY-minY);
		double py2=480*(y2-minY)/(maxY-minY);
		g.drawLine((int)px1, 480-(int)py1, (int)px2, 480-(int)py2);
	}
	abstract double dV(double x, double t);
	void run(Graphics g){
		double t=0;
		while(t<total){
			double change = dV(V,t);
			double new_V = V + change * dt;
			if(isPlot){
				Plot(g, t, V, t+dt, new_V);
			}
			V = new_V;

			t += dt;
		}
	}
}