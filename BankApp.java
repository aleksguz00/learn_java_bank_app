import java.util.*;

public class BankApp {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Bank.Account acc1 = bank.createAccount("John", "AAA123");
        Bank.Account acc2 = bank.createAccount("Bill", "BBB456");

        AccountOperation operation = new AccountOperation(acc1, 1000);

        CrossAccountOperation operation2 = new CrossAccountOperation(acc2, acc1, 250);

        bank.addOperation(operation);
        bank.addOperation(operation2);

        bank.runOperations();

        System.out.println(bank);
    }
}

class Bank {
    private static int accountNum = 1;

    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    public void runOperations() {
        for (int i = 0; i < operations.size(); ++i) {
            try {
                operations.get(i).doWork();
            }
            catch (OperationException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public Bank.Account createAccount(String clientName, String passport, double initialBalance) {
        Client cl = null;

        for (int i = 0; i < clients.size(); ++i) {
            if (clients.get(i).getPassport() == passport) {
                cl = clients.get(i);

                break;
            }
        }

        if (cl == null) {
            cl = new Client(clientName, passport);
            clients.add(cl);
        }

        Account acc = new Account(cl, initialBalance);
        accounts.add(acc);

        return acc;
    }

    public Bank.Account createAccount(String clientName, String passport) {
        return createAccount(clientName, passport, 0);
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder("Accounts state\n------------------------\n");

        for (int i = 0; i < accounts.size(); ++i) {
            builder.append(accounts.get(i));
            builder.append('\n');
        }

        return builder.toString();
    }

    public class Account {
        public Account(Client client, double startBalance) {
            this(client);
            balance = startBalance;
        }

        public Account(Client client) {
            this.client = client;
            balance = 0;
            number = "Acc " + ++accountNum;
        }

        public final double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public final String toString() {
            return "Account # " + accountNum + " Owner: " + client.toString() + " Balance: " +balance;
        }

        private double balance;
        private final String number;
        private Client client;
    }

    private Vector<Client> clients = new Vector<Client>();
    private Vector<Account> accounts = new Vector<Account>();
    private Vector<Operation> operations = new Vector<Operation>();
}

class Client {
    public Client(String name, String passport) {
        this.passport = passport;
        this.name = name;
    }

    public final String getPassport() {
        return passport;
    }

    public final String getName() {
        return name;
    }

    public final String toString() {
        return this.name + " " + this.passport;
    }

    private String passport;
    private String name;
    // private Bank.Account account;
}

abstract class Operation {
    protected double amount;

    public abstract void doWork() throws OperationException;
}

class AccountOperation extends Operation {
    public AccountOperation(Bank.Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }
    
    public void doWork() throws OperationException {
        double balance = account.getBalance();
        
        double result = balance + amount;

        if (result < 0)
            throw new OperationException("There is no much money on account " + account);

        account.setBalance(result);
    }

    private Bank.Account account;
}

class CrossAccountOperation extends Operation {
    public CrossAccountOperation(
        Bank.Account inAccount,
        Bank.Account outAccount,
        double amount
    ) {
        this.inAccount = inAccount;
        this.outAccount = outAccount;
        super.amount = amount;
    }

    public void doWork() throws OperationException {
        double outBalance = outAccount.getBalance();
        double inBalance = inAccount.getBalance();

        if (outBalance < amount)
            throw new OperationException("There is no much money on account " + outAccount);

        inAccount.setBalance(inBalance + amount);
        outAccount.setBalance(outBalance - amount);
    }

    private Bank.Account inAccount;
    private Bank.Account outAccount;
}

class OperationException extends Exception {
    OperationException(String message) {
        super(message);
    }
}
