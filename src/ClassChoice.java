import java.util.*;

/**
* Automated lottery system for the 5CLIR program.
* Separate class for storing and comparing class choice data.
* @author Sabrina Hyunmi Ko
* @version 03/10/17
*/
public class ClassChoice{
	private final Double randNum;
        private final Integer idNum;
	private final String lastName;
        private final String firstName;
	private final String className;
	//1 is moderators of that class, 2 is moderators in general, 3 is lotteried out,
        //4 is new members, 5 is priority (general made in case new types added),
        //6 is none, 7 is emeritus
	private int priority;
	private int choiceNum;
	//1 is accepted, 2 is maxed out,
	//3 is waitlisted, 4 is emeritus accepted,
        //5 is time conflict, 6 is exception
	private int status;

	/** constructor
     * @param randNum
     * @param idNum
     * @param lastName
     * @param firstName
     * @param className
     * @param priority
     * @param choiceNum */
	public ClassChoice(Double randNum, Integer idNum, String lastName, String firstName, String className, int priority, int choiceNum) {
		this.randNum = randNum;
                this.idNum = idNum;
		this.lastName = lastName;
                this.firstName = firstName;
		this.className = className;
		this.priority = priority;
		this.choiceNum = choiceNum;
	}
	
	/** sorts by choice #, from low to high */
	public static final Comparator<ClassChoice> COMPARE_BY_CHOICENUM = new Comparator<ClassChoice>() {
		public int compare(ClassChoice one, ClassChoice other) {
			return one.choiceNum - other.choiceNum;
		}
	};
	
	/** sorts by random #, from low to high */
	public static final Comparator<ClassChoice> COMPARE_BY_RANDNUM = new Comparator<ClassChoice>() {
		public int compare(ClassChoice one, ClassChoice other) {
			return (int) (one.randNum - other.randNum);
		}
	};
	
	/** sorts by priority #, from low to high */
	public static final Comparator<ClassChoice> COMPARE_BY_PRIORITY = new Comparator<ClassChoice>() {
		public int compare(ClassChoice one, ClassChoice other) {
			return one.priority - other.priority;
		}
	};
	
	/** sorts by class name, alphabetical */
	public static final Comparator<ClassChoice> COMPARE_BY_CLASSNAME = new Comparator<ClassChoice>() {
		public int compare(ClassChoice one, ClassChoice other) {
			return one.className.compareTo(other.className);
		}
	};
	
	/** sorts by status, from low to high */
	public static final Comparator<ClassChoice> COMPARE_BY_STATUS = new Comparator<ClassChoice>() {
		public int compare(ClassChoice one, ClassChoice other) {
			return one.status - other.status;
		}
	};

	/** manipulator for status
     * @param statNum */
	public void makeStatusOfficial(int statNum) {
		this.status = statNum;
	}
	
	/** accessor for random #
     * @return the random generated # */
	public double getRandNum() {
		return this.randNum;
	}
        
        /** accessor for id #
     * @return the id #*/
        public Integer getIDNum() {
            return this.idNum;
        }
	
	/** accessor for last name
     * @return last name */
	public String getLastName() {
		return this.lastName;
	}
        
        /** accessor for first name
         * @return first name
         */
        public String getFirstName() {
            return this.firstName;
        }
	
	/** accessor for class name
     * @return  the string of class name*/
	public String getClassName() {
		return this.className;
	}
	
	/** accessor for priority level
     * @return  priority level*/
	public int getPriority() {
		return this.priority;
	}
	
	/** accessor for choice #
     * @return  the choice #*/
	public int getChoiceNum() {
		return this.choiceNum;
	}
	
	/** accessor for status
     * @return the status*/
	public int getStatus() {
		return this.status;
	}
}
