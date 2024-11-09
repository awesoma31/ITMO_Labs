all: main

main: main.c
	gcc -o main main.c

preprocess: main.c
	gcc -E main.c -o preprocessed.c

.PHONY: clean
clean:
	rm -f main