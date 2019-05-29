import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.security.*;

public  class VisualSimulations{
	// model
	int nPartition;
	double K;
	double r;
	double C0;

	// field
	int H,W;
	int S;
	int T;
	int maxT;
	double[][] field;
	double[][] disturbance;
	int[] sheep_row;
	int[] sheep_col;
	
	boolean isInside(int r, int c) {
		return (r >= 0 && r < H && c >= 0 && c < W);
	}

	void generate(String seedStr){
		try{
			SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG"); 
			long seed = Long.parseLong(seedStr);
			r1.setSeed(seed);
			H = 50;
			W = 50;
			S = 50;
			T = 0;

			nPartition = 1000;
			r = 0.5;
			K = 100;
			C0 = 10;
			maxT = 500;
			field=new double[H][W];
			disturbance=new double[H][W];
			sheep_row=new int[S];
			sheep_col=new int[S];
			// TODO: 何か generate するべきものをする
			for(int i=0;i<H;i++){
				for(int j=0;j<W;j++){
					field[i][j]=r1.nextDouble()*K;
					disturbance[i][j]=C0;
					// nextGaussianでもいいかも
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	// simulation
	public void runTest(String seed){
		try{
			generate(seed);
			jf.setSize((W+3)*SZ+50, H*SZ+40);
			jf.setVisible(true);
			draw();
			try{Thread.sleep(3000);}
			catch(Exception e){}
			while(T < maxT){
				T++;
				try{Thread.sleep(del);}
				catch(Exception e){}
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						for(int k=0;k<nPartition;k++){
							double dt=1.0/nPartition;
							double x=field[i][j];
							double dx=x*r*(1-x/K)-disturbance[i][j]*x*x/(1+x*x);
							field[i][j]+=dt*dx;
						}
					}
				}
				draw();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	JFrame jf;
	Vis v;
	static int SZ;
	static int del;
	BufferedImage cacheBoard;
	double[][] cache;
	void draw(){
		v.repaint();
	}
	void DrawBoard(){
		if(cacheBoard == null) {
			cacheBoard = new BufferedImage(W*SZ+120,H*SZ+40,BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g2 = (Graphics2D)cacheBoard.getGraphics();
		// background
		g2.setColor(new Color(0xDDDDDD));
		g2.fillRect(0,0,W*SZ+120,H*SZ+40);
		g2.setColor(Color.WHITE);
		g2.fillRect(0,0,W*SZ,H*SZ);

		cache = new double[H][W];
		for (int i = 0; i < H; ++i)
		for (int j = 0; j < W; ++j) {
			cache[i][j] = field[i][j];
			g2.setColor(new Color(Math.max(0.0f,1.0f-(float)(cache[i][j]/K)),1.0f,Math.max(0.0f,1.0f-(float)(cache[i][j]/K))));
			g2.fillRect(j * SZ + 1, i * SZ + 1, SZ - 1, SZ - 1);
		}

		// lines between cells
		g2.setColor(Color.BLACK);
		for (int i = 0; i <= H; i++)
			g2.drawLine(0,i*SZ,W*SZ,i*SZ);
		for (int i = 0; i <= W; i++)
			g2.drawLine(i*SZ,0,i*SZ,H*SZ);

		g2.dispose();
	}
	
	static BufferedImage deepCopy(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	public class Vis extends JPanel implements WindowListener {
		public void paint(Graphics g){
			DrawBoard();

			BufferedImage bi = deepCopy(cacheBoard);
			Graphics2D g2 = (Graphics2D)bi.getGraphics();
			// current score
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Arial",Font.BOLD,14));
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(String.format("T = %d", T), W*SZ+25, 110+fm.getHeight());

			g.drawImage(bi,0,0,W*SZ+120,H*SZ+40,null);
		}
		public Vis(){}
		public void windowClosing(WindowEvent e){ }
		public void windowActivated(WindowEvent e) { }
		public void windowDeactivated(WindowEvent e) { }
		public void windowOpened(WindowEvent e) { }
		public void windowClosed(WindowEvent e) {
			System.exit(0); 
		}
		public void windowIconified(WindowEvent e) { }
		public void windowDeiconified(WindowEvent e) { }
	}
	public VisualSimulations(String seed){
		try{
			jf = new JFrame();
			v = new Vis();
			jf.getContentPane().add(v);
			runTest(seed);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		String seed = "1";
		del = 100;
		SZ = 15;
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-seed"))
				seed = args[++i];
			if(args[i].equals("-size"))
				SZ = Integer.parseInt(args[++i]);
			if(args[i].equals("-delay"))
				del = Integer.parseInt(args[++i]);
		}
		VisualSimulations vis = new VisualSimulations(seed);
	}
}