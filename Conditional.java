package IMTRBM;

public class Conditional {
	public static void main(String args[]){
		int N =20;
		int[] a= new int[N];
		int[] b= new int[N];
		int sum=0;
		for(int i=0;i<N;i++)
		{
			 a[i]=(int)(Math.random()*100);
		}
		b[0]=1;
		for(int i=0;i<N-1;i++)
		{
			if(a[i+1]>a[i])
				b[i+1]=b[i]+1;
			else
				b[i+1]=b[i]-1;
			sum=sum+b[i];
		}
		sum=sum+b[9];
		for(int i=0;i<N;i++)
		{
			System.out.println(i+"\t"+a[i]+"\t"+b[i]+"\t"+sum);
		}
		
	}

}
