package com.kuaiyouxi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.BorderUIResource;

/**
 * @author liangchunrong
 * @desc
 * @date 2016/3/15
 */
public class KyxApkToolFrame extends JFrame {
    private final static String WINDOW_TITLE = "渠道打包工具_1.0.1";
    private JTextArea mLogTextArea;
    private static KyxApkToolFrame mInstance;

    private KyxApkToolFrame() {
        super();
        // JFrame窗口配置
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 获取显示器尺寸信息
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // 设置窗口大小尺寸
        this.setSize(530, 400);
        // 初始位置
        this.setLocation((dim.width - this.getWidth()) / 2,
                (dim.height - this.getHeight()) / 2);
        // 设置窗口标题
        this.setTitle(WINDOW_TITLE);
        // 设置窗口不能够拉伸宽高
        this.setResizable(false);
        try {
            initView();
        } catch (UnsupportedEncodingException pE) {
            pE.printStackTrace();
        }
    }

    public static KyxApkToolFrame getInstance() {
        if (mInstance == null) {
            synchronized (KyxApkToolFrame.class) {
                if (mInstance == null) {
                    mInstance = new KyxApkToolFrame();
                }
            }
        }
        return mInstance;
    }

    public void initView() throws UnsupportedEncodingException {
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(null);

        //apk路径
        JLabel apkJLabel = new JLabel("apk文件:");
        apkJLabel.setFont(new java.awt.Font("微软雅黑", 0, 16));
        apkJLabel.setBounds(new Rectangle(10, 20, 120, 35));
        apkJLabel.setBackground(Color.BLACK);
        rootPanel.add(apkJLabel);

        final JTextField apkJeJTextArea = new JTextField("拖拽apk文件到此或者文件夹apk列表");
        apkJeJTextArea.setBounds(new Rectangle(100, 20, 400, 35));
        apkJeJTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
        apkJeJTextArea.setColumns(20);
        apkJeJTextArea.setFont(new java.awt.Font("微软雅黑", 0, 16));
        setDrag(apkJeJTextArea);
        rootPanel.add(apkJeJTextArea);


        //渠道文件路径
        JLabel channelJLabel = new JLabel("渠道文件:");
        channelJLabel.setFont(new java.awt.Font("微软雅黑", 0, 16));
        channelJLabel.setBounds(new Rectangle(10, 60, 120, 35));
        channelJLabel.setBackground(Color.BLACK);
        rootPanel.add(channelJLabel);


        final JTextField channelJTextArea = new JTextField("拖拽渠道文件到此，channel.txt，渠道号换行格式");
        channelJTextArea.setBounds(new Rectangle(100, 60, 400, 35));
        channelJTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
        channelJTextArea.setColumns(20);
        channelJTextArea.setFont(new java.awt.Font("微软雅黑", 0, 16));
        setDrag(channelJTextArea);
        rootPanel.add(channelJTextArea);

        //开始打包按钮
        final JButton lPackButton = new JButton("开始打包");
        lPackButton.setFont(new java.awt.Font("微软雅黑", 0, 15));
        lPackButton.setBounds(new Rectangle(220, 320, 100, 30));
        rootPanel.add(lPackButton);
        lPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 清除之前的日志
                mLogTextArea.setText("点击运行...");
                // 触发打包
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            start(apkJeJTextArea.getText().trim().toString(), channelJTextArea.getText().trim().toString());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            mLogTextArea.setText("打包出现异常！");
                        }
                    }
                });
            }
        });

        mLogTextArea = new JTextArea();
        mLogTextArea.setColumns(20);
        mLogTextArea.setRows(5);
        mLogTextArea.setFont(new java.awt.Font("微软雅黑", 0, 16));
        mLogTextArea.setText("日志输出：");

        final JScrollPane lLogScrollPane = new JScrollPane();
        lLogScrollPane.setViewportView(mLogTextArea);
        lLogScrollPane.setBounds(new Rectangle(10, 120, 500, 150));
        lLogScrollPane.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
        rootPanel.add(lLogScrollPane);

        this.add(rootPanel);
    }

    /**
     * 对文本区域控件{@link JTextArea} 进行文件拖拽监听
     *
     * @param pIconTextArea {@link JTextArea}
     */
    private void setDrag(JTextField pIconTextArea) {
        new DropTarget(pIconTextArea, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    try {
                        List<File> list = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        String temp = "";
                        for (File file : list) {
                            temp += file.getAbsolutePath() + "\n";
                            pIconTextArea.setText(temp);
                        }
                        dtde.dropComplete(true);// 指示拖拽操作已完成
                    } catch (UnsupportedFlavorException pE) {
                        pE.printStackTrace();
                    } catch (IOException pE) {
                        pE.printStackTrace();
                    }
                } else {
                    dtde.rejectDrop();// 否则拒绝拖拽来的数据
                }
            }
        });
    }

    public void displayFrame() {
        setVisible(true);
    }

    private void start(String apkPath, String channelPath) throws Exception {

        File sourceApkFile = new File(apkPath);
        if (!sourceApkFile.exists()) {
            mLogTextArea.setText("apk文件不存在！");
            return;
        }
        // 获取渠道列表
        List<String> channelList = new ArrayList<>();
        File channelPathFile = new File(channelPath);
        if (!channelPathFile.exists()) {
            mLogTextArea.setText("渠道文件不存在！");
            return;
        }
        read(channelPath, channelList, "");
        if (channelList.size() == 0) {
            mLogTextArea.setText("渠道内容格式不正确！");
            return;
        }
        File[] files = null;
        if (sourceApkFile.isDirectory()) {
            files = sourceApkFile.listFiles();
        }
        if (files == null) {
            files = new File[1];
            files[0] = sourceApkFile;
        }

        for (int i = 0; i < files.length; i++) {
            sourceApkFile = files[i];
            if (sourceApkFile == null || !sourceApkFile.exists())
                continue;
            String fileName = sourceApkFile.getName();
            int dot = fileName.lastIndexOf(".");
            String prefix = fileName.substring(0, dot);
            String suffix = fileName.substring(dot);
            File outDirFile = new File(prefix + "-channels");
            if (outDirFile.exists()) {
                outDirFile.delete();
            }
            for (String channelName : channelList) {

                File target = new File(outDirFile, prefix + "_" + channelName + suffix);
                //拷贝apk文件
                if (nioTransferCopy(sourceApkFile, target)) ;

                // 初始化渠道信息 写入渠道信息
                String channelFilePath = "cztchannel_" + channelName;
                File channelFile = new File(channelFilePath);
                FileUtil.createNewFile(channelFile);

                //将渠道名字的文件写入apk文件的/META-INF目录下
                ZipUtil.addFileToZip2(target, "/META-INF", channelFile);
                FileUtil.delete(channelFile);
                FileUtil.rename(target, channelName + "-" + sourceApkFile.getName());
            }
            mLogTextArea.setText("  打包完成！\n  文件保存路径: " + outDirFile.getAbsolutePath());
        }
        mLogTextArea.setText(" 全部 打包完成！\n  文件保存路径:当前工具路径下 ");

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
