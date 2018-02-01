import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
* Automated lottery system for the 5CLIR program.
* @author Sabrina Hyunmi Ko
* @version 03/10/17
*/
public class LotteryAlgorithm {	
	private static Map<Integer, int[]> hmClassCount;
        private static Map<Integer, List<Integer>> hmClassList;
	private static Map<String, int[]> hmClassSize;
	private static Map<String, Integer> hmClassTime;
        
	/** figures out what number the class choice is, 1st, 2nd, 3rd, etc.
	 *  @param row the row of the current class choice being reviewed
	 *  @return choiceNum the # of which choice level this individual class choice is
	 */
	private static int choiceNumSort(Row row) {
		int choiceNum = 7;
		for (int i = 1; i < 7; i++) {
			Cell cell = row.getCell(6+i);
			
			if (cell.getBooleanCellValue() == true) {
				choiceNum = i;
			}
		}
		return choiceNum;
	}
	
	/** figures out if the person has a priority
	 * @param row the row of the current class choice being reviewed
	 * @return priority the level of priority of the person
	 */
	private static int prioritySort(Row row) {
		int priority = 8;
		Cell cell = row.getCell(14);
                String s = cell.getStringCellValue();
		if (s.equalsIgnoreCase("emeritus")) {
                    priority = 7;
		} else if (s.equalsIgnoreCase("none")) {
                    priority = 6;
		} else if (s.equalsIgnoreCase("priority")) {
                    priority = 5;
                } else if (s.equalsIgnoreCase("new member")) {
                    priority = 4;
		} else if (s.equalsIgnoreCase("lotteried out")) {
                    priority = 3;
		} else if (s.equals("moderator") || s.equalsIgnoreCase("modPriority")) {
                    priority = 2;
		} else if (s.equals("MODERATOR")) {
                    priority = 1;
                }
		return priority;
	}
        
        /**
         * sets the status of the class choice for just the moderators of the class, does not
         * affect their personal class total counter, but affects the class counter
         * currently not in use
         * @param myClassSize the total class size counter
         * @param className the person's class name
         */
        private static int setModStatus(int[] myClassSize, String className) {
            myClassSize[0] = myClassSize[0] + 1;
            hmClassSize.put(className, myClassSize);
            return 1;
        }
        
	/**
	 * sets the accepted/waitlisted/maxed out/etc status of each class choice
	 * @param myClassCount the personal class total counter
	 * @param myClassSize the class total counter
         * @param name the person's name
         * @param className the name of the class
	 * @return the status (check ClassChoice for more info on what the numbers mean)
	 */
	private static int setStatus(int[] myClassCount, int[] myClassSize, Integer idNumber, String className) {
		int status = 6;
                //accepted
		if (myClassCount[0] < myClassCount[1] && myClassSize[0] < myClassSize[1]) {
                    List<Integer> classesRegistered;
                    Boolean okayToRegister = true;
                    try {
                        classesRegistered = hmClassList.get(idNumber);
                        int currentClassTime = hmClassTime.get(className);
                        //check if there is any overlapping times of classes
                        for (int i = 0; i < classesRegistered.size(); i++) {
                            if (currentClassTime == classesRegistered.get(i)) {
                                okayToRegister = false;
                                status = 5;
                            }
                        }
                    } catch (Exception e) {
                        classesRegistered = new ArrayList<Integer>();
                    }
                    
                    if (okayToRegister == true) {
                        status = 1;

                        //ups the counters
                        myClassCount[0] = myClassCount[0] + 1;
                        hmClassCount.put(idNumber, myClassCount);
                        myClassSize[0] = myClassSize[0] + 1;
                        hmClassSize.put(className, myClassSize);
                        
                        //add the new class time
                        int timeOfClass = hmClassTime.get(className);
                        classesRegistered.add(timeOfClass);
                        hmClassList.put(idNumber, classesRegistered);
                    }
                //maxed out
		} else if (myClassCount[0] == myClassCount[1]) {
                    status = 2;
                //waitlisted
		} else if (myClassSize[0] == myClassSize[1]) {
                    status = 3;
                //exception
		}
		
		return status;
	}
	
