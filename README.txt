docker build -t khlebtsov/kofify-db:v1 .


/apache-activemq-5.15.9/bin ./activemq start
/apache-activemq-5.15.9/bin ./activemq stop

# Backup docker exec CONTAINER /usr/bin/mysqldump -u root --password=root DATABASE > backup.sql
 docker exec kofify-db /usr/bin/mysqldump -u root --password=password kofify > backup.sql
# Restore cat backup.sql | docker exec -i CONTAINER /usr/bin/mysql -u root --password=root DATABASE
 cat backup.sql | docker exec -i kofify-db /usr/bin/mysql -u root --password=password kofify