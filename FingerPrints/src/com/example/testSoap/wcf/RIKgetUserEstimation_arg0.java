package com.example.testSoap.wcf;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 3.0.1.5
//
// Created by Quasar Development at 14-04-2014
//
//---------------------------------------------------



import org.ksoap2.serialization.*;
import java.util.Vector;
import java.util.Hashtable;


public class RIKgetUserEstimation_arg0 extends Vector< RIKgetUserEstimation_arg0_entry> implements KvmSerializable
{
    
    public RIKgetUserEstimation_arg0(){}
    
    public RIKgetUserEstimation_arg0(AttributeContainer inObj,RIKExtendedSoapSerializationEnvelope envelope)
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
                    RIKgetUserEstimation_arg0_entry j1= (RIKgetUserEstimation_arg0_entry)envelope.get(j,RIKgetUserEstimation_arg0_entry.class);
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
        info.type = RIKgetUserEstimation_arg0_entry.class;
    	info.namespace= "";
    }
    
    @Override
    public void setProperty(int arg0, java.lang.Object arg1) {
    }

}