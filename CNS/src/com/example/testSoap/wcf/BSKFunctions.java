package com.example.testSoap.wcf;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 3.0.1.8
//
// Created by Quasar Development at 23-04-2014
//
//---------------------------------------------------


public class BSKFunctions
{
    public interface IFunc< Res>
    {
        Res Func() throws java.lang.Exception;
    }

    public interface IFunc1< T,Res>
    {
        Res Func(T p);
    }

    public interface IFunc2< T1,T2,Res>
    {
        Res Func(T1 p1,T2 p2);
    }

    public interface IAction
    {
        void Action();
    }

    public interface IAction1< T>
    {
        void Action(T p1);
    }
}
