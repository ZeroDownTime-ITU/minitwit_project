#!/bin/bash
set -e
docker stop node_exporter 2>/dev/null || true
docker rm node_exporter 2>/dev/null || true
docker run -d \
  --name node_exporter \
  --restart unless-stopped \
  --pid host \
  -v /proc:/host/proc:ro \
  -v /sys:/host/sys:ro \
  -v /:/rootfs:ro \
  -p 9100:9100 \
  prom/node-exporter:latest \
  --path.procfs=/host/proc \
  --path.sysfs=/host/sys \
  --path.rootfs=/rootfs \
  --collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($|/)