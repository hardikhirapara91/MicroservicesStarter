#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
LGREEN='\033[1;32m'
LRED='\033[1;31m'
NC='\033[0m' # No Color

TIMEOUT=90

pids=()

#look into a container log for the phrase since the last startup date.
#wait up to the configured timeout, making one attempt per second
grep_ready_phrase () {
    container_id=$( docker-compose ps -q $1 )
    #get the last startup date
    startup=$( docker inspect --format='{{.State.StartedAt}}' $container_id )

    x=1
    while [ $x -le $TIMEOUT ]
    do
        res=$( docker logs --since $startup $container_id | grep -c "$2" -m 1 )
        if [ $res = "1" ]; then
            break
        fi
        x=$(( $x + 1 ))
        sleep 1
    done

    if [ $res = "1" ]; then
        echo -e "$1 - ${LGREEN}Service Ready${NC}"
    else
        echo -e "$1 - ${LRED}Service Not Ready${NC}"
    fi
}

#check each service is ready by an "init" phrase
check_ready () {
    case "$1" in
    "admingateway")
        grep_ready_phrase $1 "Started AdminGatewayApplication"
        ;;
    "adminweb")
        grep_ready_phrase $1 "The application should be accessible"
        ;; 
    "auth")
        grep_ready_phrase $1 "initialization completed"
        ;;
    "commercegateway")
        grep_ready_phrase $1 "Started CommerceGatewayApplication"
        ;;
    "commerceweb")
        grep_ready_phrase $1 "The application should be accessible"
        ;;  
    "localkafka")
        grep_ready_phrase $1 "started (kafka.server.KafkaServer)"
        ;;     
    "localsolr")
        grep_ready_phrase $1 "Solr is running"
        ;; 
    "openapi-ui")
        grep_ready_phrase $1 "Application is now listening"
        ;;      
    "postgres")
        grep_ready_phrase $1 "ready to accept connections"
        ;;    
    "zk")
        grep_ready_phrase $1 "Started AdminServer"
        ;;
    esac
}

#check each container has started
check_running () {
    res=$( docker ps --filter name=$1 --filter status=running --format "up" )

    if [ "$res" = "up" ]; then
       echo -e "$1 - ${GREEN}Container Started${NC}"
       check_ready $1
    else
       echo -e "$1 - ${RED}Container Not Started${NC}"
    fi
}

#get all configured services and start checking
check_services () {
    while read service_name
    do
      check_running $service_name & 
      pids+=($!)
    done < <(docker-compose config --service)
}

check_services

# wait for all subtasks
for pid in ${pids[*]}; do
    wait $pid
done
