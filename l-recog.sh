#!/usr/local/bin/sh

javac vc.java
for i in Recogniser/*.vc
do
	java VC.vc $i > $i.obs
done