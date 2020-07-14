package banking;

import javax.xml.crypto.Data;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;

enum State {
    MENU1, CREATE, LOGIN, MENU2, BALANCE, LOGOUT, INCOME, TRANSFER, CLOSE_ACCOUNT, EXIT
}

public class Bank {
    Scanner sc = new Scanner(System.in);
    Random r = new Random();
    State state;
    Database db;
    private String activeUser;

    public Bank(String url) {

        state = State.MENU1;
        db = Database.getInstance(url);
        activeUser = "";
    }

    public void process() {
        switch (state) {
            case MENU1: {
                System.out.println("1. Create an account");
                System.out.println("2. Log into account");
                System.out.println("0. Exit");
                int ans = Integer.parseInt(sc.nextLine());
                switch (ans) {
                    case 1: {
                        state = State.CREATE;
                        break;
                    }
                    case 2: {
                        state = State.LOGIN;
                        break;
                    }
                    case 0: {
                        System.out.println("Bye!");
                        db.close();
                        state = State.EXIT;
                        break;
                    }
                }
                break;
            }
            case CREATE: {
                String card;
                do {
                    card = "400000";
                    for (int i = 0; i < 9; i++)
                        card += r.nextInt(10);
                    int[] step = new int[card.length()];
                    for (int i = 0; i < step.length; i++)
                        step[i] = Integer.parseInt(card.substring(i, i + 1));
                    for (int i = 0; i < step.length; i += 2) {
                        step[i] *= 2;
                        if (step[i] > 9)
                            step[i] -= 9;
                    }
                    int added = Arrays.stream(step).reduce(0, Integer::sum);
                    card += 10 - (added % 10);
                } while (db.contains(card));
                String pin = "";
                for (int i = 0; i < 4; i++)
                    pin += r.nextInt(10);
                db.insert(card, pin, 0);
                System.out.println("Your card has been created");
                System.out.println("Your card number:");
                System.out.println(card);
                System.out.println("Your card PIN:");
                System.out.println(pin);
                state = State.MENU1;
                break;
            }
            case LOGIN: {
                System.out.println("Enter your card number:");
                String card = sc.nextLine();
                System.out.println("Enter your PIN:");
                String pin = sc.nextLine();
                if (db.contains(card) && db.getPin(card).equals(pin)) {
                    activeUser = card;
                    System.out.println("You have successfully logged in!");
                    state = State.MENU2;
                } else {
                    System.out.println("Wrong card number or PIN!");
                    state = State.MENU1;
                }
                break;
            }
            case MENU2: {
                System.out.println("1. Balance");
                System.out.println("2. Add income");
                System.out.println("3. Do transfer");
                System.out.println("4. Close account");
                System.out.println("5. Log out");
                System.out.println("0. Exit");
                int ans = Integer.parseInt(sc.nextLine());
                switch (ans) {
                    case 1: {
                        state = State.BALANCE;
                        break;
                    }
                    case 2: {
                        state = State.INCOME;
                        break;
                    }
                    case 3: {
                        state = State.TRANSFER;
                        break;
                    }
                    case 4: {
                        state = State.CLOSE_ACCOUNT;
                        break;
                    }
                    case 5: {
                        state = State.LOGOUT;
                        break;
                    }
                    case 0: {
                        System.out.println("Bye!");
                        db.close();
                        state = State.EXIT;
                        break;
                    }
                }
                break;
            }
            case BALANCE: {
                System.out.println(db.getBalance(activeUser));
                state = State.MENU2;
                break;
            }
            case INCOME: {
                System.out.println("Enter income:");
                db.addIncome(activeUser, Integer.parseInt(sc.nextLine()) + db.getBalance(activeUser));
                System.out.println("Income was added!");
                state = State.MENU2;
                break;
            }
            case TRANSFER: {
                System.out.println("Enter card number:");
                String dest = sc.nextLine();
                if (dest.equals(activeUser)) {
                    System.out.println("You can't transfer money to the same account!");
                } else {
                    int[] step = new int[dest.length()];
                    for (int i = 0; i < step.length; i++)
                        step[i] = Integer.parseInt(dest.substring(i, i + 1));
                    for (int i = 0; i < step.length; i += 2) {
                        step[i] *= 2;
                        if (step[i] > 9)
                            step[i] -= 9;
                    }
                    int added = Arrays.stream(step).reduce(0, Integer::sum);
                    if (added % 10 != 0) {
                        System.out.println("Probably you made mistake in the card number. Please try again!");
                    } else if (!db.contains(dest)) {
                        System.out.println("Such a card does not exist.");
                    } else {
                        System.out.println("Enter how much money you want to transfer:");
                        int amount = Integer.parseInt(sc.nextLine());
                        if (amount > db.getBalance(activeUser)) {
                            System.out.println("Not enough money!");
                        } else {
                            db.addIncome(activeUser, -amount);
                            db.addIncome(dest, amount);
                            System.out.println("Success!");
                        }
                    }
                }
                state = State.MENU2;
                break;
            }
            case CLOSE_ACCOUNT: {
                db.closeAccount(activeUser);
                System.out.println("The account has been closed!");
                state = State.MENU1;
                break;
            }
            case LOGOUT: {
                System.out.println("You have successfully logged out!");
                activeUser = "";
                state = State.MENU1;
                break;
            }
            case EXIT: {
                break;
            }
        }
    }
}
