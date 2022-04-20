```shell
docker run --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -d elasticsearch:7.8.0
```

cd /usr/share/elasticsearch/plugins/

elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.8.0/elasticsearch-analysis-ik-7.8.0.zip


docker run --name kibana --link=elasticsearch:test  -p 5601:5601 -d kibana:7.8.0