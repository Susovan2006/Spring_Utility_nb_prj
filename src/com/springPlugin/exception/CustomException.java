/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Created by Susovan Gumtya 2014
 */
package com.springPlugin.exception;

/**
 *
 * @author Susovan
 */
public class CustomException extends Exception {
    
    String message;
    
  public  CustomException(String msg) {
    message=msg;   
   }
   @Override
   public String getMessage(){
      return message ;       
   }
   
}
