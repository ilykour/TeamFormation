/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TeamFormationCode;

import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 *
 * @author lykourentzou
 */
public class TeamFormation {

    static final int Workers[] = {0, 1, 2, 3, 4, 5, 6, 7, 8}; //Each worker is given a unique id starting from 0. Not their AMT id.
    //static boolean deadWorkers[] = {false, false, false, false, false, false, false, false, false}; //if true then the worker has dropped out.
    static LinkedList<Pair> alreadyPair = new LinkedList<>(); //a list containing all pairs that have already been calculted in the previous rounds. Will be retrieved at each round by the DB.
    static LinkedList<Pair> currentList = new LinkedList<>(); //the pairs of this round. When the calculation of this list if finished this list is added to the list above and cleared for the next round.
    static final int desiredSize = Workers.length / 2; //the desired size of our pair sets is of course #workers/2 
    static int round = 1; //counts the round that we are in

    //NEW VARIABLES
    static final int teamSize = 3; //this variable needs to take its value from the database 
    static final int numberOfTeams = (int) Math.floor(Workers.length / teamSize);

    //each row of preferences is a worker and each column is that worker's preferences for another worker
    //carefull to set the values of this table as doubles!!
    static double[][] preferences = new double[][]{
        {0.0, 0.0, 0.0, 2.0, 0.0, 0.0, 3.0, 0.0, 1.0},
        {0.0, 0.0, 1.0, 0.0, 2.0, 0.0, 0.0, 3.0, 0.0},
        {0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 2.0, 1.0},
        {2.0, 1.0, 1.0, 1.0, 3.0, 0.0, 1.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 3.0, 0.0, 2.0, 1.0, 0.0, 0.0},
        {0.0, 3.0, 1.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0},
        {0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 2.0, 1.0},
        {0.0, 2.0, 0.0, 1.0, 0.0, 3.0, 0.0, 0.0, 0.0},
        {0.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0}
    };

    public static void main(String[] args) {
        createTeams(preferences, teamSize);
    }

    public static void createTeams(double[][] preferences, int teamSize) {
        List<Integer> superSet = new ArrayList<>();
        for (int i = 0; i < Workers.length; i++) {
            superSet.add(Workers[i]);
        }

        //allTeams contains every possible team combination
        List<Set<Integer>> allTeams = getSubsets(superSet, teamSize);

        //now we take allTeams and make a list of Team objects, each of which has a: set of workers and a value
        List<Team> teams = new ArrayList<Team>();
        ListIterator<Set<Integer>> iterTeams = allTeams.listIterator(); //Iterates over all teams
        while (iterTeams.hasNext()) { //for each candidate set of workers (team) we calculate its value and add both to the teams list
            Set<Integer> curTeam = iterTeams.next();
            Team tempTeam = new Team(curTeam, evaluateValue(curTeam));
            teams.add(tempTeam);
        }
        //then sort the teams list according to their values in descending order (the implementation of the descending order can be seen in the comparable method of the Team class
        Collections.sort(teams);

        /*
         // Iterate over the teams list (in comments - just for testing purposes)
         System.out.println("original teams list ordered in descending order by value");
         ListIterator<Team> listIterator = teams.listIterator();
         while (listIterator.hasNext()) {
         Team curTeam = listIterator.next();
         System.out.println("(" + curTeam.getTeamMembers().toString() + " , " + curTeam.getTeamValue() + ") " + "\t"); //here we print each candidate team with its value
         }
         */
        // Finally, we generate the disjoint set of teams, which are our final teams
        List<Team> finalTeams = cutGraph(teams);
        ListIterator<Team> listIterator2 = finalTeams.listIterator();
        while (listIterator2.hasNext()) {
            Team curTeam2 = listIterator2.next();
            //the curTeam2.getMembers() gives your the ids (of table Workers) of the workers of each team
            System.out.println("(" + curTeam2.getTeamMembers().toString() + " , " + curTeam2.getTeamValue() + ") " + "\t"); //here we print each candidate team with its value
        }

    }

