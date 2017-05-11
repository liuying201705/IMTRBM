package IMTRBM;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import IMTRBM.IMTRBM;
import IMTRBM.IMTRBMTool;

public class IMTRBMDemo {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		long t1 = System.currentTimeMillis();
		// TODO Auto-generated method stub
		double datafull[][][]=new double[Tools.weight][Tools.gene][Tools.mir];
		double[][] input=new double[Tools.weight][Tools.mir];
		double[][][] out = new double[Tools.weight][Tools.gene][Tools.mir];
		boolean[][] isMissing = new boolean[Tools.gene][Tools.mir];
		DataInput1 Di =new DataInput1(datafull);
		String title="test_data\\result\\";
		for(int i=0;i<isMissing.length;i++)
			for(int j=0;j<isMissing[0].length;j++)
			{//true: the interaction is used for prediction
				//false: the interaction is used for training
				// In this paper, all the data is used to train and predict
				isMissing[i][j]=false;   
			}
		long t2 = System.currentTimeMillis();
		
		IMTRBM rbm = IMTRBMTool.train(datafull, isMissing);
		//prediction
		long t3 = System.currentTimeMillis();
		System.out.println("Input time: "+(double)(t2-t1)/60000+"min");
		System.out.println("Training time: "+(double)(t3-t2)/60000+"min");
		for(int k=0;k<datafull[0].length;k++)
		{
			for(int i=0;i<datafull.length;i++)
						{
							for(int j=0;j<datafull[0][0].length;j++)
							{
									input[i][j]=datafull[i][k][j];
							}
						}
			//System.out.println(k+"\t1$");
			boolean[] missing =new boolean[datafull[0][0].length];
			for(int i=0;i<datafull[0][0].length;i++)
			{
				missing[i]=isMissing[k][i];
			}
			//System.out.println(k+"\t2$");
			double[][] output = IMTRBMTool.predict(rbm, input, missing);
			if(output == null)
				System.out.println("input format not right");
			else{
					for(int i = 0; i < output.length; i++)
						{
							String print = "";
							for(int j = 0; j < output[0].length; j++)
							{
								print += " " + output[i][j];
								out[i][k][j]= output[i][j];
							}
						}
			    }
			//System.out.println(k+"\t3$");
		}
		String file0=title+"out0.txt";//The probability when weight = 1
		String file1=title+"out1.txt";//The probability when weight = 2
		String file2=title+"out2.txt";//The probability when weight = 3
		String file4=title+"out.txt";// final results of IMTRBM
		try{
		FileWriter writer0=new FileWriter(file0);
		FileWriter writer1=new FileWriter(file1);
		FileWriter writer2=new FileWriter(file2);
		FileWriter writer4=new FileWriter(file4);
        for(int a=0;a<Tools.gene;a++)
        {
        	for(int b=0;b<Tools.mir;b++)
        	{
        		writer0.write(Double.toString(out[0][a][b])+"\t");
        		writer1.write(Double.toString(out[1][a][b])+"\t");
        		writer2.write(Double.toString(out[2][a][b])+"\t");
        	}
        	writer0.write("\n");
        	writer1.write("\n");
        	writer2.write("\n");
        }
        writer0.flush();
        writer0.close();
        writer1.flush();
        writer1.close();
        writer2.flush();
        writer2.close();
        for(int a=0;a<Tools.gene;a++)
        {
        	for(int b=0;b<Tools.mir;b++)
        	{
        		double temp=(out[2][a][b]+2*out[1][a][b]+3*out[0][a][b])*(2./(Tools.weight*(Tools.weight+1)));
        		writer4.write(Double.toString(temp)+"\t");
        	}
        	writer4.write("\n");
        }
        writer4.flush();
        writer4.close();
        }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long t4 = System.currentTimeMillis();
		System.out.println("Predicting time: "+(double)(t4-t3)/60000+"min");
		System.out.println("Total time: "+(double)(t4-t1)/3600000+"h");
		System.out.println("\nFinish !");
	}


}
