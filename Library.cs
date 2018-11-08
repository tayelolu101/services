using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace ServiceChecker
{
    public static class Library
    {

        public static void WriteErrorLog(Exception ex)
        {
            StreamWriter sw = null;
            try
            {
                sw = new StreamWriter(AppDomain.CurrentDomain.BaseDirectory + "\\LogFile.txt", true);
                sw.WriteLine(DateTime.Now.ToString() + " : " + ex.Source.ToString().Trim() + ": " + ex.Message.ToString().Trim());
                sw.Flush();
                sw.Close();
            }
            catch
            {

            }
        }

        public static void WriteToLog(string message)
        {
            StreamWriter swt = null;
            try
            {
                swt = new StreamWriter(AppDomain.CurrentDomain.BaseDirectory + "\\LogFile.txt", true);
                swt.WriteLine(message + " : at " + DateTime.Now.ToString());
                swt.Flush();
                swt.Close();
            }
            catch
            {



            }
        }
    }
}