    //the cutGraph method cuts the graph of workers into disjoint sets (the teams), preferring those teams that have more links one to the other
    private static List<Team> cutGraph(List<Team> teams) {
        List<Team> tempList = teams;
        List<Team> result = new ArrayList<>();

        do {
            //this is if we want to select teams randomly
            //Random rand = new Random();
            //int pickTeam = rand.nextInt((tempList.size() - 0) + 1) + 0;
            //result.add(tempList.get(pickTeam));

            Iterator<Team> iterator = tempList.iterator();
            while (iterator.hasNext()) {
                Team candidateTeam = iterator.next();
                if (teamEnters(candidateTeam, result)) { // if none of the candidateTeam members is already in the result list then add this team
                    result.add(candidateTeam);
                } else {
                    //else if even one of the candidate team members is already in one team of the result, then do nothing (i.e. do not add the team)
                }
                iterator.remove(); // Remove the current element (i.e. the team currently under inspection) from the iterator and the list.
            }
        } while (result.size() < numberOfTeams);
        return result;
    }

    //the CutGraphRand method cuts the graph randomly
     private static List<Team> cutGraphRand(List<Team> teams) {
        List<Team> tempList = teams;
        List<Team> result = new ArrayList<>();

        do {
            //this is if we want to select teams randomly
            //Random rand = new Random();
            //int pickTeam = rand.nextInt((tempList.size() - 0) + 1) + 0;
            //result.add(tempList.get(pickTeam));

            Iterator<Team> iterator = tempList.iterator();
            while (iterator.hasNext()) {
                Team candidateTeam = iterator.next();
                if (teamEnters(candidateTeam, result)) { // if none of the candidateTeam members is already in the result list then add this team
                    result.add(candidateTeam);
                } else {
                    //else if even one of the candidate team members is already in one team of the result, then do nothing (i.e. do not add the team)
                }
                iterator.remove(); // Remove the current element (i.e. the team currently under inspection) from the iterator and the list.
            }
        } while (result.size() < numberOfTeams);
        return result;
    }


    
    
    //the disjoint method checks if two teams have even one person in common, in which case it returns FALSE
    static boolean disjoint(Team firstTeam, Team secondTeam) {
        boolean result = true;
        Set<Integer> teamOneMembers = firstTeam.getTeamMembers();
        Set<Integer> teamTwoMembers = secondTeam.getTeamMembers();

        for (int item1 : teamOneMembers) {
            for (int item2 : teamTwoMembers) {
                if (item1 == item2) {
                    result = false;
                    return result;
                }
            }
        }

        return result;
    }

    //the teamEnters method checks if a team has even one common member with the existingTeams list, in which case it returns FALSE
    static boolean teamEnters(Team candidateTeam, List<Team> existingTeams) {
        boolean inResult = true;
        for (Team examinedTeam : existingTeams) {
            if (!disjoint(candidateTeam, examinedTeam)) {
                return false;
            }
        }
        return inResult;
    }

    //the evaluateValue evaluates the strength of a team's bonds, by summing up the team's average pairwise links
    private static double evaluateValue(Set<Integer> curTeam) {
        double value = 0.0;
        //turning set of current teams into Array in order to iterate over it
        int[] tempArray = new int[curTeam.size()];
        int index = 0;
        for (Integer i : curTeam) {
            tempArray[index++] = i; //note the autounboxing here
        }

        for (int i = 0; i < tempArray.length; i++) {
            int tempWorker1 = tempArray[i];
            for (int j = i + 1; j < tempArray.length; j++) {
                int tempWorker2 = tempArray[j];
                value = value + ((preferences[tempWorker1][tempWorker2] + preferences[tempWorker2][tempWorker1]) / 2);
            }
        }
        return value;
    }

