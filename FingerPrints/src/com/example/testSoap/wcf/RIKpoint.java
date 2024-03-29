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

public class RIKpoint extends AttributeContainer implements KvmSerializable
{
    
    public RIKpoint_accessPoints accessPoints=new RIKpoint_accessPoints();
    
    public String x;
    
    public String y;
    
    public String z;

    public RIKpoint ()
    {
    }

    public RIKpoint (AttributeContainer inObj,RIKExtendedSoapSerializationEnvelope envelope)
    {
	    
	    if (inObj == null)
            return;


        SoapObject soapObject=(SoapObject)inObj;  

        if (soapObject.hasProperty("accessPoints"))
        {	
	        java.lang.Object j = soapObject.getProperty("accessPoints");
	        this.accessPoints = new RIKpoint_accessPoints((AttributeContainer)j,envelope);
        }
        if (soapObject.hasProperty("x"))
        {	
	        java.lang.Object obj = soapObject.getProperty("x");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.x = j.toString();
                }
	        }
	        else if (obj!= null){
                this.x = obj.toString();
            }    
        }
        if (soapObject.hasProperty("y"))
        {	
	        java.lang.Object obj = soapObject.getProperty("y");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.y = j.toString();
                }
	        }
	        else if (obj!= null){
                this.y = obj.toString();
            }    
        }
        if (soapObject.hasProperty("z"))
        {	
	        java.lang.Object obj = soapObject.getProperty("z");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.z = j.toString();
                }
	        }
	        else if (obj!= null){
                this.z = obj.toString();
            }    
        }


    }

    @Override
    public java.lang.Object getProperty(int propertyIndex) {
        if(propertyIndex==0)
        {
            return accessPoints;
        }
        if(propertyIndex==1)
        {
            return x;
        }
        if(propertyIndex==2)
        {
            return y;
        }
        if(propertyIndex==3)
        {
            return z;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 4;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.VECTOR_CLASS;
            info.name = "accessPoints";
            info.namespace= "";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "x";
            info.namespace= "";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "y";
            info.namespace= "";
        }
        if(propertyIndex==3)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "z";
            info.namespace= "";
        }
    }
    
    @Override
    public void setProperty(int arg0, java.lang.Object arg1)
    {
    }

}
