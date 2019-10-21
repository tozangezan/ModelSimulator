import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.security.*;

public  class VisualSimulations{
	// constants
	public static final int GRAPH_W = 480;
	public static final int GRAPH_H = 360;

	public static final int MAX_S = 1000;

	public static final double EPS = 1e-9;
	// basic tools
	SecureRandom r1;
	int[] dr={1,1,1,0,0,-1,-1,-1};
	int[] dc={1,0,-1,1,-1,1,0,-1};
	static boolean debug;
	static boolean regular;
	static boolean hillclimb;
	static boolean hillclimb2;
	static boolean torus;
	static boolean window;
	static boolean control;
	static int central;
	// model
	int nPartition;
	double K;
	double r;
	double C0;
	static double d;
	double M;

	// field
	int H,W;
	int O;
	static int S;
	static int WATER;
	static int CAMP;
	static int SHEEP_G;
	int T;
	int activeCells;
	int maxT;
	double[][] field;
	double[][] new_field;
	
	double[][] disturbance;
	int[] sheep_row;
	int[] sheep_col;
	int[] sheep_pressure;
	
	int[] water_row;
	int[] water_col;
	int[] center;
	int[] camp_row;
	int[] camp_col;
	int[][] nearest_water;
	double[][] dist_from_water;
	int MAX_DIST;
	static double MAX_h = 2;
	// log
	double[] log_total;
	double[] log_productivity;
	double[] log_grazing;
	double[] log_variance;
	double[] log_skewness;
	boolean isInside(int r, int c) {
		if(torus)return true;
		return (r >= 0 && r < H && c >= 0 && c < W);
	}
	boolean isInsideFence(int r, int c) {
		if(torus)return true;
		return (r >= O && r < H-O && c >= O && c < W-O);
	}
	boolean isInsideRegion(int r,int c,int id) {
		if(central == 0) return true;
		if(central == 1){
			return (r-water_row[center[id]])*(r-water_row[center[id]])+(c-water_col[center[id]])*(c-water_col[center[id]])<=MAX_DIST;
		}
		if(central == 2){
			if((r-water_row[center[id]])*(r-water_row[center[id]])+(c-water_col[center[id]])*(c-water_col[center[id]])>=r1.nextInt(MAX_DIST))return false;
			return true;
		}
		if(central == 4){
			int cr=camp_row[center[id]];
			int cc=camp_col[center[id]];
			int wr=water_row[nearest_water[cr][cc]];
			int wc=water_col[nearest_water[cr][cc]];
			if(Math.pow((r-wr)*(r-wr)+(c-wc)*(c-wc),0.25)+Math.pow((r-cr)*(r-cr)+(c-cc)*(c-cc),0.25)
				>2*Math.pow(MAX_h*MAX_h+(double)((cr-wr)*(cr-wr)+(cc-wc)*(cc-wc))/4,0.25))return false;
			// System.out.printf("%d %d %d: %f %f\n",r,c,id,Math.pow((r-wr)*(r-wr)+(c-wc)*(c-wc),0.25)+Math.pow((r-cr)*(r-cr)+(c-cc)*(c-cc),0.25),2*Math.pow(MAX_h*MAX_h+(double)((cr-wr)*(cr-wr)+(cc-wc)*(cc-wc))/4,0.25));
			return true;
		}
		return true;
	}
	void generate(String seedStr){
		try{
			r1 = SecureRandom.getInstance("SHA1PRNG"); 
			long seed = Long.parseLong(seedStr);
			r1.setSeed(seed);
			H = 50;
			W = 50;
			O = 5;
			T = 0;
			MAX_DIST = 56;
			if(torus){
				O=0;
			}
			activeCells = (H-O*2)*(W-O*2);

			nPartition = 1000;
			r = 0.1;
			K = 100;
			C0 = 0.1;
			M = 40;
			maxT = 1000;
			field=new double[H][W];
			new_field=new double[H][W];
			disturbance=new double[H][W];
			sheep_row=new int[MAX_S];
			sheep_col=new int[MAX_S];
			sheep_pressure=new int[MAX_S];
			center=new int[MAX_S];

			water_row=new int[WATER];
			water_col=new int[WATER];
			camp_row=new int[CAMP];
			camp_col=new int[CAMP];
			nearest_water=new int[H][W];
			dist_from_water=new double[H][W];
			
			log_total=new double[maxT];
			log_productivity=new double[maxT];
			log_grazing=new double[maxT];
			log_variance=new double[maxT];
			log_skewness=new double[maxT];
			// TODO: 何か generate するべきものをする
			for(int i=0;i<H;i++){
				for(int j=0;j<W;j++){
				//	field[i][j]=r1.nextDouble()*K;
					field[i][j]=K;
					disturbance[i][j]=C0;
					if(control){
						disturbance[i][j]=C0+(double)SHEEP_G*S/activeCells;
					}
					// nextGaussianでもいいかも
				}
			}
			if(!control){
				for(int i=0;i<S;i++){
					if(regular) {
						int index=(H-2*O)*(W-2*O)*i/S;
						sheep_row[i]=index/(W-2*O)+O;
						sheep_col[i]=index%(W-2*O)+O;
					}else{
						sheep_row[i]=r1.nextInt(H-2*O)+O;
						sheep_col[i]=r1.nextInt(W-2*O)+O;
					}
					sheep_pressure[i]=SHEEP_G;
				}

				for(int i=0;i<S;i++){
					disturbance[sheep_row[i]][sheep_col[i]]+=sheep_pressure[i];
				}
			}
			for(int i=0;i<WATER;i++){
				water_row[i]=r1.nextInt(H-2*O)+O;
				water_col[i]=r1.nextInt(W-2*O)+O;
			}
			for(int i=0;i<H;i++)for(int j=0;j<W;j++){
				dist_from_water[i][j]=1000000007;
			}
			for(int i=0;i<WATER;i++){
				for(int j=0;j<H;j++){
					for(int k=0;k<W;k++){
						if(dist_from_water[j][k]>Math.sqrt((water_row[i]-j)*(water_row[i]-j)+(water_col[i]-k)*(water_col[i]-k))){
							nearest_water[j][k]=i;
							dist_from_water[j][k]=Math.sqrt((water_row[i]-j)*(water_row[i]-j)+(water_col[i]-k)*(water_col[i]-k));
						}
					}
				}
			}
			for(int i=0;i<CAMP;i++){
				camp_row[i]=r1.nextInt(H-2*O)+O;
				camp_col[i]=r1.nextInt(W-2*O)+O;
			}
			
			if(central > 0){
				if(central <= 3){
					for(int i=0;i<S;i++){
						disturbance[sheep_row[i]][sheep_col[i]]-=sheep_pressure[i];
						
						while(true){
							center[i]=r1.nextInt(WATER);
							sheep_row[i]=r1.nextInt(H-2*O)+O;
							sheep_col[i]=r1.nextInt(H-2*O)+O;
							if((sheep_row[i]-water_row[center[i]])*(sheep_row[i]-water_row[center[i]])+
								(sheep_col[i]-water_col[center[i]])*(sheep_col[i]-water_col[center[i]])<=MAX_DIST)break;
						}
						disturbance[sheep_row[i]][sheep_col[i]]+=sheep_pressure[i];
					}
				}else{
					for(int i=0;i<S;i++){
						center[i]=r1.nextInt(CAMP);
						disturbance[sheep_row[i]][sheep_col[i]]-=sheep_pressure[i];
						while(true){
							
							sheep_row[i]=r1.nextInt(H-2*O)+O;
							sheep_col[i]=r1.nextInt(H-2*O)+O;
							if(isInsideRegion(sheep_row[i],sheep_col[i],i))break;
						}
						disturbance[sheep_row[i]][sheep_col[i]]+=sheep_pressure[i];
					}
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
			if(window){
				jf.setSize((W+3)*SZ+550, H*SZ+40);
				jf.setVisible(true);
				draw();

				try{Thread.sleep(3000);}
				catch(Exception e){}
			}
			while(T < maxT){
				log_total[T]=0;
				log_grazing[T]=0;
				log_productivity[T]=0;
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						if(isInsideFence(i,j)){
							log_total[T]+=field[i][j];
						}
					}
				}
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						if(isInsideFence(i,j)){
							log_variance[T]+=(field[i][j]-log_total[T]/activeCells)*(field[i][j]-log_total[T]/activeCells);
							log_skewness[T]+=Math.pow(field[i][j]-log_total[T]/activeCells,3.0);
						}
					}
				}
				log_variance[T]/=activeCells;
				log_skewness[T]/=activeCells;
				log_skewness[T]/=Math.pow(log_variance[T],1.5);

				if(window){
					try{Thread.sleep(del);}
					catch(Exception e){}
				}
				
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						double x=field[i][j];
						double dx=x*r*(1-x/K)-disturbance[i][j]*x*x/(M+x*x);
						if(isInsideFence(i,j)){
							log_productivity[T]+=x*r*(1-x/K);
							log_grazing[T]+=Math.min(x+x*r*(1-x/K),(disturbance[i][j]-C0)*x*x/(M+x*x));
						}
						new_field[i][j]=field[i][j]+dx;
						new_field[i][j]=Math.max(new_field[i][j],0.0);	
					}
				}
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						field[i][j]=new_field[i][j];
						new_field[i][j]=0;
					}
				}
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						for(int k=0;k<8;k++){
							if(torus){
								new_field[i][j]+=field[(i+dr[k]+H)%H][(j+dc[k]+W)%W]*d/8;
							}else if(isInside(i+dr[k],j+dc[k])){
								new_field[i][j]+=field[i+dr[k]][j+dc[k]]*d/8;
							}
						}
						new_field[i][j]-=field[i][j]*d;
					}
				}
				for(int i=0;i<H;i++){
					for(int j=0;j<W;j++){
						field[i][j]+=new_field[i][j];
					}
				}

				if(debug){
					for(int i=0;i<H;i++){
						for(int j=0;j<W;j++){
							if(j>0)System.out.print(" ");
							System.out.print(field[i][j]);
						}
						System.out.println();
					}
				}

				if(log_total[T]<0.2*K*activeCells||T==maxT-1){ // biomass 2割未満 で終了
					double total_grazing = 0;
					for(int i=0;i<=T;i++)total_grazing+=log_grazing[i];
					double total_production = 0;
					for(int i=0;i<=T;i++)total_production += log_productivity[i];
					
					// S [tab] SHEEP_G [tab] time [tab] grazing [tab] production
					System.out.println(S+"\t"+SHEEP_G+"\t"+T+"\t"+total_grazing+"\t"+total_production);

					System.exit(0);
				}

				// update log data
				T++;
				if(window)draw();
				if(!control){
					for(int i=0;i<S;i++){
						int tr,tc;
						if(regular){
							tr=sheep_row[i];
							tc=sheep_col[i]+1;
							if((torus&&tc==W)||!isInsideFence(tr,tc)){
								tr++;
								tc=O;
								if((torus&&tr==H)||!isInsideFence(tr,tc)){
									tr=O;
								}
							}
						}else if(hillclimb){
							tr=sheep_row[i];
							tc=sheep_col[i];
							for(int j=0;j<8;j++){
								int tmp_tr=(sheep_row[i]+dr[j]+H)%H;
								int tmp_tc=(sheep_col[i]+dc[j]+W)%W;
								if(isInsideRegion(tmp_tr,tmp_tc,i)&&isInsideFence(tmp_tr,tmp_tc)&&disturbance[tmp_tr][tmp_tc]<C0+EPS){
									if(field[tr][tc]<field[tmp_tr][tmp_tc]){
										tr=tmp_tr;
										tc=tmp_tc;
									}
								}
							}
						}else if(hillclimb2){
							tr=sheep_row[i];
							tc=sheep_col[i];
							for(int j=-2;j<=2;j++){
								for(int k=-2;k<=2;k++){
									int tmp_tr=(sheep_row[i]+j+H)%H;
									int tmp_tc=(sheep_col[i]+k+W)%W;
									if(isInsideFence(tmp_tr,tmp_tc)&&disturbance[tmp_tr][tmp_tc]<C0+EPS){
										if(field[tr][tc]<field[tmp_tr][tmp_tc]){
											tr=tmp_tr;
											tc=tmp_tc;
										}
									}
								}
							}
						}else{
							int direction=r1.nextInt(8);
							tr=(sheep_row[i]+dr[direction]+H)%H;
							tc=(sheep_col[i]+dc[direction]+W)%W;
							if(!isInsideFence(tr,tc)){
								tr=sheep_row[i];
								tc=sheep_col[i];
							}
						}
						disturbance[sheep_row[i]][sheep_col[i]]-=sheep_pressure[i];
						sheep_row[i]=tr;
						sheep_col[i]=tc;
						disturbance[sheep_row[i]][sheep_col[i]]+=sheep_pressure[i];
					}
				}
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
			cacheBoard = new BufferedImage(W*SZ+600,H*SZ+40,BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g2 = (Graphics2D)cacheBoard.getGraphics();
		// background
		g2.setColor(new Color(0xFFFFFF));
		g2.fillRect(0,0,W*SZ+600,H*SZ+40);
		g2.setColor(Color.WHITE);
		g2.fillRect(0,0,W*SZ,H*SZ);

		cache = new double[H][W];
		for (int i = 0; i < H; ++i)
		for (int j = 0; j < W; ++j) {
			cache[i][j] = field[i][j];
			g2.setColor(new Color(0.0f, 1.0f, 0.0f, Math.min(1.0f, Math.max(0.0f,(float)(cache[i][j]/K)))));
			g2.fillRect(j * SZ + 1, i * SZ + 1, SZ - 1, SZ - 1);
		}
		if(!control){
			for(int j=0;j<S;j++){
				g2.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
				g2.fillRect(sheep_col[j] * SZ + 1, sheep_row[j] * SZ + 1, SZ - 1, SZ - 1);
			}
		}
		for(int j=0;j<WATER;j++){
			g2.setColor(new Color(0.0f, 0.0f, 1.0f, 0.5f));
			g2.fillRect(water_col[j] * SZ + 1, water_row[j] * SZ + 1, SZ - 1, SZ - 1);
		}
		for(int j=0;j<CAMP;j++){
			g2.setColor(new Color(1.0f, 0.0f, 1.0f, 0.5f));
			g2.fillRect(camp_col[j] * SZ + 1, camp_row[j] * SZ + 1, SZ - 1, SZ - 1);
		}
		
		// lines between cells
		g2.setColor(Color.BLACK);
		for (int i = 0; i <= H; i++)
			g2.drawLine(0,i*SZ,W*SZ,i*SZ);
		for (int i = 0; i <= W; i++)
			g2.drawLine(i*SZ,0,i*SZ,H*SZ);
		g2.setStroke(new BasicStroke(3.0f));
		g2.drawLine(O*SZ,O*SZ,(W-O)*SZ,O*SZ);
		g2.drawLine(O*SZ,(H-O)*SZ,(W-O)*SZ,(H-O)*SZ);
		g2.drawLine(O*SZ,O*SZ,O*SZ,(H-O)*SZ);
		g2.drawLine((W-O)*SZ,O*SZ,(W-O)*SZ,(H-O)*SZ);

		g2.dispose();
	}
	void DrawGraph(){
		if(cacheBoard == null) {
			cacheBoard = new BufferedImage(W*SZ+600,H*SZ+40,BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g2 = (Graphics2D)cacheBoard.getGraphics();
		// background
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3.0f));
		g2.drawLine(W*SZ+25,450,W*SZ+25+GRAPH_W,450);
		g2.drawLine(W*SZ+25,450,W*SZ+25,450-GRAPH_H);
	
		g2.setStroke(new BasicStroke(2.0f));

		for(int i=1;i<Math.min(T,GRAPH_W);i++){
			g2.setColor(Color.GREEN);
			g2.drawLine(W*SZ+25+i-1,(int)(450-log_total[Math.max(0,T-GRAPH_W)+i-1]/K/activeCells*GRAPH_H),W*SZ+25+i,(int)(450-log_total[Math.max(0,T-GRAPH_W)+i]/K/activeCells*GRAPH_H));
			g2.setColor(Color.BLUE);
			g2.drawLine(W*SZ+25+i-1,(int)(450-log_productivity[Math.max(0,T-GRAPH_W)+i-1]/K/activeCells*GRAPH_H*5),W*SZ+25+i,(int)(450-log_productivity[Math.max(0,T-GRAPH_W)+i]/K/activeCells*GRAPH_H*5));
			g2.setColor(Color.RED);
			g2.drawLine(W*SZ+25+i-1,(int)(450-log_grazing[Math.max(0,T-GRAPH_W)+i-1]/K/activeCells*GRAPH_H*5),W*SZ+25+i,(int)(450-log_grazing[Math.max(0,T-GRAPH_W)+i]/K/activeCells*GRAPH_H*5));
			g2.setColor(Color.ORANGE);
			g2.drawLine(W*SZ+25+i-1,(int)(450-log_variance[Math.max(0,T-GRAPH_W)+i-1]*GRAPH_H/K/K*8),W*SZ+25+i,(int)(450-log_variance[Math.max(0,T-GRAPH_W)+i]*GRAPH_H/K/K*8));
			g2.setColor(Color.PINK);
			if(!control)g2.drawLine(W*SZ+25+i-1,(int)(225-log_skewness[Math.max(0,T-GRAPH_W)+i-1]*GRAPH_H),W*SZ+25+i,(int)(225-log_skewness[Math.max(0,T-GRAPH_W)+i]*GRAPH_H));
			
		}

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
			DrawGraph();

			BufferedImage bi = deepCopy(cacheBoard);
			Graphics2D g2 = (Graphics2D)bi.getGraphics();
			// current score
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Arial",Font.BOLD,14));
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(String.format("T = %d", T), W*SZ+25, 25+fm.getHeight());

			g.drawImage(bi,0,0,W*SZ+600,H*SZ+40,null);
		}
		public Vis(){}
		public void windowClosing(WindowEvent e){
			System.exit(0);
		}
		public void windowActivated(WindowEvent e) { }
		public void windowDeactivated(WindowEvent e) {
			System.exit(0);
		}
		public void windowOpened(WindowEvent e) { }
		public void windowClosed(WindowEvent e) {
			System.exit(0); 
		}
		public void windowIconified(WindowEvent e) { }
		public void windowDeiconified(WindowEvent e) { }
	}
	public VisualSimulations(String seed){
		try{
			if(window){
				jf = new JFrame();
				v = new Vis();
				jf.getContentPane().add(v);
			}
			runTest(seed);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		String seed = "1";
		del = 100;
		SZ = 15;
		d = 0.02;
		debug = false;
		regular = false;
		window = true;
		control = false;
		central = 0;
		SHEEP_G = 50;
		WATER = 0;
		CAMP = 0;
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-seed"))
				seed = args[++i];
			if(args[i].equals("-size"))
				SZ = Integer.parseInt(args[++i]);
			if(args[i].equals("-delay"))
				del = Integer.parseInt(args[++i]);
			if(args[i].equals("-debug"))
				debug = true;
			if(args[i].equals("-regular"))
				regular = true;
			if(args[i].equals("-hillclimb"))
				hillclimb = true;
			if(args[i].equals("-hillclimb2"))
				hillclimb2 = true;
			if(args[i].equals("-central1"))
				central = 1;
			if(args[i].equals("-central2"))
				central = 2;
			if(args[i].equals("-central3"))
				central = 3;
			if(args[i].equals("-central4"))
				central = 4;
			if(args[i].equals("-torus"))
				torus = true;
			if(args[i].equals("-grazing"))
				SHEEP_G = Integer.parseInt(args[++i]);
			if(args[i].equals("-sheep"))
				S = Integer.parseInt(args[++i]);
			if(args[i].equals("-nowindow"))
				window = false;
			if(args[i].equals("-migration"))
				d = Double.parseDouble(args[++i]);
			if(args[i].equals("-control"))
				control = true;
			if(args[i].equals("-water"))
				WATER = Integer.parseInt(args[++i]);
			if(args[i].equals("-camp"))
				CAMP = Integer.parseInt(args[++i]);
		}
		VisualSimulations vis = new VisualSimulations(seed);
	}
}