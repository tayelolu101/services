using Oracle.DataAccess.Client;
using Sybase.Data.AseClient;
using System;
using System.Collections.Generic;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TradexToSybaseMigrate
{
    class DataBaseConnection
    {

        private static OracleConnection conn = null;
        private static AseConnection connAse = null;
     

        public static OracleConnection GetOracleConnection(string host, int port, String sid, String user, String password)
        {
            
            // Connection string' to connect directly to Oracle.
            string connString = "Data Source=(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = "
                 + host + ")(PORT = " + port + "))(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = "
                 + sid + ")));Password=" + password + ";User ID=" + user;

            conn = new OracleConnection();

            conn.ConnectionString = connString;
                       
            return conn; 
        }


        public static OracleConnection GetOracleConnection2()
        {

             conn = new OracleConnection();          
             conn.ConnectionString = ConfigurationManager.ConnectionStrings["Oracleconnection"].ConnectionString;

            return conn;
        }

        public static TradexObject[] GetOracleData()
        {
            TradexObject tradex = null;
            TradexObject [] tradexs = null;
            List <TradexObject> tradexlist = new List<TradexObject>();
            string cmdText = "SELECT * from table_name WHERE status = 'PROCESSED'";

            Library.WriteToLog("Getting Oracle Connection ::: ");

            try
            {
                //using (conn = DataBaseConnection.GetOracleConnection("172.29.15.30", 1521, "TRADEX", "tradex", ""))
                using (conn = DataBaseConnection.GetOracleConnection2())
                    Library.WriteToLog("Successful Oracle Connection ");

            using (OracleCommand cmd = new OracleCommand(cmdText, conn))
            {
                   // cmd.Connection = conn;
                   // cmd.CommandType = CommandType.Text;
                   // cmd.CommandText = cmdText;  
                    if(conn.State != ConnectionState.Open)
                    { conn.Open(); }                 
                    
                using (OracleDataReader odr = cmd.ExecuteReader())
                {
                    while (odr.Read())
                    {
                            tradex = new TradexObject();
                            tradex.Column1 = odr[""].ToString();
                            tradex.Column2 = odr[""].ToString();
                            tradex.Column3 = odr[""].ToString();
                            tradex.Column4 = odr[""].ToString();
                            tradexlist.Add(tradex);
                   }

                        tradexs = (TradexObject [])tradexlist.ToArray();
                }
                    Library.WriteToLog("Done retrieving records ");
                    conn.Close();
                }
                        
            }
            catch (Exception ex)
            {
               // conn.Close();
                Library.WriteErrorLog(ex);
            }
            finally { conn.Close(); }
            
            return tradexs;
        }

        public static AseConnection GetSybaseConnection()
        {
            try {
                Library.WriteToLog("Getting Sybase Connection ::: ");
                              string connectionString = ConfigurationManager.ConnectionStrings["Sybaseconnection"].ConnectionString;
                connAse = new AseConnection(connectionString);
                connAse.Open();
                
            }
            catch (Exception ex)
            {
                connAse.Close();
                Library.WriteErrorLog(ex);
            }
            return connAse;
        }

        
    }
}
