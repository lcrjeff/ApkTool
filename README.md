# ApkTool
渠道快速打包工具，java版本
渠道号放在META-INF

1.编辑需要打包的渠道

(注意:使用Notepad++等工具打开channel文件，进行编辑，把需要增加的渠道，换行的形式加入)

2.双击ZyApktool，打开界面

3.拖动apk 文件到指定位置（apk文件夹列表）

4.拖动渠道文件到指定位置

5.点击运行


1.读取方法META-INF渠道号，使用类方法
ChannelUtil.getChannel();


