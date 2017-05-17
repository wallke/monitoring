#!/bin/bash
# copy logs to logstash dir 
deltime=5
cptime=2
#---------------------------------------
# app日志拷贝 
# /home/weihu/backup_logs/*/*.log.*
app_sourcepath=/home/weihu/app_logs
app_destpath=/home/weihu/logstash_logs/app_logs

#循环每个节点日志目录
for i in `seq 9001 9030`
do
    app_dpath=$app_destpath/$i
 #目标目录，logstash 的扫描日志目录
    app_spath=$app_sourcepath/$i
    if [ -d $app_spath ]
    then
        if ! [ -d $app_dpath ]
        then
            mkdir $app_dpath
        fi
        if [ $(find $app_spath -name '*.log.*' -mmin -$cptime | wc -l) -gt 0 ]
        then
            #匹配查询近2分钟（cptime）日志文件并拷贝到目标目录
            find $app_spath -name '*.log.*' -mmin -$cptime -exec cp -f {} $app_dpath \;
        fi
    fi
done

# rm $deltime min agao  删除5分钟（）以前日志（logstash扫描日志）
if [ $(find $app_destpath -name '*.log.*' -mmin +$deltime | wc -l) -gt 0 ]
then
   find $app_destpath -name '*.log.*' -mmin +$deltime -exec rm -f {} \;
fi

