# 基础镜像使用java
FROM java:8
# 作者
MAINTAINER meixiaohu
# VOLUME 指定了临时文件目录为/tmp。
VOLUME /tmp
EXPOSE 8080
# 将jar包添加到容器中并更名为app.jar
ADD point-web-0.0.1-SNAPSHOT.jar point.jar
# 运行jar包
RUN bash -c 'touch /point.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/point.jar"]
