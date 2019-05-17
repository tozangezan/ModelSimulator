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
	boolean isPlotEquilibrium;
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
		isPlotEquilibrium = true;
		
		total = 200;
		dt = 0.001;

		minX=-80;
		maxX=80;
		minY=-10;
		maxY=110;
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0,0,640,480);
		g.setColor(Color.GRAY);
		for(int i=-100;i<=100;i+=20){
			if(i%100==0)Plot(g,i,-100,i,200,2.0);
			else Plot(g,i,-100,i,200,1.0);
		}

		for(int i=-20;i<=120;i+=20){
			if(i%100==0)Plot(g,-100,i,200,i,2.0);
			else Plot(g,-100,i,200,i,1.0);
		}
		
		g.setColor(Color.BLACK);

		double max_V = 110;
		for(int i = 0; i < 999; i++){
			if(isPlotEquilibrium){
				Plot(g,getC0FromV(max_V/1000*i),max_V/1000*i,getC0FromV(max_V/1000*(i+1)),max_V/1000*(i+1),3.0);
			}
		}

		for(int i=0;i<=20;i+=2)
			run(g, i);
	}

	void Plot(Graphics g, double x1, double y1, double x2, double y2, double thickness){
		Graphics2D g2d = (Graphics2D)g;
		double px1=640*(x1-minX)/(maxX-minX);
		double px2=640*(x2-minX)/(maxX-minX);
		double py1=480*(y1-minY)/(maxY-minY);
		double py2=480*(y2-minY)/(maxY-minY);
		BasicStroke stroke = new BasicStroke((float)thickness);
		g2d.setStroke(stroke);
		g2d.drawLine((int)px1, 480-(int)py1, (int)px2, 480-(int)py2);
	//	System.out.println((int)px1 +" "+ (480-(int)py1) +" "+ (int)px2 +" "+ (480-(int)py2));
	}
	abstract double getDisturbance(double x, double t);
	abstract double dV(double x, double t);
	abstract double getC0FromV(double V);

	void run(Graphics g, double V0){
		double t=0;
		double V=V0;

		double old_change=0;
		double old_disturbance=0;
		double old_C=0;
		boolean first_time=true;
		while(t<total){
			double change = dV(V,t);
			double disturbance = getDisturbance(V,t);
			double new_V = V + change * dt;
			if(isPlot&&!first_time){
				Plot(g, old_disturbance, V, disturbance, new_V,1);
			}
			first_time = false;
			old_change = change;
			old_disturbance = disturbance;
			old_C = getDisturbance(V, t);
			V = new_V;

			t += dt;
		}
	}
}