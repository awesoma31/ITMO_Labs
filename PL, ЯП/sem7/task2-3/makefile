CC = gcc
CFLAGS = -g -Wall -Wextra
ASLR_STATUS = $(shell cat /proc/sys/kernel/randomize_va_space)

# Targets for the vulnerable and fixed versions
all: stack-smash stack-smash-fixed

# Compile the vulnerable version with disabled protections
stack-smash: stack-smash.c
	$(CC) -fno-stack-protector -z execstack -no-pie $(CFLAGS) -o stack-smash stack-smash.c

# Compile the fixed version with enabled protections
stack-smash-fixed: stack-smash.c
	$(CC) -fstack-protector -pie $(CFLAGS) -o stack-smash-fixed stack-smash.c

run-vulnerable: stack-smash
	@echo "Running vulnerable version with exploit..."
	@echo "Ensure ASLR is disabled before running this target."
	@echo -n -e "AAAAAAAABBBBBBBB\x46\x11\x40\x00\x00\x00\x00\x00" | ./stack-smash


# Run the fixed version
run-fixed: stack-smash-fixed
	@echo "Running fixed version with harmless input..."
	@echo "harmless input" | ./stack-smash-fixed

# Disable ASLR for testing
disable-aslr:
	@echo "Disabling ASLR..."
	@sudo sh -c 'echo 0 > /proc/sys/kernel/randomize_va_space'
	@echo "ASLR status: $(shell cat /proc/sys/kernel/randomize_va_space)"

# Enable ASLR for security
enable-aslr:
	@echo "Enabling ASLR..."
	@sudo sh -c 'echo 2 > /proc/sys/kernel/randomize_va_space'
	@echo "ASLR status: $(shell cat /proc/sys/kernel/randomize_va_space)"

# Verify ASLR status
check-aslr:
	@echo "ASLR status: $(ASLR_STATUS)"
	@if [ $(ASLR_STATUS) -eq 0 ]; then \
		echo "ASLR is disabled."; \
	else \
		echo "ASLR is enabled."; \
	fi

# Clean up compiled binaries
clean:
	@echo "Cleaning up..."
	@rm -f stack-smash stack-smash-fixed

