import java.util.*;

public class Disturbance2States extends GraphicalSimulations{
	double K=100;
	double r=2;
	double L=20;
	double C0=25;
	double T=1;
	Disturbance2States(double V0){
		super(V0);
	}
	public static void main(String args[]){
		new Disturbance2States(10.0);
	}
	double dV(double x, double t){
		// dV/dt=r*V*(1-V/K)-(C0+L*sin(t*w))*(V*V/(V*V+1))
		return r*x*(1-x/K)-getDisturbance(x,t)*x/(x+1);
	}
	double getDisturbance(double x, double t){
		double md=t-(int)(t/T)*T;
		if(md<T/2)return C0+L;
		else return C0-L;
	}
	double getC0FromV(double V){
		return -r/K*V*V+r*V-r/K+r/V;
	}
}
 