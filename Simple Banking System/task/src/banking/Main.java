package banking;


import static banking.State.EXIT;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank(args[1]);
        while (bank.state != EXIT) {
            bank.process();
        }
    }
}
