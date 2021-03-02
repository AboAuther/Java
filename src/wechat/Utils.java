package wechat;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


//将消息报文的发送、接受封装在Utils工具类中
public class Utils {

    /**
     * 通过套接字Socket发送字符串到服务器
     */
    public static void sendMsg(Socket s,String msg){
        try{
            //字符流
            DataOutputStream dos =new DataOutputStream(s.getOutputStream());
            dos.writeUTF(msg);
            dos.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 获取格式化的当前时间字符串形式
     */
    public static String getTimeStr(){
        SimpleDateFormat fm=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        return fm.format(new Date());

    }
}
