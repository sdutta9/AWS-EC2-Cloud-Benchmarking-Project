import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.Random;

//Below is my defined custom write thread which extends from Thread class.
class myThreadWrite extends Thread{
	
	byte[] block;
	int iterations;
	String accessType;
	RandomAccessFile writeFile;	 
	
	/* Constructor takes block that needs to be written in file, the file object, 
	number of iterations and  access type(Sequential or random) as parameters.*/
	myThreadWrite( byte[] block, File f, int iterations, String accType){
	
		this.block=block;
		this.iterations = iterations;
	    this.accessType = accType;
	    try {
	    	//Using file object, below code creates and initializes a RandomAccessFile object which opens the file in read write mode.
			this.writeFile= new RandomAccessFile(f, "rw");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//Below is the abstract method from Class Thread which needs to be implemented in my custom thread class	
	public void run() {
		/*If the access type = sequential then below code block seeks at the start of the file and then 
		  based on the number of iterations writes the byte array(block) to the file.*/		
		if(accessType.equalsIgnoreCase("sequential")){
			try {
				writeFile.seek(0);
			
				for(int i = 0;i<iterations; i++){
					writeFile.write(block,0,block.length); //Writes the block to file sequentially
				}
				writeFile.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		/*If the access type = random then below code block then based on the number of iterations writes
		 the byte array(block) to the file. Inside the for loop it generates a random number and seeks the 
		 current pointer to that position to randomly write blocks.*/	
		else{
			try {
				for(int i = 0;i<iterations; i++){
					int randomseek = new Random().nextInt(block.length);
	            	writeFile.seek(randomseek);
				    writeFile.write(block, 0, block.length);
				}
				writeFile.close();
	         }catch(IOException e) {
					e.printStackTrace();
	         }
		}
		
	}//end of run 
}

//Below is my defined custom read thread which extends from Thread class.
class myThreadRead extends Thread{
	
	byte[] block;
	int block_size;
	int iterations;
	String accessType;
	RandomAccessFile readFile;
	public File f;
	
	/* Constructor takes block_size that needs to be read in file, the file object, 
	number of iterations and  access type(Sequential or random) as parameters.*/
	public myThreadRead(int block_size, File f,int iterations, String accType ){
	    this.block_size = block_size;
		this.iterations = iterations;
	    this.accessType = accType;
	    this.block = new byte[block_size];	    
	    try {
	    	//Using file object, below code creates and initializes a RandomAccessFile object which opens the file in read write mode.
			readFile = new RandomAccessFile(f, "rw");
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//This function returns the block that got read by the thread.
	public byte[] getReadBlock(){
		return block;
	}
	
	//Below is the abstract method from Class Thread which needs to be implemented in my custom thread class
	public void run() {
		
		/*If the access type = sequential then below code block seeks at the start of the file and then 
		  based on the number of iterations writes the byte array(block) to the file.*/
		if(accessType.equalsIgnoreCase("sequential")){
			try {
				readFile.seek(0);
				for(int i=0;i<iterations;i++){
					readFile.read(block, 0,block_size-1);
				}
				readFile.close();
			} catch(IOException e) {
				e.printStackTrace();
			}						
		}
		/*If the access type = random then below code block then based on the number of iterations writes
		 the byte array(block) to the file. Inside the for loop it generates a random number and seeks the 
		 current pointer to that position to randomly write blocks.*/
		else{
			try {
				for (int i = 0; i < iterations; i++) {
					int randomseek = new Random().nextInt(block_size);
					readFile.seek(randomseek);
					readFile.read(block, 0, block_size-1);
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}//end of run()       	  
}

public class disk_benchmarking {

	public static void main(String[] args) {

		int block_size, threadnum;
		
		/* Below block takes users preference on the block size and	also the number of threads. 
		   The program falls into this block if no command line argument has been passed.*/ 
		if(args.length<2){
			Scanner sc = new Scanner(System.in);
		
			System.out.println("Enter Block Size(1b,1Kb,1Mb):");  
			block_size = sc.nextInt();
			System.out.println("\nEnter the number of threads:");
			threadnum = sc.nextInt();
			
			sc.close();
		}
		//The program falls into this block if command line argument is provided during program execution.
		else{
			block_size = Integer.parseInt(args[0]);
			threadnum = Integer.parseInt(args[1]);
		}
		
		System.out.println("-------------------------------\nResults for block = "+block_size+" bytes for "+threadnum+" threads\n-------------------------------\n");
	    File file = new File("dataFile");// File that is being used for read write operations.
	    
	    /*Below we declare the number of iterations. I declare a large number of iterations so that OS 
	      doesn't put the file to cache and make read write operations faster.*/
	    int iterations = 100000;
	    
	    /*Using below block I restrict the number of iterations if block size is too big to avoid 
	      making of a large file during random writes.*/ 
	    if(block_size>1000000)
	    	iterations = 10;
	    int itrperThread = iterations/threadnum;// Distribute total number of iterations among the total number of threads
	    
	    // Below code creates a byte array of Blocksize size and initializes all elements with 'a'
	    byte[] block = new byte[block_size];
	    for(int i=0;i<block_size; i++){
	    	block[i] = (byte)'a';
	    }
	    
	    
	    //Below code is to test Sequential Write calls   
	    myThreadWrite[] wt = new myThreadWrite[threadnum]; // Creates an array of custom write thread based on the number of threads provided in input.
	    long startTime,endTime;
	       
	    startTime = System.currentTimeMillis(); // This line captures the wall clock time before the start of actual computation.
			
		try{
			//Below block initializes all the threads by passing proper parameters to the constructor. 
			for(int i = 0; i<threadnum;i++){
				wt[i] = new myThreadWrite(block, file,itrperThread,"sequential");
				wt[i].start();
			}
			
			//After creation of all threads we join all the threads using below block
			for(int i = 0; i<threadnum;i++){
				wt[i].join();
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		// Below line capture the wall clock time as our program would reach this point when all thread have finished their execution
		endTime = System.currentTimeMillis();
			
		long runtime = endTime-startTime;//This line calculate the runtime based on the starttime and endtime.
		//System.out.println("\nWrite runtime: "+runtime); 
		
		
		double throughput_write = ((double)(block_size*iterations)/(runtime))/1000; //calculate the throughput for sequential write operation.
		double latency_write = (double)iterations/runtime; //Calculate the latency for sequential write operation
		System.out.println("Sequential Write latency: "+ latency_write);
		System.out.println("Sequential Write throughput: "+ throughput_write+"\n");
		
		
		//Below code is to test Sequential Read calls
		myThreadRead[] rt = new myThreadRead[threadnum];// Creates an array of custom read thread based on the number of threads provided in input.
		startTime = System.currentTimeMillis();// This line captures the wall clock time before the start of actual computation.
		try {
			//Below block initializes all the threads by passing proper parameters to the constructor.
			for(int i = 0; i<threadnum;i++){
				rt[i] = new myThreadRead(block_size, file, itrperThread,"sequential");
				rt[i].start();
			}
			
			//After creation of all threads we join all the threads using below block
			for(int i = 0; i<threadnum;i++){
				rt[i].join();
			}
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		// Below line capture the wall clock time as our program would reach this point when all thread have finished their execution
		endTime = System.currentTimeMillis();
					
		runtime = endTime - startTime; //This line calculate the runtime based on the starttime and endtime.
		//System.out.println("\nRead runtime: "+runtime); 
				
		double throughput_read = ((double)(block_size*iterations)/(runtime))/1000; //calculate the throughput for sequential read operation.
		double latency_read = (double)iterations/runtime; //Calculate the latency for sequential read operation.
		System.out.println("Sequential Read latency: "+ latency_read);
		System.out.println("Sequential Read throughput: "+ throughput_read+"\n");
				
		
		//Below code is to test Random Write calls
		myThreadWrite[] rwt = new myThreadWrite[threadnum];// Creates an array of custom write thread based on the number of threads provided in input.
							       
		startTime = System.currentTimeMillis();// This line captures the wall clock time before the start of actual computation.
		try {
			//Below block initializes all the threads by passing proper parameters to the constructor.
			for(int i = 0; i<threadnum;i++){
				rwt[i] = new myThreadWrite(block,file,itrperThread,"random");
				rwt[i].start();
			}
			
			//After creation of all threads we join all the threads using below block
			for(int i = 0; i<threadnum;i++){
				rwt[i].join();
			}
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		// Below line capture the wall clock time as our program would reach this point when all thread have finished their execution
		endTime = System.currentTimeMillis();
		
		runtime = endTime - startTime;//This line calculate the runtime based on the starttime and endtime.
		//System.out.println("\nRandom write runtime: "+runtime); 
					
		double throughput_ranWrite = ((double)(block_size*iterations)/(runtime))/1000; //calculate the throughput for random write operation.
		double latency_ranWrite = (double)iterations/runtime; //Calculate the latency for random write operation.
		System.out.println("Random Write latency: "+ latency_ranWrite);
		System.out.println("Random Write throughput: "+ throughput_ranWrite+"\n");    				
		       
		//Random Read calls
		myThreadRead[] rrt = new myThreadRead[threadnum];// Creates an array of custom read thread based on the number of threads provided in input.
		
		startTime = System.currentTimeMillis();// This line captures the wall clock time before the start of actual computation.
		try {
			//Below block initializes all the threads by passing proper parameters to the constructor.
			for(int i = 0; i<threadnum;i++){
				rrt[i] = new myThreadRead(block_size,file,itrperThread,"random");
				rrt[i].start();
			}
			
			//After creation of all threads we join all the threads using below block
			for(int i = 0; i<threadnum;i++){
				rrt[i].join();
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		// Below line capture the wall clock time as our program would reach this point when all thread have finished their execution
		endTime = System.currentTimeMillis();
		
		runtime = endTime - startTime;//This line calculate the runtime based on the starttime and endtime.
		//System.out.println("\nRandom read runtime: "+runtime);		       
		
		double throughput_ranRead = ((double)(block_size*iterations)/(runtime))/1000; //calculate the throughput for random read operation.
		double latency_ranRead = (double)iterations/runtime; //Calculate the latency for random read operation.
		System.out.println("Random read latency: "+ latency_ranRead);
		System.out.println("Random read throughput: "+ throughput_ranRead+"\n"); 
		
	}//end of main()
}

