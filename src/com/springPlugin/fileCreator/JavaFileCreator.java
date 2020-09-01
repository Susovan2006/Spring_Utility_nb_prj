/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springPlugin.fileCreator;

import com.springPlugin.db.bean.TableColoumn;
import com.springPlugin.util.SpringPluginUtil;



import java.io.FileWriter;
import java.io.File ;
import java.io.BufferedWriter;
import java.util.List ;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;




/**
 *
 * @author Sandipan
 */
public class JavaFileCreator {
    
    public static String rootDirectoryPath;
    
    private static void writeImportClasses(Map<String,String>importClasses,StringBuilder sb)throws Exception{
    if(importClasses!=null) {   
    Iterator iterator = importClasses.entrySet().iterator();
    while (iterator.hasNext()) {
    Map.Entry mapEntry = (Map.Entry) iterator.next();    
    sb.append("import" + " " +  mapEntry.getValue() + " " + ";");   
    }    
    }    
    }
    
  public static void  createBeanFile(List<TableColoumn> coloumns,String packageName,String className,boolean isBO,Map<String,String> importList){
       
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   String poClass="" ;
   String poObject="";
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();    
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");   
   Map<String,String> fieldClassList=new HashMap<String,String>();
   for(TableColoumn coloumn : coloumns){
   if(!coloumn.getDataType().startsWith("java.lang"))    
   fieldClassList.put(coloumn.getDataType(), coloumn.getDataType());  
   }
   writeImportClasses(fieldClassList,sb);
   if(isBO){       
   writeImportClasses(importList,sb); 
   sb.append("import java.util.List;");  
   }
   
   sb.append("public" + " " + "class" + " " + className + " " + "{");   
   
   for(TableColoumn coloumn : coloumns){
   String fieldClassName=coloumn.getDataType().substring(coloumn.getDataType().lastIndexOf(".")+1);
   String fieldName=SpringPluginUtil.changeFieldName(coloumn.getName());
   sb.append("private" + " " + fieldClassName + " " + fieldName + " " + ";");    
   }
   if(isBO){
   poClass=importList.get("PO_CLASS").substring(importList.get("PO_CLASS").lastIndexOf(".")+1) ;  
   poObject=SpringPluginUtil.changeFieldName(poClass)+ "List" ;
   poClass="List<" + poClass + ">"  ;  
   sb.append("private" + " " + poClass + " " + poObject  + ";");  
   }
   
   for(TableColoumn coloumn : coloumns){
   String fieldClassName=coloumn.getDataType().substring(coloumn.getDataType().lastIndexOf(".")+1);
   String fieldName=SpringPluginUtil.changeFieldName(coloumn.getName()); 
   String methodPartName=fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
   sb.append("public" + " " + fieldClassName + " " +  "get" + methodPartName + "()" + "{");   
   sb.append("return" + " " + fieldName + " " + ";" );   
   sb.append("}");    
   sb.append("public" + " " + "void" + " " +  "set" + methodPartName + "(" + fieldClassName + " " + fieldName + ")" + "{");   
   sb.append("this." + fieldName + " " + "=" + " " + fieldName + " " + ";" );   
   sb.append("}");      
   }
   
   if(isBO){
   String methodPartName=poObject.substring(0,1).toUpperCase() + poObject.substring(1);    
   sb.append("public" + " " + poClass + " " +  "get" + methodPartName + "()" + "{");   
   sb.append("return" + " " + poObject + " " + ";" );   
   sb.append("}");  
   sb.append("public" + " " + "void" + " " +  "set" + methodPartName  + "(" + poClass + " " + poObject + ")" + "{");   
   sb.append("this." + poObject + " " + "=" + " " + poObject + " " + ";" );   
   sb.append("}");     
   }  
   sb.append("}");
   //Close the output stream
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   out.close();
   }catch (Exception e){//Catch exception if any
       e.printStackTrace();
   System.err.println("Error: " + e.getMessage());
   }
   }
 
  public static void  createDaoImplFile(List<TableColoumn> coloumns,String packageName,String className,Map<String,String> importList,String tableName,String schema){
       
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();    
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");       
   sb.append("import java.util.HashMap;");   
   sb.append("import java.util.Map;");  
   sb.append("import java.util.List;");   
   sb.append("import javax.sql.DataSource;");   
   sb.append("import org.springframework.beans.factory.annotation.Autowired;");      
   sb.append("import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;");   
   sb.append("import org.springframework.stereotype.Repository;");   
   sb.append("import org.springframework.jdbc.core.RowMapper;");   
   
   writeImportClasses(importList, sb);   
   sb.append("@Repository\n");   
   sb.append("public" + " " + "class" + " " + className + " implements " + importList.get("DAO_CLASS").substring(importList.get("DAO_CLASS").lastIndexOf(".")+1) + " {");   
   String select_query="private final String SEL_" + tableName.toUpperCase() + " = \"" + "Select * from" + " " + tableName + " " + "nolock where" + " ";
   String delete_query="private final String DEL_" + tableName.toUpperCase() + " = \"" + "Delete from" + " " + tableName + " " + "where" + " ";
   String update_query="private final String UPD_" + tableName.toUpperCase() + " = \"" + "Update" + " " + tableName + " " + "set" + " " ;
   String insert_query="private final String INS_" + tableName.toUpperCase() + " = \"" + "Insert into" + " " + tableName ;
   String clause="";
   String updClause="";
   String insClause1="("  ;
   String insClause2="("  ;
   String updMap="";
   String map="";
   String boClass=importList.get("BO_CLASS").substring(importList.get("BO_CLASS").lastIndexOf(".")+1) ;
   String poClass=importList.get("PO_CLASS").substring(importList.get("PO_CLASS").lastIndexOf(".")+1) ;
   String boObject=SpringPluginUtil.changeFieldName(boClass);
   //String poObject=SpringPluginUtil.changeFieldName(poClass);        
   int i=0 ;
   for(TableColoumn coloumn : coloumns){
   String fieldName=SpringPluginUtil.changeFieldName(coloumn.getName()); 
   String methodPartName=fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);    
   clause=clause+coloumn.getName().toUpperCase() + "=:" + fieldName + " " + "and" + " " ;  
   updClause=updClause+coloumn.getName().toUpperCase() + "=:new" + methodPartName + "," ;   
   insClause1=insClause1 + coloumn.getName().toUpperCase() + "," ;
   insClause2=insClause2 + fieldName + "," ; 
   updMap=updMap+"map.put(" + "\""  + "new" + methodPartName + "\"" + ","  + boObject + "." + "get" + methodPartName + "()"  + ") ; \n" ;
   map=map+"map.put(" + "\"" + fieldName + "\"" + "," + boObject + "." + "get" + methodPartName + "()" + ") ; \n" ;
   i++ ; 
   }
   clause=clause.substring(0,clause.lastIndexOf("and")) ;
   updClause=updClause.substring(0,updClause.lastIndexOf(",")) ;
   insClause1=insClause1.substring(0,insClause1.lastIndexOf(",")) + ")" ;
   insClause2=insClause2.substring(0,insClause2.lastIndexOf(",")) + ")" ;
   select_query=select_query + clause + " \"" + ";" ; 
   delete_query=delete_query + clause + " \"" + ";" ;
   insert_query=insert_query + " " + insClause1 +  " values " + insClause2 + " \"" + ";" ;
   update_query=update_query + clause.replaceAll(" and ", ",") + " " + "where" + " " + updClause + " \"" + ";" ;   
   sb.append(select_query);   
   sb.append(update_query);   
   sb.append(insert_query);   
   sb.append(delete_query);   
   sb.append("private SimpleJdbcTemplate jdbcTemplate;");   
   sb.append("@Autowired\n");   
   sb.append("public void setJdbcTemplate(DataSource" +" " + schema +"DataSource) {");   
   sb.append("this.jdbcTemplate = new SimpleJdbcTemplate(" + schema +"DataSource);");   	    
   sb.append("}");   
   sb.append("@Override\n");   
   String formatedTableName=SpringPluginUtil.changeFieldName(tableName);
   String methodPartName=formatedTableName.substring(0,1).toUpperCase() + formatedTableName.substring(1) + "s";   
   sb.append("public List<" + poClass + "> select" + methodPartName  + "(" + boClass + " " + boObject + "){");   
   String rowMapperClass=importList.get("ROW_MAPPER_CLASS").substring(importList.get("ROW_MAPPER_CLASS").lastIndexOf(".")+1);
   sb.append("RowMapper<" + poClass + "> stdRowMapper =new " + rowMapperClass + "() ;");   
   sb.append("Map<String, Object> map = new HashMap<String, Object>();");   
   sb.append(map);   
   sb.append("return jdbcTemplate.query(" + "SEL_" + tableName.toUpperCase() + ",stdRowMapper,map);");   
   sb.append("}");   
   sb.append("@Override\n");   
   sb.append("public void update" + methodPartName + "(" + boClass + " " + boObject + "){");      
   sb.append("Map<String, Object> map = new HashMap<String, Object>();");   
   sb.append(map);   
   sb.append(updMap);   
   sb.append("jdbcTemplate.update(" + "UPD_" + tableName.toUpperCase() + ",map);");   
   sb.append("}");  
   sb.append("@Override\n");   
   sb.append("public void delete" + methodPartName + "(" + boClass + " " + boObject + "){");      
   sb.append("Map<String, Object> map = new HashMap<String, Object>();");   
   sb.append(map);      
   sb.append("jdbcTemplate.update(" + "DEL_" + tableName.toUpperCase() + ",map);");   
   sb.append("}");   
   sb.append("@Override\n");   
   sb.append("public void insert" + methodPartName + "(" + boClass + " " + boObject + "){");      
   sb.append("Map<String, Object> map = new HashMap<String, Object>();");   
   sb.append(map);      
   sb.append("jdbcTemplate.update(" + "INS_" + tableName.toUpperCase() + ",map);");   
   sb.append("}");   
   sb.append("}");
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   //Close the output stream   
   out.close();
   }catch (Exception e){//Catch exception if any
       e.printStackTrace();
   System.err.println("Error: " + e.getMessage());
   }
   }
 
  public static void  createRowMapperFile(List<TableColoumn> coloumns,String packageName,Map<String,String> importList,String className){
       
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();
   /*String formatedTableName=SpringPluginUtil.changeFieldName(tableName);
   String className=formatedTableName.substring(0,1).toUpperCase() + formatedTableName.substring(1) + "RowMapper"; */
   
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");   
   sb.append("import java.sql.ResultSet;");   
   sb.append("import java.sql.SQLException;");
   sb.append("import org.springframework.jdbc.core.RowMapper;");    
   writeImportClasses(importList, sb);
   
   /*Map<String,String> fieldClassList=new HashMap<String,String>();
   for(TableColoumn coloumn : coloumns){
   if(!coloumn.getDataType().startsWith("java.lang"))    
   fieldClassList.put(coloumn.getDataType(), coloumn.getDataType());  
   }
   writeImportClasses(fieldClassList,sb);*/
   
   String poClass=importList.get("PO_CLASS").substring(importList.get("PO_CLASS").lastIndexOf(".")+1) ;
   String poObject=SpringPluginUtil.changeFieldName(poClass);   
   sb.append("public" + " " + "class" + " " + className + " implements RowMapper<" + poClass + "> " + "{");  
   sb.append("@Override\n");   
   sb.append("public" + " " + poClass + " " + "mapRow(ResultSet resultSet,int arg1)");   
   sb.append("throws SQLException {");   
   sb.append(poClass + " " + poObject + "= new " + poClass + "() ;");
   for(TableColoumn coloumn : coloumns){
       String dataType=coloumn.getDataType().substring(coloumn.getDataType().lastIndexOf(".")+1) ;
       if(dataType.equals("Integer"))
        dataType="Int"   ;  
   String fieldName=SpringPluginUtil.changeFieldName(coloumn.getName());     
   String methodPartName=fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);      
   sb.append(poObject + ".set" + methodPartName + "(resultSet.get" + dataType+ "(\"" + coloumn.getName().toUpperCase() + "\"));");  
    
   }   
   sb.append("return " + poObject + ";");   
   sb.append("}");   
   sb.append("}");
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   //Close the output stream
   out.close();
   }catch (Exception e){//Catch exception if any
       e.printStackTrace();
   System.err.println("Error: " + e.getMessage());
   }
   } 
  
  public static void  createDaoFile(List<TableColoumn> coloumns,String packageName,String className,Map<String,String> importList,String tableName){
       
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();    
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");   
   sb.append("import java.util.List;");      
   writeImportClasses(importList, sb);   
   sb.append("public" + " " + "interface" + " " + className + " " + "{");   
   
   String boClass=importList.get("BO_CLASS").substring(importList.get("BO_CLASS").lastIndexOf(".")+1) ;
   String poClass=importList.get("PO_CLASS").substring(importList.get("PO_CLASS").lastIndexOf(".")+1) ;
   String boObject=SpringPluginUtil.changeFieldName(boClass);
   String poObject=SpringPluginUtil.changeFieldName(poClass);  
   String formatedTableName=SpringPluginUtil.changeFieldName(tableName);
   String methodPartName=formatedTableName.substring(0,1).toUpperCase() + formatedTableName.substring(1) + "s"; 
   
   sb.append("public List<" + poClass + "> select" + methodPartName + "(" + boClass + " " + boObject + ");"); 
   sb.append("public void update" + methodPartName + "(" + boClass + " " + boObject + ");");  
   sb.append("public void delete" + methodPartName + "(" + boClass + " " + boObject + ");");  
   sb.append("public void insert" + methodPartName + "(" + boClass + " " + boObject + ");");   
   sb.append("}");
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   //Close the output stream
   out.close();
   }catch (Exception e){//Catch exception if any
   e.printStackTrace();    
   System.err.println("Error: " + e.getMessage());
   }
   }
  
  public static void  createControllerFile(String packageName,String className,Map<String,String> importList){
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();  
  
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");  
   sb.append(" import org.springframework.beans.factory.annotation.Autowired;");   
   sb.append("import org.springframework.stereotype.Controller;");  
   sb.append("import org.springframework.web.bind.annotation.ModelAttribute;");      
   sb.append("import org.springframework.web.bind.annotation.RequestMapping;");   
   sb.append("import org.springframework.web.bind.annotation.RequestMethod;");       
   sb.append("import org.springframework.web.servlet.ModelAndView;");   
   sb.append("import org.springframework.validation.BindingResult;");      
   writeImportClasses(importList, sb);  
   
   String serviceClass=importList.get("SERVICE_CLASS").substring(importList.get("SERVICE_CLASS").lastIndexOf(".")+1) ;
   String serviceObject=SpringPluginUtil.changeFieldName(serviceClass);
   sb.append("@Controller\n");     
   sb.append("public" + " " + "class" + " " + className +  "{");   
   sb.append("@Autowired\n");     
   sb.append("private" +  " " + serviceClass + " " + serviceObject +  ";");   
   sb.append("@RequestMapping(value=\"/url1.do\",method = RequestMethod.POST)");   
   String boClass=importList.get("BO_CLASS").substring(importList.get("BO_CLASS").lastIndexOf(".")+1) ;
   String boObject=SpringPluginUtil.changeFieldName(boClass);
   String methodPartName=boClass.replace("BO", "") ;
   String modelAttributeName=boObject.replace("BO", "");
   sb.append("public ModelAndView" + " " + "do" + methodPartName + "(@ModelAttribute(\"" + modelAttributeName +"\") " +  boClass + " " + boObject + ",BindingResult result) {");      
   sb.append(serviceObject + ".service(" + boObject + ")" + ";");   
   sb.append(" return new ModelAndView(\"jspPageName\",\"modelAttribute\"," + boObject + ");  ");     
   sb.append("}");   
   sb.append("}");
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   //Close the output stream
   out.close();
   }catch (Exception e){//Catch exception if any
       e.printStackTrace();
   System.err.println("Error: " + e.getMessage());
   }
   } 
  
  public static void  createServiceImplFile(String packageName,String className,Map<String,String> importList,String tableName){
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();  
  
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");  
   sb.append(" import org.springframework.beans.factory.annotation.Autowired;");   
   sb.append("import org.springframework.stereotype.Service;");      
   writeImportClasses(importList, sb);
   
   String daoClass=importList.get("DAO_CLASS").substring(importList.get("DAO_CLASS").lastIndexOf(".")+1) ;
   String daoObject=SpringPluginUtil.changeFieldName(daoClass);
   String serviceClass=importList.get("SERVICE_CLASS").substring(importList.get("SERVICE_CLASS").lastIndexOf(".")+1) ;
   sb.append("@Service\n");
     
   sb.append("public" + " " + "class" + " " + className + " " + "implements" + " " + serviceClass + "{");   
   sb.append("@Autowired\n");     
   sb.append("private" +  " " + daoClass + " " + daoObject +  ";");
       
   
   String boClass=importList.get("BO_CLASS").substring(importList.get("BO_CLASS").lastIndexOf(".")+1) ;
   String boObject=SpringPluginUtil.changeFieldName(boClass);
   String formatedTableName=SpringPluginUtil.changeFieldName(tableName);
   String methodPartName=formatedTableName.substring(0,1).toUpperCase() + formatedTableName.substring(1) + "s";
   sb.append("public void service(" + boClass +  " " + SpringPluginUtil.changeFieldName(boClass) + ")" + "{");   
   sb.append(boObject + ".set" + boClass.replace("BO", "POList") + "(" + daoObject + ".select" + methodPartName + "(" + boObject + "));" );   
   sb.append(daoObject + ".insert" + methodPartName + "(" + boObject + ");" );    
   sb.append(daoObject + ".update" + methodPartName + "(" + boObject + ");" );    
   sb.append(daoObject + ".delete" + methodPartName + "(" + boObject + ");" );    
   sb.append("}");   
   sb.append("}");
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   //Close the output stream
   out.close();
   }catch (Exception e){//Catch exception if any
   e.printStackTrace();
   System.err.println("Error: " + e.getMessage());
   }
   } 
  
  
  public static void  createServiceFile(String packageName,String className,Map<String,String> importList,String tableName){
      try{
   // Create file 
   StringTokenizer st = new StringTokenizer(packageName,".");	
   String directoryPath=rootDirectoryPath;
   while (st.hasMoreElements()) {
   directoryPath=directoryPath+ "\\" + st.nextElement();   
		}
   if(!new File(directoryPath).isDirectory())
   new File(directoryPath).mkdirs();  
  
   FileWriter fstream = new FileWriter(directoryPath + "\\" + className + ".java");
   BufferedWriter out = new BufferedWriter(fstream);
   StringBuilder sb = new StringBuilder();
   sb.append("package" + " " + packageName + " " + ";");         
   writeImportClasses(importList, sb);      
   sb.append("public" + " " + "interface" + " " + className + " "  + "{");
      
   String boClass=importList.get("BO_CLASS").substring(importList.get("BO_CLASS").lastIndexOf(".")+1) ;
   String boObject=SpringPluginUtil.changeFieldName(boClass);
   sb.append("public void service(" + boClass +  " " + boObject + ");");      
   sb.append("}");
   out.write(SpringPluginUtil.formatJavaFile(sb.toString()));
   //Close the output stream   
   out.close();
   }catch (Exception e){//Catch exception if any
   e.printStackTrace();
   System.err.println("Error: " + e.getMessage());
   }
   } 
  
  
  
  
  
  }
  
  
  
    
    

