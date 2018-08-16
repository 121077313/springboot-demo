
1,server 如何打包

在run-> maven build.. > 输入 package 
就可以打包

如果想做进一步的配置, 修改assembly/assembly.xml

使用这个方法可以一次性打包多个发行包,*.zip, *.tar.gz
只要增加相应的assembly.xml文件就可以



2,增加eclipse中的类文件夹
按照如下步骤增加 m2e支持.
Widnow -> Preference -> Maven -> Discovry -> Open Catalog
安装  buildhelper

完成之后 可以看到 qq, sina 等新增的类文件夹(pom.xml中有配置)
