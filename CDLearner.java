package IMTRBM;

import com.syvys.jaRBM.RBM;
import com.syvys.jaRBM.Math.Matrix;
import com.syvys.jaRBM.RBMLearn.CDStochasticRBMLearner;

import IMTRBM.IMTRBM;

public class CDLearner extends CDStochasticRBMLearner {
	
	public CDLearner(){
		super();
	}
    
    public static double Learn(IMTRBM rbm, double[][] data, int numGibbsIterations, boolean[] isMissing, double[][] prevConWeightUpdate) {
    	
    	rbm.isMissing = isMissing;
    	rbm.iniInput = data;
    	
        double[][] hiddenActivities = rbm.getHiddenActivitiesFromVisibleData(data);
        // negative phase
        double[][] negPhaseVisible = rbm.getVisibleActivitiesFromHiddenData(hiddenActivities);
        
        double[][] negPhaseHidden = rbm.getHiddenActivitiesFromVisibleData(negPhaseVisible);
        
        
        try {
        	for( int dataType = 0; dataType < rbm.getClassifyNum(); dataType++) {
        		//double[][] weightUpdates = getConnectionWeightUpdates(rbm, data, hiddenData, negPhaseVisible, negPhaseHidden);
        		//set different visible layers
        		rbm.setLayerWeight(dataType);
        		
        		double[][] posVis = {data[dataType]}, negVis = {negPhaseVisible[dataType]};
        		
        		double[][] weightUpdates = getConnectionWeightUpdates(rbm, posVis, hiddenActivities, negVis, negPhaseHidden);
        		for(int i = 0; i < rbm.getNumVisibleUnits(); i++){
        			if(isMissing[i])
        				for(int j = 0; j < rbm.getNumHiddenUnits(); j++)
        					//the weights of the interactions to be predicted is not updated
        					weightUpdates[i][j] = 0.0;
        		}
        		updateWeights(rbm, weightUpdates);
        		rbm.UpdateHiddenBiases(hiddenActivities, negPhaseHidden);
        		rbm.UpdateVisibleBiases(posVis, negVis);
        	}
            
        } catch (UnsupportedOperationException unSupportedEx) {
            try {
            	for( int dataType = 0; dataType < rbm.getClassifyNum(); dataType++) {
            		//set different visible layers
            		rbm.setLayerWeight(dataType);
            		double[][] posVis = {data[dataType]}, negVis = {negPhaseVisible[dataType]};
            		double[] weightUpdates = getConnectionWeightUpdatesVector(rbm, data, hiddenActivities, negPhaseVisible, negPhaseHidden);
            		int hiddenNum = rbm.getNumHiddenUnits();
            		for(int i = 0; i < hiddenNum; i++){
            			if(isMissing[i]){
            				for(int j = 0; j < rbm.getNumHiddenUnits(); j++)
            					//the weights of the interactions to be predicted is not updated
            					weightUpdates[i*hiddenNum + j] = 0.0;
            			}
            		}
            		updateWeights(rbm, weightUpdates);
            		rbm.UpdateHiddenBiases(hiddenActivities, negPhaseHidden);
            		rbm.UpdateVisibleBiases(posVis, negVis);
            	}
            } catch (UnsupportedOperationException ex) {
                throw new UnsupportedOperationException("CDStochasticRBMLearner.Learn(RBM, double[][]) : " + ex);
            }
        }
        
        //updating the conditional weights
        double[][] tmp = rbm.conWeight;
        for(int i = 0; i < tmp.length; i++){
        	if(isMissing[i])
        		continue;
        	for(int m = 0; m < data.length; m++){
        		if(data[m][i] > 0.5){
        			for(int k = 0; k < tmp[i].length; k++){
        				prevConWeightUpdate[i][k] = rbm.getMomentum() * prevConWeightUpdate[i][k] + (( hiddenActivities[0][k] - negPhaseHidden[0][k] ) - rbm.getWeightCost() * tmp[i][k]) * rbm.getLearningRate();
        				tmp[i][k] += prevConWeightUpdate[i][k];
        			}
        			//break;               //20161210¸Ä×¢ÊÍµô
        		}
        	}
        }
        
        hiddenActivities = rbm.getHiddenActivitiesFromVisibleData(data);
        negPhaseVisible = rbm.getVisibleActivitiesFromHiddenData(hiddenActivities);
        for(int i = 0; i < isMissing.length; i++){
        	if(isMissing[i]){
        		for(int j = 0; j < negPhaseVisible.length; j++){
        			negPhaseVisible[j][i] = data[j][i];
        		}
        	}
        }
        return Matrix.getMeanSquaredError(data, negPhaseVisible);
    }

}
