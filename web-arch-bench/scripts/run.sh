#!/bin/bash

# Function to extract JSON values without installing anything
extract_json_value() {
    local key=$1
    grep -oP '"'"$key"'"\s*:\s*"\K[^"]+' ../config/benchmark_setting.json
}

extract_json_array() {
    local key=$1
    grep -oP '"'"$key"'"\s*:\s*\[\K[^\]]+' ../config/benchmark_setting.json | sed 's/"//g' | tr ',' ';'
}

# Retrieve data from JSON using grep and sed
apiPorts=$(extract_json_array "ports")
apiProject=$(extract_json_value "project")
nginxPath=$(extract_json_value "path")
nginxConf=$(extract_json_value "conf")
nginxEnable=$(extract_json_value "enable")
nginxPort=$(extract_json_value "port")

# Print the values
echo "API Ports: $apiPorts"
echo "API Project: $apiProject"
echo "NGINX Path: $nginxPath"
echo "NGINX Config Path: $nginxConf"
echo "NGINX Enabled: $nginxEnable"
echo "NGINX Port: $nginxPort"

# Replace semicolons in ports with spaces for easier iteration in the loop
apiPorts=${apiPorts//;/ }

# Count the number of ports
portCount=0
for port in $apiPorts; do
    portCount=$((portCount+1))
done

# Validate if the ports make sense based on NGINX enable status
if [[ "$nginxEnable" == "False" && "$portCount" -gt 1 ]]; then
    echo "Only one API port is allowed when NGINX is not enabled."
    exit 1
fi

chmod +x ../gradlew

# Start the DB
echo "Starting Database..."
( cd ../ && ./gradlew :db-server:run & )

# Start the API and prepare the upstream_block for the nginx config
upstream_block=""
for port in $apiPorts; do
    echo "Starting API on port $port..."
    (cd ../ && PORT="$port" ./gradlew :$apiProject:run &)
    upstream_block="$upstream_block""server 127.0.0.1:$port; "
done

# Wait until the API is up
check_ports() {
    for port in $apiPorts; do
        echo "Checking if service is up on port $port..."
        http_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:"$port"/api/ping)
        echo "HTTP Code: $http_code"
        
        if [[ "$http_code" == "200" ]]; then
            echo "Service on port $port is up!"
        else
            echo "Service on port $port is not up yet. HTTP Code: $http_code"
            return 1
        fi
    done
    return 0
}

until check_ports; do
    echo "Waiting 5 seconds before retrying..."
    sleep 5
done

# Create the nginx config and start nginx
if [[ "$nginxEnable" == "True" ]]; then
    echo "Checking if NGINX is running..."
    nginx -t > /dev/null 2>&1
    if [[ $? -eq 0 ]]; then
        echo "NGINX is running. Stopping NGINX..."
        ( cd "$nginxPath" && ./nginx -s stop )
        echo "NGINX stopped."
    else
        echo "NGINX is not running."
    fi

    # Generate nginx.conf using the upstream block
    sed -e "s|{{upstreamBlock}}|$upstream_block|" \
        -e "s|{{nginxPort}}|$nginxPort|" \
        ../config/nginx.conf.template > "$nginxConf/nginx.conf"

    echo "nginx.conf has been generated with the upstream block from config.json."

    echo "Starting NGINX..."
    ( cd "$nginxPath" && ./nginx & )

    echo "Waiting for NGINX to initialize..."
    sleep 5
fi

# Set portBenchmark based on NGINX enable status
if [[ "$nginxEnable" == "True" ]]; then
    portBenchmark="$nginxPort"
    echo "Using NGINX port for benchmark: $portBenchmark"
else
    portBenchmark=$(echo $apiPorts | awk '{print $1}')
    echo "Using API port for benchmark: $portBenchmark"
fi

echo "Starting Benchmark..."
( cd ../ && PORT="$port" ./gradlew :benchmark:run & )

echo "All services started."
