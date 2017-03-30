#! /usr/bin/env sh
rm /tmp/logdata
touch /tmp/logdata
#tail -f /tmp/logdata | nc -w3 localhost 7777
TAIL_NC_PID=$!

sleep 5
#echo " " > /tmp/logdata
#tail -F /tmp/logdata | nc -u 7777 &
#tail -f /tmp/logdata | nc -w3 localhost 7777
tail -F /tmp/logdata | nc -lk 7777&

index=1
while read line
do
		echo $line >> /tmp/logdata
		sleep 1
	    echo $((index))
		index=$((index + 1))
done < firewall.log



#index=1
#while [ $index -le 10 ]
#do
#cat ./files/fake_logs/Log1.log >> /tmp/logdata
#sleep 3
#cat ./files/fake_logs/Log2.log >> /tmp/logdata
#sleep 1
#cat ./files/fake_logs/Log1.log >> /tmp/logdata
#sleep 2
#cat ./files/fake_logs/Log1.log >> /tmp/logdata
#sleep 2
#echo $((index))
#sleep 1
#index=$((index + 1))
#done

kill $TAIL_NC_PID
