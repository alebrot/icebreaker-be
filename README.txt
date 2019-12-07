#restart docker
systemctl restart docker

# Build image:
docker build -t khlebtsov/kofify-db:v1 .
docker push khlebtsov/kofify-db:v1

/apache-activemq-5.15.9/bin ./activemq start
/apache-activemq-5.15.9/bin ./activemq stop

# Backup docker exec CONTAINER /usr/bin/mysqldump -u root --password=root DATABASE > backup.sql
 docker exec kofify-db /usr/bin/mysqldump -u root --password=password kofify > backup.sql
# Restore cat backup.sql | docker exec -i CONTAINER /usr/bin/mysql -u root --password=root DATABASE
 cat backup.sql | docker exec -i kofify-db /usr/bin/mysql -u root --password=password kofify

# Connect ot container with bash
 docker exec -it kofify-db bash
# Connect to mysql
  mysql -u root kofify --password=password
  mysql -u root kofify -h 127.0.0.1 --password=password --port=2345

#If you want a fresh start for everything,
 docker system prune -a
 docker volume prune
 docker volume rm icebreaker-be_kofify-db



mysql> SELECT host, user FROM mysql.user;
+-----------+------------------+
| host      | user             |
+-----------+------------------+
| %         | root             |
| %         | user             |
| localhost | mysql.infoschema |
| localhost | mysql.session    |
| localhost | mysql.sys        |
| localhost | root             |
+-----------+------------------+

mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
Query OK, 0 rows affected (0.00 sec)

mysql> FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.00 sec)

mysql> SHOW GRANTS;

----

 SELECT @@sql_mode;

 SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'


move file from local to remote:
 scp be-0.0.1-SNAPSHOT.jar root@209.250.239.44:/root/app
download log from server
    scp root@209.250.239.44:/root/app/out/logs/app.log app.log






start a new session:
    screen -S {name}
switch to session:
    screen -r {name}
detach from session
ctrl+a+d

screen -S 23536 -X quit


# Check ports
    sudo lsof -i -P -n | grep LISTEN

# Truncate file
    truncate -s 0 app.log


# Config server

sudo useradd -s /usr/sbin/nologin springboot
sudo passwd springboot
# login as user
su - springboot -s /bin/bash

chown springboot:springboot be.jar
chmod 500 be.jar
#make your jar file as immutable using the change attribute (chattr) command.
sudo chattr +i be.jar

#be.service place
/etc/systemd/system
#when replace file
systemctl daemon-reload

# enable Spring Boot application at system startup
sudo systemctl enable be.service
# log
journalctl -u be



#SSH

ssh-keygen -t rsa
cat ~/.ssh/id_rsa.pub
# backup ~/.ssh/id_rsa
ssh-copy-id root@209.250.239.44
# /usr/bin/ssh-copy-id: INFO: Source of key(s) to be installed: "/Users/alexey/.ssh/id_rsa.pub"
# /usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
# /usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys


#SSL
#Generate certificate dev
    keytool -genkeypair -alias mycert -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore mycert.p12 -validity 3650

#Prod
wget https://dl.eff.org/certbot-auto
mv certbot-auto /usr/local/bin/certbot-auto
sudo chown root /usr/local/bin/certbot-auto
sudo chmod 0755 /usr/local/bin/certbot-auto
sudo /usr/local/bin/certbot-auto certonly --standalone

IMPORTANT NOTES:
 - Congratulations! Your certificate and chain have been saved at:
   /etc/letsencrypt/live/kofify.com/fullchain.pem
   Your key file has been saved at:
   /etc/letsencrypt/live/kofify.com/privkey.pem
   Your cert will expire on 2020-03-05. To obtain a new or tweaked
   version of this certificate in the future, simply run certbot-auto
   again. To non-interactively renew *all* of your certificates, run
   "certbot-auto renew"


#convert to PKCS
cd /etc/letsencrypt/live/kofify.com
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out mycert.p12 -name "mycert" -CAfile chain.pem -caname root -password pass:password
mv /etc/letsencrypt/live/kofify.com/mycert.p12 /var/springboot/app/mycert.p12
chown springboot:springboot /var/springboot/app/mycert.p12
chmod 500 /var/springboot/app/mycert.p12

#Swap Enabling
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo swapon --show



#iptables
iptables -t nat -L --line-numbers
#delete rule
iptables -t nat -D PREROUTING {line number}

# You should should see in the output entries for 80, 443, 8080,and 8443.
iptables -L -n

#if not then execute
sudo iptables -I INPUT 1 -p tcp --dport 8443 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 8080 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 443 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 80 -j ACCEPT

Once traffic on the required ports are allowed, you can run the command to forward port 80 traffic to 8080, and port 443 traffic to 8443. The commands look like this:
sudo iptables -t nat -I PREROUTING -p tcp --destination-port 443 -j REDIRECT --to-ports 8443
sudo iptables -t nat -I PREROUTING -p tcp --destination-port 80 -j REDIRECT --to-ports 8080

#saving
sudo iptables-save > /etc/iptables.rules

#restore
iptables-restore < /etc/iptables.rules