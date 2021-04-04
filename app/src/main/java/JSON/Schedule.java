package JSON;

import java.util.Date;

public class Schedule {

    private String groupe;
    private String discipline;
    private int number;
    private String datelearn;
    private String teacher;
    private String kabinet;
    private String undergroupe;

    public Schedule(String groupe, String discipline, int number, String datelearn, String teacher, String kabinet, String undergroupe) {
        this.groupe = groupe;
        this.discipline = discipline;
        this.number = number;
        this.datelearn = datelearn;
        this.teacher = teacher;
        this.kabinet = kabinet;
        this.undergroupe = undergroupe;
    }

    public Schedule() {
    }

    public String getGroupe() {
        return groupe;
    }

    public void setGroupe(String groupe) {
        this.groupe = groupe;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDatelearn() {
        return datelearn;
    }

    public void setDatelearn(String datelearn) {
        this.datelearn = datelearn;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getKabinet() {
        return kabinet;
    }

    public void setKabinet(String kabinet) {
        this.kabinet = kabinet;
    }

    public String getUndergroupe() {
        return undergroupe;
    }

    public void setUndergroupe(String undergroupe) {
        this.undergroupe = undergroupe;
    }
}
