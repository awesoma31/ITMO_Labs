#/bin/bash

FILE=test.txt

grep '^.$' $FILE

grep '\d' $FILE

grep -E '^0[xX][0-9a-fA-F]+$' $FILE

grep '.*<\w{3}>.*' $FILE

grep '^\s*$' $FILE

grep -v 'aboba' $FILE
