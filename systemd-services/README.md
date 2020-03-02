# Systemd Services for Kafka and Zookeeper

```bash
# Verify the kafka.service and zookeeper.service files and update the paths in the file correctly.

sudo vim /etc/systemd/system/kafka.service
# Paste the content of kafka.service from this directory into the opened file

sudo vim /etc/systemd/system/zookeeper.service
# Paste the content of zookeeper.service from this directory into the opened file

sudo systemctl enable zookeeper
sudo systemctl enable kafka


sudo systemctl [status | start | stop] zookeeper
sudo systemctl [status | start | stop] kafka
```