    private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current, List<Set<Integer>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        //unseccessful stop clause
        if (idx == superSet.size()) {
            return;
        }
        Integer x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
        List<Set<Integer>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
        return res;
    }

    // @Shannon: FROM HERE DOWN IS THE CODE THAT YOU HAVE ALREADY TRANSLATED INTO JAVASCRIPT
    //code needed for making the pairs
    public static void createPairs() {
        do {
            System.out.println("\n Round= " + round);
            int cursorLeft = 0; //used for the backtracking of the algorithm
            int cursorRight = 0; //used for the backtracking of the algorithm

            createGraph(cursorLeft, cursorRight); //that function will try to create the bipartite graph.
            while (currentList.size() < desiredSize) { //if the bipartite graph is not good enough, i.e. it did not return pairs for all the people
                //then we must backtrack and try again. 
                Pair tempPair = currentList.pollLast(); //In backtracking we take the last pair out of the list (it caused the problem) and examine it. 
                if ((int) tempPair.getRight() < (Workers.length - 1)) { //If the right side of the pair still has some unexamined combinations (i.e. we have not reached the end of possible numbers for this)
                    // System.out.println("Backtracking 1..."); //Then we will backtrack one position. This means that:
                    cursorLeft = (int) tempPair.getLeft(); //We keep the left side of the pair as it was and
                    cursorRight = (int) tempPair.getRight() + 1; //We increase the right side by one. Example: If the pair that was causing the problem was (2,5) and we have 9 workers--> 5<9 thus we will start again this time from pair (2,6)
                } else { //Otherwise we must throw the above pair altogether and examine the previous one in the same way as above. We do not need to examine more than 2 elements though.
                    // System.out.println("Backtracking 2...");
                    tempPair = currentList.pollLast();
                    cursorLeft = (int) tempPair.getLeft();
                    cursorRight = (int) tempPair.getRight() + 1;
                }
                createGraph(cursorLeft, cursorRight); //Then we call the createGraph with the new pair as starting point. The previous elements of the list remain as they were.
            }

            //When we finish and our current list has put each person in a pair, we can print it or store it into our DB .
            ListIterator<Pair> listIterator1 = currentList.listIterator();
            while (listIterator1.hasNext()) {
                Pair curPair = listIterator1.next();
                System.out.print("(" + curPair.getLeft() + " , " + curPair.getRight() + ") " + "\t"); //here we print each pair of workers that will be asked to work together
            }

            alreadyPair.addAll(currentList); //at every round we add the pairings of this round to the alreadyPair list
            currentList.clear(); //then we clear the currentList. It will be refilled in the next round.
            round++; //the rounds
        } while (round < 5);

    }

    public static void createGraph(int leftNode, int rightNode) {
        for (int worker = leftNode; worker < Workers.length; worker++) {
            if (!(containsPart(currentList, worker))) {
                for (int candidate = rightNode; candidate < Workers.length; candidate++) {
                    if ((!containsPart(currentList, candidate)) && (worker != candidate)) {
                        //System.out.println("Examining pair: (" + worker + " , " + candidate + " )");
                        Pair candidatePair = new Pair(worker, candidate);
                        if ((!alreadyPair.contains(candidatePair)) && (!alreadyPair.contains(new Pair(candidate, worker))) && (!currentList.contains(candidatePair))) {
                            currentList.add(candidatePair);
                            //System.out.println("added in temp: " + candidatePair.getLeft() + " " + candidatePair.getRight());
                            rightNode = 0; // *Do not touch this* otherwise it will explode. 
                            break;
                        }
                    }
                }
            }

        }
    }

    public static boolean containsPart(LinkedList list, int element) {
        boolean contains = false;
        ListIterator<Pair> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Pair curPair = listIterator.next();
            int leftPart = (Integer) curPair.getLeft();
            int rightPart = (Integer) curPair.getRight();
            if ((leftPart == element) || (rightPart == element)) {
                contains = true;
            }
        }
        return contains;
    }

}
