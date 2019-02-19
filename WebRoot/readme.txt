2019-01-14 修改说明：
1、数据表T_QGCJK中增加字段：
    zyml varchar(80),--作业命令（作业命令号）,
    zysd varchar(100),--作业时段,
    gwsczpsfsc varchar(20),--轨温三测照片是否上传。
2、qgcjkform.html页面中增加了三个字段对应的表单项。
3、SQLConfig.xml文件中增加了导入全过程数据时的SQL语句，增加这三个字段的数据录入。
4、SQLConfig.xml文件中修改了全过程数据修改时的SQL语句，增加这三个字段的数据更新。
5、webconfig.xml文件中增加了导入数据的字段：zyml,zysd,gwsczpsfsc
6、修改了表格数据双击显示详细信息的弹框背景颜色。
2019-2-19 修改说明：
1、修改了SQL语句替换参数由#改成§，解决由于字段值中包括#字符引起的替换失败的问题。