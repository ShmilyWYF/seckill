package com.peak.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.httpUiltr.ResponseResult;
import com.peak.pojo.TUser;
import org.thymeleaf.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserUlit {

    private final static String URL = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private final static String USER_NAME = "root";
    private final static String USER_PWD = "root";

    private static Connection cn;

    public static void createUser(int count) {
        List<TUser> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TUser tUser = new TUser();
            tUser.setId(13000000000L + i);
            tUser.setNickname("user" + i);
            tUser.setSlat("123456");
            tUser.setPassword(MD5util.inputPassToDBPass("123456789", tUser.getSlat()));
            tUser.setLoginCount(1);
            tUser.setRegisterDate(new Date());

            System.out.println(tUser.getPassword());
            list.add(tUser);
        }
        System.out.println("create user");

        try {
            Connection cn = getConnection();
            final String Sql = "insert into t_user(login_count,nickname,register_date,slat,password,id) values(?,?,?,?,?,?)";
            PreparedStatement pstm = cn.prepareStatement(Sql);
            for (int i = 0; i < list.size(); i++) {
                TUser user = list.get(i);
                System.out.println(user);
                pstm.setInt(1, user.getLoginCount());
                pstm.setString(2, user.getNickname());
                pstm.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
                pstm.setString(4, user.getSlat());
                pstm.setString(5, user.getPassword());
                pstm.setLong(6, user.getId());
                pstm.addBatch();
            }
            pstm.executeBatch();
            pstm.clearParameters();
            cn.close();

            System.out.println("insert to db");

            //生成usertiket 并保存在本地
            String urlString = "http://localhost:8080/login/toLogin";
            File file = new File("C:\\Users\\sowyf\\Desktop\\config.txt");
            if (file.exists()) {
                file.delete();
            }
            RandomAccessFile ref = new RandomAccessFile(file, "rw");
            ref.seek(0);

            for (int i = 0; i < list.size(); i++) {
                TUser user = list.get(i);
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                String params = "mobile=" + user.getId() + "&password=" + MD5util.inputPassToFromPass("123456789");
                outputStream.write(params.getBytes());
                outputStream.flush();
                //获得输入流
                InputStream inputStream = httpURLConnection.getInputStream();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buff)) > 0) {
                    bout.write(buff, 0, len);
                }
                inputStream.close();
                bout.close();
                String response = new String(bout.toByteArray());
                ObjectMapper mapper = new ObjectMapper();
                ResponseResult responseResult = mapper.readValue(response, ResponseResult.class);
                String userTicket =(String)responseResult.getToken();
                String row;
                if(StringUtils.isEmpty((String)responseResult.getToken())&&responseResult.getToken()==null){
                    row = user.getId() + "," + responseResult.getToken();
                }else {
                    row = user.getId() + "," + userTicket;
                }
                ref.seek(ref.length());
                ref.write(row.getBytes());
                ref.write("\r\n".getBytes());
                System.out.println("write to file :" + user.getId());
            }
            ref.close();
            System.out.println("over");
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        cn = DriverManager.getConnection(URL, USER_NAME, USER_PWD);
        return cn;
    }

    public static void main(String[] agers){
        createUser(1000);
    }

}
