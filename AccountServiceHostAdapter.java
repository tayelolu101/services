package com.zenithbank.banking.AccountSummary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenithbank.banking.ibank.account.AccountStatementsItem;
import com.zenithbank.banking.ibank.account.RequestValue;
import com.zenithbank.stringhelper.JSONArray;
import com.zenithbank.stringhelper.JSONObject;

public class AccountServiceHostAdapter {

	private static String GetAccountDetail = null;
	private static String GetConsolidatedAccountStatements = null;	
	private static String GetDailyActivityStatements= null;
	private static String GetMiniAccountStatement  =null;
	private static String BaseUrl = null;
	private static String DebitAccountSummaryCP = null;
	private static String VendorPaymentDebitAccountNoUrl = null;
	private static String GetAccountStatementsArch = null;
	private static String GetCustomerDetail = null;
	private static String X_CALLER_ID = null;
	private static String X_CALLER_NAME = null;
	private static String X_CALLER_PASSWORD = null;
	private static String X_CALLER_SESSION_ID = null;
	private static boolean IS_DEV;
	private static String CERT_1 = null;
	private static String CERT_2 = null;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
	private SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
	
	

	static {
		Properties ps = new Properties();
	
		try {
			ps.load(AccountServiceHostAdapter.class.getResourceAsStream("Account.properties"));
			IS_DEV = ps.getProperty("accountservice.dev.mode").contains("true");
			GetConsolidatedAccountStatements = ps.getProperty("accountservices.getConsolidatedAccountStatements.uri");
			GetDailyActivityStatements = ps.getProperty("accountservices.getDailyActivityStatements.uri");
			GetAccountStatementsArch = ps.getProperty("accountservices.getAccountStatementsArch.uri");
			DebitAccountSummaryCP = ps.getProperty("accountservices.getDebitAccountSummaryCP.uri");
			VendorPaymentDebitAccountNoUrl = ps.getProperty("accountservices.getVendorPaymentDebitAccountNo.uri");
			GetMiniAccountStatement = ps.getProperty("accountservices.getMiniAccountStatement.uri");
			GetCustomerDetail = ps.getProperty("accountservices.getCustomerDetail.uri");
			GetDailyActivityStatements = ps.getProperty("accountservices.getDailyActivityStatements.uri");
			BaseUrl = ps.getProperty("zencore.baseurl");
			GetAccountDetail = ps.getProperty("accountservices.getAccountDetail.uri");
			CERT_2 = ps.getProperty("accountservices.cert.2");
			CERT_1 = ps.getProperty("accountservices.cert.1");
			X_CALLER_ID = ps.getProperty("zencore.caller.id");
			X_CALLER_NAME = ps.getProperty("zencore.caller.name");
			X_CALLER_PASSWORD = ps.getProperty("zencore.caller.password");
			X_CALLER_SESSION_ID = ps.getProperty("zencore.cust.session.id");
		} catch (FileNotFoundException ex) {
			Logger.getLogger(AccountServiceHostAdapter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(AccountServiceHostAdapter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	
	private HttpURLConnection getHttpConnectionWithHeader(String apiUrl) throws IOException {
		URL url = new URL(apiUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("X-CallerID", X_CALLER_ID);
		con.setRequestProperty("X-CallerName", X_CALLER_NAME);
		con.setRequestProperty("X-CallerPassword", X_CALLER_PASSWORD);
		con.setRequestProperty("X-CUST-SESSIONID", X_CALLER_SESSION_ID);
		con.setRequestProperty("Content-Type", "application/json");

		return con;

	}
	
	public AccountSummaryValue[] getDebitAccountSummaryCP(String companycode,String accountNumber){
		
		AccountSummaryValue[] accountsummaryvalues = new  AccountSummaryValue[0];
		AccountSummaryValue account = null;
         
         if (IS_DEV) {
 			System.setProperty(CERT_1, CERT_2);
 		}
 		List<AccountSummaryValue> listValue = new ArrayList<AccountSummaryValue>();

 		URL url = null;
 		
 		try {
 			
 			String API_URL = BaseUrl+DebitAccountSummaryCP;

 		API_URL = API_URL +"companyCode="+companycode+"&accountNumber="+accountNumber;
 		    
 				
 									
 			 System.out.println("API URL FOR RETRIEVAL " + API_URL);
 			 
 			url = new URL(API_URL);
 			
 			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

 			con.setRequestMethod("GET");

 			int responseCode = con.getResponseCode();

 			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
 			String inputLine;

 			StringBuilder inputBuffer = new StringBuilder();
 			
 			if (responseCode == HttpURLConnection.HTTP_OK) {

 				while ((inputLine = in.readLine()) != null) {

 					inputBuffer.append(inputLine);
 				}

 				JSONObject json = new JSONObject(inputBuffer.toString());

 				JSONArray Accounts = (JSONArray) json.get("data");
 				

 				for (int i = 0; i < Accounts.length(); i++) {
 				    account = new AccountSummaryValue();
 					JSONObject accountObject = Accounts.getJSONObject(i);
 					account.setNoOfLeafs  (accountObject.optString("AccountNo"));	
                    account.setApplicationType(accountObject.optString("appl_type"));
                    account.setAcctType(accountObject.optString("acct_type"));                   
                    account.setAcctNo(accountObject.optString("acct_no"));
                    account.setSec_acct_id(accountObject.optInt("sd_acct_id"));
                    account.setAcctDesc(accountObject.optString("acct_desc"));
                    account.setIso_currency(accountObject.optString("iso_currency"));
                    account.setCurrentBalance(accountObject.optDouble("cur_bal"));
                    account.setAcctAvailable(accountObject.optDouble("acct_avail"));
                    account.setBranchNumber(accountObject.optInt("branch_no"));
                    account.setClassCode(accountObject.optInt("class_code"));
                    account.setRsm_name(accountObject.optString("rsm_name"));
                    account.setRsm_id(accountObject.optInt("rsm_id"));
 					listValue.add(account);
 				}
 				 accountsummaryvalues = new AccountSummaryValue[listValue.size()];
 				 accountsummaryvalues = (AccountSummaryValue[]) listValue.toArray(accountsummaryvalues);

 			}

 			in.close();

 		} catch (Exception e) {
 			
 			e.printStackTrace();
 		}
     
         return accountsummaryvalues;
	}
	
	public AccountDetailsValue getAccountDetail(String accountNumber){
		
		AccountDetailsValue accountdetailsValue = new AccountDetailsValue();
		
		if (IS_DEV) {
			System.setProperty(CERT_1, CERT_2);
		}
		
		URL url = null;
		
		try {
			
			String API_URL = BaseUrl+GetAccountDetail+"accountNumber="+accountNumber;

			 System.out.println("API URL FOR RETRIEVAL " + API_URL);
			url = new URL(API_URL);
			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
		
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			StringBuilder inputBuffer = new StringBuilder();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {

				while ((inputLine = in.readLine()) != null) {

					inputBuffer.append(inputLine);
				}
				
				JSONObject json = new JSONObject(inputBuffer.toString());
				
				JSONObject account = (JSONObject) json.get("data");
				
				 accountdetailsValue.setCurrentBalance(account.optDouble("CurrentBalance"));
				 accountdetailsValue.setAvailableBalance(account.optDouble("AvailableBalance"));
                 accountdetailsValue.setAvailable_loc_balance(account.optDouble("AvailableLocBalance"));
                 accountdetailsValue.setCurrent_begin_Balance(account.optDouble("CurrentBalanceBegin"));
                // accountdetailsValue.setOriginalAmount(account.optDouble(""));
                 Date LastDepositDate = formatter.parse(account.optString("LastDepositDate"));
                 accountdetailsValue.setLast_deposit_date(LastDepositDate);
                 accountdetailsValue.setLast_dep_Amount(account.optDouble("LastDepositAmount"));
                 Date LastWithdrawalDate = formatter.parse(account.optString("LastWithdrawalDate"));
                 accountdetailsValue.setLast_wd_date(LastWithdrawalDate);
               //  accountdetailsValue.setLast_wd_amount(account.optDouble(""));
                // accountdetailsValue.setIssueDate(rs.getDate("issue_dt"));
                // accountdetailsValue.setMaturityDate(rs.getDate("mat_dt"));
               //  accountdetailsValue.setInt_bearing_Credit(account.optInt("iba_cr"));
               //  accountdetailsValue.setNsf_items_Pending(account.optString(""));
                 accountdetailsValue.setCustomerType(account.optString("CustomerType"));
                 accountdetailsValue.setPassbook(account.optString("Passbook"));
               //  accountdetailsValue.setPassbookBalance(account.optDouble(""));
                // accountdetailsValue.setPassbookCredit(account.optDouble(""));
               //  accountdetailsValue.setPassbookDebit(account.optDouble(""));
                // accountdetailsValue.setPassbook_discrepancy_date(rs.getDate("pb_discrepancy_date"));
                 accountdetailsValue.setRsm_name(account.optString("RSMName"));
                 accountdetailsValue.setRsm_ID(account.optInt("RSMID"));
               //  accountdetailsValue.setIso_currency(account.optString(""));
              //   accountdetailsValue.setterm_of_account(account.optInt("iba_cr"));
               //  accountdetailsValue.setPeriod_cert(account.optString(""));
               //  accountdetailsValue.setStatus_of_account(account.optString(""));
               //  accountdetailsValue.setNet_memo(account.optDouble(""));
               //  accountdetailsValue.setAccount_available(account.optDouble(""));
               //  accountdetailsValue.setUcf_amount(account.optDouble(""));
               //  accountdetailsValue.setCurrent_rate(account.optDouble(""));
               //  accountdetailsValue.setInterest_year_to_date(account.optDouble(""));
               //  accountdetailsValue.setInterest_paid_last_yr(account.optDouble(""));
			}
			}catch (Exception e) {
				
				e.printStackTrace();
			}		
		return accountdetailsValue;
	}
	
	 public AccountSummaryValue[] getVendorPaymentDebitAccountNo(String companycode,String accountNumber, Integer rim_no,Integer roleid) {
		 
		 AccountSummaryValue[] accountsummaryvalues = new  AccountSummaryValue[0];
		 AccountSummaryValue account = null;
       
	 
	 if (IS_DEV) {
		 
			System.setProperty(CERT_1, CERT_2);
		}
		List<AccountSummaryValue> listValue = new ArrayList<AccountSummaryValue>();

		URL url = null;
		
		try {
			
			String API_URL = BaseUrl+VendorPaymentDebitAccountNoUrl;

			if((rim_no == null || rim_no.equals("")) && (roleid == null || roleid.equals(""))){								
					
					API_URL = API_URL +"companyCode="+companycode+"&accountNumber="+accountNumber;
					
		    }else if((rim_no == null || rim_no.equals("")) && ((!roleid.equals("")) || roleid != null)){
					
					API_URL = API_URL +"companyCode="+companycode+"&accountNumber="+accountNumber+"&roleId="+roleid;
					
		    }
		    else if((roleid == null || roleid.equals("")) && ((!rim_no.equals("")) || rim_no != null)){
				
		    	API_URL = API_URL +"companyCode="+companycode+"&accountNumber="+accountNumber+"&rimNo="+rim_no;
	   
		    }else{
		    	
				API_URL = API_URL +"companyCode="+companycode+"&accountNumber="+accountNumber+"&rimNo="+rim_no+"&roleId="+roleid;
		    }
				
									
			 System.out.println("API URL FOR RETRIEVAL " + API_URL);
			 
			url = new URL(API_URL);
			
			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			StringBuilder inputBuffer = new StringBuilder();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {

				while ((inputLine = in.readLine()) != null) {

					inputBuffer.append(inputLine);
				}

				JSONObject json = new JSONObject(inputBuffer.toString());

				JSONArray Accounts = (JSONArray) json.get("data");
				

				for (int i = 0; i < Accounts.length(); i++) {
				    account = new AccountSummaryValue();
					JSONObject accountObject = Accounts.getJSONObject(i);
					account.setNoOfLeafs  (accountObject.optString("AccountNo"));				
					listValue.add(account);
				}
				 accountsummaryvalues = new AccountSummaryValue[listValue.size()];
				 accountsummaryvalues = (AccountSummaryValue[]) listValue.toArray(accountsummaryvalues);

			}

			in.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		  return accountsummaryvalues;
	 }

	 
		public AccountSummaryValue getCustomerDetail(String accountNumber) {
			
			AccountSummaryValue accountPojo = new AccountSummaryValue();
			
			if (IS_DEV) {
				System.setProperty(CERT_1, CERT_2);
			}
			
			URL url = null;
			
			try {
				
				String API_URL = BaseUrl+GetCustomerDetail;

				API_URL = API_URL.replace("{accountNumber}", accountNumber);
				 System.out.println("API URL FOR RETRIEVAL " + API_URL);
				url = new URL(API_URL);
				HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

				con.setRequestMethod("GET");

				int responseCode = con.getResponseCode();
			
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				StringBuilder inputBuffer = new StringBuilder();
				
				if (responseCode == HttpURLConnection.HTTP_OK) {

					while ((inputLine = in.readLine()) != null) {

						inputBuffer.append(inputLine);
					}
					ObjectMapper objectMapper = new ObjectMapper();
					//accountPojo = objectMapper.readValue(inputBuffer.toString(), AccountSummaryValue.class);
					  System.out.println(inputBuffer.toString());
						JSONObject json = new JSONObject(inputBuffer.toString());

						JSONObject account = (JSONObject) json.get("data");

						accountPojo.setBranchNumber(account.optInt("BranchNumber"));
						accountPojo.setRsm_id(account.optInt("RsmID"));
						accountPojo.setClassCode(account.optInt("ClassCode"));
						accountPojo.setAcctNo(account.optString("acct_no"));
						accountPojo.setAcctType(account.optString("AccountType"));
						accountPojo.setBeneficiaryName(account.optString("Name"));
						accountPojo.setCurrentBalance(account.optDouble("AvailBalance"));
				}

				in.close();

			} catch (Exception e) {
				
				e.printStackTrace();
			}

			return accountPojo;

		}
		
		
		  public AccountStatementsItem[] getAccountMiniStatementsItem(String accountNumber) {
			  AccountStatementsItem[] accountstatementitems = new  AccountStatementsItem[0];
			  AccountStatementsItem accountstatementitem = null;
	          ArrayList<AccountStatementsItem> listValue = new ArrayList<AccountStatementsItem>();
	           
	           
	           
	           if (IS_DEV) {
	   			System.setProperty(CERT_1, CERT_2);
	   		}

	   		URL url = null;
	   		
	   		try {
	   			
	   			String API_URL = BaseUrl+GetMiniAccountStatement;

	   		   API_URL = API_URL +"accountNumber="+accountNumber;
	   		    	   				
	   									
	   			 System.out.println("API URL FOR RETRIEVAL " + API_URL);
	   			 
	   			url = new URL(API_URL);
	   			
	   			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

	   			con.setRequestMethod("POST");

	   			int responseCode = con.getResponseCode();

	   			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	   			String inputLine;

	   			StringBuilder inputBuffer = new StringBuilder();
	   			
	   			if (responseCode == HttpURLConnection.HTTP_OK) {

	   				while ((inputLine = in.readLine()) != null) {

	   					inputBuffer.append(inputLine);
	   				}
	           
	   				JSONObject json = new JSONObject(inputBuffer.toString());
	   				
	   				JSONObject jsons = (JSONObject) json.get("data");

	 				JSONArray statementItem = (JSONArray) jsons.get("Statements");
	        
	 				for (int i = 0; i < statementItem.length(); i++) {
	 					accountstatementitem = new AccountStatementsItem();
						JSONObject accountObject = statementItem.getJSONObject(i);
						
						Date EffectiveDate = formatter.parse(accountObject.optString("EffectiveDate"));
						accountstatementitem.setEffectiveDate(EffectiveDate);	
						
					//	Date CreateDate = formatter.parse(accountObject.optString("CreateDate"));
					//	accountstatementitem.setCreateDate(CreateDate);
						
					//	if(accountObject.has("item_type")){
							
					//		accountstatementitem.setItemType(accountObject.optString("item_type"));
					//	}
					//	if(accountObject.has("posting_code")){
							
					//		accountstatementitem.setPostingCode(accountObject.optString("posting_code"));
					//	}
					//	if(accountObject.has("reversal")){
							
					//		accountstatementitem.setReversal(accountObject.optString("reversal"));
					//	}
						if(accountObject.has("Description")){
							
							accountstatementitem.setDescription(accountObject.optString("Description"));
						}
					//	if(accountObject.has("tran_code_desc")){
							
						//	accountstatementitem.setTranCodeDesc(accountObject.optString("tran_code_desc"));
					//	}
					//	if(accountObject.has("origin_id")){
							
					//		accountstatementitem.setTranOrigin(accountObject.optInt("origin_id"));
					//	}
					//	if(accountObject.has("origin_tracer_no")){
							
					//		accountstatementitem.setOriginTracerNo(accountObject.optString("origin_tracer_no"));
					//	}
						
					//	 accountstatementitem.setReg_E_Desc(accountObject.optString("device_location"));
	                     accountstatementitem.setTranAmount(accountObject.optDouble("Amount"));
	                 //    accountstatementitem.setCheckNumber(accountObject.optInt("check_no"));
	                 //    accountstatementitem.setPtid(accountObject.optLong("ptid"));
	                 //    accountstatementitem.setIso_currency(accountObject.optString("iso_currency"));
	                 //    accountstatementitem.setTranCode(accountObject.optInt("tran_code"));
	                     accountstatementitem.setDrCr(accountObject.optString("DrCr"));
	                     
						listValue.add(accountstatementitem);
						
					}
	 				accountstatementitems = new AccountStatementsItem[listValue.size()];
	 				accountstatementitems = (AccountStatementsItem[]) listValue.toArray(accountstatementitems);

	   			}
	   			
		    }catch (Exception e) {


			}
	   	     return accountstatementitems;
      }
		  
		  
		  public AccountStatementsItem[] getDailyActivityStatements(String accountNumber){
			  AccountStatementsItem[] accountstatementitems = new  AccountStatementsItem[0];
			  AccountStatementsItem accountstatementitem = null;
	          ArrayList<AccountStatementsItem> listValue = new ArrayList<AccountStatementsItem>();
	           
	           
	           
	           if (IS_DEV) {
	   			System.setProperty(CERT_1, CERT_2);
	   		}

	   		URL url = null;
	   		
	   		try {
	   			
	   			String API_URL = BaseUrl+GetDailyActivityStatements;

	   		   API_URL = API_URL +"accountNumber="+accountNumber;
	   		    	   				
	   									
	   			 System.out.println("API URL FOR RETRIEVAL " + API_URL);
	   			 
	   			url = new URL(API_URL);
	   			
	   			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

	   			con.setRequestMethod("GET");

	   			int responseCode = con.getResponseCode();

	   			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	   			String inputLine;

	   			StringBuilder inputBuffer = new StringBuilder();
	   			
	   			if (responseCode == HttpURLConnection.HTTP_OK) {

	   				while ((inputLine = in.readLine()) != null) {

	   					inputBuffer.append(inputLine);
	   				}
	           
	   				JSONObject json = new JSONObject(inputBuffer.toString());
	   				
	   				JSONObject jsons = (JSONObject) json.get("data");

	 				JSONArray statementItem = (JSONArray) jsons.get("Statements");
	        
	 				for (int i = 0; i < statementItem.length(); i++) {
	 					accountstatementitem = new AccountStatementsItem();
						JSONObject accountObject = statementItem.getJSONObject(i);
						
						Date EffectiveDate = formatter.parse(accountObject.optString("EffectiveDate"));
						accountstatementitem.setEffectiveDate(EffectiveDate);	
						accountstatementitem.setTranAmount(accountObject.optDouble("Amount"));
						accountstatementitem.setDescription(accountObject.optString("Description"));
					//	accountstatementitem.setCheckNumber(accountObject.optInt("ReferenceNo"));
					//	accountstatementitem.setbranch(accountObject.optString("PostingBranchNo"));
					//	accountstatementitem.setTranCode(accountObject.optInt(""));
						
						listValue.add(accountstatementitem);
						}
	 				accountstatementitems = new AccountStatementsItem[listValue.size()];
	 				accountstatementitems = (AccountStatementsItem[]) listValue.toArray(accountstatementitems);
	   			}
	   		}catch(Exception ex){
	   			
	   		}
	 				
			  return accountstatementitems;
		  }
		  
		  
		  public AccountStatementsItem[] getArchAccountStmtItem(String acct, String StartDate, String EndDate)
	       {
			  AccountStatementsItem[] accountstatementitems = new  AccountStatementsItem[0];
			  AccountStatementsItem accountstatementitem = null;
	          ArrayList<AccountStatementsItem> listValue = new ArrayList<AccountStatementsItem>();
	           	           
	           
	           if (IS_DEV) {
	        	   
	   			System.setProperty(CERT_1, CERT_2);
	   		}
	   		
	   		try {
	   			
	   			String API_URL = BaseUrl+GetAccountStatementsArch;
	   			System.out.print("API URL" + API_URL);
	   			JSONObject postObject = new JSONObject();
	   			postObject.put("AccountNumber", acct);
	   			postObject.put("BeginDate", StartDate);
	   			postObject.put("EndDate", EndDate);
	   			
	   			byte[] postDataBytes = postObject.toString().getBytes("UTF-8");
	   			
	   			HttpURLConnection con = getHttpConnectionWithHeader(API_URL);
	   			
	   			con.setRequestMethod("POST");
	   			con.setDoOutput(true);
	   			con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	   			
	   			System.out.println("Postbytes " + postObject.toString());
	   			con.getOutputStream().write(postDataBytes);
	   			int responseCode = con.getResponseCode();

	   			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	   			String inputLine;

	   			StringBuilder inputBuffer = new StringBuilder();
	   			
	   			if (responseCode == HttpURLConnection.HTTP_OK) {

	   				while ((inputLine = in.readLine()) != null) {

	   					inputBuffer.append(inputLine);
	   				}
	           
	   				JSONObject json = new JSONObject(inputBuffer.toString());
	   				
	   				JSONObject jsons = (JSONObject) json.get("data");

	 				JSONArray statementItem = (JSONArray) jsons.get("Statements");
	        
	 				for (int i = 0; i < statementItem.length(); i++) {
	 					accountstatementitem = new AccountStatementsItem();
						JSONObject accountObject = statementItem.getJSONObject(i);
						
						Date EffectiveDate = formatter.parse(accountObject.optString("EffectiveDate"));
						accountstatementitem.setEffectiveDate(EffectiveDate);	
						Date CreateDate = formatter.parse(accountObject.optString("CreateDate"));
//                        accountstatementitem.setCreateDate(CreateDate);
                        accountstatementitem.setDrCr(accountObject.optString("DrCr"));
//                        accountstatementitem.setTranAmount(accountObject.optDouble(""));
//                        accountstatementitem.setItemType(accountObject.optString(""));
//                        accountstatementitem.setPostingCode(accountObject.optString(""));
//                        accountstatementitem.setReversal(accountObject.optString(""));
//                        accountstatementitem.setHistoryDesc(accountObject.optString(""));
//                        accountstatementitem.setTranCodeDesc(accountObject.optString(""));
//                        accountstatementitem.setTranOrigin(accountObject.optInt(""));
//                        accountstatementitem.setOriginTracerNo(accountObject.optString(""));
//                        accountstatementitem.setReg_E_Desc(accountObject.optString(""));
//                        accountstatementitem.setCheckNumber(accountObject.optInt(""));
//                        accountstatementitem.setPtid(accountObject.optLong(""));
//                        accountstatementitem.setIso_currency(accountObject.optString(""));
                        listValue.add(accountstatementitem);
						
					
						}
	 				accountstatementitems = new AccountStatementsItem[listValue.size()];
	 				accountstatementitems = (AccountStatementsItem[]) listValue.toArray(accountstatementitems);
	   			}
	   		}catch(Exception ex){
	   			
	   		}
	 				
			  return accountstatementitems;
	       }
		  
		  
		  public AccountStatementsItem[] getConsolidatedAccountStatements(String [] accountNumber , String StartDate, String EndDate)
			{
			  AccountStatementsItem[] accountstatementitems = new  AccountStatementsItem[0];
			  AccountStatementsItem accountstatementitem = null;
				ArrayList<AccountStatementsItem> listValue = new ArrayList<AccountStatementsItem>();


				if (IS_DEV) {

					System.setProperty(CERT_1, CERT_2);
				}

				try {

					String API_URL = BaseUrl+GetConsolidatedAccountStatements;

					System.out.println("API URL: " + API_URL);
					JSONObject postObject = new JSONObject();
					postObject.put("AccountList", accountNumber);
					postObject.put("BeginDate", StartDate);
					postObject.put("EndDate", EndDate);

					byte[] postDataBytes = postObject.toString().getBytes("UTF-8");

					HttpURLConnection con = getHttpConnectionWithHeader(API_URL);

					con.setRequestMethod("POST");
					con.setDoOutput(true);
					con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

					System.out.println("Postbytes " + postObject.toString());
					con.getOutputStream().write(postDataBytes);
					int responseCode = con.getResponseCode();

					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;

					StringBuilder inputBuffer = new StringBuilder();

					if (responseCode == HttpURLConnection.HTTP_OK) {

						while ((inputLine = in.readLine()) != null) {

							inputBuffer.append(inputLine);
						}

						System.out.println("content :" + inputBuffer.toString());
						JSONObject json = new JSONObject(inputBuffer.toString());
						JSONObject jsons = (JSONObject) json.get("data");
						JSONArray JsonItems = (JSONArray) jsons.get("StatementRequest");

						for (int j = 0; j < JsonItems.length(); j++) {

							JSONObject accountObjecter = JsonItems.getJSONObject(j);
							JSONArray statementItem = (JSONArray) accountObjecter.get("Statements");
				
							for (int i = 0; i < statementItem.length(); i++) {
								
								accountstatementitem = new AccountStatementsItem();
								JSONObject accountObject = statementItem.getJSONObject(i);
								
								try {
		                            Date EffectiveDate = formatter2.parse(accountObject.optString("EffectiveDate"));
		                            accountstatementitem.setEffectiveDate(EffectiveDate);
		                        }catch (Exception ex){

		                        }
//								Date CreateDate = formatter.parse(accountObject.optString("CreateDate"));
//		                       accountstatementitem.setCreateDate(CreateDate);
								accountstatementitem.setDrCr(accountObject.optString("DrCr"));
//		                       accountstatementitem.setTranAmount(accountObject.optDouble(""));
//		                       accountstatementitem.setItemType(accountObject.optString(""));
//		                       accountstatementitem.setPostingCode(accountObject.optString(""));
//		                       accountstatementitem.setReversal(accountObject.optString(""));
//		                       accountstatementitem.setHistoryDesc(accountObject.optString(""));
//		                       accountstatementitem.setTranCodeDesc(accountObject.optString(""));
//		                       accountstatementitem.setTranOrigin(accountObject.optInt(""));
//		                       accountstatementitem.setOriginTracerNo(accountObject.optString(""));
//		                       accountstatementitem.setReg_E_Desc(accountObject.optString(""));
//		                       accountstatementitem.setCheckNumber(accountObject.optInt(""));
//		                       accountstatementitem.setPtid(accountObject.optLong(""));
//		                       accountstatementitem.setIso_currency(accountObject.optString(""));
								listValue.add(accountstatementitem);


							}
						}
					}
					
						accountstatementitems = new AccountStatementsItem[listValue.size()];
						accountstatementitems = (AccountStatementsItem[]) listValue.toArray(accountstatementitems);

				}catch(Exception ex){

				}

				return accountstatementitems;
			}
			 
}


