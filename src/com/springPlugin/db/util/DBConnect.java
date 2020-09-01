/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springPlugin.db.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement ;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.springPlugin.db.bean.TableColoumn;
import com.springPlugin.fileCreator.ConfigFileCreator;
import com.springPlugin.fileCreator.JavaFileCreator;
        
/**
 *
 * @author Sandipan
 */
public class DBConnect {

    /**
     * @param args the command line arguments
     */
    String url="" ;
    String driver="";
    String username="";
    String password="";
    Connection connection=null ;
    
    public DBConnect(String dbURL,String dbDriver,String dbUsername,String dbPassword ){
        url=dbURL;
        driver=dbDriver;
        username=dbUsername;
        password=dbPassword ;
    }
    
    
    
    public void initConnection() throws Exception{
     Class.forName(driver);    
     if(connection==null){        
     connection = DriverManager.getConnection(url,username,password);  
     connection.setAutoCommit(false);
                        }
                                                 } 
    
    public List<String> getDatabaseTables() throws Exception{
    String table_type[]={"TABLE"};
    List<String> tableList=new ArrayList<String>();
    ResultSet rs = connection.getMetaData().getTables(null,null,null,table_type);
    // you might need a filter here if your database mixes system
    // tables with user tables, e.g. Microsoft SQL Server
    while (rs.next()) {
        tableList.add(rs.getString("TABLE_NAME"));      
                       }
    rs.close();  
    return tableList ;
                                                    }
    
    public List<TableColoumn> getTableColoumns(String tableName) throws Exception{
      List<TableColoumn> tableColoumnList=new ArrayList<TableColoumn>();
     String sql="Select * from " + tableName ;
     Statement statement=connection.createStatement();
     ResultSet rs = statement.executeQuery(sql);
     if(rs!=null){
     ResultSetMetaData rsmd=rs.getMetaData();     
     for(int i=1;i<=rsmd.getColumnCount();i++){
     TableColoumn tableColoumn=new TableColoumn();
     tableColoumn.setName(rsmd.getColumnName(i));
     //tableColoumn.setDataType(rsmd.getColumnTypeName(i)+ "[" + rsmd.getColumnClassName(i)+ "]");
     tableColoumn.setDataType(rsmd.getColumnClassName(i));
     tableColoumnList.add(tableColoumn);
                                               }
                 }     
     return tableColoumnList ;
    }
    
    public static void main(String[] args) throws Exception {
       DBConnect db=new DBConnect("jdbc:odbc:PASSWORD_SAFE","sun.jdbc.odbc.JdbcOdbcDriver","","");
       db.initConnection();
       System.out.println(db.getDatabaseTables());
       System.out.println(db.getTableColoumns("SESSION_INFO"));
        
       JavaFileCreator.createBeanFile(db.getTableColoumns("SESSION_INFO"), "com.test.po" ,"TestPO",false,null);
       
       Map<String,String> list1=new HashMap<String, String>();       
       list1.put("PO_CLASS","com.test.po.TestPO"); 
       list1.put("BO_CLASS","com.test.bo.TestBO"); 
       JavaFileCreator.createDaoFile(db.getTableColoumns("SESSION_INFO"), "com.test.dao" ,"TestDao",list1,"SESSION_INFO");
       
       Map<String,String> list2=new HashMap<String, String>();    
       list2.put("PO_CLASS","com.test.po.TestPO");      
       JavaFileCreator.createRowMapperFile(db.getTableColoumns("SESSION_INFO"), "com.test.rowmapper" ,list2,"SESSION_INFO");
       
       Map<String,String> list3=new HashMap<String, String>();      
       list3.put("DAO_CLASS","com.test.dao.TestDao");
       list3.put("PO_CLASS","com.test.po.TestPO");
       list3.put("BO_CLASS","com.test.bo.TestBO");
       list3.put("ROW_MAPPER_CLASS","com.test.rowmapper.SessionInfoRowMapper");
       JavaFileCreator.createDaoImplFile(db.getTableColoumns("SESSION_INFO"), "com.test.dao.impl" ,"TestDaoImpl",list3,"SESSION_INFO","passwordSafe");
       
       Map<String,String> list4=new HashMap<String, String>();       
       list4.put("BO_CLASS","com.test.bo.TestBO");
       list4.put("SERVICE_CLASS","com.test.service.TestService");
       JavaFileCreator.createControllerFile("com.test.controller" ,"TestController",list4);      
       
       Map<String,String> list5=new HashMap<String, String>();       
       list5.put("PO_CLASS","com.test.po.TestPO"); 
       JavaFileCreator.createBeanFile(db.getTableColoumns("SESSION_INFO"), "com.test.bo" ,"TestBO",true,list5);
       
       Map<String,String> list6=new HashMap<String, String>();       
       list6.put("BO_CLASS","com.test.bo.TestBO"); 
       JavaFileCreator.createServiceFile("com.test.service" ,"TestService",list6,"SESSION_INFO");
       
       Map<String,String> list7=new HashMap<String, String>();       
       list7.put("BO_CLASS","com.test.bo.TestBO");
       list7.put("SERVICE_CLASS","com.test.service.TestService");
       list7.put("DAO_CLASS","com.test.dao.TestDao");
       JavaFileCreator.createServiceImplFile("com.test.service.impl" ,"TestServiceImpl",list7,"SESSION_INFO");
       
       ConfigFileCreator.addToBeans("passwordsafe", "", "", "myURL","ms-access","testContext.xml","com.test");
    }
}
