package banking;

import java.math.BigInteger;
import java.util.*;

enum State {
    MENU1, CREATE, LOGIN, MENU2, BALANCE, LOGOUT, EXIT
}

public class Bank {
    Scanner sc = new Scanner(System.in);
    Random r = new Random();
    State state;

    private final Map<String, String>  users;
    private final Map<String, Integer> balances;
    private String activeUser;

    public Bank() {
        state = State.MENU1;
        users = new TreeMap<>();
        balances = new TreeMap<>();
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
                } while (users.containsKey(card));
                String pin = "";
                for (int i = 0; i < 4; i++)
                    pin += r.nextInt(10);
                users.put(card, pin);
                balances.put(card, 0);
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
                if (users.get(card) != null && users.get(card).equals(pin)) {
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
                System.out.println("2. Log out");
                System.out.println("0. Exit");
                int ans = Integer.parseInt(sc.nextLine());
                switch (ans) {
                    case 1: {
                        state = State.BALANCE;
                        break;
                    }
                    case 2: {
                        state = State.LOGOUT;
                        break;
                    }
                    case 0: {
                        System.out.println("Bye!");
                        state = State.EXIT;
                        break;
                    }
                }
                break;
            }
            case BALANCE: {
                System.out.println(balances.get(activeUser));
                state = State.MENU2;
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
