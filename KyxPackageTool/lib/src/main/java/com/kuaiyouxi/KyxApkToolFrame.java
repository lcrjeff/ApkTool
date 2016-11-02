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
    private final static String WINDOW_TITLE = "�����������_1.0.1";
    private JTextArea mLogTextArea;
    private static KyxApkToolFrame mInstance;

    private KyxApkToolFrame() {
        super();
        // JFrame��������
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ��ȡ��ʾ���ߴ���Ϣ
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // ���ô��ڴ�С�ߴ�
        this.setSize(530, 400);
        // ��ʼλ��
        this.setLocation((dim.width - this.getWidth()) / 2,
                (dim.height - this.getHeight()) / 2);
        // ���ô��ڱ���
        this.setTitle(WINDOW_TITLE);
        // ���ô��ڲ��ܹ�������
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

        //apk·��
        JLabel apkJLabel = new JLabel("apk�ļ�:");
        apkJLabel.setFont(new java.awt.Font("΢���ź�", 0, 16));
        apkJLabel.setBounds(new Rectangle(10, 20, 120, 35));
        apkJLabel.setBackground(Color.BLACK);
        rootPanel.add(apkJLabel);

        final JTextField apkJeJTextArea = new JTextField("��קapk�ļ����˻����ļ���apk�б�");
        apkJeJTextArea.setBounds(new Rectangle(100, 20, 400, 35));
        apkJeJTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
        apkJeJTextArea.setColumns(20);
        apkJeJTextArea.setFont(new java.awt.Font("΢���ź�", 0, 16));
        setDrag(apkJeJTextArea);
        rootPanel.add(apkJeJTextArea);


        //�����ļ�·��
        JLabel channelJLabel = new JLabel("�����ļ�:");
        channelJLabel.setFont(new java.awt.Font("΢���ź�", 0, 16));
        channelJLabel.setBounds(new Rectangle(10, 60, 120, 35));
        channelJLabel.setBackground(Color.BLACK);
        rootPanel.add(channelJLabel);


        final JTextField channelJTextArea = new JTextField("��ק�����ļ����ˣ�channel.txt�������Ż��и�ʽ");
        channelJTextArea.setBounds(new Rectangle(100, 60, 400, 35));
        channelJTextArea.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
        channelJTextArea.setColumns(20);
        channelJTextArea.setFont(new java.awt.Font("΢���ź�", 0, 16));
        setDrag(channelJTextArea);
        rootPanel.add(channelJTextArea);

        //��ʼ�����ť
        final JButton lPackButton = new JButton("��ʼ���");
        lPackButton.setFont(new java.awt.Font("΢���ź�", 0, 15));
        lPackButton.setBounds(new Rectangle(220, 320, 100, 30));
        rootPanel.add(lPackButton);
        lPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ���֮ǰ����־
                mLogTextArea.setText("�������...");
                // �������
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
                            mLogTextArea.setText("��������쳣��");
                        }
                    }
                });
            }
        });

        mLogTextArea = new JTextArea();
        mLogTextArea.setColumns(20);
        mLogTextArea.setRows(5);
        mLogTextArea.setFont(new java.awt.Font("΢���ź�", 0, 16));
        mLogTextArea.setText("��־�����");

        final JScrollPane lLogScrollPane = new JScrollPane();
        lLogScrollPane.setViewportView(mLogTextArea);
        lLogScrollPane.setBounds(new Rectangle(10, 120, 500, 150));
        lLogScrollPane.setBorder(new BorderUIResource.LineBorderUIResource(Color.GRAY));
        rootPanel.add(lLogScrollPane);

        this.add(rootPanel);
    }

    /**
     * ���ı�����ؼ�{@link JTextArea} �����ļ���ק����
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
                        dtde.dropComplete(true);// ָʾ��ק���������
                    } catch (UnsupportedFlavorException pE) {
                        pE.printStackTrace();
                    } catch (IOException pE) {
                        pE.printStackTrace();
                    }
                } else {
                    dtde.rejectDrop();// ����ܾ���ק��������
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
            mLogTextArea.setText("apk�ļ������ڣ�");
            return;
        }
        // ��ȡ�����б�
        List<String> channelList = new ArrayList<>();
        File channelPathFile = new File(channelPath);
        if (!channelPathFile.exists()) {
            mLogTextArea.setText("�����ļ������ڣ�");
            return;
        }
        read(channelPath, channelList, "");
        if (channelList.size() == 0) {
            mLogTextArea.setText("�������ݸ�ʽ����ȷ��");
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
                //����apk�ļ�
                if (nioTransferCopy(sourceApkFile, target)) ;

                // ��ʼ��������Ϣ д��������Ϣ
                String channelFilePath = "cztchannel_" + channelName;
                File channelFile = new File(channelFilePath);
                FileUtil.createNewFile(channelFile);

                //���������ֵ��ļ�д��apk�ļ���/META-INFĿ¼��
                ZipUtil.addFileToZip2(target, "/META-INF", channelFile);
                FileUtil.delete(channelFile);
                FileUtil.rename(target, channelName + "-" + sourceApkFile.getName());
            }
            mLogTextArea.setText("  �����ɣ�\n  �ļ�����·��: " + outDirFile.getAbsolutePath());
        }
        mLogTextArea.setText(" ȫ�� �����ɣ�\n  �ļ�����·��:��ǰ����·���� ");

    }

    /***
     * �����ƶ����ű�Ƕ�ȡ
     *
     * @param source     �ļ�����·��
     * @param collection �������
     * @param mark       �ƶ��ַ������
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
            throw new IOException("�����ļ�||�ļ����ܶ�||�ļ�������");
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
     * �ر�IO��
     *
     * @param obj
     */

    public static void closeCloseable(Closeable obj) {
        try {
            // �޸�С��MI2��JarFileû��ʵ��Closeable���±�������
            if (obj != null && obj instanceof Closeable)
                obj.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * nio���ٿ����ļ�
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
