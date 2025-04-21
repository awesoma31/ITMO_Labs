# Makefile
# This file combines build, clean, format, and Ubuntu setup commands.
# Place this in the project root (where the contest/ directory is).

# Compiler and flags
CXX := $(shell which clang++)
CXXFLAGS := -std=c++20 -Wall -Wextra -Wpedantic -Werror -g
CXXFLAGS_RELEASE := $(CXXFLAGS) -O3 -DNDEBUG

# Detect platform to set sanitizer options.
UNAME_S := $(shell uname -s)
ifeq ($(findstring Linux,$(UNAME_S)),Linux)
    CXXFLAGS_ASAN := $(CXXFLAGS) -fsanitize=address,undefined,leak
else ifeq ($(findstring Darwin,$(UNAME_S)),Darwin)
    CXXFLAGS_ASAN := $(CXXFLAGS) -fsanitize=address,undefined
else
    $(error Unsupported platform: $(UNAME_S))
endif

# Default mode for building is "Asan"
MODE ?= Asan
ifeq ($(MODE),Release)
    CXXFLAGS_TOTAL := $(CXXFLAGS_RELEASE)
else ifeq ($(MODE),Asan)
    CXXFLAGS_TOTAL := $(CXXFLAGS_ASAN)
else
    $(error Invalid MODE: $(MODE). Use 'Release' or 'Asan'.)
endif

# Determine list of tasks.
# If the TASK variable is provided, use it directly;
# otherwise, build all directories under contest/.
ifdef TASK
    TASKS := $(TASK)
else
    TASKS := $(wildcard contest/*)
endif

# Build targets: For each task (for example "contest/foo"),
# the target is out/<task>/App<MODE> (e.g. out/contest/foo/AppAsan)
BUILD_TARGETS := $(foreach t, $(TASKS), out/$(t)/App$(MODE))

.PHONY: build clean format ubuntu

# Build target: builds all tasks in $(BUILD_TARGETS).
build: $(BUILD_TARGETS)
	@echo "Build complete. MODE: $(MODE)"

# Pattern rule:
# The target 'out/%/App$(MODE)' depends on '%/Main.cpp'.
# The stem (%) will be the task path (e.g. contest/foo).
out/%/App$(MODE): %/Main.cpp
	@echo "Building $*..."
	@mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS_TOTAL) $< -o $@

# Clean target: remove the out/ directory.
clean:
	@rm -rf out
	@echo "Clean complete."

# clang-format target:
# Finds all source files under contest/ and either checks or fixes formatting.
# Default is "check"; to fix files run: make format FMTMODE=fix
FMTMODE ?= check
SOURCES := $(shell find contest -iname '*.cpp')

format:
	@if [ "$(FMTMODE)" = "fix" ]; then \
	    echo "Fixing formatting..."; \
	    clang-format -i --fallback-style=Google --verbose $(SOURCES); \
	elif [ "$(FMTMODE)" = "check" ]; then \
	    echo "Checking formatting..."; \
	    clang-format -Werror --dry-run --fallback-style=Google --verbose \
	      $(SOURCES); \
	else \
	    echo "Invalid format mode: $(FMTMODE). Use 'fix' or 'check'."; \
	    exit 1; \
	fi

# Ubuntu setup target:
# Links LLVM 18 tools to the default names under /usr/local/bin.
ubuntu:
	@LLVM_VERSION=18; \
	for tool in clang clang++ clang-format clang-tidy; do \
	    echo "Linking $$tool"; \
	    ln -sf $$(which $$tool-$$LLVM_VERSION) /usr/local/bin/$$tool; \
	done
	@echo "Ubuntu LLVM tool setup complete."

