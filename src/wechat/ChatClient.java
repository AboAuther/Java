    package wechat;
    import java.awt.Component;
    import java.awt.EventQueue;
    import java.io.DataInputStream;
    import java.io.DataOutputStream;
    import java.io.IOException;
    import java.net.Socket;
    import javax.swing.ButtonGroup;
    import javax.swing.DefaultListModel;
    import javax.swing.JButton;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JList;
    import javax.swing.JOptionPane;
    import javax.swing.JRadioButton;
    import javax.swing.JScrollPane;
    import javax.swing.JTextArea;
    import javax.swing.JTextField;
    import javax.swing.ListSelectionModel;
    import Constant.ConstantData;
    public class ChatClient {
        // 定义成员变量
        private ClientThread clientThread;
        private JFrame chatFrame;
        private JTextField textServerIP;//IP
        private JTextField textServerPort;//端口
        private JTextArea textAreaRecord;//消息文本记录框
        private JButton send;//发送按钮
        private JButton login;//登录按钮
        private JTextField textUsername;//用户名
        private JList<String> listUsers;//用户列表  Jlist控件显示客户端的用户列表。数据保存在DefaultListmodel数据模型对象中。
        private final ButtonGroup buttonGroup = new ButtonGroup();
        private JRadioButton groupChat;//群聊
        private JRadioButton privateChat;//私聊
        private JTextArea textAreaMsg;//消息框
        private final DefaultListModel<String> modelUsers;//数据模型，用于更新数据
        public static final String username = null;
    
        /**
         * 主函数
         */
        public static void main(String[] args) {
            EventQueue.invokeLater(() -> {
                try {
                    ChatClient window = new ChatClient();
                    ChatClient window1 = new ChatClient();
                    ChatClient window2= new ChatClient();
    
                    window.chatFrame.setVisible(true);
                    window1.chatFrame.setVisible(true);
                    window2.chatFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    
    
        /**
         * 构造函数
         */
        public ChatClient() {
            initialize();
            modelUsers= new DefaultListModel<>();
            // 初始化成员变量和随机用户名
            listUsers.setModel(modelUsers);
        }
    
        /**
         * 加载客户端界面
         */
        private void initialize() {
            chatFrame = new JFrame();
            chatFrame.setResizable(false);
            chatFrame.setTitle(ConstantData.CLIENT_TITLE);
            chatFrame.setBounds(100, 100, 715, 476);
            chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chatFrame.getContentPane().setLayout(null);
    
            //* 上层控件区*//
            JLabel lblNewLabel = new JLabel(ConstantData.IPADDRESS);
            lblNewLabel.setBounds(14, 24, 121, 18);
            chatFrame.getContentPane().add(lblNewLabel);
            //IP文本框
            textServerIP = new JTextField();
            textServerIP.setText(ConstantData.DEFAULTIP);
            textServerIP.setBounds(121, 21, 121, 24);
            chatFrame.getContentPane().add(textServerIP);
            textServerIP.setColumns(10);
            //用户名label
            JLabel portLabel = new JLabel(ConstantData.USERNAME);
            portLabel.setBounds(375, 24, 58, 18);
            chatFrame.getContentPane().add(portLabel);
            //用户名框
            textUsername = new JTextField();
            textUsername.setBounds(433, 21, 60, 24);
            chatFrame.getContentPane().add(textUsername);
            textUsername.setColumns(10);
            //端口label
            JLabel lblNewLabel_1 = new JLabel(ConstantData.PORT);
            lblNewLabel_1.setBounds(256, 24, 58, 18);
            chatFrame.getContentPane().add(lblNewLabel_1);
            //端口文本框
            textServerPort = new JTextField();
            textServerPort.setText(ConstantData.DEFAULTPORT);
            textServerPort.setBounds(313, 21, 50, 24);
            chatFrame.getContentPane().add(textServerPort);
            textServerPort.setColumns(10);
            //登录按钮
            login = new JButton(ConstantData.LOGIN);
            login.setBounds(620, 20, 70, 27);
            chatFrame.getContentPane().add(login);
            // 连接按钮事件处理程序
            login.addActionListener(arg0 -> {
                // 判断当前登录状态
                if (login.getText().equals(ConstantData.LOGIN)) {
                    // 创建客户端通信线程
                    clientThread = new ClientThread();
                    clientThread.start();
                } else {
                    clientThread.logout();
                }
            });
            //选择用户按钮
            JButton btnSelect = new JButton(ConstantData.SELECTUSER);
            btnSelect.setBounds(510,20,90,27);
            chatFrame.getContentPane().add(btnSelect);
            btnSelect.addActionListener(arg0 -> {
               openLogin();
               chatFrame.dispose();
            });
    
    
            ///*消息区*///
            JLabel lblNewLabel_2 = new JLabel(ConstantData.MESSAGE);
            lblNewLabel_2.setBounds(14, 305, 72, 23);
            chatFrame.getContentPane().add(lblNewLabel_2);
            //发送按钮
            send = new JButton(ConstantData.SEND);
            send.setEnabled(false);
            send.setBounds(444, 409, 88, 23);
            chatFrame.getContentPane().add(send);
            // 发送按钮事件处理程序
            send.addActionListener(arg0 -> {
                if(null!=clientThread){
                    clientThread.sendChatMsg();//ClientThread中的sendChatMsg方法,发送消息
                    textAreaMsg.setText("");
                }
            });
            //滚动条1
            JScrollPane scrollPane_1 = new JScrollPane();
            scrollPane_1.setBounds(14, 332, 518, 73);
            chatFrame.getContentPane().add(scrollPane_1);
            //消息输入框
            textAreaMsg = new JTextArea();
            scrollPane_1.setViewportView(textAreaMsg);
    
    
            ///*消息记录区*///
            JLabel label = new JLabel(ConstantData.CHATHISTORY);
            label.setBounds(14, 53, 72, 23);
            chatFrame.getContentPane().add(label);
            //滚动条
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(14, 72, 518, 228);
            chatFrame.getContentPane().add(scrollPane);
            //聊天记录框
            textAreaRecord = new JTextArea();
            textAreaRecord.setEditable(false);
            scrollPane.setViewportView(textAreaRecord);
    
    
            ///*用户列表区*///
            JLabel label_2 = new JLabel(ConstantData.ONLINEUSER);
            label_2.setBounds(543, 55, 72, 18);
            chatFrame.getContentPane().add(label_2);
            //滚动条
            JScrollPane scrollPane_2 = new JScrollPane();
            scrollPane_2.setBounds(546, 72, 149, 334);
            chatFrame.getContentPane().add(scrollPane_2);
            //用户列表框
            listUsers = new JList<>();
            listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scrollPane_2.setViewportView(listUsers);
            //群发选择按钮
            groupChat = new JRadioButton(ConstantData.GROUPSEND);
            buttonGroup.add(groupChat);
            groupChat.setBounds(66, 306, 104, 22);
            chatFrame.getContentPane().add(groupChat);
            //单发选择按钮
            privateChat = new JRadioButton(ConstantData.ALONESEND);
            buttonGroup.add(privateChat);
            privateChat.setSelected(true);
            privateChat.setBounds(192, 306, 72, 22);
            chatFrame.getContentPane().add(privateChat);
    
            //清屏按钮
            JButton btnClear = new JButton(ConstantData.CLEAR);
            btnClear.addActionListener(arg0 -> textAreaRecord.setText(""));
            btnClear.setBounds(460, 306, 65, 23);
            chatFrame.getContentPane().add(btnClear);
            //图片
            JButton btnPicture = new JButton(ConstantData.FIND);
            btnPicture.setBounds(310, 306, 65, 23);
            chatFrame.getContentPane().add(btnPicture);
            // 连接按钮事件处理程序
            btnPicture.addActionListener(arg0 -> {
                //判断当前系统是否支持Java AWT Desktop扩展
                if(java.awt.Desktop.isDesktopSupported()){
                    try {
                        //创建一个URI实例
                        java.net.URI uri = java.net.URI.create("http://www.baidu.com/");
                        //获取当前系统桌面扩bai展
                        java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                        //判断系统桌面是否支持要执行的功能
                        if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
                            //获取系统默认浏览器打开链接
                            dp.browse(uri);
                        }
                    } catch (java.io.IOException e) {
                        //此为无法获取系统默认浏览器
                    }
                }
    
            });
            //购物
            JButton btnBuy = new JButton(ConstantData.SHOPPING);
            btnBuy.setBounds(380, 306, 65, 23);
            chatFrame.getContentPane().add(btnBuy);
            // 连接按钮事件处理程序
            btnBuy.addActionListener(arg0 -> {
                //判断当前系统是否支持Java AWT Desktop扩展
                if(java.awt.Desktop.isDesktopSupported()){
                    try {
                        //创建一个URI实例
                        java.net.URI uri = java.net.URI.create("http://www.taobao.com/");
                        //获取当前系统桌面扩bai展
                        java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                        //判断系统桌面是否支持要执行的功能
                        if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
                            //获取系统默认浏览器打开链接
                            dp.browse(uri);
                        }
                    } catch (java.io.IOException e) {
                        //此为无法获取系统默认浏览器
                    }
                }
    
            });
    
        }
    
        /**
         * 与数据库登录页面交互函数
         */
        public void openLogin(){
            Login login=new Login(this);
            login.open();
        }
        public JFrame getChatClient() {
            return chatFrame;
        }
        public JTextField gettextUsername() {
            return textUsername;
        }
    
    
        /**
         * 内部类.客户端线程，负责与服务器交互
         */
        class ClientThread extends Thread {
            private final Component parentComponent = null;
            private  Socket socket;
            private DataInputStream dis;
            private DataOutputStream dos;
            private boolean isLogged;
    
            //线程主体
            @Override
            public void run() {
                // 连接服务器并登录
                try {
                    logIn();
                } catch (IOException e) {
                    addMessage("连接登录服务器时出现异常");
                    e.printStackTrace();
                    return;
                }
                while(isLogged) {
                    try {
                        String msg = dis.readUTF();
                        String[] parts = msg.split("#");
                        switch (parts[0]) {
                            // 处理服务器发来的用户列表报文
                            case "USERS":
                                String[] self ={username};
                                updateJList(listUsers,self,"ADD");
                                for(int i = 1; i< parts.length; i++) {
                                    modelUsers.addElement(parts[i]);
                                }
                                break;
                            // 处理服务器发来的新用户登录表报文
                            case "LOGIN":
                                modelUsers.addElement(parts[1]);
                                break;
                            case "LOGOUT":
                                modelUsers.removeElement(parts[1]);
                                addMessage(Utils.getTimeStr()+" "+parts[1]+"退出聊室");
                                break;
                            case "TALKS_ALL":
                                addMessage(Utils.getTimeStr()+parts[1]+" 跟所有人说："+parts[2]);
                                break;
                            case "TALKS_ONE":
                                addMessage(Utils.getTimeStr()+parts[1]+" 跟我说："+parts[2]);
                                break;
                            default:
                                break;
                        }
                    } catch (IOException e) {
                        isLogged = false;
                        e.printStackTrace();
                    }
                }
            }
            //登录
            private void logIn() throws IOException{
                // 获取服务器IP地址和端口
                String serverIp = textServerIP.getText();
                int serverPort = Integer.parseInt(textServerPort.getText());
                // 连接服务器，获取套接字IO流
                socket = new Socket(serverIp, serverPort);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                // 获取用户名，构建、发送登录报文
                String username = textUsername.getText();
                String msgLogin = "LOGIN#" + username;
                dos.writeUTF(msgLogin);
                dos.flush();
                // 读取服务器返回的信息，判断是否登录成功
                String response = dis.readUTF();
                if(response.equals("FAIL")) {            // 登录失败
                    addMessage("登录服务器失败,该用户已存在");
                    socket.close();
                }
                else {                                   // 登录成功
                    System.out.println("聊天室服务器登录成功");
                    addMessage("******欢迎用户^"+username+"^******");
                    isLogged = true;
                    login.setText("退出");
                    send.setEnabled(true);
                    //更新List列表信息
                    String[] self ={username};
                    updateJList(listUsers,self,"ADD");
                }
            }
    
            //更新用户列表
            public void updateJList(JList<String> jList, String[] items, String option){
                switch(option){
                    case "ADD": //添加新数据
                        for (String item : items) {
                            modelUsers.addElement(item);
                            break;
                        }
                    case "DEL"://删除数据
                        for (String item : items) {
                            modelUsers.removeElement(item);
                            break;
                        }
                    default:
                        break;
                }
                jList.setModel(modelUsers);//更新数据
            }
    
            //退出聊天室功能实现
            public void logout(){
                try {
                    Utils.sendMsg(socket, "LOGOUT#"+username);
                    //更新界面
                    modelUsers.clear();
                    listUsers.setModel(modelUsers);
                    addMessage("已经退出聊天室");
                    login.setText("登录");
                    send.setEnabled(false);
                    String[] self ={username};
                    updateJList(listUsers,self,"ADD");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    
            //发送消息
            public void sendChatMsg(){
                String msgBroadCast=null;
                String targetUser="所有人";
                if(groupChat.isSelected()){
                    msgBroadCast="TALKS_ALL#"+textAreaMsg.getText();
                }
                if(privateChat.isSelected()){
                    targetUser= listUsers.getSelectedValue();
                    if(targetUser==null){
                        JOptionPane.showMessageDialog(parentComponent,"请选择需要私聊的用户!");
                        return;
                    }
                    msgBroadCast="TALKS_ONE#"+targetUser+"#"+textAreaMsg.getText();
                }
                //发送聊天报文到服务器
                Utils.sendMsg(socket, msgBroadCast);
                //添加到消息记录框
                addMessage(Utils.getTimeStr()+" (我对"+targetUser+")说：\n"+textAreaMsg.getText());
                if(null!=msgBroadCast){
                    try{
                        dos.writeUTF(msgBroadCast);
                        dos.flush();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    
    
        /**
         * 添加消息到文本框
         */
        private void addMessage(String msg) {
            textAreaRecord.append(msg + "\n");
            // 自动滚动到文本区的最后一行
            textAreaRecord.setCaretPosition(textAreaRecord.getText().length());
        }
    }
