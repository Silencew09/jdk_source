package text.lang;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 月央泽
 */
public class AutoCloseableText {



    public static class CustomeObject implements AutoCloseable{
        @Override
        public void close() throws Exception {
            System.out.println("自动关闭了....");
        }
    }



    /**
     * 1.7之前
     */
    public static void releaseBefore(){
        InputStream inputStream = null ;
        try {
            inputStream = new FileInputStream("123");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 1.8及以后
     * 只要实现AutoCloseable接口
     */
    public static void releaseNow(){

        try( CustomeObject customeObject = new CustomeObject()) {
            //当离开当前代码块,资源会被自动释放
            System.out.println("正常结束....");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void releaseNowErro(){

        try( CustomeObject customeObject = new CustomeObject()) {
            //当离开当前代码块,资源会被自动释放
            int i = 10/0;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常了...");
        }

    }

    public static void main(String[] args) {
        //正常演示
        releaseNow();
        System.out.println("============================");
        //异常演示
        releaseNowErro();
    }
}
