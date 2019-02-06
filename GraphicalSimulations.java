import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

public class GraphicalSimulations{
	double dt;
	double total;
	double V;

	GraphicalSimulations(){
		JFrame frame = new JFrame("Graphical Simulations");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640,480);
		frame.setVisible(true);

	}
	abstruct double dV(double x, double t);
	void run(){
		double t=0;
		while(t<total){
			
			t+=dt;
		}
	}
}