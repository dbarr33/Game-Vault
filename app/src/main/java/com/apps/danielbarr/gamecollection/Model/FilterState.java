package com.apps.danielbarr.gamecollection.Model;

/**
 * @author Daniel Barr (Fuzz)
 */
public class FilterState {
    String selectedPublisher;
    String selectedDeveloper;
    SortType sortType;

    public FilterState() {
        sortType = SortType.DATE;
    }

    public String getSelectedPublisher() {
        return selectedPublisher;
    }

    public void setSelectedPublisher(String selectedPublisher) {
        this.selectedPublisher = selectedPublisher;
    }

    public String getSelectedDeveloper() {
        return selectedDeveloper;
    }

    public void setSelectedDeveloper(String selectedDeveloper) {
        this.selectedDeveloper = selectedDeveloper;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }
}
