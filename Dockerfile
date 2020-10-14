FROM store/oracle/serverjre:1.8.0_241-b07
COPY . /
# RUN yum -y install net-tools
RUN javac kasiblink.java  
CMD java -Xmx400M -Xms400M -d64 kasiblink 
# CMD ["java", "MCrelayISO"] 

