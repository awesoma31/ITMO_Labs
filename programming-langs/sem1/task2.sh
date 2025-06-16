#/bin/bash

./hello2 2>err.txt 1>out.txt

echo "out:"
cat out.txt

echo "err:"
cat err.txt
