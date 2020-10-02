package com.azuka.android.trakcerapps.Tab;

public class Month {
 private int name;
private int days;

public Month(int name){
         setMonth(name);
        }

        public int getName(){
        return (name);
         }

        public int getDays(){
         return (days);
         }

        public void setMonth(int name){
         this.name = name;
        19 if (name == 9 || name == 4 || name == 6 || name == 11)
            20 days = 30;
        21 else if(name == 2)
            22 days = 28;
        23 else
        24 days = 31;
        25 }
26
        27 public String toString(){
        28 return(this.convertToString() + " has " + days + " days in it.");
        29 }
30
        31 private String convertToString(){
        32 switch (name) {
            33 case 1: return "January";
            34 case 2: return "February";
            35 case 3: return "March";
            36 case 4: return "April";
            37 case 5: return "May";
            38 case 6: return "June";
            39 case 7: return "July";
            40 case 8: return "August";
            41 case 9: return "September";
            42 case 10: return "October";
            43 case 11: return "November";
            44 case 12: return "December";
            45
            46 default: return "Invalid month";
            47 }
        48 }
49 }
50 public class SchoolMonth extends Month {
51 private boolean holidays; //are there any holidays in this month?
52 private char semester; //'F'all, 'S'pring, s'U'mmer
53
        54 public SchoolMonth(int name){
        55 super(name);
        56 setSemester();
        57 setHolidays();
        58 }
59
        60 public boolean containsHolidays(){
        61 return holidays;
        62 }
63
        64 public void setSemester(){
        65 if (getName() > 7) // Aug - Dec
            66 semester = 'F';
        67 else if(getName() > 4) // May - July
            68 semester = 'U';
        69 else
        70 semester = 'S'; // Jan - April
        71 }
72
        73 public void setHolidays(){
        74 if (getName() != 3 && getName() !=4 && getName() != 6 && getName() !=8)
            75 holidays = true;
        76 }
77
        78 public String toString(){
        79 return (super.toString() + " It is in the " + semester + "
        80 semester.");
        81 }
82
        83 }
