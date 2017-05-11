package IMTRBM;

import IMTRBM.IMTRBM;
import com.syvys.jaRBM.Layers.Layer;
import com.syvys.jaRBM.Layers.StochasticBinaryLayer;

import IMTRBM.CDLearner;

public class IMTRBMTool {
	
	private static int _hidUnits = 100;
	private static int _iterNum = 100;
	private static double _learningRate = 0.01;
	private static double _momentum = 0.9;
	
	//training algorithm of the RBM model
	public static IMTRBM train(double[][][] data, boolean[][] isMissing){
		
		return train(data, isMissing, _hidUnits, _iterNum, _learningRate, _momentum);
	}
	
	public static IMTRBM train(double[][][] data, boolean[][] isMissing, int hidUnits, int iterNum, double learningRate, double momentum){
		
		//initialize the RBM model
		int visUnits = data[0][0].length;
		
		Layer visibleLayer = new StochasticBinaryLayer(visUnits);
		Layer hiddenLayer = new StochasticBinaryLayer(hidUnits);
		visibleLayer.setMomentum(momentum);
		hiddenLayer.setMomentum(momentum);
		visibleLayer.setLearningRate(learningRate);
		hiddenLayer.setLearningRate(learningRate);
		
		IMTRBM rbm = new IMTRBM(visibleLayer, hiddenLayer, data.length);
		
		rbm.setLearningRate(learningRate);
		rbm.setMomentum(momentum);
		double[][] prevConWeightUpdate = new double[rbm.getNumVisibleUnits()][rbm.getNumHiddenUnits()];
		
		//the length of the binary encoding vector
		int classifyNum = data.length;
		
		//begin training process
		for(int k = 0; k < iterNum; k++){
			double err = 0;
			for(int i = 0; i < data[0].length; i++){
				
				double[][] tmp = new double[classifyNum][];
				for(int j = 0; j < classifyNum; j++)
					tmp[j] = data[j][i].clone();
				for(int j = 0; j < isMissing[i].length; j++){
					if(isMissing[i][j]){
						for(int m = 0; m < classifyNum; m++)
							tmp[m][j] = 0.;
					}
				}
				
				err += CDLearner.Learn(rbm, tmp , 1, isMissing[i], prevConWeightUpdate);
			}
			System.out.println("Iteration num: " + k + "error: " + err/data[0].length);
		}
		
		//the successfully trained RBM model
		return rbm;
	}
	
	//prediction algorithm of the RBM model
	public static double[][] predict(IMTRBM rbm, double[][] input, boolean[] isMissing){
		
		//input do not satisfy requirement
		if(input.length != rbm.getClassifyNum() || input[0].length != rbm.getVisibleLayer().getNumUnits())
			return null;
		
		double[][] tmp = new double[input.length][];
		for(int i = 0; i < input.length; i++)
			tmp[i] = input[i].clone();
		
		for(int i = 0; i < isMissing.length; i++)
			if(isMissing[i]){
				for(int j = 0; j < input.length; j++)
					tmp[j][i] = 0;
			}
		
		double[][] hiddenActivity = rbm.getHiddenActivitiesFromVisibleData(tmp);
		double[][] visibleActivity = rbm.getVisibleActivitiesFromHiddenData(hiddenActivity);
		
		//predicted probability of interactions
		return visibleActivity;
	}

}
