package com.example.testSoap.wcf;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 3.0.1.5
//
// Created by Quasar Development at 14-04-2014
//
//---------------------------------------------------


import java.util.Hashtable;
import org.ksoap2.serialization.*;
import java.util.ArrayList;
import org.ksoap2.serialization.PropertyInfo;

public class RIKsaveWifiFingerPrints extends AttributeContainer implements KvmSerializable
{
    
    public ArrayList< RIKwifiAccessPoint>arg0 ;
    
    public String arg1;
    
    public String arg2;
    
    public String arg3;
    
    public String arg4;

    public RIKsaveWifiFingerPrints ()
    {
    }

    public RIKsaveWifiFingerPrints (AttributeContainer inObj,RIKExtendedSoapSerializationEnvelope envelope)
    {
	    
	    if (inObj == null)
            return;


        SoapObject soapObject=(SoapObject)inObj;  

        if (soapObject.hasProperty("arg0"))
        {	
	        int size = soapObject.getPropertyCount();
	        this.arg0 = new ArrayList<RIKwifiAccessPoint>();
	        for (int i0=0;i0< size;i0++)
	        {
	            PropertyInfo info=new PropertyInfo();
	            soapObject.getPropertyInfo(i0, info);
                java.lang.Object obj = info.getValue();
	            if (obj!=null && info.name.equals("arg0"))
	            {
                    java.lang.Object j =info.getValue();
	                RIKwifiAccessPoint j1= (RIKwifiAccessPoint)envelope.get(j,RIKwifiAccessPoint.class);
	                this.arg0.add(j1);
	            }
	        }
        }
        if (soapObject.hasProperty("arg1"))
        {	
	        java.lang.Object obj = soapObject.getProperty("arg1");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.arg1 = j.toString();
                }
	        }
	        else if (obj!= null){
                this.arg1 = obj.toString();
            }    
        }
        if (soapObject.hasProperty("arg2"))
        {	
	        java.lang.Object obj = soapObject.getProperty("arg2");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.arg2 = j.toString();
                }
	        }
	        else if (obj!= null){
                this.arg2 = obj.toString();
            }    
        }
        if (soapObject.hasProperty("arg3"))
        {	
	        java.lang.Object obj = soapObject.getProperty("arg3");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.arg3 = j.toString();
                }
	        }
	        else if (obj!= null){
                this.arg3 = obj.toString();
            }    
        }
        if (soapObject.hasProperty("arg4"))
        {	
	        java.lang.Object obj = soapObject.getProperty("arg4");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.arg4 = j.toString();
                }
	        }
	        else if (obj!= null){
                this.arg4 = obj.toString();
            }    
        }


    }

    @Override
    public java.lang.Object getProperty(int propertyIndex) {
        if(propertyIndex==0)
        {
            return arg1;
        }
        if(propertyIndex==1)
        {
            return arg2;
        }
        if(propertyIndex==2)
        {
            return arg3;
        }
        if(propertyIndex==3)
        {
            return arg4;
        }
        if(propertyIndex>=+4 && propertyIndex< + 4+this.arg0.size())
        {
            return arg0.get(propertyIndex-(+4));
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 4+arg0.size();
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "arg1";
            info.namespace= "";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "arg2";
            info.namespace= "";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "arg3";
            info.namespace= "";
        }
        if(propertyIndex==3)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "arg4";
            info.namespace= "";
        }
        if(propertyIndex>=+4 && propertyIndex <= +4+this.arg0.size())
        {
            info.type = RIKwifiAccessPoint.class;
            info.name = "arg0";
            info.namespace= "";
        }
    }
    
    @Override
    public void setProperty(int arg0, java.lang.Object arg1)
    {
    }

}
