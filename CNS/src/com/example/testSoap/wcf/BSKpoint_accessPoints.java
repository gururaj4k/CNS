package com.example.testSoap.wcf;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 3.0.1.8
//
// Created by Quasar Development at 23-04-2014
//
//---------------------------------------------------



import org.ksoap2.serialization.*;
import java.util.Vector;
import java.util.Hashtable;


public class BSKpoint_accessPoints extends Vector< BSKpoint_accessPoints_entry> implements KvmSerializable
{
    
    public BSKpoint_accessPoints(){}
    
    public BSKpoint_accessPoints(AttributeContainer inObj,BSKExtendedSoapSerializationEnvelope envelope)
    {
        if (inObj == null)
            return;
            SoapObject soapObject=(SoapObject)inObj;
            int size = soapObject.getPropertyCount();
            for (int i0=0;i0< size;i0++)
            {
                java.lang.Object obj = soapObject.getProperty(i0);
                if (obj!=null && obj instanceof AttributeContainer)
                {
                    AttributeContainer j =(AttributeContainer) soapObject.getProperty(i0);
                    BSKpoint_accessPoints_entry j1= (BSKpoint_accessPoints_entry)envelope.get(j,BSKpoint_accessPoints_entry.class);
                    add(j1);
                }
            }
        
    }
    
    @Override
    public java.lang.Object getProperty(int arg0) {
        return this.get(arg0);
    }
    
    @Override
    public int getPropertyCount() {
        return this.size();
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        info.name = "entry";
        info.type = BSKpoint_accessPoints_entry.class;
    	info.namespace= "";
    }
    
    @Override
    public void setProperty(int arg0, java.lang.Object arg1) {
    }

}