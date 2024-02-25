./mvnw package 
scp -P 22 ./target/order-0.0.1-SNAPSHOT.jar adminuser@4.185.149.147:/home/adminuser && ssh adminuser@4.185.149.147 -p 22 -f 'lsof -t -i:8080 | xargs -r kill && nohup java -jar /home/adminuser/order-0.0.1-SNAPSHOT.jar > output.log 2>&1 &'
scp -P 23 ./target/order-0.0.1-SNAPSHOT.jar adminuser@4.185.149.147:/home/adminuser && ssh adminuser@4.185.149.147 -p 23 -f 'lsof -t -i:8080 | xargs -r kill && nohup java -jar /home/adminuser/order-0.0.1-SNAPSHOT.jar > output.log 2>&1 &' 
scp -P 24 ./target/order-0.0.1-SNAPSHOT.jar adminuser@4.185.149.147:/home/adminuser && ssh adminuser@4.185.149.147 -p 24 -f 'lsof -t -i:8080 | xargs -r kill && nohup java -jar /home/adminuser/order-0.0.1-SNAPSHOT.jar > output.log 2>&1 &' 
