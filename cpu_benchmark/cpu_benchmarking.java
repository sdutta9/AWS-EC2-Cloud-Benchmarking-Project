import java.util.Scanner;

//Below is my defined thread which extends from Thread class.
class myThread extends Thread {

	long iterations;
	String vartype;
	
	// Constructor takes type(Selection type:float or int) and the number of iterations.
	myThread(String type, long cycles){
		vartype= type;
		iterations = cycles;
	}
	//Below method is for performing 5 integer related operations.
	void calculate(int a, int b){
		a= b+92;
		a-=b;
		a*=b;
		a+=b;
		b= a+25;
	}
	
	//Below method is for performing 5 float related operations.
	void calculate(float a, float b){
		a= b+92;
		a-=b;
		a*=b;
		a+=b;
		b= a+25;
		
	}
	//Below is the abstract method from Class Thread which needs to be implemented in my custom thread class
	public void run(){
		/*If the selection type = integer then I call Calculate function which does integer operations.
		This function runs iterations number of times.*/
		if(vartype.equalsIgnoreCase("integer")){
			for(long i = 0;i<iterations; i++){
				calculate(10,6);
			}
		}
		/*If the selection type != integer then it is for float. So I call Calculate function which does float operations.
		This function runs iterations number of times.*/
		else{
			for(long i = 0;i<iterations; i++){
				calculate(10.3f,6.7f);
			}
		}
		
	}
}

public class cpu_benchmarking{
	
	public static void main(String args[]) {
		
		try{									
			int selection, threadnum;
			
			/* Below block takes users preference like do they want to run benchmark for float or integer and 
			also the number of threads. The program falls into this block if we no command line argument has been passed.*/ 
			if(args.length<2){
			Scanner sc = new Scanner(System.in);
			
			System.out.println("Enter type of datatype:\n");
			System.out.println("1. Float");
			System.out.println("2. Integer");
			System.out.print("\nEnter your choice:");
			selection = sc.nextInt();
			System.out.print("\nEnter the number of threads:");
			threadnum = sc.nextInt();
			sc.close();
			}
			//The program falls into this block if command line argument is provided during program execution.
			else{
				selection = Integer.parseInt(args[0]);
				threadnum = Integer.parseInt(args[1]);
			}
			//This block calls the benchmarking function based on the selection type (ie Float or integer)			
			if((selection==1) || (selection==2)){
				
				if(selection == 1)
					benchmarking("float",1000000000, threadnum);
				else
					benchmarking("integer", 1000000000, threadnum);
					
			}
			else
				System.out.println("ERROR: Invalid choice number");
			
			//benchmarking("float", 1000000000, 1);
			//benchmarking("float", 1000000000, 2);
			//benchmarking("float", 1000000000, 4);
			
			//benchmarking("integer", 1000000000, 1);
			//benchmarking("integer", 1000000000, 2);
			//benchmarking("integer", 1000000000, 4);
			
		}catch(ArithmeticException ae){}
	}
	
	//This static function is the main function which does all the benchmarking work.
	public static void benchmarking(String type, long iterations, int numofthreads) throws ArithmeticException {
		
		myThread[] threadArr = new myThread[numofthreads]; // Created an array of my custom thread class.
		
		long split_iteration = iterations/numofthreads; // Distribute total number of iterations among the total number of threads
		
		
		long startTime = System.currentTimeMillis(); // This line captures the wall clock time before the start of actual computation.
		
		//This block creates new instances of the thread object and calls the start method for each thread.
		for(int i=0;i<numofthreads;i++){
			threadArr[i]=new myThread(type, split_iteration);
			threadArr[i].start();
		}
		try{
			//This block joins all the threads that were created in the last block.
			for(int i=0;i<numofthreads;i++)
				threadArr[i].join();
		}
		catch(InterruptedException ie){
			System.out.println(ie.toString());
		}
		
		// Below line capture the wall clock time as our progrom would reach this point when all thread have finished their execution
		long endTime = System.currentTimeMillis(); 
		//System.out.println("Start Time: "+startTime);
		
		//System.out.println("End Time: "+endTime);
	    
	    long runTime = endTime - startTime;//This line calculate the runtime based on the starttime and endtime.
	    System.out.println("\nValues for "+ type +" type for "+numofthreads+" Thread\n");
	    System.out.println("Total running time: "+runTime);
	    
	    
	    /*Below block based on the selection type (integer or float) calculates the iops and giops or flops and gflops.*/
	    if(type.equalsIgnoreCase("integer")){
	    	long iops = 0 , giops =0;
		    iops = ((iterations/runTime)*1000)*5;

		    giops = (iops/1000000000);
		    System.out.println("IOPS : " + iops);
		    System.out.println("GIOPS : " + giops);
	    }
	    else{
	    	double flops = 0 , gflops =0;
	    	
		    flops = ((iterations/runTime)*1000)*5;

		    gflops = (flops/1000000000);
		    System.out.println("FLOPS : " + flops);
		    System.out.println("GFLOPS : " + gflops+"\n");
	    }
	}
}



















