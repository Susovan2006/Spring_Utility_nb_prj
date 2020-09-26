/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Created by Susovan Gumtya 2014
 */
package com.springPlugin.db.bean;

/**
 *
 * @author Susovan
 */
public class TableColoumn {
    
    String name="" ;
    String dataType="";  
    
    

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Field:{" + "Coloumn Name=" + name + ", DataType=" + dataType + '}';
    }
    
    
    
}
