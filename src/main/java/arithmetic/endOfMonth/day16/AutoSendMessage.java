package arithmetic.endOfMonth.day16;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;


/**
 * @author Silence_w
 */

public class AutoSendMessage {


    public static void main(String[] args) {
        String name = "djhs-康秋玲";
        for (int i = 0; i <23; i++) {
            if (i == 0){
                AutoSendMessage.sendMessage(name, "蜀道难");
            }
            if (i == 1){
                AutoSendMessage.sendMessage(name, "噫吁嚱，危乎高哉！蜀道之难，难于上青天！");
            }
            if (i == 2){
                AutoSendMessage.sendMessage(name, "蚕丛及鱼凫，开国何茫然！");
            }
            if (i == 3){
                AutoSendMessage.sendMessage(name , "尔来四万八千岁，不与秦塞通人烟");
            } if (i == 4){
                AutoSendMessage.sendMessage(name , "西当太白有鸟道，可以横绝峨眉巅");
            } if (i == 5){
                AutoSendMessage.sendMessage(name , "地崩山摧壮士死，然后天梯石栈相钩连");
            } if (i == 6){
                AutoSendMessage.sendMessage(name , "上有六龙回日之高标，下有冲波逆折之回川");
            } if (i == 7){
                AutoSendMessage.sendMessage(name , "黄鹤之飞尚不得过，猿猱欲度愁攀援");
            } if (i == 8){
                AutoSendMessage.sendMessage(name , "青泥何盘盘，百步九折萦岩峦");
            } if (i == 9){
                AutoSendMessage.sendMessage(name , "扪参历井仰胁息，以手抚膺坐长叹");
            } if (i == 10){
                AutoSendMessage.sendMessage(name , "问君西游何时还？畏途巉岩不可攀");
            } if (i == 11){
                AutoSendMessage.sendMessage(name , "但见悲鸟号古木，雄飞雌从绕林间");
            } if (i == 12){
                AutoSendMessage.sendMessage(name , "又闻子规啼夜月，愁空山");
            } if (i == 13){
                AutoSendMessage.sendMessage(name , "蜀道之难，难于上青天，使人听此凋朱颜");
            } if (i == 14){
                AutoSendMessage.sendMessage(name , "连峰去天不盈尺，枯松倒挂倚绝壁");
            }if (i == 15){
                AutoSendMessage.sendMessage(name , "飞湍瀑流争喧豗，砯崖转石万壑雷");
            }if (i == 16){
                AutoSendMessage.sendMessage(name , "其险也如此，嗟尔远道之人胡为乎来哉！");
            }if (i == 17){
                AutoSendMessage.sendMessage(name , "剑阁峥嵘而崔嵬，一夫当关，万夫莫开");
            }if (i == 18){
                AutoSendMessage.sendMessage(name , "所守或匪亲，化为狼与豺");
            }if (i == 19){
                AutoSendMessage.sendMessage(name , "朝避猛虎，夕避长蛇");
            }if (i == 20){
                AutoSendMessage.sendMessage(name , "磨牙吮血，杀人如麻");
            }if (i == 21){
                AutoSendMessage.sendMessage(name , "锦城虽云乐，不如早还家");
            }if (i == 22){
                AutoSendMessage.sendMessage(name , "蜀道之难，难于上青天，侧身西望长咨嗟！");
            }

        }



    }

    public static synchronized void sendMessage(String uer,String message){

        try {
            Robot robot = new Robot();

            openWechat(robot);

            findFriend(robot,uer);

            sendMessage(message);

            closeWechat(robot);

        }catch (AWTException e){
            System.out.println("发消息异常");
        }
    }

    private static void openWechat(Robot robot) {
        // 通过robot模拟按键Ctrl+Alt+w
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_W);
        // 松开Ctrl+Alt
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_ALT);
        // 让robot延迟1秒钟，防止页面响应时间长
        robot.delay(100);
    }

    private static void findFriend(Robot robot, String userName) {
        // 模拟按键Ctrl+F
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        // 将好友的昵称先添加到系统剪贴板
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(userName);
        clip.setContents(tText, null);
        // 模拟ctrl+V，完成粘贴功能
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        // 延迟1秒，防止查询慢
        robot.delay(1000);
        // 回车，定位到好友聊天输入框
        robot.keyPress(KeyEvent.VK_ENTER);
    }

    private static void sendMessage(String message) throws AWTException {
        // 将要发送的消息设置到剪贴板
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Robot robot = new Robot();
        StringSelection text = new StringSelection(message);
        clip.setContents(text, null);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(500);
        // 回车发送
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(500);
    }

    private static void closeWechat(Robot robot) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_W);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_ALT);
    }




}
