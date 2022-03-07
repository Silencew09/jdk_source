package interview;

import javax.xml.transform.Source;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Silence_w
 */
public class CopyFile {

    public static void main(String[] args) {
        try {
            //copyFile("C:\\Users\\Administrator\\Desktop\\地址.txt","C:\\Users\\Administrator\\Desktop\\复制的地址.txt");
            //copyFileNio("C:\\Users\\Administrator\\Desktop\\地址.txt","C:\\Users\\Administrator\\Desktop\\复制的地址.txt");
            //System.out.println(countWordInFile("C:\\Users\\Administrator\\Desktop\\地址.txt","密码"));
            //listFile("E:\\java_source\\jdk_source");
            listFileNio("E:\\java_source\\jdk_source");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String fileName,String target) throws IOException {
        try(InputStream in = new FileInputStream(fileName)){
            try(OutputStream out = new FileOutputStream(target)){
                byte[] buffer = new byte[4096];
                int bytesToRead;
                while((bytesToRead=in.read(buffer))!=-1){
                    out.write(buffer,0,bytesToRead);
                }
            }
        }
    }
    public static void copyFileNio(String fileName,String target) throws IOException {
        try(FileInputStream in = new FileInputStream(fileName)){
            try(FileOutputStream out = new FileOutputStream(target)){
                FileChannel inChannel = in.getChannel();
                FileChannel outChannel = out.getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while(inChannel.read(buffer) != -1){
                    buffer.flip();
                    outChannel.write(buffer);
                    buffer.clear();
                }
            }
        }
    }
    public static int countWordInFile(String fileName,String word) throws IOException {
      int counter = 0;
      try(FileReader fr = new FileReader(fileName)){
          try(BufferedReader br = new BufferedReader(fr)){
              String line = null;
              while ((line = br.readLine())!=null){
                  int index = -1;
                  while(line.length() >= word.length() && (index = line.indexOf(word)) > 0 ){
                      counter ++;
                      line = line.substring(index + word.length());
                  }

              }
          }
      }catch (Exception ex){
          ex.printStackTrace();
      }
      return counter;
    }
    public static void listFile(String fileName)throws IOException {
        File file = new File(fileName);
        for (File listFile : file.listFiles()) {
            if (listFile.isFile()){
                System.out.println(listFile.getName());
            }else if(listFile.isDirectory()){
                dir(listFile);
            }
        }

    }
    public static void dir(File file){
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()){
                dir(file1);
            }else if(file1.isFile()){
                System.out.println(file1.getName());
            }
        }
    }
    public static void listFileNio(String fileName)throws IOException {
        Path path = Paths.get(fileName);
        Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        });

    }



}
