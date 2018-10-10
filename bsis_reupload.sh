#!/bin/bash
#JARNAME=
#USERNAME=
#HOST=
#KEYPATH=
#APPDIR=
#TARGETDIR=

source ./private.sh

/usr/bin/expect << EOF
    set timeout -1
    spawn ssh ${USERNAME}@${HOST} -i ${KEYPATH}
    expect "*#"
        send "cd ${APPDIR}\r"
    expect "*#"
        send "./killAndDelete.sh\r"
        send "logout\r"
    expect eof
EOF

echo "success delete"

cd ${TARGETDIR}
FILENAME=$(ls | egrep "${JARNAME}" | sed -n 1p)
echo "find jar ${FILENAME}"

/usr/bin/expect << EOF
    set timeout -1
    spawn scp -i ${KEYPATH} ${FILENAME} ${USERNAME}@${HOST}:${APPDIR}
    expect "ETA" {
        exp_continue;
    }
EOF

echo "success upload"

/usr/bin/expect << EOF
    set timeout -1
    spawn ssh ${USERNAME}@${HOST} -i ${KEYPATH}
    expect "*#"
        send "cd ${APPDIR}/\r"
    expect "*#"
        send "./start.sh\r"
    expect "*#" {
        sleep 10
        send "cat nohup.out\r"
        sleep 10
        send "cat nohup.out\r"
        send "logout\r"
    }
    expect eof
EOF

echo "success start"
