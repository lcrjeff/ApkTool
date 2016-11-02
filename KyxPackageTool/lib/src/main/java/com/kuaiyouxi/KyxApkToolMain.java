package com.kuaiyouxi;

import java.awt.EventQueue;

/**
 * Created by liangchunrong on 2016/3/4.
 */
public class KyxApkToolMain {
    /**
     * jar命令行的入口方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                KyxApkToolFrame.getInstance().displayFrame();
            }
        });
    }


}
