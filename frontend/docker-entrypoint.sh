#!/bin/sh
# Extract the nameserver from /etc/resolv.conf for nginx resolver directive
NAMESERVER=$(awk '/^nameserver/{print $2; exit}' /etc/resolv.conf)
export NAMESERVER="${NAMESERVER:-8.8.8.8}"

# Run the default nginx entrypoint
exec /docker-entrypoint.sh "$@"
