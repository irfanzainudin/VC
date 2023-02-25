#!/usr/bin/sh

pass=0
fail=0
javac vc.java
for i in Recogniser/*.vc
do
	java VC.vc $i > $i.obs
	file=$( echo $i | cut -d . -f 1 )
	f=$( echo $i | cut -d . -f 1 ).sol
	output=$( diff $f $i.obs )
	echo $file
	if [ -z "$output" ]; then
		echo PASSED
		pass=$((pass + 1))
	else
		echo FAILED
		fail=$((fail + 1))
	fi
done

echo "PASSED:" $pass
echo "FAILED:" $fail