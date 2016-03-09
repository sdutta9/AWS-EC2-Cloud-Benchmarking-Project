CC = gcc
CFLAGS  = -g -Wall

 
default: mem_bench

mem_bench: Mem_benchmark.o 
	$(CC) $(CFLAGS) -pthread -o mem_bench Mem_benchmark.o -lm

Mem_benchmark.o: Mem_benchmark.c 
	$(CC) $(CFLAGS) -c Mem_benchmark.c -lm

clean: 
	$(RM) mem_bench test1 *.o *~

run:
	./mem_bench


