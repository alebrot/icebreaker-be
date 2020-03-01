#!/bin/bash

MACHINE=root@209.250.239.44
FROM_FILE_PATH=$1
TO_FILE_PATH=/var/springboot/app/landing

ssh $MACHINE /bin/bash <<HERE
   echo "Creating directories $FROM_FILE_PATH"
   mkdir -p  "$TO_FILE_PATH"
   mkdir -p  "$TO_FILE_PATH/vendor/bootstrap/css"
   mkdir -p  "$TO_FILE_PATH/vendor/fontawesome-free/css"
   mkdir -p  "$TO_FILE_PATH/vendor/simple-line-icons/css"
   mkdir -p  "$TO_FILE_PATH/vendor/simple-line-icons/fonts"
   mkdir -p  "$TO_FILE_PATH/device-mockups"
   mkdir -p  "$TO_FILE_PATH/device-mockups/iphone_6_plus"
   mkdir -p  "$TO_FILE_PATH/css"
   mkdir -p  "$TO_FILE_PATH/vendor/bootstrap/js"
   mkdir -p  "$TO_FILE_PATH/vendor/jquery"
   mkdir -p  "$TO_FILE_PATH/vendor/bootstrap/js"
   mkdir -p  "$TO_FILE_PATH/vendor/jquery-easing"
   mkdir -p  "$TO_FILE_PATH/js"
   mkdir -p  "$TO_FILE_PATH/img"
HERE

echo "${FROM_FILE_PATH} -> ${TO_FILE_PATH}"
scp "$FROM_FILE_PATH/index.html" $MACHINE:"$TO_FILE_PATH/index.html"
scp "$FROM_FILE_PATH/privacy.html" $MACHINE:"$TO_FILE_PATH/privacy.html"
scp "$FROM_FILE_PATH/terms.html" $MACHINE:"$TO_FILE_PATH/terms.html"
scp "$FROM_FILE_PATH/vendor/bootstrap/css/bootstrap.min.css" $MACHINE:"$TO_FILE_PATH/vendor/bootstrap/css/bootstrap.min.css"
scp -rp "$FROM_FILE_PATH/vendor/fontawesome-free" $MACHINE:"$TO_FILE_PATH/vendor"
scp "$FROM_FILE_PATH/vendor/simple-line-icons/css/simple-line-icons.css" $MACHINE:"$TO_FILE_PATH/vendor/simple-line-icons/css/simple-line-icons.css"
scp "$FROM_FILE_PATH/vendor/simple-line-icons/fonts/Simple-Line-Icons.woff2" $MACHINE:"$TO_FILE_PATH/vendor/simple-line-icons/fonts/Simple-Line-Icons.woff2"
scp "$FROM_FILE_PATH/device-mockups/device-mockups.min.css" $MACHINE:"$TO_FILE_PATH/device-mockups/device-mockups.min.css"
scp "$FROM_FILE_PATH/device-mockups/iphone_6_plus/iphone_6_plus_white_port.png" $MACHINE:"$TO_FILE_PATH/device-mockups/iphone_6_plus/iphone_6_plus_white_port.png"
scp "$FROM_FILE_PATH/css/new-age.min.css" $MACHINE:"$TO_FILE_PATH/css/new-age.min.css"
scp "$FROM_FILE_PATH/vendor/jquery/jquery.min.js" $MACHINE:"$TO_FILE_PATH/vendor/jquery/jquery.min.js"
scp "$FROM_FILE_PATH/vendor/bootstrap/js/bootstrap.bundle.min.js" $MACHINE:"$TO_FILE_PATH/vendor/bootstrap/js/bootstrap.bundle.min.js"
scp "$FROM_FILE_PATH/vendor/jquery-easing/jquery.easing.min.js" $MACHINE:"$TO_FILE_PATH/vendor/jquery-easing/jquery.easing.min.js"
scp "$FROM_FILE_PATH/js/new-age.min.js" $MACHINE:"$TO_FILE_PATH/js/new-age.min.js"
scp -rp "$FROM_FILE_PATH/img" $MACHINE:"$TO_FILE_PATH"
echo "Uploading completed"

ssh $MACHINE /bin/bash <<HERE
  echo "Changing owner and access permissions"
  chown -R springboot:springboot "$TO_FILE_PATH"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/bootstrap/css"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/fontawesome-free/css"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/simple-line-icons/css"
  chown -R springboot:springboot "$TO_FILE_PATH/device-mockups"
  chown -R springboot:springboot "$TO_FILE_PATH/css"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/bootstrap/js"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/jquery"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/bootstrap/js"
  chown -R springboot:springboot "$TO_FILE_PATH/vendor/jquery-easing"
  chown -R springboot:springboot "$TO_FILE_PATH/js"
  chown -R springboot:springboot "$TO_FILE_PATH/img"
#  chmod -R 400 $TO_FILE_PATH
  echo "Done"
HERE
