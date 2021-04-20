# 基础镜像
FROM dockerhub.emoney.cn/emoney/jdk-alpine:8-alpine3.9
# 作者信息
MAINTAINER emoney-meixiaohu
# 设置挂载点
VOLUME /tmp
# 设置JVM参数
ENV JAVA_OPTS -Xms4096m -Xmx4096m -Xss4m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m \
    -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/opt/pointprod-web-gc.log \
    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/pointprod-web.dump
# 容器根目录添加运行jar包
ADD ./target/point-prod.jar emoney-pointprod.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
# 暴露端口信息
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /emoney-pointprod.jar
