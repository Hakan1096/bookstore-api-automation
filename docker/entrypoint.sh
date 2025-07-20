#!/bin/bash
set -e

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

run_tests() {
    log "Starting API automation tests..."
    
    local test_command="mvn clean test"
    
    if [ -n "$TEST_SUITE" ]; then
        test_command="$test_command -DsuiteXmlFile=$TEST_SUITE"
    fi
    
    if [ -n "$PARALLEL_THREADS" ]; then
        test_command="$test_command -DthreadCount=$PARALLEL_THREADS"
    fi
    
    test_command="$test_command -Dapi.base.url=$API_BASE_URL"
    
    log "Executing: $test_command"
    
    if eval $test_command; then
        log "Tests completed successfully!"
        mvn allure:report
        exit_code=0
    else
        log "Tests completed with failures"
        exit_code=1
    fi
    
    return $exit_code
}

case "${1:-run-tests}" in
    "run-tests")
        run_tests
        ;;
    *)
        log "Unknown command: $1"
        exit 1
        ;;
esac
