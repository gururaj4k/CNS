

package com.example.testSoap.wcf;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 3.0.1.8
//
// Created by Quasar Development at 23-04-2014
//
//---------------------------------------------------




import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BSKcnsServicePortBinding
{
    interface BSKIWcfMethod
    {
        BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope() throws java.lang.Exception;

        java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope envelope,SoapObject result) throws java.lang.Exception;
    }

    String url="http://54.186.110.123:8080/CNSServer/CNSController";

    int timeOut=60000;
    public List< HeaderProperty> httpHeaders;
    public boolean enableLogging;

    BSKIServiceEvents callback;
    public BSKcnsServicePortBinding(){}

    public BSKcnsServicePortBinding (BSKIServiceEvents callback)
    {
        this.callback = callback;
    }
    public BSKcnsServicePortBinding(BSKIServiceEvents callback,String url)
    {
        this.callback = callback;
        this.url = url;
    }

    public BSKcnsServicePortBinding(BSKIServiceEvents callback,String url,int timeOut)
    {
        this.callback = callback;
        this.url = url;
        this.timeOut=timeOut;
    }

    protected org.ksoap2.transport.Transport createTransport()
    {
        return new HttpTransportSE(url,timeOut);
    }
    
    protected BSKExtendedSoapSerializationEnvelope createEnvelope()
    {
        return new BSKExtendedSoapSerializationEnvelope();
    }
    
    protected void sendRequest(String methodName,BSKExtendedSoapSerializationEnvelope envelope,org.ksoap2.transport.Transport transport) throws java.lang.Exception
    {
        transport.call(methodName, envelope,httpHeaders);
    }


    java.lang.Object getResult(java.lang.Class destObj,SoapObject source,String resultName,BSKExtendedSoapSerializationEnvelope __envelope) throws java.lang.Exception
    {
        if (source.hasProperty(resultName))
        {
            java.lang.Object j=source.getProperty(resultName);
            if(j==null)
            {
                return null;
            }
            java.lang.Object instance=__envelope.get(j,destObj);
            return instance;
        }
        else if( source.getName().equals(resultName)) {
            java.lang.Object instance=__envelope.get(source,destObj);
            return instance;
        }
        return null;
    }
    
    public BSKgetPreCollectedPrintsResponse getPreCollectedPrints( ) throws java.lang.Exception
    {
        return (BSKgetPreCollectedPrintsResponse)execute(new BSKIWcfMethod()
        {
            @Override
            public BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
                BSKExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                SoapObject __soapReq = new SoapObject("http://services.cns.com/", "getPreCollectedPrints");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope __envelope,SoapObject __result)throws java.lang.Exception {
                return (BSKgetPreCollectedPrintsResponse)getResult(BSKgetPreCollectedPrintsResponse.class,__result,"getPreCollectedPrintsResponse",__envelope);
            }
        },"");
    }
    
    public android.os.AsyncTask getPreCollectedPrintsAsync()
    {
        return executeAsync(new BSKFunctions.IFunc< BSKgetPreCollectedPrintsResponse>() {
            public BSKgetPreCollectedPrintsResponse Func() throws java.lang.Exception {
                return getPreCollectedPrints( );
            }
        });
    }
    
    public BSKgetDirectionsTestResponse getDirectionsTest(final BSKpoint arg0,final String arg1 ) throws java.lang.Exception
    {
        return (BSKgetDirectionsTestResponse)execute(new BSKIWcfMethod()
        {
            @Override
            public BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
                BSKExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                __envelope.addMapping("","arg0",new BSKpoint().getClass());
                SoapObject __soapReq = new SoapObject("http://services.cns.com/", "getDirectionsTest");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg0";
                __info.type=BSKpoint.class;
                __info.setValue(arg0);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg1";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(arg1);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope __envelope,SoapObject __result)throws java.lang.Exception {
                return (BSKgetDirectionsTestResponse)getResult(BSKgetDirectionsTestResponse.class,__result,"getDirectionsTestResponse",__envelope);
            }
        },"");
    }
    
    public android.os.AsyncTask getDirectionsTestAsync(final BSKpoint arg0,final String arg1)
    {
        return executeAsync(new BSKFunctions.IFunc< BSKgetDirectionsTestResponse>() {
            public BSKgetDirectionsTestResponse Func() throws java.lang.Exception {
                return getDirectionsTest( arg0,arg1);
            }
        });
    }
    
    public void delete2(final Integer arg0 ) throws java.lang.Exception
    {
        execute(new BSKIWcfMethod()
        {
            @Override
            public BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
                BSKExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                SoapObject __soapReq = new SoapObject("http://services.cns.com/", "delete2");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg0";
                __info.type=PropertyInfo.INTEGER_CLASS;
                __info.setValue(arg0);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope __envelope,SoapObject __result)throws java.lang.Exception {
                return null;
            }
        },"");
    }
    
    public android.os.AsyncTask delete2Async(final Integer arg0)
    {
        return executeAsync(new BSKFunctions.IFunc< Void>()
        {
            @Override
            public Void Func() throws java.lang.Exception {
                delete2( arg0);
                return null;
            }
        }) ;
    }
    
    public BSKgetDirectionsResponse getDirections(final BSKpoint arg0,final String arg1 ) throws java.lang.Exception
    {
        return (BSKgetDirectionsResponse)execute(new BSKIWcfMethod()
        {
            @Override
            public BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
                BSKExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                __envelope.addMapping("","arg0",new BSKpoint().getClass());
                SoapObject __soapReq = new SoapObject("http://services.cns.com/", "getDirections");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg0";
                __info.type=BSKpoint.class;
                __info.setValue(arg0);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg1";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(arg1);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope __envelope,SoapObject __result)throws java.lang.Exception {
                return (BSKgetDirectionsResponse)getResult(BSKgetDirectionsResponse.class,__result,"getDirectionsResponse",__envelope);
            }
        },"");
    }
    
    public android.os.AsyncTask getDirectionsAsync(final BSKpoint arg0,final String arg1)
    {
        return executeAsync(new BSKFunctions.IFunc< BSKgetDirectionsResponse>() {
            public BSKgetDirectionsResponse Func() throws java.lang.Exception {
                return getDirections( arg0,arg1);
            }
        });
    }
    
    public BSKgetDirectionsTest22Response getDirectionsTest22(final BSKpoint arg0,final String arg1 ) throws java.lang.Exception
    {
        return (BSKgetDirectionsTest22Response)execute(new BSKIWcfMethod()
        {
            @Override
            public BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
                BSKExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                __envelope.addMapping("","arg0",new BSKpoint().getClass());
                SoapObject __soapReq = new SoapObject("http://services.cns.com/", "getDirectionsTest22");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg0";
                __info.type=BSKpoint.class;
                __info.setValue(arg0);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="";
                __info.name="arg1";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(arg1);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope __envelope,SoapObject __result)throws java.lang.Exception {
                return (BSKgetDirectionsTest22Response)getResult(BSKgetDirectionsTest22Response.class,__result,"getDirectionsTest22Response",__envelope);
            }
        },"");
    }
    
    public android.os.AsyncTask getDirectionsTest22Async(final BSKpoint arg0,final String arg1)
    {
        return executeAsync(new BSKFunctions.IFunc< BSKgetDirectionsTest22Response>() {
            public BSKgetDirectionsTest22Response Func() throws java.lang.Exception {
                return getDirectionsTest22( arg0,arg1);
            }
        });
    }
    
    public BSKgetPartitionsWithDirectionsResponse getPartitionsWithDirections( ) throws java.lang.Exception
    {
        return (BSKgetPartitionsWithDirectionsResponse)execute(new BSKIWcfMethod()
        {
            @Override
            public BSKExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
                BSKExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                SoapObject __soapReq = new SoapObject("http://services.cns.com/", "getPartitionsWithDirections");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(BSKExtendedSoapSerializationEnvelope __envelope,SoapObject __result)throws java.lang.Exception {
                return (BSKgetPartitionsWithDirectionsResponse)getResult(BSKgetPartitionsWithDirectionsResponse.class,__result,"getPartitionsWithDirectionsResponse",__envelope);
            }
        },"");
    }
    
    public android.os.AsyncTask getPartitionsWithDirectionsAsync()
    {
        return executeAsync(new BSKFunctions.IFunc< BSKgetPartitionsWithDirectionsResponse>() {
            public BSKgetPartitionsWithDirectionsResponse Func() throws java.lang.Exception {
                return getPartitionsWithDirections( );
            }
        });
    }
    
    public String getPreCollectedTest( ) throws java.lang.Exception
    {
/*This feature is available in Premium account, Check http://EasyWsdl.com/Payment/PremiumAccountDetails to see all benefits of Premium account*/
        return null;    
    }
    
    public android.os.AsyncTask getPreCollectedTestAsync()
    {
        return executeAsync(new BSKFunctions.IFunc< String>() {
            public String Func() throws java.lang.Exception {
                return getPreCollectedTest( );
            }
        });
    }
    
    public void delete3(final String arg0 ) throws java.lang.Exception
    {
/*This feature is available in Premium account, Check http://EasyWsdl.com/Payment/PremiumAccountDetails to see all benefits of Premium account*/
    }
    
    public android.os.AsyncTask delete3Async(final String arg0)
    {
        return executeAsync(new BSKFunctions.IFunc< Void>()
        {
            @Override
            public Void Func() throws java.lang.Exception {
                delete3( arg0);
                return null;
            }
        }) ;
    }
    
    public String getPartitionsWithDirectionsTest( ) throws java.lang.Exception
    {
/*This feature is available in Premium account, Check http://EasyWsdl.com/Payment/PremiumAccountDetails to see all benefits of Premium account*/
        return null;    
    }
    
    public android.os.AsyncTask getPartitionsWithDirectionsTestAsync()
    {
        return executeAsync(new BSKFunctions.IFunc< String>() {
            public String Func() throws java.lang.Exception {
                return getPartitionsWithDirectionsTest( );
            }
        });
    }
    
    public void save(final String arg0 ) throws java.lang.Exception
    {
/*This feature is available in Premium account, Check http://EasyWsdl.com/Payment/PremiumAccountDetails to see all benefits of Premium account*/
    }
    
    public android.os.AsyncTask saveAsync(final String arg0)
    {
        return executeAsync(new BSKFunctions.IFunc< Void>()
        {
            @Override
            public Void Func() throws java.lang.Exception {
                save( arg0);
                return null;
            }
        }) ;
    }
    
    public void delete(final String arg0 ) throws java.lang.Exception
    {
/*This feature is available in Premium account, Check http://EasyWsdl.com/Payment/PremiumAccountDetails to see all benefits of Premium account*/
    }
    
    public android.os.AsyncTask deleteAsync(final String arg0)
    {
        return executeAsync(new BSKFunctions.IFunc< Void>()
        {
            @Override
            public Void Func() throws java.lang.Exception {
                delete( arg0);
                return null;
            }
        }) ;
    }
    protected java.lang.Object execute(BSKIWcfMethod wcfMethod,String methodName) throws java.lang.Exception
    {
        org.ksoap2.transport.Transport __httpTransport=createTransport();
        __httpTransport.debug=enableLogging;
        BSKExtendedSoapSerializationEnvelope __envelope=wcfMethod.CreateSoapEnvelope();
        try
        {
            sendRequest(methodName, __envelope, __httpTransport);
        }
        finally {
            if (__httpTransport.debug) {
                if (__httpTransport.requestDump != null) {
                    android.util.Log.i("requestDump",__httpTransport.requestDump);
                }
                if (__httpTransport.responseDump != null) {
                    android.util.Log.i("responseDump",__httpTransport.responseDump);
                }
            }
        }
        java.lang.Object __retObj = __envelope.bodyIn;
        if (__retObj instanceof org.ksoap2.SoapFault){
            org.ksoap2.SoapFault __fault = (org.ksoap2.SoapFault)__retObj;
            throw convertToException(__fault,__envelope);
        }else{
            SoapObject __result=(SoapObject)__retObj;
            return wcfMethod.ProcessResult(__envelope,__result);
        }
    }
    protected < T> android.os.AsyncTask  executeAsync(final BSKFunctions.IFunc< T> func)
    {
        return new android.os.AsyncTask< Void, Void, BSKOperationResult< T>>()
        {
            @Override
            protected void onPreExecute() {
                callback.Starting();
            };
            @Override
            protected BSKOperationResult< T> doInBackground(Void... params) {
                BSKOperationResult< T> result = new BSKOperationResult< T>();
                try
                {
                    result.Result= func.Func();
                }
                catch(java.lang.Exception ex)
                {
                    ex.printStackTrace();
                    result.Exception=ex;
                }
                return result;
            }
            
            @Override
            protected void onPostExecute(BSKOperationResult< T> result)
            {
                callback.Completed(result);
            }
        }.execute();
    }
        
   
      

    java.lang.Exception convertToException(org.ksoap2.SoapFault fault,BSKExtendedSoapSerializationEnvelope envelope)
    {
        return new java.lang.Exception(fault.faultstring);
    }
}


