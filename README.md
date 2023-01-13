# alibaba-sentinel-dashboard-nacos

## 阿里sentinel后台面板规则（包括gateway模式）持久化-推模式（nacos），基于版本v1.7.0改造

##新增配置说明
> application.properties
```
nacos.server-addr=127.0.0.1:8848
nacos.groupId=DEFAULT_GROUP
nacos.namespace=
```

nacos.server-addr: nacos地址

nacos.groupId: nacos组id

nacos.namespace： nacos命名空间，可不设,默认为public
