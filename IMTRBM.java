package IMTRBM;

import javax.swing.text.Position.Bias;

import com.syvys.jaRBM.RBMImpl;
import com.syvys.jaRBM.Layers.Layer;
import com.syvys.jaRBM.Math.Matrix;
import com.syvys.jaRBM.Layers.StochasticBinaryLayer;

public class IMTRBM extends RBMImpl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean[] isMissing;
	
	//public double[][] conWeight;
	
	public double[][] iniInput;
	
	public double[][][] multiVisHidWeights;
	
	public double[][][] visHidWeightsIncrement;
	
	public Layer[] visibleLayers;
	
	public double[][] inputData;
	
	private int classifyNum;
	
	public double[][] conWeight;

	public IMTRBM(Layer VisibleLayer, Layer HiddenLayer, int classifyNum) {
		super(VisibleLayer, HiddenLayer);
		
		this.classifyNum = classifyNum;
		// TODO Auto-generated constructor stub
		
		//zero the visible-hidden weights
		/*
		for(int i = 0; i < this.visibleHiddenWeights.length; i++)
			for(int j = 0; j < this.visibleHiddenWeights[i].length; j++)
				this.visibleHiddenWeights[i][j] = 0.;
		*/
		
		//this.isSemi = true;
		this.multiVisHidWeights = new double[classifyNum][][];
		this.visHidWeightsIncrement = new double[classifyNum][][];
		this.visibleLayers = new Layer[classifyNum];
		for(int i = 0; i < classifyNum; i++){
			this.visibleLayers[i] = this.visibleLayer.clone();
			this.multiVisHidWeights[i] = new double[this.visibleHiddenWeights.length][];
			this.visHidWeightsIncrement[i] = new double[this.visibleHiddenUpdateIncrement.length][];
			for(int j = 0; j < this.visibleHiddenWeights.length; j++){
				this.multiVisHidWeights[i][j] = this.visibleHiddenWeights[j].clone();
				this.visHidWeightsIncrement[i][j] = this.visibleHiddenUpdateIncrement[j].clone();
			}
		}
		conWeight = new double[VisibleLayer.getNumUnits()][HiddenLayer.getNumUnits()];
		conWeight = Matrix.randomizeElements(conWeight);
	}

	public IMTRBM(Layer VisibleLayer, Layer HiddenLayer, double learningRate,
			double weightCost, double momentum, int classifyNum) {
		super(VisibleLayer, HiddenLayer, learningRate, weightCost, momentum);
		// TODO Auto-generated constructor stub
		
		this.classifyNum = classifyNum;
		
		//zero the visible-hidden weights
		/*
		for(int i = 0; i < this.visibleHiddenWeights.length; i++)
			for(int j = 0; j < this.visibleHiddenWeights[i].length; j++)
				this.visibleHiddenWeights[i][j] = 0.;
		*/
		
		//this.isSemi = true;
		this.multiVisHidWeights = new double[classifyNum][][];
		this.visHidWeightsIncrement = new double[classifyNum][][];
		this.visibleLayers = new Layer[classifyNum];
		for(int i = 0; i < classifyNum; i++){
			this.visibleLayers[i] = this.visibleLayer.clone();
			this.multiVisHidWeights[i] = new double[this.visibleHiddenWeights.length][];
			this.visHidWeightsIncrement[i] = new double[this.visibleHiddenUpdateIncrement.length][];
			for(int j = 0; j < this.visibleHiddenWeights.length; j++){
				this.multiVisHidWeights[i][j] = this.visibleHiddenWeights[j].clone();
				this.visHidWeightsIncrement[i][j] = this.visibleHiddenUpdateIncrement[j].clone();
			}
		}
		
		conWeight = new double[VisibleLayer.getNumUnits()][HiddenLayer.getNumUnits()];
		conWeight = Matrix.randomizeElements(conWeight);
	}
	
	public double[][] getDownwardSWSum(double[][] batchHiddenData, double[][]batchVisibleData){

		return Matrix.multiplyTranspose(batchHiddenData, this.visibleHiddenWeights);
		
	}
	
    public double[][] getUpwardSWSum(double[][] batchVisibleData) {
    	
    	double[][] tmp = new double[1][this.getHiddenLayer().getNumUnits()];
    	//Matrix.zero(tmp);
    	
    	for(int i = 0; i < batchVisibleData.length; i++){
    		double[][] data = {batchVisibleData[i]};
    		//tmp = Matrix.add(tmp, Matrix.multiply( data, this.multiVisHidWeights[i]) );
    		double[][] ini = {this.iniInput[i]};
    		tmp = Matrix.add(tmp,  
    				Matrix.add( Matrix.multiply(data, this.multiVisHidWeights[i]), 
                       		Matrix.multiply(ini, this.conWeight)));
    	}
    	
    	return tmp;
    }
	
    public void UpdateVisibleBiases(double[][] data, double[][] negativePhaseVisibleActivities) {
    	
    	double[] bias = this.visibleLayer.getBiases().clone();
    	double[] biasIncrement = this.visibleLayer.getBiasIncrement().clone();
    	
        this.visibleLayer.updateBiases(data, negativePhaseVisibleActivities);
        
        double[] biasNew = this.visibleLayer.getBiases().clone();
        double[] biasIncrementNew = this.visibleLayer.getBiasIncrement().clone();
        
        for(int i= 0; i < this.getNumVisibleUnits(); i++){
        	if(this.isMissing[i]) {
        		biasNew[i] = bias[i];
        		//biasIncrementNew[i] = biasIncrement[i];
        		biasIncrementNew[i] = 0;
        	}
        }
        
        this.visibleLayer.setBiases(biasNew);
        this.visibleLayer.setBiasIncrement(biasIncrementNew);
    }
    
    public double[][] getVisibleActivitiesFromHiddenData(double[][] hiddenData){
    	
    	double[][] visActivities = new double[classifyNum][];
    	
    	for(int i = 0; i < classifyNum; i++){
    		this.setLayerWeight(i);
    		visActivities[i] = super.getVisibleActivitiesFromHiddenData(hiddenData)[0].clone();
    	}
    	
    	return visActivities;
    }
    
    public void setLayerWeight(int i){
    	this.visibleHiddenWeights = this.multiVisHidWeights[i];
    	this.visibleLayer = this.visibleLayers[i];
    	this.visibleHiddenUpdateIncrement = this.visHidWeightsIncrement[i];
    }
    
    public int getClassifyNum() {
    	return classifyNum;
    }

}
