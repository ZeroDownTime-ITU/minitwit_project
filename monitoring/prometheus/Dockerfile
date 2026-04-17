FROM prom/prometheus:latest
COPY prometheus.yml /etc/prometheus/prometheus.yml

# Without this line, prometheus deletes data after 15 days
CMD ["--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.retention.time=90d"]