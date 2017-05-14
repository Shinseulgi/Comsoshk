#! /usr/bin/env bash
rm /tmp/logdata
touch /tmp/logdata
tail -F /tmp/logdata | nc -lk 7777&

#sleep 5
index=1
cleanup(){
  kill $(ps -ef | grep 'nc -lk 7777' | awk '{print $2}')
  kill $(ps -ef | grep 'sleep 0.5' | awk '{print $2}')
  kill $(ps -ef | grep 'tail -F /tmp/logdata' | awk '{print $2}')
  exit 1;
}
trap cleanup INT

while read -u 9 -r line;
do
	echo $line >> /tmp/logdata
        printf "%d firewall.log\n" $index
        read -t 0.5 -N 1 input
	index=$((index + 1))
	if [[ $input == "1" ]]; then
		while read line
		do
			printf "%d hacking1.log\n" $index
                	echo $line >> /tmp/logdata
                	sleep 0.5
                	index=$((index + 1))
		done < hacking_Pattern1.log
        elif [[ $input == "2" ]]; then
                while read line
		do
                       printf "%d hacking2.log\n" $index
                       echo $line >> /tmp/logdata
                       sleep 0.5
                       index=$((index + 1))
                done < hacking_Pattern2.log
        elif [[ $input == "3" ]]; then
		while read line
                do
                       printf "%d hacking3.log\n" $index
                       echo $line >> /tmp/logdata
                       sleep 0.5
                       index=$((index + 1))
                done < hacking_Pattern3.log
	fi
done 9<firewall.log
