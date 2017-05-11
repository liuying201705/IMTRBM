package IMTRBM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataInput1 {
	ArrayList<String> mirlist = new ArrayList<String>();
	ArrayList<String> genelist = new ArrayList<String>();
	ArrayList<String> weightlist = new ArrayList<String>(){
        {
        	add("3");
            add("2");     //****1
            add("1");
        }
    };
    public DataInput1(double datafull[][][]){
    	this.datafull = datafull;
    	String filetitle = "test_data\\input\\";
		String filename1 = filetitle+"miRNA_ID.txt";//
		String filename2 = filetitle+"gene_ID.txt";
		String filename3 = filetitle+"weighted_network.txt";   //
		ReadDataMirna(filename1);
		ReadDataGene(filename2);
		ReadData(filename3);
    }

	double datafull[][][];
	
	public void ReadDataMirna(String fileName){
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            System.out.println("Read the contents of the file in rows, read one line at a time: ");
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            int line = 1;
	            while ((tempString = reader.readLine()) != null) {
	                //System.out.print("line " + line + ": " );//+ tempString);
	                mirlist.add(tempString.trim().toLowerCase());
	                //System.out.println(mirlist.get(line-1));
	                line++;
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	public void ReadDataGene(String fileName){
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            System.out.println("Read the contents of the file in rows, read one line at a time: ");
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            int line = 1;
	            while ((tempString = reader.readLine()) != null) {
	               // System.out.println("line " + line + ": " + tempString);
	                genelist.add(tempString.trim().toLowerCase());
	              //  System.out.println(genelist.get(line-1));
	                line++;
	            }
	    		System.out.println(genelist.size());		
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	}
	
	public void ReadData(String fileName){
		 File file = new File(fileName);
	        BufferedReader reader = null;
	        try {
	            System.out.println("----------------");
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            int line = 1;
	            System.out.println(genelist.get(0));
	            
	            while ((tempString = reader.readLine()) != null) {
	            	tempString = tempString.toLowerCase();
	                //System.out.print("line " + line + ": " );//+ tempString);
	                String[] temp=tempString.split("\t");
	                //System.out.println(temp.length);
					int index1 = mirlist.indexOf(temp[0].trim());
					int index2 = genelist.indexOf(temp[1].trim());
					int index3 = weightlist.indexOf(temp[2].trim());
					System.out.println(line+":"+index2+"<<>>"+index1+"<<>>"+index3+"**"+temp[0]+"**"+temp[1]+"**"+temp[2]);
					
					datafull[index3][index2][index1] = 1;
	                line++;
	            }
	            System.out.println(line);
	    			
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
}

