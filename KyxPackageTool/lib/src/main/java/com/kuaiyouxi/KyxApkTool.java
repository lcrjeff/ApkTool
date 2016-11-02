package com.kuaiyouxi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangchunrong on 2016/3/4.
 */
public class KyxApkTool {

    private static final String channelPath = "info/channel.txt";//渠道文件

    /**
     * jar命令行的入口方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String cmdPath = "-path";
        String cmdOutdir = "-outdir";
        String help = "用法：java -jar KyxApkTool.jar [" + cmdPath + "] [arg0]"
                + "\n" + cmdPath + "		APK文件路径"
                + "\n" + cmdOutdir + "		输出路径（可选），默认输出到APK文件同一级目录"
                + "\n例如："
                + "\n写入：java -jar KyxApkTool.jar -path D:/test.apk ";
        Map<String, String> argsMap = new LinkedHashMap<String, String>();
        if (args.length == 0 || args[0] == null || args[0].trim().length() == 0) {
            System.out.println(help);
            return;
        } else {
            if (args.length > 0) {
                if (args.length == 1) {
                    System.out.println("参数不对");
                } else {

                    for (int i = 0; i < args.length; i += 2) {
                        if (i + 1 < args.length) {
                            if (args[i + 1].startsWith("-")) {
                                throw new IllegalStateException("args is error, help: \n" + help);
                            } else {
                                argsMap.put(args[i], args[i + 1]);
                            }
                        }
                    }
                    System.out.println("argsMap = " + argsMap);

                }
            }
        }
        File sourceApkFile = argsMap.containsKey(cmdPath) ? new File(argsMap.get(cmdPath)) : null;
//        File sourceApkFile = new File("app-debug.apk");
        String parent = sourceApkFile == null ? null : (sourceApkFile.getParent() == null ? "./" : sourceApkFile.getParent());
        File outdir = parent == null ? null : new File(argsMap.containsKey(cmdOutdir) ? argsMap.get(cmdOutdir) : parent);
        // 获取渠道列表
        List<String> channelList = new ArrayList<>();
        read(channelPath, channelList, "");

        for (String channelName : channelList) {

            String fileName = sourceApkFile.getName();
            int dot = fileName.lastIndexOf(".");
            String prefix = fileName.substring(0, dot);
            String suffix = fileName.substring(dot);
            File target = new File(outdir, prefix + "_" + channelName + suffix);
            if (nioTransferCopy(sourceApkFile, target)) ;

            String channelFilePath = "cztchannel_" + channelName;
            // 初始化渠道信息
            // 写入渠道信息

            File channelFile = new File(channelFilePath);
            FileUtil.createNewFile(channelFile);
            ZipUtil.addFileToZip2(target, "/META-INF", channelFile);
            FileUtil.delete(channelFile);
            FileUtil.rename(target, channelName + "-" + sourceApkFile.getName());
        }
    }

    /***
     * 根据制定符号标记读取
     *
     * @param source     文件完整路径
     * @param collection 结果集合
     * @param mark       制定字符串标记
     * @return
     * @throws IOException
     */
    @SuppressWarnings("finally")
    public static boolean read(String source, Collection<String> collection, String
            mark) throws IOException {
        @SuppressWarnings("unused")
        boolean result = false;

        File target = new File(source);

        if (!target.isFile() || !target.canRead() || !target.exists()) {
            throw new IOException("不是文件||文件不能读||文件不存在");
        }

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String line;
        try {
            fileInputStream = new FileInputStream(target);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("")) {
                    if (mark != null && line.endsWith(mark)) {
                        line = line.substring(0, line.lastIndexOf(mark));
                    }
                    collection.add(line);
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            closeCloseable(bufferedReader);
            closeCloseable(inputStreamReader);
            closeCloseable(fileInputStream);
        }
        return false;
    }


    /**
     * 关闭IO流
     *
     * @param obj
     */

    public static void closeCloseable(Closeable obj) {
        try {
            // 修复小米MI2的JarFile没有实现Closeable导致崩溃问题
            if (obj != null && obj instanceof Closeable)
                obj.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * nio高速拷贝文件
     *
     * @param source
     * @param target
     * @return
     * @throws IOException
     */
    private static boolean nioTransferCopy(File source, File target) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            File parent = target.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            return in.transferTo(0, in.size(), out) == in.size();
        } finally {
            closeCloseable(inStream);
            closeCloseable(in);
            closeCloseable(outStream);
            closeCloseable(out);
        }
    }
}
