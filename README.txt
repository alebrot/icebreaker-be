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