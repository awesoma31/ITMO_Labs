# Compiler to use
CC = gcc

# Compiler flags
CFLAGS = -Wall -Wextra -O2

# Name of the executable
TARGET = program

# Source file
SRCS = heap_1.c

# Default rule to build the program
all: $(TARGET)

# Rule to build the executable
$(TARGET): $(SRCS)
	$(CC) $(CFLAGS) -o $(TARGET) $(SRCS)

# Clean up build files
clean:
	rm -f $(TARGET)
