import java.util.*;

public class DisturbanceSin extends GraphicalSimulations{
	double K=100;
	double r=2;
	double L=-30;
	double C0=31;
	double w=0.43;
	DisturbanceSin(double V0){
		super(V0);
	}
	public static void main(String args[]){
		new DisturbanceSin(10.0);
	}
	double dV(double x, double t){
		// dV/dt=r*V*(1-V/K)-(C0+L*sin(t*w))*(V*V/(V*V+1))
		return r*x*(1-x/K)-getDisturbance(x,t)*x*x/(x*x+1);
	}
	double getDisturbance(double x, double t){
		return C0+L*Math.sin(t*w);
	}
	double getC0FromV(double V){
		return -r/K*V*V+r*V-r/K+r/V;
	}
}
 