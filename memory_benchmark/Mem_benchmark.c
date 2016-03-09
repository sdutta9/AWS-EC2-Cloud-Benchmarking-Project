#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <sys/time.h> 

//Below is the actual data packet that is being used by threads.
struct thread_data
{
    char *block; 
    char *temp;
    int start,end,acctype;
};

void thread_run ( void * val)
{
    struct thread_data *data = (struct thread_data *) val;  //the memory block passed through parameter gets stored into the data.
    long seek = 0;
    
    while(data->start < data->end)
    {
        seek = (long)data->start;// seek value is assigned sequencially.
        if(data->acctype == 2) {
            seek = rand()%(data->end);//if access type is random then a random value is generated and assigned to seek.
        }
	if(seek>sizeof(data->block))
		seek=seek%sizeof(data->block);// If value of seek exceeds the actual block size then its modulo is assigned to seek.	
	memset((data->block + seek),'a',1); // this line sets the value 'a' in the memory location as defined by the seek value.
        (data->start)++;
    }
    while(data->start < data->end)
    {
        seek = (long)data->start;// seek value is assigned sequencially.
        if(data->acctype == 2) {
		seek = rand()%(data->end);//if access type is random then a random value is generated and assigned to seek.
        }
        
	data->temp = strchr((data->block + seek), '1'); //this line trys to read the block from the seek position and assign that value to the temp structure.
        
	(data->start)++;
    }
    
    pthread_exit(0); // this line notifies other threads of its closure. 
}

int main(void){

	struct timeval startTime, endTime;
    	struct timezone zone;
	pthread_t t1, t2;
	int threadnum, datasize, accesstype, blocksize;
	long roundTime,iterations = 10000; //I have fixed the iteration number to 10000.

	//Below code takes users preference like number of threads, data size and access type.
	printf("Enter number of Threads:");
	scanf("%d",&threadnum);
	printf("Enter data size (1: 1B, 2: 1KB, 3: 1MB): ");
	scanf("%d",&datasize);
	printf("Enter access type (1: Sequential, 2: Random): ");
	scanf(el"%d",&accesstype);	
	
	switch(datasize){
		case 1: blocksize = 1;
			break;
		case 2: blocksize = 1000;
			break;
		case 3: blocksize =1000000;	
	}
	//Below code is for a single threaded memory access.
	if (threadnum == 1){
		struct thread_data data;
		data.acctype=accesstype;
		
		data.block = (char *) malloc(blocksize);//this line create a memory space with the selected blocksize
		data.start = 0;
        	data.end = iterations;
		
		gettimeofday(&startTime,&zone);// This line captures the wall clock time before the start of actual computation.

        	pthread_create (&t1, NULL, (void *) &thread_run, (void *) &data); // this line call the threads create method and bind our custom run method within it.
        	
		// Below line capture the wall clock time as our program would reach this point when all thread have finished their execution
		gettimeofday(&endTime,&zone);
		roundTime=endTime.tv_usec-startTime.tv_usec; //this line calculates the total time to execute the program 
		      	

		free(data.block);//this line frees the data block from the memory.
        		
	}
	//Below code is for two threaded memory access
	else{
		struct thread_data data1, data2;
		//Below code creates two data packets for two threads.
		data1.acctype=accesstype;
		data1.block = (char *) malloc(blocksize);
		data1.start = 0;
    		data1.end = iterations/2; //As the program has two threads the iterations are divided among them.
		
		data2.acctype=accesstype;
		data2.block = (char *) malloc(blocksize);
		data2.start = iterations/2;
		data2.end = iterations;

		gettimeofday(&startTime,&zone);  // This line captures the wall clock time before the start of actual computation. 

    		pthread_create (&t1, NULL, (void *) &thread_run, (void *) &data1);// this line call the threads create method and bind our custom run method within it.
    		pthread_create (&t2, NULL, (void *) &thread_run, (void *) &data2);// this line call the threads create method and bind our custom run method within it.
    
    		pthread_join(t1, NULL);// this line joins t1 thread.
    		pthread_join(t2, NULL);// this line joins t2 thread.
    
		gettimeofday(&endTime,&zone);    
		roundTime=endTime.tv_usec-startTime.tv_usec;   //this line calculates the total time to execute the program  

    		free(data1.block); //this line frees the data block from the memory.
    		free(data2.block); //this line frees the data block from the memory.
	}
	
	printf("Time: %ld microseconds\n",roundTime);
  	
	float rt_milli = (float)roundTime/1000; //converting the microseconds into milliseconds.
  	float rt_sec = (float)rt_milli/1000;//converting milliseconds into seconds.
  	printf("Latency: millisec: %.12f \n",(rt_milli)/iterations); //calculating and printing latency.
  	float throughput = (float)(1/rt_sec); //this line calculates throughput.
  	printf("Throughput: %f MB/sec\n\n",throughput);
	   
	return 0;
} 
