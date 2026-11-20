#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"

server_pid=
k6_pid=

cleanup() {
  if [[ -n $k6_pid ]]; then
    kill "$k6_pid" 2>/dev/null || true
    wait "$k6_pid" 2>/dev/null || true
  fi
  if [[ -n $server_pid ]]; then
    kill "$server_pid" 2>/dev/null || true
    wait "$server_pid" 2>/dev/null || true
  fi
}

start_server() {
  local listeners
  listeners=$(ss -H -ltn 'sport = :3000') || return 1
  if [[ -n $listeners ]]; then
    echo 'Port 3000 is occupied. Stop the other server first.' >&2
    return 1
  fi
  node server.js &
  server_pid=$!
  for attempt in {1..50}; do
    kill -0 "$server_pid" || return 1
    if curl -fsS --max-time 1 --noproxy '*' -X POST http://localhost:3000/cart/add >/dev/null 2>&1; then
      return 0
    fi
    sleep 0.1
  done
  echo 'The server did not become ready.' >&2
  return 1
}

uptime_seconds() {
  awk '{ print $1 }' /proc/uptime
}

trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

command -v k6 >/dev/null
mkdir -p results
start_server
export NO_PROXY=localhost,127.0.0.1,::1
k6_started=$(uptime_seconds)
k6 run --no-color --no-usage-report --duration 2m slo-test.js >results/chaos.txt 2>&1 &
k6_pid=$!

sleep 35
kill -0 "$k6_pid"
kill -KILL "$server_pid"
wait "$server_pid" 2>/dev/null || true
server_pid=
crashed=$(uptime_seconds)
sleep 10
start_server
recovered=$(uptime_seconds)

awk -v start="$k6_started" -v crash="$crashed" -v ready="$recovered" 'BEGIN {
  printf "server killed at +%.2f s\n", crash - start
  printf "restart delay: 10 s\n"
  printf "server ready at +%.2f s\n", ready - start
  printf "recovery time: %.2f s\n", ready - crash
}' | tee results/chaos-events.txt

k6_status=0
wait "$k6_pid" || k6_status=$?
k6_pid=
printf 'k6 exit=%s\n' "$k6_status" | tee -a results/chaos-events.txt
exit "$k6_status"
