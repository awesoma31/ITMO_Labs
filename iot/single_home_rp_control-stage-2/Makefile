VIS := ./artefacts_visualization/main.py
ML := ./main_question_loop/main.py

.PHONY: run visualize clean

run:
	python3 $(ML)

visualize: run
	python3 $(VIS)

clean:
	rm -f artefacts/*
	rm -f *.log
	rm -f main_question_loop/*.log

all: run visualize clean