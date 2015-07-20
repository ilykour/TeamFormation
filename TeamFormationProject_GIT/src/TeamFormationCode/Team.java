package TeamFormationCode;

import java.util.Set;

/**
 *
 * @author lykourentzou
 */
public class Team implements Comparable<Team> {

    private Set<Integer> team;
    private double value;

    public Team(Set<Integer> team, double value) {
        this.team = team;
        this.value = value;
    }

    public Set<Integer> getTeamMembers (){return team;}
    public double getTeamValue (){return value;}
    
    
    @Override //sorts the list in DESCENDING order
    public int compareTo(Team t) {
        if (this.value > t.value) {
            return -1;
        }
        if (this.value < t.value) {
            return 1;
        }
        return 0;
    }

}
