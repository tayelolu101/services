using Sybase.Data.AseClient;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

namespace TradexToSybaseMigrate
{
    public partial class Scheduler : ServiceBase
    {
        private static string ScheduledRunningTime = "4:00 PM";
        private Timer timer = null;
        public Scheduler()
        {
            InitializeComponent();
        }

        protected override void OnStart(string[] args)
        {

            try
            {
                timer = new Timer();
                timer.Enabled = true;              
                timer.Interval = 1 * 60 * 1000;//Every ten minute
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
           
            string _CurrentTime = String.Format("{0:t}", DateTime.Now);
            
            if (_CurrentTime == ScheduledRunningTime)
            {
                try
                {
                    Library.WriteToLog("Service started ");

                    TradexObject [] tradexObject = DataBaseConnection.GetOracleData();
                    AseConnection con = DataBaseConnection.GetSybaseConnection();
                    Library.WriteToLog("Successful Sybase Connection ");
                    Library.WriteToLog("inserting records ");
                    foreach ( TradexObject tdx in tradexObject )
                    {
                        
                        StringBuilder queryString = new StringBuilder().Append(" insert into tableName(column1, column2, column3, column4)")
                                                                        .Append(" values(@psColumn1, @psColumn2, @psColumn3, @psColumn4)");

                        AseCommand command = new AseCommand()
                        {
                            Connection = con,
                            CommandType = CommandType.Text,
                            CommandText = queryString.ToString()
                        };
                       
                        command.Parameters.Add("@psColumn1", AseDbType.VarChar).Value = tdx.Column1;
                        command.Parameters.Add("@psColumn2", AseDbType.VarChar).Value = tdx.Column2;
                        command.Parameters.Add("@psColumn3", AseDbType.VarChar).Value = tdx.Column3;
                        command.Parameters.Add("@psColumn4", AseDbType.VarChar).Value = tdx.Column4;


                        try
                        {
                            if (con.State != ConnectionState.Open)
                                con.Open();

                            int i = command.ExecuteNonQuery();

                            con.Close();
                        }
                        catch (Exception ex)
                        { Library.WriteErrorLog(ex); }
                        finally
                        { con.Close(); }
                    }
                    Library.WriteToLog("Done inserting records ");

                }
                catch(Exception ex)
                {
                    Library.WriteErrorLog(ex);
                }
            }
        }
               

        protected override void OnStop()
        {
            timer.Enabled = false;
            Library.WriteToLog("Service stopped " + DateTime.Now.ToString());
        }
    }
}