	/** Executes the sorting algorithm
        * @param args
        * @throws java.io.IOException*/
	public static void main(String args[]) throws IOException {
            //imports the class choice excel spreadsheet
            FileInputStream fis = new FileInputStream(args[0]);
            XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
            XSSFSheet sheet = myWorkBook.getSheetAt(0);

            //imports the seminar info excel spreadsheet
            FileInputStream semFile = new FileInputStream(args[1]);
            XSSFWorkbook semWorkbook = new XSSFWorkbook(semFile);
            XSSFSheet semSheet = semWorkbook.getSheetAt(0);

            //makes output workbook, separating classes into different sheets + etc sheet,
            //& makes the unfilled unsorted class an array so you can check
            //& creates the hashmaps of the class capacities + a counter
            //& creates the hashmaps of the class time
            String[] classNames = new String[semSheet.getLastRowNum()];
            XSSFWorkbook finalWorkbook = new XSSFWorkbook();
            hmClassSize = new HashMap<String,int[]>();
            //the int representing time goes as follows:
            // mon am = 1, mon pm = 2, tues am = 3, tues pm = 4
            // wed am = 5, wed pm = 6, thu am = 7, thu pm = 8
            // technically not in but in case friday classes get implemented in the future
            // fri am = 9, fri pm = 10
            // FORMULA TO CALCULATE CLASS INT IS 2x (-1), x = day of the week, mon = 1 fri = 5
            //  the -1 is if it is an AM class, the -1 does not happen if it is a PM class
            hmClassTime = new HashMap<String, Integer>();
            for (Row row : semSheet) {
                Cell cell = row.getCell(0);
                if (row.getRowNum() != 0) {
                    //sheet creation for class separated results
                    Sheet classSheet = finalWorkbook.createSheet(cell.getStringCellValue());
                    //header of the sheet
                    classSheet.createRow(0);
                    classSheet.getRow(0).createCell(0).setCellValue("Rand #");
                    classSheet.getRow(0).createCell(1).setCellValue("ID #");
                    classSheet.getRow(0).createCell(2).setCellValue("Last Name");
                    classSheet.getRow(0).createCell(3).setCellValue("First Name");
                    classSheet.getRow(0).createCell(4).setCellValue("Choice #");
                    classSheet.getRow(0).createCell(5).setCellValue("Priority");
                    classSheet.getRow(0).createCell(6).setCellValue("Status");
                    
                    classNames[row.getRowNum()-1] = cell.getStringCellValue();

                    //hashmap hmClassSize creation
                    int[] values = new int[2];
                    values[0] = 0;
                    try {
                        values[1] = Integer.parseInt(row.getCell(9).getStringCellValue());
                    } catch (IllegalStateException e) {
                        values[1] = (int) row.getCell(9).getNumericCellValue();
                    }
                    hmClassSize.put(row.getCell(0).getStringCellValue(), values);
                    
                    //hashmap hmClassTime creation
                    String day = row.getCell(6).getStringCellValue();
                    String time = row.getCell(7).getStringCellValue();
                    int timeSlot = 0;
                    if (day.equalsIgnoreCase("MON")) {
                        timeSlot = 2;
                    } else if (day.equalsIgnoreCase("TUE")) {
                        timeSlot = 4;
                    } else if (day.equalsIgnoreCase("WED")) {
                        timeSlot = 6;
                    } else if (day.equalsIgnoreCase("THU")) {
                        timeSlot = 8;
                    } else {
                        timeSlot = 10;
                    }
                    
                    if (time.equalsIgnoreCase("AM")) {
                        timeSlot--;
                    }
                    
                    hmClassTime.put(row.getCell(0).getStringCellValue(), timeSlot);
                }
            }
            Sheet exceptionsSheet = finalWorkbook.createSheet("Exceptions");
            exceptionsSheet.createRow(0);
            exceptionsSheet.getRow(0).createCell(0).setCellValue("Rand #");
            exceptionsSheet.getRow(0).createCell(1).setCellValue("ID #");
            exceptionsSheet.getRow(0).createCell(2).setCellValue("Last Name");
            exceptionsSheet.getRow(0).createCell(3).setCellValue("First Name");
            exceptionsSheet.getRow(0).createCell(4).setCellValue("Choice #");
            exceptionsSheet.getRow(0).createCell(5).setCellValue("Priority");
            exceptionsSheet.getRow(0).createCell(6).setCellValue("Class Name");
            semWorkbook.close();

            //creates the hashmaps that count how many classes someone is in/wants in total
            hmClassCount = new HashMap<Integer, int[]>();
            hmClassList = new HashMap<Integer, List<Integer>>();
            List<ClassChoice> allData = new ArrayList<ClassChoice>();
            for (Row row : sheet) {
                if (row.getRowNum() != 0){
                    //class count hashmap creation
                    //key is the member id #
                    Integer key = (int) row.getCell(1).getNumericCellValue();
                    try {
                        int classTotal = Integer.parseInt(row.getCell(13).getStringCellValue());
                        hmClassCount.put(key, new int[] {0, classTotal});
                    } catch (NumberFormatException e){
                        hmClassCount.put(key, new int[] {0, 1});
                    }
                    
                    //list of class choice obj creation
                    ClassChoice entry;
                    Double randomNumber = (Double) row.getCell(0).getNumericCellValue();
                    int membID = (int) row.getCell(1).getNumericCellValue();
                    String lName = row.getCell(2).getStringCellValue();
                    String fName = row.getCell(3).getStringCellValue();
                    String cName = row.getCell(4).getStringCellValue().toUpperCase();
                    entry = new ClassChoice(randomNumber, membID, lName, fName, cName, prioritySort(row), choiceNumSort(row));
                    allData.add(entry);
                }
            }
            myWorkBook.close();

            //sets the header values of each class sheet
           /* for (Sheet classSheet : finalWorkbook) {
                classSheet.createRow(0);
                classSheet.getRow(0).createCell(0).setCellValue("Rand #");
                classSheet.getRow(0).createCell(1).setCellValue("ID #");
                classSheet.getRow(0).createCell(2).setCellValue("Last Name");
                classSheet.getRow(0).createCell(3).setCellValue("First Name");
                classSheet.getRow(0).createCell(4).setCellValue("Choice #");
                classSheet.getRow(0).createCell(5).setCellValue("Priority");
                if (classSheet.getSheetName().equals("Exceptions")) {
                    classSheet.getRow(0).createCell(6).setCellValue("Class Name");
                } else {
                    classSheet.getRow(0).createCell(6).setCellValue("Status");
                }
            }*/

            //make another output workbook that is just all the results together
            XSSFWorkbook finalFullWorkbook = new XSSFWorkbook();
            XSSFSheet finalWBSheet = finalFullWorkbook.createSheet();
            finalWBSheet.createRow(0);
            finalWBSheet.getRow(0).createCell(0).setCellValue("Rand #");
            finalWBSheet.getRow(0).createCell(1).setCellValue("ID #");
            finalWBSheet.getRow(0).createCell(2).setCellValue("Last Name");
            finalWBSheet.getRow(0).createCell(3).setCellValue("First Name");
            finalWBSheet.getRow(0).createCell(4).setCellValue("Choice #");
            finalWBSheet.getRow(0).createCell(5).setCellValue("Priority");
            finalWBSheet.getRow(0).createCell(6).setCellValue("Class Name");
            finalWBSheet.getRow(0).createCell(7).setCellValue("Status");

            //sort the class choices
            Collections.sort(allData, ClassChoice.COMPARE_BY_RANDNUM);
            Collections.sort(allData, ClassChoice.COMPARE_BY_PRIORITY);
            Collections.sort(allData, ClassChoice.COMPARE_BY_CHOICENUM);

            //determines the status of each class choice
            for (ClassChoice cc : allData) {
                if (Arrays.asList(classNames).contains(cc.getClassName())) {
                    //extracts the counter int arrays
                    int[] myClassCount = (int[]) hmClassCount.get(cc.getIDNum());
                    int[] myClassSize = (int[]) hmClassSize.get(cc.getClassName());

                    int status;
                    /* if setModStatus ever gets reimplemented, just uncomment
                    if (cc.getPriority() == 1) {
                        //status = setModStatus(myClassSize, cc.getClassName());
                        //myClassSize[0] = myClassSize[0] + 1;
                        //hmClassSize.put(cc.getClassName(), myClassSize);
                    } else {*/
                    if (cc.getPriority() == 7) {
                        status = 4;
                    } else {
                        status = setStatus(myClassCount, myClassSize, cc.getIDNum(), cc.getClassName());

                    }
                    //}

                    cc.makeStatusOfficial(status);
                } else {
                    //add to the etc list if it already cannot be determined the status
                    XSSFSheet mySheet = finalWorkbook.getSheet("Exceptions");
                    Row myRow = mySheet.createRow(mySheet.getLastRowNum()+1);
                    myRow.createCell(0).setCellValue(cc.getRandNum());
                    myRow.createCell(1).setCellValue(cc.getIDNum());
                    myRow.createCell(2).setCellValue(cc.getLastName());
                    myRow.createCell(3).setCellValue(cc.getFirstName());
                    myRow.createCell(4).setCellValue(cc.getChoiceNum());
                    myRow.createCell(5).setCellValue(cc.getPriority());
                    myRow.createCell(6).setCellValue(cc.getClassName());
                }
            }

            Collections.sort(allData, ClassChoice.COMPARE_BY_STATUS);

            //puts the class choices into the class sheets
            for (ClassChoice cc : allData) {
                try {  
                    //creates the entry into the spreadsheet that will be exported
                    XSSFSheet mySheet = finalWorkbook.getSheet(cc.getClassName());
                    Row myRow = mySheet.createRow(mySheet.getLastRowNum()+1);
                    myRow.createCell(0).setCellValue(cc.getRandNum());
                    myRow.createCell(1).setCellValue(cc.getIDNum());
                    myRow.createCell(2).setCellValue(cc.getLastName());
                    myRow.createCell(3).setCellValue(cc.getFirstName());
                    myRow.createCell(4).setCellValue(cc.getChoiceNum());
                    switch (cc.getPriority()) {
                        case 1:
                            myRow.createCell(5).setCellValue("MODERATOR");
                            break;
                        case 2:
                            myRow.createCell(5).setCellValue("modPriority");
                            break;
                        case 3:
                            myRow.createCell(5).setCellValue("Lotteried Out");
                            break;
                        case 4:
                            myRow.createCell(5).setCellValue("New Member");
                            break;
                        case 5:
                            myRow.createCell(5).setCellValue("Priority");
                            break;
                        case 6:
                            myRow.createCell(5).setCellValue("None");
                            break;
                        case 7:
                            myRow.createCell(5).setCellValue("Emeritus");
                            break;
                        default:
                            myRow.createCell(5).setCellValue("ERROR");
                            break;
                    }
                        
                    //if the person is accepted into the class
                    switch (cc.getStatus()) {
                    case 1:
                        myRow.createCell(6).setCellValue("Accepted");
                        break;
                    case 2:
                        //if the person is maxed out
                        myRow.createCell(6).setCellValue("Maxed Out");
                        break;
                    case 3:
                        //if the person is waitlisted
                        myRow.createCell(6).setCellValue("Waitlisted");
                        break;
                    case 4:
                        //if the person is emeritus accepted
                        myRow.createCell(6).setCellValue("E Accepted");
                       break;
                    case 5:
                        //if the person has a time conflict
                        myRow.createCell(6).setCellValue("Time Conflict");
                        break;
                    default:
                        myRow.createCell(6).setCellValue("ERROR");
                        break;
                    }  
                    
                    if (cc.getPriority() == 7) {
                        //add emeritus to the exceptions list
                        XSSFSheet currentSheet = finalWorkbook.getSheet("Exceptions");
                        Row currentRow = currentSheet.createRow(mySheet.getLastRowNum()+1);
                        currentRow.createCell(0).setCellValue(cc.getRandNum());
                        currentRow.createCell(1).setCellValue(cc.getIDNum());
                        currentRow.createCell(2).setCellValue(cc.getLastName());
                        currentRow.createCell(3).setCellValue(cc.getFirstName());
                        currentRow.createCell(4).setCellValue(cc.getChoiceNum());
                        currentRow.createCell(5).setCellValue("Emeritus");
                        currentRow.createCell(6).setCellValue(cc.getClassName());
                    }
                } catch (Exception e) {
                    //add to the etc list if it already cannot be determined the status
                    XSSFSheet mySheet = finalWorkbook.getSheet("Exceptions");
                    Row myRow = mySheet.createRow(mySheet.getLastRowNum()+1);
                    myRow.createCell(0).setCellValue(cc.getRandNum());
                    myRow.createCell(1).setCellValue(cc.getIDNum());
                    myRow.createCell(2).setCellValue(cc.getLastName());
                    myRow.createCell(3).setCellValue(cc.getFirstName());
                    myRow.createCell(4).setCellValue(cc.getChoiceNum());
                    switch (cc.getPriority()) {
                        case 1:
                            myRow.createCell(5).setCellValue("MODERATOR");
                            break;
                        case 2:
                            myRow.createCell(5).setCellValue("modPriority");
                            break;
                        case 3:
                            myRow.createCell(5).setCellValue("Lotteried Out");
                            break;
                        case 4:
                            myRow.createCell(5).setCellValue("New Member");
                            break;
                        case 5: 
                            myRow.createCell(5).setCellValue("Priority");
                            break;
                        case 6:
                            myRow.createCell(5).setCellValue("None");
                            break;
                        case 7:
                            myRow.createCell(5).setCellValue("Emeritus");
                            break;
                        default:
                            myRow.createCell(5).setCellValue("ERROR");
                            break;
                    }
                    myRow.createCell(6).setCellValue(cc.getClassName());
                }		
            }

            for (Sheet mySheet : finalWorkbook) {
                    for (int i = 0; i < 7; i++) {
                            mySheet.autoSizeColumn(i);
                    }
            }

            finalWorkbook.write(new FileOutputStream(System.getProperty("user.home") +  "/Desktop/ClassResults.xlsx"));
            finalWorkbook.close();

            for (ClassChoice cc : allData) {
                //creates the entry into the full spreadsheet
                Row theRow = finalWBSheet.createRow(finalWBSheet.getLastRowNum()+1);
                theRow.createCell(0).setCellValue(cc.getRandNum());
                theRow.createCell(1).setCellValue(cc.getIDNum());
                theRow.createCell(2).setCellValue(cc.getLastName());
                theRow.createCell(3).setCellValue(cc.getFirstName());
                theRow.createCell(4).setCellValue(cc.getChoiceNum());
                switch (cc.getPriority()) {
                    case 1:
                        theRow.createCell(5).setCellValue("MODERATOR");
                        break;
                    case 2:
                        theRow.createCell(5).setCellValue("modPriority");
                        break;
                    case 3:
                        theRow.createCell(5).setCellValue("Lotteried Out");
                        break;
                    case 4:
                        theRow.createCell(5).setCellValue("New Member");
                        break;
                    case 5:
                        theRow.createCell(5).setCellValue("Priority");
                        break;
                    case 6:
                        theRow.createCell(5).setCellValue("None");
                        break;
                    case 7:
                        theRow.createCell(5).setCellValue("Emeritus");
                        break;
                    default:
                        theRow.createCell(5).setCellValue("ERROR");
                        break;
                }

                theRow.createCell(6).setCellValue(cc.getClassName());

                //if the person is accepted into the class
                switch (cc.getStatus()) {
                    case 1:
                        theRow.createCell(7).setCellValue("Accepted");
                        break;
                    case 2:
                        //if the person is maxed out
                        theRow.createCell(7).setCellValue("Maxed Out");
                        break;
                    case 3:
                        //if the person is waitlisted
                        theRow.createCell(7).setCellValue("Waitlisted");
                        break;
                    case 4:
                        //if there is an emeritus
                        theRow.createCell(7).setCellValue("E Accepted");
                        break;
                    case 5:
                        //if there is a time conflict
                        theRow.createCell(7).setCellValue("Time Conflict");
                        break;
                    default:
                        theRow.createCell(7).setCellValue("ERROR");
                        break;
                }
            }

            for (int i = 0; i < 8; i++) {
                finalWBSheet.autoSizeColumn(i);
            }

            finalFullWorkbook.write(new FileOutputStream(System.getProperty("user.home") +  "/Desktop/FullClassResults.xlsx"));
            finalFullWorkbook.close();
    }
}
	
