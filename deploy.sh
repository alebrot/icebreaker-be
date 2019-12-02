#!/bin/bash

MACHINE=root@209.250.239.44
FROM_FILE_PATH=$1
TO_FILE_PATH=/var/springboot/app/be.jar

echo "${FROM_FILE_PATH} -> ${TO_FILE_PATH}"
scp "$FROM_FILE_PATH" $MACHINE:$TO_FILE_PATH
echo "Uploading completed"

ssh $MACHINE /bin/bash << HERE
  chown springboot:springboot $TO_FILE_PATH
  chmod 500 $TO_FILE_PATH
  echo "Changed owner and access permissions"
  echo "Restarting service"
  systemctl restart be
  sleep 15
  systemctl status be | tail -5
HERE