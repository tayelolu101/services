/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zenithbank.banking.ibank.zencore.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author appdev1
 */
public class ZencoreService {

    private static final ClientAuth clientAuth = new ClientAuth();
    private static final int AUTHENTICATION_MODE = 1;
    private final ZencoreRestClient client = new ZencoreRestClient();

    static {
        Properties ps = new Properties();
        try {
            ps.load(ZencoreService.class.getResourceAsStream("zencore.properties"));
            clientAuth.setCallerId(ps.getProperty("callerId"));
            clientAuth.setClientName(ps.getProperty("clientName"));
            clientAuth.setPassword(ps.getProperty("password"));
            } catch (IOException ex){
            Logger.getLogger(ZencoreService.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
    
     public List<AccountResult> getAccounts(String accountNo, String accessCode) {

        String endPoint = "/GetAccountSummariesPOST";
        
        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(accountNo);
        customerAuth.setPassCode(accessCode);

        AccountListCommand command = new AccountListCommand();
        command.setAccountNumber(accountNo);
        command.setClientAuth(clientAuth);
        command.setCustomerAuth(customerAuth);
        
        AccountListResult res = client.send(endPoint, "POST", null, command, AccountListResult.class);
        return (res.getAccountSummary() != null && !res.getAccountSummary().isEmpty())
                ? res.getAccountSummary() : new ArrayList<AccountResult>();
    }

    public boolean saveBeneficiary(String toAccountNo, String toAccountName, String accountType,
            String bankCode, String accessCode, String accountNo) {

        String endPoint = "/CreateBeneficiaryPOST";

        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(accountNo);
        customerAuth.setPassCode(accessCode);

        AuthenticationDetail authDetail = new AuthenticationDetail();
        authDetail.setClientAuth(clientAuth);
        authDetail.setCustomerAuth(customerAuth);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAccountNumber(toAccountNo);
        beneficiary.setAccountType(accountType);
        beneficiary.setBankCode(bankCode);
        beneficiary.setBeneficiaryName(toAccountName);
        beneficiary.setBranchSortCode(bankCode + "000000");

        BeneficiaryRequest beneficiaryRequest = new BeneficiaryRequest();
        beneficiaryRequest.setAccountNumber(accountNo);
        beneficiaryRequest.setAuthenticationDetail(authDetail);
        beneficiaryRequest.setBeneficiary(beneficiary);

        BeneficiaryResponse result = client.send(endPoint, "POST", null, beneficiaryRequest, BeneficiaryResponse.class);
        return result.getResponseCode().equals("00");
    }
    
    public boolean editBeneficiary(String toAccountNo, String toAccountName, String accountType,
            String bankCode, String accessCode, String accountNo, double beneficiaryId) {

        String endPoint = "/EditBeneficiaryPOST";

        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(accountNo);
        customerAuth.setPassCode(accessCode);

        AuthenticationDetail authDetail = new AuthenticationDetail();
        authDetail.setClientAuth(clientAuth);
        authDetail.setCustomerAuth(customerAuth);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAccountNumber(toAccountNo);
        beneficiary.setAccountType(accountType);
        beneficiary.setBankCode(bankCode);
        beneficiary.setBeneficiaryName(toAccountName);
        beneficiary.setBranchSortCode(bankCode + "000000");
        beneficiary.setBeneficiaryId(beneficiaryId);

        BeneficiaryRequest beneficiaryRequest = new BeneficiaryRequest();
        beneficiaryRequest.setAccountNumber(accountNo);
        beneficiaryRequest.setAuthenticationDetail(authDetail);
        beneficiaryRequest.setBeneficiary(beneficiary);

        BeneficiaryResponse result = client.send(endPoint, "POST", null, beneficiaryRequest, BeneficiaryResponse.class);
        return result.getResponseCode().equals("00");
    }
    
    public boolean deleteBeneficiary(String toAccountNo, String toAccountName, String accountType,
            String bankCode, String accessCode, String accountNo, double beneficiaryId) {

        String endPoint = "/DeleteBeneficiaryPOST";

        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(accountNo);
        customerAuth.setPassCode(accessCode);

        AuthenticationDetail authDetail = new AuthenticationDetail();
        authDetail.setClientAuth(clientAuth);
        authDetail.setCustomerAuth(customerAuth);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAccountNumber(toAccountNo);
        beneficiary.setAccountType(accountType);
        beneficiary.setBankCode(bankCode);
        beneficiary.setBeneficiaryName(toAccountName);
        beneficiary.setBranchSortCode(bankCode + "000000");
        beneficiary.setBeneficiaryId(beneficiaryId);

        BeneficiaryRequest beneficiaryRequest = new BeneficiaryRequest();
        beneficiaryRequest.setAccountNumber(accountNo);
        beneficiaryRequest.setAuthenticationDetail(authDetail);
        beneficiaryRequest.setBeneficiary(beneficiary);

        BeneficiaryResponse result = client.send(endPoint, "POST", null, beneficiaryRequest, BeneficiaryResponse.class);
        return result.getResponseCode().equals("00");
    }
    
    public List<Beneficiary> getBeneficiaries(String accountNo) {

        String endPoint = "/GetBeneficiariesPOST";

        BeneficiaryListRequest command = new BeneficiaryListRequest();
        command.setClientAuth(clientAuth);
        command.setAccountNumber(accountNo);
        BeneficiaryListResponse res = client.send(endPoint, "POST", null, command, BeneficiaryListResponse.class);
        return (res.getResults().getBeneficiaries() != null && !res.getResults().getBeneficiaries().isEmpty())
                ? res.getResults().getBeneficiaries() : new ArrayList<Beneficiary>();
    }
    
    

    public boolean saveStandingOrder(String toAccountNo, double amount, String bankCode,
            String fromAccount, String reference, String frequency,
            String toAccountName, String startDate, String otp) {
        String endPoint = "/InitiateReOcurringTransferPOST";

        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(fromAccount);
        customerAuth.setPassCode(otp);

        ReocurringTransferRequest command = new ReocurringTransferRequest();
        command.setClientAuth(clientAuth);
        command.setCustomerAuth(customerAuth);
        command.setAmount(amount);
        command.setBankCode(bankCode);
        command.setCreditAccount(toAccountNo);
        command.setDebitAccount(fromAccount);
        command.setDescription(reference);
        command.setPayeeName(toAccountName);
        command.setPeriod(Integer.parseInt(frequency));
        command.setStartDate(startDate);
        ReocurringTransferResponse res = client.send(endPoint, "POST", null, command, ReocurringTransferResponse.class);
        return res.getResponseCode().equals("00");
    }
    
    public boolean editStandingOrder(String toAccountNo, double amount, String bankCode,
            String fromAccount, String reference, String frequency,
            String toAccountName, String startDate, String otp, double scheduleId){
        String endPoint = "/EditReOcurringTransferPOST";
        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(fromAccount);
        customerAuth.setPassCode(otp);
        
        ReocurringTransferEditRequest command = new ReocurringTransferEditRequest();
        command.setClientAuth(clientAuth);
        command.setCustomerAuth(customerAuth);
        command.setAmount(amount);
        command.setBankCode(bankCode);
        command.setCreditAccount(toAccountNo);
        command.setDebitAccount(fromAccount);
        command.setDescription(reference);
        command.setPayeeName(toAccountName);
        command.setPeriod(Integer.parseInt(frequency));
        command.setStartDate(startDate);
        command.setScheduleTransferID(scheduleId);
       
        ReocurringTransferResponse res = client.send(endPoint, "POST", null, command, ReocurringTransferResponse.class);
        return res.getResponseCode().equals("00");
    }
    
    public boolean stopStandingOrder(String accountNo, String otp, double scheduleId){
        String endPoint = "/StopReOccurringTransferPOST";
        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(accountNo);
        customerAuth.setPassCode(otp);
        
        ReocurringTransferEditRequest command = new ReocurringTransferEditRequest();
        command.setClientAuth(clientAuth);
        command.setCustomerAuth(customerAuth);
        command.setScheduleTransferID(scheduleId);
        
        ReocurringTransferResponse res = client.send(endPoint, "POST", null, command, ReocurringTransferResponse.class);
        return res.getResponseCode().equals("00");
    }
    
      public List<ReocurringTransferRequest> getStandingOrders(String accountNo, String otp) {
        String endPoint = "/GetReoccurringTransferListPOST";
        CustomerAuth customerAuth = new CustomerAuth();
        customerAuth.setAuthenticationMode(AUTHENTICATION_MODE);
        customerAuth.setAccountNumber(accountNo);
        customerAuth.setPassCode(otp);

        ReocurringTransferListRequest command = new ReocurringTransferListRequest();
        command.setClientAuth(clientAuth);
        command.setCustomerAuth(customerAuth);
        command.setAccountNumber(accountNo);

        ReocurringTransferListResponse res = client.send(endPoint, "POST", null, command, ReocurringTransferListResponse.class);
        return (res.getStandingTransferOrders() != null && !res.getStandingTransferOrders().isEmpty())
                ? res.getStandingTransferOrders() : new ArrayList<ReocurringTransferRequest>();
    }


    public static void main(String[] args) {
        String reference = "34536FF";
        String startDate = "10-02-2015";
        String frequency = "3";
        double amount = 100.00;
        String toAccount = "1020042015";
        String toAccountName = "Test Beneficiary";
        String fromAccount = "1020041529";
        String bankCode = "057";
        String otp = "8111138832";
        ZencoreService service = new ZencoreService();
        System.out.println(service.getAccounts(fromAccount, otp).size());

    }

}
