package wechat;
import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import Constant.ConstantData;
public class ChatServer {

    /**
     * 添加用于功能实现的成员变量
     */
    private ServerSocket server;
    private boolean isRunning;
    private final HashMap<String, ClientHandler> clientHandlerMap = new HashMap<>();
    private JFrame serverFrame;
    private JTextField textServerIP;//ip
    private JTextField textPort;//端口文本框
    private JButton btnStart;//启动
    private JButton btnStop;//停止
    private JTextArea textAreaRecord;//文本记录框
    /**
     * 主函数
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ChatServer window = new ChatServer();
                window.serverFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 构造函数
     */
    public ChatServer() {
        initialize();
    }


    /**
     * 加载服务器界面
     */
    private void initialize() {
        //窗体
        serverFrame = new JFrame();
        serverFrame.setResizable(false);
        serverFrame.setTitle(ConstantData.SERVER_TITLE);
        serverFrame.setBounds(100, 100, 700, 300);
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.getContentPane().setLayout(null);

        //IP框
        JLabel labelIp = new JLabel(ConstantData.IPADDRESS);
        labelIp.setBounds(10, 27, 105, 18);
        serverFrame.getContentPane().add(labelIp);
        textServerIP = new JTextField();
        textServerIP.setText("0.0.0.0");
        textServerIP.setBounds(114, 24, 141, 24);
        serverFrame.getContentPane().add(textServerIP);
        textServerIP.setColumns(10);
        //端口框
        JLabel labelPort = new JLabel(ConstantData.PORT);
        labelPort.setBounds(293, 27, 72, 18);
        serverFrame.getContentPane().add(labelPort);
        textPort = new JTextField();
        textPort.setText(ConstantData.DEFAULTPORT);
        textPort.setBounds(356, 24, 86, 24);
        serverFrame.getContentPane().add(textPort);
        textPort.setColumns(10);
        //开始按钮
        btnStart = new JButton(ConstantData.BEGIN);
        btnStart.setBounds(479, 23, 95, 27);
        serverFrame.getContentPane().add(btnStart);
        // 启动按钮事件监听处理
        btnStart.addActionListener(arg0 -> {
        // 创建、启动服务器通信线程
            Thread serverThread = new Thread(new ServerThread());
            serverThread.start();
        });
        //停止按钮
        btnStop = new JButton(ConstantData.STOP);
        btnStop.setEnabled(false);
        btnStop.setBounds(588, 23, 95, 27);
        serverFrame.getContentPane().add(btnStop);
        // 停止按钮事件监听处理
        btnStop.addActionListener(arg0 -> {
            try {
                isRunning = false;
                // 关闭服务器套接字，清空客户端映射
                server.close();
                clientHandlerMap.clear();
                // 修改按钮状态
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                addMsg("服务器关闭成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //消息记录区
        JLabel labelChat = new JLabel(ConstantData.CHATHISTORY);
        labelChat.setBounds(10, 58, 72, 18);
        serverFrame.getContentPane().add(labelChat);
        //滚动条
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 78, 673, 159);
        serverFrame.getContentPane().add(scrollPane);
        //消息域
        textAreaRecord = new JTextArea();
        textAreaRecord.setEditable(false);
        scrollPane.setViewportView(textAreaRecord);
    }


    /**
     * 内部类，服务器后台线程
     */
    class ServerThread implements Runnable {
        //启动服务
        private void startServer() {
            try {

                String serverIp = textServerIP.getText();// 获取serverIp 和 serverPort
                int serverPort = Integer.parseInt(textPort.getText());// 创建套接字地址
                SocketAddress socketAddress = new InetSocketAddress(serverIp, serverPort);
                server = new ServerSocket();// 创建ServerSocket，绑定套接字地址
                System.out.println(server.toString());
                server.bind(socketAddress);// 修改判断服务器是否运行的标识变量
                isRunning = true;
                btnStart.setEnabled(false);// 修改启动和停止按钮状态
                btnStop.setEnabled(true);
                addMsg("服务器启动成功");
            } catch (IOException e) {
                addMsg("服务器启动失败，请检查端口是否被占用");
                e.printStackTrace();
                isRunning = false;
            }
        }

        @Override//重载函数
        public void run() {
            startServer();
            while(isRunning) {// 当服务器处于运行状态时，循环监听客户端的连接请求
                try {
                    Socket socket = server.accept();
                    Thread thread = new Thread(new ClientHandler(socket));// 创建与客户端交互的线程
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 内部类，用于和客户端交互
     */
    class ClientHandler implements Runnable {
        private final Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isConnected;
        private String username;
        //构造函数
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.dis = new DataInputStream(socket.getInputStream());
                this.dos = new DataOutputStream(socket.getOutputStream());
                isConnected = true;
            } catch (IOException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }

        @Override//重写run方法
        public void run() {
            while(isRunning && isConnected) {
                try {
                    String msg =dis.readUTF();// 读取客户端发送的报文
                    String[] parts = msg.split("#");
                    switch (parts[0]) {
                        case "LOGIN":// 处理登录报文
                            String loginUsername = parts[1];
                            if(clientHandlerMap.containsKey(loginUsername)) {// 如果该用户名已登录，则返回失败报文，否则返回成功报文
                                dos.writeUTF("FAIL");
                            } else {
                                dos.writeUTF("SUCCESS");
                                // 将此客户端处理线程的信息添加到clientHandlerMap中
                                clientHandlerMap.put(loginUsername, this);
                                // 将现有用户的信息发给新用户
                                StringBuilder msgUserList = new StringBuilder();
                                msgUserList.append("USERS#");
                                for(String username : clientHandlerMap.keySet()) {
                                    msgUserList.append(username).append("#");
                                }
                                dos.writeUTF(msgUserList.toString());
                                // 将新登录的用户信息广播给其他用户
                                String msgLogin = "LOGIN#" + loginUsername;
                                broadcastMsg(loginUsername, msgLogin);
                                // 存储登录的用户名
                                this.username = loginUsername;
                            }
                            break;
                        //处理退出报文
                        case "LOGOUT":
                            clientHandlerMap.remove(username);
                            String msgLogout="LOGOUT#"+username;
                            broadcastMsg(username, msgLogout);
                            broadcastMsg(username,"LOGOUT#"+username);
                            isConnected=false;
                            socket.close();
                            break;
                        case "TALKS_ALL":
                            String msgTalkToAll="TALKS_ALL#"+username+"#"+parts[1];
                            broadcastMsg(username, msgTalkToAll);
                            break;
                        case "TALKS_ONE":
                            ClientHandler clientHandler=clientHandlerMap.get(parts[1]);
                            if(null!=clientHandler){
                                String msgTalkToOne="TALKS_ONE#"+username+"#"+parts[2];
                                clientHandler.dos.writeUTF(msgTalkToOne);
                                clientHandler.dos.flush();
                            }
                        default:
                            break;
                    }
                } catch (IOException e) {
                    isConnected = false;
                    e.printStackTrace();
                }
            }
        }

        /**
         * 将某个用户发来的消息广播给其它用户
         */
        private void broadcastMsg(String fromUsername, String msg) throws IOException{
            for(String toUserName : clientHandlerMap.keySet()) {
                if(!fromUsername.equals(toUserName)) {
                    DataOutputStream dos = clientHandlerMap.get(toUserName).dos;
                    dos.writeUTF(msg);
                    dos.flush();
                }
            }
        }
    }


    /**
     * 添加消息到文本框
     */
    private void addMsg(String msg) {
        textAreaRecord.append(msg + "\n");
        textAreaRecord.setCaretPosition(textAreaRecord.getText().length());
    }
}