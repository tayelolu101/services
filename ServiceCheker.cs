using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Timers;

namespace ServiceChecker
{
    public partial class ServiceCheker : ServiceBase
    {

        private Timer timer = null;
        public ServiceCheker()
        {
            InitializeComponent();
        }

        protected override void OnStart(string[] args)
        {
            Library.WriteToLog("Service started ");
            try
            {
                timer = new Timer();
                timer.Enabled = true;
                timer.Interval = 1 * 60 * 1000;//Every one minute
                timer.Elapsed += new System.Timers.ElapsedEventHandler(timer_Elapsed);
                timer.Start();

            }
            catch (Exception ex)
            {
                //Displays and Logs Message
                Library.WriteErrorLog(ex);
            }
        }


        static void timer_Elapsed(object sender, ElapsedEventArgs e)
        {

            try {

                Library.WriteToLog("The Service was checked " + DateTime.Now.ToString());
                string ServiceName = System.Configuration.ConfigurationManager.AppSettings["ServiceName"];
                string RemoteServiceIP = System.Configuration.ConfigurationManager.AppSettings["RemoteServiceIP"];
                // ServiceController sc = new ServiceController(ServiceName, RemoteServiceIP);
                ServiceController sc = new ServiceController(ServiceName);

                if (sc.Status.Equals(ServiceControllerStatus.Stopped) || sc.Status.Equals(ServiceControllerStatus.StopPending))
                {
                    Library.WriteToLog("Service starting...");
                    sc.Start();
                    Library.WriteToLog("Service started...");
                }

            }catch(Exception ex)
            {
                //Displays and Logs Message
                Library.WriteErrorLog(ex);
            }
        }

        protected override void OnStop()
        {
            timer.Enabled = false;
            Library.WriteToLog("Service stopped ");
        }
    }
}
