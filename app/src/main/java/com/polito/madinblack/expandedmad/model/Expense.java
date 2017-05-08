package com.polito.madinblack.expandedmad.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Expense {

    /*
    public enum State{CONTEST, ACCEPTED}
    public enum Tag{FOOD, WATER_BILL, GAS_BILL, LIGHT_BILL, FLIGHT, HOTEL, FUEL, DRINK, OTHER}
    public enum Currency{YEN, EURO, DOLLAR, GBP}
    */

    private String  id;
    private String  name;
    private String  tag;
    private String  paidByName;
    private String  paidBySurname;
    private String  paidByFirebaseId;
    private String  paidByPhoneNumber;
    private Double  cost;
    private String  currencyName;
    private String  currencySymbol;
    //private String state
    private String  groupId;
    private Long    year;
    private Long    month;
    private Long    day;
    private String  description;


    private Map<String, PaymentFirebase> payments = new HashMap<>();

    //a map showing for each user the cost of the Payment
    //private Map<String, Payment> userCost = new HashMap<>();

    public Expense(){

    }

    public Expense(String id, String name, String tag, String paidByName, String paidBySurname, String paidByFirebaseId, String paidByPhoneNumber, Double cost, String currencyName, String currencySymbol, String groupId, Long year, Long month, Long day, String description) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.paidByName = paidByName;
        this.paidBySurname = paidBySurname;
        this.paidByFirebaseId = paidByFirebaseId;
        this.paidByPhoneNumber = paidByPhoneNumber;
        this.cost = cost;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.groupId = groupId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.description = description;
    }


    public Expense(String id, String name, String tag, String paidByName, String paidBySurname, String paidByFirebaseId, String paidByPhoneNumber, Double cost, String currencyName, String currencySymbol, String groupId, Long year, Long month, Long day, String description, Map<String, PaymentFirebase> payments) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.paidByName = paidByName;
        this.paidBySurname = paidBySurname;
        this.paidByFirebaseId = paidByFirebaseId;
        this.paidByPhoneNumber = paidByPhoneNumber;
        this.cost = cost;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
        this.groupId = groupId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.description = description;
        this.payments = payments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPaidByFirebaseId() {
        return paidByFirebaseId;
    }

    public void setPaidByFirebaseId(String paidByFirebaseId) {
        this.paidByFirebaseId = paidByFirebaseId;
    }

    public String getPaidByPhoneNumber() {
        return paidByPhoneNumber;
    }

    public void setPaidByPhoneNumber(String paidByPhoneNumber) {
        this.paidByPhoneNumber = paidByPhoneNumber;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }

    public String getPaidBySurname() {
        return paidBySurname;
    }

    public void setPaidBySurname(String paidBySurname) {
        this.paidBySurname = paidBySurname;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setPayments(Map<String, PaymentFirebase> payments) {
        this.payments = payments;
    }

    public PaymentFirebase givePaymentForUser(String userFirebaseId){
        for(PaymentFirebase pay:payments.values()){
            if(pay.getUserFirebaseId().equals(userFirebaseId))
                return pay;
        }
        return null;
    }


    public static String writeNewExpense(DatabaseReference mDatabaseRootRefenrence, String name, String tag, String paidByFirebaseId, String paidByPhoneNumber, String paidByName, String paidBySurname, Double cost, String currencyName, String currencySymbol, final String groupId, Long year, Long month, Long day, String description, List<Payment> paymentList){

        DatabaseReference myExpenseRef = mDatabaseRootRefenrence.child("expenses").push();
        String expenseKey = myExpenseRef.getKey();

        Expense expense = new Expense(expenseKey, name, tag, paidByName, paidBySurname, paidByFirebaseId, paidByPhoneNumber, CostUtil.round(cost, 2), currencyName, currencySymbol, groupId, year, month, day, description);
        myExpenseRef.setValue(expense);

        Map<String, PaymentFirebase> payments = new HashMap<>();
        DatabaseReference myPaymentRef;
        String paymentKey;

        for(final Payment payment : paymentList){

            myPaymentRef = myExpenseRef.child("payments").push();
            paymentKey = myPaymentRef.getKey();

            PaymentFirebase paymentFirebase = new PaymentFirebase(payment);
            paymentFirebase.setId(paymentKey);

            myPaymentRef.setValue(paymentFirebase);

            ExpenseForUser expenseForUser = new ExpenseForUser(expense, payment.getBalance());
            mDatabaseRootRefenrence.child("users/"+payment.getUserPhoneNumber()+"/"+payment.getUserFirebaseId()+"/groups/"+groupId+"/expenses/"+expenseKey).setValue(expenseForUser);

            if(!(payment.getUserFirebaseId().equals(paidByFirebaseId))) {

                mDatabaseRootRefenrence.child("users/"+payment.getUserPhoneNumber()+"/"+payment.getUserFirebaseId()+"/groups/"+groupId+"/newExpenses").runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() == null) {
                            //no default value for data, set one
                            currentData.setValue(1);
                        } else {
                            // perform the update operations on data
                            currentData.setValue((Long) currentData.getValue() + 1);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group
                    }
                });

                /*update users balances*/
                mDatabaseRootRefenrence.child("groups/"+groupId+"/users/"+payment.getUserFirebaseId()+"/balances/"+paidByFirebaseId+"/balance").runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() == null) {
                            //no default value for data, set one
                            currentData.setValue(payment.getBalance());
                        } else {
                            // perform the update operations on data
                            currentData.setValue((Long) currentData.getValue() + payment.getBalance());
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group
                    }
                });

                mDatabaseRootRefenrence.child("groups/"+groupId+"/users/"+paidByFirebaseId+"/balances/"+payment.getUserFirebaseId()+"/balance").runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() == null) {
                            //no default value for data, set one
                            currentData.setValue(payment.getDebit());
                        } else {
                            // perform the update operations on data
                            currentData.setValue((Long) currentData.getValue() + payment.getDebit());
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                        //Update remove the user from the group
                    }
                });



            }


        }

        mDatabaseRootRefenrence.child("groups/"+groupId+"/expenses/"+expenseKey).setValue(true);

        return expenseKey;
    }


    public void addUserCost(User u, Payment payment){
    //    userCost.put(u.getId(), payment);

    }




    public static int dateCompare(Expense e1, Expense e2){
        /*if(e1.getYear()>e2.getYear())
            return 1;
        else if(e1.getMonth())
    */
        return 1;
    }



    /*
    public Payment getPayment(Long id){ return userCost.get(id); }

    public Float getMyBalance(){
        if(!userCost.containsKey(MyApplication.myself.getId()))
            return -777f;
        return userCost.get(MyApplication.myself.getId()).getBalance();
    }



    public void addPayment(User user, Float paid, Float toPaid){
        Payment p = new Payment(user, this, paid, toPaid);
        userCost.put(user.getId(), p);

        if(paying.getId()==MyApplication.myself.getId() && user.getId()!=MyApplication.myself.getId()){
            group.uplMyCreditDebit(user, (toPaid-paid));
        }else if(paying.getId()!=MyApplication.myself.getId() && user.getId()==MyApplication.myself.getId()){
            group.uplMyCreditDebit(paying, (paid-toPaid));
        }
    }

    public void addPayment(Payment p){

        userCost.put(p.getUser().getId(), p);

        if(paying.getId()==MyApplication.myself.getId() && p.getUser().getId()!=MyApplication.myself.getId()){
            group.uplMyCreditDebit(p.getUser(), (p.getToPaid()-p.getPaid()));
        }else if(paying.getId()!=MyApplication.myself.getId() && p.getUser().getId()==MyApplication.myself.getId()){
            group.uplMyCreditDebit(paying, (p.getPaid()-p.getToPaid()));
        }
    }




    public String toString(){
        return userCost.get(MyApplication.myself.getId()).toString();
    }
    */

}
