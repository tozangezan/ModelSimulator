import java.util.*;

public class DisturbanceSin extends GraphicalSimulations{
	double K=100;
	double r=2;
	double L=10;
	double C0=45;
	double w=1;
	DisturbanceSin(double V0){
		super(V0);
	}
	public static void main(String args[]){
		new DisturbanceSin(50.0);
	}
	double dV(double x, double t){
		// dV/dt=r*V*(1-V/K)-(C0+L*sin(t*w))*(V*V/(V*V+1))
		return r*x*(1-x/K)-(C0+L*Math.sin(t*w))*x*x/(x*x+1);
	}
}