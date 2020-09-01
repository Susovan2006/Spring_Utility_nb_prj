/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springPlugin.db.ui;

import com.springPlugin.db.bean.TableColoumn;
import com.springPlugin.db.util.DBConnect;
import com.springPlugin.exception.CustomException;
import com.springPlugin.fileCreator.ConfigFileCreator;
import com.springPlugin.fileCreator.JavaFileCreator;
import com.springPlugin.util.FileSaver;
import com.springPlugin.util.SpringPluginUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Font;
import java.awt.Dimension;

/**
 *
 * @author Sandipan
 */
public class PluginUI extends JFrame {

    Properties pluginProperties;
    JLabel dataBaseTypeLabel;
    JComboBox dataBaseTypeList;
    JLabel hostNameLabel;
    JTextField hostNameText;
    JLabel portNameLabel;
    JTextField portNameText;
    JLabel schemaNameLabel;
    JTextField schemaNameText;
    JLabel userNameLabel;
    JTextField userNameText;
    JLabel passwordLabel;
    JPasswordField passwordText;
    JPanel panel1;
    JPanel panel2;
    JButton connectButton;
    JButton backButton;
    JButton generateButton ;
    JLabel tableSelectLabel;
    JComboBox tableList;
    String dbUrl;
    String dbDriver;
    Map<String, String[]> databaseDetails;
    JTable table1;
    JTable table2;
    JScrollPane scrollPane1;
    JScrollPane scrollPane2;
    String columnNames[];
    Object dataValues[][];
    DBConnect db;
    JLabel basePackageLabel;
    JTextField  basePackageText ;

