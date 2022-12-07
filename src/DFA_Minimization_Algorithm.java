import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class DFA_Minimization_Algorithm {

    public static void main(String[] args) {
        ArrayList<String[][]> states = new ArrayList<>();

        String[][] initialState1 = new String[][] {
                {"X", "a", "b"},
                {"0S", "1", "0S"},
                {"1", "1", "2"},
                {"2", "3F", "0S"},
                {"3F", "3F", "4F"},
                {"4F", "3F", "5F"},
                {"5F", "3F", "5F"}
        };
        states.add(initialState1);

        String[][] initialState2 = new String[][] {
                {"X", "a", "b"},
                {"0S", "1F", "2F"},
                {"1F", "3", "3"},
                {"2F", "3", "3"},
                {"3", "3", "3"}
        };
        states.add(initialState2);

        String[][] initialState3 = new String[][] {
                {"X", "a", "b"},
                {"0S", "1F", "2F"},
                {"1F", "3", "4"},
                {"2F", "4", "3"},
                {"3", "5F", "5F"},
                {"4", "5F", "5F"},
                {"5F", "5F", "5F"}
        };
        states.add(initialState3);

        String[][] initialState4 = new String[][] {
                {"X", "a", "b"},
                {"0S", "1", "2"},
                {"1", "3F", "4F"},
                {"2", "4F", "3F"},
                {"3F", "5F", "5F"},
                {"4F", "5F", "5F"},
                {"5F", "5F", "5F"}
        };
        states.add(initialState4);

        for (String[][] state : states) {
            System.out.println("\nPrinting Table");
            printTable(state);

            minimize(state);

            System.out.println("\nPrinting Minimized Table");
            printTable(state);
        }
    }

    private static void printTable(String[][] table) {
        for (String[] line : table) {
            if (line != null) {
                for (int j = 0; j < Objects.requireNonNull(table[0]).length; j++) {
                    System.out.print(line[j] + ' ');
                }
                System.out.println();
            }
        }
    }

    private static HashMap<String,String[]> tablify(String[][] table) {
        HashMap<String, String[]> newTable = new HashMap<>();

        for (int i = 1; i < table.length; i++) {
            String[] p = new String[]{table[i][1], table[i][2]};
            newTable.put(table[i][0], p);
        }

        return newTable;
    }

    public static String[][] minimize(String[][] table) {
        HashMap<String, String[]> newTable = tablify(table);

        // create list of all pairs of states
        HashMap<HashSet<String>, Boolean>  pairs = new HashMap<> ();

        for (int i = 1; i < table.length - 1; i++) {
            for (int j = i + 1; j < table.length; j++) {
                HashSet<String> p = new HashSet<>();
                p.add(table[i][0]);
                p.add(table[j][0]);
                pairs.put(p, false);
            }
        }

        // mark F and !F pairs
        Object[] pList = pairs.keySet().toArray();
        for (int i = 0; i < pairs.size(); i++) {
            String s = pList[i].toString();
            char character = s.charAt(s.length() - 2);

            pairs.put((HashSet<String>) pList[i], (s.charAt(s.indexOf(',') - 1) == 'F' && character != 'F')
                    || (s.charAt(s.indexOf(',') - 1) != 'F' && character == 'F'));
        }

        // a iteration
        // mark pairs of p,q where D(p,a),D(q,a) is marked

        // b iteration
        // mark pairs of p,q where D(p,b),D(q,b) is marked

        iterate(pairs, newTable, 0);

        // keep going till nothing is changed
        // get results

        ArrayList<HashSet<String>> duplicates = new ArrayList<>();

        for (int i = 0; i < pairs.size(); i++) {
            if(!pairs.get(pList[i])){
                boolean added = false;
                for (int j = 0; i < duplicates.size(); j++) {
                    if (duplicates.get(j).contains(pList[i])) {
                        String[] s = pList[i].toString().replaceAll("\\s", "").substring(1, pList[i].toString().
                                replaceAll("\\s", "").length() - 1).split(",");

                        duplicates.get(j).add(s[0]);
                        duplicates.get(j).add(s[1]);
                        added = true;
                    }
                }
                if (!added) {
                    duplicates.add((HashSet<String>) pList[i]);
                }
            }
        }

        for (HashSet<String> duplicate : duplicates) {
            collapse(table, duplicate);
        }

        return table;
    }

    private static void collapse(String[][] table, HashSet<String> duplicate) {
        boolean collapsed = false;
        String defState = duplicate.toString().replaceAll("\\s", "").substring(1,
                duplicate.toString().replaceAll("\\s", "").length() - 1).split(",")[0];

        for (int i = 1; i < table.length; i++) {
            if (table[i] != null) {
                if (duplicate.contains(table[i][0]) && collapsed) {
                    table[i] = null;
                } else {
                    for (int j = 0; j < table[0].length; j++) {
                        if (duplicate.contains(table[i][j])) {
                            table[i][j] = defState;
                            if (j == 0) {
                                collapsed = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void iterate(HashMap<HashSet<String>, Boolean> pairs, HashMap<String, String[]> table,Integer ind) {
        Object[] pList = pairs.keySet().toArray();
        boolean done = true;

        for (int i = 0; i < pairs.size(); i++) {
            if (!pairs.get(pList[i])) {
                String[] s = pList[i].toString().replaceAll("\\s", "").substring(1, pList[i].toString().
                        replaceAll("\\s", "").length() - 1).split(",");

                HashSet<String> index = new HashSet<>();
                index.add(table.get(s[0])[ind]);
                index.add(table.get(s[1])[ind]);

                boolean original = pairs.get((HashSet<String>) pList[i]);
                if (index.size() > 1) {
                    pairs.put((HashSet<String>) pList[i], pairs.get(index));
                }

                done = original == pairs.get((HashSet<String>) pList[i]) && done;
            }
        }

        if (!done) {
            if (ind == 1) {
                iterate(pairs, table, 0);
            } else {
                iterate(pairs, table, 1);
            }
        }
    }
}