    public void init() {
        setTitle("Spring Plugin");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);
        initPanel1();
        initPanel2();
    }
    
    
    private void initScrollableTable() {
        table1 = new JTable() {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        table2 = new JTable() {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };        
        // Configure some of JTable's paramters
        table1.setShowHorizontalLines(true);
        table2.setShowHorizontalLines(true);
        // Change the selection colour
        table1.setSelectionForeground(Color.white);
        table2.setSelectionForeground(Color.white);
        table1.setSelectionBackground(Color.GREEN);
        table2.setSelectionBackground(Color.GREEN);
        // Add the table to a scrolling pane
        scrollPane1 = new JScrollPane(table1);
        scrollPane1.setBounds(20, 50, 400, 100);
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        addToPanel(panel2, scrollPane1, null);
        String[] columnNames_static = new String[3];
        columnNames_static[0] = "Class Type";
        columnNames_static[1] = "Class Name";
        columnNames_static[2] = "Required?";    
        
        Object[][] dataValues_static = new Object[][]{         
          { "Persistance Object" ,"",true },
          { "Business Object" ,"",true },
          { "Row Mapper" ,"",true },
          { "Service" ,"",true },
          { "DAO" ,"",true },
          { "Controller" ,"",true },
          { "DAO Implementation" ,"",true },          
          { "Service Implementation" ,"",true },        
          { "Applications Context" ,"",true }, 
        };
        scrollPane2 = new JScrollPane(table2);
        DefaultTableModel model_static = new DefaultTableModel(dataValues_static, columnNames_static);
        table2.setModel(model_static);
        scrollPane2.setBounds(20, 180, 400, 200);
        addToPanel(panel2, scrollPane2,null);
       
    }

    private void updateTable(String tableName) {

        CreateColumns();
        try {
            CreateData(db.getTableColoumns(tableName));
        } catch (Exception ex) {           
            showQuickErrorDialog("Failed to populate table data", ex);          
        }
        // Create a new table instance
        DefaultTableModel model = new DefaultTableModel(dataValues, columnNames);
        table1.setModel(model);
    }

    private void CreateColumns() {
        // Create column string labels
        columnNames = new String[3];
        columnNames[0] = "Field Name";
        columnNames[1] = "Data Type";
        columnNames[2] = "Required?";
    }

    private void CreateData(List<TableColoumn> coloumnsList) {

        // Create data for each element
        dataValues = new Object[coloumnsList.size()][3];

        for (int iY = 0; iY < coloumnsList.size(); iY++) {
            dataValues[iY][0] = coloumnsList.get(iY).getName();
            dataValues[iY][1] = coloumnsList.get(iY).getDataType();
            dataValues[iY][2] = true;
        }
    }

    public static void main(String args[]) throws Exception {
        // Create an instance of the test application
        PluginUI mainScreen = new PluginUI();
        mainScreen.pluginProperties = new Properties();
        mainScreen.pluginProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/application.properties"));
        mainScreen.init();
    }

    private void addToPanel(JPanel panel, Component component) {
        panel.add(component);
        panel.revalidate();
        panel.repaint();
    }

    private void addToPanel(JPanel panel, Component component, Object constrains) {
        panel.add(component, constrains);
        panel.add(component);
        panel.revalidate();
        panel.repaint();
    }

    private void reloadWindow() {
        validate();
        doLayout();
        repaint();
    }

    private List<TableColoumn> getSelectedFields(TableModel model) {

        List<TableColoumn> selectedFieldList = new ArrayList<TableColoumn>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 2)) {
                TableColoumn tc = new TableColoumn();
                tc.setName((String) model.getValueAt(i, 0));
                tc.setDataType((String) model.getValueAt(i, 1));
                selectedFieldList.add(tc);
            }
        }
        return selectedFieldList;

    }
    
    private void validateSelection(TableModel model) throws Exception {
   
        
       if(basePackageText.getText()==null || basePackageText.getText().equals("")){          
       throw new CustomException("Need to enter the base package name");       
       } 
       else{
       basePackageText.setText(basePackageText.getText().toLowerCase());
       }
       
       Boolean  po=(Boolean) model.getValueAt(0, 2);      
       Boolean  bo=(Boolean) model.getValueAt(1, 2);
       Boolean  rm=(Boolean) model.getValueAt(2, 2);
       Boolean  service=(Boolean) model.getValueAt(3, 2);      
       Boolean  dao=(Boolean) model.getValueAt(4, 2);
       Boolean  controller=(Boolean) model.getValueAt(5, 2);
       Boolean  daoImpl=(Boolean) model.getValueAt(6, 2);      
       Boolean  serviceImpl=(Boolean) model.getValueAt(7, 2);
       Boolean  act=(Boolean) model.getValueAt(8, 2);       
           
        if((bo||rm) && !po){
        System.out.println("Need to Select the Persistance Object");
        throw new CustomException("Need to Select the Persistance Object");   
       }
       
       else if(service && !bo){
        throw new CustomException("Need to Select the Business Object");     
       }
       
       else if(dao && !(bo&&rm)){
         throw new Exception("Need to Select the Business Object and RowMapper");    
       }
       else if(controller &&!service){
          throw new Exception("Need to Select the Service");   
       }
       
       else if(daoImpl &&!dao){
         throw new Exception("Need to Select the DAO");     
       }
       
        else if(serviceImpl &&!service){
           throw new Exception("Need to Select the Service");      
       }
       
       //UIRenderer tableRenderer = new UIRenderer();
       //table2.setDefaultRenderer(Object.class, tableRenderer);
       for(int i=0 ;i<model.getRowCount();i++){
           if((Boolean) model.getValueAt(i, 2) && (model.getValueAt(i, 1).equals(""))){             
             String name="";
             switch(i){                 
                 case 0:name="TestPO" ; break;
                 case 1:name="TestBO" ; break;           
                 case 2:name=SpringPluginUtil.changeFieldName((String)tableList.getSelectedItem());
                        name=name.substring(0,1).toUpperCase() + name.substring(1) + "RowMapper" ;
                        break;
                 case 3:name="TestService" ; break;
                 case 4:name="TestDAO" ; break;
                 case 5:name="TestDAOImpl" ;break;
                 case 6:name="TestServiceImpl" ;break;
                 case 7:name="TestController" ;break;                 
                 case 8:name="application-context" ;break; 
             }
             model.setValueAt(name, i, 1); 
             
            }    
       }
           /*else{
           tableRenderer.getTableCellRendererComponent(table2, "C", false, false, i, 1);        
           }*/
   
           
           if(po){           
           JavaFileCreator.createBeanFile(db.getTableColoumns((String)tableList.getSelectedItem()), basePackageText.getText()+"."+"po" ,(String)model.getValueAt(0, 1),false,null);
           }
           if(bo){           
           Map<String,String> importList=new HashMap<String, String>();       
           importList.put("PO_CLASS",basePackageText.getText()+".po."+(String)model.getValueAt(0, 1)); 
           JavaFileCreator.createBeanFile(getSelectedFields(table1.getModel()), basePackageText.getText()+"."+"bo" ,(String)model.getValueAt(1, 1),true,importList);
           }
           if(rm){           
           Map<String,String> importList=new HashMap<String, String>();          
           importList.put("PO_CLASS",basePackageText.getText()+".po."+(String)model.getValueAt(0, 1));      
           JavaFileCreator.createRowMapperFile(db.getTableColoumns((String)tableList.getSelectedItem()), basePackageText.getText()+"."+"rowmapper",importList,(String)model.getValueAt(2, 1));           
           }
           if(service){           
           Map<String,String> importList=new HashMap<String, String>();       
           importList.put("BO_CLASS",basePackageText.getText()+".bo."+(String)model.getValueAt(1, 1)); 
           JavaFileCreator.createServiceFile(basePackageText.getText()+"."+"service" ,(String)model.getValueAt(3, 1),importList,(String)tableList.getSelectedItem());    
           }
           if(dao){           
           Map<String,String> importList=new HashMap<String, String>();       
           importList.put("PO_CLASS",basePackageText.getText()+".po."+(String)model.getValueAt(0, 1));   
           importList.put("BO_CLASS",basePackageText.getText()+".bo."+(String)model.getValueAt(1, 1)); 
           JavaFileCreator.createDaoFile(db.getTableColoumns((String)tableList.getSelectedItem()), basePackageText.getText()+"."+"dao" ,(String)model.getValueAt(4, 1),importList,(String)tableList.getSelectedItem());    
           }
           if(daoImpl){           
           Map<String,String> importList=new HashMap<String, String>();      
           importList.put("DAO_CLASS",basePackageText.getText()+".dao."+(String)model.getValueAt(4, 1));
           importList.put("PO_CLASS",basePackageText.getText()+".po."+(String)model.getValueAt(0, 1));   
           importList.put("BO_CLASS",basePackageText.getText()+".bo."+(String)model.getValueAt(1, 1)); 
           importList.put("ROW_MAPPER_CLASS",basePackageText.getText()+".rowmapper."+(String)model.getValueAt(2, 1));
           JavaFileCreator.createDaoImplFile(getSelectedFields(table1.getModel()), basePackageText.getText()+".dao.impl" ,(String)model.getValueAt(5, 1),importList,(String)tableList.getSelectedItem(),SpringPluginUtil.changeFieldName(schemaNameText.getText()));
           }
           if(serviceImpl){           
           Map<String,String> importList=new HashMap<String, String>();       
           importList.put("BO_CLASS",basePackageText.getText()+".bo."+(String)model.getValueAt(1, 1)); 
           importList.put("SERVICE_CLASS",basePackageText.getText()+".service."+(String)model.getValueAt(3,1));
           importList.put("DAO_CLASS",basePackageText.getText()+".dao."+(String)model.getValueAt(4, 1));
           JavaFileCreator.createServiceImplFile(basePackageText.getText()+".service.impl" ,(String)model.getValueAt(6,1),importList,(String)tableList.getSelectedItem());    
           }
           if(controller){           
           Map<String,String> importList=new HashMap<String, String>();       
           importList.put("BO_CLASS",basePackageText.getText()+".bo."+(String)model.getValueAt(1, 1)); 
           importList.put("SERVICE_CLASS",basePackageText.getText()+".service."+(String)model.getValueAt(3,1));
           JavaFileCreator.createControllerFile(basePackageText.getText()+".controller" ,(String)model.getValueAt(7,1),importList);  
           }
           if(act){           
           ConfigFileCreator.addToBeans(SpringPluginUtil.changeFieldName(schemaNameText.getText()), "", "", dbUrl,dbDriver,(String)model.getValueAt(8,1),basePackageText.getText());    
           }
          
       

    }
    

    private void initPanel1() {
        panel1 = new JPanel();
        panel1.setLayout(null);
        add(panel1);
        dataBaseTypeLabel = new JLabel("Select Dtatbase Type");
        dataBaseTypeLabel.setBounds(20, 20, 150, 20);
        addToPanel(panel1, dataBaseTypeLabel);
        initDatabaseTypeList();
        hostNameLabel = new JLabel("Enter Host Name/IP");
        hostNameLabel.setBounds(290, 20, 150, 20);
        hostNameLabel.setVisible(true);
        addToPanel(panel1, hostNameLabel);
        hostNameText = new JTextField();
        hostNameText.setBounds(450, 20, 100, 20);
        addToPanel(panel1, hostNameText);
        hostNameText.setVisible(true);
        portNameLabel = new JLabel("Enter Port Number");
        portNameLabel.setBounds(560, 20, 150, 20);
        portNameLabel.setVisible(true);
        addToPanel(panel1, portNameLabel);
        portNameText = new JTextField();
        portNameText.setBounds(720, 20, 100, 20);
        addToPanel(panel1, portNameText);
        portNameText.setVisible(true);
        schemaNameLabel = new JLabel("Enter Schema Name");
        schemaNameLabel.setBounds(20, 50, 150, 20);
        addToPanel(panel1, schemaNameLabel);
        schemaNameText = new JTextField();
        schemaNameText.setBounds(180, 50, 100, 20);
        addToPanel(panel1, schemaNameText);
        userNameLabel = new JLabel("Enter User Name");
        userNameLabel.setBounds(290, 50, 150, 20);
        addToPanel(panel1, userNameLabel);
        userNameText = new JTextField();
        userNameText.setBounds(450, 50, 100, 20);
        addToPanel(panel1, userNameText);
        passwordLabel = new JLabel("Enter Password");
        passwordLabel.setBounds(560, 50, 150, 20);
        addToPanel(panel1, passwordLabel);
        passwordText = new JPasswordField();
        passwordText.setBounds(720, 50, 100, 20);
        addToPanel(panel1, passwordText);
        initConnectButton();

    }

    private void initDatabaseTypeList() {
        dataBaseTypeList = new JComboBox();
        dataBaseTypeList.setBounds(180, 20, 100, 20);
        addToPanel(panel1, dataBaseTypeList);
        String items[] = pluginProperties.getProperty("database.types").split(",");
        databaseDetails = new HashMap<String, String[]>();
        for (int i = 0; i < items.length; i++) {
            String[] info = pluginProperties.getProperty("database." + items[i] + ".details").split("\\|\\|");
            dataBaseTypeList.addItem(info[0]);
            databaseDetails.put(info[0], new String[]{info[1], info[2]});
        }
        dataBaseTypeList.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (databaseDetails.get(dataBaseTypeList.getSelectedItem())[1].contains("host")) {
                    hostNameLabel.setVisible(true);
                    hostNameText.setVisible(true);
                } else {
                    hostNameLabel.setVisible(false);
                    hostNameText.setVisible(false);
                }
                if (databaseDetails.get(dataBaseTypeList.getSelectedItem())[1].contains("port")) {
                    portNameLabel.setVisible(true);
                    portNameText.setVisible(true);
                } else {
                    portNameLabel.setVisible(false);
                    portNameText.setVisible(false);
                }
                if (databaseDetails.get(dataBaseTypeList.getSelectedItem())[1].contains("service")) {
                    schemaNameLabel.setText("Enter Service Name");
                } else if (databaseDetails.get(dataBaseTypeList.getSelectedItem())[1].contains("dsn")) {
                    schemaNameLabel.setText("Enter DNS Name");
                } else if (databaseDetails.get(dataBaseTypeList.getSelectedItem())[1].contains("dbpath")) {
                    schemaNameLabel.setText("Enter Schema Lacation");
                } else {
                    schemaNameLabel.setText("Enter Schema Name");
                }

            }
        });
    }

    private void initConnectButton() {
        connectButton = new JButton("Connect");
        connectButton.setBounds(360, 80, 100, 20);
        addToPanel(panel1, connectButton);
        connectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dbUrl = databaseDetails.get(dataBaseTypeList.getSelectedItem())[1];
                dbDriver = databaseDetails.get(dataBaseTypeList.getSelectedItem())[0];
                if (dbUrl.contains("host")) {
                    dbUrl = dbUrl.replaceAll("host", hostNameText.getText());
                }
                if (dbUrl.contains("port")) {
                    dbUrl = dbUrl.replaceAll("port", portNameText.getText());
                }
                if (dbUrl.contains("service")) {
                    dbUrl = dbUrl.replaceAll("service", schemaNameText.getText());
                } else if (dbUrl.contains("dsn")) {
                    dbUrl = dbUrl.replaceAll("dsn", schemaNameText.getText());
                } else if (dbUrl.contains("dbpath")) {
                    dbUrl = dbUrl.replaceAll("dbpath", schemaNameText.getText());
                } else {
                    dbUrl = dbUrl.replaceAll("dbname", schemaNameText.getText());
                }
                try {
                    db = new DBConnect(dbUrl, dbDriver, userNameText.getText(), passwordText.getText());
                    db.initConnection();
                    JOptionPane.showMessageDialog(null, "Database Connection is established successfully", "Spring Plugin", JOptionPane.INFORMATION_MESSAGE);
                    tableList.removeAllItems();
                    for (String item : db.getDatabaseTables()) {
                        tableList.addItem(item);
                    }
                    updateTable((String) tableList.getSelectedItem());
                    panel2.setLayout(null);
                    remove(panel1);
                    add(panel2);
                    reloadWindow();
                } catch (Exception ex) {
                    showQuickErrorDialog(ex.getMessage(), ex);                    
                }

            }
        });

    }

    private void initPanel2() {
        panel2 = new JPanel();
        tableSelectLabel = new JLabel("Select the Table");
        tableSelectLabel.setBounds(20, 20, 150, 20);
        basePackageLabel=new JLabel("Enter the Base Package");
        basePackageText=new JTextField();
        basePackageLabel.setBounds(20, 155, 180, 20);
        basePackageText.setBounds(210, 155, 150, 20);
        addToPanel(panel2, basePackageLabel);
        addToPanel(panel2, basePackageText);
        addToPanel(panel2, tableSelectLabel);
        initTableList();
        initScrollableTable();        
        initBackButton();
        initGenerateButton();
    }

    private void initTableList() {

        tableList = new JComboBox();
        tableList.setBounds(180, 20, 150, 20);
        tableList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tableList.getSelectedItem()!=null)
                updateTable((String) tableList.getSelectedItem());
            }
        });
        addToPanel(panel2, tableList);
    }

    private void initBackButton() {
        backButton = new JButton("<<Back");
        backButton.setBounds(600, 20, 100, 20);
        addToPanel(panel2, backButton);
        backButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                remove(panel2);
                add(panel1);
                reloadWindow();
            }
        });
    }
    
    private void initGenerateButton(){
        generateButton = new JButton("Generate");
        generateButton.setBounds(20,400, 100, 20);
        addToPanel(panel2, generateButton);
        generateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            String selectedDirectoryPath=FileSaver.doSave();
            if(selectedDirectoryPath!=null){
            JavaFileCreator.rootDirectoryPath=selectedDirectoryPath;
            ConfigFileCreator.rootDirectoryPath=selectedDirectoryPath;
            try{            
            validateSelection(table2.getModel());            
            }
            catch(Exception ex){            
            JOptionPane.showMessageDialog(null, "Selection Error:" +ex.getMessage(), "Spring Plugin", JOptionPane.INFORMATION_MESSAGE);   
            }
            }
            //System.out.println(getSelectedFields(table1.getModel()));
            }
        });      
        
    }
    
    private static void showQuickErrorDialog(String errorMessage ,Exception e) {
		// create and configure a text area - fill it with exception text.
		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
		textArea.setEditable(false);
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		textArea.setText(errorMessage + "\n" + writer.toString());
		
		// stuff it in a scrollpane with a controlled size.
		JScrollPane scrollPane = new JScrollPane(textArea);		
		scrollPane.setPreferredSize(new Dimension(350, 150));
		
		// pass the scrollpane to the joptionpane.				
		JOptionPane.showMessageDialog(null, scrollPane, "An Error Has Occurred", JOptionPane.ERROR_MESSAGE);
	}  
    
}
